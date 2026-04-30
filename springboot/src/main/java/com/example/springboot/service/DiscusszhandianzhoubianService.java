package com.example.springboot.service;

import com.example.springboot.entity.Discusszhandianzhoubian;
import com.example.springboot.mapper.DiscusszhandianzhoubianMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscusszhandianzhoubianService {

    @Autowired
    private DiscusszhandianzhoubianMapper discusszhandianzhoubianMapper;

    public int add(Discusszhandianzhoubian discusszhandianzhoubian) {
        return discusszhandianzhoubianMapper.insert(discusszhandianzhoubian);
    }

    public int update(Discusszhandianzhoubian discusszhandianzhoubian) {
        return discusszhandianzhoubianMapper.update(discusszhandianzhoubian);
    }

    public int reply(Integer id, String reply) {
        return discusszhandianzhoubianMapper.reply(id, reply);
    }

    public int delete(Integer id) {
        return discusszhandianzhoubianMapper.deleteById(id);
    }

    public Discusszhandianzhoubian findById(Integer id) {
        return discusszhandianzhoubianMapper.selectById(id);
    }

    public List<Discusszhandianzhoubian> findAll() {
        return discusszhandianzhoubianMapper.selectAll();
    }

    public List<Discusszhandianzhoubian> findByRefid(Integer refid) {
        return discusszhandianzhoubianMapper.selectByRefid(refid);
    }

    public List<Discusszhandianzhoubian> findByUserid(Integer userid) {
        return discusszhandianzhoubianMapper.selectByUserid(userid);
    }

    public Integer countByRefid(Integer refid) {
        return discusszhandianzhoubianMapper.countByRefid(refid);
    }

    public List<Discusszhandianzhoubian> findByCondition(Discusszhandianzhoubian discusszhandianzhoubian) {
        return discusszhandianzhoubianMapper.selectByCondition(discusszhandianzhoubian);
    }

}
