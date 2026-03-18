import React from "react";
interface LoaderProps {
  message?: string;
}

const Loader: React.FC<LoaderProps> = ({ message = "LOADING..." }) => {
  return (
    <div className="spinner-overlay">
      <div className="logo-spinner" aria-hidden="true"></div>
      <span
        className="text-main fw-bold"
        style={{ fontSize: "1rem", letterSpacing: "3px", marginTop: "15px" }}
      >
        {message}
      </span>
    </div>
  );
};
export default Loader;
