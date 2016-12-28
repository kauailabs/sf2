/*
 * ISensorInfo.h
 *
 *  Created on: Dec 28, 2016
 *      Author: Scott
 */

#ifndef SRC_SENSOR_ISENSORINFO_H_
#define SRC_SENSOR_ISENSORINFO_H_

#include <string>
using namespace std;

class ISensorInfo {
public:
	virtual string getMake() = 0;
	virtual string getModel() = 0;
	virtual string getName() = 0;
	virtual ISensorDataSource& getSensorDataSource() = 0;
	virtual TimestampInfo& getSensorTimestampInfo() = 0;
	virtual IProcessorInfo& getHostProcessorInfo() = 0;
};

#endif /* SRC_SENSOR_ISENSORINFO_H_ */
