import request from '@/utils/request'

const CommonApi = {
  upload(data) {
    return request.post(`/upload`, data);
  },
  captcha(data) {
    return request.post(`/captcha`, data);
  }
};

export default CommonApi;
