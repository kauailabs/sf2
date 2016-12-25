package com.kauailabs.sf2.pose;

import com.kauailabs.sf2.interpolation.IInterpolate;
import com.kauailabs.sf2.orientation.Quaternion;
import com.kauailabs.sf2.quantity.IQuantity;
import com.kauailabs.sf2.quantity.IQuantityContainer;
import com.kauailabs.sf2.quantity.Scalar;
import com.kauailabs.sf2.time.ICopy;
import com.kauailabs.sf2.units.Unit;
import com.kauailabs.sf2.units.Unit.IUnit;

/**
 * The Pose class represents a 2-dimensional position, and a 
 * 3-dimensional orientation at a given instant in time.
 * 
 * Note that the Pose class also provides a timestamp, representing
 * the sensor timestamp for the "master timestamp clock source".
 * 
 * In addition to representing a pose, the Pose class can also 
 * represent an amount of change in pose, in which case the 
 * timestamp represents the duration of time comprising that 
 * position and orientation motion.
 * @author Scott
 */

public class Pose implements IInterpolate<Pose>, ICopy<Pose>, IQuantity, IQuantityContainer {	
	
	float x_offset_inches;
	float y_offset_inches;
	Quaternion quat;
	
	public Pose() {
		set( new Quaternion(), 0, 0 );
	}
	
	public void set(Quaternion quat, float x_offset_inches, float y_offset_inches) {
		quat.set(quat);
		this.x_offset_inches = x_offset_inches;
		this.y_offset_inches = y_offset_inches;
	}	
	
	public void addOffsets( float x_offset_delta_inches, float y_offset_delta_inches ) {
		x_offset_inches += x_offset_delta_inches;
		y_offset_inches += y_offset_delta_inches;
	}
	
	public Pose(Quaternion quat) {
		reset(quat);
	}
	
	public Pose(Quaternion quat, float x_offset_inches, float y_offset_inches) {
		set(quat, x_offset_inches, y_offset_inches);
	}
	
	public void reset(Quaternion quat) {
		set(quat,0,0);
	}	

	public Quaternion getOrientation() {
		return quat;
	}
	
   /* Estimates an intermediate Pose given Poses representing each end of the path,
    * and an interpolation (time) ratio from 0.0 t0 1.0. */
	
	public static void interpolate( Pose from, Pose to, double t, Pose out) {
		Pose interp_pose = null;
		/* First, interpolate the aggregate Quaternion */
		from.quat.interpolate(to.quat, t, out.quat);
		/* Interpolate the X and Y offsets */
		float x_offset_delta = to.x_offset_inches - from.x_offset_inches;
		x_offset_delta *= t;
		out.x_offset_inches = from.x_offset_inches + x_offset_delta;

		float y_offset_delta = to.y_offset_inches - from.y_offset_inches;
		y_offset_delta *= t;
		out.y_offset_inches = from.y_offset_inches + y_offset_delta;
	}
	
	public double getOffsetInchesX() {
		return x_offset_inches;
	}

	public double getOffsetInchesY() {
		return y_offset_inches;
	}

	@Override
	public void copy(Pose t) {
		this.set(t.quat,t.x_offset_inches,t.y_offset_inches);
	}

	@Override
	public void interpolate(Pose to, double time_ratio, Pose out) {
		interpolate(this, to, time_ratio, out);
	}
	
	@Override
	public Pose instantiate_copy() {
		Pose new_p = new Pose();
		new_p.copy(this);
		return new_p;
	}

	@Override
	public void getQuantities(IQuantity[] quantities) {
		quantities = new IQuantity[] {
			new Scalar(x_offset_inches),
			new Scalar(y_offset_inches),
			new Scalar(quat.getW()),
			new Scalar(quat.getX()),
			new Scalar(quat.getY()),
			new Scalar(quat.getZ())
		};
	}
	static public void getUnits(IUnit[] units) {
		units = new IUnit[] {
			new Unit().new Distance().new Inches(),
			new Unit().new Distance().new Inches(),
			new Unit().new Unitless(),			
			new Unit().new Unitless(),			
			new Unit().new Unitless(),			
			new Unit().new Unitless(),			
		};
	}

	@Override
	public void getPrintableString(String printable_string) {
		String quat_printable_string = new String();
		quat.getPrintableString(quat_printable_string);
		printable_string = Float.toString(x_offset_inches) + ", " + Float.toString(y_offset_inches) + ", " + quat_printable_string;
	}	
}
