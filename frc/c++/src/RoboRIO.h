/* ============================================
 SF2 source code is placed under the MIT license
 Copyright (c) 2017 Kauai Labs

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ===============================================
 */
#ifndef SRC_ROBORIO_H_
#define SRC_ROBORIO_H_

#include <unistd.h>
#include "WPILib.h"
#include "sensor/IProcessorInfo.h"
using namespace std;

class RoboRIO: public IProcessorInfo {
	string hostname;
public:
	RoboRIO() {
		char name[255];
		gethostname(name, sizeof(name));
		hostname = name;
	}

	virtual ~RoboRIO() {
	}

	const string& getName() {
		return hostname;
	}

	void getProcessorTimestamp(Timestamp& ts) {
		ts.setResolution(Timestamp::TimestampResolution::Millisecond);
		ts.fromSeconds(Timer::GetFPGATimestamp());
	}
};

#endif /* SRC_ROBORIO_H_ */
