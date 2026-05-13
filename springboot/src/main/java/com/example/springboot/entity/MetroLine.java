package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 地铁线路实体类
 */
public class MetroLine {

    private Long id;
    private String lineId;
    private String lineName;
    private String lineCode;
    private String color;
    private String startStation;
    private String endStation;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime firstTrainTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime lastTrainTime;
    private Integer interval;
    private Integer segmentTime;
    private Integer dwellTime;
    private Integer trainCount;
    private BigDecimal distance;
    private Integer stationCount;
    private String path;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MetroLine() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public void setEndStation(String endStation) {
        this.endStation = endStation;
    }

    public LocalTime getFirstTrainTime() {
        return firstTrainTime;
    }

    public void setFirstTrainTime(LocalTime firstTrainTime) {
        this.firstTrainTime = firstTrainTime;
    }

    public LocalTime getLastTrainTime() {
        return lastTrainTime;
    }

    public void setLastTrainTime(LocalTime lastTrainTime) {
        this.lastTrainTime = lastTrainTime;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getSegmentTime() {
        return segmentTime;
    }

    public void setSegmentTime(Integer segmentTime) {
        this.segmentTime = segmentTime;
    }

    public Integer getDwellTime() {
        return dwellTime;
    }

    public void setDwellTime(Integer dwellTime) {
        this.dwellTime = dwellTime;
    }

    public Integer getTrainCount() {
        return trainCount;
    }

    public void setTrainCount(Integer trainCount) {
        this.trainCount = trainCount;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public Integer getStationCount() {
        return stationCount;
    }

    public void setStationCount(Integer stationCount) {
        this.stationCount = stationCount;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "MetroLine{" +
                "id=" + id +
                ", lineId='" + lineId + '\'' +
                ", lineName='" + lineName + '\'' +
                ", lineCode='" + lineCode + '\'' +
                ", color='" + color + '\'' +
                ", startStation='" + startStation + '\'' +
                ", endStation='" + endStation + '\'' +
                ", firstTrainTime=" + firstTrainTime +
                ", lastTrainTime=" + lastTrainTime +
                ", interval=" + interval +
                ", segmentTime=" + segmentTime +
                ", dwellTime=" + dwellTime +
                ", trainCount=" + trainCount +
                ", distance=" + distance +
                ", stationCount=" + stationCount +
                ", status=" + status +
                '}';
    }
}
