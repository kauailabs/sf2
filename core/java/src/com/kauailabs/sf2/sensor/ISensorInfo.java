package com.kauailabs.sf2.sensor;

import com.kauailabs.sf2.time.TimestampInfo;

public interface ISensorInfo {
	String getMake();
	String getModel();
	String getName();
	ISensorDataSource getSensorDataSource();
	TimestampInfo getSensorTimestampInfo();
	IProcessorInfo getHostProcessorInfo();
}
