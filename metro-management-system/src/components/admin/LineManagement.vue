<template>
  <div class="line-management">
    <!-- 左侧控制面板 -->
    <div class="left-panel">
      <div class="panel-header">
        <h3><el-icon><List /></el-icon> 线路列表</h3>
      </div>
      <div class="line-list">
        <el-checkbox-group v-model="checkedLines">
          <div v-for="line in lines" :key="line.lineId" class="line-item"
               :style="{ borderLeftColor: line.color }"
               :class="{ active: selectedLine?.lineId === line.lineId }"
               @click="selectLine(line)">
            <el-checkbox :value="line.lineId" @change="toggleLine(line)">
              <span class="line-name" :style="{ color: line.color }">
                <span class="line-dot" :style="{ background: line.color }"></span>
                {{ line.lineName }}
              </span>
            </el-checkbox>
          </div>
        </el-checkbox-group>
      </div>
      <div class="panel-actions">
        <el-button size="small" @click="checkAll">全选</el-button>
        <el-button size="small" @click="uncheckAll">反选</el-button>
      </div>
      <el-divider />
      <div class="panel-actions">
        <el-button type="primary" size="small" @click="showAddLineDialog">
          <el-icon><Plus /></el-icon> 新增线路
        </el-button>
      </div>
      <el-divider />
      <div class="edit-mode-toggle">
        <div class="toggle-label">
          <el-icon><Edit /></el-icon> 编辑模式
        </div>
        <el-switch v-model="isEditing" @change="onEditModeChange" />
      </div>
      <div class="panel-actions">
        <el-button type="success" size="small" :disabled="!hasChanges" @click="saveAll">
          <el-icon><Check /></el-icon> 保存全部{{ hasChanges ? `(${dirtyCount})` : '' }}
        </el-button>
      </div>
    </div>

    <!-- 中央地图区 -->
    <div class="map-container" ref="mapContainer">
      <div id="metroMap"></div>
      <!-- 修改提示 -->
      <div class="status-bar" v-if="hasChanges">
        <el-alert :title="`正在编辑${selectedLine?.lineName || ''} | 有${dirtyCount}项未保存的修改`"
                  type="warning" :closable="false" show-icon size="small" />
      </div>
    </div>

    <!-- 右侧属性编辑面板 -->
    <div class="right-panel">
      <div class="panel-header">
        <h3><el-icon><InfoFilled /></el-icon> 属性编辑</h3>
      </div>
      <template v-if="selectedLine && !selectedStation">
        <!-- 线路属性 -->
        <div class="property-section">
          <h4>线路属性</h4>
          <el-form label-width="70px" size="small">
            <el-form-item label="线路名称">
              <el-input v-model="editForm.lineName" :disabled="!isEditing" />
            </el-form-item>
            <el-form-item label="线路颜色">
              <el-color-picker v-model="editForm.color" :disabled="!isEditing" />
            </el-form-item>
            <el-form-item label="起点站">
              <el-input v-model="editForm.startStation" :disabled="!isEditing" />
            </el-form-item>
            <el-form-item label="终点站">
              <el-input v-model="editForm.endStation" :disabled="!isEditing" />
            </el-form-item>
            <el-form-item label="运营状态">
              <el-select v-model="editForm.status" :disabled="!isEditing">
                <el-option label="运营中" :value="1" />
                <el-option label="建设中" :value="0" />
              </el-select>
            </el-form-item>
            <el-button v-if="isEditing" type="primary" size="small" @click="saveLineProps">保存线路属性</el-button>
          </el-form>
        </div>
        <el-divider />
        <!-- 站点列表 -->
        <div class="property-section">
          <h4>站点列表</h4>
          <div class="station-list">
            <div v-for="(st, idx) in currentStations" :key="st.id"
                 class="station-item"
                 :class="{ active: selectedStation?.id === st.id }"
                 @click="selectStation(st)">
              <span class="station-idx">{{ idx + 1 }}</span>
              <span class="station-name">{{ st.stationName }}</span>
              <el-tag v-if="st.isTransfer === 1 || st.isTransfer === '1'" size="mini" type="warning">换乘</el-tag>
            </div>
          </div>
          <el-button v-if="isEditing" size="small" @click="showAddStationDialog" style="margin-top:8px;width:100%">
            <el-icon><Plus /></el-icon> 新增站点
          </el-button>
        </div>
      </template>

      <template v-if="selectedStation">
        <!-- 站点属性 -->
        <div class="property-section">
          <h4>站点属性
            <el-button size="small" text @click="selectedStation = null">返回线路</el-button>
          </h4>
          <el-form label-width="70px" size="small">
            <el-form-item label="站点名称">
              <el-input v-model="stationForm.stationName" :disabled="!isEditing" />
            </el-form-item>
            <el-form-item label="顺序号">
              <el-input-number v-model="stationForm.sequence" :min="1" :disabled="!isEditing" />
            </el-form-item>
            <el-form-item label="经度">
              <el-input v-model="stationForm.longitude" :disabled="!isEditing" />
            </el-form-item>
            <el-form-item label="纬度">
              <el-input v-model="stationForm.latitude" :disabled="!isEditing" />
            </el-form-item>
            <el-form-item label="换乘站">
              <el-switch v-model="stationForm.isTransferBool" :disabled="!isEditing" />
            </el-form-item>
          </el-form>
          <div class="station-actions" v-if="isEditing">
            <el-button type="primary" size="small" @click="saveStationProps">保存修改</el-button>
            <el-button type="danger" size="small" @click="confirmDeleteStation">删除站点</el-button>
          </div>
        </div>
      </template>

      <div class="panel-hint" v-if="!selectedLine && !selectedStation">
        <el-empty description="请从左侧选择一条线路" />
      </div>
    </div>

    <!-- 新增线路对话框 -->
    <el-dialog v-model="addLineDialogVisible" title="新增线路" width="450px">
      <el-form :model="newLineForm" label-width="80px" size="small">
        <el-form-item label="线路ID">
          <el-input v-model="newLineForm.lineId" placeholder="如: GZ01" />
        </el-form-item>
        <el-form-item label="线路名称">
          <el-input v-model="newLineForm.lineName" placeholder="如: 1号线" />
        </el-form-item>
        <el-form-item label="线路编号">
          <el-input v-model="newLineForm.lineCode" placeholder="如: GZ1" />
        </el-form-item>
        <el-form-item label="颜色">
          <el-color-picker v-model="newLineForm.color" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addLineDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNewLine">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增站点对话框 -->
    <el-dialog v-model="addStationDialogVisible" title="新增站点" width="450px">
      <el-form :model="newStationForm" label-width="80px" size="small">
        <el-form-item label="站点名称">
          <el-input v-model="newStationForm.stationName" placeholder="请输入站点名称" />
        </el-form-item>
        <el-form-item label="顺序号">
          <el-input-number v-model="newStationForm.sequence" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addStationDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNewStation">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { List, InfoFilled, Edit, Plus, Check } from '@element-plus/icons-vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import {
  getAllLines, getStationsByLineId, getTransferStations, getFullMetroData,
  addLine, updateLine, deleteLine, updateLinePath,
  addStation, updateStation, deleteStation, updateStationPosition,
  addTransfer, deleteTransfer
} from '../../api/metroApi.js'

