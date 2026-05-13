<template>
  <div class="data-management">
    <el-page-header />

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

    <!-- 新增表单 -->
    <div class="add-form">
      <h4>新增记录</h4>
      <el-form :inline="true" size="small" :model="addForm">
        <el-form-item label="站点名称">
          <el-input v-model="addForm.stationName" placeholder="请输入站点名称" />
        </el-form-item>
        <el-form-item label="年份">
          <el-input-number v-model="addForm.year" :min="2024" :max="2030" :step="1" />
        </el-form-item>
        <el-form-item label="月份">
          <el-select v-model="addForm.month" placeholder="请选择" style="width:100px">
            <el-option label="年均" :value="0" />
            <el-option v-for="m in 12" :key="m" :label="m + '月'" :value="m" />
          </el-select>
        </el-form-item>
        <el-form-item label="人口数量">
          <el-input-number v-model="addForm.population" :min="0" :max="100000" :step="100" />
        </el-form-item>
        <el-form-item label="拥挤度">
          <el-input-number v-model="addForm.crowdLevel" :min="0" :max="100" :step="1" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleAdd">添加</el-button>
        </el-form-item>
      </el-form>
    </div>

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
    </div>

    <el-table :data="tableData" border size="small">
      <el-table-column prop="stationName" label="站点名称" width="150" />
      <el-table-column prop="year" label="年份" width="80" />
      <el-table-column prop="month" label="月份" width="80">
        <template #default="{ row }">{{ row.month === 0 ? '年均' : row.month + '月' }}</template>
      </el-table-column>
      <el-table-column prop="population" label="人口数量" width="130">
        <template #default="{ row }">
          <span v-if="editCellId !== row.id">{{ row.population }}</span>
          <el-input v-else v-model="row.population" size="small" style="width:100px" />
        </template>
      </el-table-column>
      <el-table-column prop="crowdLevel" label="拥挤度" width="160">
        <template #default="{ row }">
          <el-progress v-if="editCellId !== row.id" :percentage="row.crowdLevel || 0" :stroke-width="16" :text-inside="true" />
          <el-slider v-else v-model="row.crowdLevel" :min="0" :max="100" style="padding:0 8px" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button v-if="editCellId === row.id" type="primary" size="small" @click="saveEdit(row)">保存</el-button>
          <el-button v-else size="small" @click="enterCellEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                   :total="total" layout="total, prev, pager, next" style="margin-top:16px;justify-content:flex-end" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
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

// 新增表单
const addForm = reactive({
  stationName: '',
  year: 2025,
  month: 1,
  population: 0,
  crowdLevel: 50
})

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

const handleAdd = () => {
  if (!addForm.stationName) {
    ElMessage.warning('请填写站点名称')
    return
  }
  importPopulation([{ ...addForm }]).then(res => {
    if (res.code === '200') {
      ElMessage.success('添加成功')
      addForm.stationName = ''
      addForm.population = 0
      addForm.crowdLevel = 50
      fetchData()
    } else {
      ElMessage.error(res.msg || '添加失败')
    }
  })
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

onMounted(() => { fetchData() })
</script>

<style scoped>
.data-management { padding: 20px; }
.overview-row { margin: 20px 0; }
.overview-card { background: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); text-align: center; }
.ov-value { font-size: 24px; font-weight: 700; color: #409EFF; }
.ov-label { font-size: 13px; color: #909399; margin-top: 4px; }
.add-form { background: #fff; border-radius: 8px; padding: 16px 20px; margin-bottom: 16px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
.add-form h4 { margin: 0 0 12px 0; font-size: 14px; color: #333; }
.toolbar { display: flex; justify-content: flex-end; align-items: center; margin-bottom: 16px; background: #fff; padding: 12px 16px; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
</style>
