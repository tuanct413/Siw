import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/Register.css";
import { register, verifyEmail } from "../api/register/authServiceRegister";

const Register = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [verifyCode, setVerifyCode] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isRegistered, setIsRegistered] = useState(false);
  const [message, setMessage] = useState("");

  const handleInputChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setMessage("");

    if (formData.password !== formData.confirmPassword) {
      setMessage("Mật khẩu không khớp!");
      setIsLoading(false);
      return;
    }

    try {
      const data = await register(formData.name, formData.email, formData.password);
      setMessage(data.message || "Đăng ký thành công! Vui lòng nhập mã xác thực.");
      setIsRegistered(true);
    } catch (error) {
      setMessage(error.message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleVerify = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setMessage("");

    try {
      const data = await verifyEmail(formData.email, verifyCode);

      if (data.data?.verify) {
        setMessage("Xác thực thành công!");
        navigate("/");
      } else {
        setMessage(data.message || "Xác thực thất bại!");
      }
    } catch (error) {
      setMessage(error.message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="register-page">
      <div className="register-container">
        <div className="register-logo">
          <span>SIW</span>
        </div>

        {message && <p className="message">{message}</p>}

        {!isRegistered ? (
          <>
            <h2>Đăng Ký Tài Khoản</h2>
            <form onSubmit={handleRegister}>
              <input
                type="text"
                name="name"
                placeholder="Tên người dùng"
                value={formData.name}
                onChange={handleInputChange}
                required
              />
              <input
                type="email"
                name="email"
                placeholder="Email"
                value={formData.email}
                onChange={handleInputChange}
                required
              />
              <input
                type="password"
                name="password"
                placeholder="Mật khẩu"
                value={formData.password}
                onChange={handleInputChange}
                required
              />
              <input
                type="password"
                name="confirmPassword"
                placeholder="Nhập lại mật khẩu"
                value={formData.confirmPassword}
                onChange={handleInputChange}
                required
              />
              <button
                type="submit"
                className={`register-btn ${isLoading ? "loading" : ""}`}
                disabled={isLoading}
              >
                {isLoading ? "Đang đăng ký..." : "Đăng Ký"}
              </button>
            </form>
          </>
        ) : (
          <>
            <h2>Xác Thực Email</h2>
            <p>
              Email của bạn: <strong>{formData.email}</strong>
            </p>
            <form onSubmit={handleVerify}>
              <input
                type="text"
                placeholder="Nhập mã xác thực"
                value={verifyCode}
                onChange={(e) => setVerifyCode(e.target.value)}
                required
              />
              <button
                type="submit"
                className={`register-btn ${isLoading ? "loading" : ""}`}
                disabled={isLoading}
              >
                {isLoading ? "Đang xác thực..." : "Xác Thực"}
              </button>
            </form>
          </>
        )}
      </div>
    </div>
  );
};

export default Register;
