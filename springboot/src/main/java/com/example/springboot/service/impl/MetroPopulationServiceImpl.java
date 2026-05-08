package com.example.springboot.service.impl;

import com.example.springboot.entity.MetroPopulation;
import com.example.springboot.entity.MetroStation;
import com.example.springboot.mapper.MetroPopulationMapper;
import com.example.springboot.mapper.MetroStationMapper;
import com.example.springboot.service.MetroPopulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetroPopulationServiceImpl implements MetroPopulationService, CommandLineRunner {

    @Autowired
    private MetroPopulationMapper populationMapper;

    @Autowired
    private MetroStationMapper stationMapper;

    @Override
    public List<MetroPopulation> getAnnualData(Integer year) {
        return populationMapper.getAnnualData(year);
    }

    @Override
    public List<MetroPopulation> getMonthlyData(Integer year, Integer month) {
        return populationMapper.getMonthlyData(year, month);
    }

    @Override
    public List<Integer> getAvailableYears() {
        return populationMapper.getAvailableYears();
    }

    @Override
    public List<Map<String, Object>> getAvailableYearMonths() {
        List<MetroPopulation> list = populationMapper.getAvailableYearMonths();
        return list.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("year", m.getYear());
            map.put("month", m.getMonth());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<MetroPopulation> getPopulationData(Integer year, Integer month, String stationName) {
        return populationMapper.queryByCondition(year, month, stationName);
    }

    @Override
    public int updatePopulation(MetroPopulation record) {
        return populationMapper.update(record);
    }

    @Override
    public int deletePopulation(Long id) {
        return populationMapper.deleteById(id);
    }

    @Override
    public int batchImport(List<MetroPopulation> list) {
        return populationMapper.batchInsert(list);
    }

    @Override
    public void run(String... args) {
        initMockData();
    }

    @Override
    public void initMockData() {
        List<Integer> years = populationMapper.getAvailableYears();
        if (!years.isEmpty()) return; // 已有数据，跳过

        // 获取所有站点并按 stationName 去重，取首条记录的坐标
        List<MetroStation> allStations = stationMapper.findAll();
        Map<String, MetroStation> uniqueStations = new LinkedHashMap<>();
        for (MetroStation s : allStations) {
            if (!uniqueStations.containsKey(s.getStationName())) {
                uniqueStations.put(s.getStationName(), s);
            }
        }
        List<MetroStation> stations = new ArrayList<>(uniqueStations.values());

        List<MetroPopulation> allData = new ArrayList<>();
        Random random = new Random();

        // 2025 年均值 (month=0)
        for (int i = 0; i < stations.size(); i++) {
            MetroStation s = stations.get(i);
            int basePop = generateBasePopulation(i, stations.size(), random);
            allData.add(createRecord(s.getStationName(),
                    s.getLongitude(), s.getLatitude(), 2025, 0, basePop));
        }

        // 2025-01 ~ 2026-04 月均值
        for (int year = 2025; year <= 2026; year++) {
            int endMonth = (year == 2026) ? 4 : 12;
            for (int month = 1; month <= endMonth; month++) {
                for (int i = 0; i < stations.size(); i++) {
                    MetroStation s = stations.get(i);
                    int basePop = generateBasePopulation(i, stations.size(), random);
                    double factor = 0.7 + random.nextDouble() * 0.6;
                    int monthlyPop = (int) (basePop * factor);
                    allData.add(createRecord(s.getStationName(),
                            s.getLongitude(), s.getLatitude(), year, month, monthlyPop));
                }
            }
        }

        if (!allData.isEmpty()) {
            populationMapper.batchInsert(allData);
        }
    }

    private int generateBasePopulation(int index, int total, Random random) {
        int base = 8000 + random.nextInt(12000);
        double ratio = 1.0 - (index / (double) total) * 0.5;
        return (int) (base * ratio);
    }

    private MetroPopulation createRecord(String stationName, BigDecimal lng, BigDecimal lat,
                                          int year, int month, int population) {
        MetroPopulation record = new MetroPopulation();
        record.setStationName(stationName);
        record.setLongitude(lng);
        record.setLatitude(lat);
        record.setYear(year);
        record.setMonth(month);
        record.setPopulation(population);
        record.setCrowdLevel(Math.min(100, population * 100 / 25000));
        return record;
    }
}
