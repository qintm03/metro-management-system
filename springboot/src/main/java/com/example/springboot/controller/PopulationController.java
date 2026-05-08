package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.MetroPopulation;
import com.example.springboot.entity.MetroStation;
import com.example.springboot.mapper.MetroStationMapper;
import com.example.springboot.service.MetroPopulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/population")
@CrossOrigin
public class PopulationController {

    @Autowired
    private MetroPopulationService populationService;

    @Autowired
    private MetroStationMapper stationMapper;

    /**
     * 获取所有站点坐标（按 stationName 去重，站点密度图用）
     * GET /api/population/stations
     */
    @GetMapping("/stations")
    public Result getAllStations() {
        List<MetroStation> all = stationMapper.findAll();
        Map<String, MetroStation> unique = new LinkedHashMap<>();
        for (MetroStation s : all) {
            unique.putIfAbsent(s.getStationName(), s);
        }
        return Result.success(new ArrayList<>(unique.values()));
    }

    /**
     * 获取年均人口数据
     * GET /api/population/annual?year=2025
     */
    @GetMapping("/annual")
    public Result getAnnualData(@RequestParam Integer year) {
        return Result.success(populationService.getAnnualData(year));
    }

    /**
     * 获取月均人口数据
     * GET /api/population/monthly?year=2025&month=6
     */
    @GetMapping("/monthly")
    public Result getMonthlyData(@RequestParam Integer year,
                                  @RequestParam Integer month) {
        return Result.success(populationService.getMonthlyData(year, month));
    }

    /**
     * 获取可用年份列表
     * GET /api/population/years
     */
    @GetMapping("/years")
    public Result getAvailableYears() {
        return Result.success(populationService.getAvailableYears());
    }

    /**
     * 获取可用年月组合（月均模式的选择器用）
     * GET /api/population/year-months
     */
    @GetMapping("/year-months")
    public Result getAvailableYearMonths() {
        return Result.success(populationService.getAvailableYearMonths());
    }

    /**
     * 查询人口数据（支持筛选）
     * GET /api/population?year=2025&month=6&stationName=公园前
     */
    @GetMapping
    public Result getPopulationData(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String stationName) {
        return Result.success(populationService.getPopulationData(year, month, stationName));
    }

    /**
     * 更新单条人口数据
     */
    @PutMapping("/{id}")
    public Result updatePopulation(@PathVariable Long id, @RequestBody MetroPopulation record) {
        record.setId(id);
        int result = populationService.updatePopulation(record);
        return result > 0 ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除单条人口数据
     */
    @DeleteMapping("/{id}")
    public Result deletePopulation(@PathVariable Long id) {
        int result = populationService.deletePopulation(id);
        return result > 0 ? Result.success() : Result.error("删除失败");
    }

    /**
     * 批量导入人口数据
     */
    @PostMapping("/import")
    public Result importPopulation(@RequestBody List<MetroPopulation> list) {
        if (list == null || list.isEmpty()) {
            return Result.error("导入数据不能为空");
        }
        populationService.batchImport(list);
        return Result.success();
    }
}
