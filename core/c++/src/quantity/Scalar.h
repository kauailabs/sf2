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

#ifndef SRC_QUANTITY_SCALAR_H_
#define SRC_QUANTITY_SCALAR_H_

#include <forward_list>
using namespace std;

#include "interpolation/IInterpolate.h"
#include "quantity/IQuantity.h"
#include "quantity/ICopy.h"

class Scalar: public IInterpolate<Scalar>, public ICopy<Scalar>, public IQuantity {

	float value;

public:
	float get() {
		return value;
	}

	void set(float value) {
		this->value = value;
	}

	Scalar(float value) {
		this->value = value;
	}

	Scalar() {
		value = 0;
	}

	~Scalar() {
	}

	void copy(Scalar& t) {
		set(t.get());
	}

	Scalar *instantiate_copy() {
		return new Scalar(this->value);
	}

	/* time_ratio:  interpolation ratio from 0.0 to 1.0. */
	void interpolate(const Scalar& to, double time_ratio, Scalar& out) {
		float delta = to.value - this->value;
		float interpolated_value = this->value + delta;
		out.set(interpolated_value);
	}

	bool getPrintableString(vector<string>& printable_string) {
		printable_string.push_back(std::to_string(value));
		return true;
	}

	bool getContainedQuantities(vector<IQuantity *>& quantities) {
		return false;
	}

	bool getContainedQuantityNames(vector<string>& quantity_names) {
		return false;
	}
};

#endif /* SRC_QUANTITY_SCALAR_H_ */
