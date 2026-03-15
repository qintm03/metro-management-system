const TOKEN_KEY = 'metro_token'
const USER_KEY = 'metro_user'
const ROLE_KEY = 'metro_role'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getUser() {
  const userStr = localStorage.getItem(USER_KEY)
  return userStr ? JSON.parse(userStr) : null
}

export function setUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function removeUser() {
  localStorage.removeItem(USER_KEY)
}

export function getRole() {
  return localStorage.getItem(ROLE_KEY)
}

export function setRole(role) {
  localStorage.setItem(ROLE_KEY, role)
}

export function removeRole() {
  localStorage.removeItem(ROLE_KEY)
}

export function isAdmin() {
  return getRole() === '管理员'
}

export function isUser() {
  return getRole() === '普通用户'
}

export function isLoggedIn() {
  return !!getToken()
}

export function clearAuth() {
  removeToken()
  removeUser()
  removeRole()
}

export function hasPermission(requiredRole) {
  const currentRole = getRole()
  if (!currentRole) return false
  return currentRole === requiredRole
}
