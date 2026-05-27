/**
 * 性能优化工具类
 * 提供防抖、节流、懒加载等性能优化功能
 */

/**
 * 防抖函数
 * @param {Function} func - 需要防抖的函数
 * @param {number} delay - 延迟时间（毫秒）
 * @returns {Function} 防抖后的函数
 */
export function debounce(func, delay = 300) {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      func.apply(this, args)
    }, delay)
  }
}

/**
 * 节流函数
 * @param {Function} func - 需要节流的函数
 * @param {number} delay - 间隔时间（毫秒）
 * @returns {Function} 节流后的函数
 */
export function throttle(func, delay = 300) {
  let lastTime = 0
  return function (...args) {
    const now = Date.now()
    if (now - lastTime >= delay) {
      lastTime = now
      func.apply(this, args)
    }
  }
}

/**
 * 图片懒加载
 * @param {string} selector - 图片选择器
 */
export function lazyLoadImages(selector = 'img[data-src]') {
  const images = document.querySelectorAll(selector)
  
  if ('IntersectionObserver' in window) {
    const imageObserver = new IntersectionObserver((entries, observer) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const img = entry.target
          if (img.dataset.src) {
            img.src = img.dataset.src
            img.removeAttribute('data-src')
          }
          observer.unobserve(img)
        }
      })
    })
    
    images.forEach(img => imageObserver.observe(img))
  } else {
    // 降级处理
    const lazyLoadHandler = throttle(() => {
      images.forEach(img => {
        const rect = img.getBoundingClientRect()
        if (rect.top < window.innerHeight && rect.bottom > 0) {
          if (img.dataset.src) {
            img.src = img.dataset.src
            img.removeAttribute('data-src')
          }
        }
      })
    }, 100)
    
    window.addEventListener('scroll', lazyLoadHandler)
    lazyLoadHandler()
  }
}

/**
 * 页面性能监控
 */
export function performanceMonitor() {
  if (window.performance) {
    window.addEventListener('load', () => {
      setTimeout(() => {
        const timing = window.performance.timing
        const domReady = timing.domContentLoadedEventEnd - timing.navigationStart
        const loadTime = timing.loadEventEnd - timing.navigationStart
        const firstPaint = timing.responseStart - timing.navigationStart
        
        console.log('=== 页面性能数据 ===')
        console.log(`DOM Ready: ${domReady}ms`)
        console.log(`页面完全加载: ${loadTime}ms`)
        console.log(`首屏时间: ${firstPaint}ms`)
      }, 0)
    })
  }
}

/**
 * 缓存工具
 */
export class Cache {
  constructor(prefix = 'cache_', defaultTTL = 300000) {
    this.prefix = prefix
    this.defaultTTL = defaultTTL
  }

  set(key, value, ttl = this.defaultTTL) {
    const item = {
      value,
      expiry: Date.now() + ttl
    }
    localStorage.setItem(this.prefix + key, JSON.stringify(item))
  }

  get(key) {
    const itemStr = localStorage.getItem(this.prefix + key)
    if (!itemStr) return null
    
    const item = JSON.parse(itemStr)
    if (Date.now() > item.expiry) {
      localStorage.removeItem(this.prefix + key)
      return null
    }
    return item.value
  }

  remove(key) {
    localStorage.removeItem(this.prefix + key)
  }

  clear() {
    Object.keys(localStorage)
      .filter(key => key.startsWith(this.prefix))
      .forEach(key => localStorage.removeItem(key))
  }
}

export default {
  debounce,
  throttle,
  lazyLoadImages,
  performanceMonitor,
  Cache
}
