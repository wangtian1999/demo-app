<template>
  <div class="management-container">
    <div class="management-header">
      <h2>碳排放数据管理</h2>
      <div class="user-info">
        <span class="welcome-text">欢迎，{{ currentUser }}</span>
        <span class="role-badge">{{ userRole }}</span>
      </div>
      <div class="header-actions">
        <button @click="showAddModal = true" class="add-btn">新增数据</button>
        <button @click="logout" class="logout-btn">登出</button>
        <button @click="$router.push('/')" class="back-btn">返回首页</button>
      </div>
    </div>

    <div class="main-content">
      <!-- 左侧操作区域 -->
      <div class="left-panel">
        <!-- 搜索栏 -->
        <div class="search-section">
          <h3>搜索筛选</h3>
          <input 
            v-model="searchKeyword" 
            type="text" 
            placeholder="搜索企业名称或数据类型..." 
            class="search-input"
            @input="handleSearch"
          />
        </div>

        <!-- 快速操作 -->
        <div class="quick-actions">
          <h3>快速操作</h3>
          <button @click="showAddModal = true" class="action-btn add">新增数据</button>
          <button @click="exportData" class="action-btn export">导出数据</button>
          <button @click="refreshData" class="action-btn refresh">刷新数据</button>
        </div>

        <!-- 统计信息 -->
        <div class="stats-section">
          <h3>统计信息</h3>
          <div class="stat-item">
            <span class="stat-label">总记录数：</span>
            <span class="stat-value">{{ dataList.length }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">总排放量：</span>
            <span class="stat-value">{{ totalEmissions.toFixed(2) }} 吨CO2</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">平均排放量：</span>
            <span class="stat-value">{{ averageEmissions.toFixed(2) }} 吨CO2</span>
          </div>
        </div>
      </div>

      <!-- 右侧数据表格 -->
      <div class="right-panel">
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>企业名称</th>
                <th>数据类型</th>
                <th>排放量(吨CO2)</th>
                <th>记录时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in filteredData" :key="item.id">
                <td>{{ item.id }}</td>
                <td>{{ item.company }}</td>
                <td>{{ item.type }}</td>
                <td>{{ item.emission }}</td>
                <td>{{ item.date }}</td>
                <td class="actions">
                  <button @click="editItem(item)" class="edit-btn">编辑</button>
                  <button @click="deleteItem(item.id)" class="delete-btn">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <div v-if="showAddModal || showEditModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <h3>{{ showAddModal ? '新增数据' : '编辑数据' }}</h3>
        <form @submit.prevent="submitForm">
          <div class="form-group">
            <label>企业名称</label>
            <input v-model="formData.company" type="text" required />
          </div>
          <div class="form-group">
            <label>数据类型</label>
            <select v-model="formData.type" required>
              <option value="">请选择</option>
              <option value="工业排放">工业排放</option>
              <option value="交通排放">交通排放</option>
              <option value="能源消耗">能源消耗</option>
            </select>
          </div>
          <div class="form-group">
            <label>排放量(吨CO2)</label>
            <input v-model="formData.emission" type="number" step="0.01" required />
          </div>
          <div class="form-group">
            <label>记录时间</label>
            <input v-model="formData.date" type="date" required />
          </div>
          <div class="form-actions">
            <button type="button" @click="closeModal" class="cancel-btn">取消</button>
            <button type="submit" class="submit-btn">{{ showAddModal ? '新增' : '保存' }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 用户认证状态
const currentUser = ref('')
const userRole = ref('')
const isAuthenticated = ref(false)

// 检查用户认证状态
const checkAuth = () => {
  const token = localStorage.getItem('token')
  const username = localStorage.getItem('username')
  const role = localStorage.getItem('role')
  
  if (!token || !username) {
    // 未登录，跳转到登录页面
    router.push('/simple-login')
    return false
  }
  
  currentUser.value = username
  userRole.value = role || 'USER'
  isAuthenticated.value = true
  return true
}

// 登出功能
const logout = () => {
  if (confirm('确定要登出吗？')) {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    router.push('/simple-login')
  }
}

// 数据状态
const dataList = ref([])
const searchKeyword = ref('')
const showAddModal = ref(false)
const showEditModal = ref(false)
const currentEditId = ref(null)

// 表单数据
const formData = ref({
  company: '',
  type: '',
  emission: '',
  date: ''
})

// 初始化数据
const initData = () => {
  dataList.value = [
    { id: 1, company: '绿色科技有限公司', type: '工业排放', emission: 125.5, date: '2024-01-15' },
    { id: 2, company: '环保能源集团', type: '能源消耗', emission: 89.2, date: '2024-01-16' },
    { id: 3, company: '清洁制造企业', type: '交通排放', emission: 67.8, date: '2024-01-17' },
    { id: 4, company: '可持续发展公司', type: '工业排放', emission: 156.3, date: '2024-01-18' }
  ]
}

// 搜索过滤
const filteredData = computed(() => {
  if (!searchKeyword.value) return dataList.value
  return dataList.value.filter(item => 
    item.company.includes(searchKeyword.value) || 
    item.type.includes(searchKeyword.value)
  )
})

// 统计信息计算
const totalEmissions = computed(() => {
  return dataList.value.reduce((total, item) => total + item.emission, 0)
})

const averageEmissions = computed(() => {
  return dataList.value.length > 0 ? totalEmissions.value / dataList.value.length : 0
})

// 搜索处理
const handleSearch = () => {
  // 实时搜索，由computed自动处理
}

// 导出数据
const exportData = () => {
  alert('导出功能开发中...')
}

// 刷新数据
const refreshData = () => {
  initData()
  alert('数据已刷新')
}

// 新增数据
const addItem = () => {
  const newId = Math.max(...dataList.value.map(item => item.id)) + 1
  dataList.value.push({
    id: newId,
    ...formData.value,
    emission: parseFloat(formData.value.emission)
  })
  closeModal()
}

// 编辑数据
const editItem = (item) => {
  currentEditId.value = item.id
  formData.value = { ...item }
  showEditModal.value = true
}

// 更新数据
const updateItem = () => {
  const index = dataList.value.findIndex(item => item.id === currentEditId.value)
  if (index !== -1) {
    dataList.value[index] = {
      ...formData.value,
      id: currentEditId.value,
      emission: parseFloat(formData.value.emission)
    }
  }
  closeModal()
}

// 删除数据
const deleteItem = (id) => {
  if (confirm('确定要删除这条记录吗？')) {
    dataList.value = dataList.value.filter(item => item.id !== id)
  }
}

// 提交表单
const submitForm = () => {
  if (showAddModal.value) {
    addItem()
  } else {
    updateItem()
  }
}

// 关闭弹窗
const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
  currentEditId.value = null
  formData.value = {
    company: '',
    type: '',
    emission: '',
    date: ''
  }
}

// 组件挂载时先检查认证状态，再初始化数据
onMounted(() => {
  if (checkAuth()) {
    initData()
  }
})
</script>

<style scoped>
.management-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.management-header h2 {
  margin: 0;
  color: #2c3e50;
  font-size: 24px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.welcome-text {
  color: #2c3e50;
  font-size: 16px;
  font-weight: 500;
}

.role-badge {
  background: #667eea;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.add-btn {
  background: #28a745;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.3s ease;
}

.add-btn:hover {
  background: #218838;
}

.logout-btn {
  background: #dc3545;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.3s ease;
}

.logout-btn:hover {
  background: #c82333;
}

.back-btn {
  background: #6c757d;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.3s ease;
}

.back-btn:hover {
  background: #5a6268;
}

.main-content {
  display: flex;
  gap: 20px;
  height: calc(100vh - 140px);
}

.left-panel {
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.search-section,
.quick-actions,
.stats-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.search-section h3,
.quick-actions h3,
.stats-section h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 2px solid #f8f9fa;
  padding-bottom: 8px;
}

.search-input {
  width: 100%;
  padding: 12px;
  border: 2px solid #e1e8ed;
  border-radius: 5px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s ease;
}

.search-input:focus {
  border-color: #667eea;
}

.action-btn {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-weight: 500;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.action-btn.add {
  background: #28a745;
  color: white;
}

.action-btn.add:hover {
  background: #218838;
}

.action-btn.export {
  background: #17a2b8;
  color: white;
}

.action-btn.export:hover {
  background: #138496;
}

.action-btn.refresh {
  background: #ffc107;
  color: #212529;
}

.action-btn.refresh:hover {
  background: #e0a800;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f8f9fa;
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-label {
  color: #6c757d;
  font-size: 14px;
}

.stat-value {
  color: #2c3e50;
  font-weight: 600;
  font-size: 14px;
}

.table-container {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  background: #f8f9fa;
  padding: 15px;
  text-align: left;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 2px solid #dee2e6;
}

.data-table td {
  padding: 15px;
  border-bottom: 1px solid #dee2e6;
  color: #495057;
}

.data-table tbody tr:hover {
  background: #f8f9fa;
}

.actions {
  display: flex;
  gap: 8px;
}

.edit-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background 0.3s ease;
}

.edit-btn:hover {
  background: #0056b3;
}

.delete-btn {
  background: #dc3545;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background 0.3s ease;
}

.delete-btn:hover {
  background: #c82333;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.modal-content h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #2c3e50;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #2c3e50;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 2px solid #e1e8ed;
  border-radius: 5px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s ease;
}

.form-group input:focus,
.form-group select:focus {
  border-color: #667eea;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn {
  background: #6c757d;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.cancel-btn:hover {
  background: #5a6268;
}

.submit-btn {
  background: #28a745;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.submit-btn:hover {
  background: #218838;
}

.data-table {
  width: 100%;
  table-layout: fixed;
}

.data-table th:nth-child(1) {
  width: 50px;
}

.data-table th:nth-child(2) {
  width: 20%;
}

.data-table th:nth-child(3) {
  width: 15%;
}

.data-table th:nth-child(4) {
  width: 15%;
}

.data-table th:nth-child(5) {
  width: 15%;
}

.data-table th:nth-child(6) {
  width: 120px;
}

.data-table tbody {
  display: block;
  max-height: calc(100vh - 240px);
  overflow-y: auto;
}

.data-table thead,
.data-table tbody tr {
  display: table;
  width: 100%;
  table-layout: fixed;
}
</style>