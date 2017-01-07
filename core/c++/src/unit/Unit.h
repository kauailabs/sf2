/* ============================================
 SF2 source code is placed under the MIT license
 Copyright (c) 2017 Kauai Labs

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ===============================================
 */

#ifndef SRC_UNIT_UNIT_H_
#define SRC_UNIT_UNIT_H_

#include <vector>
#include <string>

using namespace std;

class Unit {
public:
	float convertToPrimaryUnits(float value) {
		return value;
	}
	float convertFromPrimaryUnits(float value) {
		return value;
	}
};

class IUnit {
public:
	virtual string getName() = 0;
	virtual string getAbbreviation() = 0;
};

class IUnitFamily {
public:
	virtual IUnit& getPrimaryUnit() = 0;
	virtual vector<IUnit *>& getSecondaryUnits() = 0; /* May return an empty list. */
};

class IUnitDescendant {
public:
	virtual IUnitFamily& getUnitFamily() = 0;
};

class Unitless: public IUnit {
public:
	string getName() {
		return "(Unitless)";
	}
	string getAbbreviation() {
		return "(u)";
	}
};

class ElectricPotential: public IUnitFamily {
public:
	class Volts: public Unit, public IUnit {
	public:
		string getName() {
			return "Volts";
		}
		string getAbbreviation() {
			return "V";
		}
	};
	Volts primary_unit;
	vector<IUnit *> secondary_units;
	IUnit& getPrimaryUnit() {
		return primary_unit;
	}
	vector<IUnit *>& getSecondaryUnits() {
		return secondary_units;
	}
};

class ElectricCurrent: public IUnitFamily {
public:
	class Amps: public Unit, public IUnit {
	public:
		string getName() {
			return "Amps";
		}
		string getAbbreviation() {
			return "A";
		}
	};
	Amps primary_unit;
	vector<IUnit *> secondary_units;
	IUnit& getPrimaryUnit() {
		return primary_unit;
	}

	vector<IUnit *>& getSecondaryUnits() {
		return secondary_units;
	}
};

class Distance: public IUnitFamily {
	static Distance *p_unit_family;
public:
	class DistanceUnit: public Unit, public IUnitDescendant {
	public:
		IUnitFamily& getUnitFamily() {
			return *Distance::p_unit_family;
		}
	};

	class Meters: public DistanceUnit, public IUnit {
	public:
		string getName() {
			return "Meters";
		}
		string getAbbreviation() {
			return "m";
		}
	};
	class Inches: public DistanceUnit, public IUnit {
	public:
		static constexpr float METERS_PER_INCH = .0254f;
		static constexpr float INCHES_PER_METER = 1.0f / METERS_PER_INCH;

		string getName() {
			return "Inches";
		}
		string getAbbreviation() {
			return "in";
		}
		float convertToPrimaryUnits(float value) {
			return value * METERS_PER_INCH;
		}
		float convertFromPrimaryUnits(float value) {
			return value * INCHES_PER_METER;
		}
	};
	class Feet: public DistanceUnit, public IUnit {
	public:
		static constexpr float INCHES_PER_FOOT = 12.0f;
		static constexpr float METERS_PER_FOOT = Inches::METERS_PER_INCH
				* INCHES_PER_FOOT;
		static constexpr float FEET_PER_METER = 1.0f / METERS_PER_FOOT;

		string getName() {
			return "Feet";
		}
		string getAbbreviation() {
			return "ft";
		}
		float convertToPrimaryUnits(float value) {
			return value * METERS_PER_FOOT;
		}
		float convertFromPrimaryUnits(float value) {
			return value * FEET_PER_METER;
		}
	};
	class Millimeters: public DistanceUnit, public IUnit {
	public:
		static constexpr float MILLIMETERS_PER_METER = 1000.0f;
		static constexpr float METERS_PER_MILLIMETER = 1.0f / 1000.0f;

		string getName() {
			return "Millimeters";
		}
		string getAbbreviation() {
			return "m";
		}
		float convertToPrimaryUnits(float value) {
			return value * METERS_PER_MILLIMETER;
		}
		float convertFromPrimaryUnits(float value) {
			return value * MILLIMETERS_PER_METER;
		}
	};
	class Centimeters: public DistanceUnit, public IUnit {
	public:
		static constexpr float CENTIMETERS_PER_METER = 100.0f;
		static constexpr float METERS_PER_CENTIMETER = 1.0f / 100.0f;

