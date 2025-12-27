import api from './axios'

export default {
  getMyMedication() {
    return api.get('/medication/user')
  },

  getTodayLogs() {
    return api.get('/medication/log/today')
  },

  saveLog(data) {
    return api.post('/medication/log', data)
  }
}
