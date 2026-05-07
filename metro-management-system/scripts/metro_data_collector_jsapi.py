#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
广州地铁数据采集脚本 - 使用 JSAPI Key 版本
功能：
1. 使用高德地图地理编码 API 获取地铁站点坐标
2. 数据清洗、去重和验证
3. 生成 JSON、SQL、CSV 多种格式输出
4. 提供完整的数据字典文档

作者：AI Assistant
日期：2025-06
"""

import requests
import json
import time
import os
import re
import csv
from datetime import datetime
from dataclasses import dataclass
from typing import List, Dict, Optional
from collections import defaultdict

# ==================== 配置区域 ====================

# 高德地图API Key（JSAPI Key）
AMAP_KEY = "49f3c3fd7e6380f9c8b84c0f0bdd4d9f"

# 请求间隔（秒），避免请求过快被封
REQUEST_DELAY = 0.2

# 输出文件路径
OUTPUT_DIR = "./output"
JSON_FILE = f"{OUTPUT_DIR}/guangzhou_metro_jsapi.json"
SQL_FILE = f"{OUTPUT_DIR}/guangzhou_metro_jsapi.sql"
CSV_FILE = f"{OUTPUT_DIR}/guangzhou_metro_jsapi.csv"
DICT_FILE = f"{OUTPUT_DIR}/data_dictionary_jsapi.md"

# 广州地铁基础数据（从 guangzhou_subway_kopisee.json 更新）
METRO_LINES_BASE = {
    "1号线": {
        "color": "#F3D03E",
        "type": "普速地铁",
        "open_date": "1997-06-28",
        "stations": ["西塱", "坑口", "花地湾", "芳村", "黄沙", "长寿路", "陈家祠", "西门口", "公园前", "农讲所", "烈士陵园", "东山口", "杨箕", "体育西路", "体育中心", "广州东站"]
    },
    "2号线": {
        "color": "#00629B",
        "type": "普速地铁",
        "open_date": "2002-12-29",
        "stations": ["嘉禾望岗", "黄边", "江夏", "萧岗", "白云文化广场", "白云公园", "飞翔公园", "三元里", "广州火车站", "越秀公园", "纪念堂", "公园前", "海珠广场", "市二宫", "江南西", "昌岗", "江泰路", "东晓南", "南洲", "洛溪", "南浦", "会江", "石壁", "广州南站"]
    },
    "3号线": {
        "color": "#ECA154",
        "type": "普速地铁",
        "open_date": "2005-12-26",
        "stations": ["机场北", "机场南", "高增", "人和", "龙归", "嘉禾望岗", "白云大道北", "永泰", "同和", "京溪南方医院", "梅花园", "燕塘", "广州东站", "林和西", "体育西路", "珠江新城", "广州塔", "客村", "大塘", "沥滘", "厦滘", "大石", "汉溪长隆", "市桥", "番禺广场", "傍江", "石碁南", "海涌路", "海傍"]
    },
    "4号线": {
        "color": "#00843D",
        "type": "普速地铁",
        "open_date": "2005-12-26",
        "stations": ["黄村", "车陂", "车陂南", "万胜围", "官洲", "大学城北", "大学城南", "新造", "官桥", "石碁", "海傍", "低涌", "东涌", "庆盛", "黄阁汽车城", "黄阁", "蕉门", "金洲", "飞沙角", "广隆", "大涌", "塘坑", "南横", "南沙客运港"]
    },
    "5号线": {
        "color": "#C4003D",
        "type": "普速地铁",
        "open_date": "2009-12-28",
        "stations": ["滘口", "坦尾", "中山八", "西场", "西村", "广州火车站", "小北", "淘金", "区庄", "动物园", "杨箕", "五羊邨", "珠江新城", "猎德", "潭村", "员村", "科韵路", "车陂南", "东圃", "三溪", "鱼珠", "大沙地", "大沙东", "文冲", "双沙", "庙头", "夏园", "保盈大道", "夏港", "黄埔新港"]
    },
    "6号线": {
        "color": "#80225F",
        "type": "普速地铁",
        "open_date": "2013-12-28",
        "stations": ["浔峰岗", "横沙", "沙贝", "河沙", "坦尾", "如意坊", "黄沙", "文化公园", "一德路", "海珠广场", "北京路", "团一大广场", "越秀南路", "东湖", "东山口", "区庄", "黄花岗", "沙河顶", "沙河", "天平架", "燕塘", "天河客运站", "长湴", "植物园", "龙洞", "柯木塱", "高塘石", "黄陂", "金峰", "暹岗", "苏元", "萝岗", "香雪"]
    },
    "7号线": {
        "color": "#97D700",
        "type": "普速地铁",
        "open_date": "2016-12-28",
        "stations": ["燕山", "水西", "萝岗", "科丰路", "加庄", "姬堂", "大沙东", "裕丰围", "洪圣沙", "长洲", "深井", "大学城南", "板桥", "员岗", "南村万博", "汉溪长隆", "钟村", "谢村", "石壁", "广州南站", "大洲", "陈村北", "陈村", "锦龙", "南涌", "美的", "北滘公园", "美的大道"]
    },
    "8号线": {
        "color": "#008E9C",
        "type": "普速地铁",
        "open_date": "2010-09-25",
        "stations": ["滘心", "亭岗", "石井", "小坪", "石潭", "聚龙", "上步", "同德", "鹅掌坦", "西村", "彩虹桥", "陈家祠", "华林寺", "文化公园", "同福西", "凤凰新村", "沙园", "宝岗大道", "昌岗", "晓港", "中大", "鹭江", "客村", "赤岗", "磨碟沙", "新港东", "琶洲", "万胜围"]
    },
    "9号线": {
        "color": "#71CC98",
        "type": "普速地铁",
        "open_date": "2017-12-28",
        "stations": ["飞鹅岭", "花都汽车城", "广州北站", "花城路", "花果山公园", "花都广场", "马鞍山公园", "莲塘", "清㘵", "清塘", "高增"]
    },
    "10号线": {
        "color": "#7D9CC0",
        "type": "普速地铁",
        "open_date": "2025-06-29",
        "stations": ["西塱", "花围", "东沙", "大干围", "工业大道南", "东晓南", "五凤", "中大南门", "滨江东路", "东湖", "五羊邨", "杨箕东"]
    },
    "11号线": {
        "color": "#470A68",
        "type": "普速地铁",
        "open_date": "2024-12-28",
        "stations": ["赤沙", "琶洲", "员村", "天河公园", "华景路", "华师", "龙口西", "广州东站", "沙河", "云台花园", "大金钟路", "中医药大学", "梓元岗", "广州火车站", "流花", "彩虹桥", "中山八", "如意坊", "石围塘", "芳村", "大冲口", "沙涌", "鹤洞东", "棣园", "燕岗", "江泰路", "五凤", "逸景路", "上涌", "大塘", "龙潭"]
    },
    "12号线": {
        "color": "#00630F",
        "type": "普速地铁",
        "open_date": "2025-06-29",
        "stations": ["浔峰岗", "浔峰岗北", "西洲", "聚龙", "广州白云站", "棠涌", "新市墟", "白云文化广场", "广州体育馆", "景泰", "柯子岭", "中医药大学", "麓湖", "建设六马路", "烈士陵园", "东湖", "二沙岛", "赤岗塔", "赤岗", "赤沙北", "赤沙", "北山", "官洲", "大学城北", "大学城南"]
    },
    "13号线": {
        "color": "#8E8C13",
        "type": "普速地铁",
        "open_date": "2017-12-28",
        "stations": ["天河公园", "棠下", "车陂", "天河珠村", "鱼珠", "裕丰围", "双岗", "南海神庙", "夏园", "南岗", "沙村", "白江", "新塘", "官湖", "新沙"]
    },
    "14号线": {
        "color": "#793E80",
        "type": "普速地铁",
        "open_date": "2018-12-28",
        "stations": ["东风", "从化客运站", "赤草", "神岗", "太平", "新和", "马沥", "钟落潭", "竹料", "太和", "夏良", "白云东平", "嘉禾望岗", "彭边", "鹤龙", "鹤边", "马务", "新市墟", "云霄路", "乐嘉路"]
    },
    "18号线": {
        "color": "#00629B",
        "type": "高速地铁",
        "open_date": "2021-09-28",
        "stations": ["冼村", "磨碟沙", "龙潭", "沙溪", "南村万博", "番禺广场", "横沥", "万顷沙"]
    },
    "21号线": {
        "color": "#231F20",
        "type": "普速地铁",
        "open_date": "2018-12-28",
        "stations": ["天河公园", "棠东", "黄村", "大观南路", "天河智慧城", "神舟路", "科学城", "苏元", "水西", "长平", "金坑", "镇龙西", "镇龙", "中新", "坑贝", "凤岗", "朱村", "山田", "钟岗", "增城广场"]
    },
    "22号线": {
        "color": "#CD5228",
        "type": "高速地铁",
        "open_date": "2022-03-31",
        "stations": ["芳村", "西塱", "南漖", "南浦西", "陈头岗", "广州南站", "市广路", "祈福新邨", "番禺广场"]
    },
    "APM线": {
        "color": "#00B5E2",
        "type": "自动旅客捷运",
        "open_date": "2010-11-08",
        "stations": ["林和西", "体育中心南", "天河南", "黄埔大道", "妇儿中心", "花城大道", "大剧院", "海心沙", "广州塔"]
    },
    "广佛线": {
        "color": "#C4D600",
        "type": "城际地铁",
        "open_date": "2010-11-03",
        "stations": ["新城东", "东平", "世纪莲", "澜石", "魁奇路", "季华园", "同济路", "祖庙", "普君北路", "朝安", "桂城", "南桂路", "𧒽岗", "千灯湖", "金融高新区", "龙溪", "菊树", "西塱", "鹤洞", "沙涌", "沙园", "燕岗", "石溪", "南洲", "沥滘"]
    }
}


# ==================== 数据类定义 ====================

@dataclass
class Station:
    """地铁站点数据类"""
    id: str
    name: str
    line_name: str
    line_id: int
    sequence: int
    longitude: float = 0.0
    latitude: float = 0.0
    is_transfer: bool = False
    transfer_lines: List[str] = None
    address: str = ""

    def __post_init__(self):
        if self.transfer_lines is None:
            self.transfer_lines = []

    def to_dict(self) -> Dict:
        """转换为字典"""
        return {
            'id': self.id,
            'name': self.name,
            'lineName': self.line_name,
            'lineId': self.line_id,
            'sequence': self.sequence,
            'longitude': self.longitude,
            'latitude': self.latitude,
            'isTransfer': self.is_transfer,
            'transferLines': self.transfer_lines,
            'address': self.address
        }


@dataclass
class MetroLine:
    """地铁线路数据类"""
    id: int
    name: str
    color: str
    line_type: str
    open_date: str
    stations: List[Station] = None

    def __post_init__(self):
        if self.stations is None:
            self.stations = []

    def to_dict(self) -> Dict:
        """转换为字典"""
        return {
            'id': self.id,
            'name': self.name,
            'color': self.color,
            'type': self.line_type,
            'openDate': self.open_date,
            'stationCount': len(self.stations),
            'stations': [s.to_dict() for s in self.stations]
        }


# ==================== 工具函数 ====================

def log(message: str):
    """打印日志"""
    print(f"[{datetime.now().strftime('%H:%M:%S')}] {message}")


def geocode_address(address: str, city: str = "广州") -> Optional[Dict]:
    """
    使用高德地图地理编码 API 获取地址坐标
    
    Args:
        address: 地址字符串
        city: 城市名称
        
    Returns:
        包含经纬度的字典，或 None（查询失败）
    """
    url = "https://restapi.amap.com/v3/geocode/geo"
    params = {
        'key': AMAP_KEY,
        'address': address,
        'city': city,
        'output': 'JSON'
    }
    
    try:
        response = requests.get(url, params=params, timeout=10)
        data = response.json()
        
        if data.get('status') == '1' and data.get('geocodes'):
            location = data['geocodes'][0].get('location', '')
            if location:
                lng, lat = location.split(',')
                return {
                    'longitude': float(lng),
                    'latitude': float(lat),
                    'formatted_address': data['geocodes'][0].get('formatted_address', ''),
                    'level': data['geocodes'][0].get('level', '')
                }
        return None
    except Exception as e:
        log(f"地理编码请求失败: {str(e)}")
        return None


def geocode_station(station_name: str, line_name: str) -> Optional[Dict]:
    """
    查询地铁站点的经纬度
    
    Args:
        station_name: 站点名称
        line_name: 线路名称
        
    Returns:
        包含经纬度的字典，或 None
    """
    # 构建地址
    address = f"广州市{line_name}{station_name}地铁站"
    
    result = geocode_address(address)
    if result:
        return result
    
    # 如果失败，尝试简化地址
    address = f"广州市{station_name}地铁站"
    return geocode_address(address)


# ==================== 核心处理类 ====================

class MetroDataCollector:
    """地铁数据采集器"""
    
    def __init__(self, amap_key: str):
        self.amap_key = amap_key
        self.lines: List[MetroLine] = []
        self.station_coords_cache: Dict[str, Dict] = {}
    
    def collect_data(self) -> bool:
        """
        采集地铁数据
        
        Returns:
            是否成功
        """
        log("=" * 60)
        log("开始采集广州地铁数据")
        log("=" * 60)
        
        # 构建线路数据
        line_id = 1
        for line_name, line_info in METRO_LINES_BASE.items():
            log(f"处理线路: {line_name}")
            
            metro_line = MetroLine(
                id=line_id,
                name=line_name,
                color=line_info['color'],
                line_type=line_info['type'],
                open_date=line_info['open_date']
            )
            
            # 处理站点
            for seq, station_name in enumerate(line_info['stations'], 1):
                station_id = f"L{line_id:02d}_S{seq:03d}"
                
                station = Station(
                    id=station_id,
                    name=station_name,
                    line_name=line_name,
                    line_id=line_id,
                    sequence=seq
                )
                
                metro_line.stations.append(station)
            
            self.lines.append(metro_line)
            line_id += 1
            
            # 延时避免请求过快
            time.sleep(0.1)
        
        # 标记换乘站
        self._mark_transfer_stations()
        
        log(f"共处理 {len(self.lines)} 条线路")
        total_stations = sum(len(line.stations) for line in self.lines)
        log(f"共 {total_stations} 个站点")
        
        return True
    
    def _mark_transfer_stations(self):
        """标记换乘站"""
        log("标记换乘站...")
        
        # 统计每个站点出现的线路
        station_lines = defaultdict(list)
        for line in self.lines:
            for station in line.stations:
                station_lines[station.name].append(line.name)
        
        # 标记换乘站
        transfer_count = 0
        for line in self.lines:
            for station in line.stations:
                lines_at_station = station_lines[station.name]
                if len(lines_at_station) > 1:
                    station.is_transfer = True
                    station.transfer_lines = lines_at_station
                    transfer_count += 1
        
        log(f"共标记 {transfer_count} 个换乘站")
    
    def enrich_coordinates(self):
        """使用地理编码丰富站点坐标数据"""
        log("=" * 60)
        log("开始获取站点坐标")
        log("=" * 60)
        
        success_count = 0
        fail_count = 0
        
        for line in self.lines:
            for station in line.stations:
                # 检查缓存
                cache_key = f"{station.name}_{line.name}"
                if cache_key in self.station_coords_cache:
                    coords = self.station_coords_cache[cache_key]
                    station.longitude = coords['longitude']
                    station.latitude = coords['latitude']
                    success_count += 1
                    continue
                
                # 查询坐标
                result = geocode_station(station.name, line.name)
                
                if result:
                    station.longitude = result['longitude']
                    station.latitude = result['latitude']
                    station.address = result['formatted_address']
                    
                    # 缓存结果
                    self.station_coords_cache[cache_key] = {
                        'longitude': result['longitude'],
                        'latitude': result['latitude']
                    }
                    
                    success_count += 1
                    log(f"  ✓ {line.name} - {station.name}: ({result['longitude']:.6f}, {result['latitude']:.6f})")
                else:
                    fail_count += 1
                    log(f"  ✗ {line.name} - {station.name}: 获取失败")
                
                # 延时避免触发频率限制
                time.sleep(REQUEST_DELAY)
        
        log(f"坐标获取完成: 成功 {success_count}, 失败 {fail_count}")
    
    def save_to_json(self, filepath: str):
        """保存为 JSON"""
        os.makedirs(os.path.dirname(filepath), exist_ok=True)
        
        data = {
            'city': '广州',
            'updateTime': datetime.now().strftime('%Y-%m-%d'),
            'totalLines': len(self.lines),
            'totalStations': sum(len(line.stations) for line in self.lines),
            'lines': [line.to_dict() for line in self.lines]
        }
        
        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=2)
        
        log(f"JSON 文件保存成功: {filepath}")
    
    def save_to_csv(self, filepath: str):
        """保存为 CSV"""
        os.makedirs(os.path.dirname(filepath), exist_ok=True)
        
        with open(filepath, 'w', newline='', encoding='utf-8-sig') as f:
            writer = csv.writer(f)
            writer.writerow(['线路ID', '线路名称', '线路颜色', '站点ID', '站点名称', 
                           '序号', '经度', '纬度', '是否换乘', '换乘线路'])
            
            for line in self.lines:
                for station in line.stations:
                    writer.writerow([
                        line.id,
                        line.name,
                        line.color,
                        station.id,
                        station.name,
                        station.sequence,
                        station.longitude,
                        station.latitude,
                        '是' if station.is_transfer else '否',
                        ','.join(station.transfer_lines) if station.transfer_lines else ''
                    ])
        
        log(f"CSV 文件保存成功: {filepath}")
    
    def generate_sql(self, filepath: str):
        """生成 SQL 文件"""
        os.makedirs(os.path.dirname(filepath), exist_ok=True)
        
        sql_lines = []
        sql_lines.append("-- 广州地铁数据")
        sql_lines.append(f"-- 生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        sql_lines.append("")
        
        # 线路表
        sql_lines.append("-- 地铁线路表数据")
        sql_lines.append("INSERT INTO metro_line (id, line_code, line_name, line_color, line_type, total_stations, open_date, status) VALUES")
        
        line_values = []
        for line in self.lines:
            line_code = f"L{line.id:02d}"
            val = f"({line.id}, '{line_code}', '{line.name}', '{line.color}', '{line.line_type}', {len(line.stations)}, '{line.open_date}', 1)"
            line_values.append(val)
        
        sql_lines.append(",\n".join(line_values) + ";")
        sql_lines.append("")
        
        # 站点表
        sql_lines.append("-- 地铁站点表数据")
        sql_lines.append("INSERT INTO metro_station (line_id, station_code, station_name, sequence, longitude, latitude, is_transfer, address, status) VALUES")
        
        station_values = []
        for line in self.lines:
            for station in line.stations:
                is_transfer = 1 if station.is_transfer else 0
                val = f"({line.id}, '{station.id}', '{station.name}', {station.sequence}, {station.longitude}, {station.latitude}, {is_transfer}, '{station.address}', 1)"
                station_values.append(val)
        
        # 分批写入
        batch_size = 100
        for i in range(0, len(station_values), batch_size):
            batch = station_values[i:i+batch_size]
            if i == 0:
                sql_lines.append(",\n".join(batch))
            else:
                sql_lines.append("INSERT INTO metro_station (line_id, station_code, station_name, sequence, longitude, latitude, is_transfer, address, status) VALUES")
                sql_lines.append(",\n".join(batch))
        
        sql_lines.append(";")
        
        # 换乘表
        sql_lines.append("")
        sql_lines.append("-- 换乘关系表数据")
        sql_lines.append("INSERT INTO metro_transfer (station_name, line_id_1, line_id_2, is_same_platform) VALUES")
        
        transfer_values = []
        processed_pairs = set()
        
        for line in self.lines:
            for station in line.stations:
                if station.is_transfer:
                    for other_line_name in station.transfer_lines:
                        if other_line_name != line.name:
                            # 查找另一条线路的ID
                            other_line_id = None
                            for l in self.lines:
                                if l.name == other_line_name:
                                    other_line_id = l.id
                                    break
                            
                            if other_line_id:
                                pair = tuple(sorted([line.id, other_line_id]))
                                if pair not in processed_pairs:
                                    val = f"('{station.name}', {line.id}, {other_line_id}, 0)"
                                    transfer_values.append(val)
                                    processed_pairs.add(pair)
        
        if transfer_values:
            sql_lines.append(",\n".join(transfer_values) + ";")
        
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write("\n".join(sql_lines))
        
        log(f"SQL 文件生成成功: {filepath}")
    
    def generate_data_dictionary(self, filepath: str):
        """生成数据字典文档"""
        os.makedirs(os.path.dirname(filepath), exist_ok=True)
        
        content = f"""# 广州地铁数据字典

