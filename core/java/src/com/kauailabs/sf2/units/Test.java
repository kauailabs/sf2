package com.kauailabs.sf2.units;

import com.kauailabs.sf2.quantity.Scalar;
import com.kauailabs.sf2.units.Unit.IUnit;

public class Test {
	Scalar displacement_x;
	Scalar deg_per_sec;
	Scalar accel_x;
	Scalar jerk_x;
	
	public void test() {
		displacement_x = new Scalar(10);
		deg_per_sec = new Scalar(3.5f);
		accel_x = new Scalar(47);
		jerk_x = new Scalar(.004f);
		RateMetersPerSecond units = new RateMetersPerSecond();
		IUnit iunit = units;
		String name = iunit.getName();
		System.out.println("M/S Name:  " + name);
		String abbrev = iunit.getAbbreviation();
		System.out.println("M/S Abbreviation:  " + abbrev);
		if (iunit instanceof Rate) {
			IUnit numerator = (IUnit) ((Rate) iunit).getNumeratorUnit();
			System.out.println("Rate numerator is an IUnit:  " + numerator.getName());
			IUnit denominator = (IUnit) ((Rate) iunit).getDenominatorUnit();
			System.out.println("Rate denominator is an IUnit:  " + denominator.getName());
		}
		double value = deg_per_sec.get();
		System.out.println("Degress/sec retrieved value:  " + Double.toString(value));
		IUnit meter_units = new Unit().new Angle().new Degrees();
		String meter_unit_name = meter_units.getName();
		System.out.println("M Name:  " + meter_unit_name);
		String meter_unit_abbrev = meter_units.getAbbreviation();
		System.out.println("M Abbreviation:  " + meter_unit_abbrev);
		String s = Double.toString(displacement_x.get());
		System.out.println("Dispacement:  " + s);
	}
}
