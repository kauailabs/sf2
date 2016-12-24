package com.kauailabs.sf2.sensor;

import com.kauailabs.sf2.quantity.IQuantity;
import com.kauailabs.sf2.time.Timestamp;

/**
 * Interface to be implemented by any subscriber of Sensor Data of type T.
 * @author Scott
 * @param system_timestamp - the system timestamp when the data was received.
 * @param update - the object of type T representing the newly-received data.
 */
public interface ISensorDataSubscriber {
	void publish(IQuantity[] curr_values, Timestamp timestamp);
}
