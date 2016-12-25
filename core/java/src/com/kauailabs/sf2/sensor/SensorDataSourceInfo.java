package com.kauailabs.sf2.sensor;

import java.util.ArrayList;

import com.kauailabs.sf2.quantity.IQuantity;
import com.kauailabs.sf2.units.Unit.IUnit;

public class SensorDataSourceInfo {
	String name;
	IUnit[] units;	
	double update_rate_hz;
	IQuantity value;
	
	public SensorDataSourceInfo( String name, IQuantity value, IUnit[] units) {
		this.name = name;
		this.value = value;
		this.units = units;
	}
	
	/**
	 * Returns the name of this sensor data.  This name must be unique
	 * among all SensorDataInfos produced by any sensor.
	 */
	public String getName() { return name; }
	/**
	 * Returns the class object corresponding to the sensor quantity data type.
	 * @return
	 */
	public IQuantity getQuantity() { return value; }
	/**
	 * Returns an array of IUnit objects describing the units of the sensor
	 * quantity data type.  If a complex quantity data type (which implements
	 * the IQuantityContainer interface, multiple IUnit objects may be returned.  
	 * The count and order of the IUnit corresponds to the IQuantity objects
	 * contained within the quantity data type.  
	 * @return
	 */
	public IUnit[] getQuantityUnits() { return units; }

	static public void getQuantityArray(SensorDataSourceInfo[] data_source, ArrayList<IQuantity> quantity_list) {
		for ( SensorDataSourceInfo data_source_info : data_source ) {
			try {
				quantity_list.add(data_source_info.getQuantity().getClass().newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				return;
			}
		}
	}
}
