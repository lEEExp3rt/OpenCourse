import request from '@/utils/request'

const CommonApi = {
  upload(data) {
    return request.post(`/upload`, data);
  },
  captcha(data) {
    return request.post(`/user/register/send-code`, data);
  }
};

export default CommonApi;
