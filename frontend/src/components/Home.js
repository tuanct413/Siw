import React, { useState, useEffect } from "react";
import { fetchWeather, fetchWeather7days, fetchFavorite, getProfile,fetchWeather24hours } from "../api/weather/weatherService";
import { compareCity } from "../api/weather/weatherFavorite";
import "../styles/Home.css";
import conditionMap from "../utils/conditionMap";
import { Link } from "react-router-dom";
import Spinner from "../loading/Spinner";
import { cityMap } from "../utils/cityMap";

import { vietnamCities } from "../utils/vietnamCities";
 


const Home = () => {
  const [weatherData, setWeatherData] = useState(null);
  const [weather7daysData, setWeather7daysData] = useState(null);
  const [favoriteData, setFavoriteData] = useState(null);
  const [city, setCity] = useState("");
  const [profileData, setProfileData] = useState(null);
  const [city1, setCity1] = useState("");
  const [city2, setCity2] = useState("");
  const [compareData, setCompareData] = useState(null);
  const [activeTab, setActiveTab] = useState("weather");
  const [hourlyData, setHourlyData] = useState([]);
  const [filteredCities, setFilteredCities] = useState([]);
  const [showDropdown, setShowDropdown] = useState(false);

  const cityOptions = ["Ha Noi", "Ho Chi Minh", "Da Nang", "Hai Phong", "Can Tho"];

  useEffect(() => {
    loadWeather("Ha Noi");
    loadfetchFavorite("Ha Noi", "Rain");
    fetchUserProfile();
  }, []);

  const handleSearch = (value) => {
  setCity(value);

  if (value.length > 0) {
    const filtered = vietnamCities.filter((c) =>
      c.label.toLowerCase().includes(value.toLowerCase())
    );

    setFilteredCities(filtered);
    setShowDropdown(true);
  } else {
    setShowDropdown(false);
  }
};
  const fetchUserProfile = async () => {
    const token = localStorage.getItem("token");
    if (!token) return;
    try {
      const res = await getProfile();
      const { name, email, role } = res.data;
      setProfileData({ name, email, role });
    } catch (error) {
      console.error("Profile fetch error:", error.message);
    }
  };


const loadWeather = async (cityName) => {
  try {
    const [current, forecast, forecast1] = await Promise.all([
      fetchWeather(cityName),
      fetchWeather7days(cityName),
      fetchWeather24hours(cityName)
    ]);

    setWeatherData(current);
    setWeather7daysData(forecast);

    if (Array.isArray(forecast1)) {
      const formattedHourly = forecast1.slice(0, 12).map(hour => ({
        time: new Date(hour.createdAt).toLocaleTimeString([], {
          hour: "2-digit",
          minute: "2-digit"
        }),
        icon: <img src={hour.conditionIcon} alt={hour.conditionText} />,
        temp: `${hour.maxtemp_c}°`
      }));

      setHourlyData(formattedHourly);
    }

  } catch (error) {
    console.error("API error:", error);
  }
};

const loadfetchFavorite = async (cityName, weather) => {
  const token = localStorage.getItem("token");
  if (!token) {
    setFavoriteData(null);
    return null; // trả null nếu không có token
  }
  try {
    const favorite = await fetchFavorite(cityName, weather, token);
    setFavoriteData(favorite.data); // lưu data để render
    console.log(favorite.data.weatherMessage); 
    return favorite; // trả về favorite cho async/await
  } catch (error) {
    console.error("API error:", error);
    return null;
  }
};



  const fetchCompareData = async (cityName1, cityName2) => {
    try {
      const compare = await compareCity(cityName1, cityName2);
      setCompareData(compare);
    } catch (error) {
      console.error("API error:", error);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    setProfileData(null);
    window.location.reload();
  };



  return (
    <div className="weather-app">
      <div className="app-container">
        {/* Sidebar Navigation */}
        <div className="sidebar">
          {/* Profile Section at Top */}
          {profileData && (
            <div className="sidebar-profile">
              <div className="profile-avatar">
                <i className="bi bi-person-circle" style={{ fontSize: '32px', color: '#94a3b8' }}></i>
              </div>
              <div className="profile-info">
                <div className="profile-name">{profileData.name}</div>
                <div className="profile-email">{profileData.email}</div>
              </div>
            </div>
          )}

          {/* Navigation Items */}
          <div className="sidebar-nav">
            <div 
              className={`sidebar-item ${activeTab === "home" ? "active" : ""}`}
              onClick={() => setActiveTab("home")}
            >
              <span className="sidebar-icon">🌪️</span>
              <span>Home</span>
            </div>
            
            <div 
              className={`sidebar-item ${activeTab === "weather" ? "active" : ""}`}
              onClick={() => setActiveTab("weather")}
            >
              <span className="sidebar-icon">⛅</span>
              <span>Weather</span>
            </div>
            
            <div 
              className={`sidebar-item ${activeTab === "cities" ? "active" : ""}`}
              onClick={() => setActiveTab("cities")}
            >
              <span className="sidebar-icon">📋</span>
              <span>Cities</span>
            </div>
            
            <div 
              className={`sidebar-item ${activeTab === "map" ? "active" : ""}`}
              onClick={() => setActiveTab("map")}
            >
              <span className="sidebar-icon">⚠️</span>
              <span>warning</span>
            </div>
            
            <div 
              className={`sidebar-item ${activeTab === "settings" ? "active" : ""}`}
              onClick={() => setActiveTab("settings")}
            >
              <span className="sidebar-icon">⚙️</span>
              <span>Settings</span>
            </div>
          </div>

          {/* Logout Button at Bottom */}
          {profileData ? (
            <div className="sidebar-footer">
              <button className="logout-btn" onClick={handleLogout}>
                <span className="sidebar-icon">🚪</span>
                <span>Logout</span>
              </button>
            </div>
          ) : (
           <div className="sidebar-footer">
              <Link to="/login" className="login-btn">
                <span className="sidebar-icon">🔑</span>
                <span>Login</span>
              </Link>
            </div>
          )}
        </div>

        {/* Main Content */}
        <div className="main-content">
          {/* Weather Tab - Main View */}
          {activeTab === "weather" && (
            <div className="content-section">
              {/* Search Bar */}
              <div className="search-container">
                <form
                  className="search-form"
                  onSubmit={(e) => {
                    e.preventDefault();
                    loadWeather(city);
                  }}
                >
                  <input
                    type="text"
                    className="search-input"
                    placeholder="Search for cities"
                    value={city}
                    onChange={(e) => handleSearch(e.target.value)}
                  />
                  {showDropdown && (
                      <div className="search-dropdown">
                        {filteredCities.map((c, index) => (
                          <div
                            key={index}
                            className="dropdown-item"
                            onClick={() => {
                              setCity(c.label);
                              loadWeather(c.value);
                              setShowDropdown(false);
                            }}
                          >
                            {c.label}
                          </div>
                        ))}
                      </div>
                    )}
                </form>
              </div>

            {/* Weather Content Grid */}
            <div className="weather-content-grid">
              {/* Left Column */}
              <div className="left-column">
                {/* Main Weather Display */}
                {weatherData && (
                  <div className="main-weather-section">
                    <div className="city-header">
                      <h1 className="city-name">
                        {cityMap[weatherData.city] || weatherData.city}</h1>
                      <p className="chance-rain">Độ ẩm: {weatherData.humidity}%</p>
                    </div>

                    <div className="current-temp-display">
                      <div className="temperature-large">
                        {Math.round(weatherData.temperatureC)}°C
                      </div>
                      <div className="temp-details">
                        <img 
                          src={weatherData.icon} 
                          alt={conditionMap[weatherData.condition] ||weatherData.condition} 
                          className="weather-icon"
                        />
                        <p className="condition-text">{conditionMap[weatherData.condition] ||weatherData.condition}</p>
                      </div>
                    </div>
                  </div>
                )}
                  

               {/* Today's Forecast */}
          <div className="hourly-forecast-section">
            <h3 className="section-title">DỰ BÁO HÔM NAY</h3>
            <div className="hourly-items">
              {hourlyData.length > 0 ? (
                hourlyData.map((hour, index) => (
                  <div key={index} className="hourly-item">
                    <div className="hourly-time">{hour.time}</div>
                    <div className="hourly-icon">{hour.icon}</div>
                    <div className="hourly-temp">{hour.temp}</div>
                  </div>
                ))
              ) : (
                 <Spinner/>
              )}
            </div>
          </div>


                  {/* Air Conditions */}
                  {weatherData && (
                    <div className="air-conditions-section">
                      <div className="section-header">
                        <h3 className="section-title">ĐIỀU HÒA KHÔNG KHÍ</h3>
                        
                      </div>
                      
                      <div className="air-conditions-grid">
                        <div className="condition-item">
                          <div className="condition-label">
                            <span className="condition-icon">🌡️</span>
                            Cảm giác thực tế
                          </div>
                          <div className="condition-value">{weatherData.temperatureC}°</div>
                        </div>

                        <div className="condition-item">
                          <div className="condition-label">
                            <span className="condition-icon">💨</span>
                            Gió
                          </div>
                          <div className="condition-value">{weatherData.windKph} km/h</div>
                        </div>

                        <div className="condition-item">
                          <div className="condition-label">
                            <span className="condition-icon">💧</span>
                            Khả năng mưa
                          </div>
                          <div className="condition-value">{weatherData.humidity}%</div>
                        </div>

                        <div className="condition-item">
                          <div className="condition-label">
                            <span className="condition-icon">☀️</span>
                            Chỉ số UV
                          </div>
                          <div className="condition-value">3</div>
                        </div>
                      </div>
                    </div>
                  )}
                </div>

                {/* Right Column - 7 Day Forecast */}
                <div className="right-column">
                  <h3 className="section-title">thời tiết 7 ngày tới</h3>
                  
                  {weather7daysData && (
                    <div className="forecast-list">
                      {weather7daysData.slice(0, 7).map((day, index) => {
                        // Map conditions to icons
                        const getWeatherIcon = (condition) => {
                          if (index === 0) return "☀️";
                          if (index === 1 || index === 2) return "☀️";
                          if (index === 3 || index === 4) return "☁️";
                          if (index === 5) return "🌧️";
                          return "⚡";
                        };

                        const getConditionName = () => {
                          if (index === 0 || index === 1 || index === 2) return "Nắng";
                          if (index === 3 || index === 4) return "Mây";
                          if (index === 5) return "Mưa";
                          return "Bão";
                        };

                        const getDayName = (dateStr) => {
                          if (index === 0) return "Hôm nay";
                          const days = ["Chủ nhật", "Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy"];
                          const date = new Date(dateStr);
                          return days[date.getDay()];
                        };

                        return (
                          <div key={index} className="forecast-day-item">
                            <div className="day-name">{getDayName(day.date)}</div>
                            <div className="day-weather">
                              <span className="day-icon">{getWeatherIcon()}</span>
                              <span className="day-condition">{getConditionName()}</span>
                            </div>
                            <div className="day-temps">
                              <span className="temp-high">{day.maxtemp_c}°</span>
                              <span className="temp-low">/{day.mintemp_c}°</span>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}

          {/* Cities Tab */}
          {activeTab === "cities" && (
            <div className="content-section">
              <h2 className="page-title">Compare Cities</h2>

              <div className="compare-form-container">
                <form
                  onSubmit={(e) => {
                    e.preventDefault();
                    if (city1 && city2) {
                      fetchCompareData(city1, city2);
                    }
                  }}
                >
                  <div className="compare-inputs">
                    <select
                      className="compare-select"
                      value={city1}
                      onChange={(e) => setCity1(e.target.value)}
                    >
                      <option value="">-- Select City 1 --</option>
                      {cityOptions.map((c, index) => (
                        <option key={index} value={c}>{c}</option>
                      ))}
                    </select>

                    <div className="compare-icon">🔄</div>

                    <select
                      className="compare-select"
                      value={city2}
                      onChange={(e) => setCity2(e.target.value)}
                    >
                      <option value="">-- Select City 2 --</option>
                      {cityOptions.map((c, index) => (
                        <option key={index} value={c}>{c}</option>
                      ))}
                    </select>
                  </div>

                  <button className="compare-btn" type="submit">
                    Compare
                  </button>
                </form>
              </div>

              {compareData && (
                <div className="compare-result">
                  <h3 className="compare-result-title">Comparison Results</h3>
                  <div className="compare-result-content">
                    <p>{compareData.data.messgerCondition}</p>
                    <p>{compareData.data.messgersTemperatureC}</p>
                  </div>
                </div>
              )}
            </div>
          )}

          {/* Settings Tab - Alerts */}
          {activeTab === "settings" && (
            <div className="content-section">
              <h2 className="page-title">Weather Alerts</h2>

              {favoriteData ? (
                <div className="alert-card">
                  <div className="alert-icon">⚠️</div>
                  <div className="alert-content">
                    <h4 className="alert-location">
                      {favoriteData.city}{favoriteData.country ? `, ${favoriteData.country}` : ''}
                    </h4>
                    <p className="alert-message">{favoriteData.weatherMessage}</p>
                    <p className="alert-temp">{favoriteData.temperature} °C</p>
                    
                    {/* Hiển thị thêm thông tin nếu có */}
                    {favoriteData.condition && (
                      <div className="alert-extra-info">
                        <span className="info-badge">
                          <span className="badge-icon">🌤️</span>
                          Condition: {favoriteData.condition}
                        </span>
                      </div>
                    )}
                  </div>
                </div>
              ) : (
                <div className="no-alert-message">
                  <div className="no-alert-icon">🔔</div>
                  <h3>No Weather Alert</h3>
                  <p>Go to "Alerts" tab to create a weather alert for your city.</p>
                  <button 
                    className="go-alert-btn"
                    onClick={() => setActiveTab("map")}
                  >
                    Create Alert
                  </button>
                </div>
              )}
            </div>
          )}

          {/* Home Tab */}
          {activeTab === "home" && (
            <div className="content-section">
              <h2 className="page-title">Welcome to Weather App</h2>
              <p style={{ color: '#94a3b8', textAlign: 'center', marginTop: '20px' }}>
                Select a menu item from the sidebar to get started
              </p>
            </div>
          )}

          {/* Map Tab - Weather Alert Form */}
          {activeTab === "map" && (
            <div className="content-section">
              <h2 className="page-title">Weather Alert Lookup</h2>

              <div className="alert-form-container">
                <form
                  className="alert-form"
                  onSubmit={async (e) => {
                    e.preventDefault();
                    if (city && city.trim()) {
                      const selectedCondition = document.querySelector('input[name="condition"]:checked')?.value || 'Rain';
                      try {
                        const favorite = await loadfetchFavorite(city, selectedCondition);
                        setFavoriteData(favorite.data); // lưu data để hiển thị
                        setActiveTab("settings");
                      } catch (error) {
                        alert("Error fetching alert: " + "bạn cần đăng nhập để sử dụng chức năng này" );
                      }
                    }
                  }}
                >
                  <div className="form-group">
                    <label className="form-label">
                      <span className="label-icon">📍</span>
                      Tên thành phố
                    </label>
                    <input
                      type="text"
                      className="form-input"
                      placeholder="Nhập tên thành phố (ví dụ: Ha Noi, Da Nang)"
                      value={city}
                      onChange={(e) => setCity(e.target.value)}
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">
                      <span className="label-icon">🌤️</span>
                      Điều kiện thời tiết
                    </label>
                    <div className="condition-options">
                      <label className="condition-radio">
                        <input
                          type="radio"
                          name="condition"
                          value="Rain"
                          defaultChecked
                        />
                        <div className="radio-card">
                          <span className="radio-icon">🌧️</span>
                          <span className="radio-label">Rain</span>
                          <span className="radio-desc">Cảnh báo mưa</span>
                        </div>
                      </label>

                      <label className="condition-radio">
                        <input
                          type="radio"
                          name="condition"
                          value="Sunny"
                        />
                        <div className="radio-card">
                          <span className="radio-icon">☀️</span>
                          <span className="radio-label">Sunny</span>
                          <span className="radio-desc">Cảnh báo nắng</span>
                        </div>
                      </label>

                      <label className="condition-radio">
                        <input
                          type="radio"
                          name="condition"
                          value="Cloudy"
                        />
                        <div className="radio-card">
                          <span className="radio-icon">☁️</span>
                          <span className="radio-label">Cloudy</span>
                          <span className="radio-desc">Cảnh báo mây</span>
                        </div>
                      </label>

                      <label className="condition-radio">
                        <input
                          type="radio"
                          name="condition"
                          value="Storm"
                        />
                        <div className="radio-card">
                          <span className="radio-icon">⛈️</span>
                          <span className="radio-label">Storm</span>
                          <span className="radio-desc">Cảnh báo bão</span>
                        </div>
                      </label>
                    </div>
                  </div>

                  <button className="alert-submit-btn" type="submit">
                    <span>🔔</span>
                    Lấy cảnh báo thời tiết
                  </button>
                </form>

                <div className="info-box">
                  <div className="info-icon">💡</div>
                  <div className="info-content">
                    <h4>Hướng dẫn sử dụng</h4>
                    <p>Nhập tên thành phố và chọn điều kiện thời tiết bạn muốn nhận cảnh báo. Hệ thống sẽ hiển thị thông báo cảnh báo phù hợp cho thành phố đó.</p>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Home;