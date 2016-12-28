/*
 * IQuantity.h
 *
 *  Created on: Dec 27, 2016
 *      Author: Scott
 */

#ifndef SRC_QUANTITY_IQUANTITY_H_
#define SRC_QUANTITY_IQUANTITY_H_

#include <forward_list>
#include <string>

using namespace std;

class IQuantity {
public:
	/* Returns true if this quantity has a printable value */
	virtual bool getPrintableString(forward_list<string>& printable_string) = 0;
	/* Returns true if this is a quantity container */
	virtual bool getContainedQuantities(forward_list<IQuantity *>& quantities) = 0;
	/* Returns true if this is a quantity container. */
	virtual bool getContainedQuantityNames(forward_list<string>& quantity_names) = 0;
};

#endif /* SRC_QUANTITY_IQUANTITY_H_ */
