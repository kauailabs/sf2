package com.kauailabs.sf2.pose;

import com.kauailabs.sf2.orientation.ITimestampedValue;
import com.kauailabs.sf2.orientation.IValueInterpolator;
import com.kauailabs.sf2.orientation.TimestampedQuaternion;

/**
 * The Pose class represents a 2-dimensional position, and a 
 * 3-dimensional orientation at a given instant in time.
 * 
 * Note that the Pose class also provides a timestamp.
 * 
 * In addition to representing a pose, the Pose class can also 
 * represent an amount of change in pose, in which case the 
 * timestamp represents the duration of time comprising that 
 * position and orientation motion.
 * @author Scott
 */

public class TimestampedPose implements ITimestampedValue, IValueInterpolator<TimestampedPose> {	
	
	double x_offset_inches;
	double y_offset_inches;
	TimestampedQuaternion quat;
	
	public TimestampedPose() {
		set( new TimestampedQuaternion(), 0, 0 );
	}
	
	public void set(TimestampedQuaternion quat, double x_offset_inches, double y_offset_inches) {
		this.quat = quat;
		this.x_offset_inches = x_offset_inches;
		this.y_offset_inches = y_offset_inches;
	}	
	
	public void addOffsets( double x_offset_delta_inches, double y_offset_delta_inches ) {
		x_offset_inches += x_offset_delta_inches;
		y_offset_inches += y_offset_delta_inches;
	}
	
	public TimestampedPose(TimestampedQuaternion quat) {
		reset(quat);
	}
	
	public TimestampedPose(TimestampedQuaternion quat, double x_offset_inches, double y_offset_inches) {
		set(quat, x_offset_inches, y_offset_inches);
	}
	
	public void reset(TimestampedQuaternion quat) {
		set(quat,0,0);
	}	

	public TimestampedQuaternion getOrientation() {
		return this.quat;
	}
	
   /* Estimates an intermediate Pose given Poses representing each end of the path,
    * and an interpolation (time) ratio from 0.0 t0 1.0. */
	
	public static TimestampedPose interpolate( TimestampedPose from, TimestampedPose to, double t) {
		TimestampedPose interp_pose = null;
		/* First, interpolate the aggregate Quaternion */
		TimestampedQuaternion interp_quat = from.quat.interpolate(to.quat, t);
		if ( interp_quat != null ) {
			interp_pose = new TimestampedPose(interp_quat);

			/* Interpolate the X and Y offsets */
			double x_offset_delta = to.x_offset_inches - from.x_offset_inches;
			x_offset_delta *= t;
			interp_pose.x_offset_inches = from.x_offset_inches + x_offset_delta;

			double y_offset_delta = to.y_offset_inches - from.y_offset_inches;
			y_offset_delta *= t;
			interp_pose.y_offset_inches = from.y_offset_inches + y_offset_delta;
		}
		
		return interp_pose;
	}
	
	@Override
	public TimestampedPose interpolate(TimestampedPose to, double t) {
		TimestampedPose interp_pose = TimestampedPose.interpolate(this, to, t);
		return interp_pose;
	}
	
	@Override
	public boolean getInterpolated() {		
		return quat.getInterpolated();
	}
	
	@Override
	public void setInterpolated(boolean interpolated) {
		quat.setInterpolated(interpolated);		
	}
	
	@Override
	public void copy(TimestampedPose t) {
		this.quat.copy(t.quat);
		this.x_offset_inches = t.x_offset_inches;
		this.y_offset_inches = t.y_offset_inches;
	}
	
	@Override
	public long getTimestamp() {
		return this.quat.getTimestamp();
	}

	@Override
	public boolean getValid() {
		return this.quat.getValid();
	}

	@Override
	public void setValid(boolean valid) {
		this.quat.setValid(valid);
	}
	
	public double getOffsetInchesX() {
		return x_offset_inches;
	}

	public double getOffsetInchesY() {
		return y_offset_inches;
	}
	
}
