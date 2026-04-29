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

import com.entity.ZhandianzhoubianEntity;
import com.entity.view.ZhandianzhoubianView;

import com.service.ZhandianzhoubianService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;
import com.service.StoreupService;
import com.entity.StoreupEntity;

/**
 * 站点周边
 * 后端接口
 * @author 
 * @email 
 * @date 2023-06-26 07:51:34
 */
@RestController
@RequestMapping("/zhandianzhoubian")
public class ZhandianzhoubianController {
    @Autowired
    private ZhandianzhoubianService zhandianzhoubianService;



    @Autowired
    private StoreupService storeupService;

    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,ZhandianzhoubianEntity zhandianzhoubian, 
		HttpServletRequest request){

        EntityWrapper<ZhandianzhoubianEntity> ew = new EntityWrapper<ZhandianzhoubianEntity>();


		PageUtils page = zhandianzhoubianService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, zhandianzhoubian), params), params));
        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,ZhandianzhoubianEntity zhandianzhoubian, 
		HttpServletRequest request){
        EntityWrapper<ZhandianzhoubianEntity> ew = new EntityWrapper<ZhandianzhoubianEntity>();

		PageUtils page = zhandianzhoubianService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, zhandianzhoubian), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( ZhandianzhoubianEntity zhandianzhoubian){
       	EntityWrapper<ZhandianzhoubianEntity> ew = new EntityWrapper<ZhandianzhoubianEntity>();
      	ew.allEq(MPUtil.allEQMapPre( zhandianzhoubian, "zhandianzhoubian")); 
        return R.ok().put("data", zhandianzhoubianService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ZhandianzhoubianEntity zhandianzhoubian){
        EntityWrapper< ZhandianzhoubianEntity> ew = new EntityWrapper< ZhandianzhoubianEntity>();
 		ew.allEq(MPUtil.allEQMapPre( zhandianzhoubian, "zhandianzhoubian")); 
		ZhandianzhoubianView zhandianzhoubianView =  zhandianzhoubianService.selectView(ew);
		return R.ok("查询站点周边成功").put("data", zhandianzhoubianView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        ZhandianzhoubianEntity zhandianzhoubian = zhandianzhoubianService.selectById(id);
        return R.ok().put("data", zhandianzhoubian);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        ZhandianzhoubianEntity zhandianzhoubian = zhandianzhoubianService.selectById(id);
        return R.ok().put("data", zhandianzhoubian);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ZhandianzhoubianEntity zhandianzhoubian, HttpServletRequest request){
    	zhandianzhoubian.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(zhandianzhoubian);

        zhandianzhoubianService.insert(zhandianzhoubian);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody ZhandianzhoubianEntity zhandianzhoubian, HttpServletRequest request){
    	zhandianzhoubian.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(zhandianzhoubian);

        zhandianzhoubianService.insert(zhandianzhoubian);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody ZhandianzhoubianEntity zhandianzhoubian, HttpServletRequest request){
        //ValidatorUtils.validateEntity(zhandianzhoubian);
        zhandianzhoubianService.updateById(zhandianzhoubian);//全部更新
        return R.ok();
    }

    
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        zhandianzhoubianService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	









}
