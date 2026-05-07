# 热力分析模块设计文档

> 城市智慧地铁管理系统 - 热力分析功能设计
> 日期：2026-05-07

---

## 1. 模块概述

### 1.1 功能定位

热力分析模块是用户端的核心可视化功能，提供地铁客流数据的可视化分析，帮助用户和运营人员直观了解地铁运行状况。

**核心功能**：
- 客流量热力图（站点/线路维度）
- 拥挤度热力图（时间维度）
- 高峰时段分析
- 历史客流对比
- 实时数据展示

### 1.2 目标用户

| 用户角色 | 使用场景 |
|---------|---------|
| **普通用户** | 查看当前线路拥挤度，选择合适的出行时间/线路 |
| **运营管理人员** | 监控客流热力分布，优化线路调度和站点布局 |

---

## 2. 功能需求

### 2.1 客流量热力图

**功能描述**：以地图形式展示站点客流量分布

**输入参数**：
- 时间范围（最近7天/30天/自定义）
- 统计维度（站点维度/线路维度）
- 线路筛选（可选，不选则展示所有线路）

**输出结果**：
- 地图热力点：每个站点显示客流量数值和颜色深浅
- 颜色编码：蓝色(低) → 绿色(中) → 黄色(高) → 红色(极高)
- 数据统计面板：总客流量、平均客流、最高客流站点

**交互功能**：
- 点击站点查看详情
- 鼠标悬停显示客流趋势图
- 时间范围筛选器
- 线路复选框选择

---

### 2.2 拥挤度热力图

**功能描述**：展示站点不同时段的拥挤程度

**输入参数**：
- 线路选择（必选）
- 时间段选择（07:00-09:00, 17:00-19:00, 12:00-14:00, 22:00-00:00）

**输出结果**：
- 站点拥挤度等级：空闲(0-30%) → 舒适(30-60%) → 拥挤(60-85%) → 拥挤(85-100%)
- 热力颜色变化反映不同时段的拥挤度
- 实时数据（基于最新5分钟客流统计）

**交互功能**：
- 时段切换按钮
- 拥挤度图例
- 查看历史数据对比

---

### 2.3 高峰时段分析

**功能描述**：分析各线路的高峰时段客流特征

**输入参数**：
- 线路选择（必选）

**输出结果**：
- 高峰时段柱状图：每个时段的客流量对比
- 换乘站高峰流量排行
- 线路客流分布曲线图

**交互功能**：
- 线路切换
- 查看详细数据

---

### 2.4 实时客流监控

**功能描述**：展示当前实时客流数据

**输入参数**：无（默认显示当前数据）

**输出结果**：
- 当前时间各线路客流
- 实时拥挤度热力图
- 站点客流实时排名

**更新频率**：5秒刷新一次

---

## 3. 数据模型设计

### 3.1 新增数据库表

#### `metro_heatmap` — 客流数据统计表

