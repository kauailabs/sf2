/*
 * TimestampInfo.h
 *
 *  Created on: Dec 27, 2016
 *      Author: Scott
 */

#ifndef SRC_TIME_TIMESTAMPINFO_H_
#define SRC_TIME_TIMESTAMPINFO_H_

class TimestampInfo {
public:
	enum Scope {
		Sensor, 		/* Timestamp is unique to this sensor. */
		Processor, 		/* Timestamp is unique to the sensor's Host Processor. */
		SynchNetwork 	/* Timestamp is network-synchronized. */
	};

	enum Basis {
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

	TimestampInfo( Scope scope, Basis basis, double resolution_secs, double accuracy_secs, double drift_secs_per_hour, double average_latency_secs, Timestamp& default_timestamp) {
		this->basis = basis;
		this->resolution_secs = resolution_secs;
		this->accuracy_secs = accuracy_secs;
		this->drift_secs_per_hour = drift_secs_per_hour;
		this->default_timestamp = default_timestamp;
		this->average_latency_secs = average_latency_secs;
	}

	Scope getScope() { return scope; }
	Basis getBasis() { return basis; }
	double getTimestampResolutionSecs() { return resolution_secs; }
	double getTimestampAccuracyPlusMinusSecs() { return accuracy_secs; }
	double getTimestampDriftSecsPerHour() { return drift_secs_per_hour; }
	double getAverageLatencySecs() { return average_latency_secs; }
	Timestamp getDefaultTimestamp() { return default_timestamp; }
};

#endif /* SRC_TIME_TIMESTAMPINFO_H_ */
