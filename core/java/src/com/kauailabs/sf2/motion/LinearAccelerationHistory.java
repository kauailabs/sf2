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

package com.kauailabs.sf2.motion;

import java.util.ArrayList;

import com.kauailabs.sf2.math.Matrix;
import com.kauailabs.sf2.quantity.IQuantity;
import com.kauailabs.sf2.quantity.Scalar;
import com.kauailabs.sf2.quantity.Topic;
import com.kauailabs.sf2.sensor.ISensorDataSource;
import com.kauailabs.sf2.sensor.ISensorDataSubscriber;
import com.kauailabs.sf2.sensor.ISensorInfo;
import com.kauailabs.sf2.sensor.SensorDataSourceInfo;
import com.kauailabs.sf2.time.ThreadsafeInterpolatingTimeHistory;
import com.kauailabs.sf2.time.Timestamp;
import com.kauailabs.sf2.time.TimestampedValue;

/**
 * The LinearAccelerationHistory class implements a timestamped history of 
 * Linear Acceleration data (e.g., from an IMU).  The LinearAccelerationHistory 
 * is populated by data from a "timestamped linear acceleration" sensor, such 
 * as the navX-MXP.
 * 
 * The LinearAccelerationHistory buffers the linear acceleration data received 
 * over the most current time period between "now" and the size of the time 
 * history, and provides methods to retrieve orientation data in the form of
 * TimestampedLinearAcceleration objects. These objects can be looked up based 
 * upon a timestamp; if an exact match is found the object is returned direction;
 * otherwise if TimestampedLinearAcceleration objects exist for the times before 
 * and after the requested timestamp, a new TimestampedLinearAcceleration object 
 * is created via interpolation.
 * 
 * @author Scott
 */
public class LinearAccelerationHistory implements ISensorDataSubscriber {

	ISensorDataSource linear_accel_sensor;
	ThreadsafeInterpolatingTimeHistory<TimestampedValue<LinearAcceleration>> history;
	Scalar temp_s;
	int linear_acceleration_quantity_index;
	int timestamp_quantity_index;
	TimestampedValue<LinearAcceleration> temp_tsq;
	Timestamp system_timestamp;
	
	public final int MAX_HISTORY_LENGTH_NUM_SAMPLES = 1000;

	/**
	 * Constructs a LinearAccelerationHistory object with a specified size. The
	 * LinearAccelerationHistory registers for incoming data using the provided
	 * ISensorInfo object.
	 * 
	 * @param quat_sensor
	 *            - the sensor to acquire TimestampedLinearAcceleration objects from.
	 * @param history_length_seconds
	 *            - the length of the history, in seconds. The actual length of
	 *            the history in number of objects is calculated internally by 
	 *            accessing the sensor's current update rate. <i>Note: if the 
	 *            sensor update rate is changed, after this constructor is invoked, 
	 *            the length of the history may no longer accurately reflect the 
	 *            originally-configured length.</i>
	 * @param linear_accel_sensor
	 *            - the sensor to use as the source of TimestampedLinearAccelerations
	 *            contained in the History
	 * @param history_length_seconds
	 *            - the number of seconds the history will represent. This value
	 *            may not be larger than @value
	 *            #MAX_HISTORY_LENGTH_NUM_SAMPLES seconds.
	 */
	public LinearAccelerationHistory(ISensorInfo linear_accel_sensor, int history_length_num_samples) {

		this.linear_accel_sensor = linear_accel_sensor.getSensorDataSource();

		int index = 0;
		linear_acceleration_quantity_index = -1;
		timestamp_quantity_index = -1;
		ArrayList<SensorDataSourceInfo> sensor_data_source_infos = new ArrayList<SensorDataSourceInfo>();
		linear_accel_sensor.getSensorDataSource().getSensorDataSourceInfos(sensor_data_source_infos);
		for (SensorDataSourceInfo item : sensor_data_source_infos) {
			if (item.getName().equalsIgnoreCase(Topic.LINEAR_ACCELERATION)) {
				linear_acceleration_quantity_index = index;
			}
			if (item.getName().equalsIgnoreCase(Topic.TIMESTAMP)) {
				timestamp_quantity_index = index;
			}
			index++;
		}

		if (linear_acceleration_quantity_index == -1) {
			throw new IllegalArgumentException("The provided ISensorInfo (linear acceleration ensor) object"
					+ "must contain a SensorDataSourceInfo object named '" + Topic.LINEAR_ACCELERATION + "'.");
		}

		if (timestamp_quantity_index == -1) {
			throw new IllegalArgumentException("The provided ISensorInfo (linear acceleration ensor) object"
					+ "must contain a SensorDataSourceInfo object named '" + Topic.TIMESTAMP + "'.");
		}

		if (history_length_num_samples > MAX_HISTORY_LENGTH_NUM_SAMPLES) {
			history_length_num_samples = MAX_HISTORY_LENGTH_NUM_SAMPLES;
		}
		LinearAcceleration default_linear_accel = new LinearAcceleration();
		TimestampedValue<LinearAcceleration> default_ts_linear_accel = 
				new TimestampedValue<LinearAcceleration>(default_linear_accel);
		this.history = new ThreadsafeInterpolatingTimeHistory<TimestampedValue<LinearAcceleration>>(default_ts_linear_accel,
				history_length_num_samples, linear_accel_sensor.getSensorTimestampInfo(),
				sensor_data_source_infos.get(linear_acceleration_quantity_index).getName(),
				sensor_data_source_infos.get(linear_acceleration_quantity_index).getQuantityUnits());

		this.linear_accel_sensor.subscribe(this);

		temp_s = new Scalar();
		
		temp_tsq = new TimestampedValue<LinearAcceleration>(new LinearAcceleration());
		
		system_timestamp = new Timestamp();
	}

	/**
	 * Reset the OrientationHistory, clearing all existing entries.
	 * 
	 * @param quat_curr
	 */
	public void reset(TimestampedValue<LinearAcceleration> linear_accel_curr) {
		history.reset();
	}

	/**
	 * Retrieves the most recently added Quaternion.
	 * 
	 * @return
	 */
	public boolean getCurrentQuaternion(TimestampedValue<LinearAcceleration> out) {
		return history.getMostRecent(out);
	}

	/**
	 * Retrieves the TimestampedLinearAcceleration at the specified sensor timestamp. If
	 * an exact timestamp match occurs, a TimestampedLinearAcceleration representing the
	 * actual (measured) data is returned; otherwise a new interpolated
	 * TimestampedLinearAcceleration will be estimated, using the nearest
	 * preceding/following TimestampedLinearAcceleration and the requested timestamp's
	 * ratio of time between them as its basis. If no exact match could be found
	 * or interpolated value estimated, null is returned.
	 * 
	 * @param requested_timestamp
	 *            - sensor timestamp to retrieve
	 * @return TimestampedLinearAcceleration at requested timestamp, or null.
	 */
	public boolean getLinearAccelerationAtTime(long requested_timestamp, TimestampedValue<LinearAcceleration> out) {
		return history.get(requested_timestamp, out);
	}

	@Override
	public void publish(IQuantity[] curr_values, Timestamp sys_timestamp) {
		Timestamp sensor_timestamp;
		if ( timestamp_quantity_index != -1 ) {
			sensor_timestamp = ((Timestamp)curr_values[timestamp_quantity_index]);
		} else {
			sensor_timestamp = sys_timestamp;
		}
		LinearAcceleration value = ((LinearAcceleration) curr_values[linear_acceleration_quantity_index]);
		temp_tsq.set(value,  sensor_timestamp.getMilliseconds());
		history.add(temp_tsq);
	}

	public boolean writeToDirectory(String directory_path) {
		return history.writeToDirectory(directory_path);
	}
	
	public boolean writeToFile(String file_path){
		return history.writeToFile(file_path);
	}
	
	/**
	 * Calculates the linear acceleration covariances (in units of m/s^2), based
	 * the contents of the LinearAccelerationHistory.
	 * <p>
	 * 
	 * @param linear_acceleration_matrix: Must have a dimensionality of 3.
	 * @return true if covariance was successfully calculated; false otherwise.
	 */
	public boolean calculate_covariance(Matrix linear_acceleration_matrix) {
		
		final int NUM_DIMENSIONS = 3;
		
		if(linear_acceleration_matrix.get_num_dimensions() != NUM_DIMENSIONS) return false;

		ThreadsafeInterpolatingTimeHistory<TimestampedValue<LinearAcceleration>> snapshot = history.create_snapshot();

		int num_samples = snapshot.getValidSampleCount();
		if(num_samples < 1) return false;		

		float accel_total[] = new float[NUM_DIMENSIONS];
		float accel_last[] = new float[NUM_DIMENSIONS];
		float accel_avg[] = new float[NUM_DIMENSIONS];		
		float accel[][] = new float[NUM_DIMENSIONS][num_samples];
		
		Object pos = snapshot.getFirstPosition();
		TimestampedValue<LinearAcceleration> la = snapshot.getNext(pos);
		int i = 0;
		while ( la != null) {
			accel[0][i] = la.getValue().x;
			accel[1][i] = la.getValue().y;
			accel[2][i] = la.getValue().z;
			for ( int x = 0; x < NUM_DIMENSIONS; x++) {
				accel_last[x] = accel[x][i];
				accel_total[x] += accel_last[x];
			}
			i++;
			la = snapshot.getNext(pos);
		}
		/* Calculate Averages */
		for ( int x = 0; x < NUM_DIMENSIONS; x++) {
			accel_avg[x] = accel_total[x] / num_samples;
		}
		linear_acceleration_matrix.calculate_covariance(accel, accel_avg, num_samples);
		return true;
	}	
}
