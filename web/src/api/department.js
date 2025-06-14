import request from '@/utils/request'

const DepartmentApi = {
  get_all_departments() {
    return request.get(`/department`);
  },
  get_resource(id) {
    return request.post(`/department/course/`, id);
  },
  delete(id){
    return request.deletenewdepartment(`/department/`, id);
  },
  newdepartment(department) {
    return request.post('/department', department);
  },
};

export default DepartmentApi;
