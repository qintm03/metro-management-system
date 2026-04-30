package com.example.springboot.mapper;

import com.example.springboot.entity.News;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NewsMapper {

    @Insert("INSERT INTO news(title, introduction, picture, content, addtime) " +
            "VALUES(#{title}, #{introduction}, #{picture}, #{content}, NOW())")
    int insert(News news);

    @Update("UPDATE news SET title=#{title}, introduction=#{introduction}, " +
            "picture=#{picture}, content=#{content} WHERE id=#{id}")
    int update(News news);

    @Delete("DELETE FROM news WHERE id=#{id}")
    int deleteById(@Param("id") Integer id);

    @Select("SELECT * FROM news WHERE id=#{id}")
    News selectById(@Param("id") Integer id);

    @Select("SELECT * FROM news ORDER BY id DESC")
    List<News> selectAll();

    @Select("SELECT * FROM news ORDER BY id DESC LIMIT #{limit}")
    List<News> selectByLimit(@Param("limit") Integer limit);

    List<News> selectByCondition(News news);

}
