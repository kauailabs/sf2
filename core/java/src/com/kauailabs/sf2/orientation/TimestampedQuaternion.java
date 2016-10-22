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

/**
 * The TimestampeQuaternion class extends the Quaternion class and 
 * adds additional variables and methods enabling a time-history
 * of quaternions to be constructed.
 * @author Scott
 */
public class TimestampedQuaternion extends Quaternion implements ITimestampedValue, IValueInterpolator<TimestampedQuaternion> {

    private long timestamp;
    private boolean valid;
    private boolean interpolated;
    
    /**
     * Default constructor for a TimestampedQuaternion; initializes all values to reasonable defaults.
     */
    public TimestampedQuaternion() {
    	super();
    	timestamp = 0;
    	valid = false;
    	this.interpolated = false;
    }
    
    /**
     * Constructor allowing a TimestampedQuaternion to be created from a Quaternion object and a 
     * timestamp.
     * @param src - source Quaternion
     * @param timestamp - timestamp representing the time at which the source Quaternion is valid.
     */
    public TimestampedQuaternion(Quaternion src, long timestamp) {
    	set(src,timestamp);
    }
    
    /**
     * Copy constructor; initializes all values to that of the source TimestampedQuaternion.
     * @param src - source TimestampedQuaternion
     */
    public TimestampedQuaternion(TimestampedQuaternion src) {
    	set(src);
    }
    
    /**
     * Returns the timestamp for this TimestampedQuaternion.
     */
    public long getTimestamp() {
    	return timestamp;
    }
    
    /**
     * Initalizes this TimestampedQuaternion to be equal to the source TimsetampedQuaternion
     * @param src - source TimestampedQuaternion
     */
    public void set(TimestampedQuaternion src) {
    	super.set(src);
    	this.interpolated = src.interpolated;
    	this.timestamp = src.timestamp;
    	this.valid = true;
    }
    
    /**
     * Initializes this TimestampedQuaternion to be equal to the source Quaternion object and a 
     * timestamp.
     * @param src - source Quaternion
     * @param timestamp - timestamp representing the time at which the source Quaternion is valid.
     */
    public void set(Quaternion src, long timestamp) {
    	super.set(src);
    	this.timestamp = timestamp;
    	this.valid = true;
    	this.interpolated = false;
    }
    

    /**
     * Initializes this TimestampedQuaternion to be equal to the source Quaternion component values 
     * and a timestamp.
     * @param w - W quaternion component value
     * @param x - X quaternion component value
     * @param y - Y quaternion component value
     * @param z - Z quaternion component value
     * @param timestamp - timestamp representing the time at which the source Quaternion is valid.
     */
    public void set(float w, float x, float y, float z, long timestamp) {
    	this.set(w,x,y,z);
    	this.timestamp = timestamp;
    	this.valid = true;
    	this.interpolated = false;
    }
    
    /**
     * If true, this TimestampedQuaternion was interpolated, otherwise it is an 
     * actual (measured) TimestampedQuaternion
     */
	@Override
    public boolean getInterpolated() {
    	return interpolated;
    }
    
	/**
	 * Modifies this TimestampedQuaternion's interpolated state; if true, this
	 * TimestampedQuaternion was interpolated; otherwise, it is an actual
	 * (measured) TimestampedQuaternion.
	 */
	@Override
    public void setInterpolated( boolean interpolated ) {
    	this.interpolated = interpolated;
    }

	/**
	 * Modifies this TimestampedQuaternion (representing the "from" quaternion) to
	 * represent a new Quaternion and Timestamp value which is located at a ratio 
	 * (in time) between itself and a "to" TimestampedQuaternion.
	 * <p>
	 * This interpolcation occurs via the Quaternion slerp() method.
	 */
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

	
    /**
     * Initalizes this TimestampedQuaternion to be equal to the source TimsetampedQuaternion
     * @param src - source TimestampedQuaternion
     */
	@Override
	public void copy(TimestampedQuaternion t) {
		this.set(t);
	}

	/**
	 * Returns whether this TimestampedQuaternion is valid or not.  This can be used
	 * when the TimestampedQuaternion is stored within a statically-allocated
	 * data structure, allowing reuse of the same object without requiring 
	 * the destruction/reconstruction of a new object.
	 */
	@Override
	public boolean getValid() {
		return this.valid;
	}

	/**
	 * Sets whether this TimestampedQuaternion is validornot.  This can be used
	 * when the TimestampedQuaternion is stored within a statically-allocated
	 * data structure, allowing reuse of the same object without requiring 
	 * the destruction/reconstruction of a new object.
	 */
	@Override
	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
