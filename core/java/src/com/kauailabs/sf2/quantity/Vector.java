package com.kauailabs.sf2.quantity;

import com.kauailabs.sf2.interpolation.IInterpolate;
import com.kauailabs.sf2.time.ICopy;

public class Vector implements IInterpolate<Vector>, ICopy<Vector>, IQuantityContainer {
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
	public Vector interpolate(Vector to, double time_ratio) {
		Scalar direction = this.direction.instantiate_copy();
		Scalar magnitude = this.magnitude.instantiate_copy();
		return new Vector(direction, magnitude);
	}
	@Override
	public IQuantity[] getQuantities() {
		return new IQuantity[] { direction, magnitude };
	}
}
