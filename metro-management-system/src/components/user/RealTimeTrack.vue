<template>
  <div class="metro-simulation">
   
    
    <!-- 控制栏 -->
    <div class="control-bar">
      <div class="line-selector">
        <span class="label">选择线路:</span>
        <el-select v-model="selectedLineId" placeholder="请选择线路" @change="onLineChange" style="width: 220px;">
          <el-option
            v-for="line in availableLines"
            :key="line.lineId"
            :label="line.lineName"
            :value="line.lineId"
          />
        </el-select>
      </div>
      
      <div class="time-display">
        <span class="label">当前时间:</span>
        <span class="time">{{ currentTimeStr }}</span>
      </div>
      
      <div class="line-status" v-if="currentLine.lineId">
        <el-tag :type="isOperating ? 'success' : 'danger'" size="large">
          {{ isOperating ? '运营中' : '已停运' }}
        </el-tag>
      </div>
    </div>
    
    <div class="content-wrapper">
      <!-- 地图容器 -->
      <div id="metro-map" class="map-container"></div>
      
      <!-- 侧边面板 -->
      <div class="side-panel" v-if="currentLine.lineId">
        <!-- 线路信息 -->
        <div class="section">
          <h3>{{ currentLine.lineName }}</h3>
          <div class="line-info" :style="{ borderLeftColor: currentLine.color || '#409eff' }">
            <p><strong>首班车:</strong> {{ currentLine.firstTrainTime }}</p>
            <p><strong>末班车:</strong> {{ currentLine.lastTrainTime }}</p>
            <p><strong>站点数:</strong> {{ currentLine.stations?.length || 0 }} 站</p>
            <p><strong>运行列车:</strong> {{ activeTrainCount }} 辆</p>
          </div>
        </div>
        
        <!-- 列车列表 -->
        <div class="section">
          <h3>列车实时状态</h3>
          <div class="train-list">
            <div 
              v-for="train in trains" 
              :key="train.trainId"
              class="train-item"
              :class="train.status"
            >
              <div class="train-header">
                <span class="train-name">{{ train.trainId }}</span>
                <el-tag :type="getStatusType(train.status)" size="small">
                  {{ getStatusText(train.status) }}
                </el-tag>
              </div>
              <div class="train-detail" v-if="train.status === 'running'">
                <p>正在运行: {{ getCurrentStationName(train) }} → {{ getNextStationName(train) }}</p>
                <p>预计到达: {{ getArrivalTime(train, train.nextStationIndex) }}</p>
              </div>
              <div class="train-detail" v-else-if="train.status === 'stopped'">
                <p>停靠站点: {{ getCurrentStationName(train) }}</p>
                <p>预计发车: {{ getDepartureTime(train) }}</p>
              </div>
              <div class="train-detail" v-else-if="train.status === 'waiting'">
                <p>等待发车</p>
                <p>预计发车: {{ formatTime(train.startOffset) }}</p>
              </div>
              <div class="train-detail" v-else-if="train.status === 'finished'">
                <p>已到达终点站</p>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 站点列表 -->
        <div class="section">
          <h3>站点列表</h3>
          <div class="station-list">
            <div 
              v-for="(station, index) in currentLine.stations" 
              :key="station.id || index"
              class="station-item"
              :class="{ 
                'current': isTrainAtStation(index),
                'passed': isStationPassed(index)
              }"
            >
              <span class="station-index">{{ index + 1 }}</span>
              <span class="station-name">{{ station.name }}</span>
              <el-tag v-if="isTrainAtStation(index)" type="success" size="small">列车停靠</el-tag>
              <el-tag v-else-if="isTrainRunningTo(index)" type="warning" size="small">列车前往</el-tag>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div class="side-panel empty" v-else>
        <el-empty description="请选择线路查看实时运行状况" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import AMapLoader from "@amap/amap-jsapi-loader"
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()

// 高德地图 Key 配置
const AMAP_KEY = '49f3c3fd7e6380f9c8b84c0f0bdd4d9f'
const AMAP_SECURITY_CONFIG = 'f2e281bddbaaed3bfc03b711e00122fa'

// 地图相关变量
let map = null
let polyline = null
let stationMarkers = []

