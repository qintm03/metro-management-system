package com.example.springboot.mapper;

import com.example.springboot.entity.Storeup;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StoreupMapper {

    @Insert("INSERT INTO storeup(userid, refid, tablename, name, picture, type, inteltype, remark, addtime) " +
            "VALUES(#{userid}, #{refid}, #{tablename}, #{name}, #{picture}, #{type}, #{inteltype}, #{remark}, NOW())")
    int insert(Storeup storeup);

    @Update("UPDATE storeup SET userid=#{userid}, refid=#{refid}, tablename=#{tablename}, name=#{name}, " +
            "picture=#{picture}, type=#{type}, inteltype=#{inteltype}, remark=#{remark} WHERE id=#{id}")
    int update(Storeup storeup);

    @Delete("DELETE FROM storeup WHERE id=#{id}")
    int deleteById(@Param("id") Integer id);

    @Select("SELECT * FROM storeup WHERE id=#{id}")
    Storeup selectById(@Param("id") Integer id);

    @Select("SELECT * FROM storeup ORDER BY id DESC")
    List<Storeup> selectAll();

    @Select("SELECT * FROM storeup WHERE userid=#{userid} ORDER BY id DESC")
    List<Storeup> selectByUserid(@Param("userid") Integer userid);

    @Select("SELECT * FROM storeup WHERE userid=#{userid} AND tablename=#{tablename} AND refid=#{refid} LIMIT 1")
    Storeup selectByUserAndRef(@Param("userid") Integer userid, @Param("tablename") String tablename, @Param("refid") Integer refid);

    @Select("SELECT COUNT(*) FROM storeup WHERE userid=#{userid}")
    Integer countByUserid(@Param("userid") Integer userid);

    List<Storeup> selectByCondition(Storeup storeup);

}
