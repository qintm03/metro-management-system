<template>
  <div class="metro-data-collector">
    <el-page-header title="地铁数据采集工具" @back="goBack" />

    <div class="content-wrapper">
      <!-- 左侧控制面板 -->
      <div class="control-panel">
        <el-card>
          <template #header>
            <span>数据采集控制</span>
          </template>

          <div class="control-section">
            <h4>1. 加载地铁图</h4>
            <el-button type="primary" @click="initSubway" :loading="loading">
              初始化地铁图
            </el-button>
          </div>

          <div class="control-section" v-if="isReady">
            <h4>2. 获取线路列表</h4>
            <el-button type="success" @click="getLineList">
              获取所有线路
            </el-button>
          </div>

          <div class="control-section" v-if="lines.length > 0">
            <h4>3. 采集站点数据</h4>
            <el-button type="warning" @click="collectAllStations" :loading="collecting">
              采集所有站点
            </el-button>
            <el-progress
              v-if="collecting"
              :percentage="progressPercent"
              :status="progressStatus"
              style="margin-top: 10px;"
            />
          </div>

          <div class="control-section" v-if="stations.length > 0">
            <h4>4. 导出数据</h4>
            <el-button type="primary" @click="exportJSON">
              <el-icon><Download /></el-icon>
              导出 JSON
            </el-button>
            <el-button type="success" @click="exportSQL">
              <el-icon><Download /></el-icon>
              导出 SQL
            </el-button>
            <el-button @click="exportCSV">
              <el-icon><Download /></el-icon>
              导出 CSV
            </el-button>
          </div>

          <div class="control-section" v-if="stations.length > 0">
            <h4>5. 数据验证</h4>
            <el-button type="info" @click="validateData">
              <el-icon><Check /></el-icon>
              验证数据完整性
            </el-button>
          </div>
        </el-card>

        <!-- 统计信息 -->
        <el-card v-if="stats.totalLines > 0" style="margin-top: 15px;">
          <template #header>
            <span>数据统计</span>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="城市">{{ stats.city }}</el-descriptions-item>
            <el-descriptions-item label="线路总数">{{ stats.totalLines }} 条</el-descriptions-item>
            <el-descriptions-item label="站点总数">{{ stats.totalStations }} 个</el-descriptions-item>
            <el-descriptions-item label="换乘站数量">{{ stats.transferStations }} 个</el-descriptions-item>
            <el-descriptions-item label="采集时间">{{ stats.updateTime }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </div>

      <!-- 右侧地图和数据展示 -->
      <div class="main-content">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="地图视图" name="map">
            <div id="metro-map" class="map-container"></div>
          </el-tab-pane>

          <el-tab-pane label="线路列表" name="lines">
            <el-table :data="lines" style="width: 100%" height="600">
              <el-table-column type="index" width="50" />
              <el-table-column prop="name" label="线路名称" width="150">
                <template #default="scope">
                  <el-tag :style="{ backgroundColor: scope.row.color, color: '#fff' }">
                    {{ scope.row.name }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="type" label="线路类型" width="120" />
              <el-table-column prop="stationCount" label="站点数" width="100" />
              <el-table-column prop="openDate" label="开通日期" width="120" />
              <el-table-column label="操作">
                <template #default="scope">
                  <el-button size="small" @click="showLineDetail(scope.row)">
                    查看详情
                  </el-button>
                  <el-button size="small" type="primary" @click="highlightLine(scope.row)">
                    高亮显示
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="站点列表" name="stations">
            <el-table :data="allStations" style="width: 100%" height="600">
              <el-table-column type="index" width="50" />
              <el-table-column prop="name" label="站点名称" width="120" />
              <el-table-column prop="lineName" label="所属线路" width="120" />
              <el-table-column prop="sequence" label="序号" width="80" />
              <el-table-column prop="longitude" label="经度" width="120" />
              <el-table-column prop="latitude" label="纬度" width="120" />
              <el-table-column prop="isTransfer" label="是否换乘" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.isTransfer ? 'danger' : 'info'">
                    {{ scope.row.isTransfer ? '是' : '否' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作">
                <template #default="scope">
                  <el-button size="small" @click="locateStation(scope.row)">
                    定位
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="采集日志" name="logs">
            <div class="log-container">
              <el-timeline>
                <el-timeline-item
                  v-for="(log, index) in logs"
                  :key="index"
                  :type="log.type"
                  :timestamp="log.time"
                >
                  {{ log.message }}
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 线路详情对话框 -->
    <el-dialog v-model="lineDialogVisible" title="线路详情" width="600px">
      <el-descriptions :column="2" border v-if="selectedLine">
        <el-descriptions-item label="线路名称">{{ selectedLine.name }}</el-descriptions-item>
        <el-descriptions-item label="线路类型">{{ selectedLine.type }}</el-descriptions-item>
        <el-descriptions-item label="开通日期">{{ selectedLine.openDate }}</el-descriptions-item>
        <el-descriptions-item label="站点数量">{{ selectedLine.stations?.length || 0 }}</el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <h4>站点列表</h4>
      <el-table :data="selectedLine?.stations" style="width: 100%" max-height="300">
        <el-table-column type="index" width="50" />
        <el-table-column prop="name" label="站点名称" />
        <el-table-column prop="longitude" label="经度" />
        <el-table-column prop="latitude" label="纬度" />
      </el-table>
    </el-dialog>

    <!-- 数据验证结果对话框 -->
    <el-dialog v-model="validationDialogVisible" title="数据验证结果" width="500px">
      <el-result
        :icon="validationResult.success ? 'success' : 'warning'"
        :title="validationResult.success ? '数据验证通过' : '发现数据问题'"
        :sub-title="validationResult.message"
      >
        <template #extra>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="总站点数">{{ validationResult.totalStations }}</el-descriptions-item>
            <el-descriptions-item label="有效坐标">{{ validationResult.validCoords }}</el-descriptions-item>
            <el-descriptions-item label="缺失坐标">{{ validationResult.missingCoords }}</el-descriptions-item>
            <el-descriptions-item label="重复站点">{{ validationResult.duplicates }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </el-result>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download, Check } from '@element-plus/icons-vue'
import AMapLoader from '@amap/amap-jsapi-loader'

const router = useRouter()

// 高德地图 Key - 请替换为你自己的 Key
const AMAP_KEY = import.meta.env.VITE_AMAP_KEY || '49f3c3fd7e6380f9c8b84c0f0bdd4d9f'
const AMAP_SECURITY_CONFIG = import.meta.env.VITE_AMAP_SECURITY_CONFIG || ''

// 状态变量
const loading = ref(false)
const collecting = ref(false)
const isReady = ref(false)
const progressPercent = ref(0)
const progressStatus = ref('')
const activeTab = ref('map')
const lineDialogVisible = ref(false)
const validationDialogVisible = ref(false)
const selectedLine = ref(null)

// 数据存储
const lines = ref([])
const stations = ref([])
const logs = ref([])
const map = ref(null)
const subway = ref(null)

// 统计数据
const stats = ref({
  city: '广州',
  totalLines: 0,
  totalStations: 0,
  transferStations: 0,
  updateTime: ''
})

// 验证结果
const validationResult = ref({
  success: false,
  message: '',
  totalStations: 0,
  validCoords: 0,
  missingCoords: 0,
  duplicates: 0
})

// 计算属性：所有站点列表
const allStations = computed(() => {
  const result = []
  lines.value.forEach(line => {
    if (line.stations) {
      line.stations.forEach(station => {
        result.push({
          ...station,
          lineName: line.name,
          lineId: line.id
        })
      })
    }
  })
  return result
})

// 添加日志
const addLog = (message, type = 'primary') => {
  logs.value.unshift({
    message,
    type,
    time: new Date().toLocaleTimeString()
  })
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 初始化地铁图
const initSubway = async () => {
  loading.value = true
  addLog('开始初始化地铁图...', 'primary')

  try {
    // 配置安全密钥
    if (AMAP_SECURITY_CONFIG) {
      window._AMapSecurityConfig = {
        securityJsCode: AMAP_SECURITY_CONFIG
      }
    }

    const AMap = await AMapLoader.load({
      key: AMAP_KEY,
      version: '2.0',
      plugins: ['AMap.LineSearch', 'AMap.StationSearch']
    })

    // 初始化地图
    map.value = new AMap.Map('metro-map', {
      zoom: 11,
      center: [113.2644, 23.1291], // 广州市中心
      viewMode: '2D'
    })

    // 加载地铁图层
    subway.value = new AMap.Subway({
      map: map.value,
      city: '广州'
    })

    isReady.value = true
    addLog('地铁图初始化成功', 'success')
    ElMessage.success('地铁图初始化成功')
  } catch (error) {
    console.error('初始化失败:', error)
    addLog(`初始化失败: ${error.message}`, 'danger')
    ElMessage.error('初始化失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 获取线路列表
const getLineList = async () => {
  addLog('开始获取线路列表...', 'primary')

  try {
    const AMap = await AMapLoader.load({
      key: AMAP_KEY,
      version: '2.0'
    })

    // 使用公交路线搜索获取地铁线路
    const lineSearch = new AMap.LineSearch({
      pageIndex: 1,
      pageSize: 50,
      city: '广州',
      extensions: 'all'
    })

    lineSearch.search('地铁', (status, result) => {
      if (status === 'complete' && result.info === 'OK') {
        const lineInfos = result.lineInfo || []
        addLog(`获取到 ${lineInfos.length} 条线路信息`, 'success')

        // 解析线路数据
        parseLineData(lineInfos)
      } else {
        addLog('获取线路列表失败', 'danger')
        ElMessage.error('获取线路列表失败')
      }
    })
  } catch (error) {
    console.error('获取线路失败:', error)
    addLog(`获取线路失败: ${error.message}`, 'danger')
  }
}

// 解析线路数据
const parseLineData = (lineInfos) => {
  const parsedLines = []
  const lineNameMap = new Map()

  lineInfos.forEach((line, index) => {
    const name = line.name || ''

    // 只处理地铁线路
    if (!name.includes('地铁') && !/\d+号线/.test(name)) {
      return
    }

    // 提取线路名称
    let lineName = ''
    const match = name.match(/(\d+号线(?:北延段|知识城支线)?|APM线|广佛线)/)
    if (match) {
      lineName = match[1]
    } else {
      lineName = name.replace('广州地铁', '').trim()
    }

    // 去重
    if (lineNameMap.has(lineName)) {
      return
    }
    lineNameMap.set(lineName, true)

    // 解析站点
    const stations = []
    const busstops = line.via_stops || []

    busstops.forEach((stop, idx) => {
      const location = stop.location
      if (location) {
        const [lng, lat] = location.split(',').map(Number)
        stations.push({
          id: `L${(parsedLines.length + 1).toString().padStart(2, '0')}_S${(idx + 1).toString().padStart(3, '0')}`,
          name: stop.name.replace('(地铁)', '').replace('地铁站', ''),
          sequence: idx + 1,
          longitude: lng,
          latitude: lat,
          isTransfer: false,
          transferLines: [],
          address: ''
        })
      }
    })

    if (stations.length > 0) {
      parsedLines.push({
        id: parsedLines.length + 1,
        name: lineName,
        color: getLineColor(lineName),
        type: getLineType(lineName),
        openDate: '',
        stationCount: stations.length,
        stations: stations
      })
    }
  })

  lines.value = parsedLines
  updateStats()
  addLog(`成功解析 ${parsedLines.length} 条线路`, 'success')
  ElMessage.success(`成功获取 ${parsedLines.length} 条线路`)

  // 在地图上显示线路
  displayLinesOnMap()
}

// 获取线路颜色
const getLineColor = (lineName) => {
  const colorMap = {
    '1号线': '#F3D03E',
    '2号线': '#00629B',
    '3号线': '#ECA154',
    '3号线北延段': '#ECA154',
    '4号线': '#00843D',
    '5号线': '#C5003E',
    '6号线': '#700F1E',
    '7号线': '#97D700',
    '8号线': '#008E9C',
    '9号线': '#71CC98',
    '13号线': '#8E8C13',
    '14号线': '#792330',
    '14号线知识城支线': '#792330',
    '18号线': '#0047BB',
    '21号线': '#201747',
    '22号线': '#D49A25',
    '广佛线': '#C4D600',
    'APM线': '#00BFFF'
  }
  return colorMap[lineName] || '#999999'
}

// 获取线路类型
const getLineType = (lineName) => {
  if (lineName.includes('18') || lineName.includes('22')) {
    return '高速地铁'
  } else if (lineName === '广佛线') {
    return '城际地铁'
  } else if (lineName === 'APM线') {
    return '自动旅客捷运'
  } else {
    return '普速地铁'
  }
}

// 在地图上显示线路
const displayLinesOnMap = () => {
  if (!map.value) return

  lines.value.forEach(line => {
    if (line.stations && line.stations.length > 1) {
      const path = line.stations.map(s => [s.longitude, s.latitude])

      // 绘制线路
      const polyline = new window.AMap.Polyline({
        path: path,
        strokeColor: line.color,
        strokeWeight: 4,
        strokeOpacity: 0.8,
        showDir: true
      })

      map.value.add(polyline)

      // 添加站点标记
      line.stations.forEach(station => {
        const marker = new window.AMap.CircleMarker({
          center: [station.longitude, station.latitude],
          radius: station.isTransfer ? 8 : 5,
          fillColor: station.isTransfer ? '#ff0000' : line.color,
          strokeColor: '#fff',
          strokeWeight: 2,
          fillOpacity: 0.9
        })

        marker.setExtData({ station })
        marker.on('click', () => {
          ElMessage.info(`${station.name} - ${line.name}`)
        })

        map.value.add(marker)
      })
    }
  })
}

// 采集所有站点数据
const collectAllStations = async () => {
  collecting.value = true
  progressPercent.value = 0
  progressStatus.value = ''

  addLog('开始采集站点详细数据...', 'primary')

  const totalStations = allStations.value.length
  let processed = 0

  try {
    const AMap = await AMapLoader.load({
      key: AMAP_KEY,
      version: '2.0'
    })

    const geocoder = new AMap.Geocoder({
      city: '广州'
    })

    for (const line of lines.value) {
      for (const station of line.stations) {
        try {
          // 使用地理编码获取详细地址
          const result = await new Promise((resolve) => {
            geocoder.getAddress([station.longitude, station.latitude], (status, result) => {
              if (status === 'complete' && result.regeocode) {
                resolve(result.regeocode.formattedAddress)
              } else {
                resolve('')
              }
            })
          })

          station.address = result

          // 检查是否是换乘站
          const sameNameStations = allStations.value.filter(s =>
            s.name === station.name && s.lineId !== line.id
          )

          if (sameNameStations.length > 0) {
            station.isTransfer = true
            station.transferLines = [line.name, ...sameNameStations.map(s => s.lineName)]
          }

          processed++
          progressPercent.value = Math.round((processed / totalStations) * 100)

          if (processed % 10 === 0) {
            addLog(`已处理 ${processed}/${totalStations} 个站点`, 'primary')
          }

          // 添加延迟避免请求过快
          await new Promise(resolve => setTimeout(resolve, 100))
        } catch (error) {
          console.error(`处理站点 ${station.name} 失败:`, error)
        }
      }
    }

    progressStatus.value = 'success'
    addLog('站点数据采集完成', 'success')
    ElMessage.success('站点数据采集完成')

    // 更新统计
    updateStats()
  } catch (error) {
    console.error('采集失败:', error)
    progressStatus.value = 'exception'
    addLog(`采集失败: ${error.message}`, 'danger')
    ElMessage.error('采集失败: ' + error.message)
  } finally {
    collecting.value = false
  }
}

// 更新统计数据
const updateStats = () => {
  stats.value.totalLines = lines.value.length
  stats.value.totalStations = allStations.value.length
  stats.value.transferStations = allStations.value.filter(s => s.isTransfer).length
  stats.value.updateTime = new Date().toLocaleString()
}

// 导出 JSON
const exportJSON = () => {
  const data = {
    city: stats.value.city,
    updateTime: new Date().toISOString().split('T')[0],
    totalLines: stats.value.totalLines,
    totalStations: stats.value.totalStations,
    lines: lines.value.map(line => ({
      id: line.id,
      name: line.name,
      color: line.color,
      type: line.type,
      openDate: line.openDate,
      stationCount: line.stations.length,
      stations: line.stations
    }))
  }

  downloadFile(
    JSON.stringify(data, null, 2),
    `guangzhou_metro_data_${new Date().toISOString().split('T')[0]}.json`,
    'application/json'
  )

  addLog('JSON 文件导出成功', 'success')
  ElMessage.success('JSON 文件导出成功')
}

// 导出 SQL
const exportSQL = () => {
  let sql = `-- 广州地铁数据\n`
  sql += `-- 生成时间: ${new Date().toLocaleString()}\n\n`

  // 线路表
  sql += `-- 地铁线路表数据\n`
  sql += `INSERT INTO metro_line (id, line_code, line_name, line_color, line_type, total_stations, open_date, status) VALUES\n`

  const lineValues = lines.value.map(line => {
    const lineCode = `L${line.id.toString().padStart(2, '0')}`
    return `(${line.id}, '${lineCode}', '${line.name}', '${line.color}', '${line.type}', ${line.stations.length}, '${line.openDate}', 1)`
  })
  sql += lineValues.join(',\n') + ';\n\n'

  // 站点表
  sql += `-- 地铁站点表数据\n`
  sql += `INSERT INTO metro_station (line_id, station_code, station_name, sequence, longitude, latitude, is_transfer, address, status) VALUES\n`

  const stationValues = []
  lines.value.forEach(line => {
    line.stations.forEach(station => {
      const isTransfer = station.isTransfer ? 1 : 0
      stationValues.push(`(${line.id}, '${station.id}', '${station.name}', ${station.sequence}, ${station.longitude}, ${station.latitude}, ${isTransfer}, '${station.address}', 1)`)
    })
  })

  // 分批写入
  const batchSize = 100
  for (let i = 0; i < stationValues.length; i += batchSize) {
    const batch = stationValues.slice(i, i + batchSize)
    if (i === 0) {
      sql += batch.join(',\n')
    } else {
      sql += `;\n\nINSERT INTO metro_station (line_id, station_code, station_name, sequence, longitude, latitude, is_transfer, address, status) VALUES\n`
      sql += batch.join(',\n')
    }
  }
  sql += ';\n'

  downloadFile(sql, `guangzhou_metro_data_${new Date().toISOString().split('T')[0]}.sql`, 'text/plain')

  addLog('SQL 文件导出成功', 'success')
  ElMessage.success('SQL 文件导出成功')
}

// 导出 CSV
const exportCSV = () => {
  let csv = '线路名称,站点序号,站点名称,经度,纬度,是否换乘,所属线路\n'

  lines.value.forEach(line => {
    line.stations.forEach(station => {
      const isTransfer = station.isTransfer ? '是' : '否'
      const transferLines = station.transferLines ? station.transferLines.join(';') : line.name
      csv += `${line.name},${station.sequence},${station.name},${station.longitude},${station.latitude},${isTransfer},"${transferLines}"\n`
    })
  })

  downloadFile(csv, `guangzhou_metro_data_${new Date().toISOString().split('T')[0]}.csv`, 'text/csv')

  addLog('CSV 文件导出成功', 'success')
  ElMessage.success('CSV 文件导出成功')
}

// 下载文件
const downloadFile = (content, filename, type) => {
  const blob = new Blob([content], { type })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}

// 验证数据
const validateData = () => {
  const allStationsList = allStations.value
  const totalStations = allStationsList.length

  // 检查缺失坐标
  const missingCoords = allStationsList.filter(s =>
    !s.longitude || !s.latitude || s.longitude === 0 || s.latitude === 0
  ).length

  // 检查有效坐标
  const validCoords = totalStations - missingCoords

  // 检查重复站点（同一线路上的重复）
  const duplicates = 0
  lines.value.forEach(line => {
    const names = line.stations.map(s => s.name)
    const uniqueNames = [...new Set(names)]
    if (names.length !== uniqueNames.length) {
      duplicates += names.length - uniqueNames.length
    }
  })

  validationResult.value = {
    success: missingCoords === 0 && duplicates === 0,
    message: missingCoords === 0 && duplicates === 0
      ? '所有数据验证通过，数据质量良好'
      : `发现 ${missingCoords} 个站点缺失坐标，${duplicates} 个重复站点`,
    totalStations,
    validCoords,
    missingCoords,
    duplicates
  }

  validationDialogVisible.value = true

  addLog(`数据验证完成: ${validationResult.value.message}`,
    validationResult.value.success ? 'success' : 'warning')
}

// 显示线路详情
const showLineDetail = (line) => {
  selectedLine.value = line
  lineDialogVisible.value = true
}

// 高亮显示线路
const highlightLine = (line) => {
  if (!map.value || !line.stations) return

  // 清除之前的覆盖物
  map.value.clearMap()

  // 绘制高亮线路
  const path = line.stations.map(s => [s.longitude, s.latitude])
  const polyline = new window.AMap.Polyline({
    path: path,
    strokeColor: line.color,
    strokeWeight: 6,
    strokeOpacity: 1,
    showDir: true
  })

  map.value.add(polyline)

  // 添加站点标记
  line.stations.forEach(station => {
    const marker = new window.AMap.CircleMarker({
      center: [station.longitude, station.latitude],
      radius: station.isTransfer ? 10 : 7,
      fillColor: station.isTransfer ? '#ff0000' : line.color,
      strokeColor: '#fff',
      strokeWeight: 2,
      fillOpacity: 1
    })

    const infoWindow = new window.AMap.InfoWindow({
      content: `<div style="padding: 10px;">
        <h4>${station.name}</h4>
        <p>线路: ${line.name}</p>
        <p>序号: ${station.sequence}</p>
        ${station.isTransfer ? '<p style="color: red;">换乘站</p>' : ''}
      </div>`,
      offset: new window.AMap.Pixel(0, -10)
    })

    marker.on('click', () => {
      infoWindow.open(map.value, [station.longitude, station.latitude])
    })

    map.value.add(marker)
  })

  // 调整视野
  map.value.setFitView()

  // 切换到地图标签
  activeTab.value = 'map'

  addLog(`高亮显示线路: ${line.name}`, 'primary')
}

// 定位站点
const locateStation = (station) => {
  if (!map.value) return

  map.value.setCenter([station.longitude, station.latitude])
  map.value.setZoom(16)

  // 打开信息窗体
  const infoWindow = new window.AMap.InfoWindow({
    content: `<div style="padding: 10px;">
      <h4>${station.name}</h4>
      <p>线路: ${station.lineName}</p>
      <p>经度: ${station.longitude}</p>
      <p>纬度: ${station.latitude}</p>
    </div>`,
    offset: new window.AMap.Pixel(0, -10)
  })

  infoWindow.open(map.value, [station.longitude, station.latitude])

  activeTab.value = 'map'
}

// 组件卸载时清理
onUnmounted(() => {
  if (map.value) {
    map.value.destroy()
  }
})
</script>

<style scoped>
.metro-data-collector {
  padding: 20px;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.content-wrapper {
  display: flex;
  gap: 20px;
  margin-top: 20px;
  flex: 1;
  overflow: hidden;
}

.control-panel {
  width: 320px;
  flex-shrink: 0;
  overflow-y: auto;
}

.main-content {
  flex: 1;
  overflow: hidden;
}

.control-section {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.control-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.control-section h4 {
  margin: 0 0 10px 0;
  color: #606266;
  font-size: 14px;
}

.map-container {
  width: 100%;
  height: 600px;
  border-radius: 4px;
}

.log-container {
  height: 600px;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

:deep(.el-tabs__content) {
  height: calc(100% - 40px);
}

:deep(.el-tab-pane) {
  height: 100%;
}
</style>
