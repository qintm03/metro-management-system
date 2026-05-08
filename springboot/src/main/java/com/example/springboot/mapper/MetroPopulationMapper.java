package com.example.springboot.mapper;

import com.example.springboot.entity.MetroPopulation;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface MetroPopulationMapper {

    @Select("SELECT * FROM metro_population WHERE year = #{year} AND month = 0")
    List<MetroPopulation> getAnnualData(@Param("year") Integer year);

    @Select("SELECT * FROM metro_population WHERE year = #{year} AND month = #{month}")
    List<MetroPopulation> getMonthlyData(@Param("year") Integer year,
                                          @Param("month") Integer month);

    @Select("SELECT DISTINCT year FROM metro_population ORDER BY year DESC")
    List<Integer> getAvailableYears();

    @Select("SELECT DISTINCT year, month FROM metro_population WHERE month > 0 ORDER BY year, month")
    List<MetroPopulation> getAvailableYearMonths();

    @Insert("<script>" +
            "INSERT INTO metro_population " +
            "(station_name, longitude, latitude, year, month, population, crowd_level) " +
            "VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.stationName}, #{item.longitude}, #{item.latitude}, " +
            "#{item.year}, #{item.month}, #{item.population}, #{item.crowdLevel})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<MetroPopulation> list);

    // ---- 写操作 ----

    @Update("UPDATE metro_population SET population=#{population}, crowd_level=#{crowdLevel}, " +
            "updated_at=NOW() WHERE id=#{id}")
    int update(MetroPopulation record);

    @Delete("DELETE FROM metro_population WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    // ---- 条件查询 ----

    @Select("<script>" +
            "SELECT * FROM metro_population WHERE 1=1 " +
            "<if test='year != null'>AND year = #{year}</if> " +
            "<if test='month != null'>AND month = #{month}</if> " +
            "<if test='stationName != null and stationName != \"\"'>AND station_name LIKE CONCAT('%', #{stationName}, '%')</if> " +
            "ORDER BY year DESC, month DESC, station_name" +
            "</script>")
    List<MetroPopulation> queryByCondition(@Param("year") Integer year,
                                           @Param("month") Integer month,
                                           @Param("stationName") String stationName);
}
