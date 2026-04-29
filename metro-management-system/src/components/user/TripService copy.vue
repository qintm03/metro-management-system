<template>
  <div class="metro-nav">
    <!-- 左侧输入面板 -->
    <div class="input-panel">
      <h3>广州地铁导航</h3>
      
      <!-- 起点输入 -->
      <div class="input-group">
        <label>起点</label>
        <el-input
          v-model="startKeyword"
          placeholder="输入起点位置"
          clearable
        />
      </div>

      <!-- 交换按钮 -->
      <el-button type="primary" circle class="swap-btn" @click="swapLocations">
        <el-icon><Switch /></el-icon>
      </el-button>

      <!-- 终点输入 -->
      <div class="input-group">
        <label>终点</label>
        <el-input
          v-model="endKeyword"
          placeholder="输入终点位置"
          clearable
        />
      </div>

      <!-- 查询按钮 -->
      <el-button type="primary" class="search-btn" @click="searchRoute" :loading="loading">
        查询路线
      </el-button>
    </div>

    <!-- 右侧地图和路线结果 -->
    <div class="map-container">
      <div id="map" class="map"></div>
      <!-- 路线结果面板 -->
      <div id="panel" class="route-panel"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Switch } from '@element-plus/icons-vue'
import AMapLoader from '@amap/amap-jsapi-loader'

const map = ref(null)
const AMap = ref(null)
const transfer = ref(null)
const loading = ref(false)

// 起点和终点输入
const startKeyword = ref('')
const endKeyword = ref('')

// 交换起点终点
const swapLocations = () => {
  const temp = startKeyword.value
  startKeyword.value = endKeyword.value
  endKeyword.value = temp
}

// 搜索路线
const searchRoute = () => {
  if (!startKeyword.value || !endKeyword.value) {
    ElMessage.warning('请输入起点和终点')
    return
  }

  loading.value = true

  // 清除之前的路线
  if (transfer.value) {
    transfer.value.clear()
  }

  // 构造公交换乘类
  transfer.value = new AMap.value.Transfer({
    map: map.value,
    city: '广州市',
    panel: 'panel',
    policy: AMap.value.TransferPolicy.LEAST_TIME
  })

  // 根据起、终点名称查询公交换乘路线
  transfer.value.search([
    { keyword: startKeyword.value, city: '广州' },
    { keyword: endKeyword.value, city: '广州' }
  ], (status, result) => {
    loading.value = false
    
    if (status === 'complete') {
      ElMessage.success('绘制公交路线完成')
    } else {
      ElMessage.error('公交路线数据查询失败：' + result)
    }
  })
}

onMounted(async () => {
  try {
    window._AMapSecurityConfig = {
      securityJsCode: 'f2e281bddbaaed3bfc03b711e00122fa'
    }

    const amap = await AMapLoader.load({
      key: '49f3c3fd7e6380f9c8b84c0f0bdd4d9f',
      version: '2.0',
      plugins: [
        'AMap.Scale', 
        'AMap.ToolBar',
        'AMap.Transfer'
      ]
    })

    AMap.value = amap

    // 初始化地图
    map.value = new amap.Map('map', {
      resizeEnable: true,
      center: [113.2644, 23.1291],
      zoom: 11
    })

    // 添加控件
    map.value.addControl(new amap.Scale())
    map.value.addControl(new amap.ToolBar())

  } catch (error) {
    console.error('地图加载失败:', error)
    ElMessage.error('地图加载失败')
  }
})

onUnmounted(() => {
  if (transfer.value) {
    transfer.value.clear()
  }
  if (map.value) {
    map.value.destroy()
  }
})
</script>

<style scoped>
.metro-nav {
  display: flex;
  height: 100%;
}

.input-panel {
  width: 320px;
  padding: 20px;
  background: #fff;
  box-shadow: 2px 0 8px rgba(0,0,0,0.1);
  overflow-y: auto;
  flex-shrink: 0;
}

.input-panel h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #333;
}

.input-group {
  margin-bottom: 16px;
}

.input-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.input-group :deep(.el-input) {
  width: 100%;
}

.swap-btn {
  display: block;
  margin: 0 auto 16px;
}

.search-btn {
  width: 100%;
  margin-top: 10px;
}

.map-container {
  flex: 1;
  position: relative;
  min-width: 0;
}

.map {
  width: 100%;
  height: 100%;
}

/* 路线结果面板样式 */
.route-panel {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 320px;
  max-height: 90%;
  overflow-y: auto;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 6px 0 rgba(114, 124, 245, .5);
}

/* 适配高德地图 Transfer 面板样式 */
:global(.amap-call) {
  background-color: #009cf9;
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
  padding: 10px;
  color: #fff;
  font-weight: bold;
}

:global(.amap-lib-transfer) {
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 4px;
  overflow: hidden;
}
</style>
