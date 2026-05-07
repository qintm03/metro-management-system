<template>
  <div class="checixianlu-management">
    <!-- 页面标题 -->
    <el-page-header title="车次线路管理" @back="goBack" />
    
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="线路名称">
          <el-input v-model="searchForm.xianlumingcheng" placeholder="请输入线路名称" clearable />
        </el-form-item>
        <el-form-item label="起点站">
          <el-input v-model="searchForm.qidianzhan" placeholder="请输入起点站" clearable />
        </el-form-item>
        <el-form-item label="终点站">
          <el-input v-model="searchForm.zhongdianzhan" placeholder="请输入终点站" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>车次线路列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增线路
          </el