const LINES_COLORS = ['#e74c3c', '#3498db', '#2ecc71', '#f39c12', '#9b59b6', '#1abc9c', '#e67e22', '#34495e']

// 地图相关
let map = null
let polylines = {}
let markers = {}
let transferMarkers = {}
let currentEditor = null

// 状态变量
const lines = ref([])
const checkedLines = ref([])
const selectedLine = ref(null)
const selectedStation = ref(null)
const isEditing = ref(false)
const hasChanges = ref(false)
const dirtyCount = computed(() => {
  let count = 0
  lines.value.forEach(l => { if (l._dirty) count++ })
  return count
})
const currentStations = ref([])
const mapContainer = ref(null)

// 编辑表单
const editForm = reactive({
  lineName: '', color: '#3498db', startStation: '', endStation: '', status: 1
})
const stationForm = reactive({
  stationName: '', sequence: 1, longitude: '', latitude: '', isTransferBool: false
})

// 新增对话框
const addLineDialogVisible = ref(false)
const addStationDialogVisible = ref(false)
const newLineForm = reactive({ lineId: '', lineName: '', lineCode: '', color: '#3498db' })
const newStationForm = reactive({ stationName: '', sequence: 1 })

// 广州地图中心
const MAP_CENTER = [113.2644, 23.1291]
const MAP_ZOOM = 11

