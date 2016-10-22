package com.kauailabs.sf2.sensors.frc;

import com.kauailabs.sf2.sensors.IDriveMotorCurrentSensor;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class DriveMotorCurrentSensor implements IDriveMotorCurrentSensor {

	PowerDistributionPanel pdp;
	int pdp_channel;
	CANTalon talon;
	CANJaguar jaguar;
	public enum SensorType { PowerDistributionPanel, CANTalon, CANJaguar };	
	SensorType type;
	
	public DriveMotorCurrentSensor(PowerDistributionPanel pdp, int channel) {
		this.type = SensorType.PowerDistributionPanel;
		this.pdp = pdp;
		this.pdp_channel = channel;
	}
	
	public DriveMotorCurrentSensor(CANTalon talon) {
		this.type = SensorType.CANTalon;
		this.talon = talon;
	}
	
	public DriveMotorCurrentSensor(CANJaguar jaguar) {
		this.type = SensorType.CANTalon;
		this.jaguar = jaguar;
	}	

	@Override
	public double getCurrentAmps() {
		switch(type) {
		case PowerDistributionPanel:
			return pdp.getCurrent(pdp_channel);
		case CANTalon:
			return talon.getOutputCurrent();
		case CANJaguar:
			return jaguar.getOutputCurrent();
		default:
			return 0;
		}
	}

}
