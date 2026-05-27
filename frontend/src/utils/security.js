/**
 * 安全工具类
 * 提供XSS防护、输入验证等安全相关功能
 */

/**
 * HTML转义，防止XSS攻击
 * @param {string} str - 需要转义的字符串
 * @returns {string} 转义后的字符串
 */
export function escapeHtml(str) {
  if (typeof str !== 'string') {
    return str
  }
  const div = document.createElement('div')
  div.textContent = str
  return div.innerHTML
}

/**
 * 移除HTML标签
 * @param {string} str - 需要处理的字符串
 * @returns {string} 移除标签后的字符串
 */
export function stripHtml(str) {
  if (typeof str !== 'string') {
    return str
  }
  return str.replace(/<[^>]*>/g, '')
}

/**
 * 验证邮箱格式
 * @param {string} email - 邮箱地址
 * @returns {boolean} 是否为有效邮箱
 */
export function isValidEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

/**
 * 验证手机号格式（中国大陆）
 * @param {string} phone - 手机号
 * @returns {boolean} 是否为有效手机号
 */
export function isValidPhone(phone) {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone)
}

/**
 * 验证用户名格式（4-20位字母数字下划线）
 * @param {string} username - 用户名
 * @returns {boolean} 是否为有效用户名
 */
export function isValidUsername(username) {
  const usernameRegex = /^[a-zA-Z0-9_]{4,20}$/
  return usernameRegex.test(username)
}

/**
 * 验证密码强度
 * @param {string} password - 密码
 * @returns {object} 包含强度等级和提示的对象
 */
export function checkPasswordStrength(password) {
  let strength = 0
  let tips = []

  if (!password || password.length < 6) {
    return { strength: 0, tips: ['密码长度至少6位'] }
  }

  if (password.length >= 8) strength++
  if (password.length >= 12) strength++

  if (/[a-z]/.test(password)) strength++
  if (/[A-Z]/.test(password)) strength++
  if (/[0-9]/.test(password)) strength++
  if (/[^a-zA-Z0-9]/.test(password)) strength++

  if (!/[a-z]/.test(password)) tips.push('建议包含小写字母')
  if (!/[A-Z]/.test(password)) tips.push('建议包含大写字母')
  if (!/[0-9]/.test(password)) tips.push('建议包含数字')
  if (!/[^a-zA-Z0-9]/.test(password)) tips.push('建议包含特殊字符')

  let level = 'weak'
  if (strength >= 4) level = 'medium'
  if (strength >= 6) level = 'strong'

  return { strength, level, tips }
}

/**
 * 防止CSRF攻击，为请求添加token
 * @param {object} config - Axios请求配置
 * @returns {object} 添加了CSRF token的配置
 */
export function addCsrfToken(config) {
  const csrfToken = localStorage.getItem('csrfToken')
  if (csrfToken) {
    config.headers['X-CSRF-Token'] = csrfToken
  }
  return config
}

export default {
  escapeHtml,
  stripHtml,
  isValidEmail,
  isValidPhone,
  isValidUsername,
  checkPasswordStrength,
  addCsrfToken
}
