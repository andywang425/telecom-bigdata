package com.example.telecom;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class Schemas {
    public static final StructType CALL = new StructType(new StructField[]{
            new StructField("call_id", DataTypes.StringType, true, Metadata.empty()),
            new StructField("caller_number", DataTypes.StringType, true, Metadata.empty()),
            new StructField("receiver_number", DataTypes.StringType, true, Metadata.empty()),
            new StructField("call_start_time", DataTypes.TimestampType, true, Metadata.empty()),
            new StructField("call_end_time", DataTypes.TimestampType, true, Metadata.empty()),
            new StructField("call_duration_millis", DataTypes.LongType, true, Metadata.empty()),
            new StructField("call_direction", DataTypes.StringType, true, Metadata.empty()),
            new StructField("call_status", DataTypes.StringType, true, Metadata.empty()),
            new StructField("station_id", DataTypes.StringType, true, Metadata.empty())
    });

    public static final StructType SMS = new StructType(new StructField[]{
            new StructField("sms_id", DataTypes.StringType, true, Metadata.empty()),
            new StructField("sender_number", DataTypes.StringType, true, Metadata.empty()),
            new StructField("receiver_number", DataTypes.StringType, true, Metadata.empty()),
            new StructField("sms_content", DataTypes.StringType, true, Metadata.empty()),
            new StructField("send_time", DataTypes.TimestampType, true, Metadata.empty()),
            new StructField("send_direction", DataTypes.StringType, true, Metadata.empty()),
            new StructField("send_status", DataTypes.StringType, true, Metadata.empty()),
            new StructField("station_id", DataTypes.StringType, true, Metadata.empty())
    });

    public static final StructType TRAFFIC = new StructType(new StructField[]{
            new StructField("session_id", DataTypes.StringType, true, Metadata.empty()),
            new StructField("user_number", DataTypes.StringType, true, Metadata.empty()),
            new StructField("session_start_time", DataTypes.TimestampType, true, Metadata.empty()),
            new StructField("session_end_time", DataTypes.TimestampType, true, Metadata.empty()),
            new StructField("session_duration_millis", DataTypes.LongType, true, Metadata.empty()),
            new StructField("application_type", DataTypes.StringType, true, Metadata.empty()),
            new StructField("upstream_data_volume", DataTypes.LongType, true, Metadata.empty()),
            new StructField("downstream_data_volume", DataTypes.LongType, true, Metadata.empty()),
            new StructField("network_technology", DataTypes.StringType, true, Metadata.empty()),
            new StructField("station_id", DataTypes.StringType, true, Metadata.empty())
    });
}
