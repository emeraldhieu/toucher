package com.emeraldhieu.toucher.touch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"started", "finished", "durationSeconds",
    "fromStopId", "toStopId", "chargeAmount", "companyId", "busId", "hashedPan", "tripStatus"})
public class ResultLine {

    @JsonProperty("Started")
    private final String started;

    @JsonProperty("Finished")
    private final String finished;

    @JsonProperty("DurationSec")
    private final Long durationSeconds;

    @JsonProperty("FromStopId")
    private final String fromStopId;

    @JsonProperty("ToStopId")
    private final String toStopId;

    @JsonProperty("ChargeAmount")
    private final Double chargeAmount;

    @JsonProperty("CompanyId")
    private final String companyId;

    @JsonProperty("BusId")
    private final String busId;

    @JsonProperty("HashedPan")
    private final String hashedPan;

    @JsonProperty("Status")
    private final TripStatus tripStatus;

    ResultLine(String started, String finished, Long durationSeconds, String fromStopId, String toStopId, Double chargeAmount, String companyId, String busId, String hashedPan, TripStatus tripStatus) {
        this.started = started;
        this.finished = finished;
        this.durationSeconds = durationSeconds;
        this.fromStopId = fromStopId;
        this.toStopId = toStopId;
        this.chargeAmount = chargeAmount;
        this.companyId = companyId;
        this.busId = busId;
        this.hashedPan = hashedPan;
        this.tripStatus = tripStatus;
    }

    public static ResultLineBuilder builder() {
        return new ResultLineBuilder();
    }

    public String getStarted() {
        return this.started;
    }

    public String getFinished() {
        return this.finished;
    }

    public Long getDurationSeconds() {
        return this.durationSeconds;
    }

    public String getFromStopId() {
        return this.fromStopId;
    }

    public String getToStopId() {
        return this.toStopId;
    }

    public Double getChargeAmount() {
        return this.chargeAmount;
    }

    public String getCompanyId() {
        return this.companyId;
    }

    public String getBusId() {
        return this.busId;
    }

    public String getHashedPan() {
        return this.hashedPan;
    }

    public TripStatus getTripStatus() {
        return this.tripStatus;
    }

    public static class ResultLineBuilder {
        private String started;
        private String finished;
        private Long durationSeconds;
        private String fromStopId;
        private String toStopId;
        private Double chargeAmount;
        private String companyId;
        private String busId;
        private String hashedPan;
        private TripStatus tripStatus;

        ResultLineBuilder() {
        }

        public ResultLineBuilder started(String started) {
            this.started = started;
            return this;
        }

        public ResultLineBuilder finished(String finished) {
            this.finished = finished;
            return this;
        }

        public ResultLineBuilder durationSeconds(Long durationSeconds) {
            this.durationSeconds = durationSeconds;
            return this;
        }

        public ResultLineBuilder fromStopId(String fromStopId) {
            this.fromStopId = fromStopId;
            return this;
        }

        public ResultLineBuilder toStopId(String toStopId) {
            this.toStopId = toStopId;
            return this;
        }

        public ResultLineBuilder chargeAmount(Double chargeAmount) {
            this.chargeAmount = chargeAmount;
            return this;
        }

        public ResultLineBuilder companyId(String companyId) {
            this.companyId = companyId;
            return this;
        }

        public ResultLineBuilder busId(String busId) {
            this.busId = busId;
            return this;
        }

        public ResultLineBuilder hashedPan(String hashedPan) {
            this.hashedPan = hashedPan;
            return this;
        }

        public ResultLineBuilder tripStatus(TripStatus tripStatus) {
            this.tripStatus = tripStatus;
            return this;
        }

        public ResultLine build() {
            return new ResultLine(started, finished, durationSeconds, fromStopId, toStopId, chargeAmount, companyId, busId, hashedPan, tripStatus);
        }

        public String toString() {
            return "ResultLine.ResultLineBuilder(started=" + this.started + ", finished=" + this.finished + ", durationSeconds=" + this.durationSeconds + ", fromStopId=" + this.fromStopId + ", toStopId=" + this.toStopId + ", chargeAmount=" + this.chargeAmount + ", companyId=" + this.companyId + ", busId=" + this.busId + ", hashedPan=" + this.hashedPan + ", tripStatus=" + this.tripStatus + ")";
        }
    }
}
