import { createRoot } from "react-dom/client";
import React from "react";
import ReactDOM from "react-dom/client";

import App from "./App.tsx";
import "bootstrap/dist/css/bootstrap.min.css";
import "leaflet/dist/leaflet.css";
import "./index.css";
import "./assets/style.css";
const container = document.getElementById("root")!;
const root = createRoot(container);
root.render(<App />);
