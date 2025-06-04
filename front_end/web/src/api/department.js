import request from '@/utils/request'

const DepartmentApi = {
  get_all_departments() {
    return request.get(`/department`);
  },
  get_resource(id) {
    return request.post(`/department/course/`, id);
  },
  delete(id){
    return request.delete(`/department/`, id);
  }
};

export default DepartmentApi;
