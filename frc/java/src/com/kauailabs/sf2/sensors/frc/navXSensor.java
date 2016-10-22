package com.kauailabs.sf2.sensors.frc;

import com.kauailabs.navx.frc.ITimestampedDataSubscriber;
import com.kauailabs.navx.AHRSProtocol.AHRSUpdateBase;
import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.sf2.orientation.TimestampedQuaternion;
import com.kauailabs.sf2.sensors.ITimestampedQuaternionSensor;
import com.kauailabs.sf2.sensors.ITimestampedQuaternionSensorDataSubscriber;

public class navXSensor implements ITimestampedQuaternionSensor, ITimestampedDataSubscriber {
	AHRS navx_sensor;
	TimestampedQuaternion curr_data;
	public navXSensor(AHRS navx_sensor){
		this.navx_sensor = navx_sensor;
		this.curr_data = new TimestampedQuaternion();
		this.curr_data.setValid(true);
	}
	@Override
	public int getUpdateRateHz() {
		return this.navx_sensor.getActualUpdateRate();
	}
	
	@Override
	public boolean registerCallback(ITimestampedQuaternionSensorDataSubscriber subscriber) {	
		return this.navx_sensor.registerCallback(this, subscriber);
	}
	
	@Override
	public void timestampedDataReceived(long system_timestamp, long sensor_timestamp, AHRSUpdateBase data, Object context) {
		ITimestampedQuaternionSensorDataSubscriber subscriber = 
				(ITimestampedQuaternionSensorDataSubscriber)context;
		if ( subscriber != null ) {
			curr_data.set(data.quat_w, data.quat_x, data.quat_y, data.quat_z, sensor_timestamp);
			subscriber.timestampedDataReceived(system_timestamp, curr_data);
		}
	}
}
