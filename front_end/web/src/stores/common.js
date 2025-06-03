import { defineStore } from 'pinia'
import CommonApi from "@/api/common.js";

export const useCommonModule = defineStore('common', {
  state: () => ({}),

  actions: {
    async upload(data) {
      try {
        const response = await CommonApi.upload(data);
        return response
      } catch (error) {
        throw new Error(error.message)
      }
    },

    async captcha(data) {
      try {
        const response = await CommonApi.captcha(data);
        return response
      } catch (error) {
        throw new Error(error.message)
      }
    },
  }
})