// 当前真实时间（秒，从06:00开始计算）
const currentTime = ref(0)
const currentTimeStr = ref('')
let timeTimer = null

// 线路选择
const selectedLineId = ref('')
const availableLines = ref([])

// 当前线路数据
const currentLine = ref({
  lineId: '',
  lineName: '',
  color: '#F3D03E',
  firstTrainTime: '06:00',
  lastTrainTime: '23:00',
  interval: 300,
  segmentTime: 180,
  dwellTime: 30,
  stations: []
})

// 列车数据
const trains = ref([])

// 是否运营中
const isOperating = computed(() => {
  if (!currentLine.value.lineId) return false
  const now = new Date()
  const hours = now.getHours()
  const minutes = now.getMinutes()
  const currentMinutes = hours * 60 + minutes
  
  const [firstHour, firstMin] = currentLine.value.firstTrainTime.split(':').map(Number)
  const [lastHour, lastMin] = currentLine.value.lastTrainTime.split(':').map(Number)
  const firstMinutes = firstHour * 60 + firstMin
  const lastMinutes = lastHour * 60 + lastMin
  
  return currentMinutes >= firstMinutes && currentMinutes <= lastMinutes
})

// 计算活跃列车数
const activeTrainCount = computed(() => {
  return trains.value.filter(t => t.status === 'running' || t.status === 'stopped').length
})

// 返回上一页
const goBack = () => {
  router.back()
}

// 格式化时间显示（秒数转 HH:mm）
const formatTime = (seconds) => {
  const totalSeconds = Math.floor(seconds)
  const hours = Math.floor(totalSeconds / 3600) + 6
  const mins = Math.floor((totalSeconds % 3600) / 60)
  return `${String(hours).padStart(2, '0')}:${String(mins).padStart(2, '0')}`
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    'waiting': 'info',
    'running': 'success',
    'stopped': 'warning',
    'finished': ''
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    'waiting': '等待发车',
    'running': '运行中',
    'stopped': '停站中',
    'finished': '已结束'
  }
  return texts[status] || '未知'
}

// 获取当前站点名称
const getCurrentStationName = (train) => {
  if (train.currentStationIndex === undefined) return '-'
  return currentLine.value.stations[train.currentStationIndex]?.name || '-'
}

// 获取下一站名称
const getNextStationName = (train) => {
  if (train.nextStationIndex === undefined) return '-'
  return currentLine.value.stations[train.nextStationIndex]?.name || '终点站'
}

// 检查是否有列车在当前站点
const isTrainAtStation = (index) => {
  return trains.value.some(t => t.currentStationIndex === index && t.status === 'stopped')
}

// 检查是否有列车正在前往该站点
const isTrainRunningTo = (index) => {
  return trains.value.some(t => t.nextStationIndex === index && t.status === 'running')
}

// 检查站点是否已过
const isStationPassed = (index) => {
  return trains.value.every(t => {
    if (t.status === 'finished') return true
    if (t.status === 'waiting') return false
    return t.currentStationIndex > index || (t.currentStationIndex === index && t.status === 'running')
  })
}

// 计算到达某站的偏移时间（秒）
const calculateArrivalOffset = (stationIndex) => {
  const { segmentTime, dwellTime } = currentLine.value
  return stationIndex * (segmentTime + dwellTime)
}

// 获取到达时间
const getArrivalTime = (train, stationIndex) => {
  const arrivalTime = train.startOffset + calculateArrivalOffset(stationIndex)
  return formatTime(arrivalTime)
}

// 获取发车时间
const getDepartureTime = (train) => {
  const departureTime = train.startOffset + calculateArrivalOffset(train.currentStationIndex) + currentLine.value.dwellTime
  return formatTime(departureTime)
}

// 更新当前时间
const updateCurrentTime = () => {
  const now = new Date()
  const hours = now.getHours()
  const minutes = now.getMinutes()
  const seconds = now.getSeconds()
  
  // 显示时间字符串
  currentTimeStr.value = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
  
  // 计算从06:00开始的秒数
  const currentMinutes = hours * 60 + minutes
  const startMinutes = 6 * 60  // 06:00
  currentTime.value = (currentMinutes - startMinutes) * 60 + seconds
  
  // 如果有线路数据，更新列车位置
  if (currentLine.value.lineId && trains.value.length > 0) {
    updateAllTrains()
  }
}

