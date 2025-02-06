package com.example.telecom.writer;

import com.example.telecom.model.Record;
import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
public class CsvWriter {
    private FileWriter fileWriter;
    private CSVPrinter csvPrinter;

    /**
     * 写一条记录
     */
    public void write(Record record) {
        try {
            csvPrinter.printRecord(record.getRecord());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 写多条记录
     */
    public void write(List<? extends Record> records) {
        for (Record record : records) {
            write(record);
        }
    }

    public void close() {
        try {
            csvPrinter.close();
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}