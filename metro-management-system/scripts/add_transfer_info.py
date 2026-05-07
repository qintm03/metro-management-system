import json
import os

def load_json_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        return json.load(f)

def save_json_file(filepath, data):
    with open(filepath, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

# 广州地铁换乘站数据 - 基于搜索结果整理
TRANSFER_STATIONS = {
    # 1号线换乘站
    "西塱": [{"lineId": "900000099788", "lineName": "地铁12号线(广州体育馆--浔峰岗)", "stationName": "西塱"}, {"lineId": "", "lineName": "广佛线", "stationName": "西塱"}],
    "黄沙": [{"lineId": "", "lineName": "地铁6号线", "stationName": "黄沙"}],
    "陈家祠": [{"lineId": "", "lineName": "地铁8号线", "stationName": "陈家祠"}],
    "公园前": [{"lineId": "", "lineName": "地铁2号线", "stationName": "公园前"}],
    "东山口": [{"lineId": "", "lineName": "地铁6号线", "stationName": "东山口"}],
    "杨箕": [{"lineId": "", "lineName": "地铁5号线", "stationName": "杨箕"}],
    "体育西路": [{"lineId": "", "lineName": "地铁3号线", "stationName": "体育西路"}, {"lineId": "", "lineName": "地铁3号线北延段", "stationName": "体育西路"}],
    "广州东站": [{"lineId": "", "lineName": "地铁3号线北延段", "stationName": "广州东站"}, {"lineId": "", "lineName": "地铁11号线", "stationName": "广州东站"}],
    
    # 2号线换乘站
    "嘉禾望岗": [{"lineId": "", "lineName": "地铁3号线北延段", "stationName": "嘉禾望岗"}, {"lineId": "", "lineName": "地铁14号线", "stationName": "嘉禾望岗"}],
    "白云文化广场": [{"lineId": "900000099788", "lineName": "地铁12号线(广州体育馆--浔峰岗)", "stationName": "白云文化广场"}],
    "广州火车站": [{"lineId": "", "lineName": "地铁5号线", "stationName": "广州火车站"}],
    "海珠广场": [{"lineId": "", "lineName": "地铁6号线", "stationName": "海珠广场"}],
    "昌岗": [{"lineId": "", "lineName": "地铁8号线", "stationName": "昌岗"}],
    "江泰路": [{"lineId": "", "lineName": "地铁11号线", "stationName": "江泰路"}],
    "东晓南": [{"lineId": "", "lineName": "地铁10号线", "stationName": "东晓南"}],
    "南洲": [{"lineId": "", "lineName": "广佛线", "stationName": "南洲"}],
    "石壁": [{"lineId": "", "lineName": "地铁7号线", "stationName": "石壁"}],
    "广州南站": [{"lineId": "", "lineName": "地铁7号线", "stationName": "广州南站"}, {"lineId": "", "lineName": "地铁22号线", "stationName": "广州南站"}, {"lineId": "", "lineName": "佛山2号线", "stationName": "广州南站"}],
    
    # 3号线换乘站
    "高增": [{"lineId": "", "lineName": "地铁9号线", "stationName": "高增"}],
    "燕塘": [{"lineId": "", "lineName": "地铁6号线", "stationName": "燕塘"}],
    "林和西": [{"lineId": "", "lineName": "APM线", "stationName": "林和西"}],
    "天河客运站": [{"lineId": "", "lineName": "地铁6号线", "stationName": "天河客运站"}],
    "华师": [{"lineId": "", "lineName": "地铁11号线", "stationName": "华师"}],
    "珠江新城": [{"lineId": "", "lineName": "地铁5号线", "stationName": "珠江新城"}],
    "广州塔": [{"lineId": "", "lineName": "APM线", "stationName": "广州塔"}],
    "客村": [{"lineId": "", "lineName": "地铁8号线", "stationName": "客村"}],
    "大塘": [{"lineId": "", "lineName": "地铁11号线", "stationName": "大塘"}],
    "沥滘": [{"lineId": "", "lineName": "广佛线", "stationName": "沥滘"}],
    "汉溪长隆": [{"lineId": "", "lineName": "地铁7号线", "stationName": "汉溪长隆"}],
    "番禺广场": [{"lineId": "", "lineName": "地铁18号线", "stationName": "番禺广场"}, {"lineId": "", "lineName": "地铁22号线", "stationName": "番禺广场"}],
    "海傍": [{"lineId": "", "lineName": "地铁4号线", "stationName": "海傍"}],
    
    # 3号线北延段换乘站（与3号线共享部分站点）
    "机场北(2号航站楼)": [],
    "机场南(1号航站楼)": [],
    
    # 4号线换乘站
    "车陂": [{"lineId": "", "lineName": "地铁13号线", "stationName": "车陂"}],
    "车陂南": [{"lineId": "", "lineName": "地铁5号线", "stationName": "车陂南"}],
    "万胜围": [{"lineId": "", "lineName": "地铁8号线", "stationName": "万胜围"}],
    "大学城南": [{"lineId": "", "lineName": "地铁4号线", "stationName": "大学城南"}, {"lineId": "", "lineName": "地铁7号线", "stationName": "大学城南"}, {"lineId": "900000226818", "lineName": "地铁12号线(大学城南--浔峰岗)", "stationName": "大学城南"}],
    "大学城北": [{"lineId": "", "lineName": "地铁4号线", "stationName": "大学城北"}, {"lineId": "900000226818", "lineName": "地铁12号线(大学城南--浔峰岗)", "stationName": "大学城北"}],
    "官洲": [{"lineId": "", "lineName": "地铁4号线", "stationName": "官洲"}, {"lineId": "900000226818", "lineName": "地铁12号线(大学城南--浔峰岗)", "stationName": "官洲"}],
    "黄村": [{"lineId": "", "lineName": "地铁21号线", "stationName": "黄村"}],
    "裕丰围": [{"lineId": "", "lineName": "地铁13号线", "stationName": "裕丰围"}],
    "南沙客运港": [],
    
    # 5号线换乘站
    "滘口": [],
    "坦尾": [{"lineId": "", "lineName": "地铁6号线", "stationName": "坦尾"}],
    "西村": [{"lineId": "", "lineName": "地铁8号线", "stationName": "西村"}],
    "淘金": [{"lineId": "", "lineName": "地铁12号线", "stationName": "建设六马路"}],  # 建设中
    "区庄": [{"lineId": "", "lineName": "地铁6号线", "stationName": "区庄"}],
    "五羊邨": [{"lineId": "", "lineName": "地铁10号线", "stationName": "五羊邨"}],
    "猎德": [],
    "潭村": [],
    "员村": [{"lineId": "", "lineName": "地铁21号线", "stationName": "员村"}],
    "科韵路": [],
    "东圃": [],
    "三溪": [],
    "鱼珠": [{"lineId": "", "lineName": "地铁13号线", "stationName": "鱼珠"}],
    "大沙地": [],
    "大沙东": [{"lineId": "", "lineName": "地铁7号线", "stationName": "大沙东"}],
    "文冲": [],
    "双沙": [],
    "庙头": [],
    "夏园": [{"lineId": "", "lineName": "地铁13号线", "stationName": "夏园"}],
    "保盈大道": [],
    "夏港": [],
    "黄埔新港": [],
    
    # 6号线换乘站
    "浔峰岗": [{"lineId": "900000099788", "lineName": "地铁12号线(广州体育馆--浔峰岗)", "stationName": "浔峰岗"}],
    "如意坊": [],
    "文化公园": [{"lineId": "", "lineName": "地铁8号线", "stationName": "文化公园"}],
    "一德路": [],
    "北京路": [],
    "团一大广场": [],
    "东湖": [{"lineId": "", "lineName": "地铁10号线", "stationName": "东湖"}, {"lineId": "900000226818", "lineName": "地铁12号线(大学城南--浔峰岗)", "stationName": "东湖"}],
    "黄花岗": [],
    "沙河": [{"lineId": "", "lineName": "地铁11号线", "stationName": "沙河"}],
    "长湴": [],
    "植物园": [],
    "龙洞": [],
    "柯木塱": [],
    "高塘石": [],
    "黄陂": [],
    "金峰": [],
    "暹岗": [],
    "苏元": [{"lineId": "", "lineName": "地铁21号线", "stationName": "苏元"}],
    "萝岗": [{"lineId": "", "lineName": "地铁7号线", "stationName": "萝岗"}],
    "香雪": [],
    
    # 7号线换乘站
    "美的大道": [],
    "北滘公园": [{"lineId": "", "lineName": "佛山3号线", "stationName": "北滘公园"}],
    "美的大道": [],
    "汉溪长隆": [{"lineId": "", "lineName": "地铁3号线", "stationName": "汉溪长隆"}],
    "南村万博": [{"lineId": "", "lineName": "地铁18号线", "stationName": "南村万博"}],
    "大学城南": [{"lineId": "", "lineName": "地铁4号线", "stationName": "大学城南"}, {"lineId": "900000226818", "lineName": "地铁12号线(大学城南--浔峰岗)", "stationName": "大学城南"}],
    "燕山": [],
    
    # 8号线换乘站
    "滘心": [],
    "聚龙": [{"lineId": "900000099788", "lineName": "地铁12号线(广州体育馆--浔峰岗)", "stationName": "聚龙"}],
    "石井": [],
    "小坪": [],
    "石潭": [{"lineId": "", "lineName": "地铁12号线", "stationName": "广州白云站"}],
    "鹅掌坦": [],
    "同德": [],
    "上步": [],
    "横滘": [],
    "同福西": [],
    "华林寺": [],
    "同福西": [],
    "凤凰新村": [],
    "沙园": [{"lineId": "", "lineName": "广佛线", "stationName": "沙园"}],
    "宝岗大道": [],
    "晓港": [],
    "中大": [],
    "鹭江": [],
    "赤岗": [{"lineId": "900000226818", "lineName": "地铁12号线(大学城南--浔峰岗)", "stationName": "赤岗"}],
    "磨碟沙": [{"lineId": "", "lineName": "地铁18号线", "stationName": "磨碟沙"}],
    "新港东": [],
    "琶洲": [{"lineId": "", "lineName": "地铁11号线", "stationName": "琶洲"}, {"lineId": "", "lineName": "广佛环线", "stationName": "琶洲"}],
    
    # 9号线换乘站
    "飞鹅岭": [],
    "花都汽车城": [],
    "广州北站": [],
    "花城路": [],
    "花果山公园": [],
    "花都广场": [],
    "马鞍山公园": [],
    "莲塘": [],
    "清㘵": [],
    "清塘": [],
    "高增": [{"lineId": "", "lineName": "地铁3号线北延段", "stationName": "高增"}],
    
    # 10号线换乘站
    "西塱": [{"lineId": "", "lineName": "地铁1号线", "stationName": "西塱"}, {"lineId": "", "lineName": "广佛线", "stationName": "西塱"}, {"lineId": "900000099788", "lineName": "地铁12号线(广州体育馆--浔峰岗)", "stationName": "西塱"}],
    "东湖": [{"lineId": "", "lineName": "地铁6号线", "stationName": "东湖"}, {"lineId": "900000226818", "lineName": "地铁12号线(大学城南--浔峰岗)", "stationName": "东湖"}],
    "五凤": [{"lineId": "", "lineName": "地铁11号线", "stationName": "五凤"}],
    
    # 11号线换乘站（环线）
    "赤沙": [{"lineId": "900000226818", "lineName": "地铁12号线(大学城南--浔峰岗)", "stationName": "赤沙"}],
    "龙潭": [{"lineId": "", "lineName": "地铁18号线", "stationName": "龙潭"}],
    "上涌": [],
    "大塘": [{"lineId": "", "lineName": "地铁3号线", "stationName": "大塘"}],
    "海珠湿地": [],
    "仑头": [{"lineId": "900000226818", "lineName": "地铁12号线(大学城南--浔峰岗)", "stationName": "仑头"}],
    "赤沙": [{"lineId": "900000226818", "lineName": "地铁12号线(大学城南--浔峰岗)", "stationName": "赤沙"}],
    "琶洲": [{"lineId": "", "lineName": "地铁8号线", "stationName": "琶洲"}],
    "员村": [{"lineId": "", "lineName": "地铁5号线", "stationName": "员村"}],
    "天河公园": [{"lineId": "", "lineName": "地铁21号线", "stationName": "天河公园"}, {"lineId": "", "lineName": "地铁13号线", "stationName": "天河公园"}],
    "华景路": [],
    "华师": [{"lineId": "", "lineName": "地铁3号线", "stationName": "华师"}],
    "龙口西": [],
    "沙河": [{"lineId": "", "lineName": "地铁6号线", "stationName": "沙河"}],
    "云台花园": [],
    "大金钟": [],
    "中医药大学": [{"lineId": "900000226818", "lineName": "地铁12号线(大学城南--浔峰岗)", "stationName": "中医药大学"}],
    "梓元岗": [],
    "广州火车站": [{"lineId": "", "lineName": "地铁2号线", "stationName": "广州火车站"}, {"lineId": "", "lineName": "地铁5号线", "stationName": "广州火车站"}, {"lineId": "", "lineName": "地铁14号线", "stationName": "广州火车站"}],
    "流花": [],
    "彩虹桥": [{"lineId": "", "lineName": "地铁8号线", "stationName": "彩虹桥"}, {"lineId": "", "lineName": "地铁13号线", "stationName": "彩虹桥"}, {"lineId": "", "lineName": "地铁22号线", "stationName": "彩虹桥"}],
    "中山八": [{"lineId": "", "lineName": "地铁5号线", "stationName": "中山八"}],
    "如意坊": [{"lineId": "", "lineName": "地铁6号线", "stationName": "如意坊"}],
    "石围塘": [],
    "芳村": [{"lineId": "", "lineName": "地铁1号线", "stationName": "芳村"}, {"lineId": "", "lineName": "地铁22号线", "stationName": "芳村"}, {"lineId": "", "lineName": "地铁28号线", "stationName": "芳村"}],
    "大冲口": [],
    "沙涌": [{"lineId": "", "lineName": "广佛线", "stationName": "沙涌"}],
    "鹤洞东": [],
    "棣园": [],
    "燕岗": [{"lineId": "", "lineName": "广佛线", "stationName": "燕岗"}],
    "江泰路": [{"lineId": "", "lineName": "地铁2号线", "stationName": "江泰路"}],
    "五凤": [{"lineId": "", "lineName": "地铁10号线", "stationName": "五凤"}],
    "逸景路": [],
    "上涌": [],
    "大塘": [{"lineId": "", "lineName": "地铁3号线", "stationName": "大塘"}],
    
    # 12号线换乘站（东段+西段已开通）
    "浔峰岗": [{"lineId": "", "lineName": "地铁6号线", "stationName": "浔峰岗"}],
    "聚龙": [{"lineId": "", "lineName": "地铁8号线", "stationName": "聚龙"}],
    "广州白云站": [{"lineId": "", "lineName": "地铁22号线", "stationName": "广州白云站"}],
    "新市墟": [{"lineId": "", "lineName": "地铁14号线", "stationName": "新市墟"}],
    "白云文化广场": [{"lineId": "", "lineName": "地铁2号线", "stationName": "白云文化广场"}],
    "中医药大学": [{"lineId": "", "lineName": "地铁11号线", "stationName": "中医药大学"}],
    "烈士陵园": [{"lineId": "", "lineName": "地铁1号线", "stationName": "烈士陵园"}],
    "东湖": [{"lineId": "", "lineName": "地铁6号线", "stationName": "东湖"}, {"lineId": "", "lineName": "地铁10号线", "stationName": "东湖"}],
    "二沙岛": [],
    "赤岗": [{"lineId": "", "lineName": "地铁8号线", "stationName": "赤岗"}],
    "赤沙": [{"lineId": "", "lineName": "地铁11号线", "stationName": "赤沙"}],
    "官洲": [{"lineId": "", "lineName": "地铁4号线", "stationName": "官洲"}],
    "大学城北": [{"lineId": "", "lineName": "地铁4号线", "stationName": "大学城北"}],
    "大学城南": [{"lineId": "", "lineName": "地铁4号线", "stationName": "大学城南"}, {"lineId": "", "lineName": "地铁7号线", "stationName": "大学城南"}],
    
    # 13号线换乘站
    "新沙": [],
    "官湖": [],
    "新塘": [{"lineId": "", "lineName": "地铁16号线", "stationName": "新塘"}],
    "白江": [],
    "沙村": [],
    "南岗": [],
    "夏园": [{"lineId": "", "lineName": "地铁5号线", "stationName": "夏园"}],
    "南海神庙": [],
    "双岗": [],
    "裕丰围": [{"lineId": "", "lineName": "地铁7号线", "stationName": "裕丰围"}],
    "鱼珠": [{"lineId": "", "lineName": "地铁5号线", "stationName": "鱼珠"}],
    "珠村": [],
    "天河公园": [{"lineId": "", "lineName": "地铁11号线", "stationName": "天河公园"}, {"lineId": "", "lineName": "地铁21号线", "stationName": "天河公园"}],
    
    # 14号线换乘站
    "东风": [],
    "从化客运站": [],
    "赤草": [],
    "神岗": [],
    "太平": [],
    "新和": [{"lineId": "", "lineName": "地铁14号线支线(知识城线)", "stationName": "新和"}],
    "马沥": [],
    "新南": [],
    "枫下": [],
    "知识城": [],
    "何棠下": [],
    "旺村": [],
    "汤村": [],
    "镇龙北": [],
    "镇龙": [{"lineId": "", "lineName": "地铁21号线", "stationName": "镇龙"}],
    "嘉禾望岗": [{"lineId": "", "lineName": "地铁2号线", "stationName": "嘉禾望岗"}, {"lineId": "", "lineName": "地铁3号线北延段", "stationName": "嘉禾望岗"}],
    "白云东平": [],
    "夏良": [],
    "太和": [],
    "竹料": [],
    "钟落潭": [],
    "马沥": [],
    "新和": [{"lineId": "", "lineName": "地铁14号线支线(知识城线)", "stationName": "新和"}],
    "红卫": [],
    "新南": [],
    "枫下": [],
    "知识城": [],
    "何棠下": [],
    "旺村": [],
    "汤村": [],
    "镇龙北": [],
    "镇龙": [{"lineId": "", "lineName": "地铁21号线", "stationName": "镇龙"}],
    
    # 14号线支线(知识城线)换乘站
    "新和": [{"lineId": "", "lineName": "地铁14号线", "stationName": "新和"}],
    "红卫": [],
    "新南": [],
    "枫下": [],
    "知识城": [],
    "何棠下": [],
    "旺村": [],
    "汤村": [],
    "镇龙北": [],
    "镇龙": [{"lineId": "", "lineName": "地铁21号线", "stationName": "镇龙"}],
    
    # 18号线换乘站
    "万顷沙": [{"lineId": "", "lineName": "地铁15号线", "stationName": "万顷沙"}],
    "横沥": [],
    "番禺广场": [{"lineId": "", "lineName": "地铁3号线", "stationName": "番禺广场"}, {"lineId": "", "lineName": "地铁22号线", "stationName": "番禺广场"}],
    "南村万博": [{"lineId": "", "lineName": "地铁7号线", "stationName": "南村万博"}],
    "沙溪": [],
    "龙潭": [{"lineId": "", "lineName": "地铁11号线", "stationName": "龙潭"}],
    "磨碟沙": [{"lineId": "", "lineName": "地铁8号线", "stationName": "磨碟沙"}],
    "冼村": [],
    "广州东站": [{"lineId": "", "lineName": "地铁1号线", "stationName": "广州东站"}, {"lineId": "", "lineName": "地铁3号线北延段", "stationName": "广州东站"}, {"lineId": "", "lineName": "地铁11号线", "stationName": "广州东站"}],
    
    # 21号线换乘站
    "增城广场": [],
    "钟岗": [],
    "山田": [],
    "朱村": [],
    "凤岗": [],
    "坑贝": [],
    "中新": [],
    "镇龙": [{"lineId": "", "lineName": "地铁14号线支线(知识城线)", "stationName": "镇龙"}],
    "镇龙西": [],
    "金坑": [],
    "长平": [],
    "水西": [{"lineId": "", "lineName": "地铁7号线", "stationName": "水西"}],
    "苏元": [{"lineId": "", "lineName": "地铁6号线", "stationName": "苏元"}],
    "科学城": [],
    "神舟路": [],
    "天河智慧城": [],
    "大观南路": [],
    "黄村": [{"lineId": "", "lineName": "地铁4号线", "stationName": "黄村"}],
    "棠东": [],
    "天河公园": [{"lineId": "", "lineName": "地铁11号线", "stationName": "天河公园"}, {"lineId": "", "lineName": "地铁13号线", "stationName": "天河公园"}],
    
    # 22号线换乘站
    "陈头岗": [],
    "广州南站": [{"lineId": "", "lineName": "地铁2号线", "stationName": "广州南站"}, {"lineId": "", "lineName": "地铁7号线", "stationName": "广州南站"}, {"lineId": "", "lineName": "佛山2号线", "stationName": "广州南站"}],
    "市广路": [],
    "番禺广场": [{"lineId": "", "lineName": "地铁3号线", "stationName": "番禺广场"}, {"lineId": "", "lineName": "地铁18号线", "stationName": "番禺广场"}],
    "南浦西": [],
    "南漖": [],
    "西塱": [{"lineId": "", "lineName": "地铁1号线", "stationName": "西塱"}, {"lineId": "", "lineName": "地铁10号线", "stationName": "西塱"}, {"lineId": "", "lineName": "广佛线", "stationName": "西塱"}],
    "芳村": [{"lineId": "", "lineName": "地铁1号线", "stationName": "芳村"}, {"lineId": "", "lineName": "地铁11号线", "stationName": "芳村"}, {"lineId": "", "lineName": "地铁28号线", "stationName": "芳村"}],
    
    # APM线换乘站
    "广州塔": [{"lineId": "", "lineName": "地铁3号线", "stationName": "广州塔"}],
    "海心沙": [],
    "大剧院": [],
    "花城大道": [],
    "妇儿中心": [],
    "黄埔大道": [],
    "天河南": [],
    "体育中心南": [],
    "林和西": [{"lineId": "", "lineName": "地铁3号线北延段", "stationName": "林和西"}],
    
    # 广佛线换乘站
    "新城东": [],
    "东平": [{"lineId": "", "lineName": "佛山3号线", "stationName": "东平"}],
    "世纪莲": [],
    "澜石": [],
    "魁奇路": [{"lineId": "", "lineName": "佛山2号线", "stationName": "魁奇路"}],
    "季华园": [],
    "同济路": [],
    "祖庙": [],
    "普君北路": [],
    "朝安": [],
    "桂城": [],
    "南桂路": [],
    "𧒽岗": [{"lineId": "", "lineName": "佛山南海有轨电车1号线", "stationName": "𧒽岗"}],
    "千灯湖": [],
    "金融高新区": [],
    "龙溪": [],
    "菊树": [],
    "西塱": [{"lineId": "", "lineName": "地铁1号线", "stationName": "西塱"}, {"lineId": "", "lineName": "地铁10号线", "stationName": "西塱"}, {"lineId": "", "lineName": "地铁22号线", "stationName": "西塱"}],
    "鹤洞": [],
    "沙涌": [{"lineId": "", "lineName": "地铁11号线", "stationName": "沙涌"}],
    "沙园": [{"lineId": "", "lineName": "地铁8号线", "stationName": "沙园"}],
    "燕岗": [{"lineId": "", "lineName": "地铁11号线", "stationName": "燕岗"}],
    "石溪": [],
    "南洲": [{"lineId": "", "lineName": "地铁2号线", "stationName": "南洲"}],
    "沥滘": [{"lineId": "", "lineName": "地铁3号线", "stationName": "沥滘"}],
}

def add_transfer_info():
    input_file = r'e:\毕设\metro-management-system\scripts\output\merged_metro_data.json'
    output_file = r'e:\毕设\metro-management-system\scripts\output\merged_metro_data_with_transfer.json'
    
    # 加载数据
    data = load_json_file(input_file)
    
    # 为每个站点的每个站点添加换乘信息
    for line in data['lines']:
        line_name = line['lineName']
        for station in line['stations']:
            station_name = station['name']
            
            # 获取该站点的换乘信息
            transfer_info = TRANSFER_STATIONS.get(station_name, [])
            
            # 过滤掉当前线路自身的换乘信息
            filtered_transfers = []
            for transfer in transfer_info:
                # 如果换乘线路与当前线路不同，则保留
                if transfer['lineName'] != line_name:
                    filtered_transfers.append(transfer)
            
            # 添加transferLines字段
            station['transferLines'] = filtered_transfers
    
    # 保存结果
    save_json_file(output_file, data)
    print(f"已生成带换乘信息的文件：{output_file}")
    
    # 统计换乘站数量
    transfer_count = 0
    for line in data['lines']:
        for station in line['stations']:
            if station['transferLines']:
                transfer_count += 1
    
    print(f"总共标记了 {transfer_count} 个换乘站")
    
    # 打印部分示例
    print("\n示例数据（1号线部分站点）：")
    for line in data['lines']:
        if '1号线' in line['lineName']:
            for station in line['stations'][:5]:
                transfers = [t['lineName'] for t in station['transferLines']]
                print(f"  {station['name']}: {transfers if transfers else '无换乘'}")
            break

if __name__ == '__main__':
    add_transfer_info()
