import request from '@/utils/request'

const ResourceApi = {
  get_resource(id) {
    return request.get(`/resource/course/` + id);
  },
  delete_resource(id){
    return request.delete('/resource/' + id);
  },
  add_resource(formData) {
    return request.post('/resource', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },
  get_resource_view(id) {
    return request.get('/resource/' + id + '/view', {
      responseType: 'blob'
    });
  },
  like_resource(id) {
    return request.put('/resource/like' + id );
  }
};

export default ResourceApi;
