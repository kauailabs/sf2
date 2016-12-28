/*
 * RoboRIO.h
 *
 *  Created on: Dec 28, 2016
 *      Author: Scott
 */

#ifndef SRC_ROBORIO_H_
#define SRC_ROBORIO_H_

#include "sensor/IProcessorInfo.h"
#include "time/Timestamp.h"
#include <string>
#include <unistd.h>
#include "WPILib.h"
using namespace std;

class RoboRIO : IProcessorInfo {
	string hostname;
public:
	RoboRIO() {
		char name[255];
		gethostname(name,sizeof(name));
		hostname = name;
	}

	string getName() {
		return hostname;
	}

	void getProcessorTimestamp(Timestamp& t) {
		t.setResolution(Timestamp::TimestampResolution::Millisecond);
		t.fromSeconds(Timer::GetFPGATimestamp());
	}

};

#endif /* SRC_ROBORIO_H_ */
