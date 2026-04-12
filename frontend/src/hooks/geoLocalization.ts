import { useState, useEffect, useRef } from "react";

export const geoLocalization = () => {
    const [userPos, setUserpos] = useState<{lat: number; lon: number} | null>(null);
    const [loadingGps, setLoadingGps]= useState(true)
    const hasFetchedRef = useRef(false)

    useEffect(() => {
        if (hasFetchedRef.current) return;

        navigator.geolocation.getCurrentPosition(
            (position) => {
                hasFetchedRef.current = true
                setUserpos({
                    lat: position.coords.latitude,
                    lon: position.coords.longitude,
                })
                setLoadingGps(false);
            },
            (error) => {
                console.error("Errore Gps:", error);
                setLoadingGps(false)
            },
            { enableHighAccuracy: true, timeout: 5000}
        )
    },[])
    return {userPos, loadingGps};
} 