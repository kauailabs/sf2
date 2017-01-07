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

#ifndef SRC_SENSOR_ISENSORDATASUBSCRIBER_H_
#define SRC_SENSOR_ISENSORDATASUBSCRIBER_H_

#include <vector>
using namespace std;

#include "quantity/IQuantity.h"
#include "time/Timestamp.h"

/**
 * Interface to be implemented by any subscriber of Sensor Data of type T.
 * @author Scott
 * @param system_timestamp - the system timestamp when the data was received.
 * @param update - the object of type T representing the newly-received data.
 */
class ISensorDataSubscriber {
public:
	virtual void publish(vector<IQuantity*> curr_values,
			Timestamp& timestamp) = 0;
	virtual ~ISensorDataSubscriber() {
	}
};

#endif /* SRC_SENSOR_ISENSORDATASUBSCRIBER_H_ */
