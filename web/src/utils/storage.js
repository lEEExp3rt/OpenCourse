export function getItem(key) {
  const value = localStorage.getItem(key)
  try {
    return value ? value : null
  } catch (e) {
    console.warn(`getItem: 无法解析本地存储 key=${key} 的值：`, e)
    return null
  }
}

export function setItem(key, value) {
  localStorage.setItem(key, value)
}

export function removeItem(key) {
  localStorage.removeItem(key)
}
