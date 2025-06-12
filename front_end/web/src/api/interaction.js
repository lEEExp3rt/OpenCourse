import request from '@/utils/request'

const InteractionApi = {
  get_all_interactions(course_id){
    return request.get('/interaction/course/' + course_id);
  },
  
};

export default InteractionApi;
