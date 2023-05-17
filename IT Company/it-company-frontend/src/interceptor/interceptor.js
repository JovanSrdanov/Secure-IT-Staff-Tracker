import axios from 'axios';

const interceptor = axios.create({
    baseURL: 'http://localhost:4761/',
    withCredentials: true
});

interceptor.interceptors.request.use(
    async (config) => {
        const accessToken = getAccessToken();
        const refreshToken = getRefreshToken();
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        }
        if (refreshToken) {
            config.data = {
                ...config.data,
                refresh_token: refreshToken,
            };
        }
        return config;
    },
    (error) => Promise.reject(error)
);

interceptor.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            try {
                const response = await axios.post('http://localhost:4761/auth/refresh', {
                    token: getRefreshToken(),
                });

                if (response.status === 200) {
                    const {access_token: newAccessToken, refresh_token: newRefreshToken} = response.data;
                    setAccessToken(newAccessToken);
                    originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
                    return axios(originalRequest);
                }
            } catch (refreshError) {
                removeTokens();
            }
        }
        return Promise.reject(error);
    }
);

function getAccessToken() {
    const cookies = document.cookie.split('; ');
    const accessTokenCookie = cookies.find((cookie) =>
        cookie.startsWith('accessToken=')
    );
    if (!accessTokenCookie) {
        return null;
    }
    const accessToken = accessTokenCookie.split('=')[1];
    return decodeURIComponent(accessToken);
}

function getRefreshToken() {
    const cookies = document.cookie.split('; ');
    const refreshTokenCookie = cookies.find((cookie) =>
        cookie.startsWith('refreshToken=')
    );
    if (!refreshTokenCookie) {
        return null;
    }
    const refreshToken = refreshTokenCookie.split('=')[1];
    return decodeURIComponent(refreshToken);
}

function setAccessToken(accessToken) {
    document.cookie = `accessToken=${encodeURIComponent(accessToken)}; Secure; SameSite=Strict;`;
}

function removeTokens() {
    // Remove access and refresh tokens from secure storage
    document.cookie = 'accessToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; Secure; SameSite=Strict;';
    document.cookie = 'refreshToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; Secure; SameSite=Strict;';
}


export default interceptor;
