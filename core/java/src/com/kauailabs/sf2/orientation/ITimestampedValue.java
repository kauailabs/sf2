package com.kauailabs.sf2.orientation;

/**
 * The ITimestampedValue interface must be implemented by all objects which
 * have a timestamp component and support storage within timestamped value
 * collections (e.g., a ThreadsafeInterpolatingTimeHistory).
 * @author Scott
 *
 * @param <T> - the Java class representing the object.
 */
public interface ITimestampedValue {

	/**
	 * Accessor for this object's sensor timestamp.
	 * @return - sensor timestamp
	 */
    public long getTimestamp();
	
    /**
     * Indicates whether this object currently represents a valid value/timestamp.
     * @return - true if this object is currently valid, false if not.
     */
    public boolean getValid();
    
    /**
     * Modifies whether this object currently represents a valid value/timestamp. 
     * @param valid - true if this object is currently valid, false if not.
     */
    public void setValid(boolean valid);
}
