import L from "leaflet";
delete (L.Icon.Default.prototype as any)._getIconUrl;


export const userIcon = L.divIcon({
  html: '<i class="bi bi-person-fill"style="font-size: 28px; color: #02ccff;"></i>',
  className: "",
  iconSize: [30, 30],
  iconAnchor: [15, 30],
});

export const focuIcon = L.icon({
  iconUrl: "/foculogo.png",
  iconSize: [60, 60],
  iconAnchor: [20, 40],
  popupAnchor: [0, -40],
});
 
export interface ElementoUrbano {
  id: string;
  nomeProposto: string;
  lat: number;
  lon: number;
  tipo: string;
  descrizione: string;
  categoria: string;
  urlImmagineRiferimento: string;
}

export interface MapProps {
  currentLang: string;
}
 

