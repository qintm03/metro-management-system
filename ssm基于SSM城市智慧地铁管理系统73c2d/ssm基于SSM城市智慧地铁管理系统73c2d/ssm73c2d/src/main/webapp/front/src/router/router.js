import VueRouter from 'vue-router'

//引入组件
import Index from '../pages'
import Home from '../pages/home/home'
import Login from '../pages/login/login'
import Register from '../pages/register/register'
import Center from '../pages/center/center'
import Messages from '../pages/messages/list'
import Storeup from '../pages/storeup/list'
import News from '../pages/news/news-list'
import NewsDetail from '../pages/news/news-detail'
import yonghuList from '../pages/yonghu/list'
import yonghuDetail from '../pages/yonghu/detail'
import yonghuAdd from '../pages/yonghu/add'
import zhandianchaxunList from '../pages/zhandianchaxun/list'
import zhandianchaxunDetail from '../pages/zhandianchaxun/detail'
import zhandianchaxunAdd from '../pages/zhandianchaxun/add'
import checixianluList from '../pages/checixianlu/list'
import checixianluDetail from '../pages/checixianlu/detail'
import checixianluAdd from '../pages/checixianlu/add'
import zhandianzhoubianList from '../pages/zhandianzhoubian/list'
import zhandianzhoubianDetail from '../pages/zhandianzhoubian/detail'
import zhandianzhoubianAdd from '../pages/zhandianzhoubian/add'

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
	return originalPush.call(this, location).catch(err => err)
}

//配置路由
export default new VueRouter({
	routes:[
		{
      path: '/',
      redirect: '/index/home'
    },
		{
			path: '/index',
			component: Index,
			children:[
				{
					path: 'home',
					component: Home
				},
				{
					path: 'center',
					component: Center,
				},
				{
					path: 'messages',
					component: Messages
				},
				{
					path: 'storeup',
					component: Storeup
				},
				{
					path: 'news',
					component: News
				},
				{
					path: 'newsDetail',
					component: NewsDetail
				},
				{
					path: 'yonghu',
					component: yonghuList
				},
				{
					path: 'yonghuDetail',
					component: yonghuDetail
				},
				{
					path: 'yonghuAdd',
					component: yonghuAdd
				},
				{
					path: 'zhandianchaxun',
					component: zhandianchaxunList
				},
				{
					path: 'zhandianchaxunDetail',
					component: zhandianchaxunDetail
				},
				{
					path: 'zhandianchaxunAdd',
					component: zhandianchaxunAdd
				},
				{
					path: 'checixianlu',
					component: checixianluList
				},
				{
					path: 'checixianluDetail',
					component: checixianluDetail
				},
				{
					path: 'checixianluAdd',
					component: checixianluAdd
				},
				{
					path: 'zhandianzhoubian',
					component: zhandianzhoubianList
				},
				{
					path: 'zhandianzhoubianDetail',
					component: zhandianzhoubianDetail
				},
				{
					path: 'zhandianzhoubianAdd',
					component: zhandianzhoubianAdd
				},
			]
		},
		{
			path: '/login',
			component: Login
		},
		{
			path: '/register',
			component: Register
		},
	]
})
