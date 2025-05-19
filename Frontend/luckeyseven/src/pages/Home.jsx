import React, { useState, useEffect } from "react";
import {  Link, useNavigate } from "react-router-dom";
import "../styles/auth.css";
import { logout, getCurrentUser } from "../service/AuthService";

export default function Home() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    // 로그인 상태 확인
    const checkLoginStatus = () => {
      try {
        const currentUser = getCurrentUser();
        if (currentUser) {
          setUser(currentUser);
        } else {
          setError("로그인이 필요합니다.");
          // navigate("/TeamDashBoard");
          navigate("/login")
        }
      } catch (err) {
        setError("로그인이 필요합니다.");
        navigate("/login");
      } finally {
        setLoading(false);
      }
    };

    checkLoginStatus();
  }, [navigate]);

  const handleLogout = async () => {
    try {
      await logout();
      navigate("/login");
    } catch (err) {
      setError("로그아웃 중 오류가 발생했습니다.");
    }
  };

  if (loading) {
    return <div className="loading">로딩 중...</div>;
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <h1 className="auth-title">홈</h1>
        </div>
        <div className="user-info">
          {error && <div className="error-message">{error}</div>}
          {user && (
            <>
              <div className="info-item">
                <strong>이메일:</strong> {user.email}
              </div>
              <div className="info-item">
                <strong>닉네임:</strong> {user.nickname}
              </div>
            </>
          )}
        </div>
        <div className="buttons">
          <button onClick={handleLogout} className="btn btn-primary">
            로그아웃
          </button>
        </div>
      </div>
    </div>
  );
} 