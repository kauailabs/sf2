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
#ifndef SRC_ORIENTATION_ORIENTATIONHISTORY_H_
#define SRC_ORIENTATION_ORIENTATIONHISTORY_H_

#include <string>
#include <cmath>
using namespace std;

/**
 * The OrientationHistory class implements a timestamped history of
 * orientation data (e.g., from an IMU).  The OrientationHistory is populated
 * by data from a "timestamped quaternion" sensor, such as the
 * navX-MXP.
 *
 * The OrientationHistory buffers the orientation data received over the
 * most current time period between "now" and the size of the time history,
 * and provides methods to retrieve orientation data in the form of
 * TimestampedQuaternion objects.  These objects can be looked up based
 * upon a timestamp; if an exact match is found the object is returned
 * direction; otherwise if TimestampedQuaternion objects exist for the
 * times before and after the requested timestamp, a new TimestampedQuaterion
 * object is created via interpolation.
 * @author Scott
 */
class OrientationHistory: ISensorDataSubscriber {
	ISensorDataSource& quat_sensor;
	SensorDataSourceInfo& quat_data_source_info;
	ThreadsafeInterpolatingTimeHistory<TimestampedValue<Quaternion>> orientation_history;
	Scalar temp;
	int quaternion_quantity_index;

public:

	const int MAX_ORIENTATION_HISTORY_LENGTH_NUM_SAMPLES = 1000;

	/**
	 * Constructs an OrientationHistory object with a specified size.  The OrientationHistory
	 * registers for incoming data using the provided ITimestampedQuaternionSensor object.
	 * @param quat_sensor - the sensor to acquire TimestampedQuaternion objects from.
	 * @param history_length_seconds - the length of the OrientationHistory, in seconds.
	 * The actual length of the OrientationHistory in number of objects is calculated
	 * internally by accessing the sensor's current update rate.
	 * <i>Note:  if the sensor update rate is changed, after this constructor is invoked,
	 * the length of the history may no longer accurately reflect the originally-configured length.</i>
	 * @param quat_sensor - the sensor to use as the source of TimestampedQuaternions contained in the
	 * Orientation History
	 * @param history_length_seconds - the number of seconds the history will represent.  This value
	 * may not be larger than @value #MAX_ORIENTATION_HISTORY_IN_SECONDS seconds.
	 */
	OrientationHistory(ISensorInfo& quat_sensor,
			int history_length_num_samples) {

		this->quat_sensor = quat_sensor.getSensorDataSource();

		int index = 0;
		quaternion_quantity_index = -1;
		forward_list<SensorDataSourceInfo *> sensor_data_source_infos;
		quat_sensor.getSensorDataSource().getSensorDataSourceInfos(
				sensor_data_source_infos);
		for (SensorDataSourceInfo item : sensor_data_source_infos) {
			if (item.getName().compare("Quaternion") == 0) {
				quaternion_quantity_index = index;
				quat_data_source_info = item;
				break;
			}
			index++;
		}

		if (quaternion_quantity_index == -1) {
			printf(
					"The provided ISensorInfo (quat_sensor) object"
							+ "must contain a SensorDataSourceInfo object named 'Quaternion'.");
			return;
		}

		if (history_length_num_samples
				> MAX_ORIENTATION_HISTORY_LENGTH_NUM_SAMPLES) {
			history_length_num_samples =
					MAX_ORIENTATION_HISTORY_LENGTH_NUM_SAMPLES;
		}
		Quaternion default_quat = new Quaternion();
		TimestampedValue<Quaternion> default_ts_quat = new TimestampedValue<
				Quaternion>(default_quat);
		this->orientation_history = new ThreadsafeInterpolatingTimeHistory<
				TimestampedValue<Quaternion>>(default_ts_quat,
				history_length_num_samples,
				quat_sensor.getSensorTimestampInfo(),
				quat_data_source_info.getName(),
				quat_data_source_info.getQuantityUnits());

		this->quat_sensor.subscribe(this);

		temp = new Scalar();
	}

	virtual ~OrientationHistory() {
	}

	/**
	 * Reset the OrientationHistory, clearing all existing entries.
	 * @param quat_curr
	 */
	void reset(TimestampedValue<Quaternion> quat_curr) {
		orientation_history.reset();
	}

