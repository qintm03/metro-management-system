<template>
  <div class="admin-track-management">
    <el-page-header title="轨迹管理" />
    
    <div class="content-wrapper">
      <el-alert title="功能开发中" type="info" :closable="false">
        <template #default>
          <p>该功能正在开发中，敬请期待...</p>
          <p>功能包括：</p>
          <ul>
            <li>轨迹数据管理（所有用户轨迹）</li>
            <li>轨迹可视化</li>
            <li>轨迹查询</li>
          </ul>
        </template>
      </el-alert>
    </div>
  </div>
</template>

<script setup>

</script>

<style scoped>
.admin-track-management {
  padding: 20px;
}

.content-wrapper {
  margin-top: 20px;
}
</style>
