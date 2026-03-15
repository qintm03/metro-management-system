<template>
  <div class="map-management">
    <el-page-header title="地图管理" />
    
    <div class="content-wrapper">
      <el-alert title="功能开发中" type="info" :closable="false">
        <template #default>
          <p>该功能正在开发中，敬请期待...</p>
          <p>功能包括：</p>
          <ul>
            <li>自动寻路</li>
            <li>图层管理</li>
          </ul>
        </template>
      </el-alert>
    </div>
  </div>
</template>

<script setup>

</script>

<style scoped>
.map-management {
  padding: 20px;
}

.content-wrapper {
  margin-top: 20px;
}
</style>
