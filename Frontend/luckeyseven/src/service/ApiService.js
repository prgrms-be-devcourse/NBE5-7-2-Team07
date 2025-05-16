import axios from 'axios';
import { API_BASE_URL } from '../backend/backendApi';

axios.defaults.withCredentials = true;

// 토큰이 필요없는 요청
export const publicApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  }
});

// 토큰 필요한 요청
export const privateApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials : true,
});

// privateApi 요청 헤더에 항상 accessToken을 삽입
privateApi.interceptors.request.use((config) => {
  const token = axios.defaults.headers.common['Authorization']?.split(' ')[1];
  if (token) {
    config.headers.Authorization = 'Bearer ' + token;
  }
  return config;
});

// privateApi 응답 시 에러가 발생하면 refreshToken 재발급
privateApi.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const response = await postRefreshToken();
        console.log('Token refresh response:', response.data);
        const newAccessToken = response.data.accessToken;
        axios.defaults.headers.common['Authorization'] = `Bearer ${newAccessToken}`;
        originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
        return axios(originalRequest);
      } catch (refreshError) {
        console.error('Token refresh failed:', refreshError);
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  },
);

// refreshToken 재발급 요청
export async function postRefreshToken() {
  const token = axios.defaults.headers.common['Authorization']?.split(' ')[1];
  try {
    const response = await publicApi.post(
      '/refresh',
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        withCredentials: true // 토큰 갱신 시에만 withCredentials 사용
      },
    );
    console.log('postRefreshToken response:', response);
    return response;
  } catch (error) {
    console.error('postRefreshToken error:', error);
    window.location.href = "/login";
    throw error;
  }
}