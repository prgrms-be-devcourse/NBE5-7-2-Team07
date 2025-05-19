import {Route, Routes} from "react-router-dom";
import Login from "../pages/Login/Login";
import Signup from "../pages/Login/Signup";

export default function Router() {
  return (
      <Routes>
        <Route path="/login" element={<Login/>}></Route>
        <Route path="/Signup" element={<Signup/>}></Route>
        <Route path="/" element={<HomePage/>}/>
        <Route path="/teams/:teamId/settlements"
               element={<TeamSettlementsPage/>}/>
        <Route path="/teams/:teamId/settlements/new"
               element={<SettlementNewPage/>}/>
        <Route path="/teams/:teamId/settlements/:settlementId"
               element={<SettlementDetailPage/>}/>
        <Route path="/teams/:teamId/settlements/:settlementId/edit"
               element={<SettlementEditPage/>}/>
        <Route path="*" element={<Navigate to="/" replace/>}/>
      </Routes>
  )
}