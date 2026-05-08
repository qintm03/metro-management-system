import { createMemoryHistory, createRouter, createWebHistory } from 'vue-router'



import Login from './components/Login.vue'
import User from './components/User.vue'
import Manager from './components/Manager.vue'
import Test from './components/Test.vue'

import TripService from './components/user/TripService.vue'
import UserManagement from './components/admin/UserManagement.vue'
import UserTrackManagement from './components/user/UserTrackManagement.vue'
import UserHomePage from './components/user/UserHomePage.vue'
import RealTimeTrack from './components/user/RealTimeTrack.vue'
import StationServices from './components/user/StationServices.vue'
import HeatmapAnalysis from './components/user/HeatmapAnalysis.vue'

import AdminHomePage from './components/admin/AdminHomePage.vue'
import LineManagement from './components/admin/LineManagement.vue'
import MapManagement from './components/admin/MapManagement.vue'
import AdminTicketManagement from './components/admin/AdminTicketManagement.vue'
import AdminTrackManagement from './components/admin/AdminTrackManagement.vue'






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