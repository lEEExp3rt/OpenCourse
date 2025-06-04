import request from '@/utils/request'

const CourseApi = {
  get_all_courses(){
    return request.get('/course/all')
  },
  get_all_course_in_department(department_id){
    return request.get('/course/department/',department_id)
  },
  query(id) {
    return request.get(`/course/`, id);
  },
  delete(id){
    return request.delete('/course/',id)
  },
  newcourse(course){
    return request.post('/course',course)
  }
  
};

export default CourseApi;
