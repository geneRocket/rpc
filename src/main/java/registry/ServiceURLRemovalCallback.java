package registry;

import java.util.List;

public interface ServiceURLRemovalCallback {
    void removeNotExisted(List<ServiceURL> newAddresses);
}