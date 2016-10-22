package com.kauailabs.sf2.pose.drivetrain;

import com.kauailabs.sf2.orientation.Quaternion;
import com.kauailabs.sf2.orientation.TimestampedQuaternion;
import com.kauailabs.sf2.pose.TimestampedPose;

public class Kinematics_HSlide implements IDriveTrainKinematics {

	TimestampedQuaternion last_quat;
	DriveTrainParameters drive_params;
	
	final int X = 0;
	final int Y = 1;
	final int ROT = 2;
	
	static int LF_WHEEL = 0;
	static int RF_WHEEL = 1;
	static int RR_WHEEL = 2;
	static int LR_WHEEL = 3;
	static int MIDDLE_WHEEL = 4;
	
	static int NUM_WHEELS = 5;
	
	TimestampedQuaternion q_diff_temp;
	double enc_based_pose_change[];
	
	/* HSlideKinematics requires 5 wheels w/encoders:
	 * 0 - Left Front wheel
	 * 1 - Right Front wheel
	 * 2 - Right Rear wheel
	 * 3 - Left Rear wheel
	 * 4 - Middle (Slide) wheel
	 */
	public Kinematics_HSlide(DriveTrainParameters drive_params) {	
		
		this.last_quat = new TimestampedQuaternion();
		this.drive_params = drive_params;		
		/* Allocate memory for working variables. */
		this.enc_based_pose_change = new double[3]; 
		this.q_diff_temp = new TimestampedQuaternion();
		
		if ( this.drive_params.getNumDriveWheels() != NUM_WHEELS ) {
			throw new IllegalArgumentException("HSlideKinematics requires"
					+ "exactly 5 wheels be used.");
		}			
	}
	
	@Override
	public DriveTrainParameters getDriveTrainParameters() {
		return this.drive_params;
	}
	
	@Override
	/* Note:  Input drive wheel distances are in units of inches since the last time step()           */
	/* was invoked.                                                                                   */                  
	/* Note:  Input drive/steer wheel values are ordered from left front corner, increasing clockwise */
	/* Return:  Returns a TimestampedPose object representing the change in pose since pose_last.     */
	/* Note:  the individual drive/steer wheel values are assumed to be measured coincident with the  */
	/* current TimestampedQuaternion. */
	public boolean step(
			 long system_timestamp,
			 TimestampedPose pose_last,
			 TimestampedQuaternion quat_curr, 
		  	 double drive_wheel_distance_delta_inches[], 
		  	 double steer_wheel_angle_degrees_curr[],
		  	 double drive_motor_current_amps_curr[],
		  	 TimestampedPose pose_curr_out) {

		Quaternion.difference(quat_curr, pose_last.getOrientation(), q_diff_temp);		
		long delta_t = quat_curr.getTimestamp() - pose_last.getTimestamp();
		q_diff_temp.set(q_diff_temp, delta_t);
		
		enc_based_pose_change[X] = drive_wheel_distance_delta_inches[MIDDLE_WHEEL];
		enc_based_pose_change[Y] =
			(drive_wheel_distance_delta_inches[LF_WHEEL] + 
			 drive_wheel_distance_delta_inches[RF_WHEEL] + 
			 drive_wheel_distance_delta_inches[RR_WHEEL] + 
			 drive_wheel_distance_delta_inches[LR_WHEEL]) / 4; 
			 
		
		double theta = q_diff_temp.getYawRadians();
		
		/* Adjust X/Y offset deltas, currently in body frame, to be in
		 * World frame - by rotating these values by the current body rotation */
		
		enc_based_pose_change[X] *= (float)Math.sin(theta); 
		enc_based_pose_change[Y] *= (float)Math.cos(theta); 
		
		/* Use Encoder-derived values for Translational Motion */
		pose_curr_out.addOffsets( enc_based_pose_change[X], enc_based_pose_change[Y] );		                                              
		
		/* Use IMU-derived values for orientation. */
		pose_curr_out.getOrientation().copy(quat_curr);
		
		return true;
	}
}
