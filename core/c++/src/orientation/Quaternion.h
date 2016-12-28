/*
 * Quaternion.h
 *
 *  Created on: Dec 28, 2016
 *      Author: Scott
 */

#ifndef SRC_ORIENTATION_QUATERNION_H_
#define SRC_ORIENTATION_QUATERNION_H_

/*----------------------------------------------------------------------------*/
/* Copyright (c) Kauai Labs 2016. All Rights Reserved.                        */
/*                                                                            */
/* Created in support of Team 2465 (Kauaibots).  Go Purple Wave!              */
/*                                                                            */
/* Open Source Software - may be modified and shared by FRC teams. Any        */
/* modifications to this code must be accompanied by the \License.txt file    */
/* in the root directory of the project.                                      */
/*----------------------------------------------------------------------------*/

#include <forward_list>
using namespace std;

#include "../Unit/Unit.h"
/**
 * The Quaternion class provides methods to operate on a quaternion.
 * <a href="https://en.wikipedia.org/wiki/Quaternion">Quaternions</a> are used
 * among other things to describe rotation in 3D dimensions.  This is
 * typically performed using a Unit Quaternion (also known as a "Versor".
 * <p>
 * Provided Quaternion operations include basic mathematic operations as well as
 * a method for interpolation.
 * <p>
 * Quaternions actually describe 4 separate rotations - 3 dimensions of
 * rotation about a coordinate reference frame, and then a fourth rotation
 * of the coodinate reference frame to another reference frame.
 * <p>
 * At their core, Quaternions are comprised of W, X, Y and Z components, which
 * in the case of the Unit Quaternion are expressed in units of Radians which
 * have a range from -2 to 2.
 * <p>
 * From a number theory perspecive, a quaternion is a complex number.  A quaternion
 * is a formal sum of a real number and real multiples of the symbols
 * i, j, and k. For example,
 * <p>
 * <t><i>q = W + Xi + Yj + Zk</i></t>
 * <p>
 * A Unit Quaternion can express not only rotations, but also the gravity vector.
 * Therefore, the Quaternion class provides methods for deriving the gravity vector
 * as well as the more commonly-used Yaw, Pitch and Roll angles (also known as
 * Tait/Bryan angles).
 * @author Scott
 */

class Quaternion : IInterpolate<Quaternion>, ICopy<Quaternion>, IQuantity {

    float w;
    float x;
    float y;
    float z;

    class FloatVectorStruct {
        float x;
        float y;
        float z;
    };

    static Unit::Unitless component_units;

public:
    /**
     * Constructs a Quaternion instance, using default values for a
     * Unit Quaternion.
     */
    Quaternion() {
    	set(1,0,0,0);
    }

    /**
     * Constructs a Quaternion instance, using values from another
     * Quaternion instance.
     * @param src - the Quaternion instance used to initialize this Quaternion.
     */
    Quaternion(const Quaternion& src) {
    	set(src);
    }

    /**
     * Constructs a Quaternion instance, using the provides w, x, y and z valuese.
     * @param w - the Quaternion W component value.
     * @param x - the Quaternion X component value.
     * @param y - the Quaternion Y component value.
     * @param z - the Quaternion Z component value.
     */
    Quaternion(float w, float x, float y, float z) {
    	set(w, x, y, z);
    }

    /**
     * Modifies the Quaternion by setting the component W, X, Y and Z value.
     * @param w - the Quaternion W component value.
     * @param x - the Quaternion X component value.
     * @param y - the Quaternion Y component value.
     * @param z - the Quaternion Z component value.
     */
    void set(float w, float x, float y, float z) {
        this->w = w;
        this->x = x;
        this->y = y;
        this->z = z;
    }

    /**
     * Modifes the Quaternion to be equal to the provided Quaternion.
     * @param src - the Quaternion instance used to initialize this Quaternion.
     */
    void set(const Quaternion& src) {
    	set(src.w, src.x, src.y, src.z);
    }

    /**
     * Extracts the gravity vector from the Quaternion.
     * @param v - the output vector containing the quaternion's gravity component.
     * @param q - the source quaternion.
     */
    static void getGravity(FloatVectorStruct& v, const Quaternion& q) {
        v.x = 2 * ((q.x*q.z) - (q.w*q.y));
        v.y = 2 * ((q.w*q.x) + (q.y*q.z));
        v.z = (q.w*q.w) - (q.x*q.x) - (q.y*q.y) + (q.z*q.z);
    }

