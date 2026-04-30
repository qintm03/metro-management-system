package com.example.springboot.mapper;

import com.example.springboot.entity.Discusschecixianlu;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DiscusschecixianluMapper {

    @Insert("INSERT INTO discusschecixianlu(refid, userid, avatarurl, nickname, content, reply, addtime) " +
            "VALUES(#{refid}, #{userid}, #{avatarurl}, #{nickname}, #{content}, #{reply}, NOW())")
    int insert(Discusschecixianlu discusschecixianlu);

    @Update("UPDATE discusschecixianlu SET refid=#{refid}, userid=#{userid}, avatarurl=#{avatarurl}, " +
            "nickname=#{nickname}, content=#{content}, reply=#{reply} WHERE id=#{id}")
    int update(Discusschecixianlu discusschecixianlu);

    @Update("UPDATE discusschecixianlu SET reply=#{reply} WHERE id=#{id}")
    int reply(@Param("id") Integer id, @Param("reply") String reply);

    @Delete("DELETE FROM discusschecixianlu WHERE id=#{id}")
    int deleteById(@Param("id") Integer id);

    @Select("SELECT * FROM discusschecixianlu WHERE id=#{id}")
    Discusschecixianlu selectById(@Param("id") Integer id);

    @Select("SELECT * FROM discusschecixianlu ORDER BY id DESC")
    List<Discusschecixianlu> selectAll();

    @Select("SELECT * FROM discusschecixianlu WHERE refid=#{refid} ORDER BY id DESC")
    List<Discusschecixianlu> selectByRefid(@Param("refid") Integer refid);

    @Select("SELECT * FROM discusschecixianlu WHERE userid=#{userid} ORDER BY id DESC")
    List<Discusschecixianlu> selectByUserid(@Param("userid") Integer userid);

    @Select("SELECT COUNT(*) FROM discusschecixianlu WHERE refid=#{refid}")
    Integer countByRefid(@Param("refid") Integer refid);

    List<Discusschecixianlu> selectByCondition(Discusschecixianlu discusschecixianlu);

}
