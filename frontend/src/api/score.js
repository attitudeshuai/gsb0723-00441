import request from '@/utils/request'

export function getScoreList(params) {
  return request.get('/scores', { params })
}

export function inputScores(data) {
  return request.post('/scores', data)
}

export function updateScore(id, data) {
  return request.put(`/scores/${id}`, data)
}

export function getScoreStats(params) {
  return request.get('/scores/stats', { params })
}

export function getScoreTrend(params) {
  return request.get('/scores/trend', { params })
}

export function exportScores(params) {
  return request.get('/scores/export', {
    params,
    responseType: 'blob'
  })
}
