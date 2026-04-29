import { createMemoryHistory, createRouter, createWebHistory } from 'vue-router'


import TripService from './components/user/TripService.vue'
import Login from './components/Login.vue'
import User from './components/User.vue'
import Manager from './components/Manager.vue'
import Test from './components/Test.vue'
import AdminHomePage from './components/admin/AdminHomePage.vue'
import LineManagement from './components/admin/LineManagement.vue'
import MapManagement from './components/admin/MapManagement.vue'
import AdminTicketManagement from './components/admin/AdminTicketManagement.vue'
import AdminTrackManagement from './components/admin/AdminTrackManagement.vue'
import UserManagement from './components/admin/UserManagement.vue'
import UserHomePage from './components/user/UserHomePage.vue'
import UserTicketManagement from './components/user/UserTicketManagement.vue'
import UserTrackManagement from './components/user/UserTrackManagement.vue'




const routes = [
  { path: '/', component: Login },
  { path: '/test', component: Test },
  {
    path: '/manager',
    component: Manager,
    redirect: '/manager/AdminHomePage',
    children: [
      { path: 'AdminHomePage', component: AdminHomePage },
      { path: 'LineManagement', component: LineManagement },
      { path: 'MapManagement', component: MapManagement },
      { path: 'AdminTicketManagement', component: AdminTicketManagement },
      { path: 'AdminTrackManagement', component: AdminTrackManagement },
      { path: 'UserManagement', component: UserManagement },
    ]
  },
  {
    path: '/user',
    component: User,
    redirect: '/user/UserHomePage',
    children: [
      { path: 'UserHomePage', component: UserHomePage },
      { path: 'TripService', component: TripService },
      { path: 'UserTicketManagement', component: UserTicketManagement },
      { path: 'UserTrackManagement', component: UserTrackManagement },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})



export default router