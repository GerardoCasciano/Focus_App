import "bootstrap/dist/css/bootstrap.min.css";
import Registration from "./components/Registration";
import React from "react";
import { MapFeature } from "./components/MapFeature";
import { useState } from "react";
import { Login } from "./components/Login";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import AdminReviewPage from "./page/AdminReviewPage";
function App() {
  const [lang, setLang] = useState<string>("");
  return (
    <>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Registration />} />

          <Route path="/mappa" element={<MapFeature currentLang={lang} />} />
          <Route path="/admin/review/:id" element={<AdminReviewPage />} />
        </Routes>
      </Router>
    </>
  );
}

export default App;
