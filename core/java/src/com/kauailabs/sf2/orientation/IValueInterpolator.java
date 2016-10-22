package com.kauailabs.sf2.orientation;

/**
 * The IValueInterpolator interface must be implemented by all objects which
 * have both a value and a timestamp component and support interpolation 
 * of intermediate values.
 * @author Scott
 *
 * @param <T> - the Java class representing the object.
 */
public interface IValueInterpolator<T> {

	/**
	 * Returns a new object of type T which whose value and timestamp are
	 * interpolated between this "from" object and the provided "to" object
	 * at a point between them expressed by a time ratio.
	 * @param to - the "to" object (also of type T) representing an object which is
	 * "later" in time than this object.
	 * @param time_ratio - the ratio (from 0 to 1) in time between this object
	 * and the "to" object at which point the interpolated value should occur.
	 * @return a new object of type T whose value and timestamp are interpolated
	 * by this method.
	 */
	public T interpolate(T to, double time_ratio);
	    
	/**
	 * Indicates whether this object represents an actual (measured) value/timestamp,
	 * or an interpolated value/timestamp.
	 * @return - true if this object is interpolated; false if it is actual (measured).
	 */
    public boolean getInterpolated();
    
    /**
     * Marks this object as being either actual (measured) value/timestamp, or interpolated. 
     * @param interpolated - true if the object is interpolated, false if it is actual (measured).
     */
    public void setInterpolated( boolean interpolated );

    /**
     * Initializes this object with the provided object.
     * @param t - the object to intializes this object with.
     */
    public void copy(T t);
}
