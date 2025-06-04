import request from '@/utils/request'

const ResourceApi = {
  get_resource(id) {
    return request.get(`/resource/course/` + id);
  },
  delete_resource(id){
    return request.delete('/resource/' + id);
  },
  add_resource(resource)
  {
    return request.post('/resource',resource);
  }

};

export default ResourceApi;
