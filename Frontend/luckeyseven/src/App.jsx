import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Home from "./pages/Home";
import "./styles/auth.css";
import { getCurrentUser } from "./service/AuthService";
import ExpenseList from "./expense/pages/ExpenseList";


const ProtectedRoute = ({ children }) => {
  const user = getCurrentUser();
  if (!user) return <Navigate to="/login" replace />;
  return children;
};

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login"   element={<Login />} />
        <Route path="/signup"  element={<Signup />} />
        <Route 
          path="/" 
          element={
            <ProtectedRoute>
              <Home />
            </ProtectedRoute>
          } 
        />
        {/* teamId를 URL 파라미터로 받아서 ExpenseList에 전달 */}
        <Route 
          path="/:teamId/expenses" 
          element={
              <ExpenseList />
          } 
        />
      </Routes>
    </Router>
  );
}

export default App;
