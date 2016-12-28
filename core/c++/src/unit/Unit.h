/*
 * Unit.h
 *
 *  Created on: Dec 27, 2016
 *      Author: Scott
 */

#ifndef SRC_UNIT_UNIT_H_
#define SRC_UNIT_UNIT_H_

#include <forward_list>
#include <string>

using namespace std;

class Unit {
public:
	float convertToPrimaryUnits(float value) { return value; }
	float convertFromPrimaryUnits(float value) { return value; }

	class IUnit
	{
		virtual string getName() = 0;
		virtual string getAbbreviation() = 0;
	};

	class IUnitFamily
	{
	public:
		virtual IUnit& getPrimaryUnit() = 0;
		virtual forward_list<IUnit *>& getSecondaryUnits() = 0; /* May return an empty list. */
	};

	class IUnitDescendant
	{
		virtual IUnitFamily& getUnitFamily() = 0;
	};

	class Unitless : IUnit {
	public:
		string getName() { return "(Unitless)"; }
		string getAbbreviation() { return "(u)"; }
	};

	class ElectricPotential : IUnitFamily {
	public:
		class Volts : Unit, IUnit {
		public:
			string getName() { return "Volts"; }
			string getAbbreviation() { return "V"; }
		};
		Volts primary_unit;
		forward_list<IUnit *> secondary_units;
		IUnit& getPrimaryUnit() { return primary_unit; }
		forward_list<IUnit *>& getSecondaryUnits() { return secondary_units; }
	};

	class ElectricCurrent : IUnitFamily {
	public:
		class Amps : Unit, IUnit {
		public:
			string getName() { return "Amps"; }
			string getAbbreviation() { return "A"; }
		};
		Amps primary_unit;
		forward_list<IUnit *> secondary_units;
		IUnit& getPrimaryUnit() { return primary_unit; };
		forward_list<IUnit *>& getSecondaryUnits() { return secondary_units; }
	};

	class Distance : IUnitFamily {
		Distance unit_family;
	public:
		class DistanceUnit : Unit, IUnitDescendant {
		public:
			IUnitFamily& getUnitFamily() { return Distance::unit_family; }
		};

		class Meters : DistanceUnit, IUnit {
		public:
			string getName() { return "Meters"; }
			string getAbbreviation() { return "m"; }
		};
		class Inches : DistanceUnit, IUnit {
		public:
			static const float METERS_PER_INCH    = .0254f;
			static const float INCHES_PER_METER   = 1f / METERS_PER_INCH;

			string getName() { return "Inches"; }
			string getAbbreviation() { return "in"; }
			float convertToPrimaryUnits(float value) { return value * METERS_PER_INCH; }
			float convertFromPrimaryUnits(float value) { return value * INCHES_PER_METER; }
		};
		class Feet : DistanceUnit, IUnit {
		public:
			static const float INCHES_PER_FOOT    = 12f;
			static const float METERS_PER_FOOT    = Inches::METERS_PER_INCH * INCHES_PER_FOOT;
			static const float FEET_PER_METER     = 1f / METERS_PER_FOOT;

			string getName() { return "Feet"; }
			string getAbbreviation() { return "ft"; }
			float convertToPrimaryUnits(float value) { return value * METERS_PER_FOOT; }
			float convertFromPrimaryUnits(float value) { return value * FEET_PER_METER; }
		};
		class Millimeters : DistanceUnit, IUnit {
		public:
			static const float MILLIMETERS_PER_METER = 1000f;
			static const float METERS_PER_MILLIMETER = 1f/1000f;

			string getName() { return "Millimeters"; }
			string getAbbreviation() { return "m"; }
			float convertToPrimaryUnits(float value) { return value * METERS_PER_MILLIMETER; }
			float convertFromPrimaryUnits(float value) { return value * MILLIMETERS_PER_METER; }
		};
		class Centimeters : DistanceUnit, IUnit {
		public:
			static const float CENTIMETERS_PER_METER = 100f;
			static const float METERS_PER_CENTIMETER = 1f/100f;

			string getName() { return "Centimeters"; }
			string getAbbreviation() { return "cm"; }
			float convertToPrimaryUnits(float value) { return value * METERS_PER_CENTIMETER; }
			float convertFromPrimaryUnits(float value) { return value * CENTIMETERS_PER_METER; }
		};

		Meters meters;
		Inches inches;
		Feet feet;
		Millimeters millimeters;
		Centimeters centimeters;
		forward_list<IUnit *> secondary_units;

		IUnit& getPrimaryUnit() { return meters; };
		Distance(){
			secondary_units.insert_after(secondary_units.end(), &inches);
			secondary_units.insert_after(secondary_units.end(), &feet);
			secondary_units.insert_after(secondary_units.end(), &millimeters);
			secondary_units.insert_after(secondary_units.end(), &centimeters);
		}
		forward_list<IUnit *>& getSecondaryUnits() {
			return secondary_units;
		}
	};

	class Angle : IUnitFamily {
		Angle unit_family;
		static const float PI = 3.14159265358979f;
	public:
		class AngleUnit : Unit, IUnit, IUnitDescendant {
			IUnitFamily& getUnitFamily() { return Angle::unit_family; }
		};

		class Radians : AngleUnit {
		public:
			string getName() { return "Radians"; }
			string getAbbreviation() { return "rad"; }
		};

