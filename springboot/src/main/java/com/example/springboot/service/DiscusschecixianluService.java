package com.example.springboot.service;

import com.example.springboot.entity.Discusschecixianlu;
import com.example.springboot.mapper.DiscusschecixianluMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscusschecixianluService {

    @Autowired
    private DiscusschecixianluMapper discusschecixianluMapper;

    public int add(Discusschecixianlu discusschecixianlu) {
        return discusschecixianluMapper.insert(discusschecixianlu);
    }

    public int update(Discusschecixianlu discusschecixianlu) {
        return discusschecixianluMapper.update(discusschecixianlu);
    }

    public int reply(Integer id, String reply) {
        return discusschecixianluMapper.reply(id, reply);
    }

    public int delete(Integer id) {
        return discusschecixianluMapper.deleteById(id);
    }

    public Discusschecixianlu findById(Integer id) {
        return discusschecixianluMapper.selectById(id);
    }

    public List<Discusschecixianlu> findAll() {
        return discusschecixianluMapper.selectAll();
    }

    public List<Discusschecixianlu> findByRefid(Integer refid) {
        return discusschecixianluMapper.selectByRefid(refid);
    }

    public List<Discusschecixianlu> findByUserid(Integer userid) {
        return discusschecixianluMapper.selectByUserid(userid);
    }

    public Integer countByRefid(Integer refid) {
        return discusschecixianluMapper.countByRefid(refid);
    }

    public List<Discusschecixianlu> findByCondition(Discusschecixianlu discusschecixianlu) {
        return discusschecixianluMapper.selectByCondition(discusschecixianlu);
    }

}
