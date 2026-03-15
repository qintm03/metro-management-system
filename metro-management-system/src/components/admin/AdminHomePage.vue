<template>
  <div class="admin-home-page">
    <el-page-header title="运营监控" />
    
    <div class="content-wrapper">
      <el-alert title="功能开发中" type="info" :closable="false">
        <template #default>
          <p>该功能正在开发中，敬请期待...</p>
          <p>功能包括：</p>
          <ul>
            <li>实时监控线路状态</li>
            <li>实时监控站点状态</li>
            <li>客流数据可视化</li>
            <li>地图展示和导航</li>
          </ul>
        </template>
      </el-alert>
    </div>
  </div>
</template>

<script setup>

</script>

<style scoped>
.admin-home-page {
  padding: 20px;
}

.content-wrapper {
  margin-top: 20px;
}
</style>