```sql
CREATE TABLE `metro_heatmap` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `stat_hour` TINYINT NOT NULL COMMENT '统计小时(0-23)',
  `station_id` BIGINT COMMENT '站点ID（站点维度统计时使用）',
  `line_id` VARCHAR COMMENT '线路ID（线路维度统计时使用）',
  `visit_count` INT DEFAULT 0 COMMENT '客流人次',
  `average_wait` INT DEFAULT 0 COMMENT '平均等待时间(秒)',
  `crowd_level` INT DEFAULT 0 COMMENT '拥挤度等级(0-100)',
  `status` INT DEFAULT 1 COMMENT '状态(1=启用)',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_stat_date_hour` (`stat_date`, `stat_hour`),
  INDEX `idx_station_id` (`station_id`),
  INDEX `idx_line_id` (`line_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客流热力数据表';
```

#### `metro_crowd_record` — 实时客流记录表

```sql
CREATE TABLE `metro_crowd_record` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  `station_id` BIGINT NOT NULL COMMENT '站点ID',
  `line_id` VARCHAR NOT NULL COMMENT '线路ID',
  `current_crowd` INT DEFAULT 0 COMMENT '当前拥挤人数',
  `max_capacity` INT DEFAULT 0 COMMENT '最大容量',
  `crowd_percent` DECIMAL(5,2) DEFAULT 0 COMMENT '拥挤百分比(0-100)',
  `record_time` DATETIME NOT NULL COMMENT '记录时间',
  `status` INT DEFAULT 1 COMMENT '状态(1=启用)',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_station_line` (`station_id`, `line_id`),
  INDEX `idx_record_time` (`record_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实时客流记录表';
```

---

## 4. 后端设计

### 4.1 新增实体类

#### `HeatmapData.java`

```java
package com.example.springboot.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 客流热力数据实体
 */
@Data
public class HeatmapData {
    private Long id;
    private LocalDate statDate;
    private Integer statHour;
    private Long stationId;
    private String lineId;
    private Integer visitCount;
    private Integer averageWait;
    private BigDecimal crowdLevel;  // 拥挤度 0-100
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### `CrowdRecord.java`

```java
package com.example.springboot.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 实时客流记录实体
 */
@Data
public class CrowdRecord {
    private Long id;
    private Long stationId;
    private String lineId;
    private Integer currentCrowd;
    private Integer maxCapacity;
    private BigDecimal crowdPercent;  // 拥挤百分比
    private LocalDateTime recordTime;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 4.2 Mapper 接口

#### `HeatmapDataMapper.java`

```java
package com.example.springboot.mapper;

import com.example.springboot.entity.HeatmapData;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HeatmapDataMapper {

    // 获取指定日期所有站点客流
    @Select("SELECT * FROM metro_heatmap " +
            "WHERE stat_date = #{statDate} AND station_id IS NOT NULL " +
            "ORDER BY visit_count DESC")
    List<HeatmapData> getStationHeatmapByDate(LocalDate statDate);

    // 获取指定日期所有线路客流
    @Select("SELECT * FROM metro_heatmap " +
            "WHERE stat_date = #{statDate} AND line_id IS NOT NULL " +
            "ORDER BY visit_count DESC")
    List<HeatmapData> getLineHeatmapByDate(LocalDate statDate);

    // 获取指定线路指定时段客流
    @Select("SELECT * FROM metro_heatmap " +
            "WHERE line_id = #{lineId} AND stat_hour = #{statHour} " +
            "ORDER BY visit_count DESC")
    List<HeatmapData> getLineHeatmapByTime(@Param("lineId") String lineId,
                                           @Param("statHour") Integer statHour);

    // 统计指定日期总客流量
    @Select("SELECT COALESCE(SUM(visit_count), 0) FROM metro_heatmap " +
            "WHERE stat_date = #{statDate}")
    Long getTotalVisitCount(LocalDate statDate);

    // 批量插入客流数据
    @Insert("<script>" +
            "INSERT INTO metro_heatmap " +
            "(stat_date, stat_hour, station_id, line_id, visit_count, crowd_level) " +
            "VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.statDate}, #{item.statHour}, #{item.stationId}, #{item.lineId}, " +
            "#{item.visitCount}, #{item.crowdLevel})" +
            "</foreach>" +
            "</script>")
    void batchInsert(@Param("list") List<HeatmapData> dataList);

    // 获取最近N天的数据
    @Select("SELECT * FROM metro_heatmap " +
            "WHERE stat_date >= #{startDate} " +
            "ORDER BY stat_date, stat_hour")
    List<HeatmapData> getRecentData(@Param("startDate") LocalDate startDate);
}
```

#### `CrowdRecordMapper.java`

```java
package com.example.springboot.mapper;

import com.example.springboot.entity.CrowdRecord;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CrowdRecordMapper {

    // 获取站点实时客流
    @Select("SELECT * FROM metro_crowd_record " +
            "WHERE station_id = #{stationId} AND record_time >= #{startTime} " +
            "ORDER BY record_time DESC LIMIT 1")
    CrowdRecord getStationCurrentCrowd(@Param("stationId") Long stationId,
                                        @Param("startTime") LocalDateTime startTime);

    // 获取线路实时客流
    @Select("SELECT * FROM metro_crowd_record " +
            "WHERE line_id = #{lineId} AND record_time >= #{startTime} " +
            "ORDER BY record_time DESC LIMIT 1")
    CrowdRecord getLineCurrentCrowd(@Param("lineId") String lineId,
                                     @Param("startTime") LocalDateTime startTime);

    // 批量插入实时记录
    @Insert("<script>" +
            "INSERT INTO metro_crowd_record " +
            "(station_id, line_id, current_crowd, max_capacity, crowd_percent, record_time) " +
            "VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.stationId}, #{item.lineId}, #{item.currentCrowd}, " +
            "#{item.maxCapacity}, #{item.crowdPercent}, #{item.recordTime})" +
            "</foreach>" +
            "</script>")
    void batchInsert(@Param("list") List<CrowdRecord> records);

    // 获取最新实时数据（全部线路）
    @Select("SELECT * FROM metro_crowd_record " +
            "WHERE record_time >= #{startTime} " +
            "ORDER BY record_time DESC LIMIT 100")
    List<CrowdRecord> getLatestRecords(@Param("startTime") LocalDateTime startTime);

    // 获取站点实时记录趋势
    @Select("SELECT * FROM metro_crowd_record " +
            "WHERE station_id = #{stationId} " +
            "ORDER BY record_time DESC LIMIT 100")
    List<CrowdRecord> getStationHistory(@Param("stationId") Long stationId);
}
```

### 4.3 Service 接口与实现

#### `HeatmapDataService.java`

```java
package com.example.springboot.service;

import com.example.springboot.entity.HeatmapData;
import java.time.LocalDate;
import java.util.List;

public interface HeatmapDataService {
    /**
     * 获取站点客流热力图数据
     */
    List<HeatmapData> getStationHeatmap(LocalDate statDate);

    /**
     * 获取线路客流热力图数据
     */
    List<HeatmapData> getLineHeatmap(LocalDate statDate);

    /**
     * 获取线路指定时段客流
     */
    List<HeatmapData> getLineHeatmapByTime(String lineId, Integer statHour);

    /**
     * 获取总客流量统计
     */
    Long getTotalVisitCount(LocalDate statDate);

    /**
     * 获取最近7天客流趋势
     */
    List<HeatmapData> getRecentTrend(int days);

    /**
     * 批量导入客流数据
     */
    void importHeatmapData(List<HeatmapData> dataList);
}
```

#### `HeatmapDataServiceImpl.java`

```java
package com.example.springboot.service.impl;

import com.example.springboot.entity.HeatmapData;
import com.example.springboot.mapper.HeatmapDataMapper;
import com.example.springboot.service.HeatmapDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HeatmapDataServiceImpl implements HeatmapDataService {

    @Autowired
    private HeatmapDataMapper heatmapDataMapper;

    @Override
    public List<HeatmapData> getStationHeatmap(LocalDate statDate) {
        return heatmapDataMapper.getStationHeatmapByDate(statDate);
    }

    @Override
    public List<HeatmapData> getLineHeatmap(LocalDate statDate) {
        return heatmapDataMapper.getLineHeatmapByDate(statDate);
    }

    @Override
    public List<HeatmapData> getLineHeatmapByTime(String lineId, Integer statHour) {
        return heatmapDataMapper.getLineHeatmapByTime(lineId, statHour);
    }

    @Override
    public Long getTotalVisitCount(LocalDate statDate) {
        return heatmapDataMapper.getTotalVisitCount(statDate);
    }

    @Override
    public List<HeatmapData> getRecentTrend(int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return heatmapDataMapper.getRecentData(startDate);
    }

    @Override
    public void importHeatmapData(List<HeatmapData> dataList) {
        heatmapDataMapper.batchInsert(dataList);
    }
}
```

#### `CrowdRecordService.java`

```java
package com.example.springboot.service;

import com.example.springboot.entity.CrowdRecord;
import java.time.LocalDateTime;
import java.util.List;

public interface CrowdRecordService {
    /**
     * 更新站点实时客流
     */
    void updateStationCrowd(Long stationId, String lineId,
                           Integer currentCrowd, Integer maxCapacity);

    /**
     * 更新线路实时客流
     */
    void updateLineCrowd(String lineId, Integer currentCrowd, Integer maxCapacity);

    /**
     * 批量更新所有站点实时数据
     */
    void updateAllStationsCrowd();

    /**
     * 获取站点实时数据
     */
    CrowdRecord getStationCrowd(Long stationId, LocalDateTime startTime);

    /**
     * 获取线路实时数据
     */
    CrowdRecord getLineCrowd(String lineId, LocalDateTime startTime);

    /**
     * 获取所有最新实时数据
     */
    List<CrowdRecord> getLatestAllCrowd(LocalDateTime startTime);

    /**
     * 获取站点历史记录
     */
    List<CrowdRecord> getStationHistory(Long stationId);
}
```

#### `CrowdRecordServiceImpl.java`

```java
package com.example.springboot.service.impl;

import com.example.springboot.entity.CrowdRecord;
import com.example.springboot.mapper.CrowdRecordMapper;
import com.example.springboot.service.CrowdRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CrowdRecordServiceImpl implements CrowdRecordService {

    @Autowired
    private CrowdRecordMapper crowdRecordMapper;

    @Autowired
    private MetroStationMapper metroStationMapper;

    @Autowired
    private MetroLineMapper metroLineMapper;

    /**
     * 每分钟更新一次实时客流数据（模拟）
     */
    @Scheduled(fixedRate = 60000)
    public void updateAllStationsCrowd() {
        // 模拟客流数据更新
        LocalDateTime now = LocalDateTime.now();
        List<CrowdRecord> records = generateMockCrowdData(now);
        crowdRecordMapper.batchInsert(records);
    }

    @Override
    public void updateStationCrowd(Long stationId, String lineId,
                                   Integer currentCrowd, Integer maxCapacity) {
        CrowdRecord record = new CrowdRecord();
        record.setStationId(stationId);
        record.setLineId(lineId);
        record.setCurrentCrowd(currentCrowd);
        record.setMaxCapacity(maxCapacity);
        record.setCrowdPercent(new java.math.BigDecimal(currentCrowd)
                .divide(new java.math.BigDecimal(maxCapacity), 2, java.math.RoundingMode.HALF_UP));
        record.setRecordTime(LocalDateTime.now());
        crowdRecordMapper.batchInsert(List.of(record));
    }

    @Override
    public void updateLineCrowd(String lineId, Integer currentCrowd, Integer maxCapacity) {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(5);
        CrowdRecord existing = crowdRecordMapper.getLineCurrentCrowd(lineId, startTime);
        if (existing == null) {
            updateStationCrowd(null, lineId, currentCrowd, maxCapacity);
        } else {
            // 更新线路平均拥挤度
            // ...
        }
    }

    @Override
    public CrowdRecord getStationCrowd(Long stationId, LocalDateTime startTime) {
        return crowdRecordMapper.getStationCurrentCrowd(stationId, startTime);
    }

    @Override
    public CrowdRecord getLineCrowd(String lineId, LocalDateTime startTime) {
        return crowdRecordMapper.getLineCurrentCrowd(lineId, startTime);
    }

    @Override
    public List<CrowdRecord> getLatestAllCrowd(LocalDateTime startTime) {
        return crowdRecordMapper.getLatestRecords(startTime);
    }

    @Override
    public List<CrowdRecord> getStationHistory(Long stationId) {
        return crowdRecordMapper.getStationHistory(stationId);
    }

    /**
     * 生成模拟客流数据
     */
    private List<CrowdRecord> generateMockCrowdData(LocalDateTime time) {
        List<CrowdRecord> records = new ArrayList<>();

        // 获取所有站点
        List<MetroStation> stations = metroStationMapper.getAllStations();

        for (MetroStation station : stations) {
            CrowdRecord record = new CrowdRecord();
            record.setStationId(station.getId());
            record.setLineId(station.getLineId());
            // 模拟客流量 - 基于时间生成波动数据
            int hour = time.getHour();
            int baseCrowd = 500;
            if (hour >= 7 && hour <= 9) {  // 早晚高峰
                baseCrowd = 2000 + (int)(Math.random() * 1000);
            } else if (hour >= 17 && hour <= 19) {
                baseCrowd = 2500 + (int)(Math.random() * 1000);
            }
            record.setCurrentCrowd(baseCrowd);
            record.setMaxCapacity(3000);
            record.setCrowdPercent(new java.math.BigDecimal(baseCrowd)
                    .divide(new java.math.BigDecimal(3000), 2, java.math.RoundingMode.HALF_UP));
            record.setRecordTime(time);
            records.add(record);
        }

        return records;
    }
}
```

### 4.4 Controller 接口

```java
package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.HeatmapData;
import com.example.springboot.service.HeatmapDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/heatmap")
@CrossOrigin
public class HeatmapController {

    @Autowired
    private HeatmapDataService heatmapDataService;

    /**
     * 获取站点客流热力图数据
     * GET /api/heatmap/station?date=2026-05-07
     */
    @GetMapping("/station")
    public Result<List<HeatmapData>> getStationHeatmap(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<HeatmapData> data = heatmapDataService.getStationHeatmap(date);
        return Result.success(data);
    }

    /**
     * 获取线路客流热力图数据
     * GET /api/heatmap/line?date=2026-05-07
     */
    @GetMapping("/line")
    public Result<List<HeatmapData>> getLineHeatmap(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<HeatmapData> data = heatmapDataService.getLineHeatmap(date);
        return Result.success(data);
    }

    /**
     * 获取线路指定时段客流
     * GET /api/heatmap/line-time?lineId=1&hour=8
     */
    @GetMapping("/line-time")
    public Result<List<HeatmapData>> getLineHeatmapByTime(
            @RequestParam String lineId,
            @RequestParam Integer hour) {
        List<HeatmapData> data = heatmapDataService.getLineHeatmapByTime(lineId, hour);
        return Result.success(data);
    }

    /**
     * 获取总客流量统计
     * GET /api/heatmap/total?date=2026-05-07
     */
    @GetMapping("/total")
    public Result<Long> getTotalVisitCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long total = heatmapDataService.getTotalVisitCount(date);
        return Result.success(total);
    }

    /**
     * 获取最近7天客流趋势
     * GET /api/heatmap/trend?days=7
     */
    @GetMapping("/trend")
    public Result<List<HeatmapData>> getRecentTrend(@RequestParam(defaultValue = "7") int days) {
        List<HeatmapData> trend = heatmapDataService.getRecentTrend(days);
        return Result.success(trend);
    }

    /**
     * 导入客流数据
     * POST /api/heatmap/import
     */
    @PostMapping("/import")
    public Result<Void> importHeatmapData(@RequestBody List<HeatmapData> dataList) {
        heatmapDataService.importHeatmapData(dataList);
        return Result.success();
    }
}
```

```java
package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.CrowdRecord;
import com.example.springboot.service.CrowdRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/crowd")
@CrossOrigin
public class CrowdController {

    @Autowired
    private CrowdRecordService crowdRecordService;

    /**
     * 获取站点实时客流
     * GET /api/crowd/station?stationId=1&startTime=2026-05-07T10:00:00
     */
    @GetMapping("/station")
    public Result<CrowdRecord> getStationCrowd(
            @RequestParam Long stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        CrowdRecord record = crowdRecordService.getStationCrowd(stationId, startTime);
        return Result.success(record);
    }

    /**
     * 获取线路实时客流
     * GET /api/crowd/line?lineId=1&startTime=2026-05-07T10:00:00
     */
    @GetMapping("/line")
    public Result<CrowdRecord> getLineCrowd(
            @RequestParam String lineId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        CrowdRecord record = crowdRecordService.getLineCrowd(lineId, startTime);
        return Result.success(record);
    }

    /**
     * 获取所有最新实时数据
     * GET /api/crowd/latest?startTime=2026-05-07T10:00:00
     */
    @GetMapping("/latest")
    public Result<List<CrowdRecord>> getLatestAllCrowd(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        List<CrowdRecord> records = crowdRecordService.getLatestAllCrowd(startTime);
        return Result.success(records);
    }

    /**
     * 获取站点历史记录
     * GET /api/crowd/history?stationId=1
     */
    @GetMapping("/history")
    public Result<List<CrowdRecord>> getStationHistory(
            @RequestParam Long stationId) {
        List<CrowdRecord> records = crowdRecordService.getStationHistory(stationId);
        return Result.success(records);
    }
}
```

---

## 5. 前端设计

### 5.1 页面结构

```
components/user/
└── HeatmapAnalysis.vue      # 热力分析主页面
```

### 5.2 组件设计

#### `HeatmapAnalysis.vue`

```vue
<template>
  <div class="heatmap-analysis">
    <!-- 顶部工具栏 -->
    <el-card class="header-card">
      <div class="header-content">
        <h2 class="page-title">客流热力分析</h2>
        <div class="toolbar">
          <!-- 维度切换 -->
          <el-radio-group v-model="currentDimension" @change="loadHeatmapData">
            <el-radio-button label="station">站点维度</el-radio-button>
            <el-radio-button label="line">线路维度</el-radio-button>
          </el-radio-group>

          <!-- 日期选择 -->
          <el-date-picker
            v-model="selectedDate"
            type="date"
            placeholder="选择日期"
            :disabled-date="disabledDate"
            @change="loadHeatmapData"
          />

          <!-- 统计信息 -->
          <div class="stats">
            <span class="stat-item">总客流量: <strong>{{ totalVisitCount }}</strong></span>
            <span class="stat-item">站点数: {{ stationCount }}</span>
            <span class="stat-item">线路数: {{ lineCount }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 地图区域 -->
      <div class="map-section">
        <div class="map-container" ref="mapContainer">
          <!-- AMap 热力图 -->
          <div id="heatmap-container"></div>
        </div>

        <!-- 热力图图例 -->
        <div class="heatmap-legend">
          <div class="legend-label">低</div>
          <div class="legend-gradient">
            <div class="legend-stop low"></div>
            <div class="legend-stop medium"></div>
            <div class="legend-stop high"></div>
            <div class="legend-stop extreme"></div>
          </div>
          <div class="legend-label">高</div>
        </div>
      </div>

      <!-- 数据统计面板 -->
      <div class="stats-panel">
        <el-card>
          <h3>客流统计</h3>
          <el-table :data="heatmapData" stripe>
            <el-table-column prop="stationName" label="站点/线路" width="150" />
            <el-table-column prop="visitCount" label="客流人次" sortable />
            <el-table-column prop="crowdLevel" label="拥挤度" sortable>
              <template #default="{ row }">
                <el-tag :type="getCrowdLevelType(row.crowdLevel)">
                  {{ row.crowdLevel }}%
                </el-tag>
              </template>
            </el-table-column>
          </el-table>

          <!-- 客流趋势图 -->
          <div class="chart-section">
            <h4>客流趋势图</h4>
            <div ref="trendChart" class="chart"></div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 拥挤度时段分析 -->
    <el-card class="crowd-analysis-card">
      <h3>时段拥挤度分析</h3>
      <div class="time-slots">
        <el-radio-group v-model="selectedSlot" @change="loadCrowdAnalysis">
          <el-radio-button label="morning">早高峰 (07:00-09:00)</el-radio-button>
          <el-radio-button label="afternoon">晚高峰 (17:00-19:00)</el-radio-button>
          <el-radio-button label="noon">午高峰 (12:00-14:00)</el-radio-button>
          <el-radio-button label="night">深夜 (22:00-00:00)</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 拥挤度柱状图 -->
      <div ref="crowdChart" class="chart"></div>

      <!-- 线路选择 -->
      <div class="line-selector">
        <el-select v-model="selectedLine" placeholder="选择线路" clearable @change="loadCrowdAnalysis">
          <el-option
            v-for="line in metroLines"
            :key="line.lineId"
            :label="line.lineName"
            :value="line.lineId"
          />
        </el-select>
      </div>
    </el-card>

    <!-- 实时客流监控 -->
    <el-card class="realtime-card">
      <div class="realtime-header">
        <h3>实时客流监控</h3>
        <el-tag type="success">实时更新中</el-tag>
      </div>
      <el-table :data="realtimeData" stripe>
        <el-table-column prop="stationName" label="站点" width="150" />
        <el-table-column prop="lineName" label="线路" width="100" />
        <el-table-column prop="currentCrowd" label="当前拥挤人数">
          <template #default="{ row }">
            {{ row.currentCrowd }}人
          </template>
        </el-table-column>
        <el-table-column prop="maxCapacity" label="最大容量">
          <template #default="{ row }">
            {{ row.maxCapacity }}人
          </template>
        </el-table-column>
        <el-table-column prop="crowdPercent" label="拥挤百分比">
          <template #default="{ row }">
            <el-progress
              :percentage="parseFloat(row.crowdPercent)"
              :color="getCrowdProgressColor(parseFloat(row.crowdPercent))"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { getAllLines, getAllStations, getFullMetroData } from '@/api/metroApi'
import * as echarts from 'echarts'
import AMapLoader from '@amap/amap-jsapi-loader'
import request from '@/utils/request'

// 状态
const currentDimension = ref('station')  // station | line
const selectedDate = ref(new Date())
const heatmapData = ref([])
const totalVisitCount = ref(0)
const stationCount = ref(0)
const lineCount = ref(0)
const selectedSlot = ref('morning')
const selectedLine = ref('')
const realtimeData = ref([])
const metroLines = ref([])
const metroStations = ref([])

// 地图实例
let map = null
let heatmapLayer = null
let trendChart = null
let crowdChart = null
let refreshTimer = null

// 加载地铁数据
const loadMetroData = async () => {
  try {
    const data = await getFullMetroData()
    metroLines.value = data.lines
    metroStations.value = data.stations
    lineCount.value = metroLines.value.length
    stationCount.value = metroStations.value.length
  } catch (error) {
    console.error('加载地铁数据失败:', error)
  }
}

// 禁用未来日期
const disabledDate = (time) => {
  return time.getTime() > Date.now()
}

// 加载热力图数据
const loadHeatmapData = async () => {
  const date = selectedDate.value.toISOString().split('T')[0]

  try {
    if (currentDimension.value === 'station') {
      const res = await request.get(`/api/heatmap/station?date=${date}`)
      heatmapData.value = res.data.map(item => ({
        ...item,
        stationName: metroStations.value.find(s => s.id === item.stationId)?.stationName || '未知站点'
      }))
    } else {
      const res = await request.get(`/api/heatmap/line?date=${date}`)
      heatmapData.value = res.data.map(item => ({
        ...item,
        lineName: metroLines.value.find(l => l.lineId === item.lineId)?.lineName || '未知线路'
      }))
    }

    // 计算总客流量
    const totalRes = await request.get(`/api/heatmap/total?date=${date}`)
    totalVisitCount.value = totalRes.data

    // 更新地图热力图
    updateHeatmapOnMap()

    // 更新趋势图
    updateTrendChart()

  } catch (error) {
    console.error('加载热力图数据失败:', error)
  }
}

// 加载时段拥挤度分析
const loadCrowdAnalysis = async () => {
  if (!selectedLine.value) return

  try {
    const hourMap = {
      morning: 8,
      afternoon: 18,
      noon: 13,
      night: 23
    }
    const hour = hourMap[selectedSlot.value]

    const res = await request.get(`/api/heatmap/line-time?lineId=${selectedLine.value}&hour=${hour}`)
    // 更新拥挤度图表
    updateCrowdChart(res.data)
  } catch (error) {
    console.error('加载拥挤度分析失败:', error)
  }
}

// 更新地图热力图
const initMap = async () => {
  try {
    const AMap = await AMapLoader.load({
      key: 'YOUR_AMAP_KEY',
      version: '2.0',
      plugins: ['AMap.Heatmap']
    })

    // 创建地图（默认定位广州）
    map = new AMap.Map('heatmap-container', {
      zoom: 12,
      center: [113.264385, 23.129112],
      viewMode: '2D'
    })

    // 创建热力图图层
    heatmapLayer = new AMap.Heatmap({
      radius: 25,
      opacity: 0.6,
      min: 0,
      max: 5000
    })
    map.add(heatmapLayer)

  } catch (error) {
    console.error('地图初始化失败:', error)
  }
}

const updateHeatmapOnMap = () => {
  if (!map || !heatmapLayer) return

  // 准备热力数据
  const data = heatmapData.value.map(item => {
    const station = metroStations.value.find(s => s.id === item.stationId)
    return {
      lng: station?.longitude || 113.264385,
      lat: station?.latitude || 23.129112,
      count: item.visitCount
    }
  })

  heatmapLayer.setData(data)
}

// 更新趋势图
const updateTrendChart = () => {
  if (!trendChart) return

  const dates = heatmapData.value.map(item => item.statDate)
  const counts = heatmapData.value.map(item => item.visitCount)

  const option = {
    title: { text: '客流趋势图' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: dates },
    yAxis: { type: 'value', name: '客流人次' },
    series: [{
      type: 'bar',
      data: counts,
      itemStyle: { color: '#0089e6' }
    }]
  }

  trendChart.setOption(option)
}

// 更新拥挤度图表
const updateCrowdChart = (data) => {
  if (!crowdChart) return

  const option = {
    title: { text: `${selectedLine.value}线路${selectedSlot.value}高峰时段拥挤度` },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.map(d => d.stationName) },
    yAxis: { type: 'value', name: '拥挤度等级' },
    series: [{
      type: 'bar',
      data: data.map(d => d.crowdLevel),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#ff6b6b' },
          { offset: 1, color: '#ffd93d' }
        ])
      }
    }]
  }

  crowdChart.setOption(option)
}

// 获取拥挤度标签类型
const getCrowdLevelType = (level) => {
  if (level < 30) return 'info'
  if (level < 60) return 'success'
  if (level < 85) return 'warning'
  return 'danger'
}

// 获取进度条颜色
const getCrowdProgressColor = (percent) => {
  if (percent < 30) return '#909399'
  if (percent < 60) return '#67c23a'
  if (percent < 85) return '#e6a23c'
  return '#f56c6c'
}

// 加载实时客流数据
const loadRealtimeData = async () => {
  const startTime = new Date(Date.now() - 5 * 60000).toISOString()

  try {
    const res = await request.get(`/api/crowd/latest?startTime=${startTime}`)
    realtimeData.value = res.data.map(item => ({
      ...item,
      stationName: metroStations.value.find(s => s.id === item.stationId)?.stationName || '未知站点',
      lineName: metroLines.value.find(l => l.lineId === item.lineId)?.lineName || '未知线路'
    }))
  } catch (error) {
    console.error('加载实时数据失败:', error)
  }
}

// 定时刷新实时数据
const startRealtimeRefresh = () => {
  refreshTimer = setInterval(loadRealtimeData, 5000)
}

const stopRealtimeRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

// 初始化趋势图
const initTrendChart = () => {
  const chartDom = document.getElementById('trendChart')
  trendChart = echarts.init(chartDom)
}

// 初始化拥挤度图表
const initCrowdChart = () => {
  const chartDom = document.getElementById('crowdChart')
  crowdChart = echarts.init(chartDom)
}

// 生命周期
onMounted(async () => {
  await loadMetroData()
  await initMap()
  await loadHeatmapData()
  await loadCrowdAnalysis()
  loadRealtimeData()
  startRealtimeRefresh()
  initTrendChart()
  initCrowdChart()

  // 窗口大小改变时重新渲染图表
  window.addEventListener('resize', () => {
    trendChart && trendChart.resize()
    crowdChart && crowdChart.resize()
  })
})

onUnmounted(() => {
  stopRealtimeRefresh()
  if (map) map.destroy()
  if (trendChart) trendChart.dispose()
  if (crowdChart) crowdChart.dispose()
})
</script>

<style scoped>
.heatmap-analysis {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.header-card {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.toolbar {
  display: flex;
  gap: 16px;
  align-items: center;
}

.stats {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #666;
}

.stat-item {
  display: flex;
  align-items: center;
}

.main-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.map-section {
  position: relative;
  height: 500px;
}

.map-container {
  height: 100%;
  background: #e8e8e8;
}

.heatmap-container {
  width: 100%;
  height: 100%;
}

.heatmap-legend {
  position: absolute;
  bottom: 20px;
  right: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  background: white;
  padding: 10px 15px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.legend-label {
  font-size: 12px;
  color: #666;
  min-width: 20px;
}

.legend-gradient {
  width: 150px;
  height: 12px;
  background: linear-gradient(to right,
    rgba(0, 137, 230, 0.6),
    rgba(102, 194, 58, 0.6),
    rgba(230, 162, 60, 0.6),
    rgba(245, 108, 108, 0.6)
  );
  border-radius: 6px;
}

.stats-panel {
  height: 500px;
  overflow-y: auto;
}

.crowd-analysis-card {
  margin-bottom: 20px;
}

.realtime-card {
  margin-bottom: 20px;
}

.realtime-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-section {
  margin-top: 20px;
}

.chart {
  width: 100%;
  height: 300px;
}

.line-selector {
  margin-top: 20px;
  max-width: 300px;
}
</style>
```

---

## 6. API 接口清单（新增）

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/api/heatmap/station?date=2026-05-07` | 获取站点客流热力图数据 |
| GET | `/api/heatmap/line?date=2026-05-07` | 获取线路客流热力图数据 |
| GET | `/api/heatmap/line-time?lineId=1&hour=8` | 获取线路指定时段客流 |
| GET | `/api/heatmap/total?date=2026-05-07` | 获取总客流量统计 |
| GET | `/api/heatmap/trend?days=7` | 获取最近N天客流趋势 |
| POST | `/api/heatmap/import` | 导入客流数据 |
| GET | `/api/crowd/station?stationId=1&startTime=...` | 获取站点实时客流 |
| GET | `/api/crowd/line?lineId=1&startTime=...` | 获取线路实时客流 |
| GET | `/api/crowd/latest?startTime=...` | 获取所有最新实时数据 |
| GET | `/api/crowd/history?stationId=1` | 获取站点历史记录 |

---

## 7. 实现步骤

### 7.1 数据库层

1. 创建 `metro_heatmap` 表
2. 创建 `metro_crowd_record` 表
3. 编写初始化脚本导入示例数据

### 7.2 后端开发

1. 创建实体类 `HeatmapData`、`CrowdRecord`
2. 创建 Mapper 接口 `HeatmapDataMapper`、`CrowdRecordMapper`
3. 创建 Service 接口与实现
4. 创建 Controller 接口 `HeatmapController`、`CrowdController`
5. 启用定时任务（@EnableScheduling）

### 7.3 前端开发

1. 创建 `HeatmapAnalysis.vue` 组件
2. 在 `router.js` 中添加路由配置
3. 在 `metroApi.js` 中添加 API 函数
4. 在 `Manager.vue` 侧边栏添加导航项

### 7.4 功能集成

1. 实现地图热力图渲染
2. 实现客流趋势图表
3. 实现时段拥挤度分析
4. 实现实时数据刷新

---

## 8. 技术要点

### 8.1 热力图实现

- 使用高德地图 `AMap.Heatmap` 插件
- 数据格式：`[{lng, lat, count}, ...]`
- 颜色渐变：低 → 中 → 高 → 极高
- 半径与透明度可配置

### 8.2 实时数据刷新

- 使用 `setInterval` 每5秒刷新一次
- 请求最新数据后更新表格
- 显示实时状态标签

### 8.3 图表渲染

- 使用 ECharts 渲染趋势图
- 拥挤度柱状图使用渐变色
- 支持响应式布局

---

## 9. 扩展功能

### 9.1 未来可扩展功能

1. **用户自定义维度**：用户可自定义统计维度和时间范围
2. **导出功能**：支持导出客流数据报表（Excel/PDF）
3. **预警通知**：拥挤度超阈值时发送通知
4. **智能推荐**：根据客流情况推荐最优出行时间
5. **多地图切换**：支持切换不同地图引擎（百度地图、高德地图等）

### 9.2 性能优化

1. 数据缓存：减少重复请求
2. 虚拟滚动：大数据量表格优化
3. 分页加载：大数据量分页显示
4. WebWorker：图表计算在 Worker 中执行

---

## 10. 总结

热力分析模块通过地图可视化与图表结合的方式，为用户提供直观的地铁客流数据展示。模块设计遵循现有系统架构，采用前后端分离模式，支持维度切换、时段分析、实时监控等核心功能。未来可进一步扩展个性化、智能推荐等高级功能。

---

*文档生成日期：2026-05-07*
