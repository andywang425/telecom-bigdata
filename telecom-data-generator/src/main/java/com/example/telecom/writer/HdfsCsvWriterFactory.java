package com.example.telecom.writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.List;

public class HdfsCsvWriterFactory {
    private final URI HDFS_URI;
    private final String USER;
    private final String OUTPUT_PATH;
    private final Configuration CONFIGURATION;

    public HdfsCsvWriterFactory(URI HDFS_URI, String USER, String OUTPUT_PATH, Configuration CONFIGURATION) {
        this.HDFS_URI = HDFS_URI;
        this.USER = USER;
        this.OUTPUT_PATH = OUTPUT_PATH;
        this.CONFIGURATION = CONFIGURATION;
    }

    /**
     * 获取一个 HdfsCsvWriter
     * @param header csv表头
     * @param fileName 文件名
     */
    public HdfsCsvWriter getHdfsCsvWriter(List<String> header, String fileName) {
        try {
            FileSystem fs = FileSystem.get(HDFS_URI, CONFIGURATION, USER);
            FSDataOutputStream fsDataOutputStream = fs.create(new Path(OUTPUT_PATH, fileName));
            PrintWriter printWriter = new PrintWriter(fsDataOutputStream);
            CSVPrinter csvPrinter = CSVFormat.DEFAULT.builder().setHeader(header.toArray(new String[0])).get().print(printWriter);

            return new HdfsCsvWriter(fs, fsDataOutputStream, printWriter, csvPrinter);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}