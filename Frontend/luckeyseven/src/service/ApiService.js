import axios from 'axios';
import { API_BASE_URL } from '../backend/backendApi';
// 모든 axios 요청에 withCredentials 설정
axios.defaults.withCredentials = true;
// 토큰이 필요없는 요청
export const publicApi = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true  // 쿠키 포함
});

// 토큰 필요한 요청
export const privateApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,  // 쿠키 포함
});

// publicApi 요청 디버깅
publicApi.interceptors.request.use((config) => {
  console.log("publicApi 요청:", config.url);
  console.log("publicApi 요청 헤더:", config.headers);
  console.log("publicApi 요청 withCredentials:", config.withCredentials);
  return config;
});

// privateApi 인스턴스 디버깅을 위한 로그
console.log("privateApi 생성 시점의 기본 헤더:", privateApi.defaults.headers);

// 모든 privateApi 요청 전에 현재 헤더 상태 확인
privateApi.interceptors.request.use((config) => {
  console.log("=== privateApi 요청 시작 ===");
  console.log("요청 URL:", config.url);
  console.log("요청 메서드:", config.method);
  console.log("요청 헤더:", JSON.stringify(config.headers));
  console.log("axios 기본 헤더:", JSON.stringify(axios.defaults.headers.common));
  console.log("쿠키 전송 여부(withCredentials):", config.withCredentials);
  return config;
}, (error) => {
  console.error("privateApi 요청 인터셉터 오류:", error);
  return Promise.reject(error);
});

// privateApi 요청 헤더에 항상 accessToken을 삽입
privateApi.interceptors.request.use((config) => {
  console.log("인터셉터 실행 - 요청 URL:", config.url);
  console.log("인터셉터 실행 전 헤더:", JSON.stringify(config.headers));

  const token = axios.defaults.headers.common['Authorization']?.split(' ')[1];

  if (token) {
    console.log("토큰 발견:", token.substring(0, 10) + "...");
    config.headers.Authorization = 'Bearer ' + token;
    console.log("헤더에 토큰 설정 완료");
  } else {
    console.log("토큰이 없습니다. Authorization 헤더가 설정되지 않았습니다.");
  }

  console.log("인터셉터 실행 후 헤더:", JSON.stringify(config.headers));
  return config;
});

// 모든 privateApi 응답에 대한 로그
privateApi.interceptors.response.use((response) => {
  console.log("=== privateApi 응답 수신 ===");
  console.log("응답 URL:", response.config.url);
  console.log("응답 상태:", response.status);
  console.log("응답 헤더:", JSON.stringify(response.headers));

  // 응답에서 Set-Cookie 헤더 확인
  if(response.headers['set-cookie']) {
    console.log("Set-Cookie 헤더 발견:", response.headers['set-cookie']);
  }
  else{
    console.log("Set-Cookie 헤더 발견 안됨");
  }

  return response;
}, (error) => {
  console.error("privateApi 응답 오류:", error.response?.status, error.message);
  return Promise.reject(error);
});

// privateApi 응답 시 에러가 발생하면 refreshToken 재발급
privateApi.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        console.log("401 오류로 인한 토큰 재발급 시도");
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
    console.log("refreshToken 재발급 요청 시작");
    const token = axios.defaults.headers.common['Authorization']?.split(' ')[1];
    try {
        const response = await privateApi.post(
            '/api/refresh',
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

// Team API calls
export const getTeamDashboard = async (teamId) => {
  try {
    const response = await privateApi.get(`/api/teams/${teamId}/dashboard`);
    return response.data;
  } catch (error) {
    console.error('Error fetching team dashboard:', error);
    throw error;
  }
};

export const getTeamMembers = async (teamId) => {
  try {
    const response = await privateApi.get(`/api/teams/${teamId}/members`);
    return response.data;
  } catch (error) {
    console.error('Error fetching team members:', error);
    throw error;
  }
};

export const createTeam = async (name, teamPassword) => {
  try {
    const response = await privateApi.post('/api/teams', {
      name: name,
      teamPassword: teamPassword,
    });
    return response.data;
  } catch (error) {
    console.error('Error creating team:', error);
    throw error;
  }
};

export const joinTeam = async (teamCode, teamPassword) => {
  try {
    const response = await privateApi.post('/api/teams/members', {
      teamCode: teamCode,
      teamPassword: teamPassword,
    });
    return response.data;
  } catch (error) {
    console.error('Error joining team:', error);
    throw error;
  }
}

export async function getMyTeams() {
  // 예시 리턴: [{ id: 1, name: 'Alpha' }, ...]
  try {
    const response = await privateApi.get('/api/teams/myTeams');
    return response.data;
  }
  catch (error) {
    console.error('Error fetching my teams:', error);
    throw error;
  }
}

export const deleteTeam = async (teamId) => {
  try {
    const response = await privateApi.delete(`/api/teams/${teamId}`);
    return response.data;
  } catch (error) {
    console.error('Error deleting team:', error);
    throw error;
  }
};