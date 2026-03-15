<template>
  <div id="container"></div>
</template>

<script setup>
import { onMounted, onUnmounted } from "vue";
import AMapLoader from "@amap/amap-jsapi-loader";

let map = null;

onMounted(() => {
  window._AMapSecurityConfig = {
    securityJsCode: "f2e281bddbaaed3bfc03b711e00122fa",
  };
  AMapLoader.load({
    key: "49f3c3fd7e6380f9c8b84c0f0bdd4d9f",
    version: "2.0",
    plugins: ["AMap.Scale", "AMap.Buildings"],
  })
    .then((AMap) => {
      var buildingLayer = new AMap.Buildings({
        heightFactor: 1,
        wallColor: [255, 0, 0, 1],
        roofColor: 'rgba(0,0,255,0.5)',
        zooms: [16, 26],
        zindex: 10,
      });

      map = new AMap.Map("container", {
        pitch: 50, //地图俯仰角度，有效范围 0 度- 83 度
        viewMode: '3D', //地图模式
        rotateEnable: true, //是否开启地图旋转交互 鼠标右键 + 鼠标画圈移动 或 键盘Ctrl + 鼠标左键画圈移动
        pitchEnable: true, //是否开启地图倾斜交互 鼠标右键 + 鼠标上下移动或键盘Ctrl + 鼠标左键上下移动
        zoom: 17, //初始化地图层级
        rotation: -15, //初始地图顺时针旋转的角度
        zooms: [2, 26], //地图显示的缩放级别范围
        center: [116.333926, 39.997245], //初始地图中心经纬度,
        mapstyle: 'amap://styles/grey', // 设置地图的显示样式
        layers: [
          new AMap.TileLayer(),
         
          buildingLayer
        ],
      });

      // 添加建筑物图层


    })
    
});

</script>



<style scoped>
#container {
  width: 100%;
  height: 120vh;

}
</style>