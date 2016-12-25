package com.kauailabs.sf2.quantity;

import java.util.ArrayList;

import com.kauailabs.sf2.interpolation.IInterpolate;
import com.kauailabs.sf2.time.ICopy;

public class Count implements IInterpolate<Count>, ICopy<Count>, IQuantity, IQuantityContainer {
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
	public void getPrintableString(String printable_string) {
		printable_string = Long.toString(count);
	}
	@Override
	public void getQuantities(ArrayList<IQuantity> quantities) {
		quantities.add(this);
	}
}
