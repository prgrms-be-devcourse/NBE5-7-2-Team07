import React from "react"
import {BrowserRouter as Router, Navigate, Route, Routes} from "react-router-dom"
import Home from "./pages/Home"
import ExpensesPage from "./pages/ExpensesPage"
import SettlementPage from "./pages/SettlementPage"
import "./styles/auth.css"
import {getCurrentUser} from "./service/AuthService"
import TeamDashBoard from "./pages/TeamDashBoard";
import TeamSetup from "./pages/TeamSetup"

// 보호된 라우트 컴포넌트
const ProtectedRoute = ({ children }) => {
  const user = getCurrentUser();
  
  if (!user) {
    // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
    return <Navigate to="/login" replace />;
  }
  
  return children;
};

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/team/:teamId/expenses"    element={<ExpensesPage />} />
        {/*<Route path="/login" element={<Login />} />*/}
        {/*<Route path="/signup" element={<Signup />} />*/}
        <Route path="/TeamDashBoard" element={<TeamDashBoard />} />
        <Route 
          path="/" 
          element={
            // <ProtectedRoute>
              <Home />
            // </ProtectedRoute>
          } 
        />
        <Route path="/team-setup" element ={<TeamSetup/>}/> 
        <Route path="/team/:teamId/settlement"  element={<SettlementPage />} />
      </Routes>
    </Router>
  )
}

export default App 