<template>
  <div class="schedule-config">
    <el-page-header title="时刻表配置" />
    <div class="config-content">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="config-sidebar">
            <h4>线路选择</h4>
            <el-select v-model="selectedLineId" placeholder="请选择线路" style="width:100%" @change="onLineChange">
              <el-option v-for="line in lines" :key="line.lineId"
                         :label="line.lineName" :value="line.lineId">
                <span class="line-option">
                  <span class="line-dot" :style="{ background: line.color }"></span>
                  {{ line.lineName }}
                </span>
              </el-option>
            </el-select>
            <div class="current-config" v-if="currentConfig">
              <h4>当前配置</h4>
              <p>首班: {{ currentConfig.firstTrainTime }}</p>
              <p>末班: {{ currentConfig.lastTrainTime }}</p>
              <p>间隔: {{ currentConfig.interval }}秒</p>
              <p>列车: {{ currentConfig.trainCount || 5 }}辆</p>
            </div>
          </div>
        </el-col>
        <el-col :span="18">
          <div class="config-main">
            <h4>运行参数配置</h4>
            <el-form :model="configForm" label-width="100px" size="small" inline>
              <el-form-item label="首班车时间">
                <el-time-select v-model="configForm.firstTrainTime" :start="'05:00'" :step="'00:05'" :end="'08:00'" />
              </el-form-item>
              <el-form-item label="末班车时间">
                <el-time-select v-model="configForm.lastTrainTime" :start="'20:00'" :step="'00:05'" :end="'23:59'" />
              </el-form-item>
              <el-form-item label="发车间隔(秒)">
                <el-input-number v-model="configForm.interval" :min="60" :max="1800" :step="30" />
              </el-form-item>
              <el-form-item label="站间运行(秒)">
                <el-input-number v-model="configForm.segmentTime" :min="60" :max="600" :step="10" />
              </el-form-item>
              <el-form-item label="停站时间(秒)">
                <el-input-number v-model="configForm.dwellTime" :min="10" :max="120" :step="5" />
              </el-form-item>
            </el-form>

            <h4>预设模板</h4>
            <div class="presets">
              <el-tag v-for="preset in presets" :key="preset.name"
                      class="preset-tag" @click="applyPreset(preset)">
                {{ preset.name }}
              </el-tag>
            </div>

            <h4>时刻表预览</h4>
            <el-table :data="schedulePreview" border size="small" max-height="300">
              <el-table-column prop="trainId" label="列车ID" width="80" />
              <el-table-column prop="departureTime" label="发车时间" width="100" />
              <el-table-column prop="arrivalTime" label="到达终点" width="100" />
              <el-table-column prop="totalTime" label="总耗时" width="90" />
            </el-table>

            <div class="config-actions">
              <el-button type="primary" @click="saveConfig">
                <el-icon><Check /></el-icon> 保存配置
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Check } from '@element-plus/icons-vue'
import { getAllLines, updateSchedule } from '../../api/metroApi.js'

const lines = ref([])
const selectedLineId = ref('')
const currentConfig = ref(null)

const configForm = reactive({
  firstTrainTime: '06:00',
  lastTrainTime: '23:00',
  interval: 300,
  segmentTime: 180,
  dwellTime: 30
})

const presets = [
  { name: '高峰期', interval: 180, segmentTime: 120, dwellTime: 20, trainCount: 10 },
  { name: '平峰期', interval: 300, segmentTime: 180, dwellTime: 30, trainCount: 6 },
  { name: '夜间模式', interval: 600, segmentTime: 240, dwellTime: 40, trainCount: 4 }
]

const schedulePreview = ref([])

const loadLines = () => {
  getAllLines().then(res => {
    if (res.code === '200') lines.value = res.data
  })
}

const onLineChange = (lineId) => {
  const line = lines.value.find(l => l.lineId === lineId)
  if (line) {
    configForm.firstTrainTime = line.firstTrainTime || '06:00'
    configForm.lastTrainTime = line.lastTrainTime || '23:00'
    configForm.interval = line.interval || 300
    configForm.segmentTime = line.segmentTime || 180
    configForm.dwellTime = line.dwellTime || 30
    currentConfig.value = {
      firstTrainTime: configForm.firstTrainTime,
      lastTrainTime: configForm.lastTrainTime,
      interval: configForm.interval,
      trainCount: 5
    }
    generatePreview()
  }
}

const applyPreset = (preset) => {
  configForm.interval = preset.interval
  configForm.segmentTime = preset.segmentTime
  configForm.dwellTime = preset.dwellTime
  generatePreview()
}

const generatePreview = () => {
  const totalStationTime = (configForm.segmentTime + configForm.dwellTime) * 10 // 假设10站
  const totalMinutes = Math.floor(totalStationTime / 60)
  const totalSeconds = totalStationTime % 60
  const list = []
  // 首班车秒数
  const [fh, fm] = configForm.firstTrainTime.split(':').map(Number)
  const startSeconds = fh * 3600 + fm * 60
  const [lh, lm] = configForm.lastTrainTime.split(':').map(Number)
  const endSeconds = lh * 3600 + lm * 60
  const totalTrains = Math.floor((endSeconds - startSeconds) / configForm.interval) + 1
  const count = Math.min(totalTrains, 20)

  for (let i = 0; i < count; i++) {
    const depSeconds = startSeconds + i * configForm.interval
    const arrSeconds = depSeconds + totalStationTime
    const dh = Math.floor(depSeconds / 3600)
    const dm = Math.floor((depSeconds % 3600) / 60)
    const ah = Math.floor(arrSeconds / 3600)
    const am = Math.floor((arrSeconds % 3600) / 60)
    list.push({
      trainId: `T${String(i + 1).padStart(3, '0')}`,
      departureTime: `${String(dh).padStart(2, '0')}:${String(dm).padStart(2, '0')}`,
      arrivalTime: `${String(ah).padStart(2, '0')}:${String(am).padStart(2, '0')}`,
      totalTime: `${totalMinutes}′${totalSeconds}″`
    })
  }
  schedulePreview.value = list
}

const saveConfig = () => {
  if (!selectedLineId.value) {
    ElMessage.warning('请先选择一条线路')
    return
  }
  const data = {
    firstTrainTime: configForm.firstTrainTime,
    lastTrainTime: configForm.lastTrainTime,
    interval: configForm.interval,
    segmentTime: configForm.segmentTime,
    dwellTime: configForm.dwellTime
  }
  updateSchedule(selectedLineId.value, data).then(res => {
    if (res.code === '200') {
      ElMessage.success('配置已保存')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  })
}

onMounted(() => {
  loadLines()
})
</script>

<style scoped>
.schedule-config { padding: 20px; }
.config-content { margin-top: 20px; }
.config-sidebar { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
.config-sidebar h4 { margin: 16px 0 8px 0; font-size: 14px; }
.config-sidebar p { margin: 4px 0; font-size: 13px; color: #666; }
.config-main { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
.config-main h4 { margin: 16px 0 12px 0; font-size: 14px; }
.config-main h4:first-child { margin-top: 0; }
.presets { display: flex; gap: 8px; margin-bottom: 16px; }
.preset-tag { cursor: pointer; }
.preset-tag:hover { opacity: 0.8; }
.config-actions { margin-top: 16px; display: flex; justify-content: flex-end; }
.line-option { display: flex; align-items: center; gap: 6px; }
.line-dot { width: 10px; height: 10px; border-radius: 50%; display: inline-block; }
</style>
