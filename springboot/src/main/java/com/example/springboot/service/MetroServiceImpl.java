package com.example.springboot.service;

import com.example.springboot.entity.MetroLine;
import com.example.springboot.entity.MetroStation;
import com.example.springboot.entity.MetroTransfer;
import com.example.springboot.mapper.MetroLineMapper;
import com.example.springboot.mapper.MetroStationMapper;
import com.example.springboot.mapper.MetroTransferMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 地铁数据服务实现类
 */
@Service
public class MetroServiceImpl implements MetroService {

    @Autowired
    private MetroLineMapper metroLineMapper;

    @Autowired
    private MetroStationMapper metroStationMapper;

    @Autowired
    private MetroTransferMapper metroTransferMapper;

    @Override
    public List<MetroLine> getAllLines() {
        return metroLineMapper.findAll();
    }

    @Override
    public MetroLine getLineById(Long id) {
        return metroLineMapper.findById(id);
    }

    @Override
    public MetroLine getLineByLineId(String lineId) {
        return metroLineMapper.findByLineId(lineId);
    }

    @Override
    public List<MetroStation> getAllStations() {
        return metroStationMapper.findAll();
    }

    @Override
    public List<MetroStation> getStationsByLineId(String lineId) {
        return metroStationMapper.findByLineId(lineId);
    }

    @Override
    public List<MetroStation> getTransferStations() {
        return metroStationMapper.findTransferStations();
    }

    @Override
    public Map<String, Object> getFullMetroData() {
        Map<String, Object> result = new HashMap<>();

        // 获取所有线路
        List<MetroLine> lines = metroLineMapper.findAll();

        // 获取所有站点
        List<MetroStation> stations = metroStationMapper.findAll();

        // 按线路ID分组站点
        Map<String, List<MetroStation>> stationMap = new HashMap<>();
        for (MetroStation station : stations) {
            String lineId = station.getLineId();
            if (!stationMap.containsKey(lineId)) {
                stationMap.put(lineId, new ArrayList<>());
            }
            stationMap.get(lineId).add(station);
        }

        // 构建线路详情
        List<Map<String, Object>> lineDetails = new ArrayList<>();
        for (MetroLine line : lines) {
            Map<String, Object> lineDetail = new HashMap<>();
            lineDetail.put("id", line.getId());
            lineDetail.put("lineId", line.getLineId());
            lineDetail.put("lineName", line.getLineName());
            lineDetail.put("lineCode", line.getLineCode());
            lineDetail.put("startStation", line.getStartStation());
            lineDetail.put("endStation", line.getEndStation());
            lineDetail.put("distance", line.getDistance());
            lineDetail.put("stationCount", line.getStationCount());
            lineDetail.put("path", line.getPath());
            lineDetail.put("status", line.getStatus());
            lineDetail.put("stations", stationMap.getOrDefault(line.getLineId(), new ArrayList<>()));
            lineDetails.add(lineDetail);
        }

        result.put("city", "广州");
        result.put("totalLines", lines.size());
        result.put("totalStations", stations.size());
        result.put("lines", lineDetails);

        return result;
    }

    @Override
    public Map<String, Object> getLineDetail(String lineId) {
        Map<String, Object> result = new HashMap<>();

        // 获取线路信息
        MetroLine line = metroLineMapper.findByLineId(lineId);
        if (line == null) {
            return null;
        }

        // 获取站点列表
        List<MetroStation> stations = metroStationMapper.findByLineId(lineId);

        result.put("id", line.getId());
        result.put("lineId", line.getLineId());
        result.put("lineName", line.getLineName());
        result.put("lineCode", line.getLineCode());
        result.put("startStation", line.getStartStation());
        result.put("endStation", line.getEndStation());
        result.put("distance", line.getDistance());
        result.put("stationCount", stations.size());
        result.put("path", line.getPath());
        result.put("status", line.getStatus());
        result.put("stations", stations);

        return result;
    }

    @Override
    public List<MetroTransfer> getAllTransfers() {
        return metroTransferMapper.findAll();
    }

    @Override
    public List<MetroTransfer> getTransfersByStationName(String stationName) {
        return metroTransferMapper.findByStationName(stationName);
    }

    @Override
    public List<MetroStation> searchStations(String keyword) {
        return metroStationMapper.findByStationNameLike(keyword);
    }

