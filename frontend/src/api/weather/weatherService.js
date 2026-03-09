// src/services/weatherService.js
import axios from "axios";

const API_BASE_URL = process.env.REACT_APP_API_URL;

// chuẩn hóa city
const normalizeCity = (city) => {
  if (!city) return "";

  const cleaned = city.trim().replace(/\s+/g, " ");

  return removeVietnameseTones(cleaned);
};
// Hàm loại bỏ dấu tiếng Việt
const removeVietnameseTones = (str) => {
  return str
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/đ/g, "d")
    .replace(/Đ/g, "D");
};

export const fetchWeather = async (cityName) => {
  const city = normalizeCity(cityName);

  const response = await axios.get(
    `${API_BASE_URL}/weather/find?local=${encodeURIComponent(city)}`
  );

  return response.data;
};

export const fetchWeather24hours = async (cityName) => {
  const city = normalizeCity(cityName);

  const response = await axios.get(
    `${API_BASE_URL}/weather/v1/weatheroneday?city=${encodeURIComponent(city)}`
  );

  return response.data;
};

export const fetchWeather7days = async (cityName) => {
  const city = normalizeCity(cityName);

  const response = await axios.get(
    `${API_BASE_URL}/weather/findby7day/${encodeURIComponent(city)}`
  );

  return response.data;
};

export const fetchFavorite = async (cityName, weather, token) => {
  if (!token) throw new Error("No token provided");

  const city = normalizeCity(cityName);

  try {
    const response = await axios.get(
      `${API_BASE_URL}/weather/v1/getuserFavorite?city=${encodeURIComponent(
        city
      )}&condition=${encodeURIComponent(weather)}`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    return response.data;
  } catch (err) {
    console.error("Error fetching favorite:", err);
    throw err;
  }
};

export const getProfile = async () => {
  const token = localStorage.getItem("token");

  try {
    const response = await axios.get(`${API_BASE_URL}/users/profile`, {
      headers: { Authorization: `Bearer ${token}` },
    });

    return response.data;
  } catch (error) {
    if (error.response && error.response.data) {
      throw new Error(error.response.data.message);
    }

    throw new Error("Network error");
  }
};