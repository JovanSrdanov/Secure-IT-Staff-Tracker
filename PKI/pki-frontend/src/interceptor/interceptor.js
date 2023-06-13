import axios from 'axios';

const API_URL = 'http://localhost:8180/';

const interceptor = axios.create({
    baseURL: API_URL,
});

interceptor.interceptors.request.use(
    config => {
        const jwt = localStorage.getItem('jwt');
        if (jwt) {
            config.headers['Authorization'] = `Bearer ${jwt}`;
        }
        return config;
    },
    error => {
        Promise.reject(error)
    }
);

export default interceptor;