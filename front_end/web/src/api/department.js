import request from '@/utils/request'

const DepartmentApi = {
  query(id) {
    return request.get(`/course/`, id);
  },
  get_resource(id) {
    return request.post(`/resource/course/`, id);
  }
};

export default CourseApi;
