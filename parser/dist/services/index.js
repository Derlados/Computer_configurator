"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.headersJSON = exports.headers = exports.axiosInstance = void 0;
const axios_1 = __importDefault(require("axios"));
exports.axiosInstance = axios_1.default.create({
    baseURL: 'http://localhost:5000/api'
    // baseURL: 'https://vs-shop.herokuapp.com/api'
});
const headers = () => {
    return {
        Authorization: `Bearer ${process.env.TOKEN}`
    };
};
exports.headers = headers;
const headersJSON = () => {
    return {
        Authorization: `Bearer ${process.env.TOKEN}`,
        'Content-Type': 'application/json'
    };
};
exports.headersJSON = headersJSON;
