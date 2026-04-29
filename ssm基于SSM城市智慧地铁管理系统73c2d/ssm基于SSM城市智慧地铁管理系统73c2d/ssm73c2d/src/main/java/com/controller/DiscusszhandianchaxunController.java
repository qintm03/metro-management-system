package com.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import com.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.annotation.IgnoreAuth;

import com.entity.DiscusszhandianchaxunEntity;
import com.entity.view.DiscusszhandianchaxunView;

import com.service.DiscusszhandianchaxunService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;

/**
 * 站点查询评论表
 * 后端接口
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
@RestController
@RequestMapping("/discusszhandianchaxun")
public class DiscusszhandianchaxunController {
    @Autowired
    private DiscusszhandianchaxunService discusszhandianchaxunService;




    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,DiscusszhandianchaxunEntity discusszhandianchaxun, 
		HttpServletRequest request){

        EntityWrapper<DiscusszhandianchaxunEntity> ew = new EntityWrapper<DiscusszhandianchaxunEntity>();


		PageUtils page = discusszhandianchaxunService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discusszhandianchaxun), params), params));
        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,DiscusszhandianchaxunEntity discusszhandianchaxun, 
		HttpServletRequest request){
        EntityWrapper<DiscusszhandianchaxunEntity> ew = new EntityWrapper<DiscusszhandianchaxunEntity>();

		PageUtils page = discusszhandianchaxunService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discusszhandianchaxun), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( DiscusszhandianchaxunEntity discusszhandianchaxun){
       	EntityWrapper<DiscusszhandianchaxunEntity> ew = new EntityWrapper<DiscusszhandianchaxunEntity>();
      	ew.allEq(MPUtil.allEQMapPre( discusszhandianchaxun, "discusszhandianchaxun")); 
        return R.ok().put("data", discusszhandianchaxunService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(DiscusszhandianchaxunEntity discusszhandianchaxun){
        EntityWrapper< DiscusszhandianchaxunEntity> ew = new EntityWrapper< DiscusszhandianchaxunEntity>();
 		ew.allEq(MPUtil.allEQMapPre( discusszhandianchaxun, "discusszhandianchaxun")); 
		DiscusszhandianchaxunView discusszhandianchaxunView =  discusszhandianchaxunService.selectView(ew);
		return R.ok("查询站点查询评论表成功").put("data", discusszhandianchaxunView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        DiscusszhandianchaxunEntity discusszhandianchaxun = discusszhandianchaxunService.selectById(id);
        return R.ok().put("data", discusszhandianchaxun);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        DiscusszhandianchaxunEntity discusszhandianchaxun = discusszhandianchaxunService.selectById(id);
        return R.ok().put("data", discusszhandianchaxun);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody DiscusszhandianchaxunEntity discusszhandianchaxun, HttpServletRequest request){
    	discusszhandianchaxun.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(discusszhandianchaxun);

        discusszhandianchaxunService.insert(discusszhandianchaxun);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody DiscusszhandianchaxunEntity discusszhandianchaxun, HttpServletRequest request){
    	discusszhandianchaxun.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(discusszhandianchaxun);

        discusszhandianchaxunService.insert(discusszhandianchaxun);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody DiscusszhandianchaxunEntity discusszhandianchaxun, HttpServletRequest request){
        //ValidatorUtils.validateEntity(discusszhandianchaxun);
        discusszhandianchaxunService.updateById(discusszhandianchaxun);//全部更新
        return R.ok();
    }

    
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        discusszhandianchaxunService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	









}
