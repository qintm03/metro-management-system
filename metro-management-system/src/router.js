import { createMemoryHistory, createRouter, createWebHistory } from 'vue-router'



import Login from './components/Login.vue'
import User from './components/User.vue'
import Manager from './components/Manager.vue'
import Test from './components/Test.vue'

import TripService from './components/user/TripService.vue'
import UserTrackManagement from './components/user/UserTrackManagement.vue'
import UserHomePage from './components/user/UserHomePage.vue'
import RealTimeTrack from './components/user/RealTimeTrack.vue'
import StationServices from './components/user/StationServices.vue'
import HeatmapAnalysis from './components/user/HeatmapAnalysis.vue'
//import UserManagement from './components/user/UserManagement.vue'

import AdminHomePage from './components/admin/AdminHomePage.vue'
import UserManagement from './components/admin/UserManagement.vue'
import LineManagement from './components/admin/LineManagement.vue'
import ScheduleConfig from './components/admin/ScheduleConfig.vue'
import DataManagement from './components/admin/DataManagement.vue'
import SystemSettings from './components/admin/SystemSettings.vue'
import demo from './components/admin/demo.vue'  







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
      { path: 'ScheduleConfig', component: ScheduleConfig },
      { path: 'DataManagement', component: DataManagement },
      { path: 'SystemSettings', component: SystemSettings },
      { path: 'UserManagement', component: UserManagement },
      { path: 'demo', component: demo },
    ]
  },
  {
    path: '/user',
    component: User,
    redirect: '/user/UserHomePage',
    children: [
      { path: 'UserHomePage', component: UserHomePage },
      { path: 'TripService', component: TripService },
      { path: 'UserTrackManagement', component: UserTrackManagement },
      { path: 'RealTimeTrack', component: RealTimeTrack },
      { path: 'StationServices', component: StationServices },
      { path: 'HeatmapAnalysis', component: HeatmapAnalysis },
      { path: 'UserManagement', component: UserManagement },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})



export default router