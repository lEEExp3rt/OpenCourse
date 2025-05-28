import request from '@/utils/request'

const userApi = {
  info(id) {
    return request.get(`/user/${id}`);
  },
  register(data) {
    return request.post('/user/register', data);
  },
  login(data) {
    return request.post('/user/login', data);
  },
  logout() {
    return request.post('/user/logout');
  }
};

export default userApi;
