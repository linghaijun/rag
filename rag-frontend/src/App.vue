<template>
  <div class="app-container">
    <el-container>
      <el-aside width="350px" class="sidebar">
        <div class="sidebar-header">
          <div class="logo">
            <el-icon size="24"><ChatDotRound /></el-icon>
            <span>RAG 智能问答</span>
          </div>
        </div>
        <div class="sidebar-content">
          <UploadPanel @upload-success="refreshDocuments" />
          <DocumentList ref="docListRef" />
        </div>
      </el-aside>
      <el-main class="main-content">
        <ChatPanel />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import UploadPanel from './components/UploadPanel.vue'
import DocumentList from './components/DocumentList.vue'
import ChatPanel from './components/ChatPanel.vue'

const docListRef = ref(null)

const refreshDocuments = () => {
  if (docListRef.value) {
    docListRef.value.loadDocuments()
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  overflow: hidden;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

.app-container {
  height: 100vh;
  background: #f0f2f5;
}

.el-container {
  height: 100%;
}

.sidebar {
  background: #fff;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e8e8e8;
  flex-shrink: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.main-content {
  padding: 0;
  background: #f0f2f5;
  overflow: hidden;
}
</style>
