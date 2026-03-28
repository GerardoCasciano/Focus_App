import React, { useEffect, useState } from "react";
import { MapContainer, TileLayer, Marker, Popup, useMap } from "react-leaflet";
import L from "leaflet";
import { MonumentCard } from "./MonumentCard";
import { Scanner } from "./Scanner";
import Loader from "./Loader";
import { FORM_TRANSLATIONS } from "../traslations";
import "leaflet/dist/leaflet.css";
import "../assets/MapFeature.css";

delete (L.Icon.Default.prototype as any)._getIconUrl;

const focuIcon = L.icon({
  iconUrl: "/foculogo.png",
  iconSize: [40, 40],
  iconAnchor: [20, 40],
  popupAnchor: [0, -40],
});

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
// componente per centrare la mappa sull'utente
const CenterUser: React.FC<{ lat: number; lon: number }> = ({ lat, lon }) => {
  const map = useMap();
  useEffect(() => {
    map.setView([lat, lon], 15);
  }, [lat, lon]);
  return null;
};

export const MapFeature: React.FC<MapProps> = ({ currentLang }) => {
  const [elementi, setElementi] = useState<ElementoUrbano[]>([]);
  const [selectElemento, setSelectElemento] = useState<ElementoUrbano | null>(
    null,
  );

  const [loadingGps, setLoadingGps] = useState(true);
  const [loadingDati, setLoadingDati] = useState(false);
  const [userPos, setUserPos] = useState<{ lat: number; lon: number } | null>(
    null,
  );
  const [isScannerOpen, setIsScannerOpen] = useState(false);

  useEffect(() => {
    console.log("GPS useEffect avviato");
    navigator.geolocation.getCurrentPosition(
      (position) => {
        console.log(
          "GPS ok:",
          position.coords.latitude,
          position.coords.longitude,
        );
        setUserPos({
          lat: position.coords.latitude,
          lon: position.coords.longitude,
        });
        setLoadingGps(false);
      },
      (error) => {
        console.error("Errore GPS:", error);

        setLoadingGps(false);
      },
      { enableHighAccuracy: true, timeout: 5000 },
    );
  }, []);

  useEffect(() => {
    if (!userPos) return;
    const fetchVicine = async () => {
      try {
        setLoadingDati(true);
        const url = new URL("http://localhost:8080/api/mappa/vicine");
        url.searchParams.append("lat", userPos.lat.toString());
        url.searchParams.append("lon", userPos.lon.toString());
        url.searchParams.append("raggio", "5000");
        url.searchParams.append(
          "categorie",
          "MONUMENTO,CHIESA,STORIA,CIMITERI_STORICI",
        );

        const token =
          localStorage.getItem("accessToken") || localStorage.getItem("token");
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
        console.error("Errore recupero segnalazioni:", error);
      } finally {
        setLoadingDati(false);
      }
    };
    fetchVicine();
  }, [userPos]);

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
      {loadingGps && (
        <div
          style={{
            position: "fixed",
            top: 0,
            left: 0,
            width: "100%",
            height: "100%",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            flexDirection: "column",
            backgroundColor: "#1b1b1b",
            color: "#fff",
            fontSize: "1.2rem",
            zIndex: 9999,
            gap: "16px",
          }}
        >
          <div style={{ fontSize: "2rem" }}>{<Loader />}</div>
        </div>
      )}
      {loadingDati && (
        <Loader message={FORM_TRANSLATIONS[currentLang].loading} />
      )}
      {loadingGps && userPos && (
        <MapContainer
          center={[userPos.lat, userPos.lon]}
          zoom={15}
          style={{ width: "100%", height: "100%" }}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          <CenterUser lat={userPos.lat} lon={userPos.lon} />

          {/* Marker utente  */}
          <Marker position={[userPos.lat, userPos.lon]}>
            <Popup>Sei qui</Popup>
          </Marker>

          {/* Marker dei monumenti  */}
          {elementi.map((el) => (
            <Marker
              key={el.id}
              position={[el.lat, el.lon]}
              icon={focuIcon}
              eventHandlers={{ click: () => setSelectElemento(el) }}
            >
              <Popup>{el.nomeProposto}</Popup>
            </Marker>
          ))}
        </MapContainer>
      )}
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
  );
};

export default MapFeature;
