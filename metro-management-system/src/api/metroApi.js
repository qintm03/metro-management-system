import request from '../utils/request.js'

/**
 * 地铁数据 API 服务
 */

/**
 * 获取所有地铁线路
 * @returns {Promise}
 */
export function getAllLines() {
    return request.get('/api/metro/lines')
}

/**
 * 根据ID获取线路
 * @param {number} id 线路ID
 * @returns {Promise}
 */
export function getLineById(id) {
    return request.get(`/api/metro/lines/${id}`)
}

/**
 * 获取所有站点
 * @returns {Promise}
 */
export function getAllStations() {
    return request.get('/api/metro/stations')
}

/**
 * 根据线路ID获取站点
 * @param {number} lineId 线路ID
 * @returns {Promise}
 */
export function getStationsByLineId(lineId) {
    return request.get(`/api/metro/stations/line/${lineId}`)
}

/**
 * 获取换乘站
 * @returns {Promise}
 */
export function getTransferStations() {
    return request.get('/api/metro/stations/transfer')
}

/**
 * 获取完整地铁数据（线路+站点）
 * @returns {Promise}
 */
export function getFullMetroData() {
    return request.get('/api/metro/full')
}

/**
 * 获取线路详情（包含站点）
 * @param {number} lineId 线路ID
 * @returns {Promise}
 */
export function getLineDetail(lineId) {
    return request.get(`/api/metro/detail/${lineId}`)
}

// ---- 线路写操作 ----
export function addLine(data) {
    return request.post('/api/metro/lines', data)
}

export function updateLine(id, data) {
    return request.put(`/api/metro/lines/${id}`, data)
}

export function updateLinePath(id, path) {
    return request.put(`/api/metro/lines/${id}/path`, { path })
}

export function deleteLine(id) {
    return request.delete(`/api/metro/lines/${id}`)
}

// ---- 站点写操作 ----
export function addStation(data) {
    return request.post('/api/metro/stations', data)
}

export function updateStation(id, data) {
    return request.put(`/api/metro/stations/${id}`, data)
}

export function updateStationPosition(id, longitude, latitude) {
    return request.put(`/api/metro/stations/${id}/position`, { longitude, latitude })
}

export function deleteStation(id) {
    return request.delete(`/api/metro/stations/${id}`)
}

// ---- 换乘写操作 ----
export function addTransfer(data) {
    return request.post('/api/metro/transfers', data)
}

export function updateTransfer(id, data) {
    return request.put(`/api/metro/transfers/${id}`, data)
}

export function deleteTransfer(id) {
    return request.delete(`/api/metro/transfers/${id}`)
}

// ---- 时刻表 ----
export function updateSchedule(lineId, data) {
    return request.put(`/api/metro/line/${lineId}/schedule`, data)
}

// ---- 人口数据 ----
export function getPopulationData(params) {
    return request.get('/api/population', { params })
}

export function updatePopulation(id, data) {
    return request.put(`/api/population/${id}`, data)
}

export function deletePopulation(id) {
    return request.delete(`/api/population/${id}`)
}

export function importPopulation(data) {
    return request.post('/api/population/import', data)
}
