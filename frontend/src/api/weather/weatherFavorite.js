import axios from "axios";
const API_BASE_URL = "http://localhost:8080"; // chỉnh lại nếu khác

export const compareCity = async (cityName1, cityName2) => {
  const response = await axios.get(
    `${API_BASE_URL}/weather/v1/comparecity?city=${encodeURIComponent(
      cityName1
    )}&citynext=${encodeURIComponent(cityName2)}`
  );
  return response.data;
};


