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
#ifndef NAVXSENSOR_H_
#define NAVXSENSOR_H_

#include "sensor/ISensorDataSource.h"
#include "sensor/ISensorInfo.h"
#include "sensor/ISensorDataSubscriber.h"
#include "sensor/SensorDataSourceInfo.h"
#include "sensor/IProcessorInfo.h"
#include "time/TimestampedValue.h"
#include "time/TimestampInfo.h"
#include "orientation/Quaternion.h"
#include "quantity/Scalar.h"
#include "RoboRIO.h"
#include "AHRS.h"
#include "ITimestampedDataSubscriber.h"

#include <string>
#include <forward_list>
using namespace std;

class navXSensor: ISensorDataSource, ITimestampedDataSubscriber, ISensorInfo {
	AHRS& navx_sensor;
	RoboRIO roborio;
	forward_list<SensorDataSourceInfo *> sensor_data_source_infos;
	TimestampedValue<Quaternion> curr_data;
	long last_system_timestamp;
	long last_sensor_timestamp;
	string sensor_name;
	forward_list<ISensorDataSubscriber *> tsq_subscribers;
	bool navx_callback_registered;
	forward_list<IQuantity*> active_sensor_data_quantities;
	TimestampInfo navx_tsinfo;
	priority_mutex subscriber_mutex;
	Quaternion quaternion;
	Scalar yaw, pitch, roll;
	Unit::Angle::Degrees degrees;
	Unit::Time::Milliseconds milliseconds;
	Timestamp roborio_timestamp;

public:
	const static int QUANTITY_INDEX_QUATERNION = 0;
	const static int QUANTITY_INDEX_YAW = 1;
	const static int QUANTITY_INDEX_PITCH = 2;
	const static int QUANTITY_INDEX_ROLL = 3;

	navXSensor(AHRS& navx_sensor, string& sensor_name) {
		this->navx_sensor = navx_sensor;
		this->curr_data.setValid(true);
		this->sensor_name = sensor_name;
		this->navx_callback_registered = false;
		long default_timestamp_value = 0;
		Timestamp ts(default_timestamp_value,
				Timestamp::TimestampResolution::Millisecond);
		navx_tsinfo = new TimestampInfo(TimestampInfo::Scope::Sensor,
				TimestampInfo::Basis::SinceLastReboot,
				1.0 / Timestamp::MILLISECONDS_PER_SECOND, /* Resolution */
				1.0 / Timestamp::MILLISECONDS_PER_SECOND, /* Accuracy */
				1.0 / (360 * Timestamp::MILLISECONDS_PER_SECOND),/* Clock Drift - seconds per hour */
				1.0 / Timestamp::MILLISECONDS_PER_SECOND, /* Average Latency */
				ts); /* Clock drift/hour */
		this->sensor_data_source_infos.insert_after(
				this->sensor_data_source_infos.end(),
				new SensorDataSourceInfo("Timestamp", ts, milliseconds));
		forward_list<Unit::IUnit *> quaternion_units;
		Quaternion::getUnits(quaternion_units);
		this->sensor_data_source_infos.insert_after(
				this->sensor_data_source_infos.end(),
				new SensorDataSourceInfo("Timestamp", quaternion,
						quaternion_units));
		this->sensor_data_source_infos.insert_after(
				this->sensor_data_source_infos.end(),
				new SensorDataSourceInfo("Quaternion", quaternion,
						quaternion_units));
		this->sensor_data_source_infos.insert_after(
				this->sensor_data_source_infos.end(),
				new SensorDataSourceInfo("Yaw", yaw, degrees));
		this->sensor_data_source_infos.insert_after(
				this->sensor_data_source_infos.end(),
				new SensorDataSourceInfo("Pitch", pitch, degrees));
		this->sensor_data_source_infos.insert_after(
				this->sensor_data_source_infos.end(),
				new SensorDataSourceInfo("Roll", roll, degrees));
		SensorDataSourceInfo::getQuantityArray(this->sensor_data_source_infos,
				active_sensor_data_quantities);
		last_sensor_timestamp = 0;
		last_system_timestamp = 0;
	}

