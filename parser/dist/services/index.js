"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.headersJSON = exports.headers = exports.axiosInstance = exports.axiosNVInstance = void 0;
const axios_1 = __importDefault(require("axios"));
const TOKEN = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwidXNlcm5hbWUiOiLQmNC70YzRjyDQodC-0LvQvtCz0YPQsdC-0LIiLCJyb2xlcyI6WyJhZG1pbiJdLCJpYXQiOjE2Njg3Nzg4Mjh9._rOv86G3oP2HJklJOVPkE1SGmmiqpveIxNPszYafnPs';
exports.axiosNVInstance = axios_1.default.create({
    baseURL: 'https://api.novaposhta.ua/v2.0/json/'
});
exports.axiosInstance = axios_1.default.create({
    baseURL: 'http://localhost:5000/api'
    // baseURL: 'https://vs-shop.herokuapp.com/api'
});
const headers = () => {
    return {
        Authorization: `Bearer ${TOKEN}`
    };
};
exports.headers = headers;
const headersJSON = () => {
    return {
        Authorization: `Bearer ${TOKEN}`,
        'Content-Type': 'application/json'
    };
};
exports.headersJSON = headersJSON;
