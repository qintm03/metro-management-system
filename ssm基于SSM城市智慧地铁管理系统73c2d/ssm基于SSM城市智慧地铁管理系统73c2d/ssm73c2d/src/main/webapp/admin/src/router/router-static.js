import Vue from 'vue';
//配置路由
import VueRouter from 'vue-router'
Vue.use(VueRouter);
//1.创建组件
import Index from '@/views/index'
import Home from '@/views/home'
import Login from '@/views/login'
import NotFound from '@/views/404'
import UpdatePassword from '@/views/update-password'
import pay from '@/views/pay'
import register from '@/views/register'
import center from '@/views/center'
    import news from '@/views/modules/news/list'
    import discusszhandianzhoubian from '@/views/modules/discusszhandianzhoubian/list'
    import yonghu from '@/views/modules/yonghu/list'
    import checixianlu from '@/views/modules/checixianlu/list'
    import messages from '@/views/modules/messages/list'
    import discusszhandianchaxun from '@/views/modules/discusszhandianchaxun/list'
    import zhandianchaxun from '@/views/modules/zhandianchaxun/list'
    import config from '@/views/modules/config/list'
    import discusschecixianlu from '@/views/modules/discusschecixianlu/list'
    import zhandianzhoubian from '@/views/modules/zhandianzhoubian/list'


//2.配置路由   注意：名字
const routes = [{
    path: '/index',
    name: '系统首页',
    component: Index,
    children: [{
      // 这里不设置值，是把main作为默认页面
      path: '/',
      name: '系统首页',
      component: Home,
      meta: {icon:'', title:'center'}
    }, {
      path: '/updatePassword',
      name: '修改密码',
      component: UpdatePassword,
      meta: {icon:'', title:'updatePassword'}
    }, {
      path: '/pay',
      name: '支付',
      component: pay,
      meta: {icon:'', title:'pay'}
    }, {
      path: '/center',
      name: '个人信息',
      component: center,
      meta: {icon:'', title:'center'}
    }
      ,{
	path: '/news',
        name: '通知公告',
        component: news
      }
      ,{
	path: '/discusszhandianzhoubian',
        name: '站点周边评论',
        component: discusszhandianzhoubian
      }
      ,{
	path: '/yonghu',
        name: '用户',
        component: yonghu
      }
      ,{
	path: '/checixianlu',
        name: '车次线路',
        component: checixianlu
      }
      ,{
	path: '/messages',
        name: '留言板',
        component: messages
      }
      ,{
	path: '/discusszhandianchaxun',
        name: '站点查询评论',
        component: discusszhandianchaxun
      }
      ,{
	path: '/zhandianchaxun',
        name: '站点查询',
        component: zhandianchaxun
      }
      ,{
	path: '/config',
        name: '轮播图管理',
        component: config
      }
      ,{
	path: '/discusschecixianlu',
        name: '车次线路评论',
        component: discusschecixianlu
      }
      ,{
	path: '/zhandianzhoubian',
        name: '站点周边',
        component: zhandianzhoubian
      }
    ]
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: {icon:'', title:'login'}
  },
  {
    path: '/register',
    name: 'register',
    component: register,
    meta: {icon:'', title:'register'}
  },
  {
    path: '/',
    name: '系统首页',
    redirect: '/index'
  }, /*默认跳转路由*/
  {
    path: '*',
    component: NotFound
  }
]
//3.实例化VueRouter  注意：名字
const router = new VueRouter({
  mode: 'hash',
  /*hash模式改为history*/
  routes // （缩写）相当于 routes: routes
})
const originalPush = VueRouter.prototype.push
//修改原型对象中的push方法
VueRouter.prototype.push = function push(location) {
   return originalPush.call(this, location).catch(err => err)
}
export default router;
