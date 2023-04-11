import axios from 'axios';

const API_URL = 'http://localhost:8080/';

const interceptor = axios.create({
    baseURL: API_URL,
});

interceptor.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        Promise.reject(error)
    }
);

export default interceptor;