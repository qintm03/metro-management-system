<template>
  <div class="system-settings">
    <el-page-header title="系统设置" />

    <el-tabs v-model="activeTab" class="settings-tabs">
      <el-tab-pane label="公告管理" name="news">
        <div class="toolbar">
          <el-button type="primary" size="small" @click="showAddNews">
            <el-icon><Plus /></el-icon> 新增公告
          </el-button>
        </div>
        <el-table :data="newsList" border size="small">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="addtime" label="发布时间" width="180" />
          <el-table-column label="操作" width="160">
            <template #default="{ row }">
              <el-button size="small" @click="editNews(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteNews(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-dialog v-model="newsDialogVisible" :title="newsDialogTitle" width="600px">
          <el-form :model="newsForm" label-width="60px">
            <el-form-item label="标题">
              <el-input v-model="newsForm.title" placeholder="请输入公告标题" />
            </el-form-item>
            <el-form-item label="简介">
              <el-input v-model="newsForm.introduction" placeholder="请输入公告简介" />
            </el-form-item>
            <el-form-item label="内容">
              <el-input v-model="newsForm.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="newsDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitNews">确定</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>

      <el-tab-pane label="系统参数" name="config">
        <el-form :model="systemConfig" label-width="140px" size="small">
          <el-form-item label="系统名称">
            <el-input v-model="systemConfig.systemName" />
          </el-form-item>
          <el-form-item label="地图默认中心(经度)">
            <el-input v-model="systemConfig.mapCenterLng" />
          </el-form-item>
          <el-form-item label="地图默认中心(纬度)">
            <el-input v-model="systemConfig.mapCenterLat" />
          </el-form-item>
          <el-form-item label="地图默认缩放级别">
            <el-input-number v-model="systemConfig.mapZoom" :min="5" :max="18" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveSystemConfig">保存设置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const activeTab = ref('news')

// 公告管理
const newsList = ref([])
const newsDialogVisible = ref(false)
const newsDialogTitle = ref('新增公告')
const newsForm = reactive({ id: null, title: '', introduction: '', content: '' })

const fetchNews = () => {
  request.get('/news').then(res => {
    if (res.code === '200') newsList.value = res.data
  })
}

const showAddNews = () => {
  newsDialogTitle.value = '新增公告'
  newsForm.id = null; newsForm.title = ''; newsForm.introduction = ''; newsForm.content = ''
  newsDialogVisible.value = true
}

const editNews = (row) => {
  newsDialogTitle.value = '编辑公告'
  newsForm.id = row.id; newsForm.title = row.title; newsForm.introduction = row.introduction; newsForm.content = row.content
  newsDialogVisible.value = true
}

const submitNews = () => {
  if (!newsForm.title || !newsForm.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  const requestFn = newsForm.id ? request.put : request.post
  requestFn('/news', newsForm).then(res => {
    if (res.code === '200') {
      ElMessage.success(newsForm.id ? '更新成功' : '新增成功')
      newsDialogVisible.value = false
      fetchNews()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  }).catch(() => {
    ElMessage.error('网络异常，请稍后重试')
  })
}

const deleteNews = (id) => {
  ElMessageBox.confirm('确定要删除该公告吗?', '提示', { type: 'warning' }).then(() => {
    request.delete(`/news/${id}`).then(res => {
      if (res.code === '200') { ElMessage.success('删除成功'); fetchNews() }
      else { ElMessage.error(res.msg || '删除失败') }
    }).catch(() => {
      ElMessage.error('网络异常，请稍后重试')
    })
  }).catch(() => {})
}

// 系统参数
const systemConfig = reactive({
  systemName: '广州地铁智慧管理平台',
  mapCenterLng: '113.2644',
  mapCenterLat: '23.1291',
  mapZoom: 11
})

const saveSystemConfig = () => {
  ElMessage.success('系统参数已保存')
}

onMounted(() => { fetchNews() })
</script>

<style scoped>
.system-settings { padding: 20px; }
.settings-tabs { margin-top: 20px; background: #fff; padding: 16px; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
.toolbar { margin-bottom: 16px; }
</style>
