package de.elbosso.spacetraders.swing;

import de.elbosso.spacetraders.client.api.FleetApi;
import de.elbosso.spacetraders.client.invoker.ApiClient;
import de.elbosso.spacetraders.client.invoker.ApiException;
import de.elbosso.spacetraders.client.model.Ship;

import java.beans.IntrospectionException;

class ShipRegistry
{
    private static final org.slf4j.Logger CLASS_LOGGER=org.slf4j.LoggerFactory.getLogger(ShipRegistry.class);
    private static final org.slf4j.Logger EXCEPTION_LOGGER=org.slf4j.LoggerFactory.getLogger("ExceptionCatcher");
    private final static java.util.Map<String, ShipWindow> ships = new java.util.HashMap();
    private static ApiClient defaultClient;
    private static ShipRegistry sharedInstance;
    private FleetApi fleetApi;

    public static ShipRegistry getSharedInstance(ApiClient defaultClient)
    {
        if (sharedInstance == null)
            sharedInstance = new ShipRegistry(defaultClient);
        return sharedInstance;
    }

    private ShipRegistry(ApiClient defaultClient)
    {
        super();
        this.defaultClient = defaultClient;
        fleetApi=new FleetApi(defaultClient);
    }

    public ShipWindow getWindow(String id) throws IntrospectionException, ApiException
    {
        if (ships.containsKey(id) == false)
        {
            Ship ship=fleetApi.getMyShip(id).getData();
            try
            {
                ShipWindow sw = new ShipWindow(ship.getRegistration().getName(), ship, defaultClient);
                ships.put(id, sw);
            }
            catch(java.lang.Throwable t)
            {
                de.elbosso.util.Utilities.handleException(EXCEPTION_LOGGER,t);
            }
        }
        return ships.get(id);
    }
}
