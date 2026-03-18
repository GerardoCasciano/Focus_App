package focusApp.focus.repositroy;

import focusApp.focus.entities.ElementoUrbano;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
//Query Spaziale

public interface ElementoUrbanoRepository extends JpaRepository<ElementoUrbano, Long> {
    boolean existsByOsmId(Long osmId);
    @Query(value = "Select * FROM elementi_urbani e WHERE" +
    //Crea un punto geometrico dalle coordinate.
            "ST_DWithin(e.posizione, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326), 1000)",
    nativeQuery = true)
    List<ElementoUrbano>trovaVicinanza(@Param("lat") Double lat, @Param("lon") Double lon);
}
