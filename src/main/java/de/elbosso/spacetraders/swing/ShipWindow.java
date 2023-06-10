package de.elbosso.spacetraders.swing;

import de.elbosso.spacetraders.client.api.SystemsApi;
import de.elbosso.spacetraders.client.invoker.ApiClient;
import de.elbosso.spacetraders.client.invoker.ApiException;
import de.elbosso.spacetraders.client.model.Ship;
import de.netsysit.util.beans.InterfaceFactoryException;

import javax.swing.*;
import java.awt.*;
import java.beans.IntrospectionException;

class ShipWindow extends JFrame
{
    private static final org.slf4j.Logger CLASS_LOGGER=org.slf4j.LoggerFactory.getLogger(ShipWindow.class);
    private static final org.slf4j.Logger EXCEPTION_LOGGER=org.slf4j.LoggerFactory.getLogger("ExceptionCatcher");
    private final ApiClient defaultClient;
    private final Ship ship;
    private final SystemsApi systemsApi;
    private JPanel toplevel = new JPanel(new BorderLayout());
    private final ShipPanel shipPanel;

    public ShipWindow(String title, Ship ship, ApiClient defaultClient) throws HeadlessException, ApiException, IntrospectionException, InterfaceFactoryException
    {
        super(title);
        this.defaultClient = defaultClient;
        this.ship = ship;
        systemsApi = new SystemsApi(defaultClient);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setContentPane(toplevel);
        shipPanel=new ShipPanel(defaultClient,ship);
        toplevel.add(shipPanel);
        pack();
    }

}