		string getName() {
			return "Centimeters";
		}
		string getAbbreviation() {
			return "cm";
		}
		float convertToPrimaryUnits(float value) {
			return value * METERS_PER_CENTIMETER;
		}
		float convertFromPrimaryUnits(float value) {
			return value * CENTIMETERS_PER_METER;
		}
	};

	Meters meters;
	Inches inches;
	Feet feet;
	Millimeters millimeters;
	Centimeters centimeters;
	vector<IUnit *> secondary_units;

	IUnit& getPrimaryUnit() {
		return meters;
	}
	;
	Distance() {
		secondary_units.push_back(&inches);
		secondary_units.push_back(&feet);
		secondary_units.push_back(&millimeters);
		secondary_units.push_back(&centimeters);
	}
	vector<IUnit *>& getSecondaryUnits() {
		return secondary_units;
	}
};

class Angle: public IUnitFamily {
	static Angle *p_unit_family;
	static constexpr float PI = 3.14159265358979f;
public:
	class AngleUnit: public Unit, public IUnit, public IUnitDescendant {
		IUnitFamily& getUnitFamily() {
			return *Angle::p_unit_family;
		}
	};

	class Radians: public AngleUnit {
	public:
		string getName() {
			return "Radians";
		}
		string getAbbreviation() {
			return "rad";
		}
	};

	class Degrees: public AngleUnit {
	public:
		/* Range:  -180.0 to 180.0 */
		static constexpr float RADIANS_TO_DEGREES = (float) (180.0 / Angle::PI);
		static constexpr float DEGREES_TO_RADIANS = (float) (Angle::PI / 180.0);
		string getName() {
			return "Degrees";
		}
		string getAbbreviation() {
			return "deg";
		}
		float convertToPrimaryUnits(float value) {
			return value * DEGREES_TO_RADIANS;
		}
		float convertFromPrimaryUnits(float value) {
			return value * RADIANS_TO_DEGREES;
		}
	};

	class CompassHeading: public AngleUnit {
	public:
		/* North is 0 degrees, range 0-360. */
		static constexpr float DEGREES_IN_HALF_CIRCLE = 180.0f;
		string getName() {
			return "Heading";
		}
		string getAbbreviation() {
			return "deg";
		}
		float convertToPrimaryUnits(float value) {
			return (value - DEGREES_IN_HALF_CIRCLE)
					* Degrees::DEGREES_TO_RADIANS;
		}
		float convertFromPrimaryUnits(float value) {
			return (value * Degrees::RADIANS_TO_DEGREES)
					+ DEGREES_IN_HALF_CIRCLE;
		}
	};
	class Revolutions: public AngleUnit {
	public:
		static constexpr float REVOLUTIONS_TO_RADIANS = (float) (2.0f
				* Angle::PI);
		static constexpr float RADIANS_TO_REVOLUTIONS = 1.0f
				/ REVOLUTIONS_TO_RADIANS;
		string getName() {
			return "Revolutions";
		}
		string getAbbreviation() {
			return "ref";
		}
		float convertToPrimaryUnits(float value) {
			return value * REVOLUTIONS_TO_RADIANS;
		}
		float convertFromPrimaryUnits(float value) {
			return value * RADIANS_TO_REVOLUTIONS;
		}
	};

	Radians radians;
	vector<IUnit *> secondary_units;
	Degrees degrees;
	CompassHeading compass;
	Revolutions revolutions;
	IUnit& getPrimaryUnit() {
		return radians;
	}
	;
	Angle() {
		secondary_units.push_back(&degrees);
		secondary_units.push_back(&compass);
		secondary_units.push_back(&revolutions);
	}
	vector<IUnit *>& getSecondaryUnits() {
		return secondary_units;
	}
};

class Time: public IUnitFamily {
	static Time *p_unit_family;
public:
	class TimeUnit: Unit, IUnitDescendant {
	public:
		IUnitFamily& getUnitFamily() {
			return *Time::p_unit_family;
		}
	};

	class Nanoseconds : public TimeUnit, public IUnit {
		public:
		static constexpr float SECONDS_TO_NANOSECONDS = (float) (1.0f * 1000000000);
		static constexpr float NANOSECONDS_TO_SECONDS = (float) (1.0f / 1000000000);

		string getName() {
			return "Nanoseconds";
		}

		string getAbbreviation() {
			return "ns";
		}

		float convertToPrimaryUnits(float value) {
			return value * NANOSECONDS_TO_SECONDS;
		}

		float convertFromPrimaryUnits(float value) {
			return value * SECONDS_TO_NANOSECONDS;
		}
	};

	class Microseconds : public TimeUnit, public IUnit {
		public:
		static constexpr float SECONDS_TO_MICROSECONDS = (float) (1.0f * 1000000);
		static constexpr float MICROSECONDS_TO_SECONDS = (float) (1.0f / 1000000);

