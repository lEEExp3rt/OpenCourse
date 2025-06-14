import { ElMessage } from 'element-plus'

const message = {
  primary(message) {
    ElMessage({type: "primary", message: message})
  },
  success(message) {
    ElMessage({type: "success", message: message})
  },
  warning(message) {
    ElMessage({type: "warning", message: message})
  },
  info(message) {
    ElMessage({type: "info", message: message})
  },
  error(message) {
    ElMessage({type: "error", message: message})
  },
}

export default message
