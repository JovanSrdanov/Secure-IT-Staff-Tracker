import axios from 'axios';

let failedRequests = 0; // Counter for failed requests with 401 status

const interceptor = axios.create({
    baseURL: 'https://localhost:4430/',
    withCredentials: true
});

interceptor.interceptors.request.use(
    async (config) => {
        const accessToken = getAccessToken();
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        }

        return config;
    },
    (error) => Promise.reject(error)
);

interceptor.interceptors.response.use(
    (response) => {
        return response;
    },
    async (error) => {
        failedRequests++;

        const originalRequest = error.config;
        if (error.response && error.response.status === 410 && !originalRequest._retry) {
            originalRequest._retry = true;
            try {
                const response = await axios.post('https://localhost:4430/auth/refresh', {
                    token: getRefreshToken(),
                });

                if (response.status === 200) {
                    removeTokens();
                    setTokens(response);
                    originalRequest.headers.Authorization = `Bearer ${getAccessToken()}`;
                    failedRequests = 0; // Set failedRequests to 0 when there is no error
                    return interceptor(originalRequest);
                }
            } catch (refreshError) {

                console.log("Token expired: ");
                removeTokens();
            }
        }
        if (error.response && error.response.status === 401) {
            alert(" You are unauthorized to access data from this url: " + interceptor.getUri(originalRequest))
            failedRequests = 0
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

function setTokens(res) {
    document.cookie = `accessToken=${encodeURIComponent(res.data.accessToken)}; Secure; SameSite=Strict;`;
    document.cookie = `refreshToken=${encodeURIComponent(res.data.refreshToken)}; Secure; SameSite=Strict;`;
}

function removeTokens() {
    document.cookie = 'accessToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; Secure; SameSite=Strict;';
    document.cookie = 'refreshToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; Secure; SameSite=Strict;';
}

export default interceptor;
