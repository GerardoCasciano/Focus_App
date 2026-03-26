import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";

interface Segnalazione {
  id: string;
  nomeProposto: string;
  descrizione: string;
  categoria: string;
  urlImmagineRiferimento: string;
}

const AdminReviewPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [dati, setDati] = useState<Segnalazione | null>(null);
  const [loading, setLoading] = useState(true);

  // recupero tettagli della segnalazione
  useEffect(() => {
    const fetchDettagli = async () => {
      const token = localStorage.getItem("token");
      try {
        const response = await fetch(
          `http://localhost:8080/api/segnalazioni/${id}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        );
        if (response.ok) {
          const data = await response.json();
          setDati(data);
        } else {
          console.log("Errore recupero segnalazione:", response.status);
        }
      } catch (error) {
        console.log("Errore di connssione:", error);
      } finally {
        setLoading(false);
      }
    };
    if (id) {
      fetchDettagli();
    }
  }, [id]);

  // funizone per gestire le approvazioni
  const handleAction = async (azione: "approvata" | "rifiutata") => {
    const token = localStorage.getItem("token");
    if (!token) {
      alert("Sessione scaduta.Effettua nuovamente il login");
      return;
    }
    try {
      const response = await fetch(
        `http://localhost:8080/api/admin/${azione}/${id}`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        },
      );
      if (response.ok) {
        alert(
          `Segnalazione ${azione === "approvata" ? "approvata" : "rifiutata"} con successo!`,
        );
        navigate("/mappa");
      } else {
        alert("Errore durante l'operazione. Verifica i permessi Admin");
      }
    } catch (error) {
      console.error("Errore di rete:", error);
      alert("Impossibile contattare il server");
    }
  };
  if (loading)
    return (
      <div style={{ textAlign: "center", marginTop: "50px" }}>
        Caricamento dati in corso...
      </div>
    );
  if (!dati)
    return (
      <div style={{ textAlign: "center", marginTop: "50px" }}>
        Segnalazioe non trovata.
      </div>
    );
  return (
    <div
      style={{
        padding: "20px",
        maxWidth: "800px",
        margin: "0",
        fontFamily: "Arial, sans-serif",
      }}
    >
      <h1 style={{ color: "#2c3e50", textAlign: "center" }}>
        Reviisone Amministratore
      </h1>
      <div
        style={{
          backgroundColor: "#f9f9f9",
          padding: "20px",
          borderRadius: "15px",
          boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
        }}
      ></div>
      <div style={{ textAlign: "center", marginBottom: "20px" }}>
        <img
          src={dati.urlImmagineRiferimento}
          alt="Segnalazione monumento"
          style={{ maxWidth: "100%", borderRadius: "10px", maxHeight: "400px" }}
        />
      </div>
      {/* dettagli del testo         */}
      <h2>{dati.nomeProposto}</h2>
      <p>
        <strong>Categoria</strong>
        <span style={{ color: "#3498db" }}>{dati.categoria}</span>
      </p>
      <p style={{ lineHeight: "1.6", fontSize: "1.1rem" }}>
        {dati.descrizione}
      </p>
      <hr style={{ margin: "30px 0", border: "0.5px solid #ddd" }} />
      {/* bottoni  */}
      <div style={{ display: "flex", gap: "20px", justifyContent: "center" }}>
        <button
          onClick={() => handleAction("approvata")}
          style={{
            padding: "12px 25px",
            backgroundColor: "#27ae60",
            color: "white",
            border: "none",
            borderRadius: "8px",
            cursor: "pointer",
            fontWeight: "bold",
          }}
        >
          Approva
        </button>
        <button
          onClick={() => handleAction("rifiutata")}
          style={{
            padding: "12px 25px",
            backgroundColor: "#e74c3c",
            color: "white",
            border: "none",
            borderRadius: "8px",
            cursor: "pointer",
            fontWeight: "bold",
          }}
        >
          Rifiuta Segnalazione
        </button>
      </div>
    </div>
  );
};

export default AdminReviewPage;
