import { Route,Routes } from "react-router-dom";
import Login from "../pages/Login";
import Signup from "../pages/Signup";

export default function Router(){
    return(
        <Routes>
            <Route path = "/login" element = {<Login />}></Route>
            <Route path = "/Signup" element = {<Signup />}></Route>
            <Route path = "/" element = {<Home />}></Route>
        </Routes>
    )
}