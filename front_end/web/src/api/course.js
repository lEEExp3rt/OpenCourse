import request from '@/utils/request'

const CourseApi = {
  get_all_courses(){
    return request.get(`/course/all`)
  },
  query(id) {
    return request.get(`/course/`, id);
  },
  delete(id){
    return request.delete(`/course/`, id)
  }
  
};

export default CourseApi;
