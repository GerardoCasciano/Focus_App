import React, { useState } from "react";
import {
  Form,
  Button,
  Card,
  Container,
  InputGroup,
  Alert,
} from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { Key, Envelope } from "react-bootstrap-icons";
import "../assets/Registration.css";

export const Login: React.FC = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleLogin = async (event: React.FormEvent) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: email, password: password }),
        credentials: "include",
      });
      if (response.ok) {
        const data = await response.json();
        localStorage.setItem("token", data.accessToken);
        localStorage.setItem("refreshToken", data.refreshtoken);
        alert("Login effettuato con successo!");
        navigate("/mappa");
      } else {
        setError("Credenziali errate o utente non trovato");
      }
    } catch (error) {
      console.log("Errore della connessione:", error);
      setError("Impossibile connettersi al server");
    } finally {
      setLoading(false);
    }
  };
  return (
    <Container className="mt-5">
      <Card className="p-4 shadow">
        <h2 className="text-center">Accedi a FOCUS</h2>
        {error && <Alert variant="danger">{error}</Alert>}
        <Form onSubmit={handleLogin}>
          <Form.Group className="mb-3">
            <Form.Label>Email</Form.Label>
            <Form.Control
              type="email"
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Password</Form.Label>
            <Form.Control
              type="password"
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </Form.Group>
          <Button type="submit" className="w-100 btn-azure">
            Entra
          </Button>
        </Form>
      </Card>
    </Container>
  );
};
export default Login;
