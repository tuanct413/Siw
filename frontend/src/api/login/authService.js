// src/api/login/authService.js
import axios from "axios";

const API_URL = "https://siw-backend.fly.dev/auth"; // chỉnh lại nếu khác

// Login
export const login = async (email, password) => {
  try {
    const response = await axios.post(`${API_URL}/login`, { email, password });
    return response.data; // { message, token }
  }
  catch (error) {
    if (error.response && error.response.data) {
      throw new Error(error.response.data.message);
    }
    throw new Error("Network error");
  }
};
