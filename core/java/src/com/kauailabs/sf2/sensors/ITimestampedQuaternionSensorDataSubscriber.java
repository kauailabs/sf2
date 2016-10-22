package com.kauailabs.sf2.sensors;

import com.kauailabs.sf2.orientation.TimestampedQuaternion;

/**
 * Interface to be implemented by any subscriber of TimestampedQuaternion data.
 * @author Scott
 * @param system_timestamp - the system timestamp when the data was received.
 * @param update - the TimestampedQuaternion representing the newly-reeived data.
 */
public interface ITimestampedQuaternionSensorDataSubscriber {
	void timestampedDataReceived(long system_timestamp, TimestampedQuaternion update);
}
