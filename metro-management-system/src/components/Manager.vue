<template>
    <div class="manager-container">
        <!-- 左侧菜单栏 -->
        <el-aside width="220px" class="sidebar">
            <div class="logo-container">
                <div class="logo-icon">
                    <el-icon size="28"><Van /></el-icon>
                </div>
                <div class="logo-text">
                    <h2>地铁管理系统</h2>
                    <span>Metro Management</span>
                </div>
            </div>
            <el-menu :default-active="activeMenu" class="el-menu-vertical" background-color="transparent"
                text-color="rgba(255, 255, 255, 0.7)" active-text-color="#ffffff" router>
                <el-menu-item index="/manager/AdminHomePage">
                    <el-icon>
                        <HomeFilled />
                    </el-icon>
                    <span>运营监控</span>
                </el-menu-item>
                <el-menu-item index="/manager/UserManagement">
                    <el-icon>
                        <User />
                    </el-icon>
                    <span>用户管理</span>
                </el-menu-item>
                <el-menu-item index="/manager/LineManagement">
                    <el-icon>
                        <MapLocation />
                    </el-icon>
                    <span>线路站点管理</span>
                </el-menu-item>
                <el-menu-item index="/manager/ScheduleConfig">
                    <el-icon>
                        <Timer />
                    </el-icon>
                    <span>时刻表配置</span>
                </el-menu-item>
                <el-menu-item index="/manager/DataManagement">
                    <el-icon>
                        <DataAnalysis />
                    </el-icon>
                    <span>数据管理</span>
                </el-menu-item>
                <el-menu-item index="/manager/SystemSettings">
                    <el-icon>
                        <Setting />
                    </el-icon>
                    <span>系统配置</span>
                </el-menu-item>
            </el-menu>
        </el-aside>

        <!-- 右侧内容区域 -->
        <el-container class="main-container">
            <!-- 顶部导航栏 -->
            <el-header class="header">
                <div class="header-left">
                    <breadcrumb />
                    <span class="welcome-text">欢迎您，管理员</span>
                </div>
                <div class="header-right">
                    <el-dropdown @command="handleCommand" class="user-dropdown">
                        <span class="el-dropdown-link">
                            <el-avatar :size="36" class="user-avatar">
                                <el-icon><Avatar /></el-icon>
                            </el-avatar>
                            <span class="username">{{ currentUser.username || '管理员' }}</span>
                            <el-icon class="el-icon--right"><arrow-down /></el-icon>
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu class="user-dropdown-menu">
                                <el-dropdown-item command="profile">
                                    <el-icon><User /></el-icon>个人信息
                                </el-dropdown-item>
                                <el-dropdown-item command="settings">
                                    <el-icon><Setting /></el-icon>系统设置
                                </el-dropdown-item>
                                <el-dropdown-item divided command="logout">
                                    <el-icon><SwitchButton /></el-icon>退出登录
                                </el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </el-header>

            <!-- 内容区域 -->
            <el-main class="content">
                <router-view v-slot="{ Component }">
                    <transition name="page" mode="out-in">
                        <component :is="Component" />
                    </transition>
                </router-view>
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
    User,
    MapLocation,
    Timer,
    DataAnalysis,
    Setting,
    ArrowDown,
    Avatar,
    Van,
    SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 当前用户信息
