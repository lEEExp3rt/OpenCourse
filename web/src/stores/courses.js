// stores/course.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import CourseApi from '@/api/course'

export const useCoursesStore = defineStore('courses', () => {
  const courseList = ref([])
  const loaded = ref(false)

  const fetchAllCourses = async (department_id) => {
    if (loaded.value) return
    // console.log("course get ",currentDepartment.name,"id = ",currentDepartment.id)

    if (department_id == null) {
      console.warn('部门无id，无法获取课程')
      return
    }

    let tempList = []

    try {
      const res = await CourseApi.get_all_course_in_department(department_id)
      if (res.success === true && Array.isArray(res.data)) {
        tempList = res.data
      } else {
        tempList = getDefaultCourses()
      }
    } catch (err) {
      console.error('获取课程失败:', err)
      tempList = getDefaultCourses()
    }

    courseList.value = tempList
    loaded.value = true
  }

  const CreateCourse = async (newCourse) => {
    try {
      const res = await CourseApi.newcourse(newCourse)
      if (res.success === true) {
        courseList.value.push(res.data)
      } else {
        console.warn('新增课程失败，返回异常：', res)
      }
    } catch (err) {
      console.error('新增课程时出错：', err)
    }
  }

  const DeleteCourse = async (id) => {
    try {
      await CourseApi.delete(id)
      courseList.value = courseList.value.filter(course => course.id !== id)
    } catch (err) {
      console.error(`删除课程 ${id} 时出错：`, err)
    }
  }

  function getDefaultCourses() {
    return [
      {
        id: 0,
        code: "CS3140M（21120520）",
        name: "计算理论",
        departmentId: 1,
        courseType:
        {
          description:"通识选修"
        },
        credits: 3
      },
      {
        id: 1,
        code: "CS3136M（21121340）",
        name: "计算机网络",
        departmentId: 2,
        courseType:
        {
          description:"通识选修"
        },
        credits: 4
      },
      {
        id: 2,
        code: "CS2055M（21186033）",
        name: "计算机组成",
        departmentId: 3,
        courseType:
        {
          description:"通识选修"
        },
        credits: 4
      },
      {
        id: 3,
        code: "CS3097M（21121160）",
        name: "Java应用技术",
        departmentId: 4,
        courseType:
        {
          description:"通识选修"
        },
        credits: 2
      }
    ]
  }

  return {
    courseList,
    fetchAllCourses,
    DeleteCourse,
    CreateCourse
  }
})
