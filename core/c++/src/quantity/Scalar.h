/*
 * Scalar.h
 *
 *  Created on: Dec 28, 2016
 *      Author: Scott
 */

#ifndef SRC_QUANTITY_SCALAR_H_
#define SRC_QUANTITY_SCALAR_H_

#include <forward_list>
using namespace std;

class Scalar : IInterpolate<Scalar>, ICopy<Scalar>, IQuantity {

	float value;

public:
	float get() { return value; }

	void set(float value) { this->value = value; }

	Scalar(float value) {
		this->value = value;
	}

	Scalar() {
		value = 0;
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

	bool getPrintableString(forward_list<string>& printable_string) {
		printable_string.insert_after(printable_string.end(),string(value));
		return true;
	}

	bool getContainedQuantities(forward_list<IQuantity *>& quantities) {
		return false;
	}

	bool getContainedQuantityNames(forward_list<string>& quantity_names) {
		return false;
	}
}

#endif /* SRC_QUANTITY_SCALAR_H_ */
