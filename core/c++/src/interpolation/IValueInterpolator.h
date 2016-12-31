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

#ifndef SRC_INTERPOLATION_IVALUEINTERPOLATOR_H_
#define SRC_INTERPOLATION_IVALUEINTERPOLATOR_H_

/**
 * The IValueInterpolator interface must be implemented by all objects which
 * have both a value and a timestamp component and support interpolation
 * of intermediate values.
 */
template<typename T>
class IValueInterpolator {

public:
	/**
	 * Returns a new object of type T which whose value and timestamp are
	 * interpolated between this "from" object and the provided "to" object
	 * at a point between them expressed by a time ratio.
	 * @param to - the "to" object (also of type T) representing an object which is
	 * "later" in time than this object.
	 * @param time_ratio - the ratio (from 0 to 1) in time between this object
	 * and the "to" object at which point the interpolated value should occur.
	 * @return a new object of type T whose value and timestamp are interpolated
	 * by this method.
	 */
	virtual void interpolate(T& to, double time_ratio, T& out) = 0;

	/**
	 * Indicates whether this object represents an actual (measured) value/timestamp,
	 * or an interpolated value/timestamp.
	 * @return - true if this object is interpolated; false if it is actual (measured).
	 */
	virtual bool getInterpolated() = 0;

	/**
	 * Marks this object as being either actual (measured) value/timestamp, or interpolated.
	 * @param interpolated - true if the object is interpolated, false if it is actual (measured).
	 */
	virtual void setInterpolated(bool interpolated) = 0;

	/**
	 * Initializes this object with the provided object.
	 * @param t - the object to intializes this object with.
	 */
	virtual void copy(T& t) = 0;

	virtual ~IValueInterpolator() {
	}
};

#endif /* SRC_INTERPOLATION_IVALUEINTERPOLATOR_H_ */
