/*----------------------------------------------------------------------------*/
/* Copyright (c) Kauai Labs 2016. All Rights Reserved.                        */
/*                                                                            */
/* Created in support of Team 2465 (Kauaibots).  Go Purple Wave!              */
/*                                                                            */
/* Open Source Software - may be modified and shared by FRC teams. Any        */
/* modifications to this code must be accompanied by the \License.txt file    */ 
/* in the root directory of the project.                                      */
/*----------------------------------------------------------------------------*/
package com.kauailabs.sf2.time;

import com.kauailabs.sf2.interpolation.IInterpolate;
import com.kauailabs.sf2.interpolation.IValueInterpolator;
import com.kauailabs.sf2.quantity.IQuantity;
import com.kauailabs.sf2.quantity.IQuantityContainer;
import com.kauailabs.sf2.quantity.Scalar;
import com.kauailabs.sf2.time.ITimestampedValue;

/**
 * The TimestampedValue generic class encapsulates a value class via generic, and 
 * adds additional variables and methods enabling a time-history
 * of values to be constructed.
 * @author Scott
 */
public class TimestampedValue<T extends ICopy<T> & IInterpolate<T> & IQuantityContainer> 
	implements ITimestampedValue, ICopy<TimestampedValue<T>>, IValueInterpolator<TimestampedValue<T>>, IQuantityContainer {

    private T value;
    private long timestamp;
    private boolean valid;
    private boolean interpolated;
    
    public TimestampedValue(){
    	timestamp = 0;
    	valid = false;
    	this.interpolated = false;    	
    }
    
    /**
     * Default constructor for a TimestampedValue<T>; initializes all values to reasonable defaults.
     */
    public TimestampedValue(T value) {
    	this.value = value.instantiate_copy();
    	timestamp = 0;
    	valid = false;
    	this.interpolated = false;
    }
    
    /**
     * Constructor allowing a TimestampedValue<T> to be created from a T object and a 
     * timestamp.
     * @param src - source T object
     * @param timestamp - timestamp representing the time at which the source value is valid.
     */
    public TimestampedValue(T src, long timestamp) {
    	this.value = src.instantiate_copy();
    	setTimestamp(timestamp);
    }
    
    /**
     * Copy constructor; initializes all values to that of the source TimestampedValue<T>.
     * @param src - source TimestampedValue<T>
     */
    public TimestampedValue(TimestampedValue<T> src) {
    	this.value = src.getValue().instantiate_copy();
    }
    
    /**
     * Returns the timestamp for this TimestampedValue.
     */
    public long getTimestamp() {
    	return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
    	this.timestamp = timestamp;
    }
    
    /**
     * Initalizes this TimestampedValue to be equal to the source TimestampedValue
     * @param src - source TimestampedValue
     */
    public void set(TimestampedValue<T> src) {
    	this.value.copy(src.value);
    	this.interpolated = src.interpolated;
    	this.timestamp = src.timestamp;
    	this.valid = true;
    }
    
    /**
     * Initializes this TimestampedValue to be equal to the source value object and a 
     * timestamp.
     * @param src - source Quaternion
     * @param timestamp - timestamp representing the time at which the source value object is valid.
     */
    public void set(T src, long timestamp) {
    	this.value.copy(src);
    	this.timestamp = timestamp;
    	this.valid = true;
    	this.interpolated = false;
    }
    
    public T getValue() { return value; }
    
    /**
     * If true, this TimestampedValue<T> was interpolated, otherwise it is an 
     * actual (measured) TimestampedValue<T>
     */
	@Override
    public boolean getInterpolated() {
    	return interpolated;
    }
    
	/**
	 * Modifies this TimestampedValue<T>'s interpolated state; if true, this
	 * TimestampedValue<T> was interpolated; otherwise, it is an actual
	 * (measured) TimestampedValue<T>.
	 */
	@Override
    public void setInterpolated( boolean interpolated ) {
    	this.interpolated = interpolated;
    }

	/**
	 * Modifies this TimestampedValue (representing the "from" value) to
	 * represent a new value and Timestamp value which is located at a ratio 
	 * (in time) between itself and a "to" TimestampedValue.
	 * <p>
	 * This actual method of interpolation used depends upon the value type (T).
	 */
	@Override
	public void interpolate(TimestampedValue<T> to, double time_ratio, TimestampedValue<T> out) {
		TimestampedValue<T> from = this;
		this.value.interpolate(to.value,time_ratio, out.getValue());
		float delta_t = to.getTimestamp() - from.getTimestamp();
		delta_t *= time_ratio;
		out.setTimestamp((long)delta_t);
	}

	
    /**
     * Initalizes this TimestampedValue to be equal to the source TimestampedValue
     * @param src - source TimestampedValue
     */
	@Override
	public void copy(TimestampedValue<T> t) {
		this.set(t);
	}

	/**
	 * Returns whether this TimestampedValue is valid or not.  This can be used
	 * when the TimestampedValue is stored within a statically-allocated
	 * data structure, allowing reuse of the same object without requiring 
	 * the destruction/reconstruction of a new object.
	 */
	@Override
	public boolean getValid() {
		return this.valid;
	}

	/**
	 * Sets whether this TimestampedValue is valid or not.  This can be used
	 * when the TimestampedValue is stored within a statically-allocated
	 * data structure, allowing reuse of the same object without requiring 
	 * the destruction/reconstruction of a new object.
	 */
	@Override
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	@Override
	public TimestampedValue<T> instantiate_copy() {
		TimestampedValue<T> new_tsv = new TimestampedValue<T>();
		new_tsv.value = this.value.instantiate_copy();
		return new_tsv;
	}

	@Override
	public void getQuantities(IQuantity[] quantities) {
		IQuantity[] value_quantities = new IQuantity[1];
		this.value.getQuantities(value_quantities);
		IQuantity[] timestamped_value_quantities = new IQuantity[value_quantities.length+1];
		timestamped_value_quantities[0] = new Scalar(timestamp);
		for ( int i = 0; i < value_quantities.length; i++) {
			timestamped_value_quantities[i+1] = value_quantities[i];
		}
		quantities = value_quantities;
	}
}
