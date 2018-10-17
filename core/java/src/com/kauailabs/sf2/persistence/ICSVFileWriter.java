package com.kauailabs.sf2.persistence;

import java.io.PrintWriter;

public interface ICSVFileWriter {
    boolean writeCSV(PrintWriter printWriter);
}