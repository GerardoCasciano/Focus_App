import React, { useEffect, useState } from "react";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import { Button } from "react-bootstrap";
import { MonumentCard } from "./MonumentCard";
import { Scanner } from "./Scanner";
import Loader from "./Loader";
import { FORM_TRANSLATIONS } from "../traslations";
import { getSegnlazioniVicine } from "../api/apiSegnalazioniService";
import { geoLocalization } from "../hooks/geolocalization";
import { userIcon, focuIcon, ElementoUrbano, MapProps } from "../MapConfig";

export const MapFeature: React.FC<MapProps> = ({ currentLang }) => {
  const [elementi, setElementi] = useState<ElementoUrbano[]>([]);
  const [selectElemento, setSelecetElemento] = useState<ElementoUrbano | null>(
    null,
  );
  const [loadingDati, setLoadingDati] = useState(false);
  const [isScannerOpen, setIsScannerOpen] = useState(false);

  const { userPos, loadingGps } = geoLocalization();

  useEffect(() => {
    if (!userPos) return;

    const fetchData = async () => {
      try {
        setLoadingDati(true);
        const data = await getSegnlazioniVicine(userPos.lat, userPos.lon);
        setElementi(data);
      } catch (error) {
        console.error(error);
      } finally {
        setLoadingDati(false);
      }
    };
    fetchData();
  }, [userPos]);

  if (isScannerOpen) {
    return (
      <Scanner
        target={setSelecetElemento}
        onClose={() => setIsScannerOpen(false)}
        currentLang={currentLang}
      />
    );
  }

  return (
    <div className="map-page">
      {loadingGps && (
        <div className="gps">
          <div>
            <Loader />
          </div>
        </div>
      )}
      {loadingDati && !loadingGps && (
        // Pulsante  per lo Scanner

        <Button
          className="btn-scanner"
          onClick={() => setIsScannerOpen(true)}
          title="Scanner"
        >
          <i className="bi bi-camera-fill"></i>
        </Button>
      )}
      <Loader message={FORM_TRANSLATIONS[currentLang].loading} />
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
              eventHandlers={{ click: () => setSelecetElemento(el) }}
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
          onClose={() => setSelecetElemento(null)}
          onOpenScanner={() => setIsScannerOpen(true)}
          currentLang={currentLang}
        />
      )}
    </div>
  );
};

export default MapFeature;
