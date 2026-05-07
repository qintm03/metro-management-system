package com.example.springboot.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 地铁站点实体类
 */
public class MetroStation {

    private Long id;
    private String stationName;
    private String lineId;
    private Integer sequence;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer isTransfer;
    private Integer transferCount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MetroStation() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public Integer getIsTransfer() {
        return isTransfer;
    }

    public void setIsTransfer(Integer isTransfer) {
        this.isTransfer = isTransfer;
    }

    public Integer getTransferCount() {
        return transferCount;
    }

    public void setTransferCount(Integer transferCount) {
        this.transferCount = transferCount;
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
        return "MetroStation{" +
                "id=" + id +
                ", stationName='" + stationName + '\'' +
                ", lineId='" + lineId + '\'' +
                ", sequence=" + sequence +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", isTransfer=" + isTransfer +
                ", transferCount=" + transferCount +
                ", status=" + status +
                '}';
    }
}
