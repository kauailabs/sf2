package com.kauailabs.sf2.quantity;

import java.util.ArrayList;

import com.kauailabs.sf2.interpolation.IInterpolate;
import com.kauailabs.sf2.time.ICopy;

public class Scalar implements IInterpolate<Scalar>, ICopy<Scalar>, IQuantity {
	float value;
	public float get() { return value; }
	public void set(float value) { this.value = value; }
	public Scalar(float value) {
		this.value = value;
	}
	public Scalar() {
	}
	@Override
	public void copy(Scalar t) {
		set(t.get());
	}
	@Override
	public Scalar instantiate_copy() {
		return new Scalar(this.value);
	}
	@Override
	/* time_ratio:  interpolation ratio from 0.0 to 1.0. */
	public void interpolate(Scalar to, double time_ratio, Scalar out) {
		float delta = to.value - this.value;
		float interpolated_value = this.value + delta;
		out.set(interpolated_value);
	}
	@Override
	public boolean getPrintableString(StringBuilder printable_string) {
		printable_string.append(value);
		return true;
	}
	@Override
	public boolean getContainedQuantities(ArrayList<IQuantity> quantities) {
		return false;
	}
	@Override
	public boolean getContainedQuantityNames(ArrayList<String> quantity_names) {
		return false;
	}	
}
