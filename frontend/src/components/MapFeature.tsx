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
    <div className="map-page" style={{ position: "relative" }}>
      {loadingGps && (
        <div className="gps">
          <div>
            <Loader />
          </div>
        </div>
      )}

      {!loadingDati && userPos && (
        <>
          {loadingDati && (
            <div>
              {" "}
              <Loader message={FORM_TRANSLATIONS[currentLang].loading} />
            </div>
          )}

          <MapContainer
            key="mappa-principale"
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
        </>
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

      {/* button SOS  */}
      <div className="btn-ui">
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
        {/* button scanner */}
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
