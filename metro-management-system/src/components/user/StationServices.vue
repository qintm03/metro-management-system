<template>
  <div class="station-services">
    <!-- 顶部标题栏 -->
    <header class="dashboard-header">
      <div class="header-left">
        <div class="datetime">{{ currentDateTime }}</div>
      </div>
      <div class="header-center">
        <h1 class="main-title">站点周边服务</h1>
        <div class="title-decoration"></div>
      </div>
      <div class="header-right">
        <el-button class="back-btn" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="dashboard-main">
      <!-- 左侧边栏：站点选择 -->
      <aside class="sidebar-left">
        <div class="data-panel">
          <div class="panel-header">
            <div class="panel-icon">
              <el-icon><MapLocation /></el-icon>
            </div>
            <h3>选择站点</h3>
          </div>
          <div class="panel-content">
            <!-- 线路选择 -->
            <div class="line-select">
              <label>选择线路</label>
              <el-select v-model="selectedLineId" placeholder="请选择线路" @change="handleLineChange">
                <el-option
                  v-for="line in metroData.lines"
                  :key="line.lineId"
                  :label="formatLineName(line.lineName)"
                  :value="line.lineId"
                >
                  <span class="line-color-dot" :style="{ background: getLineColor(line.lineName) }"></span>
                  {{ formatLineName(line.lineName) }}
                </el-option>
              </el-select>
            </div>

            <!-- 站点列表 -->
            <div class="station-list" v-if="selectedLine">
              <label>选择站点</label>
              <div class="station-items">
                <div
                  v-for="station in selectedLine.stations"
                  :key="station.id"
                  class="station-item"
                  :class="{ active: selectedStation?.id === station.id }"
                  @click="selectStation(station)"
                >
                  <span class="station-name">{{ station.stationName }}</span>
                  <el-icon v-if="station.isTransfer === 1" class="transfer-icon"><Switch /></el-icon>
                </div>
              </div>
            </div>

            <!-- 搜索范围 -->
            <div class="radius-select">
              <label>搜索范围</label>
              <el-radio-group v-model="searchRadius">
                <el-radio-button :label="500">500m</el-radio-button>
                <el-radio-button :label="1000">1km</el-radio-button>
                <el-radio-button :label="2000">2km</el-radio-button>
              </el-radio-group>
            </div>
          </div>
        </div>
      </aside>

      <!-- 中央地图区域 -->
      <section class="map-section">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-overlay">
          <el-icon class="loading-icon"><Loading /></el-icon>
          <span>正在加载地图...</span>
        </div>
        
        <div id="amap-container" class="amap-container"></div>
      </section>

      <!-- 右侧边栏：POI 列表 -->
      <aside class="sidebar-right">
        <!-- 分类筛选 -->
        <div class="data-panel">
          <div class="panel-header">
            <div class="panel-icon">
              <el-icon><Filter /></el-icon>
            </div>
            <h3>服务分类</h3>
          </div>
          <div class="panel-content">
            <div class="category-tabs">
              <div
                v-for="category in poiCategories"
                :key="category.id"
                class="category-tab"
                :class="{ active: selectedCategory?.id === category.id }"
                @click="selectCategory(category)"
              >
                <el-icon>
                  <Food v-if="category.icon === 'Food'" />
                  <ShoppingBag v-else-if="category.icon === 'ShoppingBag'" />
                  <FirstAidKit v-else-if="category.icon === 'FirstAidKit'" />
                  <Film v-else-if="category.icon === 'Film'" />
                </el-icon>
                <span>{{ category.name }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- POI 列表 -->
        <div class="data-panel" v-if="poiList.length > 0">
          <div class="panel-header">
            <div class="panel-icon">
              <el-icon><List /></el-icon>
            </div>
            <h3>周边服务 ({{ poiList.length }})</h3>
          </div>
          <div class="panel-content">
            <div class="poi-list">
              <div
                v-for="poi in poiList"
                :key="poi.id"
                class="poi-item"
                :class="{ active: selectedPOI?.id === poi.id }"
                @click="selectPOI(poi)"
              >
                <div class="poi-name">{{ poi.name }}</div>
                <div class="poi-info">
                  <span class="poi-distance">{{ formatDistance(poi.distance) }}</span>
                  <span class="poi-type">{{ poi.type }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- POI 详情 -->
        <div class="data-panel" v-if="selectedPOI">
          <div class="panel-header">
            <div class="panel-icon">
              <el-icon><InfoFilled /></el-icon>
            </div>
            <h3>详情信息</h3>
          </div>
          <div class="panel-content">
            <div class="poi-detail">
              <h4 class="poi-detail-name">{{ selectedPOI.name }}</h4>
              <div class="detail-row">
                <span class="detail-label">地址：</span>
                <span class="detail-value">{{ selectedPOI.address }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">距离：</span>
                <span class="detail-value">{{ formatDistance(selectedPOI.distance) }}</span>
              </div>
              <div class="detail-row" v-if="selectedPOI.tel">
                <span class="detail-label">电话：</span>
                <span class="detail-value">{{ selectedPOI.tel }}</span>
              </div>
              <div class="detail-row" v-if="selectedPOI.business">
                <span class="detail-label">营业时间：</span>
                <span class="detail-value">{{ selectedPOI.business }}</span>
              </div>
              <el-button type="primary" class="navigate-btn" @click="navigateToPOI">
                <el-icon><Position /></el-icon>
                去这里
              </el-button>
            </div>
          </div>
        </div>
      </aside>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowLeft, MapLocation, Switch, Filter, List,
  InfoFilled, Position, Food, ShoppingBag, FirstAidKit, Film, Loading
} from '@element-plus/icons-vue'
import { getFullMetroData } from '../../api/metroApi.js'

const router = useRouter()

// ==================== 数据定义 ====================

// 地铁数据
const metroData = ref({
  lines: []
})

// 当前选中的线路和站点
const selectedLineId = ref(null)
const selectedStation = ref(null)

// 高德地图实例
const map = ref(null)
const placeSearch = ref(null)
const walking = ref(null)
const stationMarker = ref(null)
const poiMarkers = ref([])
const circle = ref(null)

// 搜索范围（米）
const searchRadius = ref(1000)

// POI 分类配置（直接写在组件内）
const poiCategories = [
  { id: 'all', name: '全部', icon: 'Food', code: '' },
  { id: 'food', name: '餐饮', icon: 'Food', code: '050000' },
  { id: 'shopping', name: '购物', icon: 'ShoppingBag', code: '060000' },
  { id: 'life', name: '生活', icon: 'FirstAidKit', code: '070000' },
  { id: 'entertainment', name: '娱乐', icon: 'Film', code: '080000' }
]

// 当前选中的分类
const selectedCategory = ref(poiCategories[0])

// POI 列表
const poiList = ref([])

// 当前选中的 POI
const selectedPOI = ref(null)

// 当前时间
const currentDateTime = ref('')

// 加载状态
const loading = ref(false)

// ==================== 计算属性 ====================

// 当前选中的线路
const selectedLine = computed(() => {
  return metroData.value.lines?.find(l => l.lineId === selectedLineId.value) || null
})

// ==================== 方法定义 ====================

// 更新时间
const updateDateTime = () => {
  const now = new Date()
  currentDateTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 获取线路颜色
const getLineColor = (lineName) => {
  const colorMap = {
    '1号线': '#F3D03E', '2号线': '#00629B', '3号线': '#ECA154',
    '4号线': '#00843D', '5号线': '#C5003E', '6号线': '#700F1E',
    '7号线': '#97D700', '8号线': '#008E9C', '9号线': '#71CC98',
    '10号线': '#00A0E9', '11号线': '#470A68', '12号线': '#824CA7',
    '13号线': '#8E8C13', '14号线': '#792330', '18号线': '#0047BB',
    '21号线': '#201747', '22号线': '#D49A25', '广佛线': '#C4D600',
    'APM线': '#00BFFF'
  }
  for (const [key, color] of Object.entries(colorMap)) {
    if (lineName?.includes(key)) return color
  }
  return '#00d4ff'
}

// 格式化线路名称
const formatLineName = (lineName) => {
  if (!lineName) return ''
  const match = lineName.match(/(\d+号线|APM线|广佛线)/)
  return match ? match[1] : lineName
}

// 格式化距离
const formatDistance = (distance) => {
  if (!distance) return ''
  if (distance < 1000) {
    return `${Math.round(distance)}m`
  }
  return `${(distance / 1000).toFixed(1)}km`
}

// 加载地铁数据
const loadMetroData = async () => {
  try {
    const res = await getFullMetroData()
    if (res.code === 200 || res.code === '200') {
      metroData.value = res.data
    } else {
      metroData.value = res
    }
  } catch (error) {
    console.error('加载地铁数据失败:', error)
  }
}

// 处理线路选择变化
const handleLineChange = () => {
  selectedStation.value = null
  poiList.value = []
  selectedPOI.value = null
  clearPOIMarkers()
}

// 选择站点
const selectStation = (station) => {
  selectedStation.value = station
  
  // 地图中心移动到站点
  if (map.value) {
    const position = [parseFloat(station.longitude), parseFloat(station.latitude)]
    map.value.setCenter(position)
    map.value.setZoom(15)
    
    // 添加站点标记
    addStationMarker(position, station.stationName)
    
    // 显示搜索范围圈
    showSearchCircle(position)
    
    // 搜索周边 POI
    searchNearbyPOI(position)
  }
}

// 选择分类
const selectCategory = (category) => {
  selectedCategory.value = category
  if (selectedStation.value) {
    const position = [parseFloat(selectedStation.value.longitude), parseFloat(selectedStation.value.latitude)]
    searchNearbyPOI(position)
  }
}

// 选择 POI
const selectPOI = (poi) => {
  selectedPOI.value = poi
  
  // 地图中心移动到 POI
  if (map.value && poi.location) {
    let position
    // location可能是字符串"lng,lat"或对象{lnt: x, lat: y}
    if (typeof poi.location === 'string') {
      const location = poi.location.split(',')
      position = [parseFloat(location[0]), parseFloat(location[1])]
    } else if (typeof poi.location === 'object') {
      position = [poi.location.lng, poi.location.lat]
    }
    if (position) {
      map.value.setCenter(position)
    }
  }
}

// 导航到 POI
const navigateToPOI = () => {
  if (!selectedStation.value || !selectedPOI.value) return
  
  const start = [
    parseFloat(selectedStation.value.longitude),
    parseFloat(selectedStation.value.latitude)
  ]
  
  // 处理location可能是字符串或对象的情况
  let end
  const poiLocation = selectedPOI.value.location
  if (typeof poiLocation === 'string') {
    end = poiLocation.split(',').map(Number)
  } else if (typeof poiLocation === 'object') {
    end = [poiLocation.lng, poiLocation.lat]
  }
  
  if (walking.value && end) {
    walking.value.search(start, end)
  }
}

// ==================== 地图相关方法 ====================

// 初始化地图
const initMap = () => {
  return new Promise((resolve, reject) => {
    if (window.AMap) {
      createMap()
      resolve()
      return
    }

    // 配置高德安全密钥
    window._AMapSecurityConfig = {
      securityJsCode: 'f2e281bddbaaed3bfc03b711e00122fa',
    }

    const script = document.createElement('script')
    script.src = 'https://webapi.amap.com/maps?v=2.0&key=49f3c3fd7e6380f9c8b84c0f0bdd4d9f&plugin=AMap.PlaceSearch,AMap.Walking'
    script.onload = () => {
      createMap()
      initPlaceSearch()
      initWalking()
      resolve()
    }
    script.onerror = reject
    document.head.appendChild(script)
  })
}

// 创建地图
const createMap = () => {
  map.value = new window.AMap.Map('amap-container', {
    zoom: 11,
    center: [113.2644, 23.1291], // 广州市中心
    viewMode: '2D'
  })
}

// 初始化 POI 搜索
const initPlaceSearch = () => {
  placeSearch.value = new window.AMap.PlaceSearch({
    type: selectedCategory.value.code,
    radius: searchRadius.value,
    pageSize: 20,
    extensions: 'all'
  })
}

// 初始化步行导航
const initWalking = () => {
  walking.value = new window.AMap.Walking({
    map: map.value,
    hideMarkers: false
  })
}

// 添加站点标记
const addStationMarker = (position, name) => {
  // 清除之前的站点标记
  if (stationMarker.value) {
    stationMarker.value.setMap(null)
  }
  
  stationMarker.value = new window.AMap.Marker({
    position: position,
    title: name,
    content: `
      <div style="
        width: 24px;
        height: 24px;
        border-radius: 50%;
        background: #00d4ff;
        border: 3px solid #fff;
        box-shadow: 0 2px 6px rgba(0,0,0,0.4);
      "></div>
    `,
    offset: new window.AMap.Pixel(-12, -12)
  })
  
  stationMarker.value.setMap(map.value)
}

// 显示搜索范围圈
const showSearchCircle = (center) => {
  if (circle.value) {
    circle.value.setMap(null)
  }
  
  circle.value = new window.AMap.Circle({
    center: center,
    radius: searchRadius.value,
    strokeColor: '#00d4ff',
    strokeOpacity: 0.8,
    strokeWeight: 2,
    fillColor: '#00d4ff',
    fillOpacity: 0.1
  })
  
  circle.value.setMap(map.value)
}

// 搜索周边 POI
const searchNearbyPOI = (center) => {
  if (!placeSearch.value) {
    console.log('PlaceSearch 未初始化')
    return
  }
  
  console.log('开始搜索POI:', {
    center: center,
    radius: searchRadius.value,
    type: selectedCategory.value.code,
    category: selectedCategory.value.name
  })
  
  // 重新创建 PlaceSearch 实例（因为高德API不支持动态修改radius）
  const searchOptions = {
    radius: searchRadius.value,
    pageSize: 20,
    extensions: 'all'
  }
  // 如果选择了具体分类，添加type参数
  if (selectedCategory.value.code) {
    searchOptions.type = selectedCategory.value.code
  }
  placeSearch.value = new window.AMap.PlaceSearch(searchOptions)
  
  placeSearch.value.searchNearBy(
    '',
    center,
    searchRadius.value,
    (status, result) => {
      console.log('POI搜索结果:', status, result)
      
      if (status === 'complete' && result.info === 'OK') {
        poiList.value = result.poiList.pois
        console.log('找到POI数量:', result.poiList.pois.length)
        renderPOIMarkers(result.poiList.pois)
      } else {
        poiList.value = []
        clearPOIMarkers()
        console.log('未找到周边服务，状态:', status, '结果:', result)
      }
    }
  )
}

// 渲染 POI 标记
const renderPOIMarkers = (pois) => {
  console.log('渲染POI标记:', pois.length, '个')
  
  // 清除之前的 POI 标记
  clearPOIMarkers()
  
  if (!map.value) {
    console.log('地图未初始化，无法渲染标记')
    return
  }
  
  pois.forEach((poi, index) => {
    if (!poi.location) {
      console.log('POI没有location:', poi.name)
      return
    }
    
    // 处理location可能是字符串或对象的情况
    let position
    if (typeof poi.location === 'string') {
      const location = poi.location.split(',')
      position = [parseFloat(location[0]), parseFloat(location[1])]
    } else if (typeof poi.location === 'object') {
      position = [poi.location.lng, poi.location.lat]
    }
    
    if (!position) {
      console.log('POI location格式错误:', poi.name, poi.location)
      return
    }
    
    console.log(`渲染标记 ${index}:`, poi.name, position)
    
    const marker = new window.AMap.Marker({
      position: position,
      title: poi.name,
      content: `
        <div style="
          width: 16px;
          height: 16px;
          border-radius: 50%;
          background: #ff6b6b;
          border: 2px solid #fff;
          box-shadow: 0 2px 4px rgba(0,0,0,0.3);
          cursor: pointer;
        "></div>
      `,
      offset: new window.AMap.Pixel(-8, -8),
      extData: poi
    })
    
    marker.on('click', () => {
      selectedPOI.value = poi
    })
    
    marker.setMap(map.value)
    poiMarkers.value.push(marker)
  })
  
  console.log('POI标记渲染完成，共', poiMarkers.value.length, '个')
}

// 清除 POI 标记
const clearPOIMarkers = () => {
  poiMarkers.value.forEach(marker => marker.setMap(null))
  poiMarkers.value = []
}

// ==================== 生命周期钩子 ====================

let timer = null

onMounted(async () => {
  updateDateTime()
  timer = setInterval(updateDateTime, 60000)
  
  loading.value = true
  try {
    // 加载地铁数据
    await loadMetroData()
    
    // 初始化地图
    await initMap()
  } catch (error) {
    console.error('初始化失败:', error)
  } finally {
    loading.value = false
  }
})

// 监听搜索范围变化
watch(searchRadius, (newRadius) => {
  if (selectedStation.value && map.value) {
    const position = [parseFloat(selectedStation.value.longitude), parseFloat(selectedStation.value.latitude)]
    
    // 更新搜索范围圈
    showSearchCircle(position)
    
    // 重新搜索POI（searchNearbyPOI会重新创建PlaceSearch实例）
    searchNearbyPOI(position)
  }
})

onUnmounted(() => {
  clearInterval(timer)
  
  // 清理地图资源
  if (stationMarker.value) {
    stationMarker.value.setMap(null)
  }
  clearPOIMarkers()
  if (circle.value) {
    circle.value.setMap(null)
  }
  if (map.value) {
    map.value.destroy()
  }
})
</script>

<style scoped>
/* 引入外部样式 */
@import '../../assets/css/userHomePage.css';

/* 页面容器 */
.station-services {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #0a1929 0%, #0d1f33 100%);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 主内容区 */
.dashboard-main {
  flex: 1;
  display: grid;
  grid-template-columns: 260px 1fr 300px;
  gap: 15px;
  padding: 15px;
  overflow: hidden;
  min-height: 0;
}

/* 左侧边栏 */
.sidebar-left {
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow-y: auto;
  min-height: 0;
  max-height: 100%;
}

/* 中央地图区域 */
.map-section {
  position: relative;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
}

.amap-container {
  width: 100%;
  height: 100%;
}

/* 加载状态 */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(10, 25, 41, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 100;
  color: #00d4ff;
  font-size: 14px;
}

.loading-icon {
  font-size: 32px;
  margin-bottom: 10px;
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 右侧边栏 */
.sidebar-right {
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow-y: auto;
  min-height: 0;
  max-height: 100%;
}

/* 数据面板 */
.data-panel {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
}

.data-panel:last-child {
  flex-shrink: 1;
  overflow-y: auto;
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 15px;
  background: rgba(0, 212, 255, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.panel-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 212, 255, 0.2);
  border-radius: 8px;
  color: #00d4ff;
}

.panel-header h3 {
  margin: 0;
  font-size: 14px;
  color: #fff;
  font-weight: 500;
}

.panel-content {
  padding: 12px 15px;
}

/* 线路选择 */
.line-select,
.radius-select {
  margin-bottom: 15px;
}

.line-select label,
.radius-select label,
.station-list label {
  display: block;
  margin-bottom: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

.line-color-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 8px;
}

/* 站点列表 */
.station-list {
  flex: 1;
  margin-bottom: 15px;
}

.station-items {
  max-height: 200px;
  overflow-y: auto;
}

.station-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  margin-bottom: 6px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.station-item:hover {
  background: rgba(0, 212, 255, 0.1);
}

.station-item.active {
  background: rgba(0, 212, 255, 0.2);
  border: 1px solid rgba(0, 212, 255, 0.4);
}

.station-name {
  font-size: 13px;
  color: #fff;
}

.transfer-icon {
  color: #ff6b6b;
  font-size: 14px;
}

/* 分类标签 */
.category-tabs {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.category-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 10px 8px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.category-tab:hover {
  background: rgba(0, 212, 255, 0.1);
}

.category-tab.active {
  background: rgba(0, 212, 255, 0.2);
  border: 1px solid rgba(0, 212, 255, 0.4);
}

.category-tab .el-icon {
  font-size: 20px;
  color: #00d4ff;
}

.category-tab span {
  font-size: 12px;
  color: #fff;
}

/* POI 列表 */
.poi-list {
  max-height: 250px;
  overflow-y: auto;
}

.poi-item {
  padding: 12px;
  margin-bottom: 8px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.poi-item:hover {
  background: rgba(0, 212, 255, 0.1);
}

.poi-item.active {
  background: rgba(0, 212, 255, 0.2);
  border: 1px solid rgba(0, 212, 255, 0.4);
}

.poi-name {
  font-size: 14px;
  color: #fff;
  margin-bottom: 6px;
}

.poi-info {
  display: flex;
  gap: 10px;
  font-size: 12px;
}

.poi-distance {
  color: #00d4ff;
}

.poi-type {
  color: rgba(255, 255, 255, 0.5);
}

/* POI 详情 */
.poi-detail {
  padding: 5px;
}

.poi-detail-name {
  font-size: 16px;
  color: #00d4ff;
  margin: 0 0 15px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
}

.detail-row {
  display: flex;
  margin-bottom: 12px;
  font-size: 13px;
  line-height: 1.5;
}

.detail-label {
  color: rgba(255, 255, 255, 0.5);
  min-width: 70px;
  flex-shrink: 0;
}

.detail-value {
  color: #fff;
  flex: 1;
}

.navigate-btn {
  width: 100%;
  margin-top: 15px;
}

/* 滚动条样式 */
.station-items::-webkit-scrollbar,
.poi-list::-webkit-scrollbar {
  width: 4px;
}

.station-items::-webkit-scrollbar-thumb,
.poi-list::-webkit-scrollbar-thumb {
  background: rgba(0, 212, 255, 0.3);
  border-radius: 2px;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .dashboard-main {
    grid-template-columns: 260px 1fr 280px;
  }
}

@media (max-width: 992px) {
  .dashboard-main {
    grid-template-columns: 1fr;
    grid-template-rows: auto 1fr auto;
  }
  
  .sidebar-left,
  .sidebar-right {
    max-height: 300px;
  }
}
</style>
