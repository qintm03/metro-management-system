<template>
  <div class="line-management">
    <!-- 主内容区 -->
    <main class="dashboard-main">
      <!-- 左侧边栏 -->
      <aside class="sidebar-left">
        <!-- 线路列表面板 -->
        <div class="data-panel">
          <div class="panel-header">
            <div class="panel-icon">
              <el-icon><List /></el-icon>
            </div>
            <h3>线路列表</h3>
          </div>
          <div class="panel-content">
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
            <div class="panel-actions">
              <el-button type="primary" size="small" @click="showAddLineDialog">
                <el-icon><Plus /></el-icon> 新增线路
              </el-button>
            </div>
          </div>
        </div>

        <!-- 编辑模式面板 -->
        <div class="data-panel">
          <div class="panel-header">
            <div class="panel-icon">
              <el-icon><Edit /></el-icon>
            </div>
            <h3>编辑模式</h3>
          </div>
          <div class="panel-content">
            <div class="edit-mode-buttons">
              <el-button
                :type="editingMode === 'line' ? 'primary' : 'default'"
                size="small"
                @click="setEditingMode('line')">
                <el-icon><Edit /></el-icon> 编辑线路
              </el-button>
              <el-button
                :type="editingMode === 'station' ? 'primary' : 'default'"
                size="small"
                @click="setEditingMode('station')">
                <el-icon><Edit /></el-icon> 编辑站点
              </el-button>
            </div>
            <el-button type="success" size="small" :disabled="!hasChanges" @click="saveAll">
              <el-icon><Check /></el-icon> 保存全部{{ hasChanges ? `(${dirtyCount})` : '' }}
            </el-button>
          </div>
        </div>
      </aside>

      <!-- 中央地图区域 -->
      <section class="map-section">
        <div id="metroMap" class="amap-container"></div>
        <!-- 修改提示 -->
        <div class="status-bar" v-if="hasChanges">
          <el-alert :title="`正在编辑${selectedLine?.lineName || ''} | 有${dirtyCount}项未保存的修改`"
                    type="warning" :closable="false" show-icon size="small" />
        </div>
      </section>

      <!-- 右侧边栏：属性编辑 -->
      <aside class="sidebar-right">
        <!-- 线路属性 -->
        <div class="data-panel" v-if="selectedLine && editingMode !== 'station' && (!selectedStation || editingMode === 'line')">
          <div class="panel-header">
            <div class="panel-icon">
              <el-icon><InfoFilled /></el-icon>
            </div>
            <h3>线路属性</h3>
          </div>
          <div class="panel-content">
            <el-form label-width="70px" size="small" class="property-form">
              <el-form-item label="线路名称">
                <el-input v-model="editForm.lineName" :disabled="editingMode !== 'line'" />
              </el-form-item>
              <el-form-item label="线路颜色">
                <el-color-picker v-model="editForm.color" :disabled="editingMode !== 'line'" />
              </el-form-item>
              <el-form-item label="起点站">
                <el-input v-model="editForm.startStation" :disabled="editingMode !== 'line'" />
              </el-form-item>
              <el-form-item label="终点站">
                <el-input v-model="editForm.endStation" :disabled="editingMode !== 'line'" />
              </el-form-item>
              <el-form-item label="运营状态">
                <el-select v-model="editForm.status" :disabled="editingMode !== 'line'">
                  <el-option label="运营中" :value="1" />
                  <el-option label="建设中" :value="0" />
                </el-select>
              </el-form-item>
              <el-button v-if="editingMode === 'line'" type="primary" size="small" class="save-btn" @click="saveLineProps">保存线路属性</el-button>
            </el-form>
          </div>
        </div>

        <!-- 站点列表 -->
        <div class="data-panel" v-if="selectedLine">
          <div class="panel-header">
            <div class="panel-icon">
              <el-icon><List /></el-icon>
            </div>
            <h3>站点列表</h3>
          </div>
          <div class="panel-content">
            <div class="station-items">
              <div v-for="(st, idx) in currentStations" :key="st.id"
                   class="station-item"
                   :class="{ active: selectedStation?.id === st.id }"
                   @click="selectStation(st)">
                <span class="station-idx">{{ idx + 1 }}</span>
                <span class="station-name">{{ st.stationName }}</span>
                <el-tag v-if="st.isTransfer === 1 || st.isTransfer === '1'" size="small" type="warning">换乘</el-tag>
              </div>
            </div>
            <el-button v-if="editingMode === 'station'" size="small" @click="showAddStationDialog" class="add-station-btn">
              <el-icon><Plus /></el-icon> 新增站点
            </el-button>
          </div>
        </div>

        <!-- 站点属性 -->
        <div class="data-panel" v-if="selectedStation">
          <div class="panel-header">
            <div class="panel-icon">
              <el-icon><InfoFilled /></el-icon>
            </div>
            <h3>站点属性</h3>
          </div>
          <div class="panel-content">
            <el-form label-width="70px" size="small" class="property-form">
              <el-form-item label="站点名称">
                <el-input v-model="stationForm.stationName" :disabled="editingMode !== 'station'" />
              </el-form-item>
              <el-form-item label="顺序号">
                <el-input-number v-model="stationForm.sequence" :min="1" :disabled="editingMode !== 'station'" />
              </el-form-item>
              <el-form-item label="经度">
                <el-input v-model="stationForm.longitude" :disabled="editingMode !== 'station'" />
              </el-form-item>
              <el-form-item label="纬度">
                <el-input v-model="stationForm.latitude" :disabled="editingMode !== 'station'" />
              </el-form-item>
              <el-form-item label="换乘站">
                <el-switch v-model="stationForm.isTransferBool" :disabled="editingMode !== 'station'" />
              </el-form-item>
            </el-form>
            <div class="station-actions" v-if="editingMode === 'station'">
              <el-button type="primary" size="small" @click="saveStationProps">保存修改</el-button>
              <el-button type="danger" size="small" @click="confirmDeleteStation">删除站点</el-button>
            </div>
            <el-button size="small" text @click="selectedStation = null" class="back-btn">返回线路</el-button>
          </div>
        </div>

        <!-- 空状态 -->
        <div class="data-panel empty-panel" v-if="!selectedLine && !selectedStation">
          <div class="panel-content">
            <el-empty description="请从左侧选择一条线路" />
          </div>
        </div>
      </aside>
    </main>

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
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
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

