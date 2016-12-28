/*
 * ITimestampedValue.h
 *
 *  Created on: Dec 27, 2016
 *      Author: Scott
 */

#ifndef SRC_TIME_ITIMESTAMPEDVALUE_H_
#define SRC_TIME_ITIMESTAMPEDVALUE_H_

#include "../quantity/IQuantity.h"

class ITimestampedValue {
public:
	/**
	 * Accessor for this object's sensor timestamp.
	 * @return - sensor timestamp
	 */
    virtual long getTimestamp() = 0;

    /**
     * Indicates whether this object currently represents a valid value/timestamp.
     * @return - true if this object is currently valid, false if not.
     */
    virtual bool getValid() = 0;

    /**
     * Modifies whether this object currently represents a valid value/timestamp.
     * @param valid - true if this object is currently valid, false if not.
     */
    virtual void setValid(bool valid) = 0;

    virtual IQuantity& getQuantity() = 0;;
};

#endif /* SRC_TIME_ITIMESTAMPEDVALUE_H_ */
