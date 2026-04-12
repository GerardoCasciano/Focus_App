
import { ElementoUrbano } from "../MapConfig";

export const getSegnlazioniVicine = async (lat: number, lon:number): Promise<ElementoUrbano[]> =>{
    const url = new URL("http://localhost:8080/api/mappa/vicine");
    url.searchParams.append("lat", lat.toString());
  url.searchParams.append("lon", lon.toString());
  url.searchParams.append("raggio", "5000");
  url.searchParams.append("categorie", "MONUMENTO,CHIESA,STORIA,CIMITERI_STORICI");

  const token = localStorage.getItem("accessToken") || localStorage.getItem("token");

  const response = await fetch(url.toString(), {
    method: "GET",
    headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
    }
  })
  if (!response.ok) throw new Error("Errore risposta server");
  return await response.json();
}

export default getSegnlazioniVicine;