const currentUser = ref({
    username: ''
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
            ElMessage.success('已退出登录')
            // 跳转到登录页
            router.push('/')
        }).catch(() => {
            // 取消退出
        })
    } else if (command === 'profile') {
        ElMessage.info('个人信息功能开发中')
    } else if (command === 'settings') {
        ElMessage.info('系统设置功能开发中')
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
.manager-container {
    display: flex;
    height: 100%;
    overflow: hidden;
}

/* 左侧菜单栏样式 - 渐变深色背景 */
.sidebar {
    background: linear-gradient(180deg, #0f172a 0%, #1e293b 50%, #0f172a 100%);
    height: 100%;
    overflow-y: auto;
    overflow-x: hidden;
    box-shadow: 4px 0 20px rgba(0, 0, 0, 0.3);
    position: relative;
}

.sidebar::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: 
        radial-gradient(circle at 20% 80%, rgba(0, 137, 230, 0.1) 0%, transparent 50%),
        radial-gradient(circle at 80% 20%, rgba(0, 107, 179, 0.1) 0%, transparent 50%);
    pointer-events: none;
}

.logo-container {
    height: 70px;
    display: flex;
    align-items: center;
    padding: 0 20px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    position: relative;
    z-index: 1;
}

.logo-icon {
    width: 44px;
    height: 44px;
    background: linear-gradient(135deg, #0089e6 0%, #006bb3 100%);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    margin-right: 12px;
    box-shadow: 0 4px 14px rgba(0, 137, 230, 0.4);
}

.logo-text {
    display: flex;
    flex-direction: column;
}

.logo-text h2 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: #ffffff;
    letter-spacing: 0.5px;
}

.logo-text span {
    font-size: 11px;
    color: rgba(255, 255, 255, 0.5);
    text-transform: uppercase;
    letter-spacing: 1px;
    margin-top: 2px;
}

.el-menu-vertical {
    border-right: none;
    background: transparent !important;
    padding: 12px 0;
    position: relative;
    z-index: 1;
}

/* 菜单项样式 */
:deep(.el-menu-item) {
    height: 50px;
    line-height: 50px;
    margin: 4px 12px;
    padding: 0 16px !important;
    border-radius: 8px;
    border-left: 3px solid transparent;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
}

:deep(.el-menu-item::before) {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(90deg, rgba(0, 137, 230, 0.1) 0%, transparent 100%);
    opacity: 0;
    transition: opacity 0.3s ease;
}

:deep(.el-menu-item:hover) {
    background: rgba(255, 255, 255, 0.05) !important;
    border-left-color: rgba(0, 137, 230, 0.5);
}

:deep(.el-menu-item:hover::before) {
    opacity: 1;
}

:deep(.el-menu-item.is-active) {
    background: rgba(0, 137, 230, 0.15) !important;
    border-left-color: #0089e6;
    box-shadow: 0 0 20px rgba(0, 137, 230, 0.2);
}

:deep(.el-menu-item.is-active::before) {
    opacity: 1;
}

:deep(.el-menu-item .el-icon) {
    font-size: 18px;
    margin-right: 12px;
}

:deep(.el-menu-item span) {
    font-size: 14px;
    font-weight: 500;
}

/* 主容器样式 */
.main-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    background: linear-gradient(180deg, #f8fafc 0%, #ffffff 100%);
}

/* 顶部导航栏样式 - 毛玻璃效果 */
.header {
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05), 0 4px 12px rgba(0, 0, 0, 0.05);
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 24px;
    height: 70px;
    position: relative;
    z-index: 10;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 24px;
}

.welcome-text {
    font-size: 15px;
    color: #64748b;
    font-weight: 500;
    padding-left: 24px;
    border-left: 1px solid #e2e8f0;
}

.header-right {
    display: flex;
    align-items: center;
}

.user-dropdown {
    cursor: pointer;
}

.el-dropdown-link {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 6px 12px;
    border-radius: 8px;
    transition: all 0.3s ease;
}

.el-dropdown-link:hover {
    background: #f1f5f9;
}

.user-avatar {
    background: linear-gradient(135deg, #0089e6 0%, #006bb3 100%) !important;
    color: white;
    box-shadow: 0 2px 8px rgba(0, 137, 230, 0.3);
}

.username {
    font-size: 14px;
    font-weight: 500;
    color: #334155;
}

/* 内容区域样式 */
.content {
    padding: 24px;
    overflow-y: auto;
    overflow-x: hidden;
    flex: 1;
    position: relative;
}

/* 页面过渡动画 */
.page-enter-active,
.page-leave-active {
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-enter-from {
    opacity: 0;
    transform: translateY(10px);
}

.page-leave-to {
    opacity: 0;
    transform: translateY(-10px);
}

/* 下拉菜单样式 */
:deep(.user-dropdown-menu) {
    border-radius: 12px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
    padding: 8px;
    min-width: 160px;
}

:deep(.user-dropdown-menu .el-dropdown-menu__item) {
    padding: 10px 16px;
    border-radius: 8px;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 10px;
}

:deep(.user-dropdown-menu .el-dropdown-menu__item:hover) {
    background: #f1f5f9;
    color: #0089e6;
}

:deep(.user-dropdown-menu .el-dropdown-menu__item .el-icon) {
    font-size: 16px;
}

/* 滚动条样式 */
.sidebar::-webkit-scrollbar {
    width: 4px;
}

.sidebar::-webkit-scrollbar-track {
    background: transparent;
}

.sidebar::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.2);
    border-radius: 2px;
}

.sidebar::-webkit-scrollbar-thumb:hover {
    background: rgba(255, 255, 255, 0.3);
}

.content::-webkit-scrollbar {
    width: 8px;
}

.content::-webkit-scrollbar-track {
    background: transparent;
}

.content::-webkit-scrollbar-thumb {
    background: #cbd5e1;
    border-radius: 4px;
}

.content::-webkit-scrollbar-thumb:hover {
    background: #94a3b8;
}
</style>
