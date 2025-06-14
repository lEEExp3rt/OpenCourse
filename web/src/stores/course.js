// stores/course.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import ResourceApi from '@/api/resource'
import { getItem } from "@/utils/storage"

export const useCourseStore = defineStore('course', () => {
  const resourceList = ref([])

  // 获取指定课程的所有资源
  const fetchCourseResources = async (course_id) => {

    let tempList = []
    try {
      // 假设接口返回结构：{ code: '1', data: [...] }
      const res = await ResourceApi.get_resource(course_id)
      if (res.success === true && Array.isArray(res.data)) {
        tempList = res.data
      } else {
        tempList = getDefaultResources()
      }
    } catch (err) {
      console.error('获取课程资源失败:', err)
      tempList = getDefaultResources()
    }
    resourceList.value = tempList
  }

  // 添加资源
  const addResource = async (newResource) => {
    try {
      const res = await ResourceApi.add_resource(newResource)
      if (res.success === true && res.data) {
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
      if (res.success === true) {
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
        name: "open_course.png",
        description: "这是一个默认资源",
        resourceTypeId: 51,
        fileSize: 1024,
        filePath: "/images/open_course.png",
        createdAt: "2024-01-01T00:00:00Z",
        course: "CS3140M",
        user: "admin",
        views: 0,
        likes: 0,
        dislikes: 0,
        liked: true
      },
      {
        id: 1,
        name: "示例资源",
        description: "这是一个默认资源",
        resourceTypeId: 52,
        fileSize: 1024,
        filePath: "/images/open_course.png",
        createdAt: "2024-01-01T00:00:00Z",
        course: "CS3140M",
        user: "admin",
        views: 0,
        likes: 0,
        dislikes: 0,
        liked: false
      }
    ]
  }


  // 下载资源
  const downloadResource = async (resource) => {
    try {
      const res = await ResourceApi.get_resource_view(resource.id)
      if (res) {
        // 创建 Blob 并生成下载链接
        const blob = new Blob([res], { type: 'application/octet-stream' })
        const url = window.URL.createObjectURL(blob)

        const link = document.createElement('a')
        link.href = url
        
        link.download = getItem('download-filename')|| resource.name || '下载文件'
        document.body.appendChild(link)
        link.click()

        // 清理链接
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        console.log(`资源 ${resource.id} 下载成功`)
      } else {
        console.warn(`下载资源失败：响应为空`)
      }
    } catch (err) {
      console.error(`下载资源 ${resource.id} 时出错：`, err)
    }
  }

  // 点赞资源
  const likeResource = async (resource_id) => {
    try {
      const res = await ResourceApi.like_resource(resource_id)
      if (res.success !== true) {
        console.warn(`点赞资源失败：res =`, res)
      }
    } catch (err) {
      console.error(`点赞资源 ${resource_id} 时出错：`, err)
    }
  }

  const unlikeResource = async (resource_id) => {
    try {
      const res = await ResourceApi.unlike_resource(resource_id)
      if (res.success !== true) {
        console.warn(`取消点赞资源失败：res =`, res)
      }
    } catch (err) {
      console.error(`取消点赞资源 ${resource_id} 时出错：`, err)
    }
  }

  return {
    resourceList,
    fetchCourseResources,
    addResource,
    deleteResource, 
    downloadResource,
    likeResource,
    unlikeResource
  }
})
