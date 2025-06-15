<template>
  <div class="management-container">
    <div class="management-header">
      <h2>碳排放数据管理</h2>
      <div class="user-info">
        <span class="welcome-text">欢迎，{{ currentUser }}</span>
        <span class="role-badge">{{ userRole }}</span>
      </div>
      <div class="header-actions">
        <button @click="openAddModal" class="add-btn">新增数据</button>
        <button @click="$router.push('/file-management')" class="nav-btn">文件管理</button>
        <button v-if="isAuthenticated" @click="logout" class="logout-btn">登出</button>
        <button v-if="!isAuthenticated" @click="$router.push('/simple-login')" class="login-btn">登录</button>
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
          <button @click="openAddModal" class="action-btn add">新增数据</button>
          <button v-if="isAuthenticated" @click="exportData" class="action-btn export">导出数据</button>
          <button @click="refreshData" class="action-btn refresh">刷新数据</button>
          <div v-if="!isAuthenticated" class="guest-tip">
            <p>游客模式：可查询和新增数据</p>
            <p>登录后可使用更多功能</p>
          </div>
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
                  <button v-if="isAuthenticated" @click="editItem(item)" class="edit-btn">编辑</button>
                  <button v-if="isAuthenticated" @click="deleteItem(item.id)" class="delete-btn">删除</button>
                  <span v-if="!isAuthenticated" class="guest-notice">请登录后操作</span>
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
        <!-- 调试信息 -->
        <div style="background: #f0f0f0; padding: 10px; margin-bottom: 10px; font-size: 12px; border-radius: 4px;">
          <p>调试信息：</p>
          <p>showAddModal: {{ showAddModal }}</p>
          <p>showEditModal: {{ showEditModal }}</p>
          <p>表单数据: {{ JSON.stringify(formData) }}</p>
        </div>
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
    // 未登录状态，设置为游客模式
    currentUser.value = '游客'
    userRole.value = 'GUEST'
    isAuthenticated.value = false
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
const initData = async () => {
  try {
    console.log('开始从后端获取数据列表')
    const response = await fetch('http://localhost:8080/api/emissions/all', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })
    
    if (response.ok) {
      const result = await response.json()
      if (result.success && result.data) {
        // 将后端数据格式转换为前端需要的格式
        dataList.value = result.data.map(item => ({
          id: item.id,
          company: item.enterpriseName,
          type: '工业排放', // 后端暂时没有类型字段，使用默认值
          emission: item.emissionAmount,
          date: item.emissionDate
        }))
        console.log('成功获取数据列表:', dataList.value)
      } else {
        console.error('后端返回错误:', result.message)
        // 如果获取失败，使用默认数据
        setDefaultData()
      }
    } else {
      console.error('获取数据失败，HTTP状态:', response.status)
      // 如果获取失败，使用默认数据
      setDefaultData()
    }
  } catch (error) {
    console.error('获取数据时发生错误:', error)
    // 如果获取失败，使用默认数据
    setDefaultData()
  }
}

// 设置默认数据（作为备用）
const setDefaultData = () => {
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
  if (dataList.value.length === 0) {
    alert('暂无数据可导出')
    return
  }

  try {
    // 准备CSV数据
    const headers = ['序号', '企业名称', '数据类型', '排放量(吨)', '日期']
    const csvContent = []
    
    // 添加表头
    csvContent.push(headers.join(','))
    
    // 添加数据行
    dataList.value.forEach((item, index) => {
      const row = [
        index + 1,
        `"${item.company}"`, // 用引号包围，防止逗号问题
        `"${item.type}"`,
        item.emission,
        item.date
      ]
      csvContent.push(row.join(','))
    })
    
    // 创建CSV文件内容
    const csvString = csvContent.join('\n')
    
    // 添加BOM以支持中文
    const BOM = '\uFEFF'
    const csvWithBOM = BOM + csvString
    
    // 创建Blob对象
    const blob = new Blob([csvWithBOM], { type: 'text/csv;charset=utf-8;' })
    
    // 创建下载链接
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    
    // 生成文件名（包含当前日期时间）
    const now = new Date()
    const timestamp = now.getFullYear() + 
      String(now.getMonth() + 1).padStart(2, '0') + 
      String(now.getDate()).padStart(2, '0') + '_' +
      String(now.getHours()).padStart(2, '0') + 
      String(now.getMinutes()).padStart(2, '0')
    
    link.setAttribute('download', `碳排放数据_${timestamp}.csv`)
    link.style.visibility = 'hidden'
    
    // 触发下载
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    // 清理URL对象
    URL.revokeObjectURL(url)
    
    alert(`成功导出 ${dataList.value.length} 条数据！`)
    
  } catch (error) {
    console.error('导出数据时发生错误:', error)
    alert('导出数据失败，请重试')
  }
}

// 刷新数据
const refreshData = async () => {
  await initData()
  alert('数据已刷新')
}

// 打开新增模态框
const openAddModal = () => {
  console.log('点击新增数据按钮')
  showAddModal.value = true
  console.log('新增模态框状态已设置为true:', showAddModal.value)
}

