package org.usfirst.frc.team2465.robot;

import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.sf2.frc.navXSensor;
import com.kauailabs.sf2.orientation.OrientationHistory;
import com.kauailabs.sf2.orientation.Quaternion;
import com.kauailabs.sf2.time.TimestampedValue;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This SF2 Example Robot Application demonstrates using an Orientation Time History,
 * based upon a navX-MXP or navX-Micro sensor.
 * 
 * The number of samples in the history is based upon the currently-configured
 * sensor update rate multiplied by the number of seconds desired in the history.
 *
 * In the operatorControl() method, all current (directly from the navX sensor)
 * as well as historical (from the Orientation Time History) are displayed, as 
 * well as the change in the yaw, pitch and roll angles during that time.
 */
public class Robot extends SampleRobot {
    AHRS ahrs;
    OrientationHistory orientation_history;
    Joystick stick;
    double last_write_timestamp = 0;
 
    public Robot() {
    	/* Instantiate the sensor and the history; the history acquires data 
    	 * continuously from the sensor.  Set the depth of the Orientation Time
    	 * History to 10 seconds, based upon the sensors current update rate.
    	 */
        ahrs = new AHRS(SPI.Port.kMXP);
        navXSensor navx_sensor = new navXSensor(ahrs, "Drivetrain Orientation");
        orientation_history = new OrientationHistory(navx_sensor,
    		ahrs.getRequestedUpdateRate() * 10);
        stick = new Joystick(0);
    }

    /**
     * Display current and historical Orientation Data on Smart Dashboard
     */
    public void operatorControl() {
        while (isOperatorControl() && isEnabled()) {
            
            Timer.delay(0.020);		/* wait for one motor update time period (50Hz)     */

            if ( stick.getRawButton(1)) {            	
            	if ((Timer.getFPGATimestamp() - last_write_timestamp) > 5.0) {
            		orientation_history.writeToDirectory("/home/lvuser/sf2");
                    last_write_timestamp = Timer.getFPGATimestamp();
            	}
            }            
            
            /* Acquire Historical Orientation Data */
            long navx_timestamp = ahrs.getLastSensorTimestamp();
            navx_timestamp -= 1000; /* look 1 second backwards in time */
            float historical_yaw = orientation_history.getYawDegreesAtTime(navx_timestamp);
            float historical_pitch = orientation_history.getPitchDegreesAtTime(navx_timestamp);
            float historical_roll = orientation_history.getRollDegreesAtTime(navx_timestamp);

            /* Acquire Current Orientation Data */
            float curr_yaw = ahrs.getYaw();
            float curr_pitch = ahrs.getPitch();
            float curr_roll = ahrs.getRoll();
            
            /* Calculate orientation change */
            float delta_yaw = curr_yaw - historical_yaw;
            float delta_pitch = curr_pitch - historical_pitch;
            float delta_roll = curr_roll - historical_roll;
            
            /* Display historical orientation data on Dashboard */
            SmartDashboard.putNumber("SF2_Historical_Yaw", historical_yaw);
            SmartDashboard.putNumber("SF2_Historical_Pitch", historical_pitch);
            SmartDashboard.putNumber("SF2_Historical_Roll", historical_roll);

            TimestampedValue<Quaternion> historical_quat = new TimestampedValue<Quaternion>(new Quaternion());
            orientation_history.getQuaternionAtTime(navx_timestamp, historical_quat);            
            SmartDashboard.putNumber("SF2_Historical_QuaternionW", historical_quat.getValue().getW());
            SmartDashboard.putNumber("SF2_Historical_QuaternionX", historical_quat.getValue().getX());
            SmartDashboard.putNumber("SF2_Historical_QuaternionY", historical_quat.getValue().getY());
            SmartDashboard.putNumber("SF2_Historical_QuaternionZ", historical_quat.getValue().getZ());            
            
            /* Display whether historical values are interpolated or not. */
            SmartDashboard.putBoolean("SF2_Interpolated", historical_quat.getInterpolated());
            
            /* Display 6-axis Processed Angle & Quaternion Data on Dashboard. */
            SmartDashboard.putNumber("IMU_Yaw",		 curr_yaw);
            SmartDashboard.putNumber("IMU_Pitch",    curr_pitch);
            SmartDashboard.putNumber("IMU_Roll",     curr_roll);
            SmartDashboard.putNumber("QuaternionW",  ahrs.getQuaternionW());
            SmartDashboard.putNumber("QuaternionX",  ahrs.getQuaternionX());
            SmartDashboard.putNumber("QuaternionY",  ahrs.getQuaternionY());
            SmartDashboard.putNumber("QuaternionZ",  ahrs.getQuaternionZ());
            
            SmartDashboard.putNumber("Delta_Yaw",	delta_yaw);
            SmartDashboard.putNumber("Delta_Pitch",	delta_pitch);
            SmartDashboard.putNumber("Delta_Roll",	delta_roll);
        }
    }

    /**
     * Runs during autonomous mode
     */
    public void autonomous() {
        Timer.delay(2.0);		//    for 2 seconds
    }

    /**
     * Runs during test mode
     */
    public void test() {
    }
}