	/**
	 * Retrieves the most recently added Quaternion.
	 * @return
	 */
	bool getCurrentQuaternion(TimestampedValue<Quaternion> out) {
		return orientation_history.getMostRecent(out);
	}

	/**
	 * Retrieves the TimestampedQuaterion at the specified sensor timestamp.  If
	 * an exact timestamp match occurs, a TimestampedQuaternion representing the
	 * actual (measured) data is returned; otherwise a new interpolated TimestampedQuaternion
	 * will be estimated, using the nearest preceding/following TimestampedQuaternion
	 * and the requested timestamp's ratio of time between them as its basis.  If
	 * no exact match could be found or interpolated value estimated, null is returned.
	 * @param requested_timestamp - sensor timestamp to retrieve
	 * @return TimestampedQuaternion at requested timestamp, or null.
	 */
	bool getQuaternionAtTime(long requested_timestamp,
			TimestampedValue<Quaternion> out) {
		return orientation_history.get(requested_timestamp, out);
	}

	/**
	 * Retrieves the yaw angle in degrees at the specified sensor timestamp.
	 * <p>Note that this value may be interpolated if a sample at the requested time
	 * is not available.
	 * @param requested_timestamp
	 * @return Yaw angle (in degrees, range -180 to 180) at the requested timestamp.
	 * If a yaw angle at the specified timestamp could not be found/interpolated, the
	 * value INVALID_ANGLE (NaN) will be returned.
	 */
	float getYawDegreesAtTime(long requested_timestamp) {
		TimestampedValue<Quaternion> match = new TimestampedValue<Quaternion>();
		if (getQuaternionAtTime(requested_timestamp, match)) {
			match.getValue().getYawRadians(temp);
			return temp.get() * Unit::Angle::Degrees::RADIANS_TO_DEGREES;
		} else {
			return NAN;
		}
	}

	/**
	 * Retrieves the pitch angle in degrees at the specified sensor timestamp.
	 * <p>Note that this value may be interpolated if a sample at the requested time
	 * is not available.
	 * @param requested_timestamp
	 * @return Pitch angle (in degrees, range -180 to 180) at the requested timestamp.
	 * If a pitch angle at the specified timestamp could not be found/interpolated, the
	 * value INVALID_ANGLE (NaN) will be returned.
	 */
	float getPitchDegreesAtTime(long requested_timestamp) {
		TimestampedValue<Quaternion> match = new TimestampedValue<Quaternion>();
		if (getQuaternionAtTime(requested_timestamp, match)) {
			match.getValue().getPitch(temp);
			return temp.get() * Unit::Angle::Degrees::RADIANS_TO_DEGREES;
		} else {
			return NAN;
		}
	}

	/**
	 * Retrieves the roll angle in degrees at the specified sensor timestamp.
	 * <p>Note that this value may be interpolated if a sample at the requested time
	 * is not available.
	 * @param requested_timestamp
	 * @return Roll angle (in degrees, range -180 to 180) at the requested timestamp.
	 * If a roll angle at the specified timestamp could not be found/interpolated, the
	 * value INVALID_ANGLE (NaN) will be returned.
	 */
	float getRollDegreesAtTime(long requested_timestamp) {
		TimestampedValue<Quaternion> match = new TimestampedValue<Quaternion>();
		if (getQuaternionAtTime(requested_timestamp, match)) {
			match.getValue().getRoll(temp);
			return temp.get() * Unit::Angle::Degrees::RADIANS_TO_DEGREES;
		} else {
			return NAN;
		}
	}

	virtual void publish(forward_list<IQuantity&> curr_values,
			Timestamp& timestamp) {
		Quaternion q = ((Quaternion) curr_values[quaternion_quantity_index]);
		TimestampedValue<Quaternion> tsq = new TimestampedValue<Quaternion>(q);
		tsq.setTimestamp(timestamp.getMilliseconds());
		orientation_history.add(tsq);
	}

	bool writeToDirectory(string& directory_path) {
		return orientation_history.writeToDiskInDirectory(directory_path);
	}
};

#endif /* SRC_ORIENTATION_ORIENTATIONHISTORY_H_ */
