import request from '@/utils/request'

const InteractionApi = {
  get_all_interactions(course_id){
    return request.get('/interaction/course/' + course_id);
  },
  like_comment(comment_id) {
    return request.post('/interaction/' + comment_id + '/like');
  },
  unlike_comment(comment_id) {
    return request.post('/interaction/' + comment_id + '/unlike');
  },
  delete_comment(comment_id) {
    return request.delete('/interaction/' + comment_id);
  },
  post_comment(content) {
    return request.post('/interaction', content);
  }
};

export default InteractionApi;
