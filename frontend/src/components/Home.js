"use client"

import { useState, useEffect } from "react"
import {
  Cloud,
  Sun,
  CloudRain,
  Wind,
  Droplets,
  Eye,
  Gauge,
  MapPin,
  Search,
  User,
  LogIn,
  UserPlus,
  Settings,
  LogOut,
  Menu,
  X,
  HomeIcon,
  BarChart3,
  FileText,
  Bell,
  Wallet,
  Star,
  Info,
} from "lucide-react"
import axios from "axios"
import "../styles/Home.css"
import { useNavigate } from "react-router-dom"

const Home = () => {
  const [currentTime, setCurrentTime] = useState(new Date())
  const [cityInput, setCityInput] = useState("")
  const [isAnimating, setIsAnimating] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState("")
  const [showDropdown, setShowDropdown] = useState(false)

  // User states
  const [showUserDropdown, setShowUserDropdown] = useState(false)
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [userInfo, setUserInfo] = useState({ name: "", email: "" })

  // Slide menu state
  const [isMenuOpen, setIsMenuOpen] = useState(false)

  const [weatherData, setWeatherData] = useState({
    city: "",
    temperatureC: 0,
    condition: "",
    humidity: 0,
    windKph: 0,
    visibilityKm: 0,
    uvIndex: 0,
  })

const popularCities = [
  "Ha Noi",
  "Ho Chi Minh",
  "Hai Phong",
  "Da Nang",
  "Can Tho",
  "An Giang",
  "Ba Ria – Vung Tau",
  "Bac Giang",
  "Bac Kan",
  "Bac Lieu",
  "Bac Ninh",
  "Ben Tre",
  "Binh Dinh",
  "Binh Duong",
  "Binh Phuoc",
  "Binh Thuan",
  "Ca Mau",
  "Cao Bang",
  "Dak Lak",
  "Dak Nong",
  "Dien Bien",
  "Dong Nai",
  "Dong Thap",
  "Gia Lai",
  "Ha Giang",
  "Ha Nam",
  "Ha Tinh",
  "Hai Duong",
  "Hau Giang",
  "Hoa Binh",
  "Hung Yen",
  "Khanh Hoa",
  "Kien Giang",
  "Kon Tum",
  "Lai Chau",
  "Lam Dong",
  "Lang Son",
  "Lao Cai",
  "Long An",
  "Nam Dinh",
  "Nghe An",
  "Ninh Binh",
  "Ninh Thuan",
  "Phu Tho",
  "Quang Binh",
  "Quang Nam",
  "Quang Ngai",
  "Quang Ninh",
  "Quang Tri",
  "Soc Trang",
  "Son La",
  "Tay Ninh",
  "Thai Binh",
  "Thai Nguyen",
  "Thanh Hoa",
  "Thua Thien Hue",
  "Tien Giang",
  "Tra Vinh",
  "Tuyen Quang",
  "Vinh Long",
  "Vinh Phuc",
  "Yen Bai",
]


  // Menu items for slide menu
  const menuItems = [
    { icon: HomeIcon, label: "Trang chủ", id: "home", active: true },
    { icon: Search, label: "Tìm kiếm", id: "search" },
    { icon: Star, label: "Yêu thích", id: "favorites" },
    { icon: FileText, label: "Báo cáo", id: "reports" },
    { icon: Bell, label: "Thông báo", id: "notifications" },
    { icon: Settings, label: "Cài đặt", id: "settings" },
    { icon: Info, label: "Thông tin", id: "about" },
  ]

  const navigate = useNavigate()

  // Update clock
  useEffect(() => {
    const timer = setInterval(() => setCurrentTime(new Date()), 1000)
    return () => clearInterval(timer)
  }, [])

  // Check login and fetch default weather
  useEffect(() => {
    checkLoginStatus()
    fetchWeather("Ha Noi")
    getCurrentLocation()
  }, [])

  // Close dropdowns when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (!event.target.closest(".search-container")) setShowDropdown(false)
      if (!event.target.closest(".user-menu-container")) setShowUserDropdown(false)
    }
    document.addEventListener("mousedown", handleClickOutside)
    return () => document.removeEventListener("mousedown", handleClickOutside)
  }, [])

  // ✅ Check login using token from localStorage
  const checkLoginStatus = async () => {
    const token = localStorage.getItem("token")
    if (!token) {
      setIsLoggedIn(false)
      setUserInfo({ name: "", email: "" })
      navigate("/")
      return
    }
    try {
      const res = await axios.get("http://localhost:8080/users/profile", {
        headers: { Authorization: `Bearer ${token}` },
      })
      setIsLoggedIn(true)
      setUserInfo(res.data.data)
    } catch (err) {
      console.error("❌ Error checking login:", err)
      setIsLoggedIn(false)
      setUserInfo({ name: "", email: "" })
      localStorage.removeItem("token")
      navigate("/")
    }
  }

  const handleLogin = () => navigate("/")

  const handleLogout = () => {
    localStorage.removeItem("token")
    setIsLoggedIn(false)
    setUserInfo({ name: "", email: "" })
    setShowUserDropdown(false)
    navigate("/Home")
  }

  const handleRegister = () => navigate("/register")

  const handleSettings = () => {
    console.log("Open settings")
    setShowUserDropdown(false)
  }

  const getCurrentLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => console.log("Current pos:", pos.coords.latitude, pos.coords.longitude),
        (err) => console.error("Geolocation error:", err),
      )
    }
  }

  const fetchWeather = async (city) => {
    const cityToFetch = city || "Yen Bai"
    setIsLoading(true)
    setError("")
    try {
      const apiUrl = `http://localhost:8080/weather/find?local=${encodeURIComponent(cityToFetch)}`
      const res = await axios.get(apiUrl, { timeout: 15000 })
      if (res.data) {
        setWeatherData(res.data)
        setIsAnimating(true)
        setTimeout(() => setIsAnimating(false), 600)
      }
    } catch (err) {
      console.error("❌ API error:", err)
      let errorMessage = "Không thể lấy thông tin thời tiết."
      if (err.response) {
        if (err.response.status === 404) errorMessage += " Không tìm thấy thành phố."
        else if (err.response.status === 500) errorMessage += " Lỗi server."
      } else if (err.request) {
        errorMessage += " Server đang khởi động, chờ vài giây."
      } else {
        errorMessage += err.message
      }
      setError(errorMessage)
    } finally {
      setIsLoading(false)
    }
  }

  const handleSearch = () => {
    if (cityInput.trim()) {
      fetchWeather(cityInput.trim())
      setCityInput("")
      setShowDropdown(false)
    }
  }

  const handleKeyPress = (e) => e.key === "Enter" && handleSearch()
  const handleCitySelect = (city) => {
    setCityInput(city)
    fetchWeather(city)
    setShowDropdown(false)
  }
  const handleInputFocus = () => setShowDropdown(true)
  const handleInputChange = (e) => {
    setCityInput(e.target.value)
    setShowDropdown(true)
  }
  const filteredCities = popularCities.filter((city) => city.toLowerCase().includes(cityInput.toLowerCase()))

  const getWeatherIcon = (condition) => {
    const cond = condition?.toLowerCase() || ""
    if (cond.includes("nắng") || cond.includes("sunny")) return <Sun className="weather-icon sun-icon" />
    if (cond.includes("mưa") || cond.includes("rain")) return <CloudRain className="weather-icon rain-icon" />
    if (cond.includes("mây") || cond.includes("cloud")) return <Cloud className="weather-icon cloud-icon" />
    return <Sun className="weather-icon sun-icon" />
  }

  const getTemperatureColor = (temp) => {
    if (temp > 35) return "temp-very-hot"
    if (temp > 28) return "temp-hot"
    if (temp > 20) return "temp-warm"
    if (temp > 15) return "temp-cool"
    return "temp-cold"
  }

  // Handle menu item click
  const handleMenuItemClick = (itemId) => {
    console.log(`Clicked menu item: ${itemId}`)
    setIsMenuOpen(false)
    // Add navigation logic here based on itemId
    switch (itemId) {
      case "home":
        navigate("/Home")
        break
      case "settings":
        handleSettings()
        break
      // Add more cases as needed
      default:
        console.log(`Navigate to ${itemId}`)
    }
  }

  return (
    <div className="weather-app">
      {/* BACKGROUND ELEMENTS */}
      <div className="background-elements">
        <div className="floating-element element-1"></div>
        <div className="floating-element element-2"></div>
        <div className="floating-element element-3"></div>
        <div className="floating-element element-4"></div>
      </div>

      {/* SLIDE MENU OVERLAY */}
      {isMenuOpen && (
        <div className="menu-overlay" onClick={() => setIsMenuOpen(false)}>
          <div className="slide-menu" onClick={(e) => e.stopPropagation()}>
            {/* Menu Header */}
            <div className="menu-header">
              <div className="menu-logo">
                <div className="siw-logo-small">SIW</div>
                <span className="menu-title">Dashboard</span>
              </div>
              <button className="menu-close-btn" onClick={() => setIsMenuOpen(false)}>
                <X size={24} />
              </button>
            </div>

            {/* Menu Items */}
            <div className="menu-items">
              {menuItems.map((item, index) => (
                <button
                  key={item.id}
                  className={`menu-item ${item.active ? "active" : ""}`}
                  onClick={() => handleMenuItemClick(item.id)}
                  style={{ animationDelay: `${index * 0.1}s` }}
                >
                  <div className="menu-item-icon">
                    <item.icon size={24} />
                  </div>
                  <span className="menu-item-label">{item.label}</span>
                </button>
              ))}
            </div>

            {/* Menu Footer */}
            <div className="menu-footer">
              <div className="user-profile">
                <div className="user-avatar-menu">
                  <User size={32} />
                </div>
                <div className="user-info-menu">
                  <div className="user-name-menu">{userInfo.name || "Guest User"}</div>
                  <div className="user-email-menu">{userInfo.email || "guest@siw.com"}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      <div className="main-container">
        {/* TOP NAVIGATION */}
        <nav className="top-nav-bar">
          <div className="nav-left">
            <button className="menu-toggle-btn" onClick={() => setIsMenuOpen(true)}>
              <Menu size={24} />
            </button>
            <div className="weather-logo">
              <div className="siw-logo">SIW</div>
              <span className="weather-title">Weather</span>
            </div>
          </div>

          <div className="nav-center">
  <div className="search-container">
    <input
      type="text"
      className="nav-search-input"
      placeholder="Nhập tên thành phố..."
      value={cityInput}
      onChange={handleInputChange}
      onFocus={handleInputFocus}
      onKeyDown={handleKeyPress}
    />
    <button className="nav-search-button" onClick={handleSearch}>
      <Search size={20} />
    </button>

    {showDropdown && (
      <div className="city-dropdown">
        <div className="dropdown-header">Thành phố phổ biến</div>
        {filteredCities.length > 0 ? (
          filteredCities.map((city) => (
            <div
              key={city}
              className="dropdown-item"
              onClick={() => handleCitySelect(city)}
            >
              {city}
            </div>
          ))
        ) : (
          <div className="dropdown-item no-results">Không tìm thấy</div>
        )}
      </div>
    )}
  </div>
</div>


          <div className="nav-right">
            {/* User Dropdown Menu */}
            <div className="user-menu-container">
              <button className="user-menu-button" onClick={() => setShowUserDropdown(!showUserDropdown)}>
                <User className="user-icon" />
                {isLoggedIn && <span className="user-name">{userInfo.name.split(" ")[0]}</span>}
              </button>

              {showUserDropdown && (
                <div className="user-dropdown">
                  {isLoggedIn ? (
                    <>
                      <div className="user-info">
                        <div className="user-avatar">
                          <User className="avatar-icon" />
                        </div>
                        <div className="user-details">
                          <div className="user-display-name">{userInfo.name}</div>
                          <div className="user-email">{userInfo.email}</div>
                        </div>
                      </div>
                      <div className="dropdown-divider"></div>
                      <button className="dropdown-menu-item" onClick={handleSettings}>
                        <Settings className="menu-icon" /> Cài đặt
                      </button>
                      <button className="dropdown-menu-item logout-item" onClick={handleLogout}>
                        <LogOut className="menu-icon" /> Đăng xuất
                      </button>
                    </>
                  ) : (
                    <>
                      <button className="dropdown-menu-item" onClick={handleLogin}>
                        <LogIn className="menu-icon" /> Đăng nhập
                      </button>
                      <button className="dropdown-menu-item" onClick={handleRegister}>
                        <UserPlus className="menu-icon" /> Đăng ký
                      </button>
                    </>
                  )}
                </div>
              )}
            </div>
          </div>
        </nav>

        {/* MAIN WEATHER CONTENT */}
        <main className="weather-main">
        <div className={`clock-section ${showDropdown ? "clock-hidden" : ""}`}>


            <div className="date">
              {currentTime.toLocaleDateString("vi-VN", {
                weekday: "long",
                year: "numeric",
                month: "long",
                day: "numeric",
              })}
            </div>
            <div className="time">
              {currentTime.toLocaleTimeString("vi-VN", {
                hour: "2-digit",
                minute: "2-digit",
                second: "2-digit",
              })}
            </div>
          </div>

          {error && <div className="error-message">{error}</div>}

          <div className={`weather-card ${isAnimating ? "animate" : ""}`}>
            <div className="weather-city">
              <MapPin className="location-icon" />
              {weatherData.city || "Chọn thành phố"}
            </div>

            <div className="weather-condition">
              {getWeatherIcon(weatherData.condition)}
              <div className={`temperature ${getTemperatureColor(weatherData.temperatureC)}`}>
                {weatherData.temperatureC || "--"}°C
              </div>
              <div className="condition-text">{weatherData.condition || "Đang tải..."}</div>
            </div>

            <div className="weather-details">
              <div className="weather-detail">
                <Droplets className="detail-icon" />
                <span>{weatherData.humidity || "--"}%</span>
                <small>Độ ẩm</small>
              </div>
              <div className="weather-detail">
                <Wind className="detail-icon" />
                <span>{weatherData.windKph || "--"} km/h</span>
                <small>Gió</small>
              </div>
              <div className="weather-detail">
                <Eye className="detail-icon" />
                <span>{weatherData.visibilityKm || "--"} km</span>
                <small>Tầm nhìn</small>
              </div>
              <div className="weather-detail">
                <Gauge className="detail-icon" />
                <span>UV {weatherData.uvIndex || "--"}</span>
                <small>Chỉ số UV</small>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}

export default Home
