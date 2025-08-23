import React, { useState, useEffect } from 'react';
import { Cloud, Sun, CloudRain, Wind, Thermometer, Droplets, Eye, Gauge } from 'lucide-react';
import '../styles/Home.css';


const Home = () => {
    const [currentTime, setCurrentTime] = useState(new Date());
    const [cityInput, setCityInput] = useState('');
    const [isAnimating, setIsAnimating] = useState(false);

    // Khởi tạo weatherData mặc định để tránh crash
    const [weatherData, setWeatherData] = useState({
        currentWeather: { city: '', temperature: '', condition: '', feelsLike: '', iconName: 'sun' },
        weatherDetails: [],
        forecast: [],
        additionalInfo: [],
        footer: { text: 'Weather App © 2025' }
    });

    useEffect(() => {
        const timer = setInterval(() => setCurrentTime(new Date()), 1000);
        return () => clearInterval(timer);
    }, []);

    useEffect(() => {
        // Fetch thời tiết mặc định khi load trang
        fetchWeather('Hanoi');
    }, []);

  


    const fetchWeather = async (city) => {
        try {
            // Thay bằng API thật nếu cần
            // const response = await axios.get(`YOUR_WEATHER_API_URL?city=${city}`);
            // setWeatherData(response.data);

            // Dữ liệu giả lập
            setWeatherData({
                currentWeather: {
                    city,
                    temperature: '30°C',
                    condition: 'Nắng',
                    feelsLike: '32°C',
                    iconName: 'sun'
                },
                weatherDetails: [
                    { iconName: 'thermometer', label: 'Nhiệt độ', value: '30°C', colorClass: 'orange-color' },
                    { iconName: 'droplets', label: 'Độ ẩm', value: '60%', colorClass: 'blue-color' },
                    { iconName: 'wind', label: 'Gió', value: '10 km/h', colorClass: 'green-color' },
                    { iconName: 'eye', label: 'Tầm nhìn', value: '10 km', colorClass: 'gray-color' }
                ],
                forecast: [
                    { day: 'Thứ 7', iconName: 'sun', high: '32°C', low: '26°C' },
                    { day: 'Chủ Nhật', iconName: 'cloud', high: '30°C', low: '25°C' },
                    { day: 'Thứ 2', iconName: 'cloud-rain', high: '28°C', low: '24°C' },
                    { day: 'Thứ 3', iconName: 'sun', high: '31°C', low: '25°C' }
                ],
                additionalInfo: [
                    { iconName: 'sun', title: 'UV Index', value: '5' },
                    { iconName: 'wind', title: 'Wind Speed', value: '10 km/h' },
                    { iconName: 'droplets', title: 'Humidity', value: '60%' }
                ],
                
                footer: { text: 'Weather App © 2025' }
            });
        } catch (error) {
            console.error('Error fetching weather:', error);
        }
    };

    const handleSearch = () => {
        if (cityInput.trim()) {
            setIsAnimating(true);
            setTimeout(() => setIsAnimating(false), 600);
            fetchWeather(cityInput.trim());
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter') handleSearch();
    };

    const getWeatherIcon = (iconName) => {
        const icons = {
            sun: <Sun className="weather-icon sun-icon" />,
            'cloud-rain': <CloudRain className="weather-icon rain-icon" />,
            cloud: <Cloud className="weather-icon cloud-icon" />
        };
        return icons[iconName] || <Sun className="weather-icon sun-icon" />;
    };

    const getDetailIcon = (iconName) => {
        const icons = {
            thermometer: <Thermometer className="detail-icon" />,
            droplets: <Droplets className="detail-icon" />,
            wind: <Wind className="detail-icon" />,
            eye: <Eye className="detail-icon" />
        };
        return icons[iconName];
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
        <h2 className="city-name">{weatherData.currentWeather.city}</h2>
        <Gauge className="gauge-icon" />
    </div>
    <div className="current-weather">
        {getWeatherIcon(weatherData.currentWeather.iconName)}
        <p className="temperature">{weatherData.currentWeather.temperature}</p>
        <p className="condition">{weatherData.currentWeather.condition}</p>
        <p className="feels-like">Cảm giác như {weatherData.currentWeather.feelsLike}</p>
    </div>

    {/* AI Prediction / Fun Forecast Section */}
    <section className="ai-forecast">
        <h2>Dự đoán từ AI</h2>
        <p>Hôm nay trời nắng, thời tiết đẹp để đi chơi! 🌞</p>
    </section>

    <div className="weather-details">
        {weatherData.weatherDetails.map((item, index) => (
            <div key={index} className="detail-card">
                <div className={`detail-icon-container ${item.colorClass}`}>
                    {getDetailIcon(item.iconName)}
                </div>
                <p className="detail-label">{item.label}</p>
                <p className="detail-value">{item.value}</p>
            </div>
        ))}
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
                                />
                                <button onClick={handleSearch} className="search-button">
                                    Tìm kiếm
                                </button>
                            </div>
                        </div>

                        <div className="forecast-section">
                            <h2 className="section-title">Dự báo 4 ngày</h2>
                            <div className="forecast-grid">
                                {weatherData.forecast.map((day, index) => (
                                    <div key={index} className="forecast-card">
                                        <p className="forecast-day">{day.day}</p>
                                        <div className="forecast-icon">{getWeatherIcon(day.iconName)}</div>
                                        <div className="forecast-temp">
                                            <span className="temp-high">{day.high}</span>
                                            <span className="temp-low">{day.low}</span>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </section>
                </main>

                <section className="additional-info">
                    <h2 className="section-title center">Thông tin thêm</h2>
                    <div className="info-grid">
                        {weatherData.additionalInfo.map((info, index) => (
                            <div key={index} className="info-card">
                                {getWeatherIcon(info.iconName)}
                                <h3 className="info-title">{info.title}</h3>
                                <p className="info-value">{info.value}</p>
                            </div>
                        ))}
                    </div>
                </section>

                <footer className="app-footer">
                    <p>{weatherData.footer.text}</p>
                </footer>
            </div>
        </div>
    );
};

export default Home;
