package com.example.springboot.service;

import com.example.springboot.entity.Storeup;
import com.example.springboot.mapper.StoreupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreupService {

    @Autowired
    private StoreupMapper storeupMapper;

    public int add(Storeup storeup) {
        return storeupMapper.insert(storeup);
    }

    public int update(Storeup storeup) {
        return storeupMapper.update(storeup);
    }

    public int delete(Integer id) {
        return storeupMapper.deleteById(id);
    }

    public Storeup findById(Integer id) {
        return storeupMapper.selectById(id);
    }

    public List<Storeup> findAll() {
        return storeupMapper.selectAll();
    }

    public List<Storeup> findByUserid(Integer userid) {
        return storeupMapper.selectByUserid(userid);
    }

    public Storeup findByUserAndRef(Integer userid, String tablename, Integer refid) {
        return storeupMapper.selectByUserAndRef(userid, tablename, refid);
    }

    public Integer countByUserid(Integer userid) {
        return storeupMapper.countByUserid(userid);
    }

    public List<Storeup> findByCondition(Storeup storeup) {
        return storeupMapper.selectByCondition(storeup);
    }

    public boolean isFavorited(Integer userid, String tablename, Integer refid) {
        Storeup storeup = storeupMapper.selectByUserAndRef(userid, tablename, refid);
        return storeup != null;
    }

    public int cancelFavorite(Integer userid, String tablename, Integer refid) {
        Storeup storeup = storeupMapper.selectByUserAndRef(userid, tablename, refid);
        if (storeup != null) {
            return storeupMapper.deleteById(storeup.getId());
        }
        return 0;
    }

}
