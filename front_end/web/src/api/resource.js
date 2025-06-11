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
  }
};

export default ResourceApi;
