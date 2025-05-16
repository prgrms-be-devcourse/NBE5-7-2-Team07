import { privateApi, publicApi } from "./ApiService";
import axios from 'axios';

// 사용자 정보 저장
let currentUser = null;

// 로컬 스토리지에서 사용자 정보 불러오기
try {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
        currentUser = JSON.parse(storedUser);
        const token = localStorage.getItem('accessToken');
        if (token) {
            axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        }
    }
} catch (error) {
    console.error("로컬 스토리지 데이터 로드 오류:", error);
}

export const join = async (req) =>{
    try{
        const resp = await publicApi.post("/api/users/register", req);
        return resp.data;
    }catch(err){
        console.error("회원가입 오류:", err);
        throw err;
    }
}

export const login = async(req) => {
    try {
        console.log("로그인 요청 데이터:", req);
        const resp = await publicApi.post("/api/users/login", req);
        console.log("로그인 응답 데이터:", resp.data);
        
        // 토큰 설정
        const { accessToken } = resp.data;
        if (accessToken) {
            axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
            localStorage.setItem('accessToken', accessToken);
        }
        
        // 사용자 정보 저장
        // 백엔드 응답 형식에 따라 수정 필요
        const email = resp.data.email || req.email;
        const nickname = resp.data.nickname || req.email.split('@')[0];
        
        currentUser = { email, nickname };
        console.log("저장된 사용자 정보:", currentUser);
        
        // 로컬 스토리지에 사용자 정보 저장
        localStorage.setItem('currentUser', JSON.stringify(currentUser));
        
        return resp.data;
    } catch(err) {
        console.error("로그인 API 오류:", err);
        throw err;
    }
}


export const getCurrentUser = () => {
    // 현재 메모리에 사용자 정보가 없는 경우 로컬 스토리지에서 확인
    if (!currentUser) {
        try {
            const storedUser = localStorage.getItem('currentUser');
            if (storedUser) {
                currentUser = JSON.parse(storedUser);
                const token = localStorage.getItem('accessToken');
                if (token) {
                    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
                }
            }
        } catch (error) {
            console.error("로컬 스토리지 데이터 로드 오류:", error);
        }
    }
    
    console.log("현재 사용자 정보:", currentUser);
    return currentUser;
}


export const checkEmailDuplicate = async(email) => {
    try{
        const resp = await publicApi.post("api/users/checkEmail",{params : {email : email}});
        if(resp.status === 200){
            alert("사용 가능한 이메일입니다.");
        }
    }catch(err){
        console.log(err);
    }
}

export const logout = async() => {
    try{
        const resp = await privateApi.post("api/users/logout");
        currentUser = null;
        
        // 로컬 스토리지에서 사용자 정보 및 토큰 제거
        localStorage.removeItem('currentUser');
        localStorage.removeItem('accessToken');
    }catch(err){
        console.log(err);
    }
}
