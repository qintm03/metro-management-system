import json
import os

def load_json_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        return json.load(f)

def escape_sql_string(value):
    """转义SQL字符串"""
    if value is None:
        return 'NULL'
    return "'" + str(value).replace("'", "''").replace("\\", "\\\\") + "'"

def generate_sql():
    input_file = r'e:\毕设\metro-management-system\scripts\output\merged_metro_data_with_transfer.json'
    output_file = r'e:\毕设\metro-management-system\scripts\output\guangzhou_metro_data.sql'
    
    data = load_json_file(input_file)
    
    sql_lines = []
    
    # 添加文件头
    sql_lines.append("-- 广州地铁数据SQL脚本")
    sql_lines.append("-- 生成时间: 2026-05-05")
    sql_lines.append("-- 数据库: MySQL")
    sql_lines.append("")
    sql_lines.append("SET NAMES utf8mb4;")
    sql_lines.append("SET FOREIGN_KEY_CHECKS = 0;")
    sql_lines.append("")
    
    # 1. 创建 metro_line 表
    sql_lines.append("-- ----------------------------")
    sql_lines.append("-- Table structure for metro_line")
    sql_lines.append("-- ----------------------------")
    sql_lines.append("DROP TABLE IF EXISTS `metro_line`;")
    sql_lines.append("""CREATE TABLE `metro_line` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `line_id` VARCHAR(50) NOT NULL COMMENT '线路唯一标识',
  `line_name` VARCHAR(100) NOT NULL COMMENT '线路名称',
  `line_code` VARCHAR(20) DEFAULT NULL COMMENT '线路编号（如：1号线、2号线）',
  `start_station` VARCHAR(100) NOT NULL COMMENT '起点站名称',
  `end_station` VARCHAR(100) NOT NULL COMMENT '终点站名称',
  `distance` DECIMAL(10,6) DEFAULT NULL COMMENT '线路总长度（公里）',
  `station_count` INT DEFAULT NULL COMMENT '站点总数',
  `path` JSON DEFAULT NULL COMMENT '线路坐标路径（经纬度数组）',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-未开通，1-已开通，2-建设中',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_line_id` (`line_id`),
  KEY `idx_line_name` (`line_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地铁线路表';""")
    sql_lines.append("")
    
    # 2. 创建 metro_station 表
    sql_lines.append("-- ----------------------------")
    sql_lines.append("-- Table structure for metro_station")
    sql_lines.append("-- ----------------------------")
    sql_lines.append("DROP TABLE IF EXISTS `metro_station`;")
    sql_lines.append("""CREATE TABLE `metro_station` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `station_name` VARCHAR(100) NOT NULL COMMENT '站点名称',
  `line_id` VARCHAR(50) NOT NULL COMMENT '所属线路ID',
  `sequence` INT NOT NULL COMMENT '站点在线路中的顺序',
  `longitude` DECIMAL(12,8) DEFAULT NULL COMMENT '经度',
  `latitude` DECIMAL(12,8) DEFAULT NULL COMMENT '纬度',
  `is_transfer` TINYINT DEFAULT 0 COMMENT '是否换乘站：0-否，1-是',
  `transfer_count` INT DEFAULT 0 COMMENT '可换乘线路数量',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-未开通，1-已开通',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_station_name` (`station_name`),
  KEY `idx_line_id` (`line_id`),
  KEY `idx_sequence` (`line_id`,`sequence`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地铁站点表';""")
    sql_lines.append("")
    
    # 3. 创建 metro_transfer 表
    sql_lines.append("-- ----------------------------")
    sql_lines.append("-- Table structure for metro_transfer")
    sql_lines.append("-- ----------------------------")
    sql_lines.append("DROP TABLE IF EXISTS `metro_transfer`;")
    sql_lines.append("""CREATE TABLE `metro_transfer` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `station_name` VARCHAR(100) NOT NULL COMMENT '站点名称',
  `from_line_id` VARCHAR(50) NOT NULL COMMENT '当前线路ID',
  `to_line_id` VARCHAR(50) NOT NULL COMMENT '可换乘线路ID',
  `to_line_name` VARCHAR(100) DEFAULT NULL COMMENT '可换乘线路名称',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-不可用，1-可用',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_station_name` (`station_name`),
  KEY `idx_from_line` (`from_line_id`),
  KEY `idx_to_line` (`to_line_id`),
  UNIQUE KEY `uk_transfer` (`station_name`,`from_line_id`,`to_line_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地铁换乘关系表';""")
    sql_lines.append("")
    
    # 收集所有数据
    line_values = []
    station_values = []
    transfer_values = []
    
    for line in data['lines']:
        line_id = line.get('lineId', '')
        line_name = line.get('lineName', '')
        start_station = line.get('startStation', '')
        end_station = line.get('endStation', '')
        distance = line.get('distance', '0')
        station_count = line.get('stationCount', 0)
        path = line.get('path', [])
        
        # 提取线路编号
        line_code = ''
        if '1号线' in line_name:
            line_code = '1号线'
        elif '2号线' in line_name:
            line_code = '2号线'
        elif '3号线' in line_name and '北延段' in line_name:
            line_code = '3号线北延段'
        elif '3号线' in line_name:
            line_code = '3号线'
        elif '4号线' in line_name:
            line_code = '4号线'
        elif '5号线' in line_name:
            line_code = '5号线'
        elif '6号线' in line_name:
            line_code = '6号线'
        elif '7号线' in line_name:
            line_code = '7号线'
        elif '8号线' in line_name:
            line_code = '8号线'
        elif '9号线' in line_name:
            line_code = '9号线'
        elif '10号线' in line_name:
            line_code = '10号线'
        elif '11号线' in line_name:
            line_code = '11号线'
        elif '12号线' in line_name:
            line_code = '12号线'
        elif '13号线' in line_name:
            line_code = '13号线'
        elif '14号线' in line_name and '支线' in line_name:
            line_code = '14号线支线'
        elif '14号线' in line_name:
            line_code = '14号线'
        elif '18号线' in line_name:
            line_code = '18号线'
        elif '21号线' in line_name:
            line_code = '21号线'
        elif '22号线' in line_name:
            line_code = '22号线'
        elif 'APM线' in line_name:
            line_code = 'APM线'
        elif '广佛线' in line_name:
            line_code = '广佛线'
        
        # 构建line表的值
        path_json = json.dumps(path, ensure_ascii=False)
        line_val = f"({escape_sql_string(line_id)}, {escape_sql_string(line_name)}, {escape_sql_string(line_code)}, {escape_sql_string(start_station)}, {escape_sql_string(end_station)}, {distance}, {station_count}, {escape_sql_string(path_json)})"
        line_values.append(line_val)
        
        # 处理站点
        for station in line.get('stations', []):
            station_name = station.get('name', '')
            sequence = station.get('sequence', 0)
            location = station.get('location', [None, None])
            longitude = location[0] if len(location) > 0 else None
            latitude = location[1] if len(location) > 1 else None
            transfer_lines = station.get('transferLines', [])
            is_transfer = 1 if transfer_lines else 0
            transfer_count = len(transfer_lines)
            
            station_val = f"({escape_sql_string(station_name)}, {escape_sql_string(line_id)}, {sequence}, {longitude if longitude else 'NULL'}, {latitude if latitude else 'NULL'}, {is_transfer}, {transfer_count})"
            station_values.append(station_val)
            
            # 处理换乘关系
            for transfer in transfer_lines:
                to_line_id = transfer.get('lineId', '')
                to_line_name = transfer.get('lineName', '')
                
                transfer_val = f"({escape_sql_string(station_name)}, {escape_sql_string(line_id)}, {escape_sql_string(to_line_id)}, {escape_sql_string(to_line_name)})"
                transfer_values.append(transfer_val)
    
    # 4. 插入 metro_line 数据
    sql_lines.append("-- ----------------------------")
    sql_lines.append("-- Records of metro_line")
    sql_lines.append("-- ----------------------------")
    if line_values:
        batch_size = 50
        for i in range(0, len(line_values), batch_size):
            batch = line_values[i:i+batch_size]
            sql_lines.append(f"INSERT INTO `metro_line` (`line_id`, `line_name`, `line_code`, `start_station`, `end_station`, `distance`, `station_count`, `path`) VALUES")
            sql_lines.append(",\n".join(batch) + ";")
    sql_lines.append("")
    
    # 5. 插入 metro_station 数据
    sql_lines.append("-- ----------------------------")
    sql_lines.append("-- Records of metro_station")
    sql_lines.append("-- ----------------------------")
    if station_values:
        batch_size = 100
        for i in range(0, len(station_values), batch_size):
            batch = station_values[i:i+batch_size]
            sql_lines.append(f"INSERT INTO `metro_station` (`station_name`, `line_id`, `sequence`, `longitude`, `latitude`, `is_transfer`, `transfer_count`) VALUES")
            sql_lines.append(",\n".join(batch) + ";")
    sql_lines.append("")
    
    # 6. 插入 metro_transfer 数据
    sql_lines.append("-- ----------------------------")
    sql_lines.append("-- Records of metro_transfer")
    sql_lines.append("-- ----------------------------")
    if transfer_values:
        batch_size = 100
        for i in range(0, len(transfer_values), batch_size):
            batch = transfer_values[i:i+batch_size]
            sql_lines.append(f"INSERT INTO `metro_transfer` (`station_name`, `from_line_id`, `to_line_id`, `to_line_name`) VALUES")
            sql_lines.append(",\n".join(batch) + ";")
    sql_lines.append("")
    
    # 添加文件尾
    sql_lines.append("SET FOREIGN_KEY_CHECKS = 1;")
    sql_lines.append("")
    
    # 写入文件
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write('\n'.join(sql_lines))
    
    print(f"SQL脚本已生成：{output_file}")
    print(f"- 线路数量：{len(line_values)}")
    print(f"- 站点数量：{len(station_values)}")
    print(f"- 换乘关系数量：{len(transfer_values)}")

if __name__ == '__main__':
    generate_sql()
