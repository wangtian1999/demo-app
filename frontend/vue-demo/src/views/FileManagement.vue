<template>
  <div class="file-management-container">
    <div class="management-header">
      <h2>文件管理</h2>
      <div class="user-info">
        <span class="welcome-text">欢迎，{{ currentUser }}</span>
        <span class="role-badge">{{ userRole }}</span>
      </div>
      <div class="header-actions">
        <button @click="$router.push('/simple-query')" class="nav-btn">数据管理</button>
        <button v-if="isAuthenticated" @click="logout" class="logout-btn">登出</button>
        <button v-if="!isAuthenticated" @click="$router.push('/simple-login')" class="login-btn">登录</button>
        <button @click="$router.push('/')" class="back-btn">返回首页</button>
      </div>
    </div>

    <div class="main-content">
      <!-- 左侧操作区域 -->
      <div class="left-panel">
        <!-- 文件上传区域 -->
        <div class="upload-section">
          <h3>文件上传</h3>
          <div class="upload-area" @click="triggerFileInput" @dragover.prevent @drop="handleDrop">
            <input 
              ref="fileInput" 
              type="file" 
              multiple 
              @change="handleFileSelect" 
              style="display: none"
              accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.jpg,.jpeg,.png,.gif,.csv,.txt"
            />
            <div class="upload-content">
              <div class="upload-icon">📁</div>
              <p>点击或拖拽文件到此处上传</p>
              <p class="upload-tip">支持 PDF、Office文档、图片、CSV、TXT等格式，最大10MB</p>
            </div>
          </div>
          
          <!-- 上传进度 -->
          <div v-if="uploadProgress.length > 0" class="upload-progress">
            <h4>上传进度</h4>
            <div v-for="(progress, index) in uploadProgress" :key="index" class="progress-item">
              <span class="file-name">{{ progress.fileName }}</span>
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: progress.percent + '%' }"></div>
              </div>
              <span class="progress-text">{{ progress.percent }}%</span>
            </div>
          </div>
        </div>

        <!-- 文件筛选 -->
        <div class="filter-section">
          <h3>文件筛选</h3>
          <select v-model="selectedFileType" @change="filterFiles" class="filter-select">
            <option value="">所有类型</option>
            <option value="pdf">PDF文档</option>
            <option value="doc">Word文档</option>
            <option value="xls">Excel表格</option>
            <option value="ppt">PowerPoint</option>
            <option value="image">图片文件</option>
          </select>
          
          <input 
            v-model="searchKeyword" 
            type="text" 
            placeholder="搜索文件名..." 
            class="search-input"
            @input="filterFiles"
          />
        </div>

        <!-- 统计信息 -->
        <div class="stats-section">
          <h3>统计信息</h3>
          <div class="stat-item">
            <span class="stat-label">总文件数：</span>
            <span class="stat-value">{{ filteredFiles.length }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">总大小：</span>
            <span class="stat-value">{{ formatFileSize(totalSize) }}</span>
          </div>
        </div>
      </div>

      <!-- 右侧文件列表 -->
      <div class="right-panel">
        <div class="file-list-header">
          <h3>文件列表</h3>
          <div class="view-controls">
            <button @click="viewMode = 'list'" :class="{ active: viewMode === 'list' }" class="view-btn">列表</button>
            <button @click="viewMode = 'grid'" :class="{ active: viewMode === 'grid' }" class="view-btn">网格</button>
            <button @click="refreshFiles" class="refresh-btn">刷新</button>
          </div>
        </div>

        <!-- 文件列表视图 -->
        <div v-if="viewMode === 'list'" class="file-list">
          <div class="list-header">
            <span class="col-name">文件名</span>
            <span class="col-type">类型</span>
            <span class="col-size">大小</span>
            <span class="col-date">上传时间</span>
            <span class="col-actions">操作</span>
          </div>
          
          <div v-if="loading" class="loading">加载中...</div>
          
          <div v-else-if="filteredFiles.length === 0" class="empty-state">
            <div class="empty-icon">📂</div>
            <p>暂无文件</p>
          </div>
          
          <div v-else>
            <div v-for="file in filteredFiles" :key="file.id" class="file-item">
              <span class="file-name" :title="file.fileName">
                <span class="file-icon">{{ getFileIcon(file.fileType) }}</span>
                {{ file.fileName }}
              </span>
              <span class="file-type">{{ file.fileType.toUpperCase() }}</span>
              <span class="file-size">{{ formatFileSize(file.fileSize) }}</span>
              <span class="file-date">{{ formatDate(file.createTime) }}</span>
              <span class="file-actions">
                <button @click="downloadFile(file)" class="action-btn download">下载</button>
                <button v-if="isAuthenticated" @click="deleteFile(file)" class="action-btn delete">删除</button>
              </span>
            </div>
          </div>
        </div>

        <!-- 文件网格视图 -->
        <div v-if="viewMode === 'grid'" class="file-grid">
          <div v-if="loading" class="loading">加载中...</div>
          
          <div v-else-if="filteredFiles.length === 0" class="empty-state">
            <div class="empty-icon">📂</div>
            <p>暂无文件</p>
          </div>
          
          <div v-else class="grid-container">
            <div v-for="file in filteredFiles" :key="file.id" class="file-card">
              <div class="card-icon">{{ getFileIcon(file.fileType) }}</div>
              <div class="card-name" :title="file.fileName">{{ file.fileName }}</div>
              <div class="card-info">
                <span class="card-type">{{ file.fileType.toUpperCase() }}</span>
                <span class="card-size">{{ formatFileSize(file.fileSize) }}</span>
              </div>
              <div class="card-date">{{ formatDate(file.createTime) }}</div>
              <div class="card-actions">
                <button @click="downloadFile(file)" class="action-btn download">下载</button>
                <button v-if="isAuthenticated" @click="deleteFile(file)" class="action-btn delete">删除</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 删除确认对话框 -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="showDeleteModal = false">
      <div class="modal" @click.stop>
        <h3>确认删除</h3>
        <p>确定要删除文件 "{{ fileToDelete?.fileName }}" 吗？</p>
        <div class="modal-actions">
          <button @click="showDeleteModal = false" class="cancel-btn">取消</button>
          <button @click="confirmDelete" class="confirm-btn">确认删除</button>
        </div>
      </div>
    </div>

    <!-- 消息提示 -->
    <div v-if="message" :class="['message', messageType]">
      {{ message }}
    </div>
  </div>
</template>

<script>
export default {
  name: 'FileManagement',
  data() {
    return {
      currentUser: '',
      userRole: '',
      isAuthenticated: false,
      files: [],
      filteredFiles: [],
      selectedFileType: '',
      searchKeyword: '',
      viewMode: 'list',
      loading: false,
      uploadProgress: [],
      showDeleteModal: false,
      fileToDelete: null,
      message: '',
      messageType: 'success'
    }
  },
  computed: {
    totalSize() {
      return this.files.reduce((total, file) => total + file.fileSize, 0)
    }
  },
  mounted() {
    this.checkAuthStatus()
    this.loadFiles()
  },
  methods: {
    checkAuthStatus() {
      const token = localStorage.getItem('token')
      const username = localStorage.getItem('username')
      const role = localStorage.getItem('role')
      
      if (token && username) {
        this.isAuthenticated = true
        this.currentUser = username
        this.userRole = role || '用户'
      } else {
        this.isAuthenticated = false
        this.currentUser = '游客'
        this.userRole = '访客'
      }
    },
    
    async loadFiles() {
      this.loading = true
      try {
        const token = localStorage.getItem('token')
        const response = await fetch('http://localhost:8080/api/files/user', {
          headers: {
            'Authorization': token ? `Bearer ${token}` : '',
            'Content-Type': 'application/json'
          }
        })
        
        if (response.ok) {
          const data = await response.json()
          this.files = data.data || []
          this.filterFiles()
        } else {
          this.showMessage('加载文件列表失败', 'error')
        }
      } catch (error) {
        console.error('加载文件失败:', error)
        this.showMessage('网络错误，请稍后重试', 'error')
      } finally {
        this.loading = false
      }
    },
    
    triggerFileInput() {
      this.$refs.fileInput.click()
    },
    
    handleFileSelect(event) {
      const files = Array.from(event.target.files)
      this.uploadFiles(files)
    },
    
    handleDrop(event) {
      event.preventDefault()
      const files = Array.from(event.dataTransfer.files)
      this.uploadFiles(files)
    },
    
    async uploadFiles(files) {
      if (!this.isAuthenticated) {
        this.showMessage('请先登录后再上传文件', 'error')
        return
      }
      
      for (const file of files) {
        if (file.size > 10 * 1024 * 1024) {
          this.showMessage(`文件 ${file.name} 超过10MB限制`, 'error')
          continue
        }
        
        await this.uploadSingleFile(file)
      }
    },
    
    async uploadSingleFile(file) {
      const progressItem = {
        fileName: file.name,
        percent: 0
      }
      this.uploadProgress.push(progressItem)
      
      try {
        const formData = new FormData()
        formData.append('file', file)
        
        const token = localStorage.getItem('token')
        const response = await fetch('http://localhost:8080/api/files/upload', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`
          },
          body: formData
        })
        
        progressItem.percent = 100
        
        if (response.ok) {
          const result = await response.json()
          this.showMessage(`文件 ${file.name} 上传成功`, 'success')
          this.loadFiles() // 重新加载文件列表
        } else {
          const error = await response.json()
          this.showMessage(`文件 ${file.name} 上传失败: ${error.message}`, 'error')
        }
      } catch (error) {
        console.error('上传失败:', error)
        this.showMessage(`文件 ${file.name} 上传失败`, 'error')
      } finally {
        // 移除进度项
        setTimeout(() => {
          const index = this.uploadProgress.indexOf(progressItem)
          if (index > -1) {
            this.uploadProgress.splice(index, 1)
          }
        }, 2000)
      }
    },
    
    async downloadFile(file) {
      try {
        const token = localStorage.getItem('token')
        const response = await fetch(`http://localhost:8080/api/files/${file.id}`, {
          headers: {
            'Authorization': token ? `Bearer ${token}` : ''
          }
        })
        
        if (response.ok) {
          const blob = await response.blob()
          const url = window.URL.createObjectURL(blob)
          const a = document.createElement('a')
          a.href = url
          a.download = file.fileName
          document.body.appendChild(a)
          a.click()
          window.URL.revokeObjectURL(url)
          document.body.removeChild(a)
          this.showMessage('文件下载成功', 'success')
        } else {
          this.showMessage('文件下载失败', 'error')
        }
      } catch (error) {
        console.error('下载失败:', error)
        this.showMessage('下载失败，请稍后重试', 'error')
      }
    },
    
    deleteFile(file) {
      if (!this.isAuthenticated) {
        this.showMessage('请先登录后再删除文件', 'error')
        return
      }
      this.fileToDelete = file
      this.showDeleteModal = true
    },
    
    async confirmDelete() {
      if (!this.fileToDelete) return
      
      if (!this.isAuthenticated) {
        this.showMessage('请先登录后再删除文件', 'error')
        this.showDeleteModal = false
        this.fileToDelete = null
        return
      }
      
      try {
        const token = localStorage.getItem('token')
        const response = await fetch(`http://localhost:8080/api/files/${this.fileToDelete.id}`, {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })
        
        if (response.ok) {
          this.showMessage('文件删除成功', 'success')
          this.loadFiles() // 重新加载文件列表
        } else {
          this.showMessage('文件删除失败', 'error')
        }
      } catch (error) {
        console.error('删除失败:', error)
        this.showMessage('删除失败，请稍后重试', 'error')
      } finally {
        this.showDeleteModal = false
        this.fileToDelete = null
      }
    },
    
    filterFiles() {
      let filtered = this.files
      
      // 按文件类型筛选
      if (this.selectedFileType) {
        if (this.selectedFileType === 'image') {
          filtered = filtered.filter(file => 
            ['jpg', 'jpeg', 'png', 'gif'].includes(file.fileType.toLowerCase())
          )
        } else {
          filtered = filtered.filter(file => 
            file.fileType.toLowerCase().includes(this.selectedFileType)
          )
        }
      }
      
      // 按关键词搜索
      if (this.searchKeyword) {
        filtered = filtered.filter(file => 
          file.fileName.toLowerCase().includes(this.searchKeyword.toLowerCase())
        )
      }
      
      this.filteredFiles = filtered
    },
    
    refreshFiles() {
      this.loadFiles()
    },
    
    getFileIcon(fileType) {
      const type = fileType.toLowerCase()
      if (['jpg', 'jpeg', 'png', 'gif'].includes(type)) return '🖼️'
      if (['pdf'].includes(type)) return '📄'
      if (['doc', 'docx'].includes(type)) return '📝'
      if (['xls', 'xlsx'].includes(type)) return '📊'
      if (['ppt', 'pptx'].includes(type)) return '📋'
      return '📁'
    },
    
    formatFileSize(bytes) {
      if (bytes === 0) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    },
    
    formatDate(dateString) {
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { hour12: false })
    },
    
    showMessage(text, type = 'success') {
      this.message = text
      this.messageType = type
      setTimeout(() => {
        this.message = ''
      }, 3000)
    },
    
    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      this.isAuthenticated = false
      this.currentUser = '游客'
      this.userRole = '访客'
      this.$router.push('/simple-login')
    }
  }
}
</script>

