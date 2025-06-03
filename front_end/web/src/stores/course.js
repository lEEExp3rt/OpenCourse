// stores/course.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request' // 你封装的 axios 实例

export const useCourseStore = defineStore('course', () => {
  const courseList = ref([])
  const loaded = ref(false)

  const fetchAllCourses = async () => {
    if (loaded.value) return

    const tempList = []
    let id = 0

    while (true) {
      try {
        const res = await request.get(`/course/${id}`)

        if (String(res.code) === '1') {
          tempList.push(res.data) // 假设返回结构为 { code: '1', data: { id, name, ... } }
          id++
        } else {
          break
        }
      } catch (err) {
        // 避免接口错误导致死循环
        console.error(`获取课程 ${id} 失败:`, err)
        break
      }
    }

    courseList.value = tempList
    loaded.value = true
  }

  return {
    courseList,
    fetchAllCourses
  }
})