		class Degrees : AngleUnit {
		public:
			/* Range:  -180.0 to 180.0 */
			static const float RADIANS_TO_DEGREES = (float)(180.0 / Angle::PI);
			static const float DEGREES_TO_RADIANS = (float)(Angle::PI / 180.0);
			string getName() { return "Degrees"; }
			string getAbbreviation() { return "deg"; }
			float convertToPrimaryUnits(float value) { return value * DEGREES_TO_RADIANS; }
			float convertFromPrimaryUnits(float value) { return value * RADIANS_TO_DEGREES; }
		};

		class CompassHeading : AngleUnit {
		public:
			/* North is 0 degrees, range 0-360. */
			static const float DEGREES_IN_HALF_CIRCLE = 180.0f;
			string getName() { return "Heading"; }
			string getAbbreviation() { return "deg"; }
			float convertToPrimaryUnits(float value) { return  (value - DEGREES_IN_HALF_CIRCLE) * Degrees::DEGREES_TO_RADIANS; }
			float convertFromPrimaryUnits(float value) { return (value * Degrees::RADIANS_TO_DEGREES) + DEGREES_IN_HALF_CIRCLE; }
		};
		class Revolutions : AngleUnit {
		public:
			static const float REVOLUTIONS_TO_RADIANS = (float)(2.0f*Angle::PI);
			static const float RADIANS_TO_REVOLUTIONS = 1.0f/REVOLUTIONS_TO_RADIANS;
			string getName() { return "Revolutions"; }
			string getAbbreviation() { return "ref"; }
			float convertToPrimaryUnits(float value) { return value * REVOLUTIONS_TO_RADIANS; }
			float convertFromPrimaryUnits(float value) { return value * RADIANS_TO_REVOLUTIONS; }
		};

		Radians radians;
		forward_list<IUnit *> secondary_units;
		Degrees degrees;
		CompassHeading compass;
		Revolutions revolutions;
		IUnit& getPrimaryUnit() { return radians; };
		Angle(){
			secondary_units.insert_after(secondary_units.end(), &degrees);
			secondary_units.insert_after(secondary_units.end(), &compass);
			secondary_units.insert_after(secondary_units.end(), &revolutions);
		}
		forward_list<IUnit *>& getSecondaryUnits() {
			return secondary_units;
		}
	};

	class Time : IUnitFamily {
		Time unit_family;
	public:
		class TimeUnit : Unit, IUnitDescendant {
		public:
			IUnitFamily getUnitFamily() { return unit_family; }
		};
		class Seconds : TimeUnit, IUnit {
		public:
			string getName() { return "Seconds"; }
			string getAbbreviation() { return "sec"; }
		};
		class Hours : TimeUnit, IUnit  {
		public:
			static const float HOURS_TO_SECONDS = (float)(60*60);
			static const float SECONDS_TO_HOURS = (float)(1.0f / HOURS_TO_SECONDS);
			string getName() { return "Hours"; }
			string getAbbreviation() { return "hr"; }
			float convertToPrimaryUnits(float value) { return value * HOURS_TO_SECONDS; }
			float convertFromPrimaryUnits(float value) { return value * SECONDS_TO_HOURS; }
		};
		class Minutes : TimeUnit, IUnit {
		public:
			static const float MINUTES_TO_SECONDS = (float)(60);
			static const float SECONDS_TO_MINUTES = (float)(1.0f / MINUTES_TO_SECONDS);
			string getName() { return "Minutes"; }
			string getAbbreviation() { return "min"; }
			float convertToPrimaryUnits(float value) { return value * MINUTES_TO_SECONDS; }
			float convertFromPrimaryUnits(float value) { return value * SECONDS_TO_MINUTES; }
		};

		Seconds seconds;
		Hours hours;
		Minutes minutes;
		IUnit& getPrimaryUnit() { return seconds; };
		forward_list<IUnit *> secondary_units;
		Time() {
			secondary_units.insert_after(secondary_units.end(), &hours);
			secondary_units.insert_after(secondary_units.end(), &minutes);
		}

		forward_list<IUnit *>& getSecondaryUnits() {
			return secondary_units;
		}
	};

	class SecondDerivative : IUnitFamily {
		SecondDerivative unit_family;
	public:
		class SecondDerivativeUnit : Unit, IUnitDescendant {
		public:
			IUnitFamily getUnitFamily() { return unit_family; }
		};
		class SecondsSquared : Unit, IUnit {
			string getName() { return "Squared Seconds"; }
			string getAbbreviation() { return "s^2"; }
		};
		SecondsSquared seconds_squared;
		forward_list<IUnit *> secondary_units;
		IUnit& getPrimaryUnit() { return seconds_squared; }
		forward_list<IUnit *>& getSecondaryUnits() { return secondary_units; }
	};

	class ThirdDerivative : IUnitFamily {
		ThirdDerivative unit_family;
	public:
		class ThirdDerivativeUnit : Unit, IUnitDescendant {
		public:
			IUnitFamily getUnitFamily() { return unit_family; }
		};
		class SecondsCubed : Unit, IUnit {
		public:
			string getName() { return "Cubed Seconds"; }
			string getAbbreviation() { return "s^3"; }
		};
		SecondsCubed seconds_cubed;
		forward_list<IUnit *> secondary_units;
		IUnit getPrimaryUnit() { return seconds_cubed; }
		forward_list<IUnit *>& getSecondaryUnits() { return secondary_units; }
	};
};

#endif /* SRC_UNIT_UNIT_H_ */
