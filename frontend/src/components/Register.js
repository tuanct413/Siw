import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/Register.css';
import axios from 'axios';

const Register = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: '',
        confirmPassword: ''
    });
    const [verifyCode, setVerifyCode] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [isRegistered, setIsRegistered] = useState(false);

    const handleInputChange = (e) => {
        setFormData({...formData, [e.target.name]: e.target.value});
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        setIsLoading(true);

        if (formData.password !== formData.confirmPassword) {
            alert("Mật khẩu không khớp!");
            setIsLoading(false);
            return;
        }

        try {
            const response = await axios.post("https://siw.onrender.com/users/create", {
                name: formData.name,
                email: formData.email,
                password: formData.password
            });

            console.log("✅ Đăng ký thành công:", response.data);
            alert("Đăng ký thành công! Vui lòng nhập mã xác thực email.");
            setIsRegistered(true);
        } catch (error) {
            console.error("❌ Đăng ký lỗi:", error.response?.data || error.message);
            alert(error.response?.data?.message || "Đăng ký thất bại!");
        } finally {
            setIsLoading(false);
        }
    };

const handleVerify = async (e) => {
    e.preventDefault();
    setIsLoading(true);

    try {
        const params = new URLSearchParams();
        params.append('email', formData.email);
        params.append('code', verifyCode);

        const response = await axios.post(
            "https://siw.onrender.com/users/verify",
            params,
            { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }
        );

        console.log("✅ Verify email:", response.data);

        if (response.data.data?.verify) {
            alert("Xác thực thành công!");
            navigate('/');
        } else {
            alert(response.data.message || "Xác thực thất bại!");
        }
    } catch (error) {
        console.error("❌ Verify lỗi:", error.response?.data || error.message);
        alert(error.response?.data?.message || "Xác thực thất bại!");
    } finally {
        setIsLoading(false);
    }
};




    return (
        <div className="register-page">
            <div className="register-container">
                <div className="register-logo"><span>SIW</span></div>
                {!isRegistered ? (
                    <>
                        <h2>Đăng Ký Tài Khoản</h2>
                        <form onSubmit={handleRegister}>
                            <input type="text" name="name" placeholder="Tên người dùng"
                                   value={formData.name} onChange={handleInputChange} required />
                            <input type="email" name="email" placeholder="Email"
                                   value={formData.email} onChange={handleInputChange} required />
                            <input type="password" name="password" placeholder="Mật khẩu"
                                   value={formData.password} onChange={handleInputChange} required />
                            <input type="password" name="confirmPassword" placeholder="Nhập lại mật khẩu"
                                   value={formData.confirmPassword} onChange={handleInputChange} required />
                            <button type="submit" className={`register-btn ${isLoading ? 'loading' : ''}`}
                                    disabled={isLoading}>
                                {isLoading ? 'Đang đăng ký...' : 'Đăng Ký'}
                            </button>
                        </form>
                    </>
                ) : (
                    <>
                        <h2>Xác Thực Email</h2>
                        <p>Email của bạn: <strong>{formData.email}</strong></p>
                        <form onSubmit={handleVerify}>
                            <input type="text" placeholder="Nhập mã xác thực"
                                   value={verifyCode} onChange={(e) => setVerifyCode(e.target.value)} required />
                            <button type="submit" className={`register-btn ${isLoading ? 'loading' : ''}`}
                                    disabled={isLoading}>
                                {isLoading ? 'Đang xác thực...' : 'Xác Thực'}
                            </button>
                        </form>
                    </>
                )}
            </div>
        </div>
    );
};

export default Register;
