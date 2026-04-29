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

import com.entity.DiscusschecixianluEntity;
import com.entity.view.DiscusschecixianluView;

import com.service.DiscusschecixianluService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;

/**
 * 车次线路评论表
 * 后端接口
 * @author 
 * @email 
 * @date 2023-06-26 07:51:35
 */
@RestController
@RequestMapping("/discusschecixianlu")
public class DiscusschecixianluController {
    @Autowired
    private DiscusschecixianluService discusschecixianluService;




    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,DiscusschecixianluEntity discusschecixianlu, 
		HttpServletRequest request){

        EntityWrapper<DiscusschecixianluEntity> ew = new EntityWrapper<DiscusschecixianluEntity>();


		PageUtils page = discusschecixianluService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discusschecixianlu), params), params));
        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,DiscusschecixianluEntity discusschecixianlu, 
		HttpServletRequest request){
        EntityWrapper<DiscusschecixianluEntity> ew = new EntityWrapper<DiscusschecixianluEntity>();

		PageUtils page = discusschecixianluService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discusschecixianlu), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( DiscusschecixianluEntity discusschecixianlu){
       	EntityWrapper<DiscusschecixianluEntity> ew = new EntityWrapper<DiscusschecixianluEntity>();
      	ew.allEq(MPUtil.allEQMapPre( discusschecixianlu, "discusschecixianlu")); 
        return R.ok().put("data", discusschecixianluService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(DiscusschecixianluEntity discusschecixianlu){
        EntityWrapper< DiscusschecixianluEntity> ew = new EntityWrapper< DiscusschecixianluEntity>();
 		ew.allEq(MPUtil.allEQMapPre( discusschecixianlu, "discusschecixianlu")); 
		DiscusschecixianluView discusschecixianluView =  discusschecixianluService.selectView(ew);
		return R.ok("查询车次线路评论表成功").put("data", discusschecixianluView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        DiscusschecixianluEntity discusschecixianlu = discusschecixianluService.selectById(id);
        return R.ok().put("data", discusschecixianlu);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        DiscusschecixianluEntity discusschecixianlu = discusschecixianluService.selectById(id);
        return R.ok().put("data", discusschecixianlu);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody DiscusschecixianluEntity discusschecixianlu, HttpServletRequest request){
    	discusschecixianlu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(discusschecixianlu);

        discusschecixianluService.insert(discusschecixianlu);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody DiscusschecixianluEntity discusschecixianlu, HttpServletRequest request){
    	discusschecixianlu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(discusschecixianlu);

        discusschecixianluService.insert(discusschecixianlu);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody DiscusschecixianluEntity discusschecixianlu, HttpServletRequest request){
        //ValidatorUtils.validateEntity(discusschecixianlu);
        discusschecixianluService.updateById(discusschecixianlu);//全部更新
        return R.ok();
    }

    
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        discusschecixianluService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	









}
