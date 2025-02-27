package com.example.telecom.generator;

import com.example.telecom.config.*;
import com.example.telecom.enums.*;
import com.example.telecom.model.CallRecord;
import com.example.telecom.model.SmsRecord;
import com.example.telecom.model.StationInfo;
import com.example.telecom.model.TrafficRecord;
import com.example.telecom.util.DateTimeUtils;
import com.example.telecom.util.RandomTools;
import org.apache.commons.math3.util.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class RandomTelecomDataGenerator {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<String> phoneNumbers;
    private final List<String> baseStations;
    private final Map<String, UserPattern> userProfiles;
    private final Map<String, StationInfo> baseStationInfos;
    private final List<String> smsMessages;

    public RandomTelecomDataGenerator(DateRange dateRange, Map<String, UserPattern> userProfiles, Map<String, StationInfo> baseStationInfos, List<String> smsMessages) {
        this.startDate = LocalDate.parse(dateRange.getStart());
        this.endDate = LocalDate.parse(dateRange.getEnd());
        this.phoneNumbers = new ArrayList<>(userProfiles.keySet());
        this.baseStations = new ArrayList<>(baseStationInfos.keySet());
        this.userProfiles = userProfiles;
        this.baseStationInfos = baseStationInfos;
        this.smsMessages = smsMessages;
    }

    /**
     * 生成一对通话记录（来电和去电）
     */
    public List<CallRecord> generateCallRecordPair() {
        // 生成一对通话ID
        String incomingCallId = UUID.randomUUID().toString();
        String outgoingCallId = UUID.randomUUID().toString();
        // 生成来电和去电号码
        List<String> callerReceiver = RandomTools.getRandomElements(phoneNumbers, 2);
        // 打电话的人
        String caller = callerReceiver.get(0);
        // 生成通话开始时间
        LocalDateTime callStart = generateCallStartTime(caller);
        // 接电话的人
        String receiver = callerReceiver.get(1);
        // 生成通话持续时间
        long durationMillis = generateCallDurationForUser(caller);
        // 计算通话结束时间
        LocalDateTime callEnd = callStart.plus(durationMillis, ChronoUnit.MILLIS);
        // 生成打电话的人和接电话的人所在的基站（可以是同一个）
        String callerStationId = RandomTools.getRandomElement(baseStations);
        String receiverStationId = RandomTools.getRandomElement(baseStations);
        // 生成通话状态
        CallStatus status = generateCallStatus(callerStationId, receiverStationId, receiver);

        CallRecord incomingCall = CallRecord.builder()
                .callId(incomingCallId)
                .callerNumber(caller)
                .receiverNumber(receiver)
                .callStartTime(callStart)
                .callEndTime(callEnd)
                .callDuration(durationMillis)
                .callDirection(CallDirection.INCOMING)
                .callStatus(status)
                .stationId(callerStationId)
                .build();

        CallRecord outgoingCall = CallRecord.builder()
                .callId(outgoingCallId)
                .callerNumber(caller)
                .receiverNumber(receiver)
                .callStartTime(callStart)
                .callEndTime(callEnd)
                .callDuration(durationMillis)
                .callDirection(CallDirection.OUTGOING)
                .callStatus(status)
                .stationId(receiverStationId)
                .build();

        return Arrays.asList(incomingCall, outgoingCall);
    }

    /**
     * 生成通话状态
     *
     * @param callerStationId   打电话的人所在基站ID
     * @param receiverStationId 接电话的人所在基站ID
     * @param receiver          去电手机号
     */
    private CallStatus generateCallStatus(String callerStationId, String receiverStationId, String receiver) {
        // 若任一基站故障，返回失败
        if (RandomTools.rateTest(baseStationInfos.get(callerStationId).getFailureProbability())) {
            return CallStatus.FAILED;
        }

        if (RandomTools.rateTest(baseStationInfos.get(receiverStationId).getFailureProbability())) {
            return CallStatus.FAILED;
        }

        // 根据去电用户的模式，生成通话状态
        List<CallStatusWeight> callStatusWeights = userProfiles.get(receiver).getCall().getStatusWeight();
        List<Pair<CallStatus, Double>> callStatusWeightPMF = new ArrayList<>();
        for (CallStatusWeight callStatusWeight : callStatusWeights) {
            callStatusWeightPMF.add(new Pair<>(callStatusWeight.getName(), callStatusWeight.getWeight()));
        }

        return RandomTools.weightedRandom(callStatusWeightPMF);
    }

    /**
     * 生成一对短信记录（发短信和收短信）
     */
    public List<SmsRecord> generateSmsRecordPair() {
        // 生成一对短信ID
        String sentSmsId = UUID.randomUUID().toString();
        String receivedSmsId = UUID.randomUUID().toString();
        // 生成发短信和收短信的手机号
        List<String> callerReceiver = RandomTools.getRandomElements(phoneNumbers, 2);
        // 发短信的人
        String sender = callerReceiver.get(0);
        // 收短信的人
        String receiver = callerReceiver.get(1);
        // 生成短信发送时间
        LocalDateTime sendTime = generateSmsSendTime(sender);
        // 生成发短信的人和收短信的人所在的基站（可以是同一个）
        String callerStationId = RandomTools.getRandomElement(baseStations);
        String receiverStationId = RandomTools.getRandomElement(baseStations);
        // 生成短信状态
        SmsStatus status = generateSmsStatus(callerStationId, receiverStationId);
        // 生成短信内容
        String smsContent = RandomTools.getRandomElement(smsMessages);

        SmsRecord sentSms = SmsRecord.builder()
                .smsId(sentSmsId)
                .senderNumber(sender)
                .receiverNumber(receiver)
                .smsContent(smsContent)
                .sendTime(sendTime)
                .sendDirection(SmsDirection.SENT)
                .sendStatus(status)
                .stationId(callerStationId)
                .build();

        SmsRecord receivedSms = SmsRecord.builder()
                .smsId(receivedSmsId)
                .senderNumber(sender)
                .receiverNumber(receiver)
                .smsContent(smsContent)
                .sendTime(sendTime)
                .sendDirection(SmsDirection.RECEIVED)
                .sendStatus(status)
                .stationId(receiverStationId)
                .build();

        return Arrays.asList(sentSms, receivedSms);
    }

    /**
     * 生成短信状态
     *
     * @param callerStationId   发短信的人所在基站ID
     * @param receiverStationId 接短信的人所在基站ID
     */
    private SmsStatus generateSmsStatus(String callerStationId, String receiverStationId) {
        if (RandomTools.rateTest(baseStationInfos.get(callerStationId).getFailureProbability())) {
            return SmsStatus.FAILED_TO_SEND;
        }

        if (RandomTools.rateTest(baseStationInfos.get(receiverStationId).getFailureProbability())) {
            return SmsStatus.FAILED_TO_RECEIVE;
        }

        return SmsStatus.SUCCESSFUL;
    }

    /**
     * 生成一条随机流量记录（以会话为单位）
     */
    public TrafficRecord generateRandomTrafficRecord() {
        // 生成会话ID
        String sessionId = UUID.randomUUID().toString();
        // 生成用户手机号
        String userNumber = RandomTools.getRandomElement(phoneNumbers);
        // 生成会话开始时间
        LocalDateTime sessionStart = generateTrafficSessionStartTime(userNumber);
        // 生成会话持续时间
        long durationMs = generateSessionDurationForUser(userNumber);
        // 计算会话结束时间
        LocalDateTime sessionEnd = sessionStart.plus(durationMs, ChronoUnit.MILLIS);
        // 生成会话的上行数据量
        long upstream = generateUpStreamDataVolumeForUser(userNumber, durationMs);
        // 生成会话的下行数据量
        long downstream = generateDownStreamDataVolumeForUser(userNumber, durationMs);
        // 生成用户所在基站ID
        String stationId = RandomTools.getRandomElement(baseStations);
        // 生成会话使用的应用类型
        ApplicationType applicationType = generateApplicationType(userNumber);
        // 生成会话使用的网络技术
        String networkTech = baseStationInfos.get(stationId).getTechnology();

        return TrafficRecord.builder()
                .sessionId(sessionId)
                .userNumber(userNumber)
                .sessionStartTime(sessionStart)
                .sessionEndTime(sessionEnd)
                .sessionDuration(durationMs)
                .applicationType(applicationType)
                .upstreamDataVolume(upstream)
                .downstreamDataVolume(downstream)
                .networkTechnology(networkTech)
                .stationId(stationId)
                .build();
    }

    /**
     * 生成流量的应用类型
     *
     * @param phoneNumber 手机号
     */
    private ApplicationType generateApplicationType(String phoneNumber) {
        List<UserPreference> preferences = userProfiles.get(phoneNumber).getTraffic().getPreference();

        List<Pair<ApplicationType, Double>> preferencesPMF = new ArrayList<>();
        for (UserPreference preference : preferences) {
            preferencesPMF.add(new Pair<>(preference.getName(), preference.getWeight()));
        }

        return RandomTools.weightedRandom(preferencesPMF);
    }

    /**
     * 生成通话开始时间
     *
     * @param phoneNumber 手机号
     */
    private LocalDateTime generateCallStartTime(String phoneNumber) {
        Distribution distribution = userProfiles.get(phoneNumber).getCall().getStartTime();
        double hour = RandomTools.getDistributionRandom(distribution);
        LocalTime localTime = DateTimeUtils.hourToLocalTime(hour);
        LocalDate localDate = DateTimeUtils.getRandomDate(startDate, endDate);
        return LocalDateTime.of(localDate, localTime);
    }

    /**
     * 生成短信发送时间
     *
     * @param phoneNumber 手机号
     */
    private LocalDateTime generateSmsSendTime(String phoneNumber) {
        Distribution distribution = userProfiles.get(phoneNumber).getSms().getStartTime();
        double hour = RandomTools.getDistributionRandom(distribution);
        LocalTime localTime = DateTimeUtils.hourToLocalTime(hour);
        LocalDate localDate = DateTimeUtils.getRandomDate(startDate, endDate);
        return LocalDateTime.of(localDate, localTime);
    }

    /**
     * 生成流量会话开始时间
     *
     * @param phoneNumber 手机号
     */
    private LocalDateTime generateTrafficSessionStartTime(String phoneNumber) {
        Distribution distribution = userProfiles.get(phoneNumber).getTraffic().getStartTime();
        double hour = RandomTools.getDistributionRandom(distribution);
        LocalTime localTime = DateTimeUtils.hourToLocalTime(hour);
        LocalDate localDate = DateTimeUtils.getRandomDate(startDate, endDate);
        return LocalDateTime.of(localDate, localTime);
    }

    /**
     * 生成通话持续时间
     *
     * @param phoneNumber 手机号
     */
    private long generateCallDurationForUser(String phoneNumber) {
        Distribution distribution = userProfiles.get(phoneNumber).getCall().getDuration();
        double seconds = RandomTools.getDistributionRandom(distribution);
        return (long) (seconds * 1000);
    }

    /**
     * 生成流量会话持续时间
     *
     * @param phoneNumber 手机号
     */
    private long generateSessionDurationForUser(String phoneNumber) {
        Distribution distribution = userProfiles.get(phoneNumber).getTraffic().getDuration();
        double seconds = RandomTools.getDistributionRandom(distribution);
        return (long) (seconds * 1000);
    }

    /**
     * 生成流量会话的上行数据量
     *
     * @param phoneNumber     手机号
     * @param sessionDuration 会话持续时间
     */
    private long generateUpStreamDataVolumeForUser(String phoneNumber, long sessionDuration) {
        Distribution distribution = userProfiles.get(phoneNumber).getTraffic().getUpstreamRate();
        double bytesPerSecond = RandomTools.getDistributionRandom(distribution);
        return (long) (bytesPerSecond * sessionDuration / 1000);
    }

    /**
     * 生成流量会话的下行数据量
     *
     * @param phoneNumber     手机号
     * @param sessionDuration 会话持续时间
     */
    private long generateDownStreamDataVolumeForUser(String phoneNumber, long sessionDuration) {
        Distribution distribution = userProfiles.get(phoneNumber).getTraffic().getDownstreamRate();
        double bytesPerSecond = RandomTools.getDistributionRandom(distribution);
        return (long) (bytesPerSecond * sessionDuration / 1000);
    }
}