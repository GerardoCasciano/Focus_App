import React, { useState } from "react";
import { Button } from "react-bootstrap";
import Loader from "./Loader";
import { FORM_TRANSLATIONS } from "../traslations";
import CameraView from "./CameraView";

// componente scanner
export const Scanner: React.FC<{ currentLang: string }> = ({ currentLang }) => {
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [result, setResult] = useState<{ title: string; desc: string } | null>(
    null,
  );

  const [LoaderMessage, setLoaderMessage] = useState("");

  console.log(`[SCANNER] Render. Lingua: ${currentLang}`);

  // funizone di anlisi IA
  const processImageIA = (imageSrc: string) => {
    const msgAnalisi =
      FORM_TRANSLATIONS[currentLang]?.ai_analyzing || "AI ANALYZING";
    setIsAnalyzing(msgAnalisi);

    console.log(
      "%c IA Immagine ricevuta. Avvio riconoscimento",
      "color: #02ccff; font-weight: bold;",
    );

    //Simuliamo il tempo della risposta dell'IA
    setTimeout(() => {
      const mockData =
        currentLang === "IT"
          ? {
              title: "COLOSSOE",
              desc: "Anfiteatro Flavio, Roma. Monumento iconico.",
            }
          : {
              title: "COLOSSEUM",
              desc: "Flavian Amphitheatre, Rome. Iconic monument.",
            };
      setResult(mockData);
      setIsAnalyzing(false);
      console.log(
        "%c [IA] Analisi completata con successo!",
        "color: #28a745; font-weight: bold;",
      );
    }, 3000);
  };

  // funzione per la recezione della camera
  const handleImageCapture = (imageSrc: string | null) => {
    if (!imageSrc) {
      console.error("SCANNER: Eroroe fotocamera , non ha prodotto l'immagine");
      setIsAnalyzing(false);
      alert("Errore hardware fotocamera");
      return;
    }
    console.log("%c CAMERA Frame ricevuto correttamente.", "color: #ffc107;");

    // passa il frame alla logica IA
    processImageIA(imageSrc);
  };

  // funzione di avvio tramite la funizone del tasto
  const startScanProcess = () => {
    setResult(null);
    setIsAnalyzing(true);
    setLoaderMessage(FORM_TRANSLATIONS[currentLang]?.loading || "STARTING");

    console.log("%c SCANNER Utente ha premuto SCAN.", "font-weight: bold;");

    // simulazione del tempo dello scatto
    setTimeout(() => {
      const fakeImage = "data:image/jpeg;base64,sample_data_uri";
      handleImageCapture(fakeImage);
    }, 1200);
  };
  return (
    <div
      className="scanner-container"
      style={{ position: "relative", height: "100vh", backgroundColor: "#000" }}
    >
      {isAnalyzing && <Loader message={LoaderMessage} />}
      <div
        className={`camera-cointainer-wrapper ${isAnalyzing ? "from-loading-blur" : ""}`}
        style={{ height: "80%" }}
      >
        {/* componente hardware  */}
        <CameraView onCapture={handleImageCapture} isAnalyzing={isAnalyzing} />
        {/* mirino  */}
        <div
          className={`scan-reticle ${isAnalyzing ? "scan-reticle-active" : ""}`}
        ></div>
        {/* container risultato */}
        {result && (
          <div className="scan- result-overlay">
            <h2 className="result-text" style={{ fontWeight: 900 }}>
              {result.title}
            </h2>
            <p className="description-text" style={{ fontSize: "1.2rem" }}>
              {result.desc}
            </p>
          </div>
        )}
        {/* button  */}
        <div
          className="scanner-controls d-flex justify-content-center align-center"
          style={{ height: "20%" }}
        >
          <Button
            onClick={startScanProcess}
            className="btn-scan-main"
            disabled={isAnalyzing}
          >
            {isAnalyzing ? "..." : "SCAN"}
          </Button>
        </div>
      </div>
    </div>
  );
};
export default Scanner;
