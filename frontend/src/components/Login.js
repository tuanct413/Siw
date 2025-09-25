import React, { useState } from 'react';
import '../styles/Login.css';
import { useNavigate } from 'react-router-dom';
import axios from "axios";

const Login = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });
    const [isLoading, setIsLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);

    const handleInputChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

 const handleLogin = async (e) => {
    e.preventDefault();
    setIsLoading(true);

    try {
        const response = await axios.post("http://localhost:8080/users/login", {
            email: formData.email,
            password: formData.password
        });

        const token = response.data.token;  // backend trả token
        localStorage.setItem("token", token);  // 🔹 lưu token

        alert(response.data.message || "Đăng nhập thành công!");
        navigate("/home");
    } catch (error) {
        console.error("❌ Login error:", error.response?.data || error.message);
        alert(error.response?.data?.message || "Đăng nhập thất bại!");
    } finally {
        setIsLoading(false);
    }
};
    

    return (
        <div className="login-page">
            <div className="login-container">
                <div className="logo-section">
                    <div className="logo">
                        <span className="logo-text">SIW</span>
                    </div>
                    <h2 className="welcome-text">Chào mừng trở lại!</h2>
                    <p className="subtitle">Đăng nhập vào tài khoản của bạn</p>
                </div>

                <form onSubmit={handleLogin}>
                    <div className="form-group">
                        <input 
                            type="email" 
                            name="email"
                            value={formData.email}
                            onChange={handleInputChange}
                            className="form-input" 
                            placeholder="Email"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <input 
                            type={showPassword ? "text" : "password"}
                            name="password"
                            value={formData.password}
                            onChange={handleInputChange}
                            className="form-input" 
                            placeholder="Mật khẩu"
                            required
                        />
                        <button 
                            type="button" 
                            className="password-toggle"
                            onClick={() => setShowPassword(!showPassword)}
                        >
                            {showPassword ? '🙈' : '👁️'}
                        </button>
                    </div>

                    <div className="button-group">
                        <button 
                            type="submit" 
                            className={`login-btn ${isLoading ? 'loading' : ''}`}
                            disabled={isLoading}
                        >
                            {isLoading ? 'Đang đăng nhập...' : 'Đăng Nhập'}
                        </button>

                        <button 
                            type="button" 
                            className={`register-btn ${isLoading ? 'loading' : ''}`}
                            onClick={() => navigate('/register')}
                        >
                            Đăng Ký
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Login;
