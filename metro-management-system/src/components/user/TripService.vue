<template>
  <div class="metro-nav">
    <!-- 左侧输入面板 -->
    <div class="input-panel">
      <h3>广州地铁导航</h3>
      
      <!-- 起点选择 -->
      <div class="input-group">
        <label>起点</label>
        <!-- 线路选择 -->
        <el-select
          v-model="startLine"
          placeholder="选择线路"
          clearable
          class="line-select"
          @change="onStartLineChange"
        >
          <el-option
            v-for="line in metroLines"
            :key="line.id"
            :label="line.name"
            :value="line.id"
          >
            <span class="line-option">
              <span class="line-dot" :style="{ backgroundColor: line.color }"></span>
              {{ line.name }}
            </span>
          </el-option>
        </el-select>
        <!-- 站点选择 -->
        <el-select
          v-model="startStation"
          placeholder="选择站点"
          clearable
          class="station-select"
          :disabled="!startLine"
        >
          <el-option
            v-for="station in getStations(startLine)"
            :key="station"
            :label="station"
            :value="station"
          />
        </el-select>
      </div>

      <!-- 交换按钮 -->
      <el-button type="primary" circle class="swap-btn" @click="swapStations">
        <el-icon><Switch /></el-icon>
      </el-button>

      <!-- 终点选择 -->
      <div class="input-group">
        <label>终点</label>
        <!-- 线路选择 -->
        <el-select
          v-model="endLine"
          placeholder="选择线路"
          clearable
          class="line-select"
          @change="onEndLineChange"
        >
          <el-option
            v-for="line in metroLines"
            :key="line.id"
            :label="line.name"
            :value="line.id"
          >
            <span class="line-option">
              <span class="line-dot" :style="{ backgroundColor: line.color }"></span>
              {{ line.name }}
            </span>
          </el-option>
        </el-select>
        <!-- 站点选择 -->
        <el-select
          v-model="endStation"
          placeholder="选择站点"
          clearable
          class="station-select"
          :disabled="!endLine"
        >
          <el-option
            v-for="station in getStations(endLine)"
            :key="station"
            :label="station"
            :value="station"
          />
        </el-select>
      </div>

      <!-- 查询按钮 -->
      <el-button type="primary" class="search-btn" @click="searchRoute" :loading="loading">
        查询路线
      </el-button>
    </div>

    <!-- 右侧地图和路线结果 -->
    <div class="map-container">
      <div id="map" class="map"></div>
      <!-- 路线结果面板 -->
      <div id="panel" class="route-panel"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Switch } from '@element-plus/icons-vue'
import AMapLoader from '@amap/amap-jsapi-loader'

// 广州地铁线路数据
const metroLines = [
  {
    id: '1',
    name: '1号线',
    color: '#F3D03E',
    stations: ['西塱', '坑口', '花地湾', '芳村', '黄沙', '长寿路', '陈家祠', '西门口', '公园前', '农讲所', '烈士陵园', '东山口', '杨箕', '体育西路', '体育中心', '广州东站']
  },
  {
    id: '2',
    name: '2号线',
    color: '#00629B',
    stations: ['广州南站', '石壁', '会江', '南浦', '洛溪', '南洲', '东晓南', '江泰路', '昌岗', '江南西', '市二宫', '海珠广场', '公园前', '纪念堂', '越秀公园', '广州火车站', '三元里', '飞翔公园', '白云公园', '白云文化广场', '萧岗', '江夏', '黄边', '嘉禾望岗']
  },
  {
    id: '3',
    name: '3号线',
    color: '#ECA154',
    stations: ['天河客运站', '五山', '华师', '岗顶', '石牌桥', '体育西路', '珠江新城', '广州塔', '客村', '大塘', '沥滘', '厦滘', '大石', '汉溪长隆', '市桥', '番禺广场']
  },
  {
    id: '3n',
    name: '3号线北延段',
    color: '#ECA154',
    stations: ['机场北', '机场南', '高增', '人和', '龙归', '嘉禾望岗', '白云大道北', '永泰', '同和', '京溪南方医院', '梅花园', '燕塘', '广州东站', '林和西', '体育西路']
  },
  {
    id: '4',
    name: '4号线',
    color: '#00843D',
    stations: ['黄村', '车陂', '车陂南', '万胜围', '官洲', '大学城北', '大学城南', '新造', '官桥', '石碁', '海傍', '低涌', '东涌', '庆盛', '黄阁汽车城', '黄阁', '蕉门', '金洲', '飞沙角', '广隆', '大涌', '塘坑', '南横', '南沙客运港']
  },
  {
    id: '5',
    name: '5号线',
    color: '#C5003E',
    stations: ['滘口', '坦尾', '中山八', '西场', '西村', '广州火车站', '小北', '淘金', '区庄', '动物园', '杨箕', '五羊邨', '珠江新城', '猎德', '潭村', '员村', '科韵路', '车陂南', '东圃', '三溪', '鱼珠', '大沙地', '大沙东', '文冲']
  },
  {
    id: '6',
    name: '6号线',
    color: '#6B0D00',
    stations: ['浔峰岗', '横沙', '沙贝', '河沙', '坦尾', '如意坊', '黄沙', '文化公园', '一德路', '海珠广场', '北京路', '团一大广场', '东湖', '东山口', '区庄', '黄花岗', '沙河顶', '沙河', '天平架', '燕塘', '天河客运站', '长湴', '植物园', '龙洞', '柯木塱', '高塘石', '黄陂', '金峰', '暹岗', '苏元', '萝岗', '香雪']
  },
  {
    id: '7',
    name: '7号线',
    color: '#97D700',
    stations: ['美的大道', '北滘公园', '大学城南', '板桥', '员岗', '南村万博', '汉溪长隆', '钟村', '谢村', '石壁', '广州南站']
  },
  {
    id: '8',
    name: '8号线',
    color: '#008E9C',
    stations: ['滘心', '亭岗', '石井', '小坪', '石潭', '聚龙', '上步', '同德', '鹅掌坦', '西村', '彩虹桥', '陈家祠', '华林寺', '文化公园', '同福西', '凤凰新村', '沙园', '宝岗大道', '昌岗', '晓港', '中大', '鹭江', '客村', '赤岗', '磨碟沙', '新港东', '琶洲', '万胜围']
  },
  {
    id: '9',
    name: '9号线',
    color: '#71CC98',
    stations: ['飞鹅岭', '花都汽车城', '广州北站', '花城路', '花果山公园', '花都广场', '马鞍山公园', '莲塘', '清㘵', '清塘', '高增']
  },
  {
    id: '13',
    name: '13号线',
    color: '#8E8C13',
    stations: ['鱼珠', '裕丰围', '双岗', '南海神庙', '夏园', '南岗', '沙村', '白江', '新塘', '官湖', '新沙']
  },
  {
    id: '14',
    name: '14号线',
    color: '#893895',
    stations: ['嘉禾望岗', '白云东平', '夏良', '太和', '竹料', '钟落潭', '马沥', '新和', '太平', '神岗', '赤草', '从化客运站', '东风']
  },
  {
    id: '14z',
    name: '14号线知识城支线',
    color: '#893895',
    stations: ['新和', '红卫', '新南', '枫下', '知识城', '何棠下', '旺村', '汤村', '镇龙北', '镇龙']
  },
  {
    id: '18',
    name: '18号线',
    color: '#1E90FF',
    stations: ['冼村', '磨碟沙', '龙潭', '沙溪', '南村万博', '番禺广场', '横沥', '万顷沙']
  },
  {
    id: '21',
    name: '21号线',
    color: '#211747',
    stations: ['员村', '天河公园', '棠东', '黄村', '大观南路', '天河智慧城', '神舟路', '科学城', '苏元', '水西', '长平', '金坑', '镇龙西', '镇龙', '中新', '坑贝', '凤岗', '朱村', '山田', '钟岗', '增城广场']
  },
  {
    id: '22',
    name: '22号线',
    color: '#CD853F',
    stations: ['陈头岗', '广州南站', '市广路', '番禺广场']
  },
  {
    id: 'gf',
    name: '广佛线',
    color: '#C4D600',
    stations: ['新城东', '东平', '世纪莲', '澜石', '魁奇路', '季华园', '同济路', '祖庙', '普君北路', '朝安', '桂城', '南桂路', '𧒽岗', '千灯湖', '金融高新区', '龙溪', '菊树', '西塱', '鹤洞', '沙涌', '沙园', '燕岗']
  },
  {
    id: 'apm',
    name: 'APM线',
    color: '#00BFFF',
    stations: ['广州塔', '海心沙', '大剧院', '花城大道', '妇儿中心', '黄埔大道', '天河南', '体育中心南', '林和西']
  }
]

