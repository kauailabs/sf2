package com.kauailabs.sf2.quantity;

import java.util.ArrayList;

import com.kauailabs.sf2.interpolation.IInterpolate;
import com.kauailabs.sf2.time.ICopy;

public class Boolean implements IInterpolate<Boolean>, ICopy<Boolean>, IQuantity, IQuantityContainer {
	boolean value;
	public boolean get() { return value; }
	public void set(boolean value) { this.value = value; }
	public Boolean(boolean value) {
		this.value = value;
	}
	public Boolean() {
	}
	@Override
	public void copy(Boolean t) {
		set(t.get());
	}
	@Override
	public Boolean instantiate_copy() {
		return new Boolean(this.value);
	}
	@Override
	/* time_ratio:  interpolation ratio from 0.0 to 1.0. */
	public void interpolate(Boolean to, double time_ratio, Boolean out) {
		out.set((time_ratio >= 0.5) ? to.value : this.value);
	}
	@Override
	public void getPrintableString(String printable_string) {
		printable_string = value ? "True" : "False";
	}
	@Override
	public void getQuantities(ArrayList<IQuantity> quantities) {
		quantities.add(this);
	}
}
