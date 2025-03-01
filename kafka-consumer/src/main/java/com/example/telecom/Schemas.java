package com.example.telecom;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class Schemas {
    public static final StructType CALL = new StructType(new StructField[]{
            new StructField("callId", DataTypes.StringType, true, Metadata.empty()),
            new StructField("callerNumber", DataTypes.StringType, true, Metadata.empty()),
            new StructField("receiverNumber", DataTypes.StringType, true, Metadata.empty()),
            new StructField("callStartTime", DataTypes.LongType, true, Metadata.empty()),
            new StructField("callEndTime", DataTypes.LongType, true, Metadata.empty()),
            new StructField("callDurationMillis", DataTypes.LongType, true, Metadata.empty()),
            new StructField("callDirection", DataTypes.StringType, true, Metadata.empty()),
            new StructField("callStatus", DataTypes.StringType, true, Metadata.empty()),
            new StructField("stationId", DataTypes.StringType, true, Metadata.empty())

    });

    public static final StructType SMS = new StructType(new StructField[]{
            new StructField("smsId", DataTypes.StringType, true, Metadata.empty()),
            new StructField("senderNumber", DataTypes.StringType, true, Metadata.empty()),
            new StructField("receiverNumber", DataTypes.StringType, true, Metadata.empty()),
            new StructField("smsContent", DataTypes.StringType, true, Metadata.empty()),
            new StructField("sendTime", DataTypes.LongType, true, Metadata.empty()),
            new StructField("sendDirection", DataTypes.StringType, true, Metadata.empty()),
            new StructField("sendStatus", DataTypes.StringType, true, Metadata.empty()),
            new StructField("stationId", DataTypes.StringType, true, Metadata.empty())
    });

    public static final StructType TRAFFIC = new StructType(new StructField[]{
            new StructField("sessionId", DataTypes.StringType, true, Metadata.empty()),
            new StructField("userNumber", DataTypes.StringType, true, Metadata.empty()),
            new StructField("sessionStartTime", DataTypes.LongType, true, Metadata.empty()),
            new StructField("sessionEndTime", DataTypes.LongType, true, Metadata.empty()),
            new StructField("sessionDurationMillis", DataTypes.LongType, true, Metadata.empty()),
            new StructField("applicationType", DataTypes.StringType, true, Metadata.empty()),
            new StructField("upstreamDataVolume", DataTypes.LongType, true, Metadata.empty()),
            new StructField("downstreamDataVolume", DataTypes.LongType, true, Metadata.empty()),
            new StructField("networkTechnology", DataTypes.StringType, true, Metadata.empty()),
            new StructField("stationId", DataTypes.StringType, true, Metadata.empty())
    });
}
