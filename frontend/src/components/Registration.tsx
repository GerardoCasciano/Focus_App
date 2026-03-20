import React, { useState } from "react";
import "../assets/Registration.css";
import {
  Form,
  Button,
  Row,
  Col,
  InputGroup,
  Container,
  Card,
} from "react-bootstrap";
import Loader from "./Loader";
import { FORM_TRANSLATIONS } from "../traslations";
//tipi per la gestione della ligua
interface LanguageOption {
  code: string;
  name: string;
  flag: string;
}

const LANGUAGES: LanguageOption[] = [
  { code: "IT", name: "Italiano", flag: "it" },
  { code: "EN", name: "English", flag: "gb" },
  { code: "FR", name: "Français", flag: "fr" },
  { code: "ES", name: "Español", flag: "🇪🇸" },
  { code: "ZH", name: "中文", flag: "🇨🇳" },
  { code: "JA", name: "日本語", flag: "🇯🇵" },
];

export const Registration: React.FC = () => {
  const [validated, setValidated] = useState(false);
  const [isLoading, setLoading] = useState(false);
  const [currentLang, setCurrentLang] = useState("IT");

  // caricamento
  const handleRegistration = async (event: React.FormEvent) => {
    setLoading(true);
    try {
      await new Promise((resolve) => setTimeout(resolve, 2000));

      console.log("Registrazione completata");
    } catch (error) {
      setLoading(false);
      alert("Errore durante la registrazione");
    }
  };
  //invio form
  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    const form = event.currentTarget;
    event.preventDefault();

    if (form.checkValidity() === false) {
      event.stopPropagation();
      setValidated(true);
      return;
    }
    setValidated(true);
    setLoading(true);
    console.log("Dati pronti per essere inviati");
    console.log("Linguia selezionata: ", currentLang);
    setTimeout(() => {
      setLoading(false);
      console.log("Registrazione completata con successo!");
      alert(`Ciao Benvenuti in FOCUS! Registrazione completata ${currentLang}`);
    }, 2500);
  };
  return (
    <div className="mobile-bg">
      <Container className="py-4 mb-1">
        <Card className="mobile-card border-0 shadow-lg ">
          <Card.Body style={{ position: "relative", padding: "30px" }}>
            {isLoading && (
              <Loader
                message={
                  FORM_TRANSLATIONS[currentLang]?.loading || "LOADING..."
                }
              />
            )}
            <div className={isLoading ? "form-loading-blur" : ""}>
              <div className="text-center mb-4">
                <h2 className="black">FOCUS</h2>
                <p className="text-muted small">REGISTRAZIONE UTENTE</p>
              </div>

              <Form noValidate validated={validated} onSubmit={handleSubmit}>
                <Row className="mb-3">
                  <Form.Group as={Col} xs="12" controlId="validationUsername">
                    <Form.Label className="label-custom">
                      {" "}
                      {FORM_TRANSLATIONS[currentLang]?.username}{" "}
                    </Form.Label>

                    <InputGroup hasValidation>
                      <InputGroup.Text className="input-custom-addon ">
                        @
                      </InputGroup.Text>

                      <Form.Control
                        required
                        type="text"
                        placeholder="Username"
                        name="username"
                        className="input-custom"
                      />
                    </InputGroup>
                  </Form.Group>
                </Row>
                <Row className="mb-3">
                  <Form.Group as={Col} xs="12" controlId="validationEmail">
                    <Form.Label className="label-custom">
                      {FORM_TRANSLATIONS[currentLang]?.email}
                    </Form.Label>
                    <Form.Control
                      required
                      type="email"
                      placeholder="inserisci@email.com"
                      className="input-custom"
                    />
                  </Form.Group>
                </Row>
                <Row className="mb-">
                  <Form.Group as={Col} xs="12" controlId="validationPassword">
                    <Form.Label className="label-custom">
                      {FORM_TRANSLATIONS[currentLang]?.password}
                    </Form.Label>
                    <Form.Control
                      required
                      type="password"
                      className="input-custom"
                      minLength={6}
                    />
                    <Form.Control.Feedback type="invalid">
                      La password deve essere almeno 6 caratteri.
                    </Form.Control.Feedback>
                  </Form.Group>
                </Row>
                <Form.Group
                  as={Col}
                  xs="5"
                  controlId="validationLanguage"
                  className="mt-3"
                >
                  <Form.Label className="label-custom">
                    {FORM_TRANSLATIONS[currentLang]?.language}
                  </Form.Label>

                  <Form.Select
                    required
                    name="language"
                    className="input-custom"
                    value={currentLang}
                    onChange={(event) => setCurrentLang(event.target.value)}
                  >
                    {LANGUAGES.map((lang) => (
                      <option key={lang.code} value={lang.code}>
                        {lang.flag} {lang.code}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
                <Form.Group className="mb-4">
                  <Form.Check
                    required
                    label="Accetto i termini e le condizioni"
                    feedback="Devi accettare prima di inviare"
                    feedbackType="invalid"
                    className="small-check mt-3"
                  />
                </Form.Group>
                <Button type="submit" className="w-100 btn-azure">
                  {isLoading
                    ? FORM_TRANSLATIONS[currentLang]?.loading
                    : FORM_TRANSLATIONS[currentLang]?.register}
                </Button>
              </Form>
            </div>
          </Card.Body>
        </Card>
      </Container>
    </div>
  );
};
export default Registration;
