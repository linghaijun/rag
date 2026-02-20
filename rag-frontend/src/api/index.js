import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 60000,
  headers: {
    'Content-Type': 'application/json'
  }
})

export const uploadDocument = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return apiClient.post('/documents', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const getDocuments = () => {
  return apiClient.get('/documents')
}

export const deleteDocument = (id) => {
  return apiClient.delete(`/documents/${id}`)
}

export const chat = (message) => {
  return apiClient.post('/chat', { message })
}
