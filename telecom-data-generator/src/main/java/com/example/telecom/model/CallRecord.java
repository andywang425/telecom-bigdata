package com.example.telecom.model;

import com.example.telecom.enums.CallDirection;
import com.example.telecom.enums.CallStatus;
import com.example.telecom.util.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallRecord implements Record {
    private String callId;                 // UUID
    private String callerNumber;
    private String receiverNumber;
    private LocalDateTime callStartTime;
    private LocalDateTime callEndTime;
    private long callDuration;             // milliseconds
    private CallDirection callDirection;
    private CallStatus callStatus;
    private String stationId;

    @Override
    public Object[] getRecord() {
        return new Object[]{callId, callerNumber, receiverNumber, DateTimeUtils.localDateTimeToMillis(callStartTime), DateTimeUtils.localDateTimeToMillis(callEndTime), callDuration, callDirection, callStatus, stationId};
    }
}