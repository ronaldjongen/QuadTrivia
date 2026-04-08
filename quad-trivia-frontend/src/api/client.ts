import axios from 'axios';
// https://axios-http.com/docs/instance
export const api = axios.create({
  baseURL: '/api',
  withCredentials: true});
