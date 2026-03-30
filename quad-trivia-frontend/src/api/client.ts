import axios from 'axios';
// TODO check
export const api = axios.create({
  baseURL: '/api',
  withCredentials: true});
