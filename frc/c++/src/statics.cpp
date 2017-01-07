/*
 * statics.cpp
 *
 *  Created on: Jan 5, 2017
 *      Author: Scott
 */

#include "orientation/Quaternion.h"
#include "unit/Unit.h"

Unitless Quaternion::component_units;

Angle *Angle::p_unit_family = new Angle();
Distance *Distance::p_unit_family = new Distance();
SecondDerivative *SecondDerivative::p_unit_family = new SecondDerivative();
ThirdDerivative *ThirdDerivative::p_unit_family = new ThirdDerivative();
Time *Time::p_unit_family = new Time();



