package de.elbosso.spacetraders.swing;

import de.elbosso.spacetraders.client.api.FleetApi;
import de.elbosso.spacetraders.client.api.SystemsApi;
import de.elbosso.spacetraders.client.invoker.ApiClient;
import de.elbosso.spacetraders.client.invoker.ApiException;
import de.elbosso.spacetraders.client.model.*;
import de.netsysit.util.beans.InterfaceFactory;
import de.netsysit.util.beans.InterfaceFactoryException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.System;

public class ShipPanel extends javax.swing.JPanel
{
    private static final org.slf4j.Logger CLASS_LOGGER = org.slf4j.LoggerFactory.getLogger(ShipPanel.class);
    private static final org.slf4j.Logger EXCEPTION_LOGGER = org.slf4j.LoggerFactory.getLogger("ExceptionCatcher");
    private final ApiClient defaultClient;
    private final de.elbosso.spacetraders.client.api.FleetApi fleetApi;
    private final de.elbosso.spacetraders.client.api.SystemsApi systemsApi;
    private final javax.swing.JToolBar tb;
    private Ship ship;
    private de.netsysit.util.beans.InterfaceFactory interfaceFactory = new de.netsysit.util.beans.InterfaceFactory();
    private javax.swing.Action orbitAction;
    private javax.swing.Action dockAction;
    private javax.swing.Action refuelAction;
    private javax.swing.Action extractAction;
    private javax.swing.Action navigateAction;
    private javax.swing.Action travelToNearestMarketPlaceAction;
    private javax.swing.Action travelToNearestAsteroidFieldAction;

    public ShipPanel(ApiClient defaultClient, Ship ship) throws InterfaceFactoryException, ApiException
    {
        super(new java.awt.BorderLayout());
        this.defaultClient = defaultClient;
        this.ship = ship;
        this.fleetApi = new FleetApi(defaultClient);
        this.systemsApi = new SystemsApi(defaultClient);
        tb = new javax.swing.JToolBar();
        add(tb, BorderLayout.NORTH);
        add(interfaceFactory.fetchInterfaceForBean(ship, ship.getRegistration().getName()));
        createActions();
        manageActionState();
        tb.add(orbitAction);
        tb.add(dockAction);
        tb.add(refuelAction);
        tb.add(extractAction);
        tb.add(navigateAction);
        tb.add(travelToNearestMarketPlaceAction);
        tb.add(travelToNearestAsteroidFieldAction);
//        tb.add();
    }
/*    public boolean isMarketPlace(de.elbosso.spacetraders.client.model.System system) throws ApiException
    {
        boolean rv=false;
        GetSystemWaypoints200Response wr=systemsApi.getSystemWaypoints(system.getSymbol(),1,20);
        CLASS_LOGGER.debug("GetSystemWaypoints200Response {}",wr);
        java.util.List<Waypoint> waypoints=wr.getData();
        for(Waypoint waypoint:waypoints)
        {
            for(WaypointTrait trait:waypoint.getTraits())
            {
                if(rv==false)
                    rv=trait.getSymbol()== WaypointTrait.SymbolEnum.MARKETPLACE;
            }
            if(rv==true)
                break;
        }
        return rv;
    }
*/    public boolean isMarketPlace(de.elbosso.spacetraders.client.model.Waypoint waypoint) throws ApiException
    {
        boolean rv=false;
        for(WaypointTrait trait:waypoint.getTraits())
        {
            rv=trait.getSymbol()== WaypointTrait.SymbolEnum.MARKETPLACE;
            break;
        }
        return rv;
    }
    public boolean isAsteroidField(Waypoint waypoint)
    {
        boolean rv=false;
        for(WaypointTrait trait:waypoint.getTraits())
        {
            rv=trait.getSymbol()== WaypointTrait.SymbolEnum.MARKETPLACE;
            break;
        }
        return rv;
    }
    private void update() throws InterfaceFactoryException, ApiException
    {
        removeAll();
        ship=fleetApi.getMyShip(ship.getSymbol()).getData();
        add(tb, BorderLayout.NORTH);
        add(interfaceFactory.fetchInterfaceForBean(ship, ship.getRegistration().getName()));
        invalidate();
        validate();
        doLayout();
        repaint();
        manageActionState();
    }
    private void manageActionState() throws ApiException
    {
        CLASS_LOGGER.debug("nav {}",ship.getNav());
        GetWaypoint200Response sr=systemsApi.getWaypoint(ship.getNav().getSystemSymbol(),ship.getNav().getWaypointSymbol());

        if(ship.getNav().getStatus()== ShipNavStatus.DOCKED)
        {
            orbitAction.setEnabled(true);
            dockAction.setEnabled(false);
            refuelAction.setEnabled(isMarketPlace(sr.getData()));
            extractAction.setEnabled(false);
            navigateAction.setEnabled(false);
            travelToNearestMarketPlaceAction.setEnabled(false);
            travelToNearestAsteroidFieldAction.setEnabled(false);
        }
        if(ship.getNav().getStatus()== ShipNavStatus.IN_ORBIT)
        {
            orbitAction.setEnabled(false);
            dockAction.setEnabled(true);
            refuelAction.setEnabled(false);
            extractAction.setEnabled(isAsteroidField(sr.getData()));
            navigateAction.setEnabled(true);
            travelToNearestMarketPlaceAction.setEnabled(false);
            travelToNearestAsteroidFieldAction.setEnabled(false);
        }
    }

