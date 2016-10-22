package com.kauailabs.sf2.orientation;

public interface ITimestampedValue {

    public long getTimestamp();
	
    public boolean getValid();
    
    public void setValid(boolean valid);
}
