<template>
  <div class="chat-wrapper">
    <div class="chat-header">
      <span>智能问答</span>
      <el-button v-if="messages.length > 0" text @click="clearMessages" size="small">
        清空对话
      </el-button>
    </div>
    <div class="chat-messages" ref="chatMessages">
      <div v-if="messages.length === 0" class="empty-state">
        <el-icon size="48" color="#c0c4cc"><ChatDotRound /></el-icon>
        <p>开始对话</p>
        <span>输入问题，我将基于知识库为您解答</span>
      </div>
      <div v-else class="message-list">
        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message', msg.role]"
        >
          <div class="message-content">
            <div class="message-role">{{ msg.role === 'user' ? '你' : 'AI' }}</div>
            <div class="message-text" v-html="formatMessage(msg.content)"></div>
          </div>
        </div>
        <div v-if="sending" class="message assistant">
          <div class="message-content">
            <div class="message-role">AI</div>
            <div class="message-text typing">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="chat-input-area">
      <el-input
        v-model="inputMessage"
        placeholder="输入您的问题..."
        :disabled="sending"
        @keyup.enter="handleSend"
        size="large"
      />
      <el-button type="primary" :loading="sending" @click="handleSend" size="large">
        发送
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { chat } from '../api'
import { ElMessage } from 'element-plus'

const messages = ref([])
const inputMessage = ref('')
const sending = ref(false)
const chatMessages = ref(null)

const handleSend = async () => {
  if (!inputMessage.value.trim() || sending.value) return
  
  const userMessage = inputMessage.value.trim()
  inputMessage.value = ''
  
  messages.value.push({ role: 'user', content: userMessage })
  sending.value = true
  
  await nextTick()
  scrollToBottom()
  
  try {
    const response = await chat(userMessage)
    messages.value.push({ role: 'assistant', content: response.data.answer })
  } catch (error) {
    ElMessage.error('请求失败，请稍后重试')
    messages.value.push({ 
      role: 'assistant', 
      content: '抱歉，发生了错误，请稍后重试' 
    })
  } finally {
    sending.value = false
    await nextTick()
    scrollToBottom()
  }
}

const clearMessages = () => {
  messages.value = []
}

const scrollToBottom = () => {
  if (chatMessages.value) {
    chatMessages.value.scrollTop = chatMessages.value.scrollHeight
  }
}

const formatMessage = (content) => {
  return content.replace(/\n/g, '<br>')
}
</script>

<style scoped>
.chat-wrapper {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.chat-header {
  padding: 16px 24px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.empty-state {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.empty-state p {
  margin-top: 16px;
  font-size: 18px;
  color: #606266;
}

.empty-state span {
  margin-top: 8px;
  font-size: 14px;
}

.message-list {
  max-width: 800px;
  margin: 0 auto;
}

.message {
  margin-bottom: 24px;
}

.message-content {
  max-width: 80%;
}

.message.user .message-content {
  margin-left: auto;
}

.message-role {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.message-text {
  padding: 12px 16px;
  border-radius: 8px;
  line-height: 1.6;
}

.message.user .message-text {
  background: #409eff;
  color: white;
}

.message.assistant .message-text {
  background: #f4f4f5;
  color: #303133;
}

.typing {
  display: flex;
  gap: 4px;
  padding: 16px;
}

.typing span {
  width: 8px;
  height: 8px;
  background: #909399;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
}

.chat-input-area {
  padding: 16px 24px;
  border-top: 1px solid #e8e8e8;
  display: flex;
  gap: 12px;
  background: #fafafa;
}

.chat-input-area .el-input {
  flex: 1;
}
</style>
