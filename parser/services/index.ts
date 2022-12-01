import axios from "axios";


export const axiosInstance = axios.create({
    baseURL: 'http://localhost:5000/api'
    // baseURL: 'https://vs-shop.herokuapp.com/api'
});

export const headers = () => {
    return {
        Authorization: `Bearer ${process.env.TOKEN}`
    };
};

export const headersJSON = () => {
    return {
        Authorization: `Bearer ${process.env.TOKEN}`,
        'Content-Type': 'application/json'
    };
};