// 初始化高德地图
const initMap = () => {
  window._AMapSecurityConfig = {
    securityJsCode: 'f2e281bddbaaed3bfc03b711e00122fa'
  }
  AMapLoader.load({
    key: '49f3c3fd7e6380f9c8b84c0f0bdd4d9f',
    version: '2.0',
    plugins: ['AMap.Scale', 'AMap.ToolBar']
  }).then(AMap => {
    map = new AMap.Map('metroMap', {
      zoom: MAP_ZOOM,
      center: MAP_CENTER,
      viewMode: '2D',
      mapStyle: 'amap://styles/light'
    })
    map.addControl(new AMap.Scale())
    map.addControl(new AMap.ToolBar())
    loadData()
  }).catch(e => console.error(e))
}

// 加载所有数据
const loadData = () => {
  getFullMetroData().then(res => {
    if (res.code === '200') {
      lines.value = res.data.lines || []
      lines.value.forEach(l => { l._dirty = false })
      // 默认全选
      checkedLines.value = lines.value.map(l => l.lineId)
      nextTick(() => renderAllLines())
    }
  })
}

// 渲染所有选中的线路
const renderAllLines = () => {
  clearMap()
  lines.value.forEach(line => {
    if (checkedLines.value.includes(line.lineId)) {
      renderLine(line)
    }
  })
}

// 渲染单条线路
const renderLine = (line) => {
  if (!map) return
  const AMap = window.AMap
  if (!AMap) return

  // 解析路径
  let path = []
  if (line.path) {
    try {
      path = typeof line.path === 'string' ? JSON.parse(line.path) : line.path
    } catch (e) {
      path = []
    }
  }

  // 绘制线路折线
  if (path.length > 0) {
    const polyline = new AMap.Polyline({
      path: path,
      strokeColor: line.color || '#3498db',
      strokeWeight: 4,
      strokeOpacity: 0.8,
      lineJoin: 'round',
      lineCap: 'round'
    })
    polyline.setExtData({ lineId: line.lineId })
    polyline.on('click', () => selectLine(line))
    map.add(polyline)
    polylines[line.lineId] = polyline
  }

  // 绘制站点标记
  if (line.stations && line.stations.length > 0) {
    line.stations.forEach(station => {
      const lng = parseFloat(station.longitude)
      const lat = parseFloat(station.latitude)
      if (isNaN(lng) || isNaN(lat)) return

      const marker = new AMap.Marker({
        position: [lng, lat],
        title: station.stationName,
        label: { content: station.stationName, direction: 'top', offset: [0, -5] },
        draggable: isEditing.value
      })
      marker.setExtData({ stationId: station.id, lineId: station.lineId })
      marker.on('click', () => selectStation(station))
      marker.on('dragend', (e) => {
        station.longitude = e.lnglat.getLng()
        station.latitude = e.lnglat.getLat()
        station._dirty = true
        hasChanges.value = true
        if (selectedStation.value?.id === station.id) {
          stationForm.longitude = station.longitude
          stationForm.latitude = station.latitude
        }
      })
      map.add(marker)
      if (!markers[line.lineId]) markers[line.lineId] = []
      markers[line.lineId].push(marker)
    })
  }
}

// 清除地图所有覆盖物
const clearMap = () => {
  if (!map) return
  Object.values(polylines).forEach(p => map.remove(p))
  polylines = {}
  Object.values(markers).forEach(arr => arr.forEach(m => map.remove(m)))
  markers = {}
  Object.values(transferMarkers).forEach(m => map.remove(m))
  transferMarkers = {}
}

// 切换线路显示
const toggleLine = (line) => {
  nextTick(() => renderAllLines())
}

const checkAll = () => {
  checkedLines.value = lines.value.map(l => l.lineId)
  renderAllLines()
}

const uncheckAll = () => {
  checkedLines.value = []
  renderAllLines()
}

