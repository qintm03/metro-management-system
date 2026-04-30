package com.example.springboot.service;

import com.example.springboot.entity.Messages;
import com.example.springboot.mapper.MessagesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessagesService {

    @Autowired
    private MessagesMapper messagesMapper;

    public int add(Messages messages) {
        return messagesMapper.insert(messages);
    }

    public int update(Messages messages) {
        return messagesMapper.update(messages);
    }

    public int reply(Integer id, String reply, String rpicture) {
        return messagesMapper.reply(id, reply, rpicture);
    }

    public int delete(Integer id) {
        return messagesMapper.deleteById(id);
    }

    public Messages findById(Integer id) {
        return messagesMapper.selectById(id);
    }

    public List<Messages> findAll() {
        return messagesMapper.selectAll();
    }

    public List<Messages> findByUserid(Integer userid) {
        return messagesMapper.selectByUserid(userid);
    }

    public List<Messages> findUnreplied() {
        return messagesMapper.selectUnreplied();
    }

    public Integer countByUserid(Integer userid) {
        return messagesMapper.countByUserid(userid);
    }

    public Integer countUnreplied() {
        return messagesMapper.countUnreplied();
    }

    public List<Messages> findByCondition(Messages messages) {
        return messagesMapper.selectByCondition(messages);
    }

}
