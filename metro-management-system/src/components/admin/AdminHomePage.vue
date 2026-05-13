<template>
  <div class="admin-home">
    <div class="page-header">
      <h2>运营总览</h2>
      <span class="date">{{ currentDate }}</span>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="4" v-for="stat in stats" :key="stat.label">
        <div class="stat-card" :style="{ borderTopColor: stat.color }">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-icon" :style="{ color: stat.color }">
            <el-icon :size="32"><component :is="stat.icon" /></el-icon>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="content-row">
      <el-col :span="16">
        <div class="map-card">
          <div id="homeMap" ref="homeMapRef"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="status-card">
          <h3>线路运营状态</h3>
          <div class="status-list">
            <div v-for="line in lines" :key="line.lineId" class="status-item">
              <span class="line-dot" :style="{ background: line.color }"></span>
              <span class="line-name">{{ line.lineName }}</span>
              <el-tag :type="line.status === 1 ? 'success' : 'info'" size="small">
                {{ line.status === 1 ? '运营中' : '建设中' }}
              </el-tag>
            </div>
          </div>
        </div>
        <div class="notice-card" style="margin-top: 16px;">
          <h3>系统公告</h3>
          <div class="notice-list" v-if="notices.length > 0">
            <div v-for="notice in notices" :key="notice.id" class="notice-item">
              <el-icon><Warning /></el-icon>
              <span>{{ notice.title }}</span>
            </div>
          </div>
          <el-empty v-else description="暂无公告" :image-size="60" />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { Warning } from '@element-plus/icons-vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import { getAllLines, getFullMetroData } from '../../api/metroApi.js'
import axios from 'axios'

let map = null
let mapPolylines = []
let mapMarkers = []

const currentDate = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })

const stats = reactive([
  { label: '线路总数', value: 0, color: '#409EFF', icon: 'Opportunity' },
  { label: '站点总数', value: 0, color: '#67C23A', icon: 'Location' },
  { label: '换乘站数', value: 0, color: '#E6A23C', icon: 'Connection' },
  { label: '运行列车', value: 0, color: '#F56C6C', icon: 'Ship' },
  { label: '注册用户', value: 0, color: '#909399', icon: 'User' }
])

const lines = ref([])
const notices = ref([])
const homeMapRef = ref(null)

const loadData = () => {
  getFullMetroData().then(res => {
    if (res.code === '200') {
      const data = res.data
      stats[0].value = data.totalLines || 0
      stats[1].value = data.totalStations || 0

      // 计算换乘站
      let transferCount = 0
      if (data.lines) {
        data.lines.forEach(l => {
          if (l.stations) {
            l.stations.forEach(s => {
              if (s.isTransfer === 1 || s.isTransfer === '1') transferCount++
            })
          }
        })
      }
      stats[2].value = transferCount

      lines.value = data.lines || []
      stats[3].value = lines.value.length * 5 // 粗略估算
      renderMapLines(data.lines || [])
    }
  })

  // 获取用户数
  axios.get('http://localhost:8080/user').then(res => {
    if (res.data.code === '200') {
      stats[4].value = res.data.data.length
    }
  }).catch(() => {})

  // 获取公告
  axios.get('http://localhost:8080/news/limit/5').then(res => {
    if (res.data.code === '200') {
      notices.value = res.data.data
    }
  }).catch(() => {})
}

const initMap = () => {
  window._AMapSecurityConfig = {
    securityJsCode: 'f2e281bddbaaed3bfc03b711e00122fa'
  }
  AMapLoader.load({
    key: '49f3c3fd7e6380f9c8b84c0f0bdd4d9f',
    version: '2.0',
    plugins: ['AMap.Scale']
  }).then(AMap => {
    map = new AMap.Map('homeMap', {
      zoom: 11,
      center: [113.2644, 23.1291],
      viewMode: '2D',
      mapStyle: 'amap://styles/light'
    })
    map.addControl(new AMap.Scale())
    loadData()
  }).catch(e => console.error(e))
}

const renderMapLines = (lineList) => {
  if (!map || !window.AMap) return
  const AMap = window.AMap
  mapPolylines.forEach(p => map.remove(p))
  mapMarkers.forEach(m => map.remove(m))
  mapPolylines = []
  mapMarkers = []

  lineList.forEach(line => {
    if (!line.path) return
    let path
    try { path = typeof line.path === 'string' ? JSON.parse(line.path) : line.path } catch(e) { return }
    if (!path || path.length === 0) return

    const polyline = new AMap.Polyline({
      path, strokeColor: line.color || '#3498db', strokeWeight: 3,
      strokeOpacity: 0.7, lineJoin: 'round', lineCap: 'round'
    })
    map.add(polyline)
    mapPolylines.push(polyline)

    // 站点标记
    if (line.stations) {
      line.stations.forEach(st => {
        const lng = parseFloat(st.longitude)
        const lat = parseFloat(st.latitude)
        if (isNaN(lng) || isNaN(lat)) return
        const marker = new AMap.CircleMarker({
          center: [lng, lat], radius: 5,
          fillColor: line.color || '#3498db', strokeColor: '#fff', strokeWeight: 2
        })
        map.add(marker)
        mapMarkers.push(marker)
      })
    }
  })

  if (mapPolylines.length > 0) {
    map.setFitView(mapPolylines, false, [30, 30, 30, 30])
  }
}

onMounted(() => { initMap() })
onUnmounted(() => { map?.destroy() })
</script>

<style scoped>
.admin-home { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; }
.date { color: #909399; font-size: 14px; }
.stats-row { margin-bottom: 16px; }
.stat-card {
  background: #fff; border-radius: 8px; padding: 20px; position: relative;
  border-top: 3px solid; box-shadow: 0 1px 4px rgba(0,0,0,0.08); overflow: hidden;
}
.stat-value { font-size: 28px; font-weight: 700; color: #333; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.stat-icon { position: absolute; right: 16px; top: 16px; opacity: 0.3; }
.content-row { margin-top: 16px; }
.map-card { background: #fff; border-radius: 8px; padding: 16px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
.map-card h3 { margin: 0 0 12px 0; font-size: 15px; }
#homeMap { width: 100%; height: 550px; border-radius: 4px; }
.status-card, .notice-card { background: #fff; border-radius: 8px; padding: 16px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
.status-card h3, .notice-card h3 { margin: 0 0 12px 0; font-size: 15px; }
.status-list { max-height: 250px; overflow-y: auto; }
.status-item { display: flex; align-items: center; gap: 8px; padding: 6px 0; font-size: 13px; }
.status-item .line-name { flex: 1; }
.line-dot { width: 10px; height: 10px; border-radius: 50%; display: inline-block; }
.notice-item { display: flex; align-items: center; gap: 8px; padding: 8px 0; font-size: 13px; border-bottom: 1px solid #f0f0f0; }
.notice-item:last-child { border-bottom: none; }
</style>
