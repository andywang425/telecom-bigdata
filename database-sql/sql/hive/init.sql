-- 创建数据库
CREATE DATABASE IF NOT EXISTS `telecom_data`;

USE `telecom_data`;

-- 通话记录表
CREATE TABLE IF NOT EXISTS `call`
(
    call_id              STRING,
    caller_number        STRING,
    receiver_number      STRING,
    call_start_time      TIMESTAMP,
    call_end_time        TIMESTAMP,
    call_duration_millis BIGINT,
    call_direction       STRING,
    call_status          STRING,
    station_id           STRING
) STORED AS ORC;

-- 短信记录表
CREATE TABLE IF NOT EXISTS `sms`
(
    sms_id          STRING,
    sender_number   STRING,
    receiver_number STRING,
    sms_content     STRING,
    send_time       TIMESTAMP,
    send_direction  STRING,
    send_status     STRING,
    station_id      STRING
) STORED AS ORC;

-- 流量记录表
CREATE TABLE IF NOT EXISTS `traffic`
(
    session_id              STRING,
    user_number             STRING,
    session_start_time      TIMESTAMP,
    session_end_time        TIMESTAMP,
    session_duration_millis BIGINT,
    application_type        STRING,
    upstream_data_volume    BIGINT,
    downstream_data_volume  BIGINT,
    network_technology      STRING,
    station_id              STRING
) STORED AS ORC;