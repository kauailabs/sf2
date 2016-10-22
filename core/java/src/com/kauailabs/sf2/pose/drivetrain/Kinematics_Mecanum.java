package com.kauailabs.sf2.pose.drivetrain;

import com.kauailabs.sf2.orientation.Quaternion;
import com.kauailabs.sf2.orientation.TimestampedQuaternion;
import com.kauailabs.sf2.pose.TimestampedPose;

public class Kinematics_Mecanum implements IDriveTrainKinematics {

	TimestampedQuaternion last_quat;
	DriveTrainParameters drive_params;
	double fwdMatrix [][];
	
	final int X = 0;
	final int Y = 1;
	final int ROT = 2;
	
	final int NUM_WHEELS = 4;
	
	TimestampedQuaternion q_diff_temp;
	double enc_based_pose_change[];
	
	public Kinematics_Mecanum(DriveTrainParameters drive_params) {	
		
		this.drive_params = drive_params;
		
		if ( this.drive_params.getNumDriveWheels() != NUM_WHEELS ) {
			throw new IllegalArgumentException("MecanumKinematics requires"
					+ "exactly 4 wheels be used.");
		}					
		
		this.last_quat = new TimestampedQuaternion();
		
		double wheelRadius = this.drive_params.getDriveWheelDiameterInches() / 2;
		// Calculate Rotational Coefficient
		double cRotK = ((this.drive_params.getWidthInches() + 
						 this.drive_params.getLengthInches())/2) / wheelRadius;  
		
		/* Note that this matrix assumes all rollers are at 45 degrees. */
		fwdMatrix = new double[][] {
			{	+1/4,			+1/4,			+1/4,			+1/4			},
			{	-1/4,			+1/4,			-1/4,			+1/4			},
			{	-1/(4*cRotK),	-1/(4*cRotK),	+1/(4*cRotK),	+1/(4*cRotK) 	}
		};	
		
		/* Allocate memory for working variables. */
		this.enc_based_pose_change = new double[3]; 
		this.q_diff_temp = new TimestampedQuaternion();
	}
	
	@Override
	public DriveTrainParameters getDriveTrainParameters() {
		return this.drive_params;
	}
	
	@Override
	/* Note:  Input drive/steer wheel are in encoder ticks.                                           */
	/* Note:  Input drive/steer wheel values are ordered from left front corner, increasing clockwise */
	/* Return:  Returns a TimestampedPose object representing the change in pose since pose_last.     */
	/* Note:  the individual drive/steer wheel velocities are assumed to be at the same timestamp     */
	/* as the current TimestampedQuaternion. */
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
		
		mecanumForwardKinematics(drive_wheel_distance_delta_inches, enc_based_pose_change);

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

	/* Note 1/4 term implies equal weight distribution across all four wheels */
	/* Note that all roller bearings are assumed to have equivalent friction characeristics */
	/* Note output body velocities are in same units as wheel_velocities_in, */
	/*    which is to say enc_ticks/delta_t.                                 */
	/* Note:  Input values are ordered from left front corner, clockwise */
	/* Note:  Output values are ordered LinearX (Strafe), LinearY (Forward), RotZ (Rotate) */
	void mecanumForwardKinematics( double wheel_velocities_in[], double body_velocity_out[] ) {
		for ( int i = 0; i < 3; i++ ) {
			body_velocity_out[i] = 0;
			for ( int wheel = 0; wheel < 4; wheel++ ) {
				body_velocity_out[i] += wheel_velocities_in[wheel] * fwdMatrix [i][wheel];
			}
		}
	}	
}