// 获取可用线路列表
const fetchAvailableLines = async () => {
  try {
    const res = await request.get('/api/metro/lines')
    if (res.code === '200' && res.data) {
      availableLines.value = res.data.map(line => ({
        lineId: line.lineId,
        lineName: line.lineName
      }))
    }
  } catch (error) {
    console.error('获取线路列表失败:', error)
    ElMessage.error('获取线路列表失败')
  }
}

// 获取线路时刻表
const fetchLineSchedule = async (lineId) => {
  try {
    const res = await request.get(`/api/metro/line/${lineId}/schedule`)
    if (res.code === '200' && res.data) {
      const data = res.data
      currentLine.value = {
        lineId: data.lineId,
        lineName: data.lineName,
        color: data.color || '#F3D03E',
        firstTrainTime: data.firstTrainTime || '06:00',
        lastTrainTime: data.lastTrainTime || '23:00',
        interval: data.interval || 300,
        segmentTime: data.segmentTime || 180,
        dwellTime: data.dwellTime || 30,
        stations: data.stations || []
      }
      return true
    }
    return false
  } catch (error) {
    console.error('获取线路时刻表失败:', error)
    ElMessage.error('获取线路时刻表失败')
    return false
  }
}

// 获取列车列表
const fetchTrains = async (lineId) => {
  try {
    const res = await request.get(`/api/metro/line/${lineId}/trains`)
    if (res.code === '200' && res.data && res.data.trains) {
      return res.data.trains
    }
    return []
  } catch (error) {
    console.error('获取列车列表失败:', error)
    ElMessage.error('获取列车列表失败')
    return []
  }
}

// 线路切换
const onLineChange = async (lineId) => {
  if (!lineId) return
  
  // 清除地图上的旧数据
  clearMap()
  
  // 获取新线路数据
  const success = await fetchLineSchedule(lineId)
  if (!success) return
  
  // 获取列车数据
  const trainData = await fetchTrains(lineId)
  
  // 初始化列车
  initTrains(trainData)
  
  // 绘制线路
  drawLine()
  
  // 立即更新一次列车位置
  updateAllTrains()
  
  ElMessage.success(`已切换到 ${currentLine.value.lineName}`)
}

// 清除地图
const clearMap = () => {
  if (polyline) {
    polyline.setMap(null)
    polyline = null
  }
  stationMarkers.forEach(marker => {
    marker.setMap(null)
  })
  stationMarkers = []
  trains.value.forEach(train => {
    if (train.marker) {
      train.marker.setMap(null)
    }
  })
  trains.value = []
}

// 初始化地图
const initMap = async () => {
  try {
    window._AMapSecurityConfig = {
      securityJsCode: AMAP_SECURITY_CONFIG
    }
    
    const AMap = await AMapLoader.load({
      key: AMAP_KEY,
      version: '2.0',
      plugins: ['AMap.Scale', 'AMap.ToolBar', 'AMap.MoveAnimation']
    })
    
    map = new AMap.Map('metro-map', {
      viewMode: '3D',
      zoom: 12,
      center: [113.2644, 23.1291],
      mapStyle: 'amap://styles/normal'
    })
    
    map.addControl(new AMap.Scale())
    map.addControl(new AMap.ToolBar({ position: 'RB' }))
    
    // 获取可用线路
    await fetchAvailableLines()
    
    // 启动时间更新定时器（每秒更新一次）
    timeTimer = setInterval(updateCurrentTime, 1000)
    updateCurrentTime()
    
  } catch (error) {
    console.error('地图初始化失败:', error)
    ElMessage.error('地图加载失败: ' + (error.message || '未知错误'))
  }
}

