// src/api/register/authService.js
import axios from "axios";

const API_URL = "https://siw-backend.onrender.com/users"; // base URL cho auth

// Gọi API đăng ký
export const register = async (name, email, password) => {
  try {
    const response = await axios.post(`${API_URL}/create`, {
      name,
      email,
      password,
    });
    return response.data; // backend trả về { data, message, ... }
  } catch (error) {
    if (error.response && error.response.data) {
      throw new Error(error.response.data.message);
    }
    throw new Error("Đăng ký thất bại!");
  }
};

// Gọi API xác thực email
export const verifyEmail = async (email, code) => {
  try {
    const params = new URLSearchParams();
    params.append("email", email);
    params.append("code", code);

    const response = await axios.post(`${API_URL}/verify`, params, {
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
    });
    return response.data;
  } catch (error) {
    if (error.response && error.response.data) {
      throw new Error(error.response.data.message);
    }
    throw new Error("Xác thực thất bại!");
  }
};