	~navXSensor() {
		for (auto p_sensor_data_source_info : sensor_data_source_infos) {
			delete p_sensor_data_source_info;
		}
	}

	bool subscribe(ISensorDataSubscriber* subscriber) {
		{
			std::unique_lock<priority_mutex> sync(subscriber_mutex);
			bool existing = false;
			for (auto tsq_subscriber : tsq_subscribers) {
				if (tsq_subscriber == subscriber) {
					existing = true;
					break;
				}
			}
			if (!existing)
				return false;
		}

		if (!navx_callback_registered) {
			navx_callback_registered = this->navx_sensor.RegisterCallback(this,
					NULL);
		}
		if (navx_callback_registered) {
			std::unique_lock<priority_mutex> sync(subscriber_mutex);
			tsq_subscribers.insert_after(tsq_subscribers.end(), subscriber);
			return true;
		}
		return false;
	}

	bool unsubscribe(ISensorDataSubscriber* subscriber) {
		bool unsubscribed = false;
		{
			std::unique_lock<priority_mutex> sync(subscriber_mutex);

			unsubscribed = tsq_subscribers.remove(subscriber);
			if (tsq_subscribers.empty()) {
				if (navx_callback_registered) {
					navx_callback_registered =
							this->navx_sensor.DeregisterCallback(this);
				}
			}
		}
		return unsubscribed;
	}

	void timestampedDataReceived(long system_timestamp, long sensor_timestamp,
			AHRSProtocol::AHRSUpdateBase data, void * context) {
		std::unique_lock<priority_mutex> sync(subscriber_mutex);
		((Timestamp) active_sensor_data_quantities[0]).setTimestamp(sensor_timestamp);
		((Quaternion) active_sensor_data_quantities[1]).set(data.quat_w, data.quat_x, data.quat_y, data.quat_z);
		((Scalar)active_sensor_data_quantities[2]).set(data.yaw);
		((Scalar)active_sensor_data_quantities[3]).set(data.pitch);
		((Scalar)active_sensor_data_quantities[4]).set(data.roll);

		roborio.getProcessorTimestamp(roborio_timestamp);
		for (ISensorDataSubscriber *subscriber : tsq_subscribers) {
			subscriber->publish(active_sensor_data_quantities, roborio_timestamp);
		}
	}

	IProcessorInfo& getHostProcessorInfo() {
		return roborio;
	}

	string getMake() {
		return "Kauai Labs";
	}

	string getModel() {
		return "navX-MXP";
	}

	string getName() {
		return sensor_name;
	}

	void getSensorDataSourceInfos(forward_list<SensorDataSourceInfo*>& infos) {
		for (auto sensor_data_source_info : sensor_data_source_infos) {
			infos.insert_after(infos.end(), sensor_data_source_info);
		}
	}

	bool getCurrent(forward_list<IQuantity&>& quantities, Timestamp& curr_ts) {
		if (this->navx_sensor.IsConnected()) {
			((Timestamp) quantities[0]).setTimestamp(this->navx_sensor.GetLastSensorTimestamp());
			((Quaternion) quantities[1]).set(this->navx_sensor.GetQuaternionW(), this->navx_sensor.GetQuaternionX(),
					this->navx_sensor.GetDisplacementY(), this->navx_sensor.GetQuaternionZ());
			((Scalar)quantities[2]).set(this->navx_sensor.GetYaw());
			((Scalar)quantities[3]).set(this->navx_sensor.GetPitch());
			((Scalar)quantities[4]).set(this->navx_sensor.GetRoll());
			roborio.getProcessorTimestamp(curr_ts);
			return true;
		} else {
			return false;
		}
	}

	ISensorDataSource& getSensorDataSource() {
		return *this;
	}

	TimestampInfo& getSensorTimestampInfo() {
		return navx_tsinfo;
	}

	bool reset(int index) {
		if (index == QUANTITY_INDEX_YAW) {
			navx_sensor.ZeroYaw();
			return true;
		}
		return false;
	}
};

#endif /* NAVXSENSOR_H_ */