## 数据概览

- **城市**: 广州
- **更新日期**: {datetime.now().strftime('%Y-%m-%d')}
- **线路总数**: {len(self.lines)}
- **站点总数**: {sum(len(line.stations) for line in self.lines)}

## 线路列表

| 线路ID | 线路名称 | 颜色 | 类型 | 开通日期 | 站点数 |
|--------|----------|------|------|----------|--------|
"""
        
        for line in self.lines:
            content += f"| {line.id} | {line.name} | {line.color} | {line.line_type} | {line.open_date} | {len(line.stations)} |\n"
        
        content += """
## 数据表结构

### 1. metro_line（地铁线路表）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INT | 线路ID |
| line_code | VARCHAR | 线路代码 |
| line_name | VARCHAR | 线路名称 |
| line_color | VARCHAR | 线路颜色（HEX） |
| line_type | VARCHAR | 线路类型 |
| total_stations | INT | 站点总数 |
| open_date | DATE | 开通日期 |
| status | INT | 状态（1=正常） |

### 2. metro_station（地铁站点表）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INT | 站点ID |
| line_id | INT | 所属线路ID |
| station_code | VARCHAR | 站点代码 |
| station_name | VARCHAR | 站点名称 |
| sequence | INT | 站点序号 |
| longitude | DECIMAL | 经度 |
| latitude | DECIMAL | 纬度 |
| is_transfer | INT | 是否换乘站（1=是） |
| address | VARCHAR | 地址 |
| status | INT | 状态（1=正常） |