		string getName() {
			return "Microseconds";
		}

		string getAbbreviation() {
			return "us";
		}

		float convertToPrimaryUnits(float value) {
			return value * MICROSECONDS_TO_SECONDS;
		}

		float convertFromPrimaryUnits(float value) {
			return value * SECONDS_TO_MICROSECONDS;
		}
	};

class Milliseconds : public TimeUnit, public IUnit {
	public:
	static constexpr float SECONDS_TO_MILLISECONDS = (float) (1.0f * 1000);
	static constexpr float MILLISECONDS_TO_SECONDS = (float) (1.0f / 1000);

	string getName() {
		return "Milliseconds";
	}

	string getAbbreviation() {
		return "ms";
	}

	float convertToPrimaryUnits(float value) {
		return value * MILLISECONDS_TO_SECONDS;
	}

	float convertFromPrimaryUnits(float value) {
		return value * SECONDS_TO_MILLISECONDS;
	}
};

	class Seconds: public TimeUnit, public IUnit {
	public:
		string getName() {
			return "Seconds";
		}
		string getAbbreviation() {
			return "sec";
		}
	};
	class Hours: public TimeUnit, public IUnit {
	public:
		static constexpr float HOURS_TO_SECONDS = (float) (60 * 60);
		static constexpr float SECONDS_TO_HOURS = (float) (1.0f
				/ HOURS_TO_SECONDS);
		string getName() {
			return "Hours";
		}
		string getAbbreviation() {
			return "hr";
		}
		float convertToPrimaryUnits(float value) {
			return value * HOURS_TO_SECONDS;
		}
		float convertFromPrimaryUnits(float value) {
			return value * SECONDS_TO_HOURS;
		}
	};
	class Minutes: public TimeUnit, public IUnit {
	public:
		static constexpr float MINUTES_TO_SECONDS = (float) (60);
		static constexpr float SECONDS_TO_MINUTES = (float) (1.0f
				/ MINUTES_TO_SECONDS);
		string getName() {
			return "Minutes";
		}
		string getAbbreviation() {
			return "min";
		}
		float convertToPrimaryUnits(float value) {
			return value * MINUTES_TO_SECONDS;
		}
		float convertFromPrimaryUnits(float value) {
			return value * SECONDS_TO_MINUTES;
		}
	};

	Nanoseconds nanoseconds;
	Microseconds microseconds;
	Milliseconds milliseconds;
	Seconds seconds;
	Hours hours;
	Minutes minutes;
	IUnit& getPrimaryUnit() {
		return seconds;
	}

	vector<IUnit *> secondary_units;
	Time() {
		secondary_units.push_back(&hours);
		secondary_units.push_back(&minutes);
		secondary_units.push_back(&milliseconds);
		secondary_units.push_back(&microseconds);
		secondary_units.push_back(&nanoseconds);
	}

	vector<IUnit *>& getSecondaryUnits() {
		return secondary_units;
	}
};

class SecondDerivative: public IUnitFamily {
	static SecondDerivative *p_unit_family;
public:
	class SecondDerivativeUnit: public Unit, public IUnitDescendant {
	public:
		IUnitFamily& getUnitFamily() {
			return *SecondDerivative::p_unit_family;
		}
	};
	class SecondsSquared: public Unit, public IUnit {
		string getName() {
			return "Squared Seconds";
		}
		string getAbbreviation() {
			return "s^2";
		}
	};
	SecondsSquared seconds_squared;
	vector<IUnit *> secondary_units;
	IUnit& getPrimaryUnit() {
		return seconds_squared;
	}
	vector<IUnit *>& getSecondaryUnits() {
		return secondary_units;
	}
};

class ThirdDerivative: public IUnitFamily {
	static ThirdDerivative *p_unit_family;
public:
	class ThirdDerivativeUnit: public Unit, public IUnitDescendant {
	public:
		IUnitFamily& getUnitFamily() {
			return *ThirdDerivative::p_unit_family;
		}
	};
	class SecondsCubed: public Unit, public IUnit {
	public:
		string getName() {
			return "Cubed Seconds";
		}
		string getAbbreviation() {
			return "s^3";
		}
	};
	SecondsCubed seconds_cubed;
	vector<IUnit *> secondary_units;
	IUnit& getPrimaryUnit() {
		return seconds_cubed;
	}
	vector<IUnit *>& getSecondaryUnits() {
		return secondary_units;
	}
};

#endif /* SRC_UNIT_UNIT_H_ */