// ========== 地图相关 ==========
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
const editingMode = ref('')
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
    plugins: ['AMap.Scale', 'AMap.ToolBar', 'AMap.PolylineEditor']
  }).then(AMap => {
    map = new AMap.Map('metroMap', {
      zoom: MAP_ZOOM,
      center: MAP_CENTER,
      viewMode: '2D'
    })
    map.addControl(new AMap.Scale())
    map.addControl(new AMap.ToolBar())
    loadData()
    // 确保容器尺寸正确后重置地图大小，修复坐标偏移
    setTimeout(() => { try { map?.resize?.() } catch(e) {} }, 300)
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
    polyline.on('click', () => {
      if (editingMode.value === 'line') {
        selectLine(line)
        startLineEdit(line)
      } else {
        selectLine(line)
      }
    })
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
        draggable: editingMode.value === 'station'
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
  closeEditor()
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
const setEditingMode = (mode) => {
  if (editingMode.value === mode) {
    editingMode.value = ''
    closeEditor()
    Object.values(markers).forEach(arr => arr.forEach(m => m.setDraggable(false)))
    return
  }
  editingMode.value = mode
  closeEditor()
  Object.values(markers).forEach(arr => arr.forEach(m => m.setDraggable(false)))
  if (mode === 'line') {
    const visibleLines = lines.value.filter(l => checkedLines.value.includes(l.lineId))
    if (visibleLines.length > 0) {
      const target = selectedLine.value && checkedLines.value.includes(selectedLine.value.lineId)
        ? selectedLine.value : visibleLines[0]
      selectLine(target)
      nextTick(() => startLineEdit(target))
    }
  } else if (mode === 'station') {
    Object.values(markers).forEach(arr => arr.forEach(m => m.setDraggable(true)))
  }
}

// 折线路径编辑（PolylineEditor）
const startLineEdit = (line) => {
  if (!map || !window.AMap) return
  closeEditor()

  const polyline = polylines[line.lineId]
  if (!polyline) {
    ElMessage.warning('该线路没有路径数据')
    return
  }

  const AMap = window.AMap

  if (!AMap.PolylineEditor) {
    AMap.plugin(['AMap.PolylineEditor'], () => createEditor(polyline, line))
  } else {
    createEditor(polyline, line)
  }
}

const createEditor = (polyline, line) => {
  if (!map || !window.AMap) return
  currentEditor = new window.AMap.PolylineEditor(map, polyline)
  currentEditor.on('adjust', () => onPathChanged(line, polyline))
  currentEditor.on('addnode', () => onPathChanged(line, polyline))
  currentEditor.on('removenode', () => onPathChanged(line, polyline))
  currentEditor.open()
}

const onPathChanged = (line, polyline) => {
  const path = polyline.getPath()
  if (!path || path.length === 0) return
  line.path = path.map(p => [p.getLng(), p.getLat()])
  line._dirty = true
  hasChanges.value = true
}

const closeEditor = () => {
  if (currentEditor) {
    currentEditor.close()
    currentEditor = null
  }
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

onUnmounted(() => {
  closeEditor()
})
</script>

<style scoped>
/* 页面容器 */
.line-management {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 主内容区 */
.dashboard-main {
  flex: 1;
  display: grid;
  grid-template-columns: 260px 1fr 320px;
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
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  overflow: hidden;
}

.amap-container {
  width: 100%;
  height: 100%;
}

/* 修改提示 */
.status-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 10;
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
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  overflow: hidden;
  flex-shrink: 0;
}

.data-panel:last-child {
  flex-shrink: 1;
  overflow-y: auto;
}

.empty-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 15px;
  border-bottom: 1px solid #ebeef5;
}

.panel-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ecf5ff;
  border-radius: 8px;
  color: #409eff;
}

.panel-header h3 {
  margin: 0;
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.panel-content {
  padding: 12px 15px;
}

/* 线路列表 */
.line-list {
  max-height: 250px;
  overflow-y: auto;
  margin-bottom: 12px;
}

.line-item {
  padding: 6px 8px;
  border-left: 3px solid transparent;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 2px;
  transition: all 0.2s;
}

.line-item:hover {
  background: #f5f7fa;
}

.line-item.active {
  background: #ecf5ff;
}

.line-name {
  font-size: 13px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

.line-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
}

/* 面板操作按钮 */
.panel-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.panel-actions .el-button {
  flex: 1;
  min-width: 80px;
}

/* 编辑模式按钮组 */
.edit-mode-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.edit-mode-buttons .el-button {
  width: 100%;
}

/* 属性表单 */
.property-form {
  margin-bottom: 0;
}

.property-form .el-form-item {
  margin-bottom: 12px;
}

.save-btn {
  width: 100%;
  margin-top: 8px;
}

/* 站点列表 */
.station-items {
  max-height: 200px;
  overflow-y: auto;
  margin-bottom: 12px;
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

.station-item:hover {
  background: #f5f7fa;
}

.station-item.active {
  background: #ecf5ff;
}

.station-idx {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  color: #666;
  flex-shrink: 0;
}

.station-name {
  flex: 1;
}

.add-station-btn {
  width: 100%;
}

/* 站点操作 */
.station-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.station-actions .el-button {
  flex: 1;
}

.back-btn {
  width: 100%;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .dashboard-main {
    grid-template-columns: 240px 1fr 300px;
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
