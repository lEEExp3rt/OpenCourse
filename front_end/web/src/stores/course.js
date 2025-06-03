// stores/course.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import CourseApi from '@/api/course'

export const useCourseStore = defineStore('course', () => {
  const courseList = ref([])
  const loaded = ref(false)

  const fetchAllCourses = async () => {
    if (loaded.value) return
    console.log("here")
    
    let tempList = []  // 改成 let，方便重新赋值
  
    try {
      // 假设接口返回结构：{ code: '1', data: [...] }
      const res = await CourseApi.query()
      if (res.code === '1' && Array.isArray(res.data)) {
        tempList = res.data
      } else {
        // 接口返回异常，使用默认数据
        tempList = getDefaultCourses()
      }
    } catch (err) {
      console.error(`获取课程失败:`, err)
      tempList = getDefaultCourses()
    }
  
    courseList.value = tempList
    loaded.value = true
  }
  
  // 抽取默认课程数据为函数，方便复用和代码清晰
  function getDefaultCourses() {
    return [
      {
        id: 0,
        code: "CS3140M（21120520）",
        name: "计算理论",
        departmentId: 1,
        typeId: 11,
        credits: 3
      },
      {
        id: 1,
        code: "CS3136M（21121340）",
        name: "计算机网络",
        departmentId: 2,
        typeId: 12,
        credits: 4
      },
      {
        id: 2,
        code: "CS2055M（21186033）",
        name: "计算机组成",
        departmentId: 3,
        typeId: 13,
        credits: 4
      },
      {
        id: 3,
        code: "CS3097M（21121160）",
        name: "Java应用技术",
        departmentId: 4,
        typeId: 14,
        credits: 2
      }
    ]
  }
  

  const DeleteCourse = async (id) => {
    try {
      await CourseApi.delete(id)
      courseList.value = courseList.value.filter(course => course.id !== id)
    } catch (err) {
      console.error(`删除课程 ${id} 时出错：`, err)
    }
  }
  return {
    courseList,
    fetchAllCourses,
    DeleteCourse
  }
})
