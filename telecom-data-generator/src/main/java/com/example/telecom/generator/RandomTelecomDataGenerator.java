package com.example.telecom.generator;

import com.example.telecom.config.*;
import com.example.telecom.enums.*;
import com.example.telecom.model.CallRecord;
import com.example.telecom.model.SmsRecord;
import com.example.telecom.model.StationInfo;
import com.example.telecom.model.TrafficRecord;
import com.example.telecom.util.DateTimeUtils;
import com.example.telecom.util.RandomGenerator;
import org.apache.commons.math3.util.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public List<CallRecord> generateCallRecordPair() {
        String callId = UUID.randomUUID().toString();

        List<String> callerReceiver = RandomGenerator.getRandomElements(phoneNumbers, 2);

        String caller = callerReceiver.get(0);

        LocalDateTime callStart = generateStartTimeForUser(caller);

        String receiver = callerReceiver.get(1);

        long durationMillis = generateCallDurationForUser(caller);

        LocalDateTime callEnd = callStart.plus(durationMillis, ChronoUnit.MILLIS);

        String callerStationId = RandomGenerator.getRandomElement(baseStations);
        String receiverStationId = RandomGenerator.getRandomElement(baseStations);

        CallStatus status = generateRealisticCallStatus(List.of(callerStationId, receiverStationId), receiver);

        CallRecord incomingCall = CallRecord.builder()
                .callId(callId)
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
                .callId(callId)
                .callerNumber(receiver)
                .receiverNumber(caller)
                .callStartTime(callStart)
                .callEndTime(callEnd)
                .callDuration(durationMillis)
                .callDirection(CallDirection.OUTGOING)
                .callStatus(status)
                .stationId(receiverStationId)
                .build();

        return List.of(incomingCall, outgoingCall);
    }

    private CallStatus generateRealisticCallStatus(List<String> baseStations, String receiver) {
        for (String baseStation : baseStations) {
            if (RandomGenerator.rateTest(baseStationInfos.get(baseStation).getFailureProbability())) {
                return CallStatus.FAILED;
            }
        }

        List<CallStatusWeight> callStatusWeights = userProfiles.get(receiver).getCall().getStatusWeight();
        List<Pair<CallStatus, Double>> callStatusWeightPMF = new ArrayList<>();
        for (CallStatusWeight callStatusWeight : callStatusWeights) {
            callStatusWeightPMF.add(new Pair<>(callStatusWeight.getName(), callStatusWeight.getWeight()));
        }

        return RandomGenerator.weightedRandom(callStatusWeightPMF);
    }

    public List<SmsRecord> generateSmsRecordPair() {
        String smsId = UUID.randomUUID().toString();

        List<String> callerReceiver = RandomGenerator.getRandomElements(phoneNumbers, 2);

        String sender = callerReceiver.get(0);

        String receiver = callerReceiver.get(1);

        LocalDateTime sendTime = generateStartTimeForUser(sender);

        String callerStationId = RandomGenerator.getRandomElement(baseStations);
        String receiverStationId = RandomGenerator.getRandomElement(baseStations);

        SmsStatus status = generateSmsStatus(List.of(callerStationId, receiverStationId));

        String smsContent = RandomGenerator.getRandomElement(smsMessages);
        
        SmsRecord sentSms = SmsRecord.builder()
                .smsId(smsId)
                .senderNumber(sender)
                .receiverNumber(receiver)
                .smsContent(smsContent)
                .sendTime(sendTime)
                .sendDirection(SmsDirection.SENT)
                .sendStatus(status)
                .stationId(callerStationId)
                .build();

        SmsRecord receivedSms = SmsRecord.builder()
                .smsId(smsId)
                .senderNumber(sender)
                .receiverNumber(receiver)
                .smsContent(smsContent)
                .sendTime(sendTime)
                .sendDirection(SmsDirection.RECEIVED)
                .sendStatus(status)
                .stationId(receiverStationId)
                .build();

        return List.of(sentSms, receivedSms);
    }

    private SmsStatus generateSmsStatus(List<String> baseStations) {
        if (RandomGenerator.rateTest(baseStationInfos.get(baseStations.get(0)).getFailureProbability())) {
            return SmsStatus.FAILED_TO_SEND;
        }

        if (RandomGenerator.rateTest(baseStationInfos.get(baseStations.get(1)).getFailureProbability())) {
            return SmsStatus.FAILED_TO_RECEIVE;
        }

        return SmsStatus.SUCCESSFUL;
    }

    public TrafficRecord generateRandomTrafficRecord() {
        String sessionId = UUID.randomUUID().toString();

        String userNumber = RandomGenerator.getRandomElement(phoneNumbers);

        LocalDateTime sessionStart = generateStartTimeForUser(userNumber);

        long durationMs = generateSessionDurationForUser(userNumber);
        LocalDateTime sessionEnd = sessionStart.plus(durationMs, ChronoUnit.MILLIS);

        long upstream = generateUpStreamDataVolumeForUser(userNumber, durationMs);

        long downstream = generateDownStreamDataVolumeForUser(userNumber, durationMs);

        String stationId = RandomGenerator.getRandomElement(baseStations);

        ApplicationType applicationType = generateApplicationType(userNumber);

        String networkTech =  baseStationInfos.get(stationId).getTechnology();

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

    private ApplicationType generateApplicationType(String phoneNumber) {
        List<UserPreference> preferences = userProfiles.get(phoneNumber).getTraffic().getPreference();

        List<Pair<ApplicationType, Double>> preferencesPMF = new ArrayList<>();
        for (UserPreference preference : preferences) {
            preferencesPMF.add(new Pair<>(preference.getName(), preference.getWeight()));
        }

        return RandomGenerator.weightedRandom(preferencesPMF);
    }

    private LocalDateTime generateStartTimeForUser(String phoneNumber) {
        Distribution distribution = userProfiles.get(phoneNumber).getCall().getStartTime();
        double hour = RandomGenerator.getDistributionRandom(distribution);
        LocalTime localTime = DateTimeUtils.hourToLocalTime(hour);
        LocalDate localDate = DateTimeUtils.getRandomDate(startDate, endDate);
        return LocalDateTime.of(localDate, localTime);
    }

    private long generateCallDurationForUser(String phoneNumber) {
        Distribution distribution = userProfiles.get(phoneNumber).getCall().getDuration();
        double seconds = RandomGenerator.getDistributionRandom(distribution);
        return (long) (seconds * 1000);
    }

    private long generateSessionDurationForUser(String phoneNumber) {
        Distribution distribution = userProfiles.get(phoneNumber).getTraffic().getDuration();
        double seconds = RandomGenerator.getDistributionRandom(distribution);
        return (long) (seconds * 1000);
    }

    private long generateUpStreamDataVolumeForUser(String phoneNumber, long sessionDuration) {
        Distribution distribution = userProfiles.get(phoneNumber).getTraffic().getUpstreamRate();
        double bytesPerSecond  = RandomGenerator.getDistributionRandom(distribution);
        return (long) (bytesPerSecond * sessionDuration / 1000);
    }

    private long generateDownStreamDataVolumeForUser(String phoneNumber, long sessionDuration) {
        Distribution distribution = userProfiles.get(phoneNumber).getTraffic().getDownstreamRate();
        double bytesPerSecond  = RandomGenerator.getDistributionRandom(distribution);
        return (long) (bytesPerSecond * sessionDuration / 1000);
    }
}