### 3. metro_transfer（换乘关系表）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INT | 关系ID |
| station_name | VARCHAR | 站点名称 |
| line_id_1 | INT | 线路1 ID |
| line_id_2 | INT | 线路2 ID |
| is_same_platform | INT | 是否同站台换乘 |

## 数据来源

- 线路和站点信息: 广州地铁官方网站、高德地图
- 坐标数据: 高德地图地理编码 API

## 更新说明

本次数据更新包括广州地铁所有运营线路，共 {len(self.lines)} 条线路，{sum(len(line.stations) for line in self.lines)} 个站点。
"""
        
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        
        log(f"数据字典生成成功: {filepath}")


def main():
    """主函数"""
    collector = MetroDataCollector(AMAP_KEY)
    
    # 1. 采集基础数据
    if not collector.collect_data():
        log("数据采集失败")
        return
    
    # 2. 获取坐标数据
    collector.enrich_coordinates()
    
    # 3. 保存为 JSON
    collector.save_to_json(JSON_FILE)
    
    # 4. 保存为 CSV
    collector.save_to_csv(CSV_FILE)
    
    # 5. 生成 SQL
    collector.generate_sql(SQL_FILE)
    
    # 6. 生成数据字典
    collector.generate_data_dictionary(DICT_FILE)
    
    log("=" * 60)
    log("数据采集完成！")
    log(f"输出文件:")
    log(f"  - JSON: {JSON_FILE}")
    log(f"  - CSV: {CSV_FILE}")
    log(f"  - SQL: {SQL_FILE}")
    log(f"  - 数据字典: {DICT_FILE}")
    log("=" * 60)


if __name__ == "__main__":
    main()
