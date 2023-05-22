import React, {useEffect} from 'react';
import {useNavigate, useParams} from "react-router-dom";

// import interceptor from "../../interceptor/interceptor";

function PasswordlessLogin(props) {
    const params = useParams();
    const navigate = useNavigate();


    useEffect(() => {
        console.log(params.hash);
        // interceptor.post("").then((res) => {
        //     console.log(res.data)
        //     navigate("/profile")
        // }).catch((err) => {
        //     console.log(err)
        // alert("Can not log in with this hash code.")
        //     navigate("/login")
        // })
    }, []);


    return (
        <div></div>
    );
}

export default PasswordlessLogin;