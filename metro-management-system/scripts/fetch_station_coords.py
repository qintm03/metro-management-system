#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
批量查询广州地铁站经纬度
使用高德地图地理编码 API
"""

import requests
import json
import time
import os
from datetime import datetime
from typing import List, Dict, Optional

# ==================== 配置区域 ====================

# 高德地图 Web服务 API Key
AMAP_KEY = "5d828209350ee94ec2391a76634cc431"

# 输出目录
OUTPUT_DIR = "./output"

# 请求间隔（秒），避免触发频率限制
REQUEST_DELAY = 0.2

# 重试次数
MAX_RETRIES = 3

# ==================== 广州地铁站列表 ====================
# 从 guangzhou_metro_jsapi.json 提取的所有站点（去重）
GUANGZHOU_METRO_STATIONS = [
    # 1号线
    "西塱", "坑口", "花地湾", "芳村", "黄沙", "长寿路", "陈家祠", "西门口",
    "公园前", "农讲所", "烈士陵园", "东山口", "杨箕", "体育西路", "体育中心", "广州东站",
    
    # 2号线
    "嘉禾望岗", "黄边", "江夏", "萧岗", "白云文化广场", "白云公园", "飞翔公园",
    "三元里", "广州火车站", "越秀公园", "纪念堂", "公园前", "海珠广场", "市二宫",
    "江南西", "昌岗", "江泰路", "东晓南", "南洲", "洛溪", "南浦", "会江", "石壁", "广州南站",
    
    # 3号线
    "机场北", "机场南", "高增", "人和", "龙归", "嘉禾望岗", "白云大道北", "永泰",
    "同和", "京溪南方医院", "梅花园", "燕塘", "广州东站", "林和西", "体育西路",
    "珠江新城", "广州塔", "客村", "大塘", "沥滘", "厦滘", "大石", "汉溪长隆",
    "市桥", "番禺广场", "傍江", "石碁南", "海涌路", "海傍",
    
    # 4号线
    "黄村", "车陂", "车陂南", "万胜围", "官洲", "大学城北", "大学城南", "新造",
    "官桥", "石碁", "海傍", "低涌", "东涌", "庆盛", "黄阁汽车城", "黄阁",
    "蕉门", "金洲", "飞沙角", "广隆", "大涌", "塘坑", "南横", "南沙客运港",
    
    # 5号线
    "滘口", "坦尾", "中山八", "西场", "西村", "广州火车站", "小北", "淘金",
    "区庄", "动物园", "杨箕", "五羊邨", "珠江新城", "猎德", "潭村", "员村",
    "科韵路", "车陂南", "东圃", "三溪", "鱼珠", "大沙地", "大沙东", "文冲",
    "双沙", "庙头", "夏园", "保盈大道", "夏港", "黄埔新港",
    
    # 6号线
    "浔峰岗", "横沙", "沙贝", "河沙", "坦尾", "如意坊", "黄沙", "文化公园",
    "一德路", "海珠广场", "北京路", "团一大广场", "越秀南路", "东湖", "东山口",
    "区庄", "黄花岗", "沙河顶", "沙河", "天平架", "燕塘", "天河客运站", "长湴",
    "植物园", "龙洞", "柯木塱", "高塘石", "黄陂", "金峰", "暹岗", "苏元", "萝岗", "香雪",
    
    # 7号线
    "燕山", "水西", "萝岗", "科丰路", "加庄", "姬堂", "大沙东", "裕丰围",
    "洪圣沙", "长洲", "深井", "大学城南", "板桥", "员岗", "南村万博", "汉溪长隆",
    "钟村", "谢村", "石壁", "广州南站", "大洲", "陈村北", "陈村", "锦龙",
    "南涌", "美的", "北滘公园", "美的大道",
    
    # 8号线
    "滘心", "亭岗", "石井", "小坪", "石潭", "聚龙", "上步", "同德", "鹅掌坦",
    "西村", "彩虹桥", "陈家祠", "华林寺", "文化公园", "同福西", "凤凰新村",
    "沙园", "宝岗大道", "昌岗", "晓港", "中大", "鹭江", "客村", "赤岗",
    "磨碟沙", "新港东", "琶洲", "万胜围",
    
    # 9号线
    "飞鹅岭", "花都汽车城", "广州北站", "花城路", "花果山公园", "花都广场",
    "马鞍山公园", "莲塘", "清㘵", "清塘", "高增",
    
    # 10号线
    "西塱", "花围", "东沙", "大干围", "工业大道南", "东晓南", "五凤",
    "中大南门", "滨江东路", "东湖", "五羊邨", "杨箕东",
    
    # 11号线
    "赤沙", "琶洲", "员村", "天河公园", "华景路", "华师", "龙口西", "广州东站",
    "沙河", "云台花园", "大金钟路", "中医药大学", "梓元岗", "广州火车站", "流花",
    "彩虹桥", "中山八", "如意坊", "石围塘", "芳村", "大冲口", "沙涌",
    "鹤洞东", "棣园", "燕岗", "江泰路", "五凤", "逸景路", "上涌", "大塘", "龙潭",
    
    # 12号线
    "浔峰岗", "浔峰岗北", "西洲", "聚龙", "广州白云站", "棠涌", "新市墟",
    "白云文化广场", "广州体育馆", "景泰", "柯子岭", "中医药大学", "麓湖",
    "建设六马路", "烈士陵园", "东湖", "二沙岛", "赤岗塔", "赤岗", "赤沙北",
    "赤沙", "北山", "官洲", "大学城北", "大学城南",
    
    # 13号线
    "天河公园", "棠下", "车陂", "天河珠村", "鱼珠", "裕丰围", "双岗",
    "南海神庙", "夏园", "南岗", "沙村", "白江", "新塘", "官湖", "新沙",
    
    # 14号线
    "东风", "从化客运站", "赤草", "神岗", "太平", "新和", "马沥", "钟落潭",
    "竹料", "太和", "夏良", "白云东平", "嘉禾望岗", "彭边", "鹤龙", "鹤边",
    "马务", "新市墟", "云霄路", "乐嘉路",
    
    # 18号线
    "冼村", "磨碟沙", "龙潭", "沙溪", "南村万博", "番禺广场", "横沥", "万顷沙",
    
    # 21号线
    "天河公园", "棠东", "黄村", "大观南路", "天河智慧城", "神舟路", "科学城",
    "苏元", "水西", "长平", "金坑", "镇龙西", "镇龙", "中新", "坑贝",
    "凤岗", "朱村", "山田", "钟岗", "增城广场",
    
    # 22号线
    "芳村", "西塱", "南漖", "南浦西", "陈头岗", "广州南站", "市广路", "祈福新邨", "番禺广场",
    
    # APM线
    "林和西", "体育中心南", "天河南", "黄埔大道", "妇儿中心", "花城大道", "大剧院", "海心沙", "广州塔",
    
    # 广佛线
    "新城东", "东平", "世纪莲", "澜石", "魁奇路", "季华园", "同济路", "祖庙",
    "普君北路", "朝安", "桂城", "南桂路", "𧒽岗", "千灯湖", "金融高新区",
    "龙溪", "菊树", "西塱", "鹤洞", "沙涌", "沙园", "燕岗", "石溪", "南洲", "沥滘"
]


# ==================== 核心功能 ====================

def log(message: str):
    """打印日志"""
    print(f"[{datetime.now().strftime('%H:%M:%S')}] {message}")


def geocode_station(station_name: str) -> Optional[Dict]:
    """
    使用高德地图地理编码 API 查询站点经纬度
    
    Args:
        station_name: 站点名称
        
    Returns:
        包含经纬度的字典，或 None（查询失败）
    """
    url = "https://restapi.amap.com/v3/geocode/geo"
    
    # 构建完整地址
    address = f"广州市{station_name}地铁站"
    
    params = {
        'key': AMAP_KEY,
        'address': address,
        'city': '广州',
        'output': 'JSON'
    }
    
    for attempt in range(MAX_RETRIES):
        try:
            response = requests.get(url, params=params, timeout=10)
            data = response.json()
            
            # 调试：打印 API 返回
            if data.get('status') != '1':
                log(f"  API 错误: {data.get('info', '未知错误')}")
                log(f"  完整响应: {data}")
                return None
            
            if data.get('geocodes'):
                location = data['geocodes'][0].get('location', '')
                if location:
                    lng, lat = location.split(',')
                    return {
                        'name': station_name,
                        'lng': float(lng),
                        'lat': float(lat),
                        'formatted_address': data['geocodes'][0].get('formatted_address', ''),
                        'level': data['geocodes'][0].get('level', '')
                    }
            
            log(f"  警告: {station_name} 未找到坐标")
            return None
            
        except Exception as e:
            if attempt < MAX_RETRIES - 1:
                log(f"  重试 {station_name} ({attempt + 1}/{MAX_RETRIES}): {str(e)}")
                time.sleep(1)
            else:
                log(f"  错误: {station_name} 查询失败 - {str(e)}")
                return None
    
    return None


def batch_geocode_stations(stations: List[str]) -> Dict:
    """
    批量查询站点经纬度
    
    Args:
        stations: 站点名称列表
        
    Returns:
        查询结果字典
    """
    log("=" * 60)
    log("开始批量查询地铁站经纬度")
    log(f"共 {len(stations)} 个站点")
    log("=" * 60)
    
    results = {
        'metadata': {
            'total': len(stations),
            'success': 0,
            'failed': 0,
            'query_date': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
            'data_source': '高德地图'
        },
        'stations': {},
        'failed_stations': []
    }
    
    for idx, station in enumerate(stations, 1):
        log(f"[{idx}/{len(stations)}] 查询: {station}")
        
        result = geocode_station(station)
        
        if result:
            results['stations'][station] = {
                'lng': result['lng'],
                'lat': result['lat'],
                'address': result['formatted_address']
            }
            results['metadata']['success'] += 1
            log(f"  ✓ 成功: ({result['lng']:.6f}, {result['lat']:.6f})")
        else:
            results['failed_stations'].append(station)
            results['metadata']['failed'] += 1
        
        # 延时避免触发频率限制
        if idx < len(stations):
            time.sleep(REQUEST_DELAY)
    
    return results


def save_results(results: Dict, output_dir: str):
    """
    保存查询结果到 JSON 文件
    
    Args:
        results: 查询结果字典
        output_dir: 输出目录
    """
    os.makedirs(output_dir, exist_ok=True)
    
    # 主结果文件
    json_file = os.path.join(output_dir, 'station_coords.json')
    with open(json_file, 'w', encoding='utf-8') as f:
        json.dump(results, f, ensure_ascii=False, indent=2)
    log(f"\n结果已保存: {json_file}")
    
    # 简化的坐标文件（仅站点名和坐标）
    simple_coords = {}
    for name, data in results['stations'].items():
        simple_coords[name] = [data['lng'], data['lat']]
    
    simple_file = os.path.join(output_dir, 'station_coords_simple.json')
    with open(simple_file, 'w', encoding='utf-8') as f:
        json.dump(simple_coords, f, ensure_ascii=False, indent=2)
    log(f"简化版已保存: {simple_file}")
    
    # 失败站点列表
    if results['failed_stations']:
        failed_file = os.path.join(output_dir, 'failed_stations.json')
        with open(failed_file, 'w', encoding='utf-8') as f:
            json.dump(results['failed_stations'], f, ensure_ascii=False, indent=2)
        log(f"失败列表已保存: {failed_file}")
    
    # CSV 格式
    csv_file = os.path.join(output_dir, 'station_coords.csv')
    with open(csv_file, 'w', encoding='utf-8-sig') as f:
        f.write("站点名称,经度,纬度,地址\n")
        for name, data in results['stations'].items():
            address = data.get('address', '').replace(',', ' ')
            f.write(f"{name},{data['lng']},{data['lat']},{address}\n")
    log(f"CSV 已保存: {csv_file}")


def print_summary(results: Dict):
    """打印查询摘要"""
    log("\n" + "=" * 60)
    log("查询完成摘要")
    log("=" * 60)
    log(f"总站点数: {results['metadata']['total']}")
    log(f"成功: {results['metadata']['success']}")
    log(f"失败: {results['metadata']['failed']}")
    log(f"成功率: {results['metadata']['success'] / results['metadata']['total'] * 100:.1f}%")
    
    if results['failed_stations']:
        log("\n失败站点列表:")
        for station in results['failed_stations']:
            log(f"  - {station}")
    
    log("=" * 60)


def main():
    """主函数"""
    # 检查 API Key
    if AMAP_KEY == "你的高德地图Web服务API_Key":
        log("错误: 请先配置 AMAP_KEY！")
        log("请修改脚本中的 AMAP_KEY 变量为你的高德地图 Web服务 API Key")
        return
    
    # 批量查询
    results = batch_geocode_stations(GUANGZHOU_METRO_STATIONS)
    
    # 保存结果
    save_results(results, OUTPUT_DIR)
    
    # 打印摘要
    print_summary(results)


if __name__ == "__main__":
    main()