// 新增数据
const addItem = async () => {
  console.log('开始新增数据，表单数据：', formData.value)
  
  // 验证表单数据
  if (!formData.value.company || !formData.value.emission || !formData.value.date) {
    console.error('表单数据不完整：', formData.value)
    alert('请填写完整的表单信息（企业名称、排放量、日期）')
    return
  }
  
  try {
    const token = localStorage.getItem('token')
    console.log('获取到的token：', token)
    
    const requestData = {
      enterpriseName: formData.value.company,
      emissionAmount: parseFloat(formData.value.emission),
      emissionDate: formData.value.date
    }
    
    console.log('准备发送到后端的数据：', requestData)
    console.log('调用后端API: POST http://localhost:8080/api/emissions')
    
    const response = await fetch('http://localhost:8080/api/emissions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
        // 移除Authorization头部，允许不登录访问
      },
      body: JSON.stringify(requestData)
    })
    
    console.log('后端API响应状态：', response.status)
     console.log('响应头Content-Type：', response.headers.get('content-type'))
     
     // 先获取原始响应文本
     const responseText = await response.text()
     console.log('原始响应文本：', responseText)
     
     let result
     try {
       result = JSON.parse(responseText)
       console.log('解析后的JSON数据：', result)
     } catch (jsonError) {
       console.error('JSON解析失败：', jsonError)
       console.error('无法解析的响应内容：', responseText)
       alert('服务器返回了无效的JSON格式：' + responseText.substring(0, 200))
       return
     }
    
    if (response.ok && result.success) {
      console.log('数据新增成功，重新获取数据列表')
      alert('数据新增成功！')
      closeModal()
      // 重新获取数据列表
      await refreshData()
    } else {
      console.error('后端返回错误：', result.message)
      alert('新增失败：' + (result.message || '未知错误'))
    }
  } catch (error) {
    console.error('新增数据时发生错误：', error)
    alert('新增数据失败，请重试：' + error.message)
  }
}

// 编辑数据
const editItem = (item) => {
  currentEditId.value = item.id
  formData.value = { ...item }
  showEditModal.value = true
}

// 更新数据
const updateItem = async () => {
  try {
    console.log('开始更新数据，ID:', currentEditId.value)
    console.log('更新的表单数据:', formData.value)
    
    // 验证表单数据
    if (!formData.value.company || !formData.value.emission || !formData.value.date) {
      console.error('表单数据不完整：', formData.value)
      alert('请填写完整的表单信息（企业名称、排放量、日期）')
      return
    }
    
    const requestData = {
      enterpriseName: formData.value.company,
      emissionAmount: parseFloat(formData.value.emission),
      emissionDate: formData.value.date
    }
    
    console.log('准备发送到后端的更新数据：', requestData)
    
    const response = await fetch(`http://localhost:8080/api/emissions/${currentEditId.value}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(requestData)
    })
    
    console.log('更新API响应状态:', response.status)
    
    if (response.ok) {
      const result = await response.json()
      console.log('更新API响应数据:', result)
      
      if (result.success) {
        console.log('数据更新成功，刷新数据列表')
        alert('数据更新成功！')
        closeModal()
        // 重新获取数据列表
        await refreshData()
      } else {
        console.error('后端返回更新失败:', result.message)
        alert('更新失败：' + (result.message || '未知错误'))
      }
    } else {
      console.error('更新请求失败，HTTP状态:', response.status)
      alert('更新请求失败，请重试')
    }
  } catch (error) {
    console.error('更新数据时发生错误:', error)
    alert('更新数据失败，请重试：' + error.message)
  }
}

// 删除数据
const deleteItem = async (id) => {
  if (confirm('确定要删除这条记录吗？')) {
    try {
      console.log('开始删除数据，ID:', id)
      
      const response = await fetch(`http://localhost:8080/api/emissions/${id}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json'
        }
      })
      
      console.log('删除API响应状态:', response.status)
      
      if (response.ok) {
        const result = await response.json()
        console.log('删除API响应数据:', result)
        
        if (result.success) {
          console.log('数据删除成功，刷新数据列表')
          alert('数据删除成功！')
          // 重新获取数据列表
          await refreshData()
        } else {
          console.error('后端返回删除失败:', result.message)
          alert('删除失败：' + (result.message || '未知错误'))
        }
      } else {
        console.error('删除请求失败，HTTP状态:', response.status)
        alert('删除请求失败，请重试')
      }
    } catch (error) {
      console.error('删除数据时发生错误:', error)
      alert('删除数据失败，请重试：' + error.message)
    }
  }
}

// 提交表单
const submitForm = () => {
  console.log('表单提交被触发')
  console.log('当前模态框状态 - showAddModal:', showAddModal.value, 'showEditModal:', showEditModal.value)
  console.log('当前表单数据：', formData.value)
  
  if (showAddModal.value) {
    console.log('执行新增数据操作')
    addItem()
  } else if (showEditModal.value) {
    console.log('执行更新数据操作')
    updateItem()
  } else {
    console.error('未知的表单提交状态')
  }
}

// 关闭弹窗
const closeModal = () => {
  console.log('关闭模态框')
  showAddModal.value = false
  showEditModal.value = false
  currentEditId.value = null
  formData.value = {
    company: '',
    type: '',
    emission: '',
    date: ''
  }
  console.log('模态框已关闭，表单数据已重置')
}

// 组件挂载时检查认证状态并初始化数据
onMounted(async () => {
  checkAuth() // 检查认证状态，无论是否登录都继续
  await initData() // 初始化数据，允许游客查看
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

.nav-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.3s ease;
}

.nav-btn:hover {
  background: #0056b3;
}

.login-btn {
  background: #28a745;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.3s ease;
}

.login-btn:hover {
  background: #218838;
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

.guest-tip {
  margin-top: 15px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 5px;
  border-left: 4px solid #007bff;
}

.guest-tip p {
  margin: 5px 0;
  font-size: 12px;
  color: #6c757d;
}

.guest-notice {
  color: #6c757d;
  font-size: 12px;
  font-style: italic;
}

.login-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 5px;
  cursor: pointer;
  margin: 0 5px;
  transition: background 0.3s ease;
}

.login-btn:hover {
  background: #0056b3;
}

.data-table thead,
.data-table tbody tr {
  display: table;
  width: 100%;
  table-layout: fixed;
}
</style>