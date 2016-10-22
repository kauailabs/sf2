package com.kauailabs.sf2.sensors;

public interface ITimestampedQuaternionSensor {
	boolean registerCallback(ITimestampedQuaternionSensorDataSubscriber subscriber);
	int getUpdateRateHz();
}
