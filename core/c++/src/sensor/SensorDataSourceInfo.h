/* ============================================
 SF2 source code is placed under the MIT license
 Copyright (c) 2017 Kauai Labs

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ===============================================
 */

#ifndef SRC_SENSOR_SENSORDATASOURCEINFO_H_
#define SRC_SENSOR_SENSORDATASOURCEINFO_H_

#include <string>
#include <vector>
using namespace std;

#include "quantity/IQuantity.h"
#include "unit/Unit.h"
#include "orientation/Quaternion.h"
#include "quantity/Scalar.h"

class SensorDataSourceInfo {
	string name;
	vector<IUnit*> units;
	IQuantity& value;
public:
	SensorDataSourceInfo(string name, IQuantity& value,
		vector<IUnit *>& units) :
		value(value)
	{
		this->name = name;
		this->units = units;
	}

	SensorDataSourceInfo(string name, IQuantity& value, IUnit&units) :
		value(value)
	{
		this->name = name;
		this->units.push_back(&units);
	}

	/**
	 * Returns the name of this sensor data.  This name must be unique
	 * among all SensorDataInfos produced by any sensor.
	 */
	string getName() {
		return name;
	}
	/**
	 * Returns the class object corresponding to the sensor quantity data type.
	 * @return
	 */
	IQuantity& getQuantity() {
		return value;
	}
	/**
	 * Returns an array of IUnit objects describing the units of the sensor
	 * quantity data type.  If a complex quantity data type (which implements
	 * the IQuantityContainer interface, multiple IUnit objects may be returned.
	 * The count and order of the IUnit corresponds to the IQuantity objects
	 * contained within the quantity data type.
	 * @return
	 */
	const vector<IUnit *>& getQuantityUnits() {
		return units;
	}

	static void getQuantityArray(
			vector<SensorDataSourceInfo*>& data_source,
			vector<IQuantity*>& quantity_list) {
		for (SensorDataSourceInfo *p_data_source_info : data_source) {
			const std::type_info& qti = typeid(p_data_source_info->getQuantity());
			string quantity_name = qti.name();
			IQuantity *p_new_quantity = NULL;
			if (quantity_name.compare("Quaternion")) {
				p_new_quantity = new Quaternion((Quaternion &)p_data_source_info->value);
			} else if (quantity_name.compare("Scalar")) {
				p_new_quantity = new Scalar((Scalar &)p_data_source_info->value);
			} else if (quantity_name.compare("Timestamp")) {
				p_new_quantity = new Timestamp((Timestamp&)p_data_source_info->value);
			}
			quantity_list.push_back(p_new_quantity);
		}
	}
};

#endif /* SRC_SENSOR_SENSORDATASOURCEINFO_H_ */
