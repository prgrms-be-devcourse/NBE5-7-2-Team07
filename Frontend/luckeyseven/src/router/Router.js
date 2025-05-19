import {Route, Routes} from "react-router-dom";
import Login from "../pages/Login/Login";
import Signup from "../pages/Login/Signup";
import TeamSetup from "../pages/TeamSetup";
import ExpensesPage from "../pages/ExpensesPage";
import SettlementPage from "../pages/SettlementPage";

export default function Router() {
  return (
      <Routes>
        <Route path="/login" element={<Login/>}></Route>
        <Route path="/Signup" element={<Signup/>}></Route>
        <Route path="/" element={<HomePage/>}/>
        <Route path = "/team-setup" element = {<TeamSetup />}></Route>
        <Route path="/TeamDashBoard" element={<TeamDashBoard />} />
        <Route path="/teams/:teamId/settlements"
               element={<TeamSettlementsPage/>}/>
        <Route path="/teams/:teamId/settlements/new"
               element={<SettlementNewPage/>}/>
        <Route path="/settlements/:settlementId"
               element={<SettlementDetailPage/>}/>
        <Route path="/teams/:teamId/settlements/:settlementId/edit"
               element={<SettlementEditPage/>}/>
        <Route path="/team/:teamId/expenses" element={<ExpensesPage />} />
        <Route path="/team/:teamId/settlement" element={<SettlementPage />} />
        <Route path="*" element={<Navigate to="/" replace/>}/>
      </Routes>
  )
}
