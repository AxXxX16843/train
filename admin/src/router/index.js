import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../views/main.vue'),
    children: [{
      path: '',
      redirect: '/welcome',
    },{
      path: 'welcome',
      component: () => import('../views/main/welcome.vue'),
    },{
      path: 'passenger',
      component: () => import('../views/main/passenger.vue'),
    },{
      path: 'station',
      component: () => import('../views/main/station.vue'),
    },{
      path: 'train',
      component: () => import('../views/main/train.vue'),
    },{
      path: 'carriage',
      component: () => import('../views/main/carriage.vue'),
    },{
      path: 'train-station',
      component: () => import('../views/main/train-station.vue'),
    },{
      path: 'train-seat',
      component: () => import('../views/main/train-seat.vue'),
    },]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
