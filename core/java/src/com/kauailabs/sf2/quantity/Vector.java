package com.kauailabs.sf2.quantity;

import java.util.ArrayList;

import com.kauailabs.sf2.interpolation.IInterpolate;
import com.kauailabs.sf2.time.ICopy;

public class Vector implements IInterpolate<Vector>, ICopy<Vector>, IQuantity {
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
	public boolean getPrintableString(StringBuilder printable_string) {
		return false;
	}

	@Override
	public boolean getContainedQuantities(ArrayList<IQuantity> quantities) {
		quantities.add(direction);
		quantities.add(magnitude);
		return true;
	}
	@Override
	public boolean getContainedQuantityNames(ArrayList<String> quantity_names) {
		quantity_names.add("Direction");
		quantity_names.add("Magnitude");
		return true;
	}
}