const map = ref(null)
const AMap = ref(null)
const transfer = ref(null)
const loading = ref(false)

// 起点线路和站点
const startLine = ref('')
const startStation = ref('')

// 终点线路和站点
const endLine = ref('')
const endStation = ref('')

// 根据线路ID获取站点列表
const getStations = (lineId) => {
  const line = metroLines.find(l => l.id === lineId)
  return line ? line.stations : []
}

// 起点线路变化时清空站点
const onStartLineChange = () => {
  startStation.value = ''
}

// 终点线路变化时清空站点
const onEndLineChange = () => {
  endStation.value = ''
}

// 交换起点终点
const swapStations = () => {
  const tempLine = startLine.value
  const tempStation = startStation.value
  
  startLine.value = endLine.value
  startStation.value = endStation.value
  
  endLine.value = tempLine
  endStation.value = tempStation
}

// 搜索路线
const searchRoute = () => {
  if (!startStation.value || !endStation.value) {
    ElMessage.warning('请选择起点和终点站点')
    return
  }

  if (startStation.value === endStation.value) {
    ElMessage.warning('起点和终点不能相同')
    return
  }

  loading.value = true

  // 清除之前的路线
  if (transfer.value) {
    transfer.value.clear()
  }

  // 构造公交换乘类
  transfer.value = new AMap.value.Transfer({
    map: map.value,
    city: '广州市',
    panel: 'panel',
    policy: AMap.value.TransferPolicy.LEAST_TIME
  })

  // 根据起、终点名称查询公交换乘路线
  transfer.value.search([
    { keyword: startStation.value, city: '广州' },
    { keyword: endStation.value, city: '广州' }
  ], (status, result) => {
    loading.value = false
    
    if (status === 'complete') {
      ElMessage.success('绘制公交路线完成')
    } else {
      ElMessage.error('公交路线数据查询失败：' + result)
    }
  })
}

onMounted(async () => {
  try {
    window._AMapSecurityConfig = {
      securityJsCode: 'f2e281bddbaaed3bfc03b711e00122fa'
    }

    const amap = await AMapLoader.load({
      key: '49f3c3fd7e6380f9c8b84c0f0bdd4d9f',
      version: '2.0',
      plugins: [
        'AMap.Scale', 
        'AMap.ToolBar',
        'AMap.Transfer'
      ]
    })

    AMap.value = amap

    // 初始化地图
    map.value = new amap.Map('map', {
      resizeEnable: true,
      center: [113.2644, 23.1291],
      zoom: 11
    })

    // 添加控件
    map.value.addControl(new amap.Scale())
    map.value.addControl(new amap.ToolBar())

  } catch (error) {
    console.error('地图加载失败:', error)
    ElMessage.error('地图加载失败')
  }
})

onUnmounted(() => {
  if (transfer.value) {
    transfer.value.clear()
  }
  if (map.value) {
    map.value.destroy()
  }
})
</script>

<style scoped>
.metro-nav {
  display: flex;
  height: 100%;
}

.input-panel {
  width: 360px;
  padding: 20px;
  background: #fff;
  box-shadow: 2px 0 8px rgba(0,0,0,0.1);
  overflow-y: auto;
  flex-shrink: 0;
}

.input-panel h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #333;
}

.input-group {
  margin-bottom: 16px;
}

.input-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.line-select {
  width: 100%;
  margin-bottom: 8px;
}

.station-select {
  width: 100%;
}

.line-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.line-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  display: inline-block;
}

.swap-btn {
  display: block;
  margin: 0 auto 16px;
}

.search-btn {
  width: 100%;
  margin-top: 10px;
}

.map-container {
  flex: 1;
  position: relative;
  min-width: 0;
}

.map {
  width: 100%;
  height: 100%;
}

/* 路线结果面板样式 */
.route-panel {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 320px;
  max-height: 90%;
  overflow-y: auto;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 6px 0 rgba(114, 124, 245, .5);
}

/* 适配高德地图 Transfer 面板样式 */
:global(.amap-call) {
  background-color: #009cf9;
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
  padding: 10px;
  color: #fff;
  font-weight: bold;
}

:global(.amap-lib-transfer) {
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 4px;
  overflow: hidden;
}
</style>
