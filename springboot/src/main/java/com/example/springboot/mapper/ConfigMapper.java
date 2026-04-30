package com.example.springboot.mapper;

import com.example.springboot.entity.Config;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ConfigMapper {

    @Insert("INSERT INTO config(name, value) VALUES(#{name}, #{value})")
    int insert(Config config);

    @Update("UPDATE config SET name=#{name}, value=#{value} WHERE id=#{id}")
    int update(Config config);

    @Delete("DELETE FROM config WHERE id=#{id}")
    int deleteById(@Param("id") Integer id);

    @Select("SELECT * FROM config WHERE id=#{id}")
    Config selectById(@Param("id") Integer id);

    @Select("SELECT * FROM config WHERE name=#{name}")
    Config selectByName(@Param("name") String name);

    @Select("SELECT * FROM config")
    List<Config> selectAll();

}
