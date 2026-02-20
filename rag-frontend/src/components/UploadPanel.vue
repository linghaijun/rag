<template>
  <div class="upload-section">
    <div class="section-header">上传文档</div>
    <el-upload
      ref="uploadRef"
      class="upload-area"
      :auto-upload="false"
      :limit="1"
      :on-change="handleFileChange"
      :on-exceed="handleExceed"
      :file-list="fileList"
      accept=".pdf,.txt,.doc,.docx"
      action="#"
      drag
    >
      <el-icon class="upload-icon"><Upload /></el-icon>
      <div class="upload-text">点击或拖拽上传</div>
      <div class="upload-tip">支持 PDF、TXT、DOC 格式</div>
    </el-upload>
    <el-button 
      v-if="selectedFile"
      type="primary" 
      :loading="uploading" 
      @click="handleUpload"
      style="width: 100%; margin-top: 12px"
    >
      上传到知识库
    </el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import { uploadDocument } from '../api'

const emit = defineEmits(['upload-success'])

const uploadRef = ref()
const fileList = ref([])
const selectedFile = ref(null)
const uploading = ref(false)

const handleFileChange = (file) => {
  selectedFile.value = file.raw
  fileList.value = [file]
}

const handleExceed = () => {
  ElMessage.warning('只能上传一个文件')
}

const handleUpload = async () => {
  if (!selectedFile.value) return
  
  uploading.value = true
  try {
    await uploadDocument(selectedFile.value)
    ElNotification({
      title: '成功',
      message: '文件上传成功',
      type: 'success',
    })
    fileList.value = []
    selectedFile.value = null
    emit('upload-success')
  } catch (error) {
    ElNotification({
      title: '错误',
      message: error.response?.data?.message || '上传失败',
      type: 'error',
    })
  } finally {
    uploading.value = false
  }
}
</script>

<style scoped>
.upload-section {
  margin-bottom: 16px;
}

.section-header {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.upload-area {
  width: 100%;
}

.upload-area :deep(.el-upload-dragger) {
  width: 100%;
  height: 120px;
  border-radius: 8px;
  border: 1px dashed #d9d9d9;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.upload-area :deep(.el-upload-dragger:hover) {
  border-color: #409eff;
}

.upload-icon {
  font-size: 32px;
  color: #c0c4cc;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
  color: #606266;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
