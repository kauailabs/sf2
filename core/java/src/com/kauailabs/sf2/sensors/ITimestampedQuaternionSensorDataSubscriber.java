package com.kauailabs.sf2.sensors;

import com.kauailabs.sf2.orientation.TimestampedQuaternion;

public interface ITimestampedQuaternionSensorDataSubscriber {
	void timestampedDataReceived(long system_timestamp, TimestampedQuaternion update);
}
