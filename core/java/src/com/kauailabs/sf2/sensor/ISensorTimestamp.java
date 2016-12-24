package com.kauailabs.sf2.sensor;

public interface ISensorTimestamp {
	
	/**
	 * If this stream is timestamped with a Sensor timestamp, returns current difference
	 * between the Host Processor Timestamp and the Sensor Timestamp.  If false is 
	 * returned, the sensor data does not have an associated Sensor timestamp.
	 * @return
	 */
	boolean getDifferentialTimestampPair(SensorDataSourceInfo sensor_data_info);
}
