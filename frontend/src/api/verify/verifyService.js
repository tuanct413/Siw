import axios from "axios";

const API_URL = process.env.REACT_APP_API_URL + "/users"; // base URL cho auth
const API_URL1 = process.env.REACT_APP_API_URL + "/auth";

export const verify = async (email) => {
  try {
    const response = await axios.get(`${API_URL}/v1/verify?email=${email}`);
    return response.data;
  } catch (error) {
    console.error("❌ Lỗi xác thực:", error);
    throw error;
  }
};

// ✅ Đúng định dạng backend yêu cầu
export const resetPassword = async (email, password, verificationCode) => {
  try {
    const response = await axios.post(`${API_URL1}/forgot-password`, {
      email,
      password,
      verificationCode,
    });
    return response.data;
  } catch (error) {
    console.error("❌ Lỗi đặt lại mật khẩu:", error);
    throw error;
  }
};
