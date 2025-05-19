import { Route,Routes } from "react-router-dom";
import Login from "../pages/Login";
import Signup from "../pages/Signup";
import TeamSetup from "../pages/TeamSetup";
import Home from "../pages/Home";
import ExpensesPage from "../pages/ExpensesPage";
import SettlementPage from "../pages/SettlementPage";

// 추후 삽입
export default function AppRouter(){
    return(
        <Routes>
            <Route path = "/login" element = {<Login />}></Route>
            <Route path = "/Signup" element = {<Signup />}></Route>
            <Route path = "/team-setup" element = {<TeamSetup />}></Route>
            <Route path="/TeamDashBoard" element={<TeamDashBoard />} />
            <Route path = "/" element = {<Home />}></Route>
            <Route path="/team/:teamId/expenses" element={<ExpensesPage />} />
            <Route path="/team/:teamId/settlement" element={<SettlementPage />} />
        </Routes>
    )
}