    /**
     * Extracts the yaw, pitch and roll values from the Quaternion.
     * Returned values are in units of Radians
     * @param q - the source quaternion
     * @param gravity - the gravity component of the quaternion
     * @param ypr - a FloatVectorStruct containing the yaw/pitch/roll values extracted
     * from the Quaternion.  x:  yaw; y:  pitch; z:  roll.
     */
    static void getYawPitchRoll(const Quaternion& q, const FloatVectorStruct& gravity, FloatVectorStruct& ypr) {
        // yaw: (clockwise rotation, about Z axis)
        ypr.x = (float)atan2((2*(q.x*q.y)) - (2*(q.w*q.z)), (2*(q.w*q.w)) + (2*(q.x*q.x)) - 1);
        // pitch: (tilt up/down, about X axis)
        ypr.y = (float)atan(gravity.y / sqrt((gravity.x*gravity.x) + (gravity.z*gravity.z)));
        // roll: (tilt left/right, about Y axis)
        ypr.z = (float)atan(gravity.x / sqrt((gravity.y*gravity.y) + (gravity.z*gravity.z)));
    }

    /**
     * Extracts the yaw, pitch and roll values from the Quaternion.
     * Returned values are in units of Radians.
     */
    void getYawPitchRollRadians(FloatVectorStruct& ypr) {
    	FloatVectorStruct gravity;
    	getGravity(gravity,*this);
    	getYawPitchRoll(*this,gravity,ypr);
    }

    /**
     * Extracts the yaw angle value from the Quaternion.
     * The Return value is in units of Radians.
     */
    void getYawRadians(Scalar& yaw) {
    	FloatVectorStruct ypr;
    	getYawPitchRollRadians(ypr);
    	yaw.set(ypr.x);
    }

    /**
     * Extracts the pitch angle value from the Quaternion.
     * The Return value is in units of Radians.
     */
    void getPitch(Scalar& pitch) {
    	FloatVectorStruct ypr;
    	getYawPitchRollRadians(ypr);
    	pitch.set(ypr.y);
    }

    /**
     * Extracts the roll angle value from the Quaternion.
     * The Return value is in units of Radians.
     */
    void getRoll(Scalar& roll) {
    	FloatVectorStruct ypr;
    	getYawPitchRollRadians(ypr);
    	roll.set(ypr.z);
    }

    /**
     * Estimates an intermediate Quaternion given Quaternions representing each end of the path,
     * and an interpolation ratio from 0.0 t0 1.0.
     *
     * Uses Quaternion SLERP (Spherical Linear Interpolation), an algorithm
     * originally introduced by Ken Shoemake in the context of quaternion interpolation for the
     * purpose of animating 3D rotation. This estimation is based upon the assumption of
     * constant-speed motion along a unit-radius great circle arc.
     *
     * For more info:
     *
     * http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/slerp/index.htm
     */
    static void slerp(const Quaternion& qa, const Quaternion& qb, double t, Quaternion& out) {
    	// Calculate angle between them.
    	double cosHalfTheta = qa.w * qb.w + qa.x * qb.x + qa.y * qb.y + qa.z * qb.z;
    	// if qa=qb or qa=-qb then theta = 0 and we can return qa
    	if (abs(cosHalfTheta) >= 1.0){
    		out.w = qa.w;
    		out.x = qa.x;
    		out.y = qa.y;
    		out.z = qa.z;
    		return;
    	}
    	// Calculate temporary values.
    	double halfTheta = acos(cosHalfTheta);
    	double sinHalfTheta = sqrt(1.0 - cosHalfTheta*cosHalfTheta);
    	// if theta = 180 degrees then result is not fully defined
    	// we could rotate around any axis normal to qa or qb
    	if (abs(sinHalfTheta) < 0.001){
    		out.w = (qa.w * 0.5f + qb.w * 0.5f);
    		out.x = (qa.x * 0.5f + qb.x * 0.5f);
    		out.y = (qa.y * 0.5f + qb.y * 0.5f);
    		out.z = (qa.z * 0.5f + qb.z * 0.5f);
    		return;
    	}
    	float ratioA = (float)(sin((1 - t) * halfTheta) / sinHalfTheta);
    	float ratioB = (float)(sin(t * halfTheta) / sinHalfTheta);
    	//calculate Quaternion.
    	out.w = (qa.w * ratioA + qb.w * ratioB);
    	out.x = (qa.x * ratioA + qb.x * ratioB);
    	out.y = (qa.y * ratioA + qb.y * ratioB);
    	out.z = (qa.z * ratioA + qb.z * ratioB);
    	return;
    }

    /**
     * Modifies the Quaternion to be its complex conjugate.
     *
     * The <a href="https://en.wikipedia.org/wiki/Complex_conjugate">complex conjugate</a> of a
     * complex number is the number with equal real part and imaginary part
     * equal in magnitude but opposite in sign.
     */
    void conjugate() {
    	this->x = -this->x;
    	this->y = -this->y;
    	this->z = -this->z;
    }

