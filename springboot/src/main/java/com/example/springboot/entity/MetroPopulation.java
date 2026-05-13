package com.example.springboot.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 站点人口数据实体（静态聚合数据）
 */
public class MetroPopulation {

    private Long id;
    private String stationName;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer year;
    private Integer month;      // 0=年均值, 1-12=月均值
    private Integer population;
    private Integer crowdLevel; // 0-100
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getPopulation() { return population; }
    public void setPopulation(Integer population) { this.population = population; }

    public Integer getCrowdLevel() { return crowdLevel; }
    public void setCrowdLevel(Integer crowdLevel) { this.crowdLevel = crowdLevel; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
