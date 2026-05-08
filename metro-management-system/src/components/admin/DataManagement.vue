<template>
  <div class="data-management">
    <el-page-header title="数据管理" />

    <el-row :gutter="16" class="overview-row">
      <el-col :span="6">
        <div class="overview-card">
          <div class="ov-value">{{ totalRecords }}</div>
          <div class="ov-label">总记录数</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="overview-card">
          <div class="ov-value">{{ yearRange }}</div>
          <div class="ov-label">年份范围</div>
        </div>
      </el-col>
    </el-row>

    <div class="toolbar">
      <el-form :inline="true" size="small">
        <el-form-item label="年份">
          <el-select v-model="filterYear" placeholder="全部" clearable style="width:100px">
            <el-option v-for="y in availableYears" :key="y" :label="y" :value="y" />
          </el-select>
        </el-form-item>
        <el-form-item label="月份">
          <el-select v-model="filterMonth" placeholder="全部" clearable style="width:100px">
            <el-option v-for="m in 12" :key="m" :label="m + '月'" :value="m" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="toolbar-actions">
        <el-button type="success" size="small" @click="showImportDialog">
          <el-icon><Upload /></el-icon> 导入
        </el-button>
        <el-button size="small" @click="exportData">
          <el-icon><Download /></el-icon> 导出
        </el-button>
      </div>
    </div>

    <el-table :data="tableData" border size="small" @cell-dblclick="enterCellEdit">
      <el-table-column prop="stationName" label="站点名称" width="150" />
      <el-table-column prop="year" label="年份" width="80" />
      <el-table-column prop="month" label="月份" width="80">
        <template #default="{ row }">{{ row.month === 0 ? '年均' : row.month + '月' }}</template>
      </el-table-column>
      <el-table-column prop="population" label="人口数量" width="120">
        <template #default="{ row }">
          <span v-if="editCellId !== row.id">{{ row.population }}</span>
          <el-input v-else v-model="row.population" size="small" style="width:100px" />
        </template>
      </el-table-column>
      <el-table-column prop="crowdLevel" label="拥挤度" width="100">
        <template #default="{ row }">
          <el-progress :percentage="row.crowdLevel || 0" :stroke-width="16" :text-inside="true" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button v-if="editCellId === row.id" type="primary" size="small" @click="saveEdit(row)">保存</el-button>
          <el-button v-else size="small" @click="editCellId = row.id">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                   :total="total" layout="total, prev, pager, next" style="margin-top:16px;justify-content:flex-end" />

    <!-- 导入对话框 -->
    <el-dialog v-model="importDialogVisible" title="导入数据" width="500px">
      <el-upload drag accept=".csv,.json" :auto-upload="false" :on-change="handleFileChange">
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">拖拽文件到此处或 <em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">支持 CSV 或 JSON 格式</div>
        </template>
      </el-upload>
      <el-button size="small" @click="downloadTemplate">下载模板</el-button>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doImport">导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload, Download, UploadFilled } from '@element-plus/icons-vue'
import { getPopulationData, updatePopulation, deletePopulation, importPopulation } from '../../api/metroApi.js'

const tableData = ref([])
const totalRecords = ref(0)
const yearRange = ref('—')
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const filterYear = ref(null)
const filterMonth = ref(null)
const availableYears = ref([])
const editCellId = ref(null)
const importDialogVisible = ref(false)
const importFile = ref(null)

const fetchData = () => {
  const params = {}
  if (filterYear.value) params.year = filterYear.value
  if (filterMonth.value) params.month = filterMonth.value
  getPopulationData(params).then(res => {
    if (res.code === '200') {
      let data = res.data || []
      total.value = data.length
      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      tableData.value = data.slice(start, end)
    }
  })
  getPopulationData({}).then(res => {
    if (res.code === '200') {
      const all = res.data || []
      totalRecords.value = all.length
      const years = [...new Set(all.map(d => d.year))].sort()
      availableYears.value = years
      if (years.length > 0) {
        yearRange.value = `${Math.min(...years)}-${Math.max(...years)}`
      }
    }
  })
}

const resetFilter = () => {
  filterYear.value = null
  filterMonth.value = null
  currentPage.value = 1
  fetchData()
}

const enterCellEdit = (row) => {
  editCellId.value = row.id
}

const saveEdit = (row) => {
  updatePopulation(row.id, {
    population: row.population,
    crowdLevel: row.crowdLevel
  }).then(res => {
    if (res.code === '200') {
      ElMessage.success('保存成功')
    } else {
      ElMessage.warning('保存失败，请重试')
    }
    editCellId.value = null
    fetchData()
  })
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该记录吗?', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(() => {
    deletePopulation(id).then(res => {
      if (res.code === '200') {
        ElMessage.success('删除成功')
        fetchData()
      }
    })
  }).catch(() => {})
}

const showImportDialog = () => {
  importFile.value = null
  importDialogVisible.value = true
}

const handleFileChange = (file) => {
  importFile.value = file.raw
}

const doImport = () => {
  if (!importFile.value) {
    ElMessage.warning('请先选择文件')
    return
  }
  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      let data
      if (importFile.value.name.endsWith('.json')) {
        data = JSON.parse(e.target.result)
      } else {
        // CSV parse
        const lines = e.target.result.split('\n')
        const headers = lines[0].split(',')
        data = []
        for (let i = 1; i < lines.length; i++) {
          if (!lines[i].trim()) continue
          const vals = lines[i].split(',')
          const row = {}
          headers.forEach((h, idx) => { row[h.trim()] = vals[idx]?.trim() })
          data.push(row)
        }
      }
      importPopulation(data).then(res => {
        ElMessage.success(`成功导入 ${data.length} 条记录`)
        importDialogVisible.value = false
        fetchData()
      })
    } catch(e) {
      ElMessage.error('文件解析失败')
    }
  }
  reader.readAsText(importFile.value)
}

const downloadTemplate = () => {
  const csv = 'station_name,year,month,population,crowd_level\n公园前,2025,6,58000,92\n体育西路,2025,6,62000,98'
  const blob = new Blob(['﻿' + csv], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url; a.download = '人口数据导入模板.csv'; a.click()
  URL.revokeObjectURL(url)
}

const exportData = () => {
  const headers = '站点名称,年份,月份,人口数量,拥挤度\n'
  const rows = tableData.value.map(r =>
    `${r.stationName},${r.year},${r.month},${r.population},${r.crowdLevel}`
  ).join('\n')
  const blob = new Blob(['﻿' + headers + rows], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url; a.download = `人口数据_${Date.now()}.csv`; a.click()
  URL.revokeObjectURL(url)
}

onMounted(() => { fetchData() })
</script>

<style scoped>
.data-management { padding: 20px; }
.overview-row { margin: 20px 0; }
.overview-card { background: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); text-align: center; }
.ov-value { font-size: 24px; font-weight: 700; color: #409EFF; }
.ov-label { font-size: 13px; color: #909399; margin-top: 4px; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; background: #fff; padding: 12px 16px; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
.toolbar-actions { display: flex; gap: 8px; }
</style>
