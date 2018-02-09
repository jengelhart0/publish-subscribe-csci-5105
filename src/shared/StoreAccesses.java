package shared;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class StoreAccesses {
    private Map<String, Integer> offsets;
    private Date NextAccess = null;
    private Protocol protocol;

    StoreAccesses(Protocol protocol) {
        this.offsets = new HashMap<>();
        this.protocol = protocol;
        refreshOffsets();
    }


}
