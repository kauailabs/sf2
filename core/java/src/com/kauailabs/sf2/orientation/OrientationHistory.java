package com.kauailabs.sf2.orientation;

import com.kauailabs.sf2.sensors.ITimestampedQuaternionSensor;
import com.kauailabs.sf2.sensors.ITimestampedQuaternionSensorDataSubscriber;

public class OrientationHistory implements ITimestampedQuaternionSensorDataSubscriber{

	ITimestampedQuaternionSensor quat_sensor;
	ThreadsafeInterpolatingTimeHistory<TimestampedQuaternion> orientation_history;

	public final int MAX_ORIENTATION_HISTORY_LENGTH_SECONDS = 10; 	
	
	public final float RADIANS_TO_DEGREES = (float) (180.0 / Math.PI);
	
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
	
	public void reset(TimestampedQuaternion quat_curr) {
		orientation_history.reset();
	}
	
	public TimestampedQuaternion getCurrentQuaternion() {
		return orientation_history.getMostRecent();
	}
	
	public TimestampedQuaternion getQuaternionAtTime(long requested_timestamp) {
		return orientation_history.get(requested_timestamp);
	}
	
    public float getYawDegreesAtTime( long requested_timestamp ) {
    	float yaw = 0.0f;
    	TimestampedQuaternion match = getQuaternionAtTime( requested_timestamp );
    	if ( match != null ) {
    		yaw = match.getYawRadians() * RADIANS_TO_DEGREES;
    	}
    	return yaw;
    }
    
    public float getPitchDegreesAtTime( long requested_timestamp ) {
    	float pitch = 0.0f;
    	TimestampedQuaternion match = getQuaternionAtTime( requested_timestamp );
    	if ( match != null ) {
    		pitch = match.getPitchRadians() * RADIANS_TO_DEGREES;
    	}
    	return pitch;
    }
    
    public float getRollDegreesAtTime( long requested_timestamp ) {
    	float roll = 0.0f;
    	TimestampedQuaternion match = getQuaternionAtTime( requested_timestamp );
    	if ( match != null ) {
    		roll = match.getRollRadians() * RADIANS_TO_DEGREES;
    	}
    	return roll;
    }  	
}
