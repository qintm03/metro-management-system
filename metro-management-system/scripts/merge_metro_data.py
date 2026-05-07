import json
import os
import re
from datetime import datetime

def load_json_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        return json.load(f)

def save_json_file(filepath, data):
    with open(filepath, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

def parse_line_name(filename):
    """从文件名解析线路名称"""
    # 移除日期部分
    name = re.sub(r'_\d{4}-\d{2}-\d{2}\.json$', '', filename)
    return name

def is_line12_segment(name):
    """判断是否是12号线的分段"""
    return '12号线' in name

def merge_line12_segments(segments):
    """合并12号线的三个分段"""
    if not segments:
        return None
    
    # 确定顺序：东段(大学城南--二沙岛) -> 中段(二沙岛--广州体育馆) -> 西段(广州体育馆--浔峰岗)
    # 按照起点和终点匹配
    
    # 找到东段（起点是大学城南）
    east_segment = None
    middle_segment = None
    west_segment = None
    
    for seg in segments:
        line_name = seg['line']['name']
        if '大学城南' in line_name and '二沙岛' in line_name:
            east_segment = seg
        elif '二沙岛' in line_name and '广州体育馆' in line_name:
            middle_segment = seg
        elif '广州体育馆' in line_name and '浔峰岗' in line_name:
            west_segment = seg
    
    if not all([east_segment, middle_segment, west_segment]):
        print("警告：未能正确识别12号线的三个分段")
        return None
    
    # 合并路径和站点
    # 顺序：东段 -> 中段 -> 西段
    
    # 合并path（去除重复点）
    merged_path = []
    
    # 添加东段的所有path点
    merged_path.extend(east_segment['line']['path'])
    
    # 添加中段path（跳过第一个点，因为它与东段最后一个点重复）
    middle_path = middle_segment['line']['path']
    if len(middle_path) > 1:
        merged_path.extend(middle_path[1:])
    
    # 添加西段path（跳过第一个点，因为它与中段最后一个点重复）
    west_path = west_segment['line']['path']
    if len(west_path) > 1:
        merged_path.extend(west_path[1:])
    
    # 合并站点（去除重复站点）
    all_stations = []
    seen_stations = set()
    
    for seg in [east_segment, middle_segment, west_segment]:
        for station in seg['line']['stations']:
            if station['name'] not in seen_stations:
                seen_stations.add(station['name'])
                all_stations.append(station)
    
    # 重新排序站点
    for i, station in enumerate(all_stations, 1):
        station['sequence'] = i
    
    # 计算总距离
    total_distance = sum([
        float(east_segment['line']['distance']),
        float(middle_segment['line']['distance']),
        float(west_segment['line']['distance'])
    ])
    
    # 构建合并后的线路数据
    merged_line = {
        'city': east_segment['city'],
        'queryTime': datetime.now().isoformat() + 'Z',
        'line': {
            'name': '地铁12号线(大学城南--浔峰岗)',
            'id': '900000226818',  # 使用东段的ID
            'startStation': '大学城南',
            'endStation': '浔峰岗',
            'distance': f'{total_distance:.6f}',
            'stationCount': len(all_stations),
            'path': merged_path,
            'stations': all_stations
        }
    }
    
    return merged_line

def convert_to_merged_format(data):
    """将单个线路数据转换为合并格式"""
    line = data['line']
    return {
        'lineId': line.get('id', ''),
        'lineName': line['name'],
        'startStation': line['startStation'],
        'endStation': line['endStation'],
        'distance': line['distance'],
        'stationCount': line['stationCount'],
        'path': line['path'],
        'stations': line['stations']
    }

def main():
    data_dir = r'e:\毕设\metro-management-system\scripts\output\数据'
    output_file = r'e:\毕设\metro-management-system\scripts\output\merged_metro_data.json'
    
    # 获取所有JSON文件
    json_files = [f for f in os.listdir(data_dir) if f.endswith('.json')]
    
    # 分类文件
    line12_segments = []
    other_lines = []
    
    for filename in json_files:
        filepath = os.path.join(data_dir, filename)
        data = load_json_file(filepath)
        line_name = data['line']['name']
        
        if is_line12_segment(line_name):
            line12_segments.append(data)
        else:
            other_lines.append(data)
    
    print(f"找到 {len(line12_segments)} 个12号线分段")
    print(f"找到 {len(other_lines)} 条其他线路")
    
    # 合并所有线路
    all_lines = []
    
    # 处理12号线
    if line12_segments:
        merged_line12 = merge_line12_segments(line12_segments)
        if merged_line12:
            all_lines.append(convert_to_merged_format(merged_line12))
            print("12号线分段已合并")
    
    # 处理其他线路
    for data in other_lines:
        all_lines.append(convert_to_merged_format(data))
    
    # 构建最终输出
    output = {
        'city': '广州',
        'queryTime': datetime.now().isoformat() + 'Z',
        'totalLines': len(all_lines),
        'lines': all_lines
    }
    
    # 保存结果
    save_json_file(output_file, output)
    print(f"合并完成！共 {len(all_lines)} 条线路")
    print(f"输出文件：{output_file}")
    
    # 打印线路列表
    print("\n线路列表：")
    for line in all_lines:
        print(f"  - {line['lineName']} ({line['stationCount']}站)")

if __name__ == '__main__':
    main()
