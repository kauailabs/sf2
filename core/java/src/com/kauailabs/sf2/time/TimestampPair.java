package com.kauailabs.sf2.time;

public class TimestampPair {
	long processor_ts;
	long sensor_ts;	
	public TimestampPair( long processor_ts, long sensor_ts) {
		this.processor_ts = processor_ts;
		this.sensor_ts = sensor_ts;	
	}	
	
	long GetSensorTimestampDelta() { 
		return processor_ts - sensor_ts;
	}
}
