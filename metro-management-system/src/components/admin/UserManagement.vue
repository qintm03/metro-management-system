<template>
  <div class="user-management">
    <el-page-header title="用户管理" />

    <div class="content-wrapper">
      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索用户名" style="width: 200px; margin-right: 10px" clearable />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="success" @click="handleAdd">新增用户</el-button>
      </div>

      <el-table :data="tableData" style="width: 100%; margin-top: 20px" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column label="密码" width="150">
          <template #default>
            <span>******</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" width="150" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="row.role === '管理员' ? 'danger' : 'primary'">{{ row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

     <!--  分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="管理员" />
            <el-option label="普通用户" value="普通用户" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request.js'

const tableData = ref([])
const allData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const formRef = ref()

const form = reactive({
  id: null,
  username: '',
  password: '',
  name: '',
  role: '普通用户'
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const fetchData = () => {
  request.get('/user').then(res => {
    if (res.code === '200') {
      allData.value = res.data
      filterAndPaginateData()
    }
  })
}

const filterAndPaginateData = () => {
  let filteredData = allData.value

  if (searchKeyword.value) {
    filteredData = filteredData.filter(item =>
      item.username.includes(searchKeyword.value) ||
      item.name.includes(searchKeyword.value)
    )
  }

  total.value = filteredData.length

  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  tableData.value = filteredData.slice(start, end)
}

const handleSearch = () => {
  currentPage.value = 1
  filterAndPaginateData()
}

const handleReset = () => {
  searchKeyword.value = ''
  currentPage.value = 1
  filterAndPaginateData()
}

const handleAdd = () => {
  dialogTitle.value = '新增用户'
  form.id = null
  form.username = ''
  form.password = ''
  form.name = ''
  form.role = '普通用户'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  form.id = row.id
  form.username = row.username
  form.password = ''  // 编辑时不填充密码
  form.name = row.name
  form.role = row.role
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该用户吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.delete(`/user/${id}`).then(res => {
      if (res.code === '200') {
        ElMessage.success('删除成功')
        fetchData()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(() => {})
}

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      if (form.id) {
        const data = { ...form }
        if (!data.password) delete data.password  // 编辑时密码为空则不修改
        request.put('/user', data).then(res => {
          if (res.code === '200') {
            ElMessage.success('更新成功')
            dialogVisible.value = false
            fetchData()
          } else {
            ElMessage.error(res.msg)
          }
        })
      } else {
        request.post('/user', form).then(res => {
          if (res.code === '200') {
            ElMessage.success('新增成功')
            dialogVisible.value = false
            fetchData()
          } else {
            ElMessage.error(res.msg)
          }
        })
      }
    }
  })
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  filterAndPaginateData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  filterAndPaginateData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.content-wrapper {
  margin-top: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
}
</style>
