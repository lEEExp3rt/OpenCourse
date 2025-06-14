import { defineStore } from 'pinia'
import CommonApi from "@/api/common.js"

import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'

// 扩展 dayjs 插件
dayjs.extend(utc)
dayjs.extend(timezone)

export const useCommonModule = defineStore('common', {
  state: () => ({}),

  actions: {
    async upload(data) {
      try {
        const response = await CommonApi.upload(data)
        return response
      } catch (error) {
        throw new Error(error.message)
      }
    },

    async captcha(data) {
      try {
        const response = await CommonApi.captcha(data)
        return response
      } catch (error) {
        throw new Error(error.message)
      }
    },

    // 使用 dayjs + timezone 格式化时间
    formatDate(dateStr, timeZone = 'Asia/Shanghai', format = 'YYYY年MM月DD日 HH:mm:ss') {
      return dayjs.utc(dateStr).tz(timeZone).format(format)
    }
  }
})
