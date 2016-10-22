/*----------------------------------------------------------------------------*/
/* Copyright (c) Kauai Labs 2016. All Rights Reserved.                        */
/*                                                                            */
/* Created in support of Team 2465 (Kauaibots).  Go Purple Wave!              */
/*                                                                            */
/* Open Source Software - may be modified and shared by FRC teams. Any        */
/* modifications to this code must be accompanied by the \License.txt file    */ 
/* in the root directory of the project.                                      */
/*----------------------------------------------------------------------------*/
package com.kauailabs.sf2.orientation;

public class TimestampedQuaternion extends Quaternion implements ITimestampedValue, IValueInterpolator<TimestampedQuaternion> {

    private long timestamp;
    private boolean valid;
    private boolean interpolated;
    
    public TimestampedQuaternion() {
    	super();
    	timestamp = 0;
    	valid = false;
    	this.interpolated = false;
    }
    
    public TimestampedQuaternion(Quaternion src, long timestamp) {
    	set(src,timestamp);
    }
    
    public TimestampedQuaternion(TimestampedQuaternion src) {
    	set(src);
    }
    
    public long getTimestamp() {
    	return timestamp;
    }
    
    public void set(TimestampedQuaternion src) {
    	super.set(src);
    	this.interpolated = src.interpolated;
    	this.timestamp = src.timestamp;
    	this.valid = true;
    }
    
    public void set(Quaternion src, long timestamp) {
    	super.set(src);
    	this.timestamp = timestamp;
    	this.valid = true;
    	this.interpolated = false;
    }
    
    public void set(float w, float x, float y, float z, long timestamp) {
    	this.set(w,x,y,z);
    	this.timestamp = timestamp;
    	this.valid = true;
    	this.interpolated = false;
    }
    
	@Override
    public boolean getInterpolated() {
    	return interpolated;
    }
    
	@Override
    public void setInterpolated( boolean interpolated ) {
    	this.interpolated = interpolated;
    }

	@Override
	public TimestampedQuaternion interpolate(TimestampedQuaternion to, double time_ratio) {
		TimestampedQuaternion from = this;
		TimestampedQuaternion interpolated = null;
		Quaternion q = Quaternion.slerp(from, to, time_ratio);
		if ( q != null ) {
			float delta_t = to.getTimestamp() - from.getTimestamp();
			delta_t *= time_ratio;
			interpolated = new TimestampedQuaternion( q, from.getTimestamp() + (long)delta_t);
		}
		return interpolated;
	}

	@Override
	public void copy(TimestampedQuaternion t) {
		this.set(t);
	}

	@Override
	public boolean getValid() {
		return this.valid;
	}

	@Override
	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
