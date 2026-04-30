package com.example.springboot.mapper;

import com.example.springboot.entity.Discusszhandianzhoubian;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DiscusszhandianzhoubianMapper {

    @Insert("INSERT INTO discusszhandianzhoubian(refid, userid, avatarurl, nickname, content, reply, addtime) " +
            "VALUES(#{refid}, #{userid}, #{avatarurl}, #{nickname}, #{content}, #{reply}, NOW())")
    int insert(Discusszhandianzhoubian discusszhandianzhoubian);

    @Update("UPDATE discusszhandianzhoubian SET refid=#{refid}, userid=#{userid}, avatarurl=#{avatarurl}, " +
            "nickname=#{nickname}, content=#{content}, reply=#{reply} WHERE id=#{id}")
    int update(Discusszhandianzhoubian discusszhandianzhoubian);

    @Update("UPDATE discusszhandianzhoubian SET reply=#{reply} WHERE id=#{id}")
    int reply(@Param("id") Integer id, @Param("reply") String reply);

    @Delete("DELETE FROM discusszhandianzhoubian WHERE id=#{id}")
    int deleteById(@Param("id") Integer id);

    @Select("SELECT * FROM discusszhandianzhoubian WHERE id=#{id}")
    Discusszhandianzhoubian selectById(@Param("id") Integer id);

    @Select("SELECT * FROM discusszhandianzhoubian ORDER BY id DESC")
    List<Discusszhandianzhoubian> selectAll();

    @Select("SELECT * FROM discusszhandianzhoubian WHERE refid=#{refid} ORDER BY id DESC")
    List<Discusszhandianzhoubian> selectByRefid(@Param("refid") Integer refid);

    @Select("SELECT * FROM discusszhandianzhoubian WHERE userid=#{userid} ORDER BY id DESC")
    List<Discusszhandianzhoubian> selectByUserid(@Param("userid") Integer userid);

    @Select("SELECT COUNT(*) FROM discusszhandianzhoubian WHERE refid=#{refid}")
    Integer countByRefid(@Param("refid") Integer refid);

    List<Discusszhandianzhoubian> selectByCondition(Discusszhandianzhoubian discusszhandianzhoubian);

}
