<template>
  <div class="user-home-page">
    <el-page-header title="用户首页" />
    
    <div class="content-wrapper">
      <el-alert title="功能开发中" type="info" :closable="false">
        <template #default>
          <p>该功能正在开发中，敬请期待...</p>
          <p>功能包括：</p>
          <ul>
            <li>欢迎信息</li>
            <li>快捷入口（线路查询、站点查询、换乘方案推荐）</li>
          </ul>
        </template>
      </el-alert>
    </div>
  </div>
</template>

<script setup>

</script>

<style scoped>
.user-home-page {
  padding: 20px;
}

.content-wrapper {
  margin-top: 20px;
}
</style>
