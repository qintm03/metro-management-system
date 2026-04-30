package com.example.springboot.service;

import com.example.springboot.entity.Discusszhandianchaxun;
import com.example.springboot.mapper.DiscusszhandianchaxunMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscusszhandianchaxunService {

    @Autowired
    private DiscusszhandianchaxunMapper discusszhandianchaxunMapper;

    public int add(Discusszhandianchaxun discusszhandianchaxun) {
        return discusszhandianchaxunMapper.insert(discusszhandianchaxun);
    }

    public int update(Discusszhandianchaxun discusszhandianchaxun) {
        return discusszhandianchaxunMapper.update(discusszhandianchaxun);
    }

    public int reply(Integer id, String reply) {
        return discusszhandianchaxunMapper.reply(id, reply);
    }

    public int delete(Integer id) {
        return discusszhandianchaxunMapper.deleteById(id);
    }

    public Discusszhandianchaxun findById(Integer id) {
        return discusszhandianchaxunMapper.selectById(id);
    }

    public List<Discusszhandianchaxun> findAll() {
        return discusszhandianchaxunMapper.selectAll();
    }

    public List<Discusszhandianchaxun> findByRefid(Integer refid) {
        return discusszhandianchaxunMapper.selectByRefid(refid);
    }

    public List<Discusszhandianchaxun> findByUserid(Integer userid) {
        return discusszhandianchaxunMapper.selectByUserid(userid);
    }

    public Integer countByRefid(Integer refid) {
        return discusszhandianchaxunMapper.countByRefid(refid);
    }

    public List<Discusszhandianchaxun> findByCondition(Discusszhandianchaxun discusszhandianchaxun) {
        return discusszhandianchaxunMapper.selectByCondition(discusszhandianchaxun);
    }

}
