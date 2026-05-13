<template>
  <div class="admin-ticket-management">
    <el-page-header title="票务管理" />
    
    <div class="content-wrapper">
      <el-alert title="功能开发中" type="info" :closable="false">
        <template #default>
          <p>该功能正在开发中，敬请期待...</p>
         
        </template>
      </el-alert>
    </div>
  </div>
</template>

<script setup>

</script>

<style scoped>
.admin-ticket-management {
  padding: 20px;
}

.content-wrapper {
  margin-top: 20px;
}
</style>