// 绘制地铁线路
const drawLine = () => {
  const stations = currentLine.value.stations
  if (!stations || stations.length === 0) return
  
  const path = stations.map(s => [s.lng, s.lat])
  
  // 线路折线
  polyline = new window.AMap.Polyline({
    path: path,
    strokeColor: currentLine.value.color,
    strokeWeight: 8,
    strokeOpacity: 0.8,
    strokeStyle: 'solid',
    lineJoin: 'round',
    lineCap: 'round',
    showDir: true
  })
  
  // 站点标记
  stations.forEach((station, index) => {
    const marker = new window.AMap.CircleMarker({
      center: [station.lng, station.lat],
      radius: 8,
      fillColor: '#fff',
      strokeColor: currentLine.value.color,
      strokeWeight: 3,
      fillOpacity: 1,
      extData: { index, name: station.name }
    })
    marker.setMap(map)
    stationMarkers.push(marker)
    
    // 站点标签
    const label = new window.AMap.Text({
      text: station.name,
      position: [station.lng, station.lat],
      offset: new window.AMap.Pixel(0, -20),
      style: {
        'background-color': 'rgba(255,255,255,0.9)',
        'border-color': currentLine.value.color,
        'border-width': '1px',
        'border-style': 'solid',
        'padding': '4px 8px',
        'font-size': '12px',
        'border-radius': '4px',
        'font-weight': 'bold'
      }
    })
    label.setMap(map)
    stationMarkers.push(label)
  })
  
  polyline.setMap(map)
  map.setFitView([polyline], false, [50, 50, 50, 50])
}

