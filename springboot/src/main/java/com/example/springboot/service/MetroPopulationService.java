package com.example.springboot.service;

import com.example.springboot.entity.MetroPopulation;
import java.util.List;
import java.util.Map;

public interface MetroPopulationService {
    List<MetroPopulation> getAnnualData(Integer year);
    List<MetroPopulation> getMonthlyData(Integer year, Integer month);
    List<Integer> getAvailableYears();
    List<Map<String, Object>> getAvailableYearMonths();
    void initMockData();

    // ---- 写操作 ----
    List<MetroPopulation> getPopulationData(Integer year, Integer month, String stationName);
    int updatePopulation(MetroPopulation record);
    int deletePopulation(Long id);
    int batchImport(List<MetroPopulation> list);
}
