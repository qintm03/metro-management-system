# 城市智慧地铁管理系统 — 项目文档

> 基于 Vue 3 + Spring Boot 的全栈地铁管理平台，面向广州地铁运营场景，提供管理员控制台与用户门户双端入口。

---

## 目录

1. [项目概述](#1-项目概述)
2. [技术栈](#2-技术栈)
3. [项目结构](#3-项目结构)
4. [前端架构](#4-前端架构)
5. [后端架构](#5-后端架构)
6. [API 接口清单](#6-api-接口清单)
7. [数据库设计](#7-数据库设计)
8. [路由配置](#8-路由配置)
9. [认证流程](#9-认证流程)
10. [数据流](#10-数据流)
11. [已知问题](#11-已知问题)

---

## 1. 项目概述

城市智慧地铁管理系统是一个面向城市轨道交通运营管理的全栈 Web 应用，提供：

- **管理员端**：数据看板、线路/地图管理、票务/轨道管理、用户管理、列车线路管理
- **用户端**：智慧出行平台、出行导航、实时列车追踪、站点服务查询、票务管理

系统基于广州地铁真实数据进行建模，集成了高德地图(AMap)进行可视化展示。

---

## 2. 技术栈

### 前端

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | 3.5.24 | 前端框架 (Composition API) |
| Vite | 7.2.4 | 构建工具 |
| Element Plus | 2.13.0 | UI 组件库 |
| Vue Router | ^4.0 | 路由管理 |
| Axios | 1.13.2 | HTTP 客户端 |
| ECharts | 6.0.0 | 数据可视化图表 |
| AMap (高德地图) | JSAPI 2.0 | 地图展示与交互 |
| ArcGIS | 4.34.8 | GIS 地图引擎 |

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 4.0.1 | 后端框架 |
| MyBatis | — | ORM 持久层框架 |
| MySQL | 8.0 | 关系型数据库 |
| JDK | 21 | Java 开发环境 |
| Maven | — | 项目构建管理 |

---

## 3. 项目结构

```
E:\毕设\
├── metro-management-system/          # 前端项目 (Vue 3 + Vite)
│   ├── index.html                    # HTML 入口
│   ├── package.json                  # 依赖与脚本配置
│   ├── vite.config.js                # Vite 构建配置
│   └── src/
│       ├── main.js                   # 应用入口
│       ├── App.vue                   # 根组件
│       ├── router.js                 # Vue Router 配置
│       ├── style.css                 # 全局样式
│       ├── api/
│       │   └── metroApi.js           # 地铁数据 API 服务层
│       ├── assets/
│       │   ├── css/                  # 组件样式
│       │   └── img/                  # 图片资源
│       ├── components/
│       │   ├── Login.vue             # 登录页
│       │   ├── Manager.vue           # 管理员布局
│       │   ├── User.vue              # 用户布局
│       │   ├── Test.vue              # AMap 3D 测试页
│       │   ├── Navigation/           # 导航子组件
│       │   ├── admin/                # 管理员子页面
│       │   └── user/                 # 用户子页面
│       ├── styles/                   # 设计系统样式
│       └── utils/
│           ├── request.js            # Axios 实例与拦截器
│           ├── auth.js               # 认证辅助工具
│           └── metroSimulation/      # 列车模拟工具
│
├── springboot/                       # 后端项目 (Spring Boot)
│   ├── pom.xml                       # Maven 依赖配置
│   └── src/main/
│       ├── java/com/example/springboot/
│       │   ├── SpringbootApplication.java   # 启动类
│       │   ├── common/                      # 公共类 (Result, CorsConfig)
│       │   ├── controller/                  # REST 控制器 (15个)
│       │   ├── service/                     # 业务服务层
│       │   ├── mapper/                      # MyBatis 映射器
│       │   ├── entity/                      # 数据实体
│       │   └── exception/                   # 异常处理
│       └── resources/
│           ├── application.properties       # 数据库与 MyBatis 配置
│           └── mapper/                      # MyBatis XML 映射文件
│
├── metro_line.txt                   # 地铁线路原始数据
├── metro_station.txt                # 地铁站点原始数据 (广州地铁)
│
└── ssm基于SSM城市智慧地铁管理系统73c2d/  # 参考项目 (旧版 SSM)
```

---

## 4. 前端架构

### 4.1 核心模块

#### API 服务层 (`src/api/metroApi.js`)

封装了 6 个地铁数据接口：

| 函数 | API 端点 | 说明 |
|------|----------|------|
| `getAllLines()` | GET `/api/metro/lines` | 获取所有线路 |
| `getLineById(id)` | GET `/api/metro/lines/{id}` | 按 DB ID 获取线路 |
| `getAllStations()` | GET `/api/metro/stations` | 获取所有站点 |
| `getStationsByLineId(lineId)` | GET `/api/metro/stations/line/{lineId}` | 按线路获取站点 |
| `getTransferStations()` | GET `/api/metro/stations/transfer` | 获取换乘站 |
| `getFullMetroData()` | GET `/api/metro/full` | 获取完整地铁数据 |
| `getLineDetail(lineId)` | GET `/api/metro/detail/{lineId}` | 获取线路详情含站点 |

#### HTTP 请求层 (`src/utils/request.js`)

- 基于 Axios 封装
- 基础 URL：`http://localhost:8080`（通过 `VITE_BASE_URL` 环境变量配置）
- 超时时间：30 秒
- 请求拦截器：自动设置 `Content-Type: application/json;charset=utf-8`
- 响应拦截器：401 状态码自动重定向到登录页

#### 认证辅助 (`src/utils/auth.js`)

- 基于 `localStorage` 存储用户信息
- 提供 `getToken()`、`getUser()`、`setUser()`、`isAdmin()`、`isLoggedIn()`、`clearAuth()` 等方法
- 存储键名：`system-user`

### 4.2 页面组件

#### 管理员端 (`components/admin/`)

| 组件 | 路由 | 状态 | 说明 |
|------|------|------|------|
| `AdminHomePage.vue` | `/manager/AdminHomePage` | ✅ 已完成 | 管理员看板，AMap 3D 地图展示，定位北京 `[116.333926, 39.997245]` |
| `UserManagement.vue` | `/manager/UserManagement` | ✅ 已完成 | 用户 CRUD：搜索、新增、编辑、删除，分页查询 `/user` API |
| `ChecixianluManagement.vue` | `/manager/ChecixianluManagement` | ✅ 已完成 | 列车线路 CRUD：搜索、新增、编辑、删除，调用 `/checixianlu` API |
| `LineManagement.vue` | `/manager/LineManagement` | ⏳ 占位 | "功能开发中" |
| `MapManagement.vue` | `/manager/MapManagement` | ⏳ 占位 | "功能开发中" |
| `AdminTicketManagement.vue` | `/manager/AdminTicketManagement` | ⏳ 占位 | "功能开发中" |
| `AdminTrackManagement.vue` | `/manager/AdminTrackManagement` | ⏳ 占位 | "功能开发中" |

#### 用户端 (`components/user/`)

| 组件 | 路由 | 状态 | 说明 |
|------|------|------|------|
| `UserHomePage.vue` | `/user/UserHomePage` | ✅ 已完成 | 智慧地铁管理平台首页，深色主题，ECharts 仪表盘（线路数、站点数、列车数、客流量统计） |
| `TripService.vue` | `/user/TripService` | ✅ 已完成 | 出行导航：选择起止站点，换乘方案推荐，AMap 路径展示 |
| `StationServices.vue` | `/user/StationServices` | ✅ 已完成 | 站点周边服务：公交路线查询、POI 信息展示 |
| `RealTimeTrack.vue` | `/user/RealTimeTrack` | ✅ 已完成 | 实时列车追踪：选择线路，查看列车在线路上的实时位置与运行时间 |
| `UserTrackManagement.vue` | `/user/UserTrackManagement` | ✅ 已完成 | 站点坐标查询：搜索站点名称，AMap 地图标记显示 |
| `MetroDataCollector.vue` | 未注册路由 | ✅ 已完成 | 数据采集工具：加载地铁线路图，从 AMap 采集站点数据，导出 JSON/SQL/CSV |
| `UserTicketManagement.vue` | `/user/UserTicketManagement` | ⏳ 占位 | "功能开发中" |

### 4.3 设计系统 (`src/styles/`)

- **`variables.css`**：50+ CSS 自定义属性，定义颜色（主色 #0089e6）、间距（4px 基准）、排版、阴影、过渡、暗黑模式变量
- **`utilities.css`**：工具类（flexbox、grid、间距、文本对齐、显示辅助）
- **`components.css`**：可复用组件样式（按钮 `.btn-primary`、卡片 `.card`、徽章 `.badge`、标签 `.tag`）

---

## 5. 后端架构

### 5.1 MVC 分层

```
Controller (接收请求) → Service (业务逻辑) → Mapper (数据访问) → MySQL
```

- **Controller 层**：15 个 REST 控制器，接收 HTTP 请求，返回 `Result` 统一响应
- **Service 层**：接口 + 实现类，业务逻辑处理
- **Mapper 层**：MyBatis 映射器，数据库操作（注解 + XML 双模式）
- **Entity 层**：JPA 实体，数据模型

### 5.2 统一响应 (`common/Result.java`)

```java
{
  "code": "200",    // 成功 "200"，失败 "500"
  "msg": "success", // 提示信息
  "data": {}        // 响应数据
}
```

静态工厂方法：`Result.success()`、`Result.success(data)`、`Result.error()`、`Result.error(msg)`

### 5.3 异常处理

- `CustomException`：自定义运行时异常，携带用户可读的错误消息
- `GlobalExceptionHandler`：`@ControllerAdvice` 全局捕获，`Exception` 返回通用错误，`CustomException` 返回自定义消息

### 5.4 两种 MyBatis 模式

| 模式 | 适用范围 | 说明 |
|------|----------|------|
| **注解模式** | `MetroLineMapper`、`MetroStationMapper`、`MetroTransferMapper` | 使用 `@Select`、`@Insert` 等注解直接写在接口上，无 XML 文件 |
| **XML 模式** | `UserMapper`、`ChecixianluMapper` 等 11 个 Mapper | SQL 写在 `resources/mapper/*.xml` 中 |

### 5.5 两组领域实体

| 组别 | 实体类 | 字段风格 | 说明 |
|------|--------|----------|------|
| **核心地铁域** | `MetroLine`、`MetroStation`、`MetroTransfer` | 英文命名，`Long`/`BigDecimal`/`LocalTime` 类型 | 新建模块，广州地铁真实数据 |
| **遗留域** | `Checixianlu`、`Zhandianchaxun`、`Zhandianzhoubian` 等 | 中文拼音命名，`Integer`/`String` 类型 | 从 SSM 项目迁移，沿用旧字段名 |

---

## 6. API 接口清单

### 6.1 认证

| 方法 | 端点 | 说明 |
|------|------|------|
| POST | `/login` | 用户登录。请求体：`{username, password, role}`。角色值："管理员"/"普通用户" |

### 6.2 地铁数据（核心域）

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/api/metro/lines` | 获取所有地铁线路 |
| GET | `/api/metro/lines/{id}` | 按 DB ID 查询线路 |
| GET | `/api/metro/lines/lineId/{lineId}` | 按线路编号查询 |
| GET | `/api/metro/stations` | 获取所有站点 |
| GET | `/api/metro/stations/line/{lineId}` | 按线路获取站点 |
| GET | `/api/metro/stations/transfer` | 获取换乘站 |
| GET | `/api/metro/stations/search?keyword=` | 按名称搜索站点 |
| GET | `/api/metro/full` | 获取完整地铁数据（线路 + 分组站点） |
| GET | `/api/metro/detail/{lineId}` | 获取线路详情（含站点列表） |
| GET | `/api/metro/transfers` | 获取所有换乘关系 |
| GET | `/api/metro/transfers/station/{name}` | 按站点查询换乘关系 |
| GET | `/api/metro/line/{lineId}/schedule` | 获取线路时刻表（运营时间、发车间隔、站停时间） |
| GET | `/api/metro/line/{lineId}/trains` | 获取列车列表（20 辆模拟列车，含起始偏移时间） |

### 6.3 用户管理

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/user` | 获取所有用户 |
| POST | `/user` | 新增用户 |
| PUT | `/user` | 更新用户 |
| DELETE | `/user/{id}` | 删除用户 |
| GET | `/user/page?pageNum=&pageSize=` | 分页查询用户 |

### 6.4 列车线路管理 (Checixianlu)

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/checixianlu` | 查询所有 |
| GET | `/checixianlu/{id}` | 按 ID 查询 |
| POST | `/checixianlu` | 新增 |
| PUT | `/checixianlu` | 更新 |
| DELETE | `/checixianlu/{id}` | 删除 |
| GET | `/checixianlu/page?pageNum=&pageSize=` | 分页查询 |

### 6.5 站点查询 (Zhandianchaxun)

完整 CRUD 端点：`/zhandianchaxun`、`/zhandianchaxun/page`

### 6.6 站点周边 (Zhandianzhoubian)

完整 CRUD 端点：`/zhandianzhoubian`、`/zhandianzhoubian/page`

### 6.7 新闻资讯 (News)

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/news` | 查询所有 |
| GET | `/news/{id}` | 按 ID 查询 |
| GET | `/news/limit/{limit}` | 按数量限制查询 |
| POST | `/news` | 新增 |
| PUT | `/news` | 更新 |
| DELETE | `/news/{id}` | 删除 |
| POST | `/news/search` | 条件搜索 |

### 6.8 留言反馈 (Messages)

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/messages` | 查询所有 |
| GET | `/messages/{id}` | 按 ID 查询 |
| GET | `/messages/user/{userid}` | 按用户查询 |
| GET | `/messages/unreplied` | 查询未回复 |
| POST | `/messages` | 创建 |
| PUT | `/messages` | 更新 |
| POST | `/messages/reply` | 回复留言 |
| DELETE | `/messages/{id}` | 删除 |
| POST | `/messages/search` | 条件搜索 |
| GET | `/messages/count/{userid}` | 统计用户留言数 |
| GET | `/messages/count/unreplied` | 统计未回复数 |

### 6.9 收藏管理 (Storeup)

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/storeup` | 查询所有 |
| GET | `/storeup/{id}` | 按 ID 查询 |
| GET | `/storeup/user/{userid}` | 按用户查询 |
| GET | `/storeup/check?userid=&tablename=&refid=` | 检查是否已收藏 |
| POST | `/storeup` | 添加收藏 |
| PUT | `/storeup` | 更新 |
| POST | `/storeup/cancel` | 取消收藏 |
| DELETE | `/storeup/{id}` | 删除 |
| POST | `/storeup/search` | 条件搜索 |
| GET | `/storeup/count/{userid}` | 统计用户收藏数 |

### 6.10 系统配置 (Config)

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/config` | 查询所有 |
| GET | `/config/{id}` | 按 ID 查询 |
| GET | `/config/name/{name}` | 按名称查询 |
| POST | `/config` | 新增 |
| PUT | `/config` | 更新 |
| DELETE | `/config/{id}` | 删除 |

### 6.11 评论模块

三个评论模块（Discusschecixianlu、Discusszhandianchaxun、Discusszhandianzhoubian）共享相同接口模式：

| 方法 | 端点模式 | 说明 |
|------|----------|------|
| GET | `/{base}` | 查询所有 |
| GET | `/{base}/{id}` | 按 ID 查询 |
| GET | `/{base}/refid/{refid}` | 按关联 ID 查询 |
| GET | `/{base}/userid/{userid}` | 按用户 ID 查询 |
| POST | `/{base}` | 创建 |
| PUT | `/{base}` | 更新 |
| DELETE | `/{base}/{id}` | 删除 |
| POST | `/{base}/search` | 条件搜索 |
| GET | `/{base}/count/{refid}` | 统计评论数 |

---

## 7. 数据库设计

数据库名：`metro`，MySQL 8.0

### 7.1 核心地铁表

#### `metro_line` — 地铁线路

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT (PK) | 自增主键 |
| line_id | VARCHAR | 业务线路编号 (如 "1", "2") |
| line_name | VARCHAR | 显示名称 (如 "1号线") |
| line_code | VARCHAR | 缩写代码 |
| color | VARCHAR | 线路颜色 (十六进制) |
| start_station | VARCHAR | 起始站 |
| end_station | VARCHAR | 终点站 |
| first_train_time | TIME | 首班车时间 |
| last_train_time | TIME | 末班车时间 |
| interval | INT | 发车间隔 (秒) |
| segment_time | INT | 站间运行时间 (秒) |
| dwell_time | INT | 站停时间 (秒) |
| distance | DECIMAL | 线路总里程 |
| station_count | INT | 站点数量 |
| path | VARCHAR | 线路 GeoJSON 路径 |
| status | INT | 状态 (1=启用) |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### `metro_station` — 地铁站点

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT (PK) | 自增主键 |
| station_name | VARCHAR | 站点名称 |
| line_id | VARCHAR | 所属线路编号 (FK → metro_line.line_id) |
| sequence | INT | 在线路上的顺序 |
| longitude | DECIMAL | 经度 |
| latitude | DECIMAL | 纬度 |
| is_transfer | INT | 是否换乘站 (1=是) |
| transfer_count | INT | 换乘线路数 |
| status | INT | 状态 (1=启用) |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### `metro_transfer` — 换乘关系

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT (PK) | 自增主键 |
| station_name | VARCHAR | 换乘站名称 |
| from_line_id | VARCHAR | 来源线路 |
| to_line_id | VARCHAR | 目标线路 |
| to_line_name | VARCHAR | 目标线路显示名称 |
| status | INT | 状态 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 7.2 遗留业务表 (中文拼音字段)

#### `checixianlu` — 列车线路

| 字段 | 说明 |
|------|------|
| id | 主键 |
| xianlumingcheng | 线路名称 |
| luxiantupian | 路线图片 |
| xianlufenlei | 线路分类 |
| shifazhandian | 始发站点 |
| zhongdianzhandian | 终点站点 |
| tujingzhandian | 途经站点 |
| checi | 车次 |
| huanchengxinxi | 换乘信息 |
| yunxingshijian | 运行时间 |
| luxianxiangqing | 路线详情 |
| gengxinshijian | 更新时间 |
| feiyongshuoming | 费用说明 |
| huanchengfangan | 换乘方案 |
| clicknum | 点击量 |
| addtime | 创建时间 |

#### `zhandianchaxun` — 站点查询

| 字段 | 说明 |
|------|------|
| id | 主键 |
| zhandianmingcheng | 站点名称 |
| tupian | 图片 |
| daolumingcheng | 道路名称 |
| xiangxidizhi | 详细地址 |
| jingguozhandian | 经过站点 |
| fujinjianzhu | 附近建筑 |
| gengxinshijian | 更新时间 |
| addtime | 创建时间 |

#### `zhandianzhoubian` — 站点周边

| 字段 | 说明 |
|------|------|
| id | 主键 |
| zhandianmingcheng | 站点名称 |
| tupian | 图片 |
| daolumingcheng | 道路名称 |
| xiangxidizhi | 详细地址 |
| zhoubiangongjiao | 周边公交 |
| zhoubianqingkuang | 周边情况 (POI) |
| gengxinshijian | 更新时间 |
| addtime | 创建时间 |

### 7.3 系统表

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `admin` | 管理员 | id, name, username, password, role (默认"管理员") |
| `user` | 普通用户 | id, name, username, password, role (默认"普通用户") |
| `news` | 新闻资讯 | id, title, introduction, picture, content, addtime |
| `messages` | 留言反馈 | id, userid, username, avatarurl, content, cpicture, reply, rpicture, addtime |
| `storeup` | 收藏记录 | id, userid, refid, tablename, name, picture, type, inteltype, remark, addtime |
| `config` | 系统配置 | id, name, value |
| `discusschecixianlu` | 线路评论 | id, refid, userid, avatarurl, nickname, content, reply, addtime |
| `discusszhandianchaxun` | 站点评论 | 同上 |
| `discusszhandianzhoubian` | 周边评论 | 同上 |

---

## 8. 路由配置

```javascript
const routes = [
  { path: '/',           component: Login },              // 登录页
  { path: '/test',       component: Test },               // AMap 3D 测试

  // ===== 管理员端 =====
  {
    path: '/manager',
    component: Manager,
    redirect: '/manager/AdminHomePage',
    children: [
      { path: 'AdminHomePage',          component: AdminHomePage },
      { path: 'LineManagement',         component: LineManagement },
      { path: 'MapManagement',          component: MapManagement },
      { path: 'AdminTicketManagement',  component: AdminTicketManagement },
      { path: 'AdminTrackManagement',   component: AdminTrackManagement },
      { path: 'UserManagement',         component: UserManagement },
      // ⚠️ ChecixianluManagement 在侧边栏引用但未注册到路由
    ]
  },

  // ===== 用户端 =====
  {
    path: '/user',
    component: User,
    redirect: '/user/UserHomePage',
    children: [
      { path: 'UserHomePage',           component: UserHomePage },
      { path: 'TripService',            component: TripService },
      { path: 'UserTicketManagement',   component: UserTicketManagement },
      { path: 'UserTrackManagement',    component: UserTrackManagement },
      { path: 'RealTimeTrack',          component: RealTimeTrack },
      { path: 'StationServices',        component: StationServices },
    ]
  }
]
```

---

## 9. 认证流程

```
用户输入凭证 → Login.vue 发送 POST /login → LoginController 根据角色分发
                                                     ↓
                         管理员角色 ───→ AdminService.login()
                         普通用户 ───→ UserService.login()
                                          ↓
                              查找用户 → 不存在？→ CustomException("用户不存在")
                              密码错误？→ CustomException("账号或密码错误")
                                          ↓
                              成功 → 返回用户对象
                                          ↓
                         前端存入 localStorage (key: system-user)
                         根据角色重定向 /manager 或 /user
```

> **注意**：当前版本未实现 JWT Token 或 Session 管理，`auth.js` 中的 token 相关函数为预留框架。

---

## 10. 数据流

### 10.1 通用请求流程

```
Vue 组件 → metroApi.js 服务函数 → request.js (Axios) → HTTP 请求
                                                            ↓
后端 Controller → Service → Mapper (MyBatis) → MySQL
                                                            ↓
Result 统一响应 ← Controller ← Service ← Mapper
                                                            ↓
Vue 组件 ← metroApi.js 解析响应 ← Axios 响应拦截器
```

### 10.2 出行导航 (TripService)

```
TripService.vue
  → getFullMetroData() → GET /api/metro/full
    → MetroController.getAllFull()
      → MetroService: 查询所有线路 + 所有站点，按 line_id 分组
      → 返回 { lines: [...], stations: { lineId: [...] } }
  → 前端渲染线路/站点选择器
  → 用户选择起止站点 → AMap 路径规划与展示
```

### 10.3 实时列车追踪 (RealTimeTrack)

```
RealTimeTrack.vue
  → getSchedule(lineId) → GET /api/metro/line/{lineId}/schedule
    → 返回线路运营参数（首末班车时间、间隔、站停时间、站点列表）
  → getTrains(lineId) → GET /api/metro/line/{lineId}/trains
    → MetroServiceImpl: 生成 20 辆模拟列车，计算偏移发车时间
    → 返回每辆车的起始偏移秒数
  → 前端定时动画：模拟列车在 AMap 上的移动
```

### 10.4 用户主页仪表盘 (UserHomePage)

```
UserHomePage.vue
  → getFullMetroData()
  → ECharts 柱状图：各线路客流量统计
  → ECharts 折线图：各时段客流趋势
  → 统计卡片：线路总数、站点总数、在线列车数、日客流量
```

---

## 11. 已知问题

### 11.1 路由缺失

`ChecixianluManagement.vue` 在 `Manager.vue` 的侧边栏中使用路由 `/manager/ChecixianluManagement`，但该路径**未在 `router.js` 中注册**。点击侧边栏导航项会导致路由错误。

### 11.2 无真正认证机制

- 后端登录仅返回用户对象，未实现 JWT/Session
- 密码明文存储，未加密
- `auth.js` 中的 `getToken()` 始终返回 `null`

### 11.3 两套独立实体体系

项目中存在两套功能重叠的实体体系：

| 核心地铁域 | 遗留业务域 |
|------------|------------|
| `MetroLine` + `MetroStation` + `MetroTransfer` | `Checixianlu` + `Zhandianchaxun` + `Zhandianzhoubian` |
| 英文命名，现代类型 | 拼音命名，String 类型 |
| 注解 Mapper | XML Mapper |
| 实时地图展示 | 传统 CRUD 管理 |

两套体系数据未打通，可能存在数据重复或冲突。

### 11.4 占位页面

以下组件为**占位页面**，显示"功能开发中"提示：

- `LineManagement.vue`
- `MapManagement.vue`
- `AdminTicketManagement.vue`
- `AdminTrackManagement.vue`
- `UserTicketManagement.vue`

### 11.5 无数据库初始化脚本

- 原 `system_tables.sql` 已删除
- 无 `schema.sql` 或 `data.sql` 自动初始化脚本
- 站点数据存在于 `metro_station.txt` 但未导入数据库

---

## 附录：构建与运行

### 后端

```bash
cd springboot
mvn clean install          # 构建 JAR 包
mvn spring-boot:run        # 运行开发服务器 (默认: http://localhost:8080)
```

### 前端

```bash
cd metro-management-system
npm install                 # 安装依赖
npm run dev                # 启动前端开发服务器 (默认: http://localhost:5173)
npm run build              # 构建生产版本
npm run preview            # 预览生产构建
```

### 一键运行

```bash
cd metro-management-system
npm run start:backend      # 在后台运行后端
npm run dev                # 在前台运行前端
npm run start:all          # 同时运行后端和前端
```

### 数据库配置

```properties
# springboot/src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/metro
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

---

*文档生成日期：2026-05-07*
