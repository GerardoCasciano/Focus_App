import React, {useEffect, useState} from "react";
import "./MapFeature.css";
import {MonumentCard} from "./MonumentCard";


interface ElementoUrbano {
    id: number;
    nome: string;
    latitudine: number;
    longitudine: number;
    tipo: string;
    descrizione: string;
    fotoUrl: string;
    categoria: string;
}

interface MapProps{
    currentLang: string;
}

export const MapFeature: React.FC<Mapprops> = ({currentLang}) => {
    const [elementi, setElementi] = useState<SegnalazioneMappaDTO[]>([]);
    const [selectElemento, setSelectElemento] = useState<SegnalazioneMappaDTO | null>(null);
    const [loading, setLoading] = useState(true);
    const[userPos, setUserPos] = useState<{lat: number; lon: number} | null> (null);
// recupero pos gps 
    useEffect(() =>{
        navigator.geolocation.getCurrentPosition(
            (position) =>{
                setUserPos({
                    lat: position.coords.latitude,
                    lon: position.coords.longitude,
                });
            },
            (error) => console.error("Errore GPS:", error),
            {enableHighAccuracy: true}
        )
    },[]);

    // chiamata controller (vicine) 
    useEffect(() =>{
        if(!userPos) return;

        const fetchVicine = async () => {
            try {
                setLoading(true);
                const url = new URL(("http://localhost:8080/api/mappa/vicine"));
                url.searchParams.append("lat", userPos.lat.toString()),
                url.searchParams.append("lon", userPos.lon.toString()),
                url.searchParams.append("raggio", "1500");
                url.searchParams.append("categorie","MONUMENTO,CHIESA,STORIA")
                const response = await fetch(url.toString())  
               if(!response.ok) throw new Error("Errore risposta server");
                const data = await response.json();
                setElementi(data);
            }catch(error){
                console.error("Errore recpero segnalazioni:", error);
               
            }finally{
                setLoading(false);
            }
        }
        fetchVicine()
    },[userPos]);
    return(

    )
}