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
    },{
      path: 'batch',
      component: () => import('../views/main/batch.vue'),
    },{
      path: 'daily-train',
      component: () => import('../views/main/daily-train.vue'),
    },{
      path: 'daily-train-station',
      component: () => import('../views/main/daily-train-station.vue'),
    },{
      path: 'daily-train-carriage',
      component: () => import('../views/main/daily-train-carriage.vue'),
    },{
      path: 'daily-train-seat',
      component: () => import('../views/main/daily-train-seat.vue'),
    },{
      path: 'daily-train-ticket',
      component: () => import('../views/main/daily-train-ticket.vue'),
    },{
      path: 'confirm-order',
      component: () => import('../views/main/confirm-order.vue'),
    },{
      path: 'ticket-manage',
      component: () => import('../views/main/ticket-manage.vue'),
    },]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
