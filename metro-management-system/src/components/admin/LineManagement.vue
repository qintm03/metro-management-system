<template>
  <div class="line-management">
    <el-page-header title="线路管理" />
    
    <div class="content-wrapper">
      <el-alert title="功能开发中" type="info" :closable="false">
        <template #default>
          <p>该功能正在开发中，敬请期待...</p>
          <p>功能包括：</p>
          <ul>
            <li>地图数据管理（导入、编辑、属性数据录入）</li>
            <li>线路查询</li>
            <li>导航功能</li>
            <li>线路渲染</li>
            <li>站点渲染</li>
            <li>空间查询</li>
            <li>唯一值渲染</li>
            <li>分类渲染</li>
            <li>空间查询渲染</li>
          </ul>
        </template>
      </el-alert>
    </div>
  </div>
</template>

<script setup>

</script>

<style scoped>
.line-management {
  padding: 20px;
}

.content-wrapper {
  margin-top: 20px;
}
</style>
