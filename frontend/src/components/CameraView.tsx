import React, { useRef, useCallback } from "react";
import Webcam from "react-webcam";
// confugurazione fotocamera posteriore

const videoCamSet = {
  width: { min: 640, ideal: 1929, max: 2560 },
  height: { min: 480, ideal: 1000, max: 1440 },
  facingMode: "enviromoent",
};
// funzione per passare la foto allo scanner

interface CameraViewProps {
  onCapture: (imageSrc: string | null) => void;
  isAnalyzing: boolean;
}

export const CameraView: React.FC<CameraViewProps> = ({
  onCapture,
  isAnalyzing,
}) => {
  const webcamRef = useRef<Webcam>(null);

  // funzione per catturare il frame quando lo scanner chiama
  const captureFrame = useCallback(() => {
    if (webcamRef.current) {
      const imageSrc = webcamRef.current.getScreenshot();
      console.log(
        "%c [CAMERA] Frame catturato con successo.",
        "color: #ffc107;",
      );
      onCapture(imageSrc);
    } else {
      console.error("[CAMERA] Errore: Webcam non pronta.");
      onCapture(null);
    }
  }, [onCapture]);
  return (
    <div
      className="camera-view-container"
      style={{ width: "100%", height: "100%" }}
    >
      <Webcam
        audio={false}
        ref={webcamRef}
        screenshotFormat="image/jpeg"
        style={{
          width: "100%",
          height: "100%",
          objectFit: "cover",
        }}
        className={isAnalyzing ? "webcam-blur" : ""}
      />
    </div>
  );
};
export default CameraView;
