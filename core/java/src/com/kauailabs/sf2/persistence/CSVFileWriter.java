package com.kauailabs.sf2.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;

public class CSVFileWriter implements IFileWriter {
	String file_name_prefix;
	ICSVFileWriter csv_writer;
	public CSVFileWriter(String file_name_prefix, ICSVFileWriter csv_writer) {
		this.file_name_prefix = file_name_prefix;
		this.csv_writer = csv_writer;
	}
	public CSVFileWriter(String file_name_prefix) {
		this.file_name_prefix = file_name_prefix;
	}
	public void setWriter(ICSVFileWriter csv_writer) {
		this.csv_writer = csv_writer;
	}	
	public boolean writeToDirectory(String directory) {

		File dir = new File(directory);
		if (!dir.isDirectory() || !dir.canWrite()) {
			if (!dir.mkdirs()) {
				System.out.println("Directory parameter '" + directory + "' must be a writable directory.");
				return false;
			}
		}

		if ((directory.charAt(directory.length() - 1) != '/') && (directory.charAt(directory.length() - 1) != '\\')) {
			directory += File.separatorChar;
		}

		String filename_prefix = this.file_name_prefix + "History";
		String filename_suffix = "csv";

		File f = new File(directory);
		File[] matching_files = f.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(filename_prefix) && name.endsWith(filename_suffix);
			}
		});

		int next_available_index = -1;

		for (File matching_file : matching_files) {
			String file_name = matching_file.getName();
			String file_name_prefix = file_name.replaceFirst("[.][^.]+$", "");
			String file_counter = file_name_prefix.substring(filename_prefix.length());
			Integer counter = Integer.decode(file_counter);
			if (counter.intValue() > next_available_index) {
				next_available_index = counter.intValue();
			}
		}

		next_available_index++;

		String new_filename = filename_prefix + Integer.toString(next_available_index);
		return writeToFile(directory + new_filename + "." + filename_suffix);
	}

	public boolean writeToFile(String file_path) {
		try {
			PrintWriter out = new PrintWriter(file_path);
			boolean success = writeCSV(out);
			out.close();
			return success;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	protected boolean writeCSV(PrintWriter out) { 
		return csv_writer.writeCSV(out);
	}
}
