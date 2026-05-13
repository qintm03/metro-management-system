<template>
  <div class="home-page">
    <!-- 顶部标题栏 -->
    <header class="page-header">
      <div class="header-center">
        <h1 class="main-title">广州地铁线路图</h1>
        <div class="title-decoration"></div>
      </div>
      <div class="header-right">
        <el-button class="back-btn" @click="goBack">
          <el-icon>
            <ArrowLeft />
          </el-icon>
          返回
        </el-button>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="page-main">
      <!-- 地图区域 -->
      <section class="map-section">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-overlay">
          <el-icon class="loading-icon">
            <Loading />
          </el-icon>
          <span>正在加载地铁线路...</span>
        </div>

        <!-- 高德地图容器 -->
        <div id="home-amap-container" class="amap-container"></div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Loading } from '@element-plus/icons-vue'

// 引入 API
import { getFullMetroData } from '../../api/metroApi.js'

const router = useRouter()

// 加载状态
const loading = ref(false)

// 地铁数据
const metroData = ref({
  lines: []
})

// 高德地图实例
let map = null
const polylines = []
const markers = []

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
  map = new window.AMap.Map('home-amap-container', {
    zoom: 11,
    center: [113.2644, 23.1291], // 广州市中心
    viewMode: '2D'
  })
}

// 绘制地铁线路
const drawMetroLines = () => {
  if (!map || !metroData.value.lines) return

  // 清除之前的线路和标记
  polylines.forEach(p => p.setMap(null))
  markers.forEach(m => m.setMap(null))
  polylines.length = 0
  markers.length = 0

  metroData.value.lines.forEach(line => {
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
      strokeWeight: 4,
      strokeOpacity: 0.8,
      strokeStyle: 'solid',
      lineJoin: 'round',
      lineCap: 'round'
    })

    polyline.setMap(map)
    polylines.push(polyline)

    // 绘制站点标记
    if (line.stations) {
      line.stations.forEach(station => {
        const isTransfer = station.isTransfer === 1
        const color = getLineColor(line.lineName)
        const size = isTransfer ? 18 : 12
        const borderWidth = isTransfer ? 3 : 2

        const marker = new window.AMap.Marker({
          position: [parseFloat(station.longitude), parseFloat(station.latitude)],
          title: station.stationName,
          content: `
            <div style="
              width: ${size}px;
              height: ${size}px;
              border-radius: 50%;
              background: ${isTransfer ? color : '#fff'};
              border: ${borderWidth}px solid ${color};
              box-shadow: 0 2px 4px rgba(0,0,0,0.3);
            ">
            </div>
          `,
          offset: new window.AMap.Pixel(-size / 2, -size / 2)
        })

        marker.setMap(map)
        markers.push(marker)
      })
    }
  })
}

// 加载地铁数据
const loadMetroData = async () => {
  loading.value = true
  try {
    const res = await getFullMetroData()
    let data = res
    if ((res.code === 200 || res.code === '200') && res.data) {
      data = res.data
    }

    metroData.value = data

    // 在地图上绘制地铁线路
    drawMetroLines()

  } catch (error) {
    console.error('加载地铁数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(async () => {
  // 初始化地图
  await initMap()

  // 加载地铁数据
  await loadMetroData()
})

onUnmounted(() => {
  // 销毁地图
  if (map) {
    map.destroy()
    map = null
  }
})
</script>

<style scoped>
.home-page {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #0a1628 0%, #1a2a4a 50%, #0d1a2d 100%);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  margin: -24px;
  padding: 24px;
  box-sizing: content-box;
}

/* ==================== 顶部标题栏 ==================== */
.page-header {
  height: 70px;
  background: linear-gradient(180deg, rgba(10, 30, 60, 0.95) 0%, rgba(10, 30, 60, 0.8) 100%);
  border-bottom: 1px solid rgba(0, 212, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  position: relative;
}

.page-header::before {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent 0%, #00d4ff 50%, transparent 100%);
}

/* 头部中央 - 标题 */
.header-center {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.main-title {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  text-shadow: 0 0 20px rgba(0, 212, 255, 0.5), 0 0 40px rgba(0, 212, 255, 0.3);
  letter-spacing: 4px;
  margin: 0;
  background: linear-gradient(180deg, #fff 0%, #00d4ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title-decoration {
  width: 300px;
  height: 3px;
  background: linear-gradient(90deg, transparent 0%, #00d4ff 30%, #00d4ff 70%, transparent 100%);
  margin-top: 8px;
  position: relative;
}

.title-decoration::before,
.title-decoration::after {
  content: '';
  position: absolute;
  top: -3px;
  width: 10px;
  height: 10px;
  background: #00d4ff;
  transform: rotate(45deg);
}

.title-decoration::before { left: 30%; }
.title-decoration::after { right: 30%; }

/* 头部右侧 - 按钮 */
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: flex-end;
}

.back-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #fff;
  padding: 10px 20px;
  font-size: 14px;
  border-radius: 6px;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* ==================== 主内容区 ==================== */
.page-main {
  flex: 1;
  overflow: hidden;
  padding: 15px;
}

/* ==================== 地图区域 ==================== */
.map-section {
  width: 100%;
  height: 100%;
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(0, 212, 255, 0.3);
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
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  color: #fff;
  font-size: 14px;
}

.loading-icon {
  font-size: 40px;
  color: #00d4ff;
  margin-bottom: 15px;
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