    @Override
    public Map<String, Object> getSchedule(String lineId) {
        Map<String, Object> result = new HashMap<>();

        // 获取线路信息
        MetroLine line = metroLineMapper.findByLineId(lineId);
        if (line == null) {
            return null;
        }

        // 获取站点列表（按顺序排序）
        List<MetroStation> stations = metroStationMapper.findByLineId(lineId);

        // 构建响应数据
        result.put("lineId", line.getLineId());
        result.put("lineName", line.getLineName());
        result.put("color", line.getColor());
        result.put("firstTrainTime", line.getFirstTrainTime() != null ? line.getFirstTrainTime().toString() : "06:00");
        result.put("lastTrainTime", line.getLastTrainTime() != null ? line.getLastTrainTime().toString() : "23:00");
        result.put("interval", line.getInterval() != null ? line.getInterval() : 300);
        result.put("segmentTime", line.getSegmentTime() != null ? line.getSegmentTime() : 180);
        result.put("dwellTime", line.getDwellTime() != null ? line.getDwellTime() : 30);
        result.put("trainCount", line.getTrainCount() != null ? line.getTrainCount() : 10);
        
        // 站点信息
        List<Map<String, Object>> stationList = stations.stream().map(s -> {
            Map<String, Object> station = new HashMap<>();
            station.put("id", s.getId());
            station.put("name", s.getStationName());
            station.put("lng", s.getLongitude());
            station.put("lat", s.getLatitude());
            station.put("sequence", s.getSequence());
            return station;
        }).collect(Collectors.toList());
        result.put("stations", stationList);

        return result;
    }

    @Override
    public Map<String, Object> getTrains(String lineId) {
        Map<String, Object> result = new HashMap<>();

        // 获取线路信息
        MetroLine line = metroLineMapper.findByLineId(lineId);
        if (line == null) {
            return null;
        }

        // 获取发车间隔（默认5分钟）
        int interval = line.getInterval() != null ? line.getInterval() : 300;
        int trainCount = line.getTrainCount() != null ? line.getTrainCount() : 10;
        
        // 生成 trainCount 辆列车
        List<Map<String, Object>> trains = new ArrayList<>();
        for (int i = 0; i < trainCount; i++) {
            Map<String, Object> train = new HashMap<>();
            train.put("trainId", "T" + String.format("%03d", i + 1));
            train.put("startOffset", i * interval);  // 发车时间偏移量（秒）
            trains.add(train);
        }

        result.put("trains", trains);
        return result;
    }

    // ---- 线路写操作 ----

    @Override
    public MetroLine addLine(MetroLine line) {
        metroLineMapper.insert(line);
        return line;
    }

    @Override
    public int updateLine(MetroLine line) {
        return metroLineMapper.update(line);
    }

    @Override
    public int updateLinePath(Long id, String path) {
        return metroLineMapper.updatePath(id, path);
    }

    @Override
    public int deleteLine(Long id) {
        return metroLineMapper.deleteById(id);
    }

    // ---- 站点写操作 ----

    @Override
    public MetroStation addStation(MetroStation station) {
        metroStationMapper.insert(station);
        return station;
    }

    @Override
    public int updateStation(MetroStation station) {
        return metroStationMapper.update(station);
    }

    @Override
    public int updateStationPosition(Long id, BigDecimal longitude, BigDecimal latitude) {
        return metroStationMapper.updatePosition(id, longitude, latitude);
    }

    @Override
    public int deleteStation(Long id) {
        return metroStationMapper.deleteById(id);
    }

    // ---- 换乘写操作 ----

    @Override
    public MetroTransfer addTransfer(MetroTransfer transfer) {
        metroTransferMapper.insert(transfer);
        return transfer;
    }

    @Override
    public int updateTransfer(MetroTransfer transfer) {
        return metroTransferMapper.update(transfer);
    }

    @Override
    public int deleteTransfer(Long id) {
        return metroTransferMapper.deleteById(id);
    }

    // ---- 时刻表 ----

    @Override
    public int updateSchedule(String lineId, MetroLine params) {
        MetroLine line = metroLineMapper.findByLineId(lineId);
        if (line == null) return 0;
        line.setFirstTrainTime(params.getFirstTrainTime());
        line.setLastTrainTime(params.getLastTrainTime());
        line.setInterval(params.getInterval());
        line.setSegmentTime(params.getSegmentTime());
        line.setDwellTime(params.getDwellTime());
        line.setTrainCount(params.getTrainCount());
        return metroLineMapper.update(line);
    }
}
