package com.kauailabs.sf2.sensors;

/**
 * The ITimestampedQuaternionSensor interface should be implemented by 
 * any sensor which generates timestamped Quaternions (e.g., the 
 * navX-MXP).
 * @author Scott
 *
 */
public interface ITimestampedQuaternionSensor {
	/**
	 * Registers the provided subscriber object for callbacks whenever new
	 * TimestampedQuaternion data is received by the sensor.
	 * @param subscriber - object implementing the ITimestampedQuaternionSensorDataSubscriber
	 * which will be called back whenever new data arrives.
	 * @return true if registration was successful; false otherwise.
	 */
	boolean registerCallback(ITimestampedQuaternionSensorDataSubscriber subscriber);
	/**
	 * Unregisters the previously-registered subscriber object for callbacks whenever new
	 * TimestampedQuaternion data is received by the sensor.  The subscriber should have
	 * been previously registered for callbacks via the registerCallback() method.
	 * @param subscriber - object implementing the ITimestampedQuaternionSensorDataSubscriber
	 * interface, which will no longer be called back whenever new data arrives.
	 * @return true if unregistration was successful; false otherwise.
	 */
	boolean deregisterCallback(ITimestampedQuaternionSensorDataSubscriber subscriber);
	/**
	 * Returns the currently-configured sensor update rate.
	 * @return currently-configured sensor update rate in Hertz (Hz).
	 */
	int getUpdateRateHz();
}
