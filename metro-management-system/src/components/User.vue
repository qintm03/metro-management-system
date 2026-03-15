<template>
    <div class="user-container">
        <!-- 左侧菜单栏 -->
        <el-aside width="200px" class="sidebar">
            <div class="logo-container">
                <h2>地铁管理系统</h2>
            </div>
            <el-menu :default-active="activeMenu" class="el-menu-vertical" background-color="#304156"
                text-color="#bfcbd9" active-text-color="#409EFF" router>
                <el-menu-item index="/user/UserHomePage">
                    <el-icon>
                        <HomeFilled />
                    </el-icon>
                    <span>首页</span>
                </el-menu-item>
                <el-menu-item index="/user/TripService">
                    <el-icon>
                        <Guide />
                    </el-icon>
                    <span>出行服务</span>
                </el-menu-item>
                <el-menu-item index="/user/UserTicketManagement">
                    <el-icon>
                        <Ticket />
                    </el-icon>
                    <span>我的票务</span>
                </el-menu-item>
                <el-menu-item index="/user/UserTrackManagement">
                    <el-icon>
                        <Location />
                    </el-icon>
                    <span>我的轨迹</span>
                </el-menu-item>
            </el-menu>
        </el-aside>

        <!-- 右侧内容区域 -->
        <el-container class="main-container">
            <!-- 顶部导航栏 -->
            <el-header class="header">
                <div class="header-left">
                    <span class="welcome-text">欢迎您，普通用户</span>
                </div>
                <div class="header-right">
                    <el-dropdown @command="handleCommand">
                        <span class="el-dropdown-link">
                            <el-icon>
                                <Avatar />
                            </el-icon>
                            <span>{{ currentUser.username }}</span>
                            <el-icon class="el-icon--right"><arrow-down /></el-icon>
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </el-header>

            <!-- 内容区域 -->
            <el-main class="content">
                <router-view></router-view>
            </el-main>
        </el-container>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
    HomeFilled,
    Ticket,
    Location,
    Guide,
    ArrowDown,
    Avatar
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 当前用户信息
const currentUser = ref({

})

// 当前激活的菜单
const activeMenu = computed(() => {
    return route.path
})

// 处理下拉菜单命令
const handleCommand = (command) => {
    if (command === 'logout') {
        ElMessageBox.confirm('确定要退出登录吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            // 清除本地存储的用户信息
            localStorage.removeItem('system-user')
            localStorage.removeItem('metro_token')
            localStorage.removeItem('metro_role')
            ElMessage.success('已退出登录')
            // 跳转到登录页
            router.push('/')
        }).catch(() => {
            // 取消退出
        })
    } else if (command === 'profile') {
        ElMessage.info('个人信息功能开发中')
    }
}

// 页面加载时获取当前用户信息
onMounted(() => {
    const user = localStorage.getItem('system-user')
    if (user) {
        currentUser.value = JSON.parse(user)
    }
})
</script>

<style scoped>
.user-container {
    display: flex;
    height: 100vh;
    overflow: hidden;
}

/* 左侧菜单栏样式 */
.sidebar {
    background-color: #304156;
    height: 100%;
    overflow-y: auto;
}

.logo-container {
    height: 60px;
    line-height: 60px;
    text-align: center;
    color: #fff;
    font-size: 18px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-container h2 {
    margin: 0;
    font-size: 18px;
    font-weight: 500;
}

.el-menu-vertical {
    border-right: none;
}

/* 主容器样式 */
.main-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

/* 顶部导航栏样式 */
.header {
    background-color: #fff;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
}

.header-left {
    display: flex;
    align-items: center;
}

.welcome-text {
    font-size: 16px;
    color: #333;
}

.header-right {
    display: flex;
    align-items: center;
}

.el-dropdown-link {
    display: flex;
    align-items: center;
    cursor: pointer;
    color: #606266;
    font-size: 14px;
}

.el-dropdown-link span {
    margin: 0 8px;
}

/* 内容区域样式 */
.content {
    background-color: #f0f2f5;
    padding: 20px;
    overflow-y: auto;
    flex: 1;
}
</style>
