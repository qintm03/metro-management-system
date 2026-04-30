package com.example.springboot.mapper;

import com.example.springboot.entity.Discusszhandianchaxun;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DiscusszhandianchaxunMapper {

    @Insert("INSERT INTO discusszhandianchaxun(refid, userid, avatarurl, nickname, content, reply, addtime) " +
            "VALUES(#{refid}, #{userid}, #{avatarurl}, #{nickname}, #{content}, #{reply}, NOW())")
    int insert(Discusszhandianchaxun discusszhandianchaxun);

    @Update("UPDATE discusszhandianchaxun SET refid=#{refid}, userid=#{userid}, avatarurl=#{avatarurl}, " +
            "nickname=#{nickname}, content=#{content}, reply=#{reply} WHERE id=#{id}")
    int update(Discusszhandianchaxun discusszhandianchaxun);

    @Update("UPDATE discusszhandianchaxun SET reply=#{reply} WHERE id=#{id}")
    int reply(@Param("id") Integer id, @Param("reply") String reply);

    @Delete("DELETE FROM discusszhandianchaxun WHERE id=#{id}")
    int deleteById(@Param("id") Integer id);

    @Select("SELECT * FROM discusszhandianchaxun WHERE id=#{id}")
    Discusszhandianchaxun selectById(@Param("id") Integer id);

    @Select("SELECT * FROM discusszhandianchaxun ORDER BY id DESC")
    List<Discusszhandianchaxun> selectAll();

    @Select("SELECT * FROM discusszhandianchaxun WHERE refid=#{refid} ORDER BY id DESC")
    List<Discusszhandianchaxun> selectByRefid(@Param("refid") Integer refid);

    @Select("SELECT * FROM discusszhandianchaxun WHERE userid=#{userid} ORDER BY id DESC")
    List<Discusszhandianchaxun> selectByUserid(@Param("userid") Integer userid);

    @Select("SELECT COUNT(*) FROM discusszhandianchaxun WHERE refid=#{refid}")
    Integer countByRefid(@Param("refid") Integer refid);

    List<Discusszhandianchaxun> selectByCondition(Discusszhandianchaxun discusszhandianchaxun);

}
