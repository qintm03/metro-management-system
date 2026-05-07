import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import ElementPlus from 'element-plus'
import zhCn from "element-plus/es/locale/lang/zh-cn"
import 'element-plus/dist/index.css'//👆导入element-plus系列组件

import "@arcgis/core/assets/esri/themes/light/main.css" //导入arcgis样式
// 导入高德地图 AMapLoader 模块
// @amap/amap-jsapi-loader 是高德地图官方提供的 JavaScript API 加载器
// 用于加载高德地图 JavaScript API，方便在项目中使用高德地图服务


import router from './router.js'//导入路由配置文件

const app=createApp(App)

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用 Element Plus 并配置主题
app.use(ElementPlus, {
  locale: zhCn,
  zIndex: 3000,
  size: 'default',
  button: {
    autoInsertSpace: true
  }
})

app.use(router)
app.mount('#app')
