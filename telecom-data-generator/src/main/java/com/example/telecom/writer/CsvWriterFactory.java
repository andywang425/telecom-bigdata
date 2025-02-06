package com.example.telecom.writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CsvWriterFactory {
    /**
     * 获取一个 CsvWriter
     *
     * @param fileName 文件路径
     * @param append 是否追加写入
     */
    public CsvWriter getCsvWriter(String fileName, boolean append) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, StandardCharsets.UTF_8, append);
            CSVPrinter csvPrinter = CSVFormat.DEFAULT.builder().setAutoFlush(true).get().print(fileWriter);

            return new CsvWriter(fileWriter, csvPrinter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}