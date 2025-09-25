import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './components/Login';
import Home from './components/Home';
import Register from './components/Register';

function App() {
  return (
    <Router>
      <Routes>
        {/* Mặc định vào Home */}
       <Route path="/login" element={<Login />} />
        <Route path="/home" element={<Home />} />

        {/* Các route còn lại */}
    
        <Route path="/register" element={<Register />} />
      </Routes>
    </Router>
  );
}

export default App;
