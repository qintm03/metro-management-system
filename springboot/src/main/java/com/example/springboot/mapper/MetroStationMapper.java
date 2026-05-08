package com.example.springboot.mapper;

import com.example.springboot.entity.MetroStation;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
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

    // ---- 写操作 ----

    @Insert("INSERT INTO metro_station(station_name, line_id, sequence, longitude, latitude, " +
            "is_transfer, transfer_count, status, created_at, updated_at) " +
            "VALUES(#{stationName}, #{lineId}, #{sequence}, #{longitude}, #{latitude}, " +
            "#{isTransfer}, #{transferCount}, 1, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MetroStation station);

    @Update("UPDATE metro_station SET station_name=#{stationName}, sequence=#{sequence}, " +
            "longitude=#{longitude}, latitude=#{latitude}, is_transfer=#{isTransfer}, " +
            "transfer_count=#{transferCount}, updated_at=NOW() WHERE id=#{id}")
    int update(MetroStation station);

    @Update("UPDATE metro_station SET longitude=#{longitude}, latitude=#{latitude}, updated_at=NOW() WHERE id=#{id}")
    int updatePosition(@Param("id") Long id, @Param("longitude") BigDecimal longitude, @Param("latitude") BigDecimal latitude);

    @Delete("UPDATE metro_station SET status = 0, updated_at = NOW() WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
