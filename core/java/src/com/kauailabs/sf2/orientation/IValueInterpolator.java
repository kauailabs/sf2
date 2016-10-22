package com.kauailabs.sf2.orientation;

public interface IValueInterpolator<T> {

	public T interpolate(T to, double time_ratio);
	    
    public boolean getInterpolated();
    
    public void setInterpolated( boolean interpolated );

    public void copy(T t);
}
