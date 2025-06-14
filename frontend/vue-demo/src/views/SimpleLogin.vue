<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">登录</h2>
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="input-group">
          <input 
            v-model="username" 
            type="text" 
            placeholder="用户名" 
            class="login-input"
            required
          />
        </div>
        <div class="input-group">
          <input 
            v-model="password" 
            type="password" 
            placeholder="密码" 
            class="login-input"
            required
          />
        </div>
        <button type="submit" class="login-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      <div class="back-link">
        <button @click="$router.push('/')" class="back-btn">返回首页</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const password = ref('')
const loading = ref(false)

const handleLogin = async () => {
  if (!username.value || !password.value) {
    alert('请输入用户名和密码')
    return
  }
  
  loading.value = true
  
  try {
    // 调用后端登录API
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: username.value,
        password: password.value
      })
    })
    
    if (response.ok) {
      const data = await response.json()
      // 存储JWT token到localStorage
      localStorage.setItem('token', data.token)
      localStorage.setItem('username', data.username)
      localStorage.setItem('role', data.role)
      
      alert('登录成功！')
      // 跳转到查询页面
      router.push('/simple-query')
    } else {
      const errorData = await response.json()
      alert('登录失败：' + (errorData.message || '用户名或密码错误'))
    }
  } catch (error) {
    console.error('登录请求失败:', error)
    alert('网络错误，请检查后端服务是否启动')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #2c3e50;
  font-size: 28px;
  font-weight: 600;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-group {
  display: flex;
  flex-direction: column;
}

.login-input {
  padding: 15px;
  border: 2px solid #e1e8ed;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
  outline: none;
}

.login-input:focus {
  border-color: #667eea;
}

.login-btn {
  padding: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.back-link {
  text-align: center;
  margin-top: 20px;
}

.back-btn {
  background: none;
  border: none;
  color: #667eea;
  font-size: 14px;
  cursor: pointer;
  text-decoration: underline;
  transition: color 0.3s ease;
}

.back-btn:hover {
  color: #764ba2;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-box {
    padding: 30px 20px;
  }
  
  .login-title {
    font-size: 24px;
  }
  
  .login-input,
  .login-btn {
    padding: 12px;
    font-size: 14px;
  }
}
</style>