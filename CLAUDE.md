# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在此代码库中工作时提供指导。

## 项目概述

这是一个城市智慧地铁管理系统，前端使用 Vue 3，后端使用 Spring Boot。

**技术栈:**
- 前端: Vue 3.5.24 + Vite 7.2.4 + Element Plus + Vue Router + Axios + ECharts + ArcGIS/AMap
- 后端: Spring Boot 4.0.1 + MyBatis + MySQL 8.0
- Java: 21

## 构建和运行命令

**后端 (Spring Boot):**
```bash
cd springboot
mvn clean install          # 构建 JAR 包
mvn spring-boot:run        # 运行开发服务器 (默认: http://localhost:8080)
```

**前端 (Vue 3 + Vite):**
```bash
cd metro-management-system
npm install                 # 安装依赖
npm run dev                # 启动前端开发服务器 (默认: http://localhost:5173)
npm run build              # 构建生产版本
npm run preview            # 预览生产构建
```

**一键运行 (开发):**
```bash
cd metro-management-system
npm run start:backend      # 在后台运行后端
npm run dev                # 在前台运行前端
# 或使用 concurrently 同时运行:
npm run start:all          # 同时运行后端和前端
```

## 架构说明

### 前端结构 (`metro-management-system/src/`)

**路由:**
- `/` → 登录页 (默认)
- `/manager` → 管理员控制台 (嵌套路由: AdminHomePage, LineManagement, MapManagement, AdminTicketManagement, AdminTrackManagement, UserManagement)
- `/user` → 用户控制台 (嵌套路由: UserHomePage, TripService, UserTicketManagement, UserTrackManagement, RealTimeTrack, StationServices)

**布局组件:**
- `Manager.vue` - 管理员布局，左侧边栏导航
- `User.vue` - 用户布局，左侧边栏导航

**API 集成:**
- `src/utils/request.js` - Axios 实例拦截器 (设置 `Content-Type`，处理 401 重定向)
- `src/api/metroApi.js` - 地铁数据 API 服务层
- 基础 URL: `http://localhost:8080` (通过 `VITE_BASE_URL` 环境变量配置)
- 所有 API 调用通过 `request.get()` 或 `request.post()` 进行

**主要库:**
- `@arcgis/core` - ArcGIS 地图/可视化
- `@amap/amap-jsapi-loader` - AMap (高德地图) 集成
- `echarts` - 数据可视化
- `v-scale-screen` - 全屏响应式缩放

### 后端结构 (`springboot/src/main/java/com/example/springboot/`)

**MVC 分层:**
- `controller/` - 15 个 REST 控制器 (MetroController, LoginController, UserController, AdminController 等)
- `service/` - 服务层，接口 + 实现类 (MetroService, AdminService, UserService 等)
- `mapper/` - MyBatis 映射器 (MetroLineMapper, MetroStationMapper, MetroTransferMapper 等)
- `entity/` - JPA 实体 (MetroLine, MetroStation, MetroTransfer, User, Admin, News, Messages 等)

**地铁领域实体:**
- `MetroLine` - 线路信息 (id, lineId, lineName, color, 行车参数, path 等)
- `MetroStation` - 站点信息 (stationName, lineId, sequence, 坐标, isTransfer 等)
- `MetroTransfer` - 换乘关系 (stationName, fromLineId, toLineId 等)

**公共类:**
- `common/Result` - 统一响应包装器 (`{code, msg, data}`)
- `common/CorsConfig` - CORS 配置

**MyBatis 配置:**
- Mapper XML 位置: `classpath:mapper/*.xml`
- 驼峰命名映射: 已启用 (`map-underscore-to-camel-case=true`)
- 扫描包: `com.example.springboot.mapper`

## 数据库配置

位置: `springboot/src/main/resources/application.properties`

```
spring.datasource.url=jdbc:mysql://localhost:3306/metro
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.type-aliases-package=com.example.springboot.entity
mybatis.configuration.map-underscore-to-camel-case=true
```

数据库名: `metro`

## 前后端集成模式

1. 前端通过 `metroApi.js` 服务函数调用 API
2. 服务调用 `request.js` 中的 `request.get()` 或 `request.post()`
3. Axios 添加 `Content-Type: application/json;charset=utf-8` 请求头
4. 后端在控制器 → 服务 → 映射器层级接收请求
5. 映射器查询 MyBatis (注解或 XML)
6. 服务返回业务逻辑结果
7. 控制器将结果封装在 `Result` 对象中
8. 响应拦截器解析响应并处理 401 错误

**认证流程:**
- 登录 POST 到 `/login`
- `LoginController` 检查角色 → 调用 `adminService.login()` 或 `userService.login()`
- 返回包含角色的用户对象
- 前端存储到 `localStorage`
- 根据角色重定向到 `/manager` 或 `/user`
