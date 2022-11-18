import axios from "axios";

const TOKEN = process.env.TOKEN;

export const axiosNVInstance = axios.create({
    baseURL: 'https://api.novaposhta.ua/v2.0/json/'
})

export const axiosInstance = axios.create({
    baseURL: 'http://localhost:5000/api'
    // baseURL: 'https://vs-shop.herokuapp.com/api'
});

export const headers = () => {
    return {
        Authorization: `Bearer ${TOKEN}`
    };
};

export const headersJSON = () => {
    return {
        Authorization: `Bearer ${TOKEN}`,
        'Content-Type': 'application/json'
    };
};