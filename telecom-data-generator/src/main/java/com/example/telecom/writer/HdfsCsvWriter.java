package com.example.telecom.writer;

import com.example.telecom.model.Record;
import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVPrinter;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@AllArgsConstructor
public class HdfsCsvWriter {
    private FileSystem fs;
    private FSDataOutputStream fsDataOutputStream;
    private PrintWriter printWriter;
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
            csvPrinter.flush();
            csvPrinter.close();
            printWriter.close();
            fsDataOutputStream.close();
            fs.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}