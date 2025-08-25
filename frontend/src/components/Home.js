import React, { useState, useEffect } from 'react';
import { Cloud, Sun, CloudRain, Wind, Thermometer, Droplets, Eye, Gauge, MapPin, Loader } from 'lucide-react';
// import weatherService from '../services/weatherService'; // Uncomment nếu dùng service
import axios from 'axios';
import '../styles/Home.css';

const Home = () => {
    const [currentTime, setCurrentTime] = useState(new Date());
    const [cityInput, setCityInput] = useState('');
    const [isAnimating, setIsAnimating] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    // Khởi tạo weatherData mặc định
    const [weatherData, setWeatherData] = useState({
        city: '',
        temperatureC: 0,
        condition: '',
        humidity: 0,
        windKph: 0,
        visibilityKm: 0,
        uvIndex: 0
    });

    // Danh sách các thành phố phổ biến ở Việt Nam (định dạng cho API)
    const popularCities = [
        'Ha Noi', 'Ho Chi Minh City', 'Da Nang', 'Hue', 
        'Can Tho', 'Hai Phong', 'Nha Trang', 'Da Lat'
    ];

    useEffect(() => {
        const timer = setInterval(() => setCurrentTime(new Date()), 1000);
        return () => clearInterval(timer);
    }, []);

    useEffect(() => {
        // Fetch thời tiết mặc định khi load trang
        fetchWeather('Ha Noi');
        // Thử lấy vị trí hiện tại
        getCurrentLocation();
    }, []);

    // Lấy vị trí hiện tại của user
    const getCurrentLocation = () => {
        
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const { latitude, longitude } = position.coords;
                    // Có thể dùng reverse geocoding để lấy tên thành phố từ lat/lng
                    // Hoặc gọi API với lat/lng
                    console.log('Current position:', latitude, longitude);
                },
                (error) => {
                    console.error('Error getting location:', error);
                }
            );
        }
    };



    // Gọi API thời tiết thật từ Render server
