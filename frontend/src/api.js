const API_BASE = 'http://localhost:8080/api'
const TOKEN_KEY = 'calorietrack_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

async function request(path, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
  }

  const token = getToken()
  if (token) headers.Authorization = `Bearer ${token}`

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  })

  const isJson = response.headers.get('content-type')?.includes('application/json')
  const body = isJson ? await response.json() : null

  if (!response.ok) {
    if (response.status === 401) clearToken()
    throw new Error(body?.message || 'Request failed')
  }

  return body
}

export const api = {
  login: (data) => request('/auth/login', { method: 'POST', body: JSON.stringify(data) }),
  register: (data) => request('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  me: () => request('/auth/me'),
  dashboard: () => request('/dashboard'),
  searchFoods: (q) => request(`/foods/search?query=${encodeURIComponent(q)}`),
  getEntries: (date) => request(`/entries?date=${date}`),
  addEntry: (data) => request('/entries', { method: 'POST', body: JSON.stringify(data) }),
  deleteEntry: (id) => request(`/entries/${id}`, { method: 'DELETE' }),
  updateProfile: (data) => request('/profile', { method: 'PUT', body: JSON.stringify(data) }),
}
