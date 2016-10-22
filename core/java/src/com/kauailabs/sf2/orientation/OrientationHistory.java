package com.kauailabs.sf2.orientation;

import com.kauailabs.sf2.sensors.ITimestampedQuaternionSensor;
import com.kauailabs.sf2.sensors.ITimestampedQuaternionSensorDataSubscriber;

/**
 * The OrientationHistory class implements a timestamped history of 
 * orientation data (e.g., from an IMU).  The OrientationHistory is populated
 * by data from a "timestamped quaternion" sensor, such as the 
 * navX-MXP.
 * 
 * The OrientationHistory buffers the orientation data received over the
 * most current time period between "now" and the size of the time history, 
 * and provides methods to retrieve orientation data in the form of
 * TimestampedQuaternion objects.  These objects can be looked up based
 * upon a timestamp; if an exact match is found the object is returned 
 * direction; otherwise if TimestampedQuaternion objects exist for the
 * times before and after the requested timestamp, a new TimestampedQuaterion
 * object is created via interpolation.
 * @author Scott
 */
public class OrientationHistory implements ITimestampedQuaternionSensorDataSubscriber{

	ITimestampedQuaternionSensor quat_sensor;
	ThreadsafeInterpolatingTimeHistory<TimestampedQuaternion> orientation_history;

	public final int MAX_ORIENTATION_HISTORY_LENGTH_SECONDS = 10; 	
	
	public final float INVALID_ANGLE = Float.NaN;
	
	public final float RADIANS_TO_DEGREES = (float) (180.0 / Math.PI);
	
	/**
	 * Constructs an OrientationHistory object with a specified size.  The OrientationHistory
	 * registers for incoming data using the provided ITimestampedQuaternionSensor object.
	 * @param quat_sensor - the sensor to acquire TimestampedQuaternion objects from.
	 * @param history_length_seconds - the length of the OrientationHistory, in seconds.
	 * The actual length of the OrientationHistory in number of objects is calculated 
	 * internally by accessing the sensor's current update rate.
	 * <i>Note:  if the sensor update rate is changed, after this constructor is invoked,
	 * the length of the history may no longer accurately reflect the originally-configured length.</i>
	 * @param quat_sensor - the sensor to use as the source of TimestampedQuaternions contained in the
	 * Orientation History
	 * @param history_length_seconds - the number of seconds the history will represent.  This value
	 * may not be larger than @value #MAX_ORIENTATION_HISTORY_IN_SECONDS seconds.
	 */
	public OrientationHistory(ITimestampedQuaternionSensor quat_sensor, int history_length_seconds) { 
		
		this.quat_sensor = quat_sensor;
		
		if ( history_length_seconds > MAX_ORIENTATION_HISTORY_LENGTH_SECONDS) {
			history_length_seconds = MAX_ORIENTATION_HISTORY_LENGTH_SECONDS;
		}
		
		int ahrs_update_rate = this.quat_sensor.getUpdateRateHz();		
		this.orientation_history = new ThreadsafeInterpolatingTimeHistory<TimestampedQuaternion>(
				TimestampedQuaternion.class, ahrs_update_rate * history_length_seconds);

		this.quat_sensor.registerCallback(this);		
	}
		
	@Override
	public void timestampedDataReceived(long system_timestamp, TimestampedQuaternion update) {
		orientation_history.add(update);		
	}
	
	/**
	 * Reset the OrientationHistory, clearing all existing entries.
	 * @param quat_curr
	 */
	public void reset(TimestampedQuaternion quat_curr) {
		orientation_history.reset();
	}
	
	/**
	 * Retrieves the most recently added Quaternion.
	 * @return
	 */
	public TimestampedQuaternion getCurrentQuaternion() {
		return orientation_history.getMostRecent();
	}
	
	/**
	 * Retrieves the TimestampedQuaterion at the specified sensor timestamp.  If
	 * an exact timestamp match occurs, a TimestampedQuaternion representing the
	 * actual (measured) data is returned; otherwise a new interpolated TimestampedQuaternion
	 * will be estimated, using the nearest preceding/following TimestampedQuaternion
	 * and the requested timestamp's ratio of time between them as its basis.  If
	 * no exact match could be found or interpolated value estimated, null is returned.
	 * @param requested_timestamp - sensor timestamp to retrieve 
	 * @return TimestampedQuaternion at requested timestamp, or null.
	 */
	public TimestampedQuaternion getQuaternionAtTime(long requested_timestamp) {
		return orientation_history.get(requested_timestamp);
	}
	
	/**
	 * Retrieves the yaw angle in degrees at the specified sensor timestamp.
	 * <p>Note that this value may be interpolated if a sample at the requested time
	 * is not available.
	 * @param requested_timestamp
	 * @return Yaw angle (in degrees, range -180 to 180) at the requested timestamp.
	 * If a yaw angle at the specified timestamp could not be found/interpolated, the
	 * value INVALID_ANGLE (NaN) will be returned.
	 */
    public float getYawDegreesAtTime( long requested_timestamp ) {
    	float yaw;
    	TimestampedQuaternion match = getQuaternionAtTime( requested_timestamp );
    	if ( match != null ) {
    		yaw = match.getYawRadians() * RADIANS_TO_DEGREES;
    	} else {
    		yaw = INVALID_ANGLE;
    	}
    	return yaw;
    }
    
	/**
	 * Retrieves the pitch angle in degrees at the specified sensor timestamp.
	 * <p>Note that this value may be interpolated if a sample at the requested time
	 * is not available.
	 * @param requested_timestamp
	 * @return Pitch angle (in degrees, range -180 to 180) at the requested timestamp.
	 * If a pitch angle at the specified timestamp could not be found/interpolated, the
	 * value INVALID_ANGLE (NaN) will be returned.
	 */
    public float getPitchDegreesAtTime( long requested_timestamp ) {
    	float pitch;
    	TimestampedQuaternion match = getQuaternionAtTime( requested_timestamp );
    	if ( match != null ) {
    		pitch = match.getPitchRadians() * RADIANS_TO_DEGREES;
    	} else {
    		pitch = INVALID_ANGLE;
    	}
    	return pitch;
    }
    
	/**
	 * Retrieves the roll angle in degrees at the specified sensor timestamp.
	 * <p>Note that this value may be interpolated if a sample at the requested time
	 * is not available.
	 * @param requested_timestamp
	 * @return Roll angle (in degrees, range -180 to 180) at the requested timestamp.
	 * If a roll angle at the specified timestamp could not be found/interpolated, the
	 * value INVALID_ANGLE (NaN) will be returned.
	 */
    public float getRollDegreesAtTime( long requested_timestamp ) {
    	float roll;
    	TimestampedQuaternion match = getQuaternionAtTime( requested_timestamp );
    	if ( match != null ) {
    		roll = match.getRollRadians() * RADIANS_TO_DEGREES;
    	} else {
    		roll = INVALID_ANGLE;
    	}
    	return roll;
    }  	
}
