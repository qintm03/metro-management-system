package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.MetroLine;
import com.example.springboot.entity.MetroStation;
import com.example.springboot.entity.MetroTransfer;
import com.example.springboot.service.MetroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 地铁数据控制器
 */
@RestController
@RequestMapping("/api/metro")
public class MetroController {

    @Autowired
    private MetroService metroService;

    /**
     * 获取所有线路
     */
    @GetMapping("/lines")
    public Result getAllLines() {
        List<MetroLine> lines = metroService.getAllLines();
        return Result.success(lines);
    }

    /**
     * 根据ID获取线路
     */
    @GetMapping("/lines/{id}")
    public Result getLineById(@PathVariable Long id) {
        MetroLine line = metroService.getLineById(id);
        if (line == null) {
            return Result.error("线路不存在");
        }
        return Result.success(line);
    }

    /**
     * 根据线路唯一标识获取线路
     */
    @GetMapping("/lines/lineId/{lineId}")
    public Result getLineByLineId(@PathVariable String lineId) {
        MetroLine line = metroService.getLineByLineId(lineId);
        if (line == null) {
            return Result.error("线路不存在");
        }
        return Result.success(line);
    }

    /**
     * 获取所有站点
     */
    @GetMapping("/stations")
    public Result getAllStations() {
        List<MetroStation> stations = metroService.getAllStations();
        return Result.success(stations);
    }

    /**
     * 根据线路ID获取站点
     */
    @GetMapping("/stations/line/{lineId}")
    public Result getStationsByLineId(@PathVariable String lineId) {
        List<MetroStation> stations = metroService.getStationsByLineId(lineId);
        return Result.success(stations);
    }

    /**
     * 获取换乘站
     */
    @GetMapping("/stations/transfer")
    public Result getTransferStations() {
        List<MetroStation> stations = metroService.getTransferStations();
        return Result.success(stations);
    }

    /**
     * 搜索站点
     */
    @GetMapping("/stations/search")
    public Result searchStations(@RequestParam String keyword) {
        List<MetroStation> stations = metroService.searchStations(keyword);
        return Result.success(stations);
    }

    /**
     * 获取完整地铁数据（线路+站点）
     */
    @GetMapping("/full")
    public Result getFullMetroData() {
        Map<String, Object> data = metroService.getFullMetroData();
        return Result.success(data);
    }

    /**
     * 根据线路ID获取完整线路数据（包含站点）
     */
    @GetMapping("/detail/{lineId}")
    public Result getLineDetail(@PathVariable String lineId) {
        Map<String, Object> detail = metroService.getLineDetail(lineId);
        if (detail == null) {
            return Result.error("线路不存在");
        }
        return Result.success(detail);
    }

    /**
     * 获取所有换乘关系
     */
    @GetMapping("/transfers")
    public Result getAllTransfers() {
        List<MetroTransfer> transfers = metroService.getAllTransfers();
        return Result.success(transfers);
    }

    /**
     * 根据站点名称获取换乘信息
     */
    @GetMapping("/transfers/station/{stationName}")
    public Result getTransfersByStationName(@PathVariable String stationName) {
        List<MetroTransfer> transfers = metroService.getTransfersByStationName(stationName);
        return Result.success(transfers);
    }

    /**
     * 获取线路时刻表
     * @param lineId 线路ID
     * @return 时刻表数据（包含站点、运行参数等）
     */
    @GetMapping("/line/{lineId}/schedule")
    public Result getLineSchedule(@PathVariable String lineId) {
        Map<String, Object> schedule = metroService.getSchedule(lineId);
        if (schedule == null) {
            return Result.error("线路不存在");
        }
        return Result.success(schedule);
    }

    /**
     * 获取列车列表
     * @param lineId 线路ID
     * @return 列车列表（包含发车时间偏移量）
     */
    @GetMapping("/line/{lineId}/trains")
    public Result getLineTrains(@PathVariable String lineId) {
        Map<String, Object> trains = metroService.getTrains(lineId);
        if (trains == null) {
            return Result.error("线路不存在");
        }
        return Result.success(trains);
    }
}
