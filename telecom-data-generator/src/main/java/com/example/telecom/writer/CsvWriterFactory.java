package com.example.telecom.writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvWriterFactory {
    /**
     * 获取一个 CsvWriter
     *
     * @param header 表头
     * @param fileName 文件路径
     * @param isAppend 是否追加写入
     */
    public CsvWriter getCsvWriter(List<String> header, String fileName, boolean isAppend) {
        try {
            boolean isFileExist = new File(fileName).exists();
            FileWriter fileWriter = new FileWriter(fileName, isAppend);
            CSVFormat.Builder builder = CSVFormat.DEFAULT.builder().setAutoFlush(true);

            if (!isAppend || !isFileExist) {
                // 如果没有设置追加写入或文件不存在，则添加表头
                builder.setHeader(header.toArray(new String[0]));
            }

            CSVPrinter csvPrinter = builder.get().print(fileWriter);
            return new CsvWriter(fileWriter, csvPrinter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}