-- 为 metro_line 表添加 train_count 列
ALTER TABLE metro_line ADD COLUMN train_count INT DEFAULT 10 COMMENT '列车数量' AFTER dwell_time;

-- 为已有线路设置默认值（每条线路10辆）
UPDATE metro_line SET train_count = 10 WHERE train_count IS NULL;
