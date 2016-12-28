/*
 * ISensorDataSubscriber.h
 *
 *  Created on: Dec 28, 2016
 *      Author: Scott
 */

#ifndef SRC_SENSOR_ISENSORDATASUBSCRIBER_H_
#define SRC_SENSOR_ISENSORDATASUBSCRIBER_H_

#include <forward_list>
using namespace std;

/**
 * Interface to be implemented by any subscriber of Sensor Data of type T.
 * @author Scott
 * @param system_timestamp - the system timestamp when the data was received.
 * @param update - the object of type T representing the newly-received data.
 */
class ISensorDataSubscriber {
public:
	virtual void publish(forward_list<IQuantity&> curr_values, Timestamp& timestamp) = 0;
};




#endif /* SRC_SENSOR_ISENSORDATASUBSCRIBER_H_ */
