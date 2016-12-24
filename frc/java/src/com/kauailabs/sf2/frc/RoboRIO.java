package com.kauailabs.sf2.frc;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.kauailabs.sf2.sensor.IProcessorInfo;
import com.kauailabs.sf2.time.Timestamp;

import edu.wpi.first.wpilibj.Timer;

public class RoboRIO implements IProcessorInfo {

	@Override
	public String getName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "RoboRIO";
		}
	}

	@Override
	public Timestamp getProcessorTimestamp() {
		Timestamp t = new Timestamp(0,Timestamp.TimestampResolution.Millisecond);
		t.fromSeconds(Timer.getFPGATimestamp());
		return t;
	}

}
