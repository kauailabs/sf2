package com.kauailabs.sf2.units;

import com.kauailabs.sf2.units.Unit.IUnit;

public class Rate implements IUnit {

	IUnit numerator;
	IUnit denominator;
	
	public Rate( IUnit numerator, IUnit denominator ) {
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	@Override
	public String getName() {
		String numerator_name = numerator.getName();
		String denominator_name = denominator.getName();
		if ( denominator_name.charAt(denominator_name.length()-1) == 's') {
			int index = denominator_name.lastIndexOf('s');
			denominator_name = denominator_name.substring(0, index);
		}
		return numerator_name + "/" + denominator_name;
	}

	@Override
	public String getAbbreviation() {
		String numerator_abbrev = numerator.getAbbreviation();
		String denominator_abbrev = denominator.getAbbreviation();
		return numerator_abbrev + "/" + denominator_abbrev;
	}

	public IUnit getNumeratorUnit() {
		return numerator;
	}

	public IUnit getDenominatorUnit() {
		return denominator;
	}
}
