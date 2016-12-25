package com.kauailabs.sf2.quantity;

import java.util.ArrayList;

import com.kauailabs.sf2.interpolation.IInterpolate;
import com.kauailabs.sf2.time.ICopy;

public class Vector implements IInterpolate<Vector>, ICopy<Vector>, IQuantity, IQuantityContainer {
	Scalar direction;
	Scalar magnitude;
	private Vector()
	{		
	}
	public Vector(Scalar direction, Scalar magnitude) {
		this.direction = direction;
		this.magnitude = magnitude;
	}
	public float getDirection() { return direction.get(); }
	public float getMagnitude() { return magnitude.get(); }
	@Override
	public void copy(Vector t) {
		this.direction.copy(t.direction);
		this.magnitude.copy(t.magnitude);
	}
	@Override
	public Vector instantiate_copy() {
		Vector v = new Vector();
		v.copy(this);
		return v;
	}
	@Override
	public void interpolate(Vector to, double time_ratio, Vector out) {
		Scalar direction = this.direction.instantiate_copy();
		Scalar magnitude = this.magnitude.instantiate_copy();
		out.direction = direction;
		out.magnitude = magnitude;
	}
	@Override
	public void getQuantities(ArrayList<IQuantity> quantities) {
		direction.getQuantities(quantities);
		magnitude.getQuantities(quantities);
	}
	@Override
	public void getPrintableString(String printable_string) {
		String direction_printable_string = new String();
		String magnitude_printable_string = new String();
		direction.getPrintableString(direction_printable_string);
		magnitude.getPrintableString(magnitude_printable_string);
		printable_string = direction + ", " + magnitude;
	}
}
