package shared;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class StoreAccesses {
    private Map<String, Integer> offsets;
    private Date lastAccess = null;

    StoreAccesses(Protocol protocol) {
        this.offsets = new HashMap<>();
        initialize(protocol);
    }

    private void initialize(Protocol protocol) {
        String[] fields = protocol.getSubscriptionFields();
        for(String field: fields) {
            this.offsets.put(field, 0);
        }
    }

    public Integer getLastOffsetFor(String field) {
        return this.offsets.get(field);
    }

    public void setLastOffsetFor(String field, Integer offset) {
        this.offsets.put(field, offset);
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
    }
}
