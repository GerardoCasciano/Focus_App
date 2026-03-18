package focusApp.focus.service;


import focusApp.focus.entities.ElementoUrbano;
import focusApp.focus.repositroy.ElementoUrbanoRepository;
import lombok.RequiredArgsConstructor;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class ElementoUrbanoService {
    private final ElementoUrbanoRepository repository;
    // SRID 4326 coordinate standard per GPS mondiali.
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    // Trova monumenti e strade entro 1km usando la QUERY ST_DWithin
    public List<ElementoUrbano>cercaNelleVicinanze(Double lat, Double lon){
        return repository.trovaVicinanza(lat, lon);
    }
    //Metodo utile per cercare dati el DB la prima volta
    public ElementoUrbano salva(ElementoUrbano elemento, Double lat, Double lon){
        Point punto = geometryFactory.createPoint(new Coordinate(lon, lat));
        elemento.setPosizione(punto);
        return repository.save(elemento);
    }
}