    /**
     * Modifies the Quaternion to be its inverse (reciprocal).
     *
     * The quaternion inverse of a rotation is the opposite rotation, so it can be thought of as a
     * mirror image of the original quaternion
     */
    void inverse() {
        this->conjugate();
        this->divide(dotProduct(*this, *this));
    }

    /**
     * Modifies this quaternion (the multiplicand) to be the product of
     * multiplication by a multiplier Quaternion.
     *
     * <i>Key point:  the result of multiplying two Quaternions is to logically
     * add together their respective rotations.</i>
     *
     * Note that Quaternion multiplication is NOT <a href="https://en.wikipedia.org/wiki/Commutative_property">commutative</a>,
     * in other words when multiplying Quaternions, the result of a * b
     * is NOT the same as b * a.
     * @param q - the multiplier quaternion.
     */
    void multiply(const Quaternion& q) {
    	float w, x, y, z;

    	x = this->w*q.x + this->x*q.w + this->y*q.z - this->z*q.y;
    	y = this->w*q.y + this->y*q.w + this->z*q.x - this->x*q.z;
    	z = this->w*q.z + this->z*q.w + this->x*q.y - this->y*q.x;
    	w = this->w*q.w - this->x*q.x - this->y*q.y - this->z*q.z;

    	this->w = w;
    	this->x = x;
    	this->y = y;
    	this->z = z;
    }

    /**
     * Modifies a quaternion, scaling it by the provided parameter.
     * @param s - the value by which to divide each Quaternion component value.
     */
    void divide(float s) {
    	this->w = this->w / s;
       	this->x = this->x / s;
       	this->y = this->y / s;
       	this->z = this->z / s;
    }

    float dotProduct(const Quaternion& q1, const Quaternion& q2) {
    	return q1.x*q2.x + q1.y*q2.y + q1.z*q2.z + q1.w*q2.w;
    }

    /**
     * Divides two quaternions.  Since Quaternion multiplication is not
     * commutative, to perform this operation, the multiplicand Quaternion
     * is multiplied by the inverse of the multiplier Quaternion.
     *
     * <i>Key point:  the result of dividing two Quaternions is to logically
     * subtract their respective rotations.  Thus, use difference() to calculate
     * the amount of 3D rotation between two Quaternions.</i>
     *
     * @param qa - the dividend Quaternion
     * @param qb - the divisor Quaternion
     * @param q_diff - the resulting quotient Quaternion representing the difference
     * in rotation
     */
    static void difference(const Quaternion& qa, const Quaternion& qb, Quaternion& q_diff) {
    	q_diff.set(qa.w, qa.x, qa.y, qa.z);
    	q_diff.inverse();
    	q_diff.multiply(qb);
    }

    /**
     * Accessor for the Quaternion's W component value.
     * @return Quaternion W component value.
     */
    float getW() { return w; }

    /**
     * Accessor for the Quaternion's X component value.
     * @return Quaternion X component value.
     */
    float getX() { return x; }

    /**
     * Accessor for the Quaternion's Y component value.
     * @return Quaternion Y component value.
     */
    float getY() { return y; }

    /**
     * Accessor for the Quaternion's Z component value.
     * @return Quaternion Z component value.
     */
    float getZ() { return z; }

	void interpolate(const Quaternion& to, double time_ratio, Quaternion& out) {
		Quaternion::slerp(*this, to, time_ratio, out);
	}

	void copy(Quaternion& t) {
		this->w = t.w;
		this->x = t.x;
		this->y = t.y;
		this->z = t.z;
	}

	Quaternion* instantiate_copy() {
		return new Quaternion(this);
	}

	static void getUnits(forward_list<Unit::IUnit *>& units) {
		units.insert_after(units.end(),&component_units);
		units.insert_after(units.end(),&component_units);
		units.insert_after(units.end(),&component_units);
		units.insert_after(units.end(),&component_units);
	}

	bool getPrintableString(forward_list<string>& printable_string) {
		return false;
	}

	bool getContainedQuantities(forward_list<IQuantity *>& quantities) {
		quantities.insert_after(quantities.end(),new Scalar(w));
		quantities.insert_after(quantities.end(),new Scalar(x));
		quantities.insert_after(quantities.end(),new Scalar(y));
		quantities.insert_after(quantities.end(),new Scalar(z));
		return true;
	}

	bool getContainedQuantityNames(forward_list<string>& quantity_names) {
		quantity_names.insert_after(quantity_names.end(),("W"));
		quantity_names.insert_after(quantity_names.end(),("X"));
		quantity_names.insert_after(quantity_names.end(),("Y"));
		quantity_names.insert_after(quantity_names.end(),("Z"));
		return true;
	}
};

#endif /* SRC_ORIENTATION_QUATERNION_H_ */
