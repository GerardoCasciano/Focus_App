import React, { useEffect, useRef, useState } from "react";
import { MapContainer, TileLayer, Marker, Popup, useMap } from "react-leaflet";

import { MonumentCard } from "./MonumentCard";
import { Scanner } from "./Scanner";
import Loader from "./Loader";
import { FORM_TRANSLATIONS } from "../traslations";
import { userIcon, focuIcon, ElementoUrbano, MapProps } from "../MapConfig";

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
  const hasFetchedRef = useRef(false);
  useEffect(() => {
    console.log("GPS useEffect avviato");
    navigator.geolocation.getCurrentPosition(
      (position) => {
        if (!hasFetchedRef.current) {
          hasFetchedRef.current = true;
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
        }
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
        <div className="gps">
          <div style={{ fontSize: "2rem" }}>{<Loader />}</div>
        </div>
      )}
      {loadingDati && !loadingGps && (
        <Loader message={FORM_TRANSLATIONS[currentLang].loading} />
      )}
      {!loadingGps && userPos && (
        <MapContainer
          key={`mappa-${userPos.lat}-${userPos.lon}`}
          center={[userPos.lat, userPos.lon]}
          zoom={15}
          style={{ width: "100vw", height: "100vh" }}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />

          {/* Marker utente  */}
          <Marker position={[userPos.lat, userPos.lon]} icon={userIcon}>
            <Popup> Sei qui</Popup>
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
