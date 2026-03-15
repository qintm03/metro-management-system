import { ElMessage } from 'element-plus'
import router from '../router.js'
import axios from "axios";

/* 调用 axios.create() 创建实例时，返回的 request 对象继承了 axios 原型上的所有方法，包括：
request(config)
get(url, config)
delete(url, config)
head(url, config)
options(url, config)
post(url, data, config)
put(url, data, config)
patch(url, data, config) */

const request = axios.create({
    baseURL: import.meta.env.VITE_BASE_URL,
    timeout: 30000  // 后台接口超时时间设置
})

// request 拦截器
/* 拦截器在请求发送前执行
设置请求头 Content-Type 为 'application/json;charset=utf-8'
axios 会自动将 form 对象序列化为 JSON 格式 */
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    return config
}, error => {
    return Promise.reject(error)
});

// response 拦截器
// 把后端接口传回来的数据封装在race实例中，方便调用
request.interceptors.response.use(response => {
        let res = response.data;
        // 如果是返回的文件
        if (response.config.responseType === 'blob') {
            return res
        }
        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        // 当权限验证不通过的时候给出提示
        if (res.code === '401') {
            ElMessage.error(res.msg);
            router.push("/login")
        }
        return res;
    },
        error => {
        console.log('err' + error)
        return Promise.reject(error)
    }
)

export default request