// 选择线路
const selectLine = (line) => {
  selectedLine.value = line
  selectedStation.value = null
  editForm.lineName = line.lineName
  editForm.color = line.color
  editForm.startStation = line.startStation || ''
  editForm.endStation = line.endStation || ''
  editForm.status = line.status

  // 加载站点
  if (line.stations) {
    currentStations.value = line.stations
  } else {
    getStationsByLineId(line.lineId).then(res => {
      if (res.code === '200') {
        currentStations.value = res.data
        line.stations = res.data
      }
    })
  }

  // 地图聚焦
  if (map && polylines[line.lineId]) {
    map.setFitView([polylines[line.lineId]], false, [50, 50, 50, 50])
  }
}

// 选择站点
const selectStation = (station) => {
  selectedStation.value = station
  stationForm.stationName = station.stationName
  stationForm.sequence = station.sequence
  stationForm.longitude = station.longitude
  stationForm.latitude = station.latitude
  stationForm.isTransferBool = station.isTransfer === 1 || station.isTransfer === '1'

  if (map) {
    const lng = parseFloat(station.longitude)
    const lat = parseFloat(station.latitude)
    if (!isNaN(lng) && !isNaN(lat)) {
      map.setCenter([lng, lat])
      map.setZoom(15)
    }
  }
}

// 编辑模式切换
const onEditModeChange = (val) => {
  // 切换所有 marker 的可拖拽状态
  Object.values(markers).forEach(arr => {
    arr.forEach(m => m.setDraggable(val))
  })
}

