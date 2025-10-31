import React, { useState } from "react";
import "../styles/ForgotPassword.css";
import { verify, resetPassword } from "../api/verify/verifyService";
import { useNavigate } from "react-router-dom";

const ForgotPassword = () => {
  const [email, setEmail] = useState("");
  const [otp, setOtp] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [step, setStep] = useState(1); // 1: nhập email, 2: nhập OTP + mật khẩu
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  // Gửi mã xác thực
  const handleVerify = async () => {
    if (!email) {
      alert("❗ Vui lòng nhập email của bạn!");
      return;
    }

    setIsLoading(true);
    try {
      await verify(email);
      alert("✅ Mã xác thực đã được gửi đến email của bạn!");
      setStep(2);
    } catch (error) {
      console.error("❌ Lỗi gửi email xác thực:", error);
      alert("❌ Gửi mã xác thực thất bại!");
    } finally {
      setIsLoading(false);
    }
  };

  // Đặt lại mật khẩu
  const handleResetPassword = async () => {
    if (!otp || !newPassword) {
      alert("❗ Vui lòng nhập mã xác thực và mật khẩu mới!");
      return;
    }

    setIsLoading(true);
    try {
      await resetPassword(email, newPassword, otp);
      alert("✅ Đặt lại mật khẩu thành công!");
      navigate("/login");
    } catch (error) {
      console.error("❌ Lỗi đặt lại mật khẩu:", error);
      alert("❌ Mã xác thực không hợp lệ hoặc đã hết hạn!");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="forgot-page">
      <div className="forgot-container">
        <h2>Quên mật khẩu</h2>
        {step === 1 ? (
          <>
            <p>Nhập email để nhận mã xác thực đổi mật khẩu</p>
            <input
              type="email"
              className="form-input"
              placeholder="Nhập email của bạn"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <button className="verify-btn" onClick={handleVerify} disabled={isLoading}>
              {isLoading ? "Đang gửi..." : "Gửi mã xác thực"}
            </button>
          </>
        ) : (
          <>
            <p>Nhập mã xác thực và mật khẩu mới</p>
            <input
              type="text"
              className="form-input"
              placeholder="Mã xác thực"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
            />
            <input
              type="password"
              className="form-input"
              placeholder="Mật khẩu mới"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
            />
            <button className="verify-btn" onClick={handleResetPassword} disabled={isLoading}>
              {isLoading ? "Đang đặt lại..." : "Đặt lại mật khẩu"}
            </button>
          </>
        )}
        <button className="back-btn" onClick={() => navigate("/login")}>
          ← Quay lại đăng nhập
        </button>
      </div>
    </div>
  );
};

export default ForgotPassword;
