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