// 保存线路属性
const saveLineProps = () => {
  if (!selectedLine.value) return
  const line = selectedLine.value
  const data = {
    lineName: editForm.lineName,
    color: editForm.color,
    startStation: editForm.startStation,
    endStation: editForm.endStation,
    status: editForm.status
  }
  updateLine(line.id, data).then(res => {
    if (res.code === '200') {
      line.lineName = editForm.lineName
      line.color = editForm.color
      line.startStation = editForm.startStation
      line.endStation = editForm.endStation
      line.status = editForm.status
      // 更新折线颜色
      if (polylines[line.lineId]) {
        polylines[line.lineId].setOptions({ strokeColor: line.color })
      }
      ElMessage.success('线路属性已保存')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  })
}

// 保存站点属性
const saveStationProps = () => {
  if (!selectedStation.value) return
  const st = selectedStation.value
  const data = {
    stationName: stationForm.stationName,
    sequence: stationForm.sequence,
    longitude: parseFloat(stationForm.longitude) || 0,
    latitude: parseFloat(stationForm.latitude) || 0,
    isTransfer: stationForm.isTransferBool ? 1 : 0
  }
  updateStation(st.id, data).then(res => {
    if (res.code === '200') {
      st.stationName = data.stationName
      st.sequence = data.sequence
      st.longitude = data.longitude
      st.latitude = data.latitude
      st.isTransfer = data.isTransfer
      // 更新标记
      const lineMarkers = markers[st.lineId] || []
      const m = lineMarkers.find(m => m.getExtData().stationId === st.id)
      if (m) {
        m.setPosition([data.longitude, data.latitude])
        m.setLabel({ content: data.stationName, direction: 'top', offset: [0, -5] })
      }
      ElMessage.success('站点属性已保存')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  })
}

// 保存全部（批量提交脏数据）
const saveAll = () => {
  const promises = []
  lines.value.forEach(line => {
    if (line._dirty) {
      const pathStr = typeof line.path === 'string' ? line.path : JSON.stringify(line.path)
      promises.push(updateLinePath(line.id, pathStr))
      line._dirty = false
    }
    if (line.stations) {
      line.stations.forEach(st => {
        if (st._dirty) {
          promises.push(updateStationPosition(st.id, st.longitude, st.latitude))
          st._dirty = false
        }
      })
    }
  })
  if (promises.length === 0) {
    ElMessage.info('没有需要保存的修改')
    return
  }
  Promise.all(promises).then(() => {
    hasChanges.value = false
    ElMessage.success('全部保存成功')
  }).catch(() => {
    ElMessage.error('部分保存失败')
  })
}

// 新增线路
const showAddLineDialog = () => {
  newLineForm.lineId = ''
  newLineForm.lineName = ''
  newLineForm.lineCode = ''
  newLineForm.color = LINES_COLORS[lines.value.length % LINES_COLORS.length]
  addLineDialogVisible.value = true
}

const submitNewLine = () => {
  if (!newLineForm.lineId || !newLineForm.lineName) {
    ElMessage.warning('请填写线路ID和名称')
    return
  }
  addLine(newLineForm).then(res => {
    if (res.code === '200') {
      ElMessage.success('新增成功')
      addLineDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '新增失败')
    }
  })
}

// 新增站点
const showAddStationDialog = () => {
  if (!selectedLine.value) {
    ElMessage.warning('请先选择一条线路')
    return
  }
  newStationForm.sequence = currentStations.value.length + 1
  newStationForm.stationName = ''
  addStationDialogVisible.value = true
}

const submitNewStation = () => {
  if (!newStationForm.stationName) {
    ElMessage.warning('请输入站点名称')
    return
  }
  const data = {
    stationName: newStationForm.stationName,
    lineId: selectedLine.value.lineId,
    sequence: newStationForm.sequence,
    longitude: 0,
    latitude: 0,
    isTransfer: 0
  }
  addStation(data).then(res => {
    if (res.code === '200') {
      ElMessage.success('新增成功')
      addStationDialogVisible.value = false
      // 重新加载数据
      getStationsByLineId(selectedLine.value.lineId).then(r => {
        if (r.code === '200') {
          selectedLine.value.stations = r.data
          currentStations.value = r.data
        }
      })
    } else {
      ElMessage.error(res.msg || '新增失败')
    }
  })
}

// 删除站点
const confirmDeleteStation = () => {
  if (!selectedStation.value) return
  ElMessageBox.confirm(`确定要删除站点"${selectedStation.value.stationName}"吗?`, '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(() => {
    deleteStation(selectedStation.value.id).then(res => {
      if (res.code === '200') {
        ElMessage.success('删除成功')
        selectedStation.value = null
        getStationsByLineId(selectedLine.value.lineId).then(r => {
          if (r.code === '200') {
            selectedLine.value.stations = r.data
            currentStations.value = r.data
          }
        })
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    })
  }).catch(() => {})
}

onMounted(() => {
  initMap()
})
</script>

<style scoped>
.line-management {
  display: flex;
  height: calc(100vh - 140px);
  position: relative;
}

.left-panel {
  width: 220px;
  min-width: 220px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  padding: 16px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.panel-header h3 {
  margin: 0 0 12px 0;
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.line-item {
  padding: 6px 8px;
  border-left: 3px solid transparent;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 2px;
  transition: all 0.2s;
}
.line-item:hover { background: #f5f7fa; }
.line-item.active { background: #ecf5ff; }
.line-name { font-size: 13px; font-weight: 500; display: flex; align-items: center; gap: 6px; }
.line-dot { width: 10px; height: 10px; border-radius: 50%; display: inline-block; }

.panel-actions { display: flex; gap: 8px; flex-wrap: wrap; }

.edit-mode-toggle {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
}
.toggle-label { display: flex; align-items: center; gap: 6px; font-size: 14px; }

.map-container {
  flex: 1;
  margin: 0 8px;
  position: relative;
  border-radius: 8px;
  overflow: hidden;
}
#metroMap { width: 100%; height: 100%; }

.status-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 10;
}

.right-panel {
  width: 300px;
  min-width: 300px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  padding: 16px;
  overflow-y: auto;
}

.property-section h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.station-list {
  max-height: 300px;
  overflow-y: auto;
}
.station-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  cursor: pointer;
  border-radius: 4px;
  font-size: 13px;
}
.station-item:hover { background: #f5f7fa; }
.station-item.active { background: #ecf5ff; }
.station-idx { width: 20px; height: 20px; border-radius: 50%; background: #e8e8e8; display: flex; align-items: center; justify-content: center; font-size: 11px; color: #666; }
.station-name { flex: 1; }

.station-actions { display: flex; gap: 8px; margin-top: 12px; }

.panel-hint { display: flex; align-items: center; justify-content: center; height: 200px; }
</style>
