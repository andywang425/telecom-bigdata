-- 创建数据库
CREATE DATABASE IF NOT EXISTS `telecom-data`;

-- 通话记录表
CREATE TABLE IF NOT EXISTS `telecom-data.call` (
                                                   callId STRING,
                                                   callerNumber STRING,
                                                   receiverNumber STRING,
                                                   callStartTime STRING,
                                                   callEndTime STRING,
                                                   callDurationMillis BIGINT,
                                                   callDirection STRING,
                                                   callStatus STRING,
                                                   stationId STRING
) STORED AS ORC;

-- 短信记录表
CREATE TABLE IF NOT EXISTS `telecom-data.sms` (
                                                  smsId STRING,
                                                  senderNumber STRING,
                                                  receiverNumber STRING,
                                                  smsContent STRING,
                                                  sendTime STRING,
                                                  sendDirection STRING,
                                                  sendStatus STRING,
                                                  stationId STRING
) STORED AS ORC;

-- 流量记录表
CREATE TABLE IF NOT EXISTS `telecom-data.traffic` (
                                                      sessionId STRING,
                                                      userNumber STRING,
                                                      sessionStartTime STRING,
                                                      sessionEndTime STRING,
                                                      sessionDurationMillis BIGINT,
                                                      applicationType STRING,
                                                      upstreamDataVolume BIGINT,
                                                      downstreamDataVolume BIGINT,
                                                      networkTechnology STRING,
                                                      stationId STRING
) STORED AS ORC;