#include "WPILib.h"
#include "AHRS.h"
#include "orientation/OrientationHistory.h"
#include "navXSensor.h"

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

const static int joystickChannel	= 0;

class Robot: public IterativeRobot
{
	AHRS *ahrs;
    navXSensor *p_navx_sensor;
    OrientationHistory *p_orientation_history;
    Joystick stick;
    double last_write_timestamp;

public:
	Robot() :
		stick(joystickChannel)
	{
		last_write_timestamp = 0;
        try {
            /* Communicate w/navX MXP via the MXP SPI Bus.                                       */
            /* Alternatively:  I2C::Port::kMXP, SerialPort::Port::kMXP or SerialPort::Port::kUSB */
            /* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details.   */
            ahrs = new AHRS(SPI::Port::kMXP);
        } catch (std::exception& ex ) {
            std::string err_string = "Error instantiating navX MXP:  ";
            err_string += ex.what();
            DriverStation::ReportError(err_string.c_str());
        }
        if ( ahrs ) {
            LiveWindow::GetInstance()->AddSensor("IMU", "Gyro", ahrs);
            p_navx_sensor = new navXSensor(ahrs, "navXSensor");
            p_orientation_history = new OrientationHistory(p_navx_sensor, 50 * 10);
        }
	}

private:
	void AutonomousInit()
	{
	}

	void AutonomousPeriodic()
	{
	}

	void TeleopInit()
	{
	}

	void TeleopPeriodic()
	{
        bool write_history = stick.GetRawButton(1);
        if ( write_history ) {
        	if ((Timer::GetFPGATimestamp() - last_write_timestamp) > 5.0) {
        		p_orientation_history->writeToDirectory("/home/lvuser/sf2");
                last_write_timestamp = Timer::GetFPGATimestamp();
        	}
        }

		/* Acquire Historical Orientation Data */
        long navx_timestamp = ahrs->GetLastSensorTimestamp();
        navx_timestamp -= 1000; /* look 1 second backwards in time */
        TimestampedValue<Quaternion> historical_quat;
        p_orientation_history->getQuaternionAtTime(navx_timestamp, historical_quat);
        float historical_yaw = p_orientation_history->getYawDegreesAtTime(navx_timestamp);
        float historical_pitch = p_orientation_history->getPitchDegreesAtTime(navx_timestamp);
        float historical_roll = p_orientation_history->getRollDegreesAtTime(navx_timestamp);

        /* Acquire Current Orientation Data */
        float curr_yaw = ahrs->GetYaw();
        float curr_pitch = ahrs->GetPitch();
        float curr_roll = ahrs->GetRoll();

        /* Calculate orientation change */
        float delta_yaw = curr_yaw - historical_yaw;
        float delta_pitch = curr_pitch - historical_pitch;
        float delta_roll = curr_roll - historical_roll;

        /* Display historical orientation data on Dashboard */
        SmartDashboard::PutNumber("SF2_Historical_Yaw", historical_yaw);
        SmartDashboard::PutNumber("SF2_Historical_Pitch", historical_pitch);
        SmartDashboard::PutNumber("SF2_Historical_Roll", historical_roll);
        SmartDashboard::PutNumber("SF2_Historical_QuaternionW", historical_quat.getValue().getW());
        SmartDashboard::PutNumber("SF2_Historical_QuaternionX", historical_quat.getValue().getX());
        SmartDashboard::PutNumber("SF2_Historical_QuaternionY", historical_quat.getValue().getY());
        SmartDashboard::PutNumber("SF2_Historical_QuaternionZ", historical_quat.getValue().getZ());

        /* Display whether historical values are interpolated or not. */
        SmartDashboard::PutBoolean("SF2_Interpolated", historical_quat.getInterpolated());

        /* Display 6-axis Processed Angle & Quaternion Data on Dashboard. */
        SmartDashboard::PutNumber("IMU_Yaw",		 curr_yaw);
        SmartDashboard::PutNumber("IMU_Pitch",    curr_pitch);
        SmartDashboard::PutNumber("IMU_Roll",     curr_roll);
        SmartDashboard::PutNumber("QuaternionW",  ahrs->GetQuaternionW());
        SmartDashboard::PutNumber("QuaternionX",  ahrs->GetQuaternionX());
        SmartDashboard::PutNumber("QuaternionY",  ahrs->GetQuaternionY());
        SmartDashboard::PutNumber("QuaternionZ",  ahrs->GetQuaternionZ());

        SmartDashboard::PutNumber("Delta_Yaw",	delta_yaw);
        SmartDashboard::PutNumber("Delta_Pitch",	delta_pitch);
        SmartDashboard::PutNumber("Delta_Roll",	delta_roll);
	}

	void TestPeriodic()
	{
	}

};

START_ROBOT_CLASS(Robot)
