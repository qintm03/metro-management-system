package com.example.springboot.service;

import com.example.springboot.entity.MetroLine;
import com.example.springboot.entity.MetroStation;
import com.example.springboot.entity.MetroTransfer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 地铁数据服务接口
 */
public interface MetroService {

    /**
     * 获取所有线路
     */
    List<MetroLine> getAllLines();

    /**
     * 根据ID获取线路
     */
    MetroLine getLineById(Long id);

    /**
     * 根据线路唯一标识获取线路
     */
    MetroLine getLineByLineId(String lineId);

    /**
     * 获取所有站点
     */
    List<MetroStation> getAllStations();

    /**
     * 根据线路ID获取站点
     */
    List<MetroStation> getStationsByLineId(String lineId);

    /**
     * 获取换乘站
     */
    List<MetroStation> getTransferStations();

    /**
     * 获取完整地铁数据（线路+站点）
     */
    Map<String, Object> getFullMetroData();

    /**
     * 根据线路ID获取完整线路数据（包含站点）
     */
    Map<String, Object> getLineDetail(String lineId);

    /**
     * 获取所有换乘关系
     */
    List<MetroTransfer> getAllTransfers();

    /**
     * 根据站点名称获取换乘信息
     */
    List<MetroTransfer> getTransfersByStationName(String stationName);

    /**
     * 搜索站点
     */
    List<MetroStation> searchStations(String keyword);

    /**
     * 获取线路时刻表
     * @param lineId 线路ID
     * @return 时刻表数据（包含站点、运行参数等）
     */
    Map<String, Object> getSchedule(String lineId);

    /**
     * 获取列车列表
     * @param lineId 线路ID
     * @return 列车列表（包含发车时间偏移量）
     */
    Map<String, Object> getTrains(String lineId);

    // ---- 线路写操作 ----
    MetroLine addLine(MetroLine line);
    int updateLine(MetroLine line);
    int updateLinePath(Long id, String path);
    int deleteLine(Long id);

    // ---- 站点写操作 ----
    MetroStation addStation(MetroStation station);
    int updateStation(MetroStation station);
    int updateStationPosition(Long id, BigDecimal longitude, BigDecimal latitude);
    int deleteStation(Long id);

    // ---- 换乘写操作 ----
    MetroTransfer addTransfer(MetroTransfer transfer);
    int updateTransfer(MetroTransfer transfer);
    int deleteTransfer(Long id);

    // ---- 时刻表 ----
    int updateSchedule(String lineId, MetroLine params);
}
