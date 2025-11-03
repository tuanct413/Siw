// Spinner.js
import React from "react";
import "../styles/Spinner.css"; // CSS cho spinner

const Spinner = () => {
  return (
    <div className="spinner-container">
      <div className="sk-circle">
        {Array.from({ length: 12 }).map((_, i) => (
          <div key={i} className={`sk-circle${i + 1} sk-child`}></div>
        ))}
      </div>
    </div>
  );
};

export default Spinner;
