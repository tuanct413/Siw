// src/services/weatherService.js
import axios from "axios";

const API_BASE_URL = "http://localhost:8080"; // chỉnh lại nếu khác


export const fetchWeather = async (cityName) => {
  const response = await axios.get(`${API_BASE_URL}/weather/find?local=${cityName}`);
  return response.data;
};
export const fetchWeather24hours = async (cityName) => {
  const response = await axios.get(
    `${API_BASE_URL}/weather/v1/weatheroneday?city=${encodeURIComponent(cityName)}`
  );
  return response.data;
};


export const fetchWeather7days = async (cityName) => {
  const response = await axios.get(`${API_BASE_URL}/weather/findby7day/${encodeURIComponent(cityName)}`);
  return response.data;
};
export const fetchFavorite = async (cityName, weather, token) => {
  if (!token) throw new Error("No token provided");

  try {
    const response = await axios.get(
      `${API_BASE_URL}/weather/v1/getuserFavorite?city=${encodeURIComponent(cityName)}&condition=${encodeURIComponent(weather)}`,
      {
        headers: {
          Authorization: `Bearer ${token}`, // gửi token JWT
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
      headers: { Authorization: `Bearer ${token}` } 
    });
    return response.data;
  } catch (error) {
    if (error.response && error.response.data) {
      throw new Error(error.response.data.message);
    }
    throw new Error("Network error");
  }
};





