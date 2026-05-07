package com.example.springboot.mapper;

import com.example.springboot.entity.MetroStation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 地铁站点数据访问层
 */
@Mapper
public interface MetroStationMapper {

    /**
     * 查询所有站点
     */
    @Select("SELECT * FROM metro_station WHERE status = 1 ORDER BY line_id, sequence")
    List<MetroStation> findAll();

    /**
     * 根据ID查询站点
     */
    @Select("SELECT * FROM metro_station WHERE id = #{id} AND status = 1")
    MetroStation findById(@Param("id") Long id);

    /**
     * 根据线路ID查询站点
     */
    @Select("SELECT * FROM metro_station WHERE line_id = #{lineId} AND status = 1 ORDER BY sequence")
    List<MetroStation> findByLineId(@Param("lineId") String lineId);

    /**
     * 查询所有站点（包含线路信息）
     */
    @Select("SELECT s.*, l.line_name as line_name FROM metro_station s " +
            "LEFT JOIN metro_line l ON s.line_id = l.line_id " +
            "WHERE s.status = 1 ORDER BY s.line_id, s.sequence")
    List<MetroStation> findAllWithLineInfo();

    /**
     * 根据线路ID查询站点（包含线路信息）
     */
    @Select("SELECT s.*, l.line_name as line_name FROM metro_station s " +
            "LEFT JOIN metro_line l ON s.line_id = l.line_id " +
            "WHERE s.line_id = #{lineId} AND s.status = 1 ORDER BY s.sequence")
    List<MetroStation> findByLineIdWithLineInfo(@Param("lineId") String lineId);

    /**
     * 查询换乘站
     */
    @Select("SELECT * FROM metro_station WHERE is_transfer = 1 AND status = 1 ORDER BY station_name")
    List<MetroStation> findTransferStations();

    /**
     * 根据站点名称查询
     */
    @Select("SELECT * FROM metro_station WHERE station_name = #{stationName} AND status = 1")
    List<MetroStation> findByStationName(@Param("stationName") String stationName);

    /**
     * 根据站点名称模糊查询
     */
    @Select("SELECT * FROM metro_station WHERE station_name LIKE CONCAT('%', #{keyword}, '%') AND status = 1")
    List<MetroStation> findByStationNameLike(@Param("keyword") String keyword);
}
