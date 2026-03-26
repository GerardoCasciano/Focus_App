import React, { useEffect, useState } from "react";

import { MonumentCard } from "./MonumentCard";
import { Scanner } from "./Scanner";
import Loader from "./Loader";
import { FORM_TRANSLATIONS } from "../traslations";
import "../assets/MapFeature.css";
interface ElementoUrbano {
  id: string;
  nomeProposto: string;
  lat: number;
  lon: number;
  tipo: string;
  descrizione: string;

  categoria: string;
  urlImmagineRiferimento: string;
}

interface MapProps {
  currentLang: string;
}

export const MapFeature: React.FC<MapProps> = ({ currentLang }) => {
  const [elementi, setElementi] = useState<ElementoUrbano[]>([]);
  const [selectElemento, setSelectElemento] = useState<ElementoUrbano | null>(
    null,
  );
  const [loading, setLoading] = useState(true);
  const [userPos, setUserPos] = useState<{ lat: number; lon: number } | null>(
    null,
  );
  const [isScannerOpen, setIsScannerOpen] = useState(false);
  // recupero pos gps
  useEffect(() => {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setUserPos({
          lat: position.coords.latitude,
          lon: position.coords.longitude,
        });
      },
      (error) => console.error("Errore GPS:", error),
      { enableHighAccuracy: true },
    );
  }, []);

  // chiamata controller (vicine)
  useEffect(() => {
    if (!userPos) return;

    const fetchVicine = async () => {
      try {
        setLoading(true);
        const url = new URL("http://localhost:8080/api/mappa/vicine");
        url.searchParams.append("lat", userPos.lat.toString());
        url.searchParams.append("lon", userPos.lon.toString());
        url.searchParams.append("raggio", "5000");
        url.searchParams.append(
          "categorie",
          "MONUMENTO,CHIESA,STORIA,CIMITERI_STORICI",
        );
        // recupero del token
        const token =
          localStorage.getItem("accessToken") || localStorage.getItem("token");
        console.log("DEBUG Token", token);
        const response = await fetch(url.toString(), {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });

        if (!response.ok) throw new Error("Errore risposta server");
        const data = await response.json();
        setElementi(data);
      } catch (error) {
        console.error("Errore recpero segnalazioni:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchVicine();
  }, [userPos]);

  // gestione dello scanner
  if (isScannerOpen) {
    return (
      <Scanner
        target={selectElemento}
        onClose={() => setIsScannerOpen(false)}
        currentLang={currentLang}
      />
    );
  }
  return (
    <div className="map-page">
      {loading && <Loader message={FORM_TRANSLATIONS[currentLang].loading} />}
      <div className="map-canvas">
        {!loading &&
          userPos &&
          elementi.map((el) => (
            <div
              key={el.id}
              className="focus-marker"
              onClick={() => setSelectElemento(el)}
              style={{
                position: "absolute",

                left: `${50 + (el.lon - userPos.lon) * 5000}%`,
                top: `${50 - (el.lat - userPos.lat) * 5000}%`,
                transform: "translate(-50%, -50%)",
              }}
            >
              <div className="marker">
                <img src="public\foculogo.png" alt="logo" />
              </div>
            </div>
          ))}
        {/* mostra la card al select dell'elemento  */}
        {selectElemento && (
          <MonumentCard
            data={{
              title: selectElemento.nomeProposto,
              desc: selectElemento.descrizione,
            }}
            onClose={() => setSelectElemento(null)}
            onOpenScanner={() => setIsScannerOpen(true)}
            currentLang={currentLang}
          />
        )}
      </div>
    </div>
  );
};
export default MapFeature;
