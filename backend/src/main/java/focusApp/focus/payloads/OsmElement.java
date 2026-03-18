package focusApp.focus.payloads;

import java.util.Map;

public record OsmElement(
        Long id,
        Double lat,
        Double lon,
        Map<String, String>tags
) {

}
