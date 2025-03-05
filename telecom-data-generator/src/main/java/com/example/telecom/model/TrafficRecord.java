package com.example.telecom.model;

import com.example.telecom.enums.ApplicationType;
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
public class TrafficRecord implements Record {
    private String sessionId;
    private String userNumber;
    private LocalDateTime sessionStartTime;
    private LocalDateTime sessionEndTime;
    private long sessionDuration;            // milliseconds
    private ApplicationType applicationType;
    private long upstreamDataVolume;         // bytes
    private long downstreamDataVolume;       // bytes
    private String networkTechnology;
    private String stationId;

    @Override
    public Object[] getRecord() {
        return new Object[]{sessionId, userNumber, DateTimeUtils.formatDateTime(sessionStartTime), DateTimeUtils.formatDateTime(sessionEndTime), sessionDuration, applicationType, upstreamDataVolume, downstreamDataVolume, networkTechnology, stationId};
    }
}