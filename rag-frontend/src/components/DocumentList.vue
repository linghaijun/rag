<template>
  <div class="document-section">
    <div class="section-header">
      <span>知识库文档</span>
      <el-button text @click="loadDocuments" size="small">
        <el-icon><Refresh /></el-icon>
      </el-button>
    </div>
    
    <div v-loading="loading" class="document-list">
      <div v-if="!loading && documents.length === 0" class="empty">
        暂无文档
      </div>
      <div v-else>
        <div v-for="doc in documents" :key="doc.id" class="document-item">
          <div class="doc-info">
            <el-icon class="doc-icon"><Document /></el-icon>
            <div class="doc-meta">
              <div class="doc-name">{{ doc.fileName }}</div>
              <div class="doc-stats">
                {{ formatSize(doc.fileSize) }} · {{ doc.chunkCount || 0 }} chunks
              </div>
            </div>
          </div>
          <el-button type="danger" text size="small" @click="handleDelete(doc)">
            删除
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDocuments, deleteDocument } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const documents = ref([])
const loading = ref(false)

const emit = defineEmits(['documents-changed'])

const loadDocuments = async () => {
  loading.value = true
  try {
    const response = await getDocuments()
    documents.value = response.data
  } catch (error) {
    ElMessage.error('加载文档列表失败')
  } finally {
    loading.value = false
  }
}

const handleDelete = async (doc) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除 "${doc.fileName}" 吗？`,
      '删除确认',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    
    await deleteDocument(doc.id)
    ElMessage.success('删除成功')
    await loadDocuments()
    emit('documents-changed')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const formatSize = (bytes) => {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

onMounted(() => {
  loadDocuments()
})

defineExpose({ loadDocuments })
</script>

<style scoped>
.document-section {
  margin-top: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.document-list {
  min-height: 100px;
}

.empty {
  text-align: center;
  color: #909399;
  padding: 24px 0;
  font-size: 14px;
}

.document-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 8px;
  background: #f9f9f9;
  transition: background 0.2s;
}

.document-item:hover {
  background: #f0f0f0;
}

.doc-info {
  display: flex;
  align-items: center;
  gap: 12px;
  overflow: hidden;
}

.doc-icon {
  font-size: 24px;
  color: #409eff;
  flex-shrink: 0;
}

.doc-meta {
  overflow: hidden;
}

.doc-name {
  font-size: 14px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.doc-stats {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}
</style>
