package focusApp.focus.payloads;

import java.util.List;


public record OsmResponse(
 List<OsmElement> elements
) {
    @Override
    public List<OsmElement> elements() {
        return elements;
    }

}
