package com.kauailabs.sf2.pose.drivetrain;

public class DriveTrainParameters {

	int num_drive_wheels;
	int num_steer_wheels;
	float width_inches;
	float length_inches;
	float drivewheel_diameter_inches;
	float enc_ticks_per_drivewheel_revolution;
	
	static final int DEFAULT_ENC_COUNTS_PER_REV = 256;
	static final int DEFAULT_ENC_TICKS_PER_COUNT = 4;
	static final int DEFAULT_GEAR_RATIO = 1;
	static final int DEFAULT_ENC_TICKS_PER_DRIVEWHEEL_REV = 
			DEFAULT_ENC_COUNTS_PER_REV * 
			DEFAULT_ENC_TICKS_PER_COUNT *
			DEFAULT_GEAR_RATIO;
	
	public int getNumDriveWheels() { return num_drive_wheels; }
	public int getNumSteerWheels() { return num_steer_wheels; }
	public float getWidthInches() { return width_inches; }
	public float getLengthInches() { return length_inches; }
	public float getDriveWheelDiameterInches() { return drivewheel_diameter_inches; }
	public float getEncTicksPerDriveWheelRevolution() { return enc_ticks_per_drivewheel_revolution; }

	public DriveTrainParameters() {
		num_drive_wheels = 4;
		num_steer_wheels = 0;
		width_inches = 24;
		length_inches = 18;
		drivewheel_diameter_inches = 6;
		enc_ticks_per_drivewheel_revolution = DEFAULT_ENC_TICKS_PER_DRIVEWHEEL_REV;
	}
	
	public void setNumDriveWheels(int num_drive_wheels) { this.num_drive_wheels = num_drive_wheels; }
	public void setNumSteerWheels(int num_steer_wheels) { this.num_steer_wheels = num_steer_wheels; }
	public void setWidthInches(float width_inches) { this.width_inches = width_inches; }
	public void setLengthInches(float length_inches) { this.width_inches = length_inches; }
	public void setDriveWheelDiameterInches(float drivewheel_diameter_inches) { 
		this.drivewheel_diameter_inches = drivewheel_diameter_inches; 
	}
	public void setEncTicksPerDriveWheelRevolution( float enc_ticks_per_drivewheel_revolution) {
		this.enc_ticks_per_drivewheel_revolution = enc_ticks_per_drivewheel_revolution;		
	}

	public float getInchesPerEncTick() {
		return drivewheel_diameter_inches / enc_ticks_per_drivewheel_revolution;
	}
}
