// stores/course.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import ResourceApi from '@/api/resource'

export const useCourseStore = defineStore('course', () => {
  const resourceList = ref([])
  const loaded = ref(false)

  // 获取指定课程的所有资源
  const fetchCourseResources = async (course_id) => {
    if (loaded.value) return

    let tempList = []
    try {
      const res = await ResourceApi.get_resource(course_id)
      if (res.code === '1' && Array.isArray(res.data)) {
        tempList = res.data
      } else {
        tempList = getDefaultResources()
      }
    } catch (err) {
      console.error('获取课程资源失败:', err)
      tempList = getDefaultResources()
    }
    resourceList.value = tempList
    loaded.value = true
  }

  // 添加资源
  const addResource = async (newResource) => {
    try {
      const res = await ResourceApi.add_resource(newResource)
      if (res.code === '1' && res.data) {
        resourceList.value.push(res.data)
        console.log("添加成功：", res.data)
      } else {
        console.warn('添加资源失败，返回异常：', res)
      }
    } catch (err) {
      console.error('添加资源时出错：', err)
    }
  }

  // 删除资源
  const deleteResource = async (resource_id) => {
    try {
      const res = await ResourceApi.delete_resource(resource_id)
      if (res.code === '1') {
        resourceList.value = resourceList.value.filter(item => item.id !== resource_id)
        console.log(`资源 ${resource_id} 删除成功`)
      } else {
        console.warn(`删除资源失败：res =`, res)
      }
    } catch (err) {
      console.error(`删除资源 ${resource_id} 时出错：`, err)
    }
  }

  function getDefaultResources() {
    return [
      {
        id: 0,
        name: "示例资源",
        description: "这是一个默认资源",
        resourceType: 1,
        fileSize: 1024,
        filePath: "/images/open_course.png",
        createdAt: "2024-01-01T00:00:00Z",
        course: "CS3140M",
        user: "admin",
        views: 0,
        likes: 0,
        dislikes: 0
      },
      {
        id: 1,
        name: "示例资源",
        description: "这是一个默认资源",
        resourceType: 1,
        fileSize: 1024,
        filePath: "/images/open_course.png",
        createdAt: "2024-01-01T00:00:00Z",
        course: "CS3140M",
        user: "admin",
        views: 0,
        likes: 0,
        dislikes: 0
      }
    ]
  }

  return {
    resourceList,
    fetchCourseResources,
    addResource,
    deleteResource
  }
})
