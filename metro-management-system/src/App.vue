<template>
  <div class="view">
  <Vscalescreen full-screen="true" >
    <div class="navi">
      <RouterView />
    </div>
  </Vscalescreen>
</div>
</template>

<script setup>

  import Vscalescreen from 'v-scale-screen';
  
 </script>

<style scoped>
.view, .navi {
  height: 100%;
}
</style>