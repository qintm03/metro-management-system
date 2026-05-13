<template>
  <div class="heatmap-analysis">
    <el-card class="header-card" shadow="never">
      <div class="header-content">
        <h2 class="page-title">热力分析</h2>
        <div class="toolbar">
          <el-radio-group v-model="currentMode" @change="onModeChange">
            <el-radio-button value="density">站点密度</el-radio-button>
            <el-radio-button value="population">人口热力</el-radio-button>
          </el-radio-group>

          <template v-if="currentMode === 'population'">
            <el-radio-group v-model="isAnnual" size="small" @change="onAnnualChange">
              <el-radio-button :value="true">年均</el-radio-button>
              <el-radio-button :value="false">月均</el-radio-button>
            </el-radio-group>

            <el-select v-model="selectedYear" placeholder="选择年份" size="small" style="width:110px">
              <el-option v-for="y in availableYears" :key="y" :label="`${y}年`" :value="y" />
            </el-select>

            <el-select v-if="!isAnnual" v-model="selectedMonth" placeholder="选择月份" size="small" style="width:110px">
              <el-option v-for="m in availableMonths" :key="m" :label="`${m}月`" :value="m" />
            </el-select>
          </template>

          <el-button type="primary" @click="onGenerate">
            {{ currentMode === 'density' ? '生成密度图' : '生成人口热力图' }}
          </el-button>

          <div class="stats" v-if="currentMode === 'population' && tableData.length > 0">
            <span class="stat-item">总人口: <strong>{{ totalPopulation.toLocaleString() }}</strong></span>
            <span class="stat-item">站点数: {{ stationCount }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <div class="main-content">
      <el-card class="map-section" shadow="never">
        <div id="heatmap-container" class="map-container"></div>
        <div class="heatmap-legend">
          <span class="legend-label">低</span>
          <div class="legend-gradient"></div>
          <span class="legend-label">高</span>
        </div>
      </el-card>

      <el-card class="stats-panel" shadow="never">
        <template #header>
          <div class="panel-header">
            <span>{{ panelTitle }}</span>
            <el-tag v-if="currentMode === 'density'" type="info">密度模式</el-tag>
            <el-tag v-else-if="isAnnual" type="info">{{ selectedYear }}年均</el-tag>
            <el-tag v-else type="info">{{ selectedYear }}年{{ selectedMonth }}月</el-tag>
          </div>
        </template>
        <el-table :data="tableData" stripe height="200" size="small">
          <el-table-column prop="stationName" label="站点" min-width="100" />
          <el-table-column v-if="currentMode === 'population'" prop="population" label="人口" sortable min-width="80" />
          <el-table-column v-if="currentMode === 'population'" prop="crowdLevel" label="拥挤度" sortable min-width="80">
            <template #default="{ row }">
              <el-tag :type="getCrowdLevelType(Number(row.crowdLevel))" size="small">
                {{ row.crowdLevel }}%
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column v-if="currentMode === 'density'" prop="lineId" label="线路" min-width="80" />
        </el-table>

        <div class="chart-section" v-if="currentMode === 'population' && chartReady">
          <h4>{{ chartTitle }}</h4>
          <div ref="chartRef" class="chart"></div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { getFullMetroData } from '@/api/metroApi'
import * as echarts from 'echarts'
import AMapLoader from '@amap/amap-jsapi-loader'
import request from '@/utils/request'

const AMAP_KEY = '49f3c3fd7e6380f9c8b84c0f0bdd4d9f'
const AMAP_SECURITY_CONFIG = 'f2e281bddbaaed3bfc03b711e00122fa'

const LINE_COLORS = {
  '1号线': '#F3D03E', '2号线': '#00629B', '3号线': '#ECA154',
  '4号线': '#00843D', '5号线': '#C5003E', '6号线': '#80225F',
  '7号线': '#97D700', '8号线': '#008C95', '9号线': '#71CC98',
  '10号线': '#B3814B', '11号线': '#FFB00A', '12号线': '#007A5E',
  '13号线': '#8E4B99', '14号线': '#81312F', '18号线': '#004B87',
  '21号线': '#A45A2A', '22号线': '#CD5228',
  '28号线': '#6E6E6E', 'APM线': '#00B5E2', '广佛线': '#5DAE40',
  '有轨电车': '#D32F2F'
}

const currentMode = ref('density')
const isAnnual = ref(true)
const selectedYear = ref(2025)
const selectedMonth = ref(1)
const availableYears = ref([])
const yearMonths = ref([])
const tableData = ref([])
const chartData = ref([])
const totalPopulation = ref(0)
const stationCount = ref(0)
const metroLines = ref([])
const metroStations = ref([])
const chartReady = ref(false)

const chartRef = ref(null)

let map = null
let heatmapLayer = null
let barChart = null
let linePolylines = []

const panelTitle = computed(() => {
  if (currentMode.value === 'density') return '站点列表'
  return '人口数据'
})

const chartTitle = computed(() => {
  if (currentMode.value === 'density') return ''
  if (isAnnual.value) return selectedYear.value + '年人口月度趋势'
  return '人口 TOP10'
})

const availableMonths = computed(() => {
  return yearMonths.value
    .filter(m => m.year === selectedYear.value)
    .map(m => m.month)
    .sort((a, b) => a - b)
})

const getCrowdLevelType = (level) => {
  if (level < 30) return 'info'
  if (level < 60) return 'success'
  if (level < 85) return 'warning'
  return 'danger'
}

const loadMetroData = async () => {
  try {
    const data = await getFullMetroData()
    metroLines.value = data.lines || []
    metroStations.value = data.stations || []
  } catch (e) {
    console.error('加载地铁数据失败:', e)
  }
}

const loadYearOptions = async () => {
  try {
    const yearsRes = await request.get('/api/population/years')
    availableYears.value = yearsRes.data || []
    if (availableYears.value.length > 0) {
      selectedYear.value = availableYears.value[0]
    }
    const ymRes = await request.get('/api/population/year-months')
    yearMonths.value = ymRes.data || []
  } catch (e) {
    console.error('加载年份选项失败:', e)
  }
}

const initMap = async () => {
  try {
    window._AMapSecurityConfig = { securityJsCode: AMAP_SECURITY_CONFIG }
    let AMap
    if (window.AMap && window.AMap.map) {
      AMap = window.AMap
    } else {
      AMap = await AMapLoader.load({
        key: AMAP_KEY,
        version: '2.0',
        plugins: ['AMap.HeatMap', 'AMap.Scale', 'AMap.ToolBar']
      })
    }
    map = new AMap.Map('heatmap-container', {
      zoom: 12,
      center: [113.264385, 23.129112],
      viewMode: '2D'
    })
    map.addControl(new AMap.Scale())
    map.addControl(new AMap.ToolBar({ position: 'RB' }))
    heatmapLayer = new AMap.HeatMap(map, {
      radius: 25,
      opacity: [0, 0.6],
      gradient: {
        0.4: 'rgb(0, 137, 230)',
        0.6: 'rgb(102, 194, 58)',
        0.8: 'rgb(230, 162, 60)',
        1.0: 'rgb(245, 108, 108)'
      }
    })
  } catch (e) {
    console.error('地图初始化失败:', e)
  }
}

const clearHeatmap = () => {
  if (heatmapLayer) {
    heatmapLayer.setDataSet({ data: [], max: 1 })
  }
}

const clearLinePolylines = () => {
  if (map && linePolylines.length > 0) {
    map.remove(linePolylines)
    linePolylines = []
  }
}

const drawMetroLines = () => {
  if (!map || metroLines.value.length === 0) return
  clearLinePolylines()

  metroLines.value.forEach(line => {
    const lineName = line.lineName
    const stations = metroStations.value[line.lineId] || []
    if (stations.length < 2) return

    const path = stations
      .filter(s => s.longitude && s.latitude)
      .map(s => [Number(s.longitude), Number(s.latitude)])

    if (path.length < 2) return

    const color = LINE_COLORS[lineName] || line.color || '#0089e6'
    const polyline = new window.AMap.Polyline({
      path: path,
      strokeColor: color,
      strokeWeight: 4,
      strokeOpacity: 0.7,
      lineJoin: 'round',
      lineCap: 'round',
      zIndex: 50,
      extData: { lineName }
    })
    polyline.setMap(map)
    linePolylines.push(polyline)
  })
}

const onModeChange = () => {
  clearHeatmap()
  clearLinePolylines()
  tableData.value = []
  chartData.value = []
  totalPopulation.value = 0
  chartReady.value = false
  if (barChart) {
    barChart.dispose()
    barChart = null
  }
}

const onAnnualChange = () => {
  chartReady.value = false
  chartData.value = []
  if (barChart) {
    barChart.dispose()
    barChart = null
  }
}

const onGenerate = async () => {
  clearLinePolylines()
  if (currentMode.value === 'density') {
    await generateDensityMap()
  } else {
    await generatePopulationMap()
  }
}

const generateDensityMap = async () => {
  try {
    const res = await request.get('/api/population/stations')
    const stations = res.data || []
    stationCount.value = stations.length
    tableData.value = stations

    drawMetroLines()

    const points = stations
      .filter(s => s.longitude && s.latitude)
      .map(s => ({
        lng: Number(s.longitude),
        lat: Number(s.latitude),
        count: 1
      }))

    if (points.length > 0 && heatmapLayer) {
      heatmapLayer.setDataSet({ data: points, max: 5 })
    }
  } catch (e) {
    console.error('生成密度图失败:', e)
  }
}

const generatePopulationMap = async () => {
  try {
    if (isAnnual.value) {
      await generateAnnualTrend()
    } else {
      await generateMonthlyTop10()
    }
  } catch (e) {
    console.error('生成人口热力图失败:', e)
  }
}

const generateMonthlyTop10 = async () => {
  if (!selectedMonth.value) return
  const api = `/api/population/monthly?year=${selectedYear.value}&month=${selectedMonth.value}`

  const res = await request.get(api)
  const data = res.data || []
  tableData.value = data
  chartData.value = [...data].sort((a, b) => b.population - a.population).slice(0, 10)
  stationCount.value = data.length
  totalPopulation.value = data.reduce((sum, d) => sum + (d.population || 0), 0)

  const points = data
    .filter(d => d.longitude && d.latitude)
    .map(d => ({
      lng: Number(d.longitude),
      lat: Number(d.latitude),
      count: d.population
    }))

  if (points.length > 0 && heatmapLayer) {
    const maxVal = Math.max(...points.map(p => p.count), 100)
    heatmapLayer.setDataSet({ data: points, max: maxVal })
  }

  chartReady.value = true
  await nextTick()
  initChart()
  await nextTick()
  renderTop10Chart()
}

const generateAnnualTrend = async () => {
  const allMonthlyData = []

  for (let month = 1; month <= 12; month++) {
    try {
      const res = await request.get(
        `/api/population/monthly?year=${selectedYear.value}&month=${month}`
      )
      const data = res.data || []
      const monthTotal = data.reduce((sum, d) => sum + (d.population || 0), 0)
      allMonthlyData.push({ month, total: monthTotal })
    } catch (e) {
      allMonthlyData.push({ month, total: 0 })
    }
  }

  const annualRes = await request.get(`/api/population/annual?year=${selectedYear.value}`)
  const annualData = annualRes.data || []
  tableData.value = annualData
  stationCount.value = annualData.length
  totalPopulation.value = annualData.reduce((sum, d) => sum + (d.population || 0), 0)

  const points = annualData
    .filter(d => d.longitude && d.latitude)
    .map(d => ({
      lng: Number(d.longitude),
      lat: Number(d.latitude),
      count: d.population
    }))

  if (points.length > 0 && heatmapLayer) {
    const maxVal = Math.max(...points.map(p => p.count), 100)
    heatmapLayer.setDataSet({ data: points, max: maxVal })
  }

  chartData.value = allMonthlyData
  chartReady.value = true
  await nextTick()
  initChart()
  await nextTick()
  renderTrendChart()
}

const initChart = () => {
  if (!chartRef.value) return
  if (barChart) {
    barChart.dispose()
    barChart = null
  }
  barChart = echarts.init(chartRef.value)
}

const renderTop10Chart = () => {
  if (!barChart) return
  const data = chartData.value
  if (data.length === 0) return

  barChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 50, right: 20, top: 10, bottom: 50 },
    xAxis: {
      type: 'category',
      data: data.map(d => d.stationName),
      axisLabel: { rotate: 30, fontSize: 10 }
    },
    yAxis: { type: 'value', name: '人口' },
    series: [{
      type: 'bar',
      data: data.map(d => d.population || 0),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#0089e6' },
          { offset: 1, color: '#66c23a' }
        ]),
        borderRadius: [2, 2, 0, 0]
      }
    }]
  }, true)
}

