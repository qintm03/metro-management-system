<template>
  <div class="user-ticket-management">
    <el-page-header title="我的票务" />
    
    <div class="content-wrapper">
      <el-alert title="功能开发中" type="info" :closable="false">
        <template #default>
          <p>该功能正在开发中，敬请期待...</p>
          <p>功能包括：</p>
          <ul>
            <li>查看自己的票务</li>
            <li>票务购买记录查询</li>
          </ul>
        </template>
      </el-alert>
    </div>
  </div>
</template>

<script setup>

</script>

<style scoped>
.user-ticket-management {
  padding: 20px;
}

.content-wrapper {
  margin-top: 20px;
}
</style>
