package de.elbosso.spacetraders.swing;

import de.elbosso.spacetraders.client.invoker.ApiClient;
import de.elbosso.spacetraders.client.invoker.ApiException;

import java.beans.IntrospectionException;

class SystemRegistry
{
    private static final org.slf4j.Logger CLASS_LOGGER=org.slf4j.LoggerFactory.getLogger(SystemRegistry.class);
    private static final org.slf4j.Logger EXCEPTION_LOGGER=org.slf4j.LoggerFactory.getLogger("ExceptionCatcher");
    private final static java.util.Map<String, SystemWindow> systems = new java.util.HashMap();
    private static ApiClient defaultClient;
    private static SystemRegistry sharedInstance;

    public static SystemRegistry getSharedInstance(ApiClient defaultClient)
    {
        if (sharedInstance == null)
            sharedInstance = new SystemRegistry(defaultClient);
        return sharedInstance;
    }

    private SystemRegistry(ApiClient defaultClient)
    {
        super();
        this.defaultClient = defaultClient;
    }

    public SystemWindow getWindow(String id) throws IntrospectionException, ApiException
    {
        if (systems.containsKey(id) == false)
        {
            SystemWindow sw = new SystemWindow(null, id, defaultClient);
            systems.put(id, sw);
        }
        return systems.get(id);
    }
}
