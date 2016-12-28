/*
 * IProcessorInfo.h
 *
 *  Created on: Dec 28, 2016
 *      Author: Scott
 */

#ifndef SRC_SENSOR_IPROCESSORINFO_H_
#define SRC_SENSOR_IPROCESSORINFO_H_

using namespace std;

class IProcessorInfo {
public:
	virtual string getName() = 0;
	virtual void getProcessorTimestamp(Timestamp& out) = 0;
};

#endif /* SRC_SENSOR_IPROCESSORINFO_H_ */
