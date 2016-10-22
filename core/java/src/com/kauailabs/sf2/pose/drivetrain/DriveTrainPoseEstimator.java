package com.kauailabs.sf2.pose.drivetrain;

import com.kauailabs.sf2.orientation.ThreadsafeInterpolatingTimeHistory;
import com.kauailabs.sf2.orientation.TimestampedQuaternion;
import com.kauailabs.sf2.pose.TimestampedPose;
import com.kauailabs.sf2.sensors.IDriveMotorCurrentSensor;
import com.kauailabs.sf2.sensors.ITimestampedQuaternionSensor;
import com.kauailabs.sf2.sensors.ITimestampedQuaternionSensorDataSubscriber;
import com.kauailabs.sf2.sensors.IWheelAngleSensor;
import com.kauailabs.sf2.sensors.IWheelVelocitySensor;

public class DriveTrainPoseEstimator implements ITimestampedQuaternionSensorDataSubscriber {

	public final int MAX_POSE_HISTORY_LENGTH_SECONDS = 10; 
	
	ITimestampedQuaternionSensor quat_sensor;
	ThreadsafeInterpolatingTimeHistory<TimestampedPose> pose_history;
	IDriveTrainKinematics drive_model;
	IWheelVelocitySensor[] driveWheelDistanceSensors;
	IWheelAngleSensor[] steerWheelAngleSensors;
	IDriveMotorCurrentSensor[] driveMotorCurrentSensors;	
	volatile double[] curr_drive_wheel_distance_inches;
	volatile double[] curr_drive_wheel_distance_delta_inches;
	volatile double[] curr_drive_motor_current_amps;
	double[] last_drive_wheel_distance_inches;
	volatile double[] curr_steer_wheel_angle_inches;
	volatile TimestampedPose curr_pose;
	
	public DriveTrainPoseEstimator(IDriveTrainKinematics drive_model, 
									ITimestampedQuaternionSensor quat_sensor, 
									IWheelVelocitySensor[] driveWheelDistanceSensors,
									IWheelAngleSensor[] steerWheelAngleSensors,
									IDriveMotorCurrentSensor[] driveMotorCurrentSensors,
									int history_seconds) {
		this.quat_sensor = quat_sensor;
		this.driveWheelDistanceSensors = driveWheelDistanceSensors;
		this.steerWheelAngleSensors = steerWheelAngleSensors;
		
		if ( ( drive_model == null ) ||
			 ( quat_sensor == null ) ||
			 ( driveWheelDistanceSensors == null ) ||
			 ( history_seconds <= 0 ) ) {
			throw new IllegalArgumentException("Invalid input parameter.");
		}
		
		if ( drive_model.getDriveTrainParameters().getNumDriveWheels() !=
				driveWheelDistanceSensors.length ) {
			throw new IllegalArgumentException("Size of wheel encoders array does not match " +
					"the number of drive wheels specified by the DriveTrainKinematics class.");
		}
		
		for ( int i = 0; i < driveWheelDistanceSensors.length; i++ ) {
			if ( driveWheelDistanceSensors[i] == null ) {
				throw new IllegalArgumentException("One or more driveWheelEncoders is null.");
			}
		}
		
		curr_drive_wheel_distance_inches = new double[driveWheelDistanceSensors.length];
		curr_drive_wheel_distance_delta_inches = new double[driveWheelDistanceSensors.length];
		last_drive_wheel_distance_inches = new double[driveWheelDistanceSensors.length];
		if ( driveMotorCurrentSensors != null) {
			curr_drive_motor_current_amps = new double[driveMotorCurrentSensors.length];
		}
		if (steerWheelAngleSensors != null ) {		
			curr_steer_wheel_angle_inches = new double[steerWheelAngleSensors.length];
		}
		
		curr_pose = new TimestampedPose();
		
		int quat_update_rate = quat_sensor.getUpdateRateHz();
		if ( history_seconds > MAX_POSE_HISTORY_LENGTH_SECONDS ) {
			history_seconds = MAX_POSE_HISTORY_LENGTH_SECONDS;
		}
		this.pose_history = new ThreadsafeInterpolatingTimeHistory<TimestampedPose>(
									TimestampedPose.class, quat_update_rate * history_seconds);
		this.drive_model = drive_model;
		
		this.quat_sensor.registerCallback(this);				
	}
	
	public void reset() {
		synchronized(this) {
			pose_history.reset();
			// Clear Encoder Counts
			for ( int i = 0; i < driveWheelDistanceSensors.length; i++ ) {
				driveWheelDistanceSensors[i].reset();
				last_drive_wheel_distance_inches[i] = 0;			
			}
		}
	}
	
	public TimestampedPose getCurrentPose() {
		return pose_history.getMostRecent();
	}
	
	public TimestampedPose getPoseAtTimestamp(long timestamp) {
		return pose_history.get(timestamp);
	}

	@Override
	public void timestampedDataReceived(long system_timestamp, TimestampedQuaternion update) {
		TimestampedQuaternion tq = new TimestampedQuaternion(update);
		
		for ( int i = 0; i < driveWheelDistanceSensors.length; i++ ) {
			curr_drive_wheel_distance_inches[i] = driveWheelDistanceSensors[i].getCurrentDistanceInches();
			curr_drive_wheel_distance_delta_inches[i] = curr_drive_wheel_distance_inches[i] -
						last_drive_wheel_distance_inches[i];
		}

		if ( steerWheelAngleSensors != null) {
			for ( int i = 0; i < steerWheelAngleSensors.length; i++ ) {
				curr_steer_wheel_angle_inches[i] = steerWheelAngleSensors[i].getCurrentAngleDegees();
			}
		}
		
		if (driveMotorCurrentSensors != null) {
			for ( int i = 0; i < driveMotorCurrentSensors.length; i++ ) {
				curr_drive_motor_current_amps[i] = driveMotorCurrentSensors[i].getCurrentAmps();
			}
		}
		
		if ( pose_history.getValidSampleCount() > 0 ) {
			TimestampedPose tp = getCurrentPose();
			if ( tp != null ) {
				if ( drive_model.step(	system_timestamp,
										tp,  
										tq, 
										curr_drive_wheel_distance_delta_inches, 
										curr_steer_wheel_angle_inches,
										curr_drive_motor_current_amps,
										curr_pose) ) {
					pose_history.add(curr_pose);
				} else {
					System.out.println("Error invoking DriveTrainKinematics method.");
				}
			}
		} else {
			/* Pose history is empty.  Add a new pose (using latest quaternion). */
			/* The x/y offset values should default to their reset value.        */			
			TimestampedPose tp = new TimestampedPose(tq);
			pose_history.add(tp);
		}
		
		for ( int i = 0; i < driveWheelDistanceSensors.length; i++ ) {
			last_drive_wheel_distance_inches[i] = curr_drive_wheel_distance_inches[i];
		}
	}		
}
