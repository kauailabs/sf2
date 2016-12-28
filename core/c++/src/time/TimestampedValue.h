/*
 * TimestampedValue.h
 *
 *  Created on: Dec 27, 2016
 *      Author: Scott
 */

#ifndef SRC_TIME_TIMESTAMPEDVALUE_H_
#define SRC_TIME_TIMESTAMPEDVALUE_H_

#include "ITimestampedValue.h"
#include "../quantity/ICopy.h"
#include "../interpolation/IValueInterpolator.h"

template<typename T>
class TimestampedValue :
	ITimestampedValue,
	ICopy<TimestampedValue<T>>,
	IValueInterpolator<TimestampedValue<T>>
{
protected:
	T value;
    long timestamp;
    uint8_t flags;

    static const uint8_t valid_flag = 0x01;
    static const uint8_t interpolated_flag = 0x02;

    TimestampedValue(){
    	timestamp = 0;
    	flags = 0;
    }

    /**
     * Default constructor for a TimestampedValue<T>; initializes all values to reasonable defaults.
     */
    TimestampedValue(T& value) {
    	this->value = value.instantiate_copy();
    	timestamp = 0;
    	flags = 0;
    }

    /**
     * Constructor allowing a TimestampedValue<T> to be created from a T object and a
     * timestamp.
     * @param src - source T object
     * @param timestamp - timestamp representing the time at which the source value is valid.
     */
    TimestampedValue(T& src, long timestamp) {
    	this->value = src.instantiate_copy();
    	setTimestamp(timestamp);
    	this->flags = 0;
    }

    /**
     * Copy constructor; initializes all values to that of the source TimestampedValue<T>.
     * @param src - source TimestampedValue<T>
     */
    TimestampedValue(TimestampedValue<T>& src) {
    	this->value = src.getValue().instantiate_copy();
    	this->timestamp = src.timestamp;
    	this->flags = src.flags;
    }

    /**
     * Returns the timestamp for this TimestampedValue.
     */
    long getTimestamp() {
    	return timestamp;
    }

    void setTimestamp(long timestamp) {
    	this->timestamp = timestamp;
    }

    /**
     * Initalizes this TimestampedValue to be equal to the source TimestampedValue
     * @param src - source TimestampedValue
     */
    void set(TimestampedValue<T>& src) {
    	this->value.copy(src.value);
     	this->timestamp = src.timestamp;
       	this->flags = src.flags;
   }

    /**
     * Initializes this TimestampedValue to be equal to the source value object and a
     * timestamp.
     * @param src - source Quaternion
     * @param timestamp - timestamp representing the time at which the source value object is valid.
     */
    void set(T& src, long timestamp) {
    	this->value.copy(src);
    	this->timestamp = timestamp;
    	this->flags = valid_flag; /* Interpolated = false */
    }

    T& getValue() { return value; }

    /**
     * If true, this TimestampedValue<T> was interpolated, otherwise it is an
     * actual (measured) TimestampedValue<T>
     */
	/* IValueInterpolator */
    bool getInterpolated() {
    	return ((flags & interpolated_flag) != 0);
    }

	/**
	 * Modifies this TimestampedValue<T>'s interpolated state; if true, this
	 * TimestampedValue<T> was interpolated; otherwise, it is an actual
	 * (measured) TimestampedValue<T>.
	 */
    void setInterpolated( bool interpolated ) {
		if ( interpolated ) {
			flags |= interpolated_flag;
		} else {
			flags &= ~interpolated_flag;
		}
     }

	/**
	 * Modifies this TimestampedValue (representing the "from" value) to
	 * represent a new value and Timestamp value which is located at a ratio
	 * (in time) between itself and a "to" TimestampedValue.
	 * <p>
	 * This actual method of interpolation used depends upon the value type (T).
	 */
	void interpolate(TimestampedValue<T>& to, double time_ratio, TimestampedValue<T>& out) {
		TimestampedValue<T>& from = *this;
		this->value.interpolate(to.value,time_ratio, out.getValue());
		float delta_t = to.getTimestamp() - from.getTimestamp();
		delta_t *= time_ratio;
		out.setTimestamp((long)delta_t);
	}

    /**
     * Initalizes this TimestampedValue to be equal to the source TimestampedValue
     * @param src - source TimestampedValue
     */
	/* ICopy */
	void copy(TimestampedValue<T>& t) {
		this->set(t);
	}

	/**
	 * Returns whether this TimestampedValue is valid or not.  This can be used
	 * when the TimestampedValue is stored within a statically-allocated
	 * data structure, allowing reuse of the same object without requiring
	 * the destruction/reconstruction of a new object.
	 */
	/* ITimestampedValue */
	bool getValid() {
		return ((flags & valid_flag) != 0);
	}

	/**
	 * Sets whether this TimestampedValue is valid or not.  This can be used
	 * when the TimestampedValue is stored within a statically-allocated
	 * data structure, allowing reuse of the same object without requiring
	 * the destruction/reconstruction of a new object.
	 */
	void setValid(bool valid) {
		if ( valid ) {
			flags |= valid_flag;
		} else {
			flags &= ~valid_flag;
		}
	}

	TimestampedValue<T>* instantiate_copy() {
		TimestampedValue<T>* new_tsv = new TimestampedValue<T>();
		new_tsv->value = this->value.instantiate_copy();
		return new_tsv;
	}

	IQuantity& getQuantity() {
		return this->value;
	}
};

#endif /* SRC_TIME_TIMESTAMPEDVALUE_H_ */
