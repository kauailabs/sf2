package com.kauailabs.sf2.sensor;

import com.kauailabs.sf2.time.Timestamp;

public interface IProcessorInfo {
	public String getName();
	public void getProcessorTimestamp(Timestamp out);
}
