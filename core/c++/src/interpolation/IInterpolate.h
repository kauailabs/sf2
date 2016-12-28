/*
 * IInterpolate.h
 */

#ifndef SRC_INTERPOLATION_IINTERPOLATE_H_
#define SRC_INTERPOLATION_IINTERPOLATE_H_

template<typename T>
class IInterpolate {

public:
	virtual void interpolate(const T& to, double time_ratio, T& out) = 0;
};




#endif /* SRC_INTERPOLATION_IINTERPOLATE_H_ */
