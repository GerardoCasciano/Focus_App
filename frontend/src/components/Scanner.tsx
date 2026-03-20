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
  const [capturedImage, setCapturedImage] = useState<string | null>(null);
  const [LoaderMessage, setLoaderMessage] = useState(false);
  console.log(`[SCANNER] Render. Lingua: ${currentLang}`);

  // funizone allo scatto della foto di CameraView
  const handleImageCapture = (imageSrc: string | null) => {
    if (!imageSrc) {
      console.error("[SCANNER] Impossibile analizzare la foto catturata.");
      setIsAnalyzing(false);
      alert("Errore durante la cattura dell'immagine.");
      return;
    }
    // messaggio di loader
    const msgAnalisi =
      FORM_TRANSLATIONS[currentLang]?.ai_analyzing || "ANALYSIS...";
    setLoaderMessage(msgAnalisi);
    setIsAnalyzing(true);
    setResult(null);
    console.log("%c [SCANNER] Avvio cattura hardware.", "color: #ffc107;");

    // tempo per lo scatto della foto
    setTimeout(() => {
      const fakeImage = "data:image/jpeg;base64,...";
      handleImageCapture(fakeImage);
    }, 1200);
  };
  return(
    <div className="scanner-container" style={{position: 'relative', height: '100vh', backgroundColor: '#000'}}>
        {isAnalyzing && (<Loader message={LoaderMessage}/>)}
     <div className={`camera-cointainer-wrapper ${isAnalyzing ? "from-loading-blur" : ""}`} style={{height: '80%'}}>
       {/* componente hardware  */}
        <CameraView onCapture={handleImageCapture} isAnalyzing={isAnalyzing}/>
     {/* mirino  */}
     <div className={`scan-reticle ${isAnalyzing ? "scan-reticle-active" : ""}`}></div>
     {/* container risultato */}
     {result && (
        <div className="scan- result-overlay">
            <h2 className="result-text" style={{fontWeight: 900}}>{result.title}</h2>
             <p className="description-text" style={{fontSize: '1.2rem'}}>{result.desc}</p>
            </div>
     )}
     {/* button  */}
     <div className="scanner-controls d-flex justify-content-center align-center" style={{height: '20%'}}>
        <Button
        onClick={startScanProcess}
        className="btn-scan-main"
        disabled={isAnalyzing}
        style={{
            width: '100px',
            height: '100px',
            borderRadius: '50%',
            backgroundColor: '#02ccff',
            border: '5px solid #fff',
            color: '#000',
            fontWeight: '900',
            fontSize: '1.2rem',
            boxShadow: '0 0 5px rgba(255, 253, 247, 0.57) '
        }}
        {isAnalyzing ? "...." : "SCAN"}
        ></Button>
     </div>
     </div>
    </div>
  );
};
export default Scanner;