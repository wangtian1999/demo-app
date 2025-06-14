import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import { createRouter, createWebHistory } from 'vue-router'

import LoginPage from './views/LoginPage.vue'
import SimpleLogin from './views/SimpleLogin.vue'
import SimpleQuery from './views/SimpleQuery.vue'
import HomeView from './views/HomeView.vue'
import LoginView from './views/LoginView.vue'
import EmissionsView from './views/EmissionsView.vue'

const routes = [
  { path: '/', component: LoginPage },
  { path: '/simple-login', component: SimpleLogin },
  { path: '/simple-query', component: SimpleQuery },
  { path: '/home', component: HomeView },
  { path: '/login', component: LoginView },
  { path: '/emissions', component: EmissionsView }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const app = createApp(App)
app.use(router)
app.mount('#app')
