<template>
  <div class="metro-dashboard">
    <!-- 顶部标题栏 -->
    <header class="dashboard-header">
      <div class="header-left">
        <div class="weather-info">
          <el-icon class="weather-icon">
            <Sunny />
          </el-icon>
          <span class="temperature">24°</span>
          <span class="weather-text">晴</span>
        </div>
        <div class="datetime">{{ currentDateTime }}</div>
      </div>
      <div class="header-center">
        <h1 class="main-title">广州地铁智慧管理平台</h1>
        <div class="title-decoration"></div>
      </div>
      <div class="header-right">
        <el-button type="primary" class="nav-btn" @click="goToDigitalTwin">
          <el-icon>
            <Monitor />
          </el-icon>
          数字孪生
        </el-button>
        <el-button class="back-btn" @click="goBack">
          <el-icon>
            <ArrowLeft />
          </el-icon>
          返回
        </el-button>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="dashboard-main">
      <!-- 左侧边栏 -->
      <aside class="sidebar-left">
        <!-- 运营概况面板 -->
        <div class="data-panel">
          <div class="panel-header">
            <div class="panel-icon">
              <el-icon>
                <DataLine />
              </el-icon>
            </div>
            <h3>运营概况</h3>
          </div>
          <div class="panel-content">
            <div class="stat-row">
              <div class="stat-item">
                <div class="stat-value">{{ metroData.totalLines || 0 }}</div>
                <div class="stat-label">运营线路</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ metroData.totalStations || 0 }}</div>
                <div class="stat-label">车站数量</div>
              </div>
            </div>
            <div class="stat-row">
              <div class="stat-item">
                <div class="stat-value">{{ transferCount }}</div>
                <div class="stat-label">换乘站数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">621</div>
                <div class="stat-label">车辆编组</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 线路列表 -->
        <div class="data-panel">
          <div class="panel-header">
            <div class="panel-icon">
              <el-icon>
                <MapLocation />
              </el-icon>
            </div>
            <h3>线路列表</h3>
          </div>
          <div class="panel-content">
            <div class="line-list">
              <div
                v-for="line in metroData.lines"
                :key="line.lineId"
                class="line-item"
                :class="{ active: selectedLineId === line.lineId }"
                @click="selectLine(line)"
              >
                <span class="line-color-dot" :style="{ background: getLineColor(line.lineName) }"></span>
                <span class="line-name">{{ formatLineName(line.lineName) }}</span>
                <span class="line-station-count">{{ line.stations ? line.stations.length : 0 }}站</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 站点搜索 -->
        <div class="data-panel">
          <div class="panel-header">
            <div class="panel-icon">
              <el-icon>
                <Search />
              </el-icon>
            </div>
            <h3>站点搜索</h3>
          </div>
          <div class="panel-content">
            <el-input
              v-model="searchKeyword"
              placeholder="输入站点名称"
              clearable
              @input="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <div class="search-results" v-if="searchResults.length > 0">
              <div
                v-for="station in searchResults"
                :key="station.id"
                class="search-result-item"
                @click="focusOnStation(station)"
              >
                <span class="station-name">{{ station.stationName }}</span>
                <span class="line-tag" :style="{ background: getLineColorById(station.lineId) }">
                  {{ getLineNameById(station.lineId) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </aside>

      <!-- 中央地图区域 -->
      <section class="map-section">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-overlay">
          <el-icon class="loading-icon">
            <Loading />
          </el-icon>
          <span>正在加载地铁数据...</span>
        </div>

        <!-- 高德地图容器 -->
        <div id="amap-container" class="amap-container"></div>

        <!-- 图例 -->
        <div class="map-legend">
          <div class="legend-title">图例</div>
          <div class="legend-item">
            <span class="legend-dot transfer"></span>
            <span>换乘站</span>
          </div>
          <div class="legend-item">
            <span class="legend-dot normal"></span>
            <span>普通站</span>
          </div>
        </div>
      </section>

      <!-- 右侧边栏 -->
      <aside class="sidebar-right">
        <!-- 站点详情 -->
        <div class="data-panel" v-if="selectedStation">
          <div class="panel-header">
            <div class="panel-icon">
              <el-icon>
                <Location />
              </el-icon>
            </div>
            <h3>站点详情</h3>
          </div>
          <div class="panel-content">
            <div class="station-detail">
              <h4 class="station-name-large">{{ selectedStation.stationName }}</h4>
              <div class="detail-row">
                <span class="detail-label">线路：</span>
                <span class="line-badge-large" :style="{ background: getLineColorById(selectedStation.lineId) }">
                  {{ getLineNameById(selectedStation.lineId) }}
                </span>
              </div>
              <div class="detail-row" v-if="selectedStation.isTransfer === 1">
                <span class="detail-label">类型：</span>
                <span class="transfer-badge">换乘站</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">序号：</span>
                <span>第 {{ selectedStation.sequence }} 站</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 换乘信息 -->
        <div class="data-panel" v-if="selectedStation && selectedStation.isTransfer === 1">
          <div class="panel-header">
            <div class="panel-icon">
              <el-icon>
                <Switch />
              </el-icon>
            </div>
            <h3>可换乘线路</h3>
          </div>
          <div class="panel-content">
            <div class="transfer-lines">
              <div
                v-for="transfer in getTransferLines(selectedStation)"
                :key="transfer.toLineId"
                class="transfer-line-item"
              >
                <span class="line-color-bar" :style="{ background: getLineColor(transfer.toLineName) }"></span>
                <span class="transfer-line-name">{{ transfer.toLineName }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 线路站点列表 -->
        <div class="data-panel" v-if="selectedLine">
          <div class="panel-header">
            <div class="panel-icon">
              <el-icon>
                <List />
              </el-icon>
            </div>
            <h3>站点列表</h3>
          </div>
          <div class="panel-content">
            <div class="station-list">
              <div
                v-for="(station, index) in selectedLine.stations"
                :key="station.id"
                class="station-list-item"
                :class="{ active: selectedStation?.id === station.id }"
                @click="focusOnStation(station)"
              >
                <span class="station-index">{{ index + 1 }}</span>
                <span class="station-name">{{ station.stationName }}</span>
                <el-icon v-if="station.isTransfer === 1" class="transfer-icon-small"><Switch /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </aside>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  Sunny, Monitor, ArrowLeft, DataLine, MapLocation, Search,
  Loading, Location, Switch, List
} from '@element-plus/icons-vue'

// 引入 API
import { getFullMetroData, getTransferStations } from '../../api/metroApi.js'

const router = useRouter()

// 加载状态
const loading = ref(false)

// 地铁数据
const metroData = ref({
  city: '',
  totalLines: 0,
  totalStations: 0,
  lines: []
})

// 换乘站数量
const transferCount = ref(0)

// 换乘关系数据
const transferData = ref([])

// 高德地图实例
const map = ref(null)
const polylines = ref([])
const markers = ref([])

// 当前时间
const currentDateTime = ref('')
const updateDateTime = () => {
  const now = new Date()
  currentDateTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 搜索相关
const searchKeyword = ref('')
const searchResults = ref([])

// 选中的线路和站点
const selectedLineId = ref(null)
const selectedLine = computed(() => {
  return metroData.value.lines?.find(l => l.lineId === selectedLineId.value) || null
})
const selectedStation = ref(null)

// 线路颜色映射
const lineColorMap = {
  '1 号线': '#F3D03E',
  '2 号线': '#00629B',
  '3 号线': '#ECA154',
  '4 号线': '#00843D',
  '5 号线': '#C5003E',
  '6 号线': '#700F1E',
  '7 号线': '#97D700',
  '8 号线': '#008E9C',
  '9 号线': '#71CC98',
  '10 号线': '#00A0E9',
  '11 号线': '#470A68',
  '12 号线': '#824CA7',
  '13 号线': '#8E8C13',
  '14 号线': '#792330',
  '18 号线': '#0047BB',
  '21 号线': '#201747',
  '22 号线': '#D49A25',
  '广佛线': '#C4D600',
  'APM 线': '#00BFFF'
}

// 获取线路颜色
const getLineColor = (lineName) => {
  if (!lineName) return '#00d4ff'
  for (const [key, color] of Object.entries(lineColorMap)) {
    if (lineName.includes(key)) {
      return color
    }
  }
  return '#00d4ff'
}

// 根据线路 ID 获取颜色
const getLineColorById = (lineId) => {
  const line = metroData.value.lines?.find(l => l.lineId === lineId)
  return line ? getLineColor(line.lineName) : '#00d4ff'
}

// 根据线路 ID 获取名称
const getLineNameById = (lineId) => {
  const line = metroData.value.lines?.find(l => l.lineId === lineId)
  return line ? formatLineName(line.lineName) : '未知'
}

// 格式化线路名称
const formatLineName = (lineName) => {
  if (!lineName) return ''
  const match = lineName.match(/(\d+号线 |APM 线 | 广佛线)/)
  return match ? match[1] : lineName
}

// 加载高德地图
const initMap = () => {
  return new Promise((resolve, reject) => {
    // 检查 AMap 是否已加载
    if (window.AMap) {
      createMap()
      resolve()
      return
    }

    // 动态加载高德地图 JSAPI
    const script = document.createElement('script')
    script.src = 'https://webapi.amap.com/maps?v=2.0&key=49f3c3fd7e6380f9c8b84c0f0bdd4d9f'
    script.onload = () => {
      createMap()
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

// 绘制地铁线路
const drawMetroLines = () => {
  if (!map.value || !metroData.value.lines) return

  // 清除之前的线路和标记
  clearMapOverlays()

  const linesToDraw = selectedLineId.value
    ? metroData.value.lines.filter(l => l.lineId === selectedLineId.value)
    : metroData.value.lines

  linesToDraw.forEach(line => {
    // 优先使用 line.path 数据
    let path = null
    if (line.path) {
      try {
        path = typeof line.path === 'string' ? JSON.parse(line.path) : line.path
      } catch (e) {
        console.error('解析线路路径失败:', e)
      }
    }

    // 如果没有 path 数据，则使用站点坐标构建路径
    if (!path || path.length === 0) {
      if (!line.stations || line.stations.length < 2) return
      const sortedStations = [...line.stations].sort((a, b) => a.sequence - b.sequence)
      path = sortedStations.map(station => [
        parseFloat(station.longitude),
        parseFloat(station.latitude)
      ])
    }

    // 绘制线路
    const polyline = new window.AMap.Polyline({
      path: path,
      strokeColor: getLineColor(line.lineName),
      strokeWeight: selectedLineId.value ? 6 : 4,
      strokeOpacity: selectedLineId.value && selectedLineId.value !== line.lineId ? 0.3 : 0.9,
      strokeStyle: 'solid',
      lineJoin: 'round',
      lineCap: 'round',
      extData: { line }
    })

    polyline.on('click', () => selectLine(line))
    polyline.setMap(map.value)
    polylines.value.push(polyline)

    // 绘制站点标记
    if (line.stations) {
      line.stations.forEach(station => {
        const isTransfer = station.isTransfer === 1
        const marker = new window.AMap.Marker({
          position: [parseFloat(station.longitude), parseFloat(station.latitude)],
          title: station.stationName,
          extData: { station },
          content: createMarkerContent(station, isTransfer, line),
          offset: new window.AMap.Pixel(-10, -10)
        })

        marker.on('click', () => {
          selectedStation.value = station
        })

        marker.setMap(map.value)
        markers.value.push(marker)
      })
    }
  })
}

// 创建标记点内容
const createMarkerContent = (station, isTransfer, line) => {
  const color = getLineColor(line.lineName)
  const size = isTransfer ? 20 : 14
  const borderWidth = isTransfer ? 3 : 2

  return `
    <div style="
      width: ${size}px;
      height: ${size}px;
      border-radius: 50%;
      background: ${isTransfer ? color : '#fff'};
      border: ${borderWidth}px solid ${color};
      box-shadow: 0 2px 4px rgba(0,0,0,0.3);
      cursor: pointer;
      transition: transform 0.2s;
    " onmouseover="this.style.transform='scale(1.2)'" onmouseout="this.style.transform='scale(1)'">
    </div>
  `
}

// 清除地图覆盖物
const clearMapOverlays = () => {
  polylines.value.forEach(p => p.setMap(null))
  markers.value.forEach(m => m.setMap(null))
  polylines.value = []
  markers.value = []
}

// 加载地铁数据
const loadMetroData = async () => {
  loading.value = true
  try {
    const res = await getFullMetroData()
    console.log('后端返回的原始数据:', res)

    let data = res
    if ((res.code === 200 || res.code === '200') && res.data) {
      data = res.data
    }

    metroData.value = data
    console.log('处理后的地铁数据:', metroData.value)

    // 计算换乘站数量
    if (metroData.value.lines) {
      const uniqueStations = new Set()
      metroData.value.lines.forEach(line => {
        if (line.stations) {
          line.stations.forEach(station => {
            if (station.isTransfer === 1) {
              uniqueStations.add(station.stationName)
            }
          })
        }
      })
      transferCount.value = uniqueStations.size
    }

    // 加载换乘关系数据
    await loadTransferData()

    // 在地图上绘制地铁线路
    drawMetroLines()

  } catch (error) {
    console.error('加载地铁数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载换乘数据
const loadTransferData = async () => {
  try {
    const res = await getTransferStations()
    if ((res.code === 200 || res.code === '200') && res.data) {
      transferData.value = res.data
    }
  } catch (error) {
    console.error('加载换乘数据失败:', error)
  }
}

// 获取换乘线路
const getTransferLines = (station) => {
  if (!station || station.isTransfer !== 1) return []
  return transferData.value.filter(t => t.stationName === station.stationName && t.fromLineId === station.lineId)
}

// 选择线路
const selectLine = (line) => {
  if (selectedLineId.value === line.lineId) {
    selectedLineId.value = null
  } else {
    selectedLineId.value = line.lineId
  }
  // 重新绘制线路
  drawMetroLines()
}

// 搜索站点
const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    searchResults.value = []
    return
  }

  const results = []
  const keyword = searchKeyword.value.toLowerCase()

  metroData.value.lines?.forEach(line => {
    line.stations?.forEach(station => {
      if (station.stationName.toLowerCase().includes(keyword)) {
        results.push(station)
      }
    })
  })

  searchResults.value = results.slice(0, 10)
}

// 聚焦到站点
const focusOnStation = (station) => {
  selectedStation.value = station
  searchResults.value = []
  searchKeyword.value = ''

  // 地图中心移动到站点位置
  if (map.value) {
    map.value.setCenter([parseFloat(station.longitude), parseFloat(station.latitude)])
    map.value.setZoom(15)
  }
}

// 导航方法
const goToDigitalTwin = () => {
  router.push('/user/digital-twin')
}

const goBack = () => {
  router.back()
}

let timer = null

onMounted(async () => {
  updateDateTime()
  timer = setInterval(updateDateTime, 1000)

  // 初始化地图
  await initMap()

  // 加载地铁数据
  await loadMetroData()
})

onUnmounted(() => {
  clearInterval(timer)
  // 销毁地图
  if (map.value) {
    map.value.destroy()
  }
})
</script>

<style scoped>
/* 引入外部样式 */
@import '../../assets/css/userHomePage.css';

/* 高德地图容器 */
.amap-container {
  width: 100%;
  height: 100%;
  border-radius: 12px;
}

/* 图例 */
.map-legend {
  position: absolute;
  bottom: 20px;
  left: 20px;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  padding: 12px;
  z-index: 10;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.legend-title {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.6);
  margin-bottom: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.8);
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.legend-dot.transfer {
  background: #ff6b6b;
  border: 2px solid #fff;
}

.legend-dot.normal {
  background: #fff;
  border: 2px solid #00d4ff;
}

/* 线路列表样式 */
.line-list {
  max-height: 300px;
  overflow-y: auto;
}

.line-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
  margin-bottom: 4px;
}

.line-item:hover {
  background: rgba(0, 212, 255, 0.1);
}

.line-item.active {
  background: rgba(0, 212, 255, 0.2);
  border: 1px solid rgba(0, 212, 255, 0.4);
}

.line-color-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  flex-shrink: 0;
}

.line-name {
  flex: 1;
  font-size: 13px;
  color: #fff;
}

.line-station-count {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
}

/* 搜索样式 */
.search-results {
  margin-top: 10px;
  max-height: 200px;
  overflow-y: auto;
}

.search-result-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
  margin-bottom: 4px;
}

.search-result-item:hover {
  background: rgba(0, 212, 255, 0.1);
}

.station-name {
  font-size: 13px;
  color: #fff;
}

.line-tag {
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 10px;
  color: #fff;
}

/* 站点详情样式 */
.station-detail {
  padding: 10px;
}

.station-name-large {
  font-size: 18px;
  color: #00d4ff;
  margin: 0 0 15px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  font-size: 13px;
}

.detail-label {
  color: rgba(255, 255, 255, 0.6);
  min-width: 60px;
}

.line-badge-large {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
  font-weight: bold;
}

.transfer-badge {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
  border: 1px solid rgba(255, 107, 107, 0.4);
}

/* 换乘线路样式 */
.transfer-lines {
  padding: 10px;
}

.transfer-line-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  margin-bottom: 6px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 6px;
}

.line-color-bar {
  width: 4px;
  height: 20px;
  border-radius: 2px;
}

.transfer-line-name {
  font-size: 13px;
  color: #fff;
}

/* 站点列表样式 */
.station-list {
  max-height: 400px;
  overflow-y: auto;
}

.station-list-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
  margin-bottom: 4px;
}

.station-list-item:hover {
  background: rgba(0, 212, 255, 0.1);
}

.station-list-item.active {
  background: rgba(0, 212, 255, 0.2);
  border: 1px solid rgba(0, 212, 255, 0.4);
}

.station-index {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 212, 255, 0.2);
  border-radius: 50%;
  font-size: 11px;
  color: #00d4ff;
}

.transfer-icon-small {
  color: #ff6b6b;
  font-size: 14px;
}

/* 滚动条样式 */
.line-list::-webkit-scrollbar,
.station-list::-webkit-scrollbar,
.search-results::-webkit-scrollbar {
  width: 4px;
}

.line-list::-webkit-scrollbar-thumb,
.station-list::-webkit-scrollbar-thumb,
.search-results::-webkit-scrollbar-thumb {
  background: rgba(0, 212, 255, 0.3);
  border-radius: 2px;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .dashboard-main {
    grid-template-columns: 240px 1fr 240px;
  }
}

@media (max-width: 992px) {
  .dashboard-main {
    grid-template-columns: 1fr;
    grid-template-rows: auto 1fr auto;
  }

  .sidebar-left,
  .sidebar-right {
    display: flex;
    gap: 15px;
    overflow-x: auto;
  }

  .data-panel {
    min-width: 280px;
  }
}
</style>
