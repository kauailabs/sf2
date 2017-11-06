package com.kauailabs.sf2.persistence;

public interface IFileWriter {
	boolean writeToDirectory(String directory);
	boolean writeToFile(String file_path);
}
