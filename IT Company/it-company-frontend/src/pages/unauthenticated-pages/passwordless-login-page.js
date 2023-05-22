import React, {useEffect} from 'react';
import {useLocation, useNavigate} from "react-router-dom";
import interceptor from "../../interceptor/interceptor";

function PasswordlessLoginPage(props) {
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const hash = searchParams.get('hash');
    const navigate = useNavigate();

    console.log(hash)

    useEffect(() => {

        interceptor.post("auth/passwordless-login/" + hash).then((res) => {
            console.log(res.data)
            document.cookie = `accessToken=${encodeURIComponent(res.data.accessToken)}; Secure; SameSite=Strict; Path=/;`;
            document.cookie = `refreshToken=${encodeURIComponent(res.data.refreshToken)}; Secure; SameSite=Strict; Path=/;`;
            navigate("/profile")
        }).catch((err) => {
            console.log(err)
            alert("Can not log in with this hash code.")
            navigate("/login")
        })
    }, []);


    return (
        <div>
            <h1>Checking the hash</h1>
        </div>
    );
}

export default PasswordlessLoginPage;