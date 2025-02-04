package com.example.telecom.model;

import com.example.telecom.enums.SmsDirection;
import com.example.telecom.enums.SmsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsRecord implements Record {

    private String smsId;                 // UUID
    private String senderNumber;
    private String receiverNumber;
    private String smsContent;
    private LocalDateTime sendTime;
    private SmsDirection sendDirection;
    private SmsStatus sendStatus;
    private String stationId;

    @Override
    public Object[] getRecord() {
        return new Object[]{smsId, senderNumber, receiverNumber, smsContent, sendTime, sendDirection, sendStatus, stationId};
    }
}