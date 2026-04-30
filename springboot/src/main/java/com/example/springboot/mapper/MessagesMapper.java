package com.example.springboot.mapper;

import com.example.springboot.entity.Messages;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessagesMapper {

    @Insert("INSERT INTO messages(userid, username, avatarurl, content, cpicture, reply, rpicture, addtime) " +
            "VALUES(#{userid}, #{username}, #{avatarurl}, #{content}, #{cpicture}, #{reply}, #{rpicture}, NOW())")
    int insert(Messages messages);

    @Update("UPDATE messages SET userid=#{userid}, username=#{username}, avatarurl=#{avatarurl}, " +
            "content=#{content}, cpicture=#{cpicture}, reply=#{reply}, rpicture=#{rpicture} WHERE id=#{id}")
    int update(Messages messages);

    @Update("UPDATE messages SET reply=#{reply}, rpicture=#{rpicture} WHERE id=#{id}")
    int reply(@Param("id") Integer id, @Param("reply") String reply, @Param("rpicture") String rpicture);

    @Delete("DELETE FROM messages WHERE id=#{id}")
    int deleteById(@Param("id") Integer id);

    @Select("SELECT * FROM messages WHERE id=#{id}")
    Messages selectById(@Param("id") Integer id);

    @Select("SELECT * FROM messages ORDER BY id DESC")
    List<Messages> selectAll();

    @Select("SELECT * FROM messages WHERE userid=#{userid} ORDER BY id DESC")
    List<Messages> selectByUserid(@Param("userid") Integer userid);

    @Select("SELECT * FROM messages WHERE reply IS NULL OR reply = '' ORDER BY id DESC")
    List<Messages> selectUnreplied();

    @Select("SELECT COUNT(*) FROM messages WHERE userid=#{userid}")
    Integer countByUserid(@Param("userid") Integer userid);

    @Select("SELECT COUNT(*) FROM messages WHERE reply IS NULL OR reply = ''")
    Integer countUnreplied();

    List<Messages> selectByCondition(Messages messages);

}
