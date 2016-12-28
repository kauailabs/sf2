/*
 * Timestamp.h
 *
 *  Created on: Dec 27, 2016
 *      Author: Scott
 */

#ifndef SRC_TIME_TIMESTAMP_H_
#define SRC_TIME_TIMESTAMP_H_

#include "../quantity/IQuantity.h"

class Timestamp : public IQuantity {

public:
	enum TimestampResolution { Second, Millisecond, Microsecond, Nanosecond };
	long timestamp;
	TimestampResolution resolution;

	Timestamp() {
		this->timestamp = 0;
		this->resolution = TimestampResolution::Millisecond;
	}

	Timestamp(Timestamp& ts_copy) {
		this->timestamp = ts_copy.timestamp;
		this->resolution = ts_copy.resolution;
	}

	Timestamp(long timestamp, TimestampResolution resolution) {
		this->timestamp = timestamp;
		this->resolution = resolution;
	}

	Timestamp(double seconds, TimestampResolution resolution) {
		this->resolution = resolution;
		this->fromSeconds(seconds);
	}

	long getTimestamp() {
		return this->timestamp;
	}

	void setTimestamp(long new_timestamp) {
		this->timestamp = new_timestamp;
	}

	void setResolution(TimestampResolution r) {
		this->resolution = r;
	}

	TimestampResolution getResolution() {
		return this->resolution;
	}

	static const long MILLISECONDS_PER_SECOND = 1000;
	static const long MICROSECONDS_PER_SECOND = MILLISECONDS_PER_SECOND * 1000;
	static const long NANOSECONDS_PER_SECOND = MICROSECONDS_PER_SECOND * 1000;
	static const long NANOSECONDS_PER_MICROSECOND = 1000;
	static const long MICROSECONDS_PER_MILLISECOND = 1000;
	static const long NANOSECONDS_PER_MILLISECOND = NANOSECONDS_PER_MICROSECOND * 1000;

	long getNanoseconds() {
		switch ( resolution ) {
		case Second:
			return timestamp * NANOSECONDS_PER_SECOND;
		case Millisecond:
			return timestamp * NANOSECONDS_PER_MILLISECOND;
		case Microsecond:
			return timestamp * NANOSECONDS_PER_MICROSECOND;
		case Nanosecond:
		default:
			return timestamp;
		}
	}

	long getMicroseconds() {
		switch ( resolution ) {
		case Second:
			return timestamp * MICROSECONDS_PER_SECOND;
		case Millisecond:
			return timestamp * MICROSECONDS_PER_MILLISECOND;
		case Microsecond:
		default:
			return timestamp;
		case Nanosecond:
			return timestamp / NANOSECONDS_PER_MICROSECOND;
		}
	}

	long getMilliseconds() {
		switch ( resolution ) {
		case Second:
			return timestamp * MILLISECONDS_PER_SECOND;
		case Millisecond:
		default:
			return timestamp;
		case Microsecond:
			return timestamp / MICROSECONDS_PER_MILLISECOND;
		case Nanosecond:
			return timestamp / NANOSECONDS_PER_MILLISECOND;
		}
	}

	double getSeconds() {
		switch ( resolution ) {
		case Second:
			return (double)timestamp;
		case Millisecond:
		default:
			return ((double)timestamp)/MILLISECONDS_PER_SECOND;
		case Microsecond:
			return ((double)timestamp)/MICROSECONDS_PER_SECOND;
		case Nanosecond:
			return ((double)timestamp)/NANOSECONDS_PER_SECOND;
		}
	}

	void fromSeconds(double seconds) {
		switch ( resolution ) {
		case Second:
			timestamp = (long)seconds;
			break;
		case Millisecond:
		default:
			timestamp = (long)(seconds * MILLISECONDS_PER_SECOND);
			break;
		case Microsecond:
			timestamp = (long)(seconds * MICROSECONDS_PER_SECOND);
			break;
		case Nanosecond:
			timestamp = (long)(seconds * NANOSECONDS_PER_SECOND);
			break;
		}
	}

	/* IQuantity */
	bool getPrintableString(forward_list<string>& printable_string) {
		printable_string.push_front(to_string(timestamp));
		return true;
	}

	bool getContainedQuantities(forward_list<IQuantity *>& quantities) {
		return false;
	}

	bool getContainedQuantityNames(forward_list<string>& quantity_names) {
		return false;
	}
};

#endif /* SRC_TIME_TIMESTAMP_H_ */
