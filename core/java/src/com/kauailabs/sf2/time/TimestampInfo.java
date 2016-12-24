package com.kauailabs.sf2.time;

public class TimestampInfo {
	
	public enum Scope { 
		Sensor, 		/* Timestamp is unique to this sensor. */
		Processor, 		/* Timestamp is unique to the sensor's Host Processor. */
		SynchNetwork 	/* Timestamp is network-synchronized. */
	};
	
	public enum Basis { 
		Epoch,			/* Timestamp is num of nanoseconds since Jan. 1, 1970 (UTC) */ 
		SinceLastReboot /* Timestamp is num of nanoseconds since last sensor/processor reboot. */
	};
	
	Basis basis;
	Scope scope;
	double resolution_secs;
	double accuracy_secs; /* +/- */
	double drift_secs_per_hour;
	double average_latency_secs;
	Timestamp default_timestamp;
	
	public TimestampInfo( Scope scope, Basis basis, double resolution_secs, double accuracy_secs, double drift_secs_per_hour, double average_latency_secs, Timestamp default_timestamp) {
		this.basis = basis;
		this.resolution_secs = resolution_secs;
		this.accuracy_secs = accuracy_secs;
		this.drift_secs_per_hour = drift_secs_per_hour;
		this.default_timestamp = default_timestamp;
		this.average_latency_secs = average_latency_secs;
	}
	
	public Scope getScope() { return scope; }
	public Basis getBasis() { return basis; }
	public double getTimestampResolutionSecs() { return resolution_secs; }
	public double getTimestampAccuracyPlusMinusSecs() { return accuracy_secs; } 
	public double getTimestampDriftSecsPerHour() { return drift_secs_per_hour; }
	public double getAverageLatencySecs() { return average_latency_secs; }
	public Timestamp getDefaultTimestamp() { return default_timestamp; }
}
