package com.kauailabs.sf2.motion;

import java.util.ArrayList;

import com.kauailabs.sf2.interpolation.IInterpolate;
import com.kauailabs.sf2.orientation.Quaternion;
import com.kauailabs.sf2.quantity.ICopy;
import com.kauailabs.sf2.quantity.IQuantity;
import com.kauailabs.sf2.quantity.Scalar;

public class LinearAcceleration  implements IInterpolate<LinearAcceleration>, ICopy<LinearAcceleration>, IQuantity {
	
	float x;
	float y;
	float z;

	/**
	 * Constructs a LinearAcceleration instance, using default values (0).
	 */
	public LinearAcceleration() {
		set(0, 0, 0);
	}

	/**
	 * Constructs a LinearAcceleration instance, using values from another LinearAcceleration
	 * instance.
	 * 
	 * @param src
	 *            - the LinearAcceleration instance used to initialize this instance.
	 */
	public LinearAcceleration(final LinearAcceleration src) {
		set(src);
	}

	/**
	 * Constructs a LinearAcceleration instance, using the provided x, y and z
	 * values.
	 * 
	 * @param x
	 *            - the LinearAcceleration X component value
	 * @param y
	 *            - the LinearAcceleration Y component value
	 * @param z
	 *            - the LinearAcceleration Z component value
	 */
	public LinearAcceleration(float x, float y, float z) {
		set(x, y, z);
	}

	/**
	 * Modifies the LinearAcceleration by setting the component X, Y and Z value.
	 * 
	 * @param x
	 *            - the LinearAcceleration X component value.
	 * @param y
	 *            - the LinearAcceleration Y component value.
	 * @param z
	 *            - the LinearAcceleration Z component value.
	 */
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Modifies the LinearAcceleration to be equal to the provided LinearAcceleration.
	 * 
	 * @param src
	 *            - the LinearAcceleration instance used to initialize this LinearAcceleration.
	 */
	public void set(final LinearAcceleration src) {
		set(src.x, src.y, src.z);
	}

	/**
	 * Accessor for the LinearAcceleration X component value.
	 * 
	 * @return LinearAcceleration X component value.
	 */
	public float getX() {
		return x;
	}

	/**
	 * Accessor for the LinearAcceleration Y component value.
	 * 
	 * @return LinearAcceleration Y component value.
	 */
	public float getY() {
		return y;
	}

	/**
	 * Accessor for the LinearAcceleration Z component value.
	 * 
	 * @return LinearAcceleration Z component value.
	 */
	public float getZ() {
		return z;
	}
	
	@Override
	public boolean getPrintableString(StringBuilder printable_string) {
		return false;
	}
	
	@Override
	public boolean getContainedQuantities(ArrayList<IQuantity> quantities) {
		quantities.add(new Scalar(x));
		quantities.add(new Scalar(y));
		quantities.add(new Scalar(z));
		return true;
	}
	
	@Override
	public boolean getContainedQuantityNames(ArrayList<String> quantity_names) {
		quantity_names.add("X");
		quantity_names.add("Y");
		quantity_names.add("Z");
		return true;
	}
	
	@Override
	public void copy(LinearAcceleration t) {
		this.x = t.x;
		this.y = t.y;
		this.z = t.z;
	}
	
	@Override
	public LinearAcceleration instantiate_copy() {
		return new LinearAcceleration(this);
	}
	
	@Override
	public void interpolate(LinearAcceleration to, double time_ratio, LinearAcceleration out) {
		Scalar s_from = new Scalar();
		Scalar s_to = new Scalar();
		Scalar s_interp = new Scalar();
		
		s_from.set(this.x);
		s_to.set(to.x);
		s_from.interpolate(s_to, time_ratio, s_interp);
		float new_x = s_interp.get();
		
		s_from.set(this.y);
		s_to.set(to.y);
		s_from.interpolate(s_to, time_ratio, s_interp);
		float new_y = s_interp.get();

		s_from.set(this.z);
		s_to.set(to.z);
		s_from.interpolate(s_to, time_ratio, s_interp);
		float new_z = s_interp.get();
		
		out.set(new_x, new_y, new_z);
	}
}