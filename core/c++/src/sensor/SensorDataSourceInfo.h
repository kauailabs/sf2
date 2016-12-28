/*
 * SensorDataSourceInfo.h
 *
 *  Created on: Dec 28, 2016
 *      Author: Scott
 */

#ifndef SRC_SENSOR_SENSORDATASOURCEINFO_H_
#define SRC_SENSOR_SENSORDATASOURCEINFO_H_

#include <string>
using namespace std;

class SensorDataSourceInfo {
	string name;
	forward_list<Unit::IUnit*> units;
	double update_rate_hz;
	IQuantity& value;
public:
	SensorDataSourceInfo( string& name, IQuantity& value, forward_list<Unit::IUnit *>& units) {
		this->name = name;
		this->value = value;
		this->units = units;
	}

	/**
	 * Returns the name of this sensor data.  This name must be unique
	 * among all SensorDataInfos produced by any sensor.
	 */
	string getName() { return name; }
	/**
	 * Returns the class object corresponding to the sensor quantity data type.
	 * @return
	 */
	IQuantity getQuantity() { return value; }
	/**
	 * Returns an array of IUnit objects describing the units of the sensor
	 * quantity data type.  If a complex quantity data type (which implements
	 * the IQuantityContainer interface, multiple IUnit objects may be returned.
	 * The count and order of the IUnit corresponds to the IQuantity objects
	 * contained within the quantity data type.
	 * @return
	 */
	const forward_list<Unit::IUnit *>& getQuantityUnits() { return units; }

	static void getQuantityArray(forward_list<SensorDataSourceInfo&>& data_source, forward_list<IQuantity*>& quantity_list) {
		for ( SensorDataSourceInfo data_source_info : data_source ) {
			std::type_info qti = typeid(data_source_info.getQuantity());
			string quantity_name = qti.name();
			IQuantity *p_new_quantity = NULL;
			if (quantity_name.compare("Quaternion")) {
				p_new_quantity = new Quaternion();
			} else if ( quantity_name.compare("Scalar")){
				p_new_quantity = new Scalar();
			}
			quantity_list.insert_after(quantity_list.end(),p_new_quantity);
		}
	}
};

#endif /* SRC_SENSOR_SENSORDATASOURCEINFO_H_ */
