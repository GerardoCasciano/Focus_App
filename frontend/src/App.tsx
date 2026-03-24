import "bootstrap/dist/css/bootstrap.min.css";
import Registration from "./components/Registration";
import React from "react";
import { MapFeature } from "./components/MapFeature";
import { useState } from "react";
import { FORM_TRANSLATIONS } from "./traslations";
function App() {
  const [view, setView] = useState<"reg" | "map">("map");
  const [lang, setLang] = useState<string>("");
  return (
    <>
      {/* <Registration /> */}
      <MapFeature currentLang={lang} />
    </>
  );
}

export default App;
