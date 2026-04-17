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
import { EmergenzaNazionale } from "../api/apiEmergenzeService";
import { getSosByPosition } from "../api/apiEmergenzeService";

export const MapFeature: React.FC<MapProps> = ({ currentLang }) => {
  const [elementi, setElementi] = useState<ElementoUrbano[]>([]);
  const [selectElemento, setSelecetElemento] = useState<ElementoUrbano | null>(
    null,
  );
  const [loadingDati, setLoadingDati] = useState(false);
  const [isScannerOpen, setIsScannerOpen] = useState(false);

  const { userPos, loadingGps } = geoLocalization();
  const [emergenza, setEmergenza] = useState<EmergenzaNazionale | null>(null);

  useEffect(() => {
    if (!userPos) return;

    const fetchAllData = async () => {
      try {
        setLoadingDati(true);

        const [data, dataSos] = await Promise.all([
          getSegnlazioniVicine(userPos.lat, userPos.lon),
          getSosByPosition(" ", userPos.lat, userPos.lon),
        ]);
        setElementi(data);
        setEmergenza(dataSos);
      } catch (error) {
        console.error(error);
      } finally {
        setLoadingDati(false);
      }
    };
    fetchAllData();
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
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        height: "100vh",
        width: "100vw",
        overflow: "hidden",
      }}
    >
      <div className="map-page" style={{ position: "relative", flex: 1 }}>
        {loadingGps && (
          <div
            className="gps"
            style={{
              position: "absolute",
              zIndex: 1000,
              width: "100%",
              height: "100%",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              background: "rgba(0,0,0,0.3)",
            }}
          >
            <Loader />
          </div>
        )}

        {!loadingDati && userPos && (
          <MapContainer
            key="mappa-principale"
            center={[userPos.lat, userPos.lon]}
            zoom={15}
            style={{ width: "100%", height: "100%" }}
          >
            <TileLayer
              attribution="&copy; OpenStreetMap"
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />

            <Marker position={[userPos.lat, userPos.lon]} icon={userIcon}>
              <Popup> Sei qui</Popup>
            </Marker>

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

      {/* display button */}
      <div
        className="btn-ui"
        style={{
          display: "flex",
          height: "80px",
          width: "100%",
          flexDirection: "row",
          justifyContent: "space-around",
          alignItems: "center",
          background: "#f5f1f1",
          borderTop: "1px solid #02ccff",
          padding: "10px",
          zIndex: 1001,
        }}
      >
        {/* SOS  */}
        {emergenza && (
          <Button
            className="btn-phone"
            onClick={() =>
              (window.location.href = `tel:${emergenza.numeroEmergenza}`)
            }
            title="SOS"
          >
            <i className="bi bi-telephone-fill"></i>
          </Button>
        )}
        {/* Scanner  */}
        <Button
          className="btn-scanner"
          onClick={() => setIsScannerOpen(true)}
          title="Scanner"
        >
          <i className="bi bi-camera-fill"></i>
        </Button>
      </div>
    </div>
  );
};

export default MapFeature;