<style scoped>
.file-management-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.management-header {
  background: rgba(255, 255, 255, 0.95);
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.management-header h2 {
  color: #333;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.welcome-text {
  color: #666;
  font-size: 14px;
}

.role-badge {
  background: #4CAF50;
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.nav-btn, .logout-btn, .login-btn, .back-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.nav-btn {
  background: #2196F3;
  color: white;
}

.logout-btn {
  background: #f44336;
  color: white;
}

.login-btn {
  background: #4CAF50;
  color: white;
}

.back-btn {
  background: #9E9E9E;
  color: white;
}

.nav-btn:hover, .logout-btn:hover, .login-btn:hover, .back-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.main-content {
  display: flex;
  gap: 20px;
  height: calc(100vh - 140px);
}

.left-panel {
  width: 300px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.right-panel {
  flex: 1;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.upload-section h3,
.filter-section h3,
.stats-section h3 {
  color: #333;
  margin-bottom: 15px;
  font-size: 16px;
}

.upload-area {
  border: 2px dashed #ddd;
  border-radius: 8px;
  padding: 30px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 20px;
}

.upload-area:hover {
  border-color: #2196F3;
  background: rgba(33, 150, 243, 0.05);
}

.upload-content {
  pointer-events: none;
}

.upload-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.upload-tip {
  color: #999;
  font-size: 12px;
  margin-top: 5px;
}

.upload-progress {
  margin-top: 15px;
}

.progress-item {
  margin-bottom: 10px;
}

.file-name {
  display: block;
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 5px;
}

.progress-fill {
  height: 100%;
  background: #4CAF50;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 12px;
  color: #666;
}

.filter-select,
.search-input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  margin-bottom: 10px;
  font-size: 14px;
}

.stats-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.stat-value {
  color: #333;
  font-weight: bold;
  font-size: 14px;
}

.file-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.view-controls {
  display: flex;
  gap: 10px;
}

.view-btn,
.refresh-btn {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
}

.view-btn.active {
  background: #2196F3;
  color: white;
  border-color: #2196F3;
}

.refresh-btn {
  background: #4CAF50;
  color: white;
  border-color: #4CAF50;
}

.list-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1.5fr 1.5fr;
  gap: 15px;
  padding: 10px 0;
  border-bottom: 2px solid #eee;
  font-weight: bold;
  color: #333;
  font-size: 14px;
}

.file-item {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1.5fr 1.5fr;
  gap: 15px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  align-items: center;
  font-size: 14px;
}

.file-name {
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-icon {
  font-size: 16px;
}

.file-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 4px 8px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
}

.action-btn.download {
  background: #2196F3;
  color: white;
}

.action-btn.delete {
  background: #f44336;
  color: white;
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.file-card {
  background: white;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
  transition: all 0.3s ease;
}

.file-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-icon {
  font-size: 32px;
  margin-bottom: 10px;
}

.card-name {
  font-weight: bold;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 14px;
}

.card-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 12px;
  color: #666;
}

.card-date {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.card-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #666;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  max-width: 400px;
  width: 90%;
}

.modal h3 {
  margin-bottom: 15px;
  color: #333;
}

.modal p {
  margin-bottom: 20px;
  color: #666;
}

.modal-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.cancel-btn,
.confirm-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.cancel-btn {
  background: #9E9E9E;
  color: white;
}

.confirm-btn {
  background: #f44336;
  color: white;
}

.message {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 12px 20px;
  border-radius: 6px;
  color: white;
  font-size: 14px;
  z-index: 1001;
  animation: slideIn 0.3s ease;
}

.message.success {
  background: #4CAF50;
}

.message.error {
  background: #f44336;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
    height: auto;
  }
  
  .left-panel {
    width: 100%;
    margin-bottom: 20px;
  }
  
  .management-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .header-actions {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .list-header,
  .file-item {
    grid-template-columns: 1fr;
    gap: 5px;
  }
  
  .grid-container {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}
</style>