package com.example.springboot.mapper;

import com.example.springboot.entity.MetroLine;
import org.apache.ibatis.annotations.*;

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

    // ---- 写操作 ----

    @Insert("INSERT INTO metro_line(line_id, line_name, line_code, color, start_station, end_station, " +
            "first_train_time, last_train_time, `interval`, segment_time, dwell_time, distance, station_count, " +
            "path, status, created_at, updated_at) " +
            "VALUES(#{lineId}, #{lineName}, #{lineCode}, #{color}, #{startStation}, #{endStation}, " +
            "#{firstTrainTime}, #{lastTrainTime}, #{interval}, #{segmentTime}, #{dwellTime}, #{distance}, #{stationCount}, " +
            "#{path}, 1, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MetroLine line);

    @Update("UPDATE metro_line SET line_name=#{lineName}, color=#{color}, start_station=#{startStation}, " +
            "end_station=#{endStation}, first_train_time=#{firstTrainTime}, last_train_time=#{lastTrainTime}, " +
            "`interval`=#{interval}, segment_time=#{segmentTime}, dwell_time=#{dwellTime}, " +
            "distance=#{distance}, path=#{path}, updated_at=NOW() " +
            "WHERE id=#{id}")
    int update(MetroLine line);

    @Update("UPDATE metro_line SET path=#{path}, updated_at=NOW() WHERE id=#{id}")
    int updatePath(@Param("id") Long id, @Param("path") String path);

    @Delete("UPDATE metro_line SET status = 0, updated_at = NOW() WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
