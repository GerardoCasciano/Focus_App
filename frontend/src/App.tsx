import "bootstrap/dist/css/bootstrap.min.css";
import React, { useState } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom"; // Aggiunto Navigate
import Registration from "./components/Registration";
import { MapFeature } from "./components/MapFeature";
import { Login } from "./components/Login";
import AdminReviewPage from "./page/AdminReviewPage";
function App() {
  const [lang, setLang] = useState<string>(
    localStorage.getItem("prefLang") || "IT",
  );
  return (
    <BrowserRouter
      future={{
        v7_startTransition: true,
        v7_relativeSplatPath: true,
      }}
    >
      <Routes>
        <Route path="/register" element={<Registration />} />
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />

        <Route path="/mappa" element={<MapFeature currentLang={lang} />} />
        <Route path="/admin/review/:id" element={<AdminReviewPage />} />
        <Route path="*" element={<div>Pagina non trovata</div>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
