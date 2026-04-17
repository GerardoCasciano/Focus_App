import React from "react";
import { Marker, Popup } from "react-leaflet";
import {
  ambulanzaIcon,
  infoIcon,
  poliziaIcon,
  vigiliIcon,
} from "../EmergenzeIconConfig";
import { FORM_TRANSLATIONS } from "../traslations";
import { EmergenzaNazionale } from "../api/apiEmergenzeService";

interface EmergenzaMarkersProps {
  userPos: { lat: number; lon: number };
  emergenzaNazionale: EmergenzaNazionale;
  currentLang: string;
}

export const EmergenzaMarkers: React.FC<EmergenzaMarkersProps> = ({
  userPos,
  emergenzaNazionale,
  currentLang,
}) => {
  // offset per definire la distanza tra icone
  const offset = 0.0003;
  const traslation = FORM_TRANSLATIONS[currentLang];
  return (
    <>
      {/* Marker Polizia  */}
      {emergenzaNazionale.polizia && (
        <Marker
          position={[userPos.lat, userPos.lon + offset]}
          icon={poliziaIcon}
        >
          <Popup>
            <strong>{traslation.polizia || "POLIZIA"}:</strong>{" "}
            {emergenzaNazionale.polizia}
            <br />
            <a
              href={`tel:${emergenzaNazionale.polizia}`}
              className="btn btn-sm btn-primary mt-2 text-white"
            >
              {traslation.call || "Chiama"}
              {}
            </a>
          </Popup>
        </Marker>
      )}

      {/* Marker Ambulanza  */}
      {emergenzaNazionale.ambulanza && (
        <Marker
          position={[userPos.lat, userPos.lon + offset]}
          icon={ambulanzaIcon}
        >
          <Popup>
            <strong>{traslation.ambulanza || "AMBULANZA"}:</strong>{" "}
            {emergenzaNazionale.ambulanza}
            <br />
            <a
              href={`tel:${emergenzaNazionale.ambulanza}`}
              className="btn btn-sm btn-primary mt-2 text-white"
            >
              {traslation.call || "Chiama"}
              {}
            </a>
          </Popup>
        </Marker>
      )}
      {/* Marker Vigili del fuoco  */}
      {emergenzaNazionale.vigiliDelFuoco && (
        <Marker
          position={[userPos.lat, userPos.lon + offset]}
          icon={vigiliIcon}
        >
          <Popup>
            <strong>{traslation.vigiliDelFuoco || "VIGILI DEL FUOCO"}:</strong>{" "}
            {emergenzaNazionale.vigiliDelFuoco}
            <br />
            <a
              href={`tel:${emergenzaNazionale.vigiliDelFuoco}`}
              className="btn btn-sm btn-primary mt-2 text-white"
            >
              {traslation.call || "Chiama"}
              {}
            </a>
          </Popup>
        </Marker>
      )}
      {emergenzaNazionale.infoUtili && (
        <Marker position={[userPos.lat, userPos.lon + offset]} icon={infoIcon}>
          <Popup>
            <strong>{traslation.infoUtili || " INFO"}:</strong>
            <br />
            {emergenzaNazionale.infoUtili}
          </Popup>
        </Marker>
      )}
    </>
  );
};
