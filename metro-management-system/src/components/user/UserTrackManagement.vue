<template>
  <div class="track-management">
    <el-page-header title="地铁站点经纬度查询" @back="goBack" />
    
    <div class="content-wrapper">
      <!-- 地图容器 -->
      <div id="track-map" class="map-container"></div>
      
      <!-- 查询面板 -->
      <div class="query-panel">
        <h3>站点查询</h3>
        <div class="input-section">
          <el-input
            v-model="stationName"
            placeholder="请输入广州地铁站名，如：体育西路"
            clearable
            @keyup.enter="queryStation"
          >
            <template #append>
              <el-button type="primary" @click="queryStation" :loading="loading">
                <el-icon><Search /></el-icon>
                查询
              </el-button>
            </template>
          </el-input>
        </div>
        
        <!-- 查询结果 -->
        <div v-if="queryResult" class="result-section">
          <h4>查询结果</h4>
          <div class="result-card">
            <div class="result-item">
              <span class="label">站点名称：</span>
              <span class="value">{{ queryResult.name }}</span>
            </div>
            <div class="result-item">
              <span class="label">经度：</span>
              <span class="value">{{ queryResult.lng }}</span>
            </div>
            <div class="result-item">
              <span class="label">纬度：</span>
              <span class="value">{{ queryResult.lat }}</span>
            </div>
            <div class="result-item">
              <span class="label">完整坐标：</span>
              <span class="value coords">{{ queryResult.lng }}, {{ queryResult.lat }}</span>
            </div>
          </div>
        </div>
        
        <!-- 常用站点 -->
        <div class="common-stations">
          <h4>常用站点</h4>
          <div class="station-tags">
            <el-tag 
              v-for="station in commonStations" 
              :key="station"
              class="station-tag"
              @click="selectStation(station)"
            >
              {{ station }}
            </el-tag>
          </div>
        </div>
        
        <!-- 查询历史 -->
        <div v-if="queryHistory.length > 0" class="history-section">
          <h4>查询历史</h4>
          <div class="history-list">
            <div 
              v-for="(item, index) in queryHistory" 
              :key="index"
              class="history-item"
              @click="selectStation(item.name)"
            >
              <span class="history-name">{{ item.name }}</span>
              <span class="history-coords">{{ item.lng.toFixed(4) }}, {{ item.lat.toFixed(4) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import AMapLoader from "@amap/amap-jsapi-loader"
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()

// 高德地图 Key 配置 - 与项目其他组件保持一致
const AMAP_KEY = '49f3c3fd7e6380f9c8b84c0f0bdd4d9f'
const AMAP_SECURITY_CONFIG = 'f2e281bddbaaed3bfc03b711e00122fa'

// 响应式数据
const stationName = ref('')
const loading = ref(false)
const queryResult = ref(null)
const queryHistory = ref([])

// 常用站点
const commonStations = [
  '体育西路', '珠江新城', '广州南站', '广州东站', '公园前',
  '嘉禾望岗', '客村', '昌岗', '万胜围', '汉溪长隆',
  '番禺广场', '机场南', '广州塔', '陈家祠', '西塱'
]

// 地图相关变量
let map = null
let marker = null
let geocoder = null

// 返回上一页
const goBack = () => {
  router.back()
}

// 初始化地图
const initMap = async () => {
  try {
    // 配置安全密钥
    window._AMapSecurityConfig = {
      securityJsCode: AMAP_SECURITY_CONFIG
    }
    
    // 加载高德地图
    const AMap = await AMapLoader.load({
      key: AMAP_KEY,
      version: '2.0',
      plugins: ['AMap.Geocoder', 'AMap.Scale', 'AMap.ToolBar']
    })
    
    // 初始化地图 - 以广州为中心
    map = new AMap.Map('track-map', {
      viewMode: '2D',
      zoom: 11,
      center: [113.2644, 23.1291],
      mapStyle: 'amap://styles/normal'
    })
    
    // 添加控件
    map.addControl(new AMap.Scale())
    map.addControl(new AMap.ToolBar({
      position: 'RB'
    }))
    
    // 初始化地理编码服务
    geocoder = new AMap.Geocoder({
      city: '广州', // 设置城市为广州
    })
    
    // 初始化标记
    marker = new AMap.Marker()
    
  } catch (error) {
    console.error('地图初始化失败:', error)
    ElMessage.error('地图加载失败: ' + (error.message || '未知错误'))
  }
}

// 查询站点
const queryStation = async () => {
  if (!stationName.value.trim()) {
    ElMessage.warning('请输入站点名称')
    return
  }
  
  if (!geocoder) {
    ElMessage.error('地图服务未初始化')
    return
  }
  
  loading.value = true
  
  try {
    // 构建完整地址（添加广州市前缀以提高准确性）
    const address = `广州市${stationName.value}地铁站`
    
    geocoder.getLocation(address, (status, result) => {
      loading.value = false
      
      if (status === 'complete' && result.geocodes && result.geocodes.length > 0) {
        const location = result.geocodes[0].location
        const lng = location.lng
        const lat = location.lat
        
        // 保存查询结果
        queryResult.value = {
          name: stationName.value,
          lng: lng,
          lat: lat
        }
        
        // 添加到历史记录
        addToHistory(queryResult.value)
        
        // 在地图上显示标记
        showMarker(lng, lat)
        
        ElMessage.success('查询成功')
      } else {
        ElMessage.error('未找到该站点，请检查站点名称是否正确')
        queryResult.value = null
      }
    })
  } catch (error) {
    loading.value = false
    console.error('查询失败:', error)
    ElMessage.error('查询失败: ' + (error.message || '未知错误'))
  }
}

// 在地图上显示标记
const showMarker = (lng, lat) => {
  if (!map || !marker) return
  
  const position = [lng, lat]
  marker.setPosition(position)
  map.add(marker)
  map.setFitView([marker], false, [100, 100, 100, 100], 15)
}

// 选择常用站点
const selectStation = (name) => {
  stationName.value = name
  queryStation()
}

// 添加到历史记录
const addToHistory = (result) => {
  // 避免重复添加
  const exists = queryHistory.value.findIndex(item => item.name === result.name)
  if (exists !== -1) {
    queryHistory.value.splice(exists, 1)
  }
  
  // 添加到开头
  queryHistory.value.unshift(result)
  
  // 最多保留10条
  if (queryHistory.value.length > 10) {
    queryHistory.value = queryHistory.value.slice(0, 10)
  }
}

// 生命周期钩子
onMounted(() => {
  initMap()
})

onUnmounted(() => {
  if (map) {
    map.destroy()
    map = null
  }
})
</script>

<style scoped>
.track-management {
  padding: 20px;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.content-wrapper {
  flex: 1;
  display: flex;
  margin-top: 20px;
  gap: 20px;
  min-height: 0;
}

.map-container {
  flex: 1;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.query-panel {
  width: 350px;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.query-panel h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 12px;
}

.query-panel h4 {
  margin: 20px 0 12px 0;
  font-size: 14px;
  color: #666;
}

.input-section {
  margin-bottom: 20px;
}

.result-section {
  margin-bottom: 20px;
}

.result-card {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  border-left: 4px solid #409eff;
}

.result-item {
  display: flex;
  margin-bottom: 10px;
  font-size: 14px;
}

.result-item:last-child {
  margin-bottom: 0;
}

.result-item .label {
  color: #666;
  width: 80px;
  flex-shrink: 0;
}

.result-item .value {
  color: #333;
  font-weight: 500;
  flex: 1;
}

.result-item .coords {
  color: #409eff;
  font-family: monospace;
  font-size: 13px;
}

.common-stations {
  margin-bottom: 20px;
}

.station-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.station-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.station-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.history-section {
  border-top: 1px solid #eee;
  padding-top: 10px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  background: #f5f7fa;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.history-item:hover {
  background: #e4e7ed;
}

.history-name {
  font-size: 14px;
  color: #333;
}

.history-coords {
  font-size: 12px;
  color: #909399;
  font-family: monospace;
}
</style>
