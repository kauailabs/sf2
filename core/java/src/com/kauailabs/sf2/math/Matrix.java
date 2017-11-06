package com.kauailabs.sf2.math;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.kauailabs.sf2.persistence.CSVFileWriter;
import com.kauailabs.sf2.quantity.IQuantity;

public class Matrix extends CSVFileWriter {
	int num_dimensions;
	float matrix[][];	
	String[] dimension_names;
	public Matrix(String name, int num_dimensions, String[] dimension_names) {
		super(name + "Matrix");
		this.num_dimensions = num_dimensions;
		matrix = new float[num_dimensions][num_dimensions];
		this.dimension_names = dimension_names;
	}
	
	public int get_num_dimensions() {
		return num_dimensions;
	}
	
	public float[][] get_matrix() {
		return matrix;
	}
	
	/**
	 * Calculates the covariance of the input data set.
	 * <p>
	 * Note that this value may be interpolated if a sample at the requested
	 * time is not available.
	 * 
	 * @param input: array of values.  Array dimension 1:  must be equal to the matrix num dimensions.
	 * 								   Array dimension 2:  must be equal to num_samples
	 * @param input_avg:  array of averages.  Size must be qual to the matrix num dimensions.
	 * @param num_samples:  defines the number of samples (dimension 2) of the input values.
	 * @return Pitch angle (in degrees, range -180 to 180) at the requested
	 *         timestamp. If a pitch angle at the specified timestamp could not
	 *         be found/interpolated, the value INVALID_ANGLE (NaN) will be
	 *         returned.
	 */

	public void calculate_covariance(float[][] input, float input_avg[], int num_samples) {
		for ( int x = 0; x < num_dimensions; x++) {
			for ( int y = 0; y < num_dimensions; y++) {
				matrix[x][y] = 0;
				for ( int i = 0; i < num_samples; i++) {
					matrix[x][y] +=
						(input[x][i] - input_avg[x]) * (input[y][i] - input_avg[y]); 
				}
				matrix[x][y] /= (num_samples - 1);
			}
		}
	}

	@Override
	public boolean writeCSV(PrintWriter out) {
		boolean success = false;
		if (num_dimensions > 0) {
			success = true;
			/* Write Header */
			String header = ",";
			for ( int i = 0; i < num_dimensions; i++) {
				header += dimension_names[i];
				if(i < (num_dimensions -1)){
					header += ",";
				}
			}
			out.println(header);

			for (int x = 0; x < num_dimensions; x++) {
				String row = ",";
				for (int y = 0; y < num_dimensions; y++) {
					row += matrix[x][y];
					if( y < (num_dimensions -1)){
						row += ",";
					}
				}
				out.println(row);
			}
		}
		return success;
	}
}
