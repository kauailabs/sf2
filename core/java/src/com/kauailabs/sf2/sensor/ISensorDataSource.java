package com.kauailabs.sf2.sensor;

import java.util.ArrayList;

import com.kauailabs.sf2.quantity.IQuantity;
import com.kauailabs.sf2.time.Timestamp;

/**
 * The ITimestampedQuaternionSensor interface should be implemented by 
 * any sensor which generates timestamped Quaternions (e.g., the 
 * navX-MXP).
 * @author Scott
 *
 */
public interface ISensorDataSource {
	/**
	 * Subscribes the provided subscriber object for callbacks whenever new
	 * TimestampedQuaternion data is received by the sensor.
	 * @param subscriber - object implementing the ISensorDataSubscriber<T> interface
	 * which will be called back whenever new data arrives.
	 * @return true if registration was successful; false otherwise.
	 */
	boolean subscribe(ISensorDataSubscriber subscriber);
	/**
	 * Unsubscribes the previously-registered subscriber object for callbacks whenever new
	 * Sensor Data is received by the sensor.  The subscriber should have
	 * been previously registered for callbacks via the subscribe() method.
	 * @param subscriber - object implementing the ISensorDataSubscriber<T>
	 * interface, which up success will no longer be called back whenever new data arrives.
	 * @return true if unsubscription was successful; false otherwise.
	 */
	boolean unsubscribe(ISensorDataSubscriber subscriber);

	/**
	 * Retrieves the most recently-received Sensor Data value.
	 * @param tq
	 * @return true if valid Sensor was received.  false if data is invalid
	 * (e.g., the sensor is no longer connected).
	 */
	boolean getCurrent(IQuantity[] quantities, Timestamp curr_ts);
	
	/**
	 * For those quantities which can be reset to their default 
	 * (e.g., an IMU Yaw, or an encoder counter), this method 
	 * will cause the reset to occur.
	 * 
	 * If the quantity is reset successful, true is returned.  If the
	 * quantity could not be reset, or if the quantity does not support
	 * resetting, false is returned.
	 * @param quantity_index
	 * @return
	 */
	boolean reset(int quantity_index);
	
	void getSensorDataSourceInfos(ArrayList<SensorDataSourceInfo> out);

}
