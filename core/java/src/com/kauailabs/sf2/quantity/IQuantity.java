package com.kauailabs.sf2.quantity;

import java.util.ArrayList;

public interface IQuantity {
	/* Returns true if this quantity has a printable value */
	boolean getPrintableString(String printable_string);
	/* Returns true if this is a quantity container */
	boolean getContainedQuantities(ArrayList<IQuantity> quantities);
	/* Returns true if this is a quantity container. */
	boolean getContainedQuantityNames(ArrayList<String> quantity_names);	
}
