package com.example.springboot.mapper;

import com.example.springboot.entity.MetroLine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 地铁线路数据访问层
 */
@Mapper
public interface MetroLineMapper {

    /**
     * 查询所有线路
     */
    @Select("SELECT * FROM metro_line WHERE status = 1 ORDER BY id")
    List<MetroLine> findAll();

    /**
     * 根据ID查询线路
     */
    @Select("SELECT * FROM metro_line WHERE id = #{id} AND status = 1")
    MetroLine findById(@Param("id") Long id);

    /**
     * 根据线路唯一标识查询
     */
    @Select("SELECT * FROM metro_line WHERE line_id = #{lineId} AND status = 1")
    MetroLine findByLineId(@Param("lineId") String lineId);

    /**
     * 根据线路编码查询
     */
    @Select("SELECT * FROM metro_line WHERE line_code = #{lineCode} AND status = 1")
    MetroLine findByLineCode(@Param("lineCode") String lineCode);

    /**
     * 查询所有线路（包含站点数量统计）
     */
    @Select("SELECT l.*, COUNT(s.id) as station_count FROM metro_line l " +
            "LEFT JOIN metro_station s ON l.line_id = s.line_id AND s.status = 1 " +
            "WHERE l.status = 1 GROUP BY l.id ORDER BY l.id")
    List<MetroLine> findAllWithStationCount();
}
