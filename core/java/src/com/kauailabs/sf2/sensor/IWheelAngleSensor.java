package com.kauailabs.sf2.sensor;

import com.kauailabs.sf2.quantity.Scalar;

public interface IWheelAngleSensor<T extends Scalar> {
	boolean getCurrent(T t);
}
