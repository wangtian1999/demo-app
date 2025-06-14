<template>
  <div class="home-page">
    <!-- 系统标题区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="system-title">吉林省制造企业碳排放数据资源聚合系统</h1>
        <p class="system-subtitle">Jilin Province Manufacturing Enterprise Carbon Emission Data Resource Aggregation System</p>
        <p class="system-description">
          专为吉林省制造企业打造的碳排放数据管理平台，提供数据收集、分析和监控的一站式解决方案
        </p>
      </div>
    </section>

    <!-- 功能特色区域 -->
    <section class="features-section">
      <h2 class="section-title">核心功能</h2>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">📊</div>
          <h3>数据聚合</h3>
          <p>集中收集和整合多个企业的碳排放数据，建立统一的数据仓库</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">📈</div>
          <h3>实时监控</h3>
          <p>实时监控企业碳排放状况，及时发现异常并提供预警机制</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">📋</div>
          <h3>报告生成</h3>
          <p>自动生成各类碳排放报告，支持多种格式导出和定制化需求</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🎯</div>
          <h3>目标管理</h3>
          <p>设定减排目标并跟踪进度，助力企业实现碳中和目标</p>
        </div>
      </div>
    </section>

    <!-- 快速操作区域 -->
    <section class="actions-section">
      <h2 class="section-title">快速开始</h2>
      <div class="actions-grid">
        <router-link to="/login" class="action-card">
          <div class="action-icon">🔐</div>
          <h3>系统登录</h3>
          <p>登录系统开始使用各项功能</p>
          <span class="action-arrow">→</span>
        </router-link>
        <router-link to="/emissions" class="action-card">
          <div class="action-icon">📊</div>
          <h3>查看数据</h3>
          <p>浏览和分析碳排放数据</p>
          <span class="action-arrow">→</span>
        </router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
// 首页组件，无需额外逻辑
</script>

<style scoped>
.home-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* 系统标题区域 */
.hero-section {
  text-align: center;
  padding: 60px 0;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 16px;
  margin-bottom: 60px;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.system-title {
  font-size: 36px;
  font-weight: 700;
  color: #2156a1;
  margin-bottom: 16px;
  line-height: 1.2;
}

.system-subtitle {
  font-size: 18px;
  color: #6c757d;
  margin-bottom: 24px;
  font-weight: 300;
}

.system-description {
  font-size: 16px;
  color: #495057;
  line-height: 1.6;
  margin: 0;
}

/* 功能特色区域 */
.features-section {
  margin-bottom: 60px;
}

.section-title {
  font-size: 28px;
  font-weight: 600;
  color: #2156a1;
  text-align: center;
  margin-bottom: 40px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 20px;
}

.feature-card {
  background: white;
  padding: 32px 24px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 1px solid #e9ecef;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 16px;
  display: block;
}

.feature-card h3 {
  font-size: 20px;
  font-weight: 600;
  color: #2156a1;
  margin-bottom: 12px;
}

.feature-card p {
  font-size: 14px;
  color: #6c757d;
  line-height: 1.5;
  margin: 0;
}

/* 快速操作区域 */
.actions-section {
  margin-bottom: 40px;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.action-card {
  background: white;
  padding: 32px 24px;
  border-radius: 12px;
  text-decoration: none;
  color: inherit;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: 2px solid #e9ecef;
  position: relative;
  display: block;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(33, 86, 161, 0.15);
  border-color: #2156a1;
  text-decoration: none;
  color: inherit;
}

.action-icon {
  font-size: 40px;
  margin-bottom: 16px;
  display: block;
}

.action-card h3 {
  font-size: 20px;
  font-weight: 600;
  color: #2156a1;
  margin-bottom: 8px;
}

.action-card p {
  font-size: 14px;
  color: #6c757d;
  margin-bottom: 16px;
  line-height: 1.5;
}

.action-arrow {
  position: absolute;
  bottom: 24px;
  right: 24px;
  font-size: 20px;
  color: #2156a1;
  font-weight: bold;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .home-page {
    padding: 20px 16px;
  }
  
  .hero-section {
    padding: 40px 0;
    margin-bottom: 40px;
  }
  
  .system-title {
    font-size: 28px;
  }
  
  .system-subtitle {
    font-size: 16px;
  }
  
  .features-grid,
  .actions-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .section-title {
    font-size: 24px;
  }
}
</style>