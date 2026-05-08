package com.example.springboot.mapper;

import com.example.springboot.entity.MetroTransfer;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 地铁换乘关系数据访问层
 */
@Mapper
public interface MetroTransferMapper {

    /**
     * 查询所有换乘关系
     */
    @Select("SELECT * FROM metro_transfer WHERE status = 1")
    List<MetroTransfer> findAll();

    /**
     * 根据站点名称查询换乘关系
     */
    @Select("SELECT * FROM metro_transfer WHERE station_name = #{stationName} AND status = 1")
    List<MetroTransfer> findByStationName(@Param("stationName") String stationName);

    /**
     * 根据当前线路ID查询换乘关系
     */
    @Select("SELECT * FROM metro_transfer WHERE from_line_id = #{fromLineId} AND status = 1")
    List<MetroTransfer> findByFromLineId(@Param("fromLineId") String fromLineId);

    /**
     * 根据站点名称和当前线路ID查询可换乘线路
     */
    @Select("SELECT * FROM metro_transfer WHERE station_name = #{stationName} AND from_line_id = #{fromLineId} AND status = 1")
    List<MetroTransfer> findByStationAndLine(@Param("stationName") String stationName, @Param("fromLineId") String fromLineId);

    // ---- 写操作 ----

    @Insert("INSERT INTO metro_transfer(station_name, from_line_id, to_line_id, to_line_name, status, created_at, updated_at) " +
            "VALUES(#{stationName}, #{fromLineId}, #{toLineId}, #{toLineName}, 1, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MetroTransfer transfer);

    @Update("UPDATE metro_transfer SET to_line_id=#{toLineId}, to_line_name=#{toLineName}, " +
            "updated_at=NOW() WHERE id=#{id}")
    int update(MetroTransfer transfer);

    @Delete("UPDATE metro_transfer SET status = 0, updated_at = NOW() WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
