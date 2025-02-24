-- 创建数据库
CREATE DATABASE IF NOT EXISTS `telecom-data`;

USE `telecom-data`;

-- 通话记录表
CREATE TABLE IF NOT EXISTS `call`
(
    callId             STRING,
    callerNumber       STRING,
    receiverNumber     STRING,
    callStartTime      BIGINT,
    callEndTime        BIGINT,
    callDurationMillis BIGINT,
    callDirection      STRING,
    callStatus         STRING,
    stationId          STRING
) STORED AS ORC;

-- 短信记录表
CREATE TABLE IF NOT EXISTS `sms`
(
    smsId          STRING,
    senderNumber   STRING,
    receiverNumber STRING,
    smsContent     STRING,
    sendTime       BIGINT,
    sendDirection  STRING,
    sendStatus     STRING,
    stationId      STRING
) STORED AS ORC;

-- 流量记录表
CREATE TABLE IF NOT EXISTS `traffic`
(
    sessionId             STRING,
    userNumber            STRING,
    sessionStartTime      BIGINT,
    sessionEndTime        BIGINT,
    sessionDurationMillis BIGINT,
    applicationType       STRING,
    upstreamDataVolume    BIGINT,
    downstreamDataVolume  BIGINT,
    networkTechnology     STRING,
    stationId             STRING
) STORED AS ORC;