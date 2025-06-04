import request from '@/utils/request'

const ResourceApi = {
  get_resource(id) {
    return request.post(`/resource/course/`, id);
  },
  delete(id){
    return request.delete('/resource/',id)
  },
  add()

};

export default ResourceApi;
