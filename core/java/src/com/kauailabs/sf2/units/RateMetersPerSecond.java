package com.kauailabs.sf2.units;

public class RateMetersPerSecond extends Rate {
	public RateMetersPerSecond() {		
		super(new Unit().new Distance().new Meters(), new Unit().new Time().new Seconds());
	}
}
