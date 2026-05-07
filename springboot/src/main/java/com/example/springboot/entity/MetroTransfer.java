package com.example.springboot.entity;

import java.time.LocalDateTime;

/**
 * 地铁换乘关系实体类
 */
public class MetroTransfer {

    private Long id;
    private String stationName;
    private String fromLineId;
    private String toLineId;
    private String toLineName;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MetroTransfer() {}

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

    public String getFromLineId() {
        return fromLineId;
    }

    public void setFromLineId(String fromLineId) {
        this.fromLineId = fromLineId;
    }

    public String getToLineId() {
        return toLineId;
    }

    public void setToLineId(String toLineId) {
        this.toLineId = toLineId;
    }

    public String getToLineName() {
        return toLineName;
    }

    public void setToLineName(String toLineName) {
        this.toLineName = toLineName;
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
        return "MetroTransfer{" +
                "id=" + id +
                ", stationName='" + stationName + '\'' +
                ", fromLineId='" + fromLineId + '\'' +
                ", toLineId='" + toLineId + '\'' +
                ", toLineName='" + toLineName + '\'' +
                ", status=" + status +
                '}';
    }
}
