import React from "react";

import { Card } from "react-bootstrap";
import { FORM_TRANSLATIONS } from "../traslations";

interface MonumentCardProps {
  data: any;
  onClose: () => void;
  onOpenScanner: (monument: any) => void;
  currentLang: string;
}
export const MonumentCard: React.FC<MonumentCardProps> = ({
  data,
  onClose,
  onOpenScanner,
  currentLang,
}) => {
  if (!data) return null;
  console.log("Card Attiva", data.title);
  return (
    <div className="bottom-sheet-container d-flex justify-content-center">
      <Card className="info-card shadow-lg col-12 col-md-6 col-lg-5">
        <div className="drag-handle"></div>
        <Card.Body className="info-card-content px-4 pb-4">
          <button className="close-card-btn" onClick={onClose}>
            X
          </button>
          <Card.Title className="card-title-text">{data.title}</Card.Title>
          <Card.Text className="card-description">{data.desc}</Card.Text>
          <div className="card-actions d-grid gap-3">
            <button className="btn-scanner" onClick={() => onOpenScanner(data)}>
              {FORM_TRANSLATIONS[currentLang]?.scanner || "SCANNER"}
            </button>
            <button className="btn-audio">
              {FORM_TRANSLATIONS[currentLang]?.listen || "LISTEN"}
            </button>
            <button className="btn-navigate">
              {FORM_TRANSLATIONS[currentLang]?.go || "GO"}
            </button>
          </div>
        </Card.Body>
      </Card>
    </div>
  );
};
export default MonumentCard;