const renderTrendChart = () => {
  if (!barChart) return
  const data = chartData.value
  if (data.length === 0) return

  barChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 50, right: 20, top: 10, bottom: 30 },
    xAxis: {
      type: 'category',
      data: data.map(d => d.month + '月'),
      axisLabel: { fontSize: 10 }
    },
    yAxis: { type: 'value', name: '总人口' },
    series: [{
      type: 'line',
      data: data.map(d => d.total),
      smooth: true,
      lineStyle: { color: '#0089e6', width: 2 },
      itemStyle: { color: '#0089e6' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(0, 137, 230, 0.3)' },
          { offset: 1, color: 'rgba(0, 137, 230, 0.05)' }
        ])
      },
      markLine: {
        silent: true,
        data: [{ type: 'average', name: '平均值' }],
        lineStyle: { color: '#f56c6c', type: 'dashed' }
      }
    }]
  }, true)
}

onMounted(async () => {
  await loadMetroData()
  await initMap()
  await loadYearOptions()
})

onUnmounted(() => {
  if (map) { map.destroy(); map = null }
  if (barChart) { barChart.dispose(); barChart = null }
  heatmapLayer = null
  linePolylines = []
})
</script>

<style scoped>
.heatmap-analysis {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header-card {
  flex-shrink: 0;
  margin-bottom: 12px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  color: #1a1a2e;
  font-weight: 600;
}

.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #666;
}

.stat-item strong {
  color: #0089e6;
}

.main-content {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: 1.8fr 1fr;
  gap: 12px;
  overflow: hidden;
}

.map-section {
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
}

.map-section :deep(.el-card__body) {
  flex: 1;
  padding: 0;
  overflow: hidden;
}

.map-container {
  width: 100%;
  height: 100%;
}

.heatmap-legend {
  position: absolute;
  bottom: 16px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.92);
  padding: 8px 12px;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
  z-index: 100;
}

.legend-label {
  font-size: 11px;
  color: #666;
}

.legend-gradient {
  width: 120px;
  height: 10px;
  background: linear-gradient(to right, #0089e6, #66c23a, #e6a23c, #f56c6c);
  border-radius: 5px;
}

.stats-panel {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.stats-panel :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 16px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-section {
  margin-top: 12px;
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.chart-section h4 {
  margin: 0 0 8px;
  font-size: 14px;
  color: #333;
  flex-shrink: 0;
}

.chart {
  width: 100%;
  flex: 1;
  min-height: 180px;
}

@media (max-width: 960px) {
  .main-content {
    grid-template-columns: 1fr;
    grid-template-rows: 1fr 1fr;
  }
}
</style>
