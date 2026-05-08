package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.MetroLine;
import com.example.springboot.entity.MetroStation;
import com.example.springboot.entity.MetroTransfer;
import com.example.springboot.service.MetroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    // ---- 线路 CRUD ----

    @PostMapping("/lines")
    public Result addLine(@RequestBody MetroLine line) {
        if (line.getLineId() == null || line.getLineName() == null) {
            return Result.error("线路ID和名称不能为空");
        }
        MetroLine result = metroService.addLine(line);
        return Result.success(result);
    }

    @PutMapping("/lines/{id}")
    public Result updateLine(@PathVariable Long id, @RequestBody MetroLine line) {
        line.setId(id);
        int result = metroService.updateLine(line);
        return result > 0 ? Result.success() : Result.error("更新失败");
    }

    @PutMapping("/lines/{id}/path")
    public Result updateLinePath(@PathVariable Long id, @RequestBody Map<String, String> body) {
        int result = metroService.updateLinePath(id, body.get("path"));
        return result > 0 ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/lines/{id}")
    public Result deleteLine(@PathVariable Long id) {
        int result = metroService.deleteLine(id);
        return result > 0 ? Result.success() : Result.error("删除失败");
    }

    // ---- 站点 CRUD ----

    @PostMapping("/stations")
    public Result addStation(@RequestBody MetroStation station) {
        MetroStation result = metroService.addStation(station);
        return Result.success(result);
    }

    @PutMapping("/stations/{id}")
    public Result updateStation(@PathVariable Long id, @RequestBody MetroStation station) {
        station.setId(id);
        int result = metroService.updateStation(station);
        return result > 0 ? Result.success() : Result.error("更新失败");
    }

    @PutMapping("/stations/{id}/position")
    public Result updateStationPosition(@PathVariable Long id, @RequestBody Map<String, BigDecimal> body) {
        int result = metroService.updateStationPosition(id, body.get("longitude"), body.get("latitude"));
        return result > 0 ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/stations/{id}")
    public Result deleteStation(@PathVariable Long id) {
        int result = metroService.deleteStation(id);
        return result > 0 ? Result.success() : Result.error("删除失败");
    }

    // ---- 换乘 CRUD ----

    @PostMapping("/transfers")
    public Result addTransfer(@RequestBody MetroTransfer transfer) {
        MetroTransfer result = metroService.addTransfer(transfer);
        return Result.success(result);
    }

    @PutMapping("/transfers/{id}")
    public Result updateTransfer(@PathVariable Long id, @RequestBody MetroTransfer transfer) {
        transfer.setId(id);
        int result = metroService.updateTransfer(transfer);
        return result > 0 ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/transfers/{id}")
    public Result deleteTransfer(@PathVariable Long id) {
        int result = metroService.deleteTransfer(id);
        return result > 0 ? Result.success() : Result.error("删除失败");
    }

    // ---- 时刻表 ----

    @PutMapping("/line/{lineId}/schedule")
    public Result updateSchedule(@PathVariable String lineId, @RequestBody MetroLine params) {
        int result = metroService.updateSchedule(lineId, params);
        return result > 0 ? Result.success() : Result.error("保存失败");
    }
}
