package com.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;


import com.dao.DiscusszhandianzhoubianDao;
import com.entity.DiscusszhandianzhoubianEntity;
import com.service.DiscusszhandianzhoubianService;
import com.entity.vo.DiscusszhandianzhoubianVO;
import com.entity.view.DiscusszhandianzhoubianView;

@Service("discusszhandianzhoubianService")
public class DiscusszhandianzhoubianServiceImpl extends ServiceImpl<DiscusszhandianzhoubianDao, DiscusszhandianzhoubianEntity> implements DiscusszhandianzhoubianService {
	

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<DiscusszhandianzhoubianEntity> page = this.selectPage(
                new Query<DiscusszhandianzhoubianEntity>(params).getPage(),
                new EntityWrapper<DiscusszhandianzhoubianEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<DiscusszhandianzhoubianEntity> wrapper) {
		  Page<DiscusszhandianzhoubianView> page =new Query<DiscusszhandianzhoubianView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
    @Override
	public List<DiscusszhandianzhoubianVO> selectListVO(Wrapper<DiscusszhandianzhoubianEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public DiscusszhandianzhoubianVO selectVO(Wrapper<DiscusszhandianzhoubianEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<DiscusszhandianzhoubianView> selectListView(Wrapper<DiscusszhandianzhoubianEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public DiscusszhandianzhoubianView selectView(Wrapper<DiscusszhandianzhoubianEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}
