<template>
  <div class="page-container">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="nav-content">
        <div class="nav-title">碳排放聚合系统</div>
        <div class="nav-buttons">
          <button class="nav-btn" @click="$router.push('/simple-login')">登录</button>
          <button class="nav-btn" @click="$router.push('/simple-query')">查询</button>
        </div>
      </div>
    </nav>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <div class="hero-section">
        <div class="logo-section">
          <div class="logo-icon">🌱</div>
          <h1 class="system-title">吉林省制造企业碳排放数据资源聚合系统</h1>
          <p class="system-subtitle">Jilin Province Manufacturing Enterprise Carbon Emission Data Resource Aggregation System</p>
          <p class="system-description">
            专为吉林省制造企业打造的碳排放数据管理平台，提供数据收集、分析和监控的一站式解决方案
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 系统介绍首页，无需额外逻辑
</script>

<style scoped>
.page-container {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

/* 导航栏样式 */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  z-index: 1000;
  height: 70px;
}

.nav-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 40px;
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-title {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  letter-spacing: 0.5px;
}

.nav-buttons {
  display: flex;
  gap: 15px;
}

.nav-btn {
  padding: 10px 25px;
  border: 2px solid #667eea;
  background: transparent;
  color: #667eea;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.nav-btn:hover {
  background: #667eea;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3);
}

/* 主要内容区域 */
.main-content {
  padding-top: 70px;
  width: 100%;
  min-height: calc(100vh - 70px);
}

/* 系统标题区域 */
.hero-section {
  padding: 100px 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
  min-height: calc(100vh - 70px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-section {
  width: 100%;
}

.logo-icon {
  font-size: 80px;
  margin-bottom: 30px;
  display: block;
}

.system-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 20px;
  letter-spacing: 1px;
  line-height: 1.2;
}

.system-subtitle {
  font-size: 18px;
  opacity: 0.9;
  font-weight: 300;
  margin-bottom: 30px;
}

.system-description {
  font-size: 16px;
  line-height: 1.6;
  opacity: 0.95;
  max-width: 600px;
  margin: 0 auto;
}



/* 响应式设计 */
@media (max-width: 768px) {
  .nav-content {
    padding: 0 20px;
  }
  
  .nav-title {
    font-size: 20px;
  }
  
  .nav-btn {
    padding: 8px 20px;
    font-size: 14px;
  }
  
  .hero-section {
    padding: 60px 20px;
  }
  
  .system-title {
    font-size: 28px;
  }
  
  .system-subtitle {
    font-size: 16px;
  }
}
</style>