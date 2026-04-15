import axios from "axios";

export interface EmergenzaNazionale {
    countryCode: string;
    numeroEmergenza: string;
    polizia: string;
    ambulanza: string;
    infoUtili: string;
}

const API_BASE_URL ="http://localhost:8080/api/emergenza";

export const getSosByPosition = async (utenteId: string, lat: number, lon: number): Promise <EmergenzaNazionale | null> => {
    try{
        const response = await axios.get(`${API_BASE_URL}/sos`, {
            params: { utenteId, lat, lon}
        });
        return response.data;
    }catch(error){
        console.error("Errore recupero sos", error);
        return null
    }
};