/*
 * navXSensor.h
 *
 *  Created on: Dec 28, 2016
 *      Author: Scott
 */

#ifndef NAVXSENSOR_H_
#define NAVXSENSOR_H_

#include "sensor/ISensorDataSource.h"
#include "sensor/ISensorInfo.h"
#include "sensor/ISensorDataSubscriber.h"
#include "sensor/SensorDataSourceInfo.h"
#include "time/TimestampedValue.h"
#include "time/TimestampInfo.h"
#include "orientation/Quaternion.h"
#include "RoboRIO.h"
#include "AHRS.h"
#include "ITimestampedDataSubscriber.h"

#include <string>
#include <forward_list>
using namespace std;

class navXSensor :
	ISensorDataSource,
	ITimestampedDataSubscriber,
	ISensorInfo {
	AHRS& navx_sensor;
	RoboRIO roborio;
	forward_list<SensorDataSourceInfo> sensor_data_source_infos;
	TimestampedValue<Quaternion> curr_data;
	long last_system_timestamp;
	long last_sensor_timestamp;
	string sensor_name;
	forward_list<ISensorDataSubscriber> tsq_subscribers;
	bool navx_callback_registered;
	forward_list<IQuantity*> active_sensor_data_quantities;
	TimestampInfo navx_tsinfo;

public:
	const static int QUANTITY_INDEX_QUATERNION = 0;
	const static int QUANTITY_INDEX_YAW = 1;
	const static int QUANTITY_INDEX_PITCH = 2;
	const static int QUANTITY_INDEX_ROLL = 3;

	navXSensor(AHRS& navx_sensor, string& sensor_name){
		this->navx_sensor = navx_sensor;
		this->curr_data.setValid(true);
		this->sensor_name = sensor_name;
		this->navx_callback_registered = false;
		Timestamp ts(0,Timestamp::TimestampResolution::Millisecond);
		navx_tsinfo = new TimestampInfo(TimestampInfo::Scope::Sensor,
				TimestampInfo::Basis::SinceLastReboot,
				1.0 / Timestamp::MILLISECONDS_PER_SECOND, 		/* Resolution */
				1.0 / Timestamp::MILLISECONDS_PER_SECOND, 		/* Accuracy */
				1.0 / (360 * Timestamp::MILLISECONDS_PER_SECOND),/* Clock Drift - seconds per hour */
				1.0 / Timestamp::MILLISECONDS_PER_SECOND,		/* Average Latency */
				ts);   /* Clock drift/hour */
		this->sensor_data_source_infos.add(new SensorDataSourceInfo("Quaternion",new Quaternion(),
				Quaternion.getUnits()));
		this->sensor_data_source_infos.add(new SensorDataSourceInfo("Yaw", new Scalar(),
				new IUnit[]{new Unit().new Angle().new Degrees()}));
		this->sensor_data_source_infos.add(new SensorDataSourceInfo("Pitch", new Scalar(),
				new IUnit[]{new Unit().new Angle().new Degrees()}));
		this->sensor_data_source_infos.add(new SensorDataSourceInfo("Roll", new Scalar(),
				new IUnit[]{new Unit().new Angle().new Degrees()}));
		SensorDataSourceInfo[] data_source_infos =
			((SensorDataSourceInfo[]) sensor_data_source_infos.toArray(new SensorDataSourceInfo[sensor_data_source_infos.size()]));
		ArrayList<IQuantity> quantity_list = new ArrayList<IQuantity>();
		SensorDataSourceInfo.getQuantityArray(data_source_infos,quantity_list);
		active_sensor_data_quantities = (IQuantity[])quantity_list.toArray(new IQuantity[quantity_list.size()]);
	}


	bool subscribe(ISensorDataSubscriber* subscriber) {
		synchronized(tsq_subscribers) {
			if ( tsq_subscribers.contains(subscriber)) {
				return false;
			}
			if ( !navx_callback_registered) {
				navx_callback_registered = this->navx_sensor.registerCallback(this, null);
			}
			if (navx_callback_registered) {
				tsq_subscribers.add(subscriber);
				return true;
			} else {
				return false;
			}
		}
	}


	bool unsubscribe(ISensorDataSubscriber* subscriber) {
		bool unsubscribed = false;
		synchronized(tsq_subscribers) {
			unsubscribed = tsq_subscribers.remove(subscriber);
			if ( tsq_subscribers.size() == 0 ) {
				if ( navx_callback_registered) {
					navx_callback_registered = this->navx_sensor.deregisterCallback(this);
				}
			}
		}
		return unsubscribed;
	}


	void timestampedDataReceived(long system_timestamp, long sensor_timestamp, AHRSUpdateBase data, Object context) {
		synchronized(tsq_subscribers) {
			curr_data.getValue().set(data.quat_w, data.quat_x, data.quat_y, data.quat_z);
			curr_data.setTimestamp(sensor_timestamp);
			for ( ISensorDataSubscriber subscriber : tsq_subscribers) {
				Timestamp t = new Timestamp();
				roborio.getProcessorTimestamp(t);
				subscriber.publish(active_sensor_data_quantities, t);
			}
		}
	}


	IProcessorInfo getHostProcessorInfo() {
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
		infos.addAll(sensor_data_source_infos);
	}

	bool getCurrent(IQuantity[] quantities, Timestamp ts) {
		if (this->navx_sensor.isConnected()) {
			((Quaternion)quantities[0]).set(
				this->navx_sensor.getQuaternionW(),
				this->navx_sensor.getQuaternionX(),
				this->navx_sensor.getDisplacementY(),
				this->navx_sensor.getQuaternionZ());
			ts.setTimestamp(this->navx_sensor.getLastSensorTimestamp());
			return true;
		} else {
			return false;
		}
	}


	ISensorDataSource getSensorDataSource() {
		return this;
	}


	TimestampInfo getSensorTimestampInfo() {
		return navx_tsinfo;
	}


	bool reset(int index) {
		if ( index == QUANTITY_INDEX_YAW) {
			navx_sensor.zeroYaw();
			return true;
		}
		return false;
	}
};

#endif /* NAVXSENSOR_H_ */
