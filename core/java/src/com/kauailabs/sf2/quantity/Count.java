package com.kauailabs.sf2.quantity;

import java.util.ArrayList;

import com.kauailabs.sf2.interpolation.IInterpolate;

public class Count implements IInterpolate<Count>, ICopy<Count>, IQuantity {
	long count;
	public long get() { return count; }
	public void set(long value) { count = value; }
	public Count(long value) {
		this.count = value;
	}
	public Count() {
	}
	@Override
	public void copy(Count t) {
		set(t.get());
	}
	@Override
	public Count instantiate_copy() {
		return new Count(this.count);
	}
	@Override
	/* time_ratio:  interpolation ratio from 0.0 to 1.0. */
	public void interpolate(Count to, double time_ratio, Count out) {
		long delta = to.count - this.count;
		long interpolated_value = this.count + delta;
		out.set(interpolated_value);
	}
	@Override
	public boolean getPrintableString(StringBuilder printable_string) {
		printable_string.append(count);
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
