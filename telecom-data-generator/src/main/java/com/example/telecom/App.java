package com.example.telecom;

import com.example.telecom.config.*;
import com.example.telecom.generator.RandomTelecomDataGenerator;
import com.example.telecom.model.StationInfo;
import com.example.telecom.util.RandomTools;
import com.example.telecom.util.ResourceLoader;
import com.example.telecom.writer.CsvWriter;
import com.example.telecom.writer.CsvWriterFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j(topic = "App")
public class App {
    public static void main(String[] args) {
        AppConfig config = ResourceLoader.loadYaml("config.yaml", AppConfig.class);

        RecordNumber recordNumber = config.getRecordNumber();
        DateRange dateRange = config.getDateRange();
        log.info("配置已读取，将输出{}条数据，日期范围{} ~ {}", recordNumber.getCall() * 2 + recordNumber.getSms() * 2 + recordNumber.getTraffic(), dateRange.getStart(), dateRange.getEnd());

        List<String> phoneNumbers = ResourceLoader.loadTextLines("phone_numbers.txt");
        List<String> baseStations = ResourceLoader.loadTextLines("base_stations.txt");
        List<String> smsMessages = ResourceLoader.loadTextLines("sms_messages.txt");

        log.info("手机号共{}个，基站共{}个", phoneNumbers.size(), baseStations.size());

        // 给用户分配模式
        List<UserPattern> userPatterns = config.getPatterns().getUser();
        List<Pair<UserPattern, Double>> userPatternsPMF = userPatterns.stream().map(userPattern -> new Pair<>(userPattern, userPattern.getWeight())).collect(Collectors.toList());

        Map<String, UserPattern> userProfiles = new HashMap<>();
        for (String phone : phoneNumbers) {
            UserPattern userPattern = RandomTools.weightedRandom(userPatternsPMF);
            userProfiles.put(phone, userPattern);
            log.info("用户{}被分配为【{}】模式", phone, userPattern.getName());
        }

        // 给基站分配模式
        Map<String, StationInfo> baseStationInfos = new HashMap<>();
        StationPattern stationPattern = config.getPatterns().getStation();

        // 基站失败率
        List<StationFailureInfo> stationFailureInfoList = stationPattern.getFailureInfo();
        List<Pair<StationFailureInfo, Double>> stationFailureProbabilityPMF = new ArrayList<>();

        for (StationFailureInfo stationFailureInfo : stationFailureInfoList) {
            stationFailureProbabilityPMF.add(new Pair<>(stationFailureInfo, stationFailureInfo.getWeight()));
        }

        for (String baseStation : baseStations) {
            StationFailureInfo stationFailureInfo = RandomTools.weightedRandom(stationFailureProbabilityPMF);
            StationInfo stationInfo = new StationInfo();
            stationInfo.setFailureProbability(stationFailureInfo.getProbability());
            baseStationInfos.put(baseStation, stationInfo);
            log.info("基站{}被分配为【{}】失败率", baseStation, stationFailureInfo.getName());
        }

        // 基站使用的技术
        Map<String, Double> technologyWeight = stationPattern.getTechnologyWeight();
        List<Pair<String, Double>> stationTechnologyPMF = new ArrayList<>();

        for (Map.Entry<String, Double> entry : technologyWeight.entrySet()) {
            stationTechnologyPMF.add(new Pair<>(entry.getKey(), entry.getValue()));
        }

        for (Map.Entry<String, StationInfo> entry : baseStationInfos.entrySet()) {
            String technology = RandomTools.weightedRandom(stationTechnologyPMF);
            StationInfo stationInfo = entry.getValue();
            stationInfo.setTechnology(technology);
            log.info("基站{}被分配为【{}】技术", entry.getKey(), technology);
        }

        log.info("开始生成随机电信数据");

        // 生成随机电信数据并写入CSV
        RandomTelecomDataGenerator generator = new RandomTelecomDataGenerator(dateRange, userProfiles, baseStationInfos, smsMessages);

        CsvWriterFactory csvWriterFactory = new CsvWriterFactory();
        Output output = config.getOutput();
        Path path = output.getPath();

        CsvWriter callWriter = csvWriterFactory.getCsvWriter(path.getCall(), output.isAppend());
        CsvWriter smsWriter = csvWriterFactory.getCsvWriter(path.getSms(), output.isAppend());
        CsvWriter trafficWriter = csvWriterFactory.getCsvWriter(path.getTraffic(), output.isAppend());

        log.info("开始写入通话记录");
        for (int i = 0; i < recordNumber.getCall(); i++) {
            callWriter.write(generator.generateCallRecordPair());
        }
        callWriter.close();

        log.info("开始写入短信记录");
        for (int i = 0; i < recordNumber.getSms(); i++) {
            smsWriter.write(generator.generateSmsRecordPair());
        }
        smsWriter.close();

        log.info("开始写入流量记录");
        for (int i = 0; i < recordNumber.getTraffic(); i++) {
            trafficWriter.write(generator.generateRandomTrafficRecord());
        }
        trafficWriter.close();

        log.info("数据写入完毕");
    }
}