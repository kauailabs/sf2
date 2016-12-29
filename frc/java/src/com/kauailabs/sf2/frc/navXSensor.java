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

package com.kauailabs.sf2.frc;

import com.kauailabs.navx.frc.ITimestampedDataSubscriber;

import java.util.ArrayList;

import com.kauailabs.navx.AHRSProtocol.AHRSUpdateBase;
import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.sf2.orientation.Quaternion;
import com.kauailabs.sf2.quantity.IQuantity;
import com.kauailabs.sf2.quantity.Scalar;
import com.kauailabs.sf2.sensor.IProcessorInfo;
import com.kauailabs.sf2.sensor.ISensorDataSource;
import com.kauailabs.sf2.sensor.ISensorDataSubscriber;
import com.kauailabs.sf2.sensor.ISensorInfo;

import com.kauailabs.sf2.sensor.SensorDataSourceInfo;
import com.kauailabs.sf2.time.Timestamp;
import com.kauailabs.sf2.time.TimestampInfo;
import com.kauailabs.sf2.time.TimestampedValue;
import com.kauailabs.sf2.units.Unit;
import com.kauailabs.sf2.units.Unit.IUnit;

public class navXSensor implements ISensorDataSource, ITimestampedDataSubscriber, ISensorInfo {
	AHRS navx_sensor;
	RoboRIO roborio;
	ArrayList<SensorDataSourceInfo> sensor_data_source_infos;
	TimestampedValue<Quaternion> curr_data;
	long last_system_timestamp;
	long last_sensor_timestamp;
	String sensor_name;
	ArrayList<ISensorDataSubscriber> tsq_subscribers;
	boolean navx_callback_registered;
	IQuantity[] active_sensor_data_quantities;
	TimestampInfo navx_tsinfo;

	final static int QUANTITY_INDEX_QUATERNION = 0;
	final static int QUANTITY_INDEX_YAW = 1;
	final static int QUANTITY_INDEX_PITCH = 2;
	final static int QUANTITY_INDEX_ROLL = 3;

	public navXSensor(AHRS navx_sensor, String sensor_name) {
		this.navx_sensor = navx_sensor;
		this.curr_data = new TimestampedValue<Quaternion>(new Quaternion());
		this.curr_data.setValid(true);
		this.roborio = new RoboRIO();
		this.sensor_name = sensor_name;
		this.sensor_data_source_infos = new ArrayList<SensorDataSourceInfo>();
		this.navx_callback_registered = false;
		Timestamp ts = new Timestamp(0, Timestamp.TimestampResolution.Millisecond);
		navx_tsinfo = new TimestampInfo(TimestampInfo.Scope.Sensor, TimestampInfo.Basis.SinceLastReboot,
				1.0 / Timestamp.MILLISECONDS_PER_SECOND, /* Resolution */
				1.0 / Timestamp.MILLISECONDS_PER_SECOND, /* Accuracy */
				1.0 / (360 * Timestamp.MILLISECONDS_PER_SECOND), /*
																	 * Clock Drift
																	 * - seconds
																	 * per hour
																	 */
				1.0 / Timestamp.MILLISECONDS_PER_SECOND, /* Average Latency */
				ts); /* Clock drift/hour */
		this.sensor_data_source_infos
				.add(new SensorDataSourceInfo("Quaternion", new Quaternion(), Quaternion.getUnits()));
		this.sensor_data_source_infos.add(
				new SensorDataSourceInfo("Yaw", new Scalar(), new IUnit[] { new Unit().new Angle().new Degrees() }));
		this.sensor_data_source_infos.add(
				new SensorDataSourceInfo("Pitch", new Scalar(), new IUnit[] { new Unit().new Angle().new Degrees() }));
		this.sensor_data_source_infos.add(
				new SensorDataSourceInfo("Roll", new Scalar(), new IUnit[] { new Unit().new Angle().new Degrees() }));
		SensorDataSourceInfo[] data_source_infos = ((SensorDataSourceInfo[]) sensor_data_source_infos
				.toArray(new SensorDataSourceInfo[sensor_data_source_infos.size()]));
		ArrayList<IQuantity> quantity_list = new ArrayList<IQuantity>();
		SensorDataSourceInfo.getQuantityArray(data_source_infos, quantity_list);
		active_sensor_data_quantities = (IQuantity[]) quantity_list.toArray(new IQuantity[quantity_list.size()]);
	}

	@Override
	public boolean subscribe(ISensorDataSubscriber subscriber) {
		synchronized (tsq_subscribers) {
			if (tsq_subscribers.contains(subscriber)) {
				return false;
			}
			if (!navx_callback_registered) {
				navx_callback_registered = this.navx_sensor.registerCallback(this, null);
			}
			if (navx_callback_registered) {
				tsq_subscribers.add(subscriber);
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean unsubscribe(ISensorDataSubscriber subscriber) {
		boolean unsubscribed = false;
		synchronized (tsq_subscribers) {
			unsubscribed = tsq_subscribers.remove(subscriber);
			if (tsq_subscribers.size() == 0) {
				if (navx_callback_registered) {
					navx_callback_registered = this.navx_sensor.deregisterCallback(this);
				}
			}
		}
		return unsubscribed;
	}

	@Override
	public void timestampedDataReceived(long system_timestamp, long sensor_timestamp, AHRSUpdateBase data,
			Object context) {
		synchronized (tsq_subscribers) {
			curr_data.getValue().set(data.quat_w, data.quat_x, data.quat_y, data.quat_z);
			curr_data.setTimestamp(sensor_timestamp);
			for (ISensorDataSubscriber subscriber : tsq_subscribers) {
				Timestamp t = new Timestamp();
				roborio.getProcessorTimestamp(t);
				subscriber.publish(active_sensor_data_quantities, t);
			}
		}
	}

	@Override
	public IProcessorInfo getHostProcessorInfo() {
		return roborio;
	}

	@Override
	public String getMake() {
		return "Kauai Labs";
	}

	@Override
	public String getModel() {
		return "navX-MXP";
	}

	@Override
	public String getName() {
		return sensor_name;
	}

	@Override
	public void getSensorDataSourceInfos(ArrayList<SensorDataSourceInfo> infos) {
		infos.addAll(sensor_data_source_infos);
	}

	@Override
	public boolean getCurrent(IQuantity[] quantities, Timestamp ts) {
		if (this.navx_sensor.isConnected()) {
			((Quaternion) quantities[0]).set(this.navx_sensor.getQuaternionW(), this.navx_sensor.getQuaternionX(),
					this.navx_sensor.getDisplacementY(), this.navx_sensor.getQuaternionZ());
			ts.setTimestamp(this.navx_sensor.getLastSensorTimestamp());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public ISensorDataSource getSensorDataSource() {
		return this;
	}

	@Override
	public TimestampInfo getSensorTimestampInfo() {
		return navx_tsinfo;
	}

	@Override
	public boolean reset(int index) {
		if (index == QUANTITY_INDEX_YAW) {
			navx_sensor.zeroYaw();
			return true;
		}
		return false;
	}
}
