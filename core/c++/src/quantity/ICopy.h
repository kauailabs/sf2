/*
 * ICopy.h
 *
 *  Created on: Dec 27, 2016
 *      Author: Scott
 */

#ifndef SRC_QUANTITY_ICOPY_H_
#define SRC_QUANTITY_ICOPY_H_

template<typename T>
class ICopy {
public:
	virtual void copy(T& t) = 0;
	virtual T* instantiate_copy() = 0;
};

#endif /* SRC_QUANTITY_ICOPY_H_ */
