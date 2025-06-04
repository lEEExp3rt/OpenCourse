import { defineStore } from 'pinia'
import { ref } from 'vue'
import DepartmentApi from '@/api/department'

export const useDepartmentsStore = defineStore('departments', () => {
  const departmentList = ref([])
  const loaded = ref(false)

  // 新增：当前选中的部门
  const currentDepartment = ref(null)

  const fetchAllDepartments = async () => {
    if (loaded.value) return

    let tempList = []

    try {
      const res = await DepartmentApi.get_all_departments()
      if (res.success === true && Array.isArray(res.data)) {
        tempList = res.data
      } else {
        tempList = getDefaultDepartments()
      }
    } catch (err) {
      console.error(`获取部门失败:`, err)
      tempList = getDefaultDepartments()
    }

    departmentList.value = tempList
    loaded.value = true
  }

  const createDepartment = async (newDepartment) => {
    try {
      const res = await DepartmentApi.newdepartment(newDepartment)
      if (res.success === true) {
        departmentList.value.push(res.data)
      } else {
        console.warn('新增部门失败，返回异常：', res)
      }
    } catch (err) {
      console.error('新增部门时出错：', err)
    }
  }

  const deleteDepartment = async (id) => {
    try {
      await DepartmentApi.delete(id)
      departmentList.value = departmentList.value.filter(dep => dep.id !== id)
      // 如果删除的是当前选中的部门，清空
      if (currentDepartment.value?.id === id) {
        currentDepartment.value = null
      }
    } catch (err) {
      console.error(`删除部门 ${id} 时出错：`, err)
    }
  }

  // 新增：设置当前选中部门
  const chooseDepartment = (department) => {
    currentDepartment.value = department
    console.log("department is ",currentDepartment.value.name)
  }

  function getDefaultDepartments() {
    return [
      { id: 0, name: '计算机科学与技术学院' },
      { id: 1, name: '控制学院' },
      { id: 2, name: '经济学院' },
      { id: 3, name: '法学院' }
    ]
  }

  return {
    departmentList,
    currentDepartment,        // ← 导出
    fetchAllDepartments,
    createDepartment,
    deleteDepartment,
    chooseDepartment          // ← 导出
  }
})