const fetchWeather = async (city) => {
    const defaultCity = 'Yen Bai'; // city mặc định
    const cityToFetch = city || defaultCity; // nếu city null/undefined thì dùng default

    setIsLoading(true);
    setError('');

    try {
        const apiUrl = `https://siw.onrender.com/weather/find?local=${encodeURIComponent(cityToFetch)}`;
        console.log('🔥 Calling API:', apiUrl);

        const response = await axios.get(apiUrl, {
            timeout: 15000,
            headers: { 'Accept': 'application/json', 'Content-Type': 'application/json' }
        });

        console.log('✅ API Response:', response.data);

        if (response.data) {
            setWeatherData(response.data);
            setIsAnimating(true);
            setTimeout(() => setIsAnimating(false), 600);
        }
    } catch (error) {
        console.error('❌ API Error:', error);
        let errorMessage = 'Không thể lấy thông tin thời tiết. ';

        if (error.response) {
            if (error.response.status === 404) errorMessage += 'Không tìm thấy thông tin thành phố này.';
            else if (error.response.status === 500) errorMessage += 'Lỗi server, vui lòng thử lại sau.';
            else errorMessage += `Lỗi ${error.response.status}: ${error.response.statusText}`;
        } else if (error.request) {
            errorMessage += 'Server Render đang khởi động, vui lòng chờ 30-60 giây và thử lại.';
        } else {
            errorMessage += error.message;
        }

        setError(errorMessage);
    } finally {
        setIsLoading(false);
    }
};


    const handleSearch = () => {
        if (cityInput.trim()) {
            fetchWeather(cityInput.trim());
            setCityInput('');
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter') handleSearch();
    };

    const handleCitySelect = (city) => {
        fetchWeather(city);
    };

    // Hàm lấy icon thời tiết dựa trên condition
    const getWeatherIcon = (condition) => {
        const conditionLower = condition.toLowerCase();
        
        if (conditionLower.includes('nắng') || conditionLower.includes('sunny')) {
            return <Sun className="weather-icon sun-icon" />;
        } else if (conditionLower.includes('mưa') || conditionLower.includes('rain')) {
            return <CloudRain className="weather-icon rain-icon" />;
        } else if (conditionLower.includes('mây') || conditionLower.includes('cloud')) {
            return <Cloud className="weather-icon cloud-icon" />;
        }
        return <Sun className="weather-icon sun-icon" />;
    };

    // Hàm lấy màu sắc dựa trên nhiệt độ
    const getTemperatureColor = (temp) => {
        if (temp > 35) return 'temp-very-hot';
        if (temp > 28) return 'temp-hot';
        if (temp > 20) return 'temp-warm';
        if (temp > 15) return 'temp-cool';
        return 'temp-cold';
    };

    return (
        <div className="weather-app">
            <div className="background-elements">
                <div className="floating-element element-1"></div>
                <div className="floating-element element-2"></div>
                <div className="floating-element element-3"></div>
                <div className="floating-element element-4"></div>
            </div>

            <div className="main-container">
                <header className="app-header">
                    <div className="clock-section">
                        <div className="date">
                            {currentTime.toLocaleDateString('vi-VN', {
                                weekday: 'long',
                                year: 'numeric',
                                month: 'long',
                                day: 'numeric'
                            })}
                        </div>
                        <div className="time">
                            {currentTime.toLocaleTimeString('vi-VN', {
                                hour: '2-digit',
                                minute: '2-digit',
                                second: '2-digit'
                            })}
                        </div>
                    </div>
                    <h1 className="app-title">Weather App</h1>
                    <p className="app-subtitle">Khám phá thời tiết tại nơi bạn ở một cách dễ dàng!</p>
                </header>

                <main className="main-content">
                    <section className={`weather-card ${isAnimating ? 'animating' : ''}`}>
                        <div className="weather-header">
                            <h2 className="city-name">
                                <MapPin className="location-icon" />
                                {weatherData.city || 'Chưa chọn thành phố'}
                            </h2>
                            {isLoading && <Loader className="loading-spinner spinning" />}
                        </div>

                        {error && (
                            <div className="error-message">
                                <p>{error}</p>
                            </div>
                        )}

                        <div className="current-weather">
                            {getWeatherIcon(weatherData.condition)}
                            <p className={`temperature ${getTemperatureColor(weatherData.temperatureC)}`}>
                                {weatherData.temperatureC}°C
                            </p>
                            <p className="condition">{weatherData.condition}</p>
                        </div>

                        <div className="weather-details">
                            <div className="detail-card">
                                <div className="detail-icon-container orange-color">
                                    <Thermometer className="detail-icon" />
                                </div>
                                <div className="detail-content">
                                    <p className="detail-label">Nhiệt độ</p>
                                    <p className="detail-value">{weatherData.temperatureC}°C</p>
                                </div>
                            </div>

                            <div className="detail-card">
                                <div className="detail-icon-container blue-color">
                                    <Droplets className="detail-icon" />
                                </div>
                                <div className="detail-content">
                                    <p className="detail-label">Độ ẩm</p>
                                    <p className="detail-value">{weatherData.humidity}%</p>
                                </div>
                            </div>

                            <div className="detail-card">
                                <div className="detail-icon-container green-color">
                                    <Wind className="detail-icon" />
                                </div>
                                <div className="detail-content">
                                    <p className="detail-label">Gió</p>
                                    <p className="detail-value">{weatherData.windKph} km/h</p>
                                </div>
                            </div>

                            <div className="detail-card">
                                <div className="detail-icon-container gray-color">
                                    <Eye className="detail-icon" />
                                </div>
                                <div className="detail-content">
                                    <p className="detail-label">Tầm nhìn</p>
                                    <p className="detail-value">{weatherData.visibilityKm} km</p>
                                </div>
                            </div>

                            <div className="detail-card">
                                <div className="detail-icon-container purple-color">
                                    <Gauge className="detail-icon" />
                                </div>
                                <div className="detail-content">
                                    <p className="detail-label">Chỉ số UV</p>
                                    <p className="detail-value">{weatherData.uvIndex}</p>
                                </div>
                            </div>
                        </div>
                    </section>

                    <section className="right-section">
                        <div className="search-section">
                            <h2 className="section-title">
                                <Cloud className="section-icon" />
                                Tìm kiếm thành phố
                            </h2>
                            <div className="search-container">
                                <input
                                    type="text"
                                    value={cityInput}
                                    onChange={(e) => setCityInput(e.target.value)}
                                    onKeyPress={handleKeyPress}
                                    placeholder="Nhập tên thành phố..."
                                    className="search-input"
                                    disabled={isLoading}
                                />
                                <button 
                                    onClick={handleSearch} 
                                    className="search-button"
                                    disabled={isLoading || !cityInput.trim()}
                                >
                                    {isLoading ? 'Đang tìm...' : 'Tìm kiếm'}
                                </button>
                            </div>
                        </div>

                        <div className="popular-cities-section">
                            <h2 className="section-title">
                                <MapPin className="section-icon" />
                                Thành phố phổ biến
                            </h2>
                            <div className="popular-cities-grid">
                                {popularCities.map((city, index) => (
                                    <button
                                        key={index}
                                        onClick={() => handleCitySelect(city)}
                                        className={`city-button ${weatherData.city === city ? 'active' : ''}`}
                                        disabled={isLoading}
                                    >
                                        {city}
                                    </button>
                                ))}
                            </div>
                        </div>

                        {/* AI Prediction Section */}
                        <div className="ai-forecast">
                            <h2 className="section-title">Dự đoán từ AI</h2>
                            <div className="ai-prediction">
                                {weatherData.temperatureC > 30 && (
                                    <p>🌞 Hôm nay trời nắng nóng, nhớ mang theo nước và kem chống nắng!</p>
                                )}
                                {weatherData.condition && weatherData.condition.toLowerCase().includes('mưa') && (
                                    <p>🌧️ Trời có mưa, đừng quên mang theo ô khi ra ngoài!</p>
                                )}
                                {weatherData.humidity > 80 && (
                                    <p>💧 Độ ẩm cao, có thể cảm thấy oi bức. Nên ở nơi thoáng mát!</p>
                                )}
                                {weatherData.windKph > 20 && (
                                    <p>💨 Gió mạnh, hãy cẩn thận khi di chuyển!</p>
                                )}
                                {!weatherData.city && (
                                    <p>🔍 Hãy chọn một thành phố để xem thông tin thời tiết!</p>
                                )}
                            </div>
                        </div>
                    </section>
                </main>

                <footer className="app-footer">
                    <p>Weather App © 2025 - Dữ liệu thời tiết cập nhật realtime</p>
                </footer>
            </div>
        </div>
    );
};

export default Home;