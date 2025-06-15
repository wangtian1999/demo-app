import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import { createRouter, createWebHistory } from 'vue-router'

import LoginPage from './views/LoginPage.vue'
import SimpleLogin from './views/SimpleLogin.vue'
import SimpleQuery from './views/SimpleQuery.vue'
import HomeView from './views/HomeView.vue'

import FileManagement from './views/FileManagement.vue'

const routes = [
  { path: '/', component: LoginPage },
  { path: '/simple-login', component: SimpleLogin },
  { path: '/simple-query', component: SimpleQuery },
  { path: '/file-management', component: FileManagement },
  { path: '/home', component: HomeView },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const app = createApp(App)
app.use(router)
app.mount('#app')