// 创建列车图标
const createTrainIcon = (color) => {
  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 36 36">
      <circle cx="18" cy="18" r="16" fill="${color}" stroke="#fff" stroke-width="3"/>
      <rect x="10" y="13" width="16" height="10" rx="2" fill="#fff"/>
      <rect x="12" y="15" width="4" height="6" fill="${color}"/>
      <rect x="20" y="15" width="4" height="6" fill="${color}"/>
    </svg>
  `
  return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svg)))
}

// 初始化列车
const initTrains = (trainData) => {
  if (!trainData || trainData.length === 0) return
  
  const stations = currentLine.value.stations
  if (!stations || stations.length === 0) return
  
  trainData.forEach(trainInfo => {
    // 创建列车标记
    const marker = new window.AMap.Marker({
      position: [stations[0].lng, stations[0].lat],
      icon: new window.AMap.Icon({
        size: new window.AMap.Size(36, 36),
        imageSize: new window.AMap.Size(36, 36),
        image: createTrainIcon(currentLine.value.color)
      }),
      offset: new window.AMap.Pixel(-18, -18),
      autoRotation: true,
      extData: { trainId: trainInfo.trainId }
    })
    
    marker.setMap(map)
    
    // 添加到列车数组
    trains.value.push({
      trainId: trainInfo.trainId,
      startOffset: trainInfo.startOffset,
      marker: marker,
      status: 'waiting',
      currentStationIndex: 0,
      nextStationIndex: 1,
      progress: 0
    })
  })
}

// 计算列车当前位置
const calculateTrainPosition = (train, currentSeconds) => {
  const { segmentTime, dwellTime, stations } = currentLine.value
  const trainStartTime = train.startOffset
  
  // 还没发车
  if (currentSeconds < trainStartTime) {
    return { 
      status: 'waiting', 
      stationIndex: 0, 
      progress: 0 
    }
  }
  
  // 已过时间（从该列车发车开始算）
  const elapsed = currentSeconds - trainStartTime
  
  // 计算当前在哪一站或哪一段
  let accumulatedTime = 0
  const stationCount = stations.length
  const segmentCount = stationCount - 1
  
  for (let i = 0; i < segmentCount; i++) {
    // 停站时间
    if (elapsed >= accumulatedTime && elapsed < accumulatedTime + dwellTime) {
      return { 
        status: 'stopped', 
        stationIndex: i,
        progress: 0 
      }
    }
    accumulatedTime += dwellTime
    
    // 运行时间
    if (elapsed >= accumulatedTime && elapsed < accumulatedTime + segmentTime) {
      const progress = (elapsed - accumulatedTime) / segmentTime
      return {
        status: 'running',
        fromIndex: i,
        toIndex: i + 1,
        progress: progress,
        remainTime: segmentTime - (elapsed - accumulatedTime)
      }
    }
    accumulatedTime += segmentTime
  }
  
  // 已到达终点
  return { 
    status: 'finished', 
    stationIndex: segmentCount 
  }
}

// 更新所有列车位置
const updateAllTrains = () => {
  trains.value.forEach(train => {
    const pos = calculateTrainPosition(train, currentTime.value)
    
    // 状态或位置变化时才更新
    if (pos.status !== train.status || 
        pos.stationIndex !== train.currentStationIndex ||
        pos.fromIndex !== train.currentStationIndex) {
      
      train.status = pos.status
      train.currentStationIndex = pos.stationIndex !== undefined ? pos.stationIndex : pos.fromIndex
      train.nextStationIndex = pos.toIndex
      train.progress = pos.progress
      
      // 更新标记位置
      updateTrainMarker(train, pos)
    } else if (pos.status === 'running') {
      // 运行中，更新位置
      train.progress = pos.progress
      updateTrainMarker(train, pos)
    }
  })
}

// 更新列车标记
const updateTrainMarker = (train, pos) => {
  if (!train.marker) return
  
  const stations = currentLine.value.stations
  
  switch (pos.status) {
    case 'running': {
      const from = stations[pos.fromIndex]
      const to = stations[pos.toIndex]
      
      // 计算当前经纬度
      const currentLng = from.lng + (to.lng - from.lng) * pos.progress
      const currentLat = from.lat + (to.lat - from.lat) * pos.progress
      
      train.marker.setPosition([currentLng, currentLat])
      break
    }
    
    case 'stopped': {
      const station = stations[pos.stationIndex]
      train.marker.setPosition([station.lng, station.lat])
      break
    }
    
    case 'waiting': {
      const station = stations[0]
      train.marker.setPosition([station.lng, station.lat])
      break
    }
    
    case 'finished': {
      const station = stations[stations.length - 1]
      train.marker.setPosition([station.lng, station.lat])
      break
    }
  }
}

// 生命周期
onMounted(() => {
  initMap()
})

onUnmounted(() => {
  if (timeTimer) {
    clearInterval(timeTimer)
    timeTimer = null
  }
  if (map) {
    map.destroy()
    map = null
  }
})
</script>

<style scoped>
.metro-simulation {
  padding: 20px;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.control-bar {
  display: flex;
  align-items: center;
  gap: 30px;
  padding: 15px 20px;
  background: #fff;
  border-radius: 8px;
  margin: 15px 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  flex-wrap: wrap;
}

.line-selector {
  display: flex;
  align-items: center;
  gap: 10px;
}

.time-display {
  display: flex;
  align-items: center;
  gap: 10px;
}

.time-display .label,
.line-selector .label {
  color: #666;
  font-size: 14px;
}

.time-display .time {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  font-family: monospace;
}

.line-status {
  margin-left: auto;
}

.content-wrapper {
  flex: 1;
  display: flex;
  gap: 20px;
  min-height: 0;
}

.map-container {
  flex: 1;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.side-panel {
  width: 380px;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.side-panel.empty {
  display: flex;
  align-items: center;
  justify-content: center;
}

.section {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.section:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.section h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #333;
}

.line-info {
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 8px;
  border-left: 4px solid #F3D03E;
}

.line-info p {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
}

.train-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.train-item {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  border-left: 4px solid #909399;
}

.train-item.running {
  border-left-color: #67c23a;
  background: #f0f9ff;
}

.train-item.stopped {
  border-left-color: #e6a23c;
  background: #fdf6ec;
}

.train-item.waiting {
  border-left-color: #909399;
}

.train-item.finished {
  border-left-color: #f56c6c;
  opacity: 0.7;
}

.train-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.train-name {
  font-weight: bold;
  color: #333;
}

.train-detail {
  font-size: 13px;
  color: #666;
}

.train-detail p {
  margin: 4px 0;
}

.station-list {
  max-height: 400px;
  overflow-y: auto;
}

.station-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 6px;
  margin-bottom: 6px;
  background: #f5f7fa;
  transition: all 0.3s;
}

.station-item:hover {
  background: #e6f2ff;
}

.station-item.current {
  background: #67c23a;
  color: white;
}

.station-item.passed {
  opacity: 0.5;
}

.station-index {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #409eff;
  color: white;
  border-radius: 50%;
  font-size: 12px;
  font-weight: bold;
}

.station-item.current .station-index {
  background: white;
  color: #67c23a;
}

.station-name {
  flex: 1;
  font-size: 14px;
}
</style>