    private void createActions()
    {
        orbitAction = new javax.swing.AbstractAction("orbit")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    fleetApi.orbitShip(ship.getSymbol());
                    update();
                }
                catch(java.lang.Throwable t)
                {
                    de.elbosso.util.Utilities.handleException(EXCEPTION_LOGGER,ShipPanel.this,t);
                }
            }
        };
        orbitAction.setEnabled(false);
        dockAction = new javax.swing.AbstractAction("dock")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    fleetApi.dockShip(ship.getSymbol());
                    update();
                }
                catch(java.lang.Throwable t)
                {
                    de.elbosso.util.Utilities.handleException(EXCEPTION_LOGGER,ShipPanel.this,t);
                }
            }
        };
        dockAction.setEnabled(false);
        refuelAction = new javax.swing.AbstractAction("refuel")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {

                    update();
                }
                catch(java.lang.Throwable t)
                {
                    de.elbosso.util.Utilities.handleException(EXCEPTION_LOGGER,ShipPanel.this,t);
                }
            }
        };
        refuelAction.setEnabled(false);
        extractAction = new javax.swing.AbstractAction("extract")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {

                    update();
                }
                catch(java.lang.Throwable t)
                {
                    de.elbosso.util.Utilities.handleException(EXCEPTION_LOGGER,ShipPanel.this,t);
                }
            }
        };
        extractAction.setEnabled(false);
        navigateAction = new javax.swing.AbstractAction("navigate")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {

                    update();
                }
                catch(java.lang.Throwable t)
                {
                    de.elbosso.util.Utilities.handleException(EXCEPTION_LOGGER,ShipPanel.this,t);
                }
            }
        };
        navigateAction.setEnabled(false);
        travelToNearestMarketPlaceAction = new javax.swing.AbstractAction("> Marketplace")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {

                    update();
                }
                catch(java.lang.Throwable t)
                {
                    de.elbosso.util.Utilities.handleException(EXCEPTION_LOGGER,ShipPanel.this,t);
                }
            }
        };
        travelToNearestMarketPlaceAction.setEnabled(false);
        travelToNearestAsteroidFieldAction = new javax.swing.AbstractAction("> ASteroidField")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {

                    update();
                }
                catch(java.lang.Throwable t)
                {
                    de.elbosso.util.Utilities.handleException(EXCEPTION_LOGGER,ShipPanel.this,t);
                }
            }
        };
        travelToNearestAsteroidFieldAction.setEnabled(false);
    }
}
