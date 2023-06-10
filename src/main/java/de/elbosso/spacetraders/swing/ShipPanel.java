package de.elbosso.spacetraders.swing;

import de.elbosso.spacetraders.client.api.FleetApi;
import de.elbosso.spacetraders.client.api.SystemsApi;
import de.elbosso.spacetraders.client.invoker.ApiClient;
import de.elbosso.spacetraders.client.invoker.ApiException;
import de.elbosso.spacetraders.client.model.*;
import de.netsysit.util.beans.InterfaceFactory;
import de.netsysit.util.beans.InterfaceFactoryException;
import de.netsysit.util.lang.TimeSpan;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
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
    private javax.swing.Action extractUntilFullAction;
    private javax.swing.Action navigateAction;
    private javax.swing.Action travelToNearestMarketPlaceAction;
    private javax.swing.Action travelToNearestAsteroidFieldAction;
    private javax.swing.Action refreshAction;
    private javax.swing.Action sellAction;

    public ShipPanel(ApiClient defaultClient, Ship ship) throws InterfaceFactoryException, ApiException
    {
        super(new java.awt.BorderLayout());
        this.defaultClient = defaultClient;
        this.ship = ship;
        this.fleetApi = new FleetApi(defaultClient);
        this.systemsApi = new SystemsApi(defaultClient);
        tb = new javax.swing.JToolBar();
        tb.setFloatable(false);
        add(tb, BorderLayout.NORTH);
        add(interfaceFactory.fetchInterfaceForBean(ship, ship.getRegistration().getName()));
        createActions();
        manageActionState();
        tb.add(refreshAction);
        tb.add(orbitAction);
        tb.add(dockAction);
        tb.add(refuelAction);
        tb.add(sellAction);
        tb.add(extractAction);
        tb.add(extractUntilFullAction);
        tb.add(navigateAction);
        tb.add(travelToNearestMarketPlaceAction);
        tb.add(travelToNearestAsteroidFieldAction);
//        tb.add();
    }
    public Waypoint getNearestMarketPlace(de.elbosso.spacetraders.client.model.System system, Waypoint location) throws ApiException
    {
        java.awt.geom.Point2D home=new Point2D.Double(location.getX(),location.getY());
        double mindist= Double.MAX_VALUE;
        Waypoint rv=null;
        GetSystemWaypoints200Response wr=systemsApi.getSystemWaypoints(system.getSymbol(),1,20);
        CLASS_LOGGER.debug("GetSystemWaypoints200Response {}",wr);
        java.util.List<Waypoint> waypoints=wr.getData();
        Waypoint candidate=null;
        for(Waypoint waypoint:waypoints)
        {
            if(waypoint.getSymbol().equals(location.getSymbol())==false)
            {
                for (WaypointTrait trait : waypoint.getTraits())
                {
                    if ((rv == null)&&(trait.getSymbol() == WaypointTrait.SymbolEnum.MARKETPLACE))
                    {
                        candidate = waypoint;
                        break;
                    }
                }
                if(candidate!=null)
                {
                    java.awt.geom.Point2D cp=new Point2D.Double(candidate.getX(),candidate.getY());
                    if(cp.distanceSq(home)<mindist)
                    {
                        rv=candidate;
                        mindist=cp.distanceSq(home);
                    }
                }
            }
        }
        return rv;
    }
    public Waypoint getNearestAsteroidField(de.elbosso.spacetraders.client.model.System system, Waypoint location) throws ApiException
    {
        java.awt.geom.Point2D home=new Point2D.Double(location.getX(),location.getY());
        double mindist= Double.MAX_VALUE;
        Waypoint rv=null;
        GetSystemWaypoints200Response wr=systemsApi.getSystemWaypoints(system.getSymbol(),1,20);
        CLASS_LOGGER.debug("GetSystemWaypoints200Response {}",wr);
        java.util.List<Waypoint> waypoints=wr.getData();
        Waypoint candidate=null;
        for(Waypoint waypoint:waypoints)
        {
            if(waypoint.getSymbol().equals(location.getSymbol())==false)
            {
                if(waypoint.getType()==WaypointType.ASTEROID_FIELD)
                    candidate=waypoint;
                if(candidate!=null)
                {
                    java.awt.geom.Point2D cp=new Point2D.Double(candidate.getX(),candidate.getY());
                    if(cp.distanceSq(home)<mindist)
                    {
                        rv=candidate;
                        mindist=cp.distanceSq(home);
                    }
                }
            }
        }
        return rv;
    }
    public boolean isMarketPlace(de.elbosso.spacetraders.client.model.Waypoint waypoint) throws ApiException
    {
        boolean rv=false;
        for(WaypointTrait trait:waypoint.getTraits())
        {
            rv=trait.getSymbol()== WaypointTrait.SymbolEnum.MARKETPLACE;
            if(rv==true)
                break;
        }
        return rv;
    }
    public boolean isAsteroidField(Waypoint waypoint)
    {
        boolean rv=waypoint.getType()==WaypointType.ASTEROID_FIELD;
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
        try
        {
            SpaceTraders.getFleetWindow().update();
        }
        catch(java.lang.Throwable t)
        {
            de.elbosso.util.Utilities.handleException(EXCEPTION_LOGGER,ShipPanel.this,t);
        }
    }
    private void manageActionState() throws ApiException
    {
        CLASS_LOGGER.debug("nav {}",ship.getNav());

        GetWaypoint200Response sr=systemsApi.getWaypoint(ship.getNav().getSystemSymbol(),ship.getNav().getWaypointSymbol());
        GetSystem200Response system=systemsApi.getSystem(ship.getNav().getSystemSymbol());

        if(ship.getNav().getStatus()== ShipNavStatus.DOCKED)
        {
            orbitAction.setEnabled(true);
            dockAction.setEnabled(false);
            refuelAction.setEnabled(isMarketPlace(sr.getData()));
            extractAction.setEnabled(isAsteroidField(sr.getData()));
            extractUntilFullAction.setEnabled(isAsteroidField(sr.getData()));
            sellAction.setEnabled(isMarketPlace(sr.getData()));
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
            extractUntilFullAction.setEnabled(isAsteroidField(sr.getData()));
            sellAction.setEnabled(false);
            navigateAction.setEnabled(true);
            travelToNearestMarketPlaceAction.setEnabled(getNearestMarketPlace(system.getData(),sr.getData())!=null);
            travelToNearestAsteroidFieldAction.setEnabled(getNearestAsteroidField(system.getData(),sr.getData())!=null);
        }
        if(ship.getNav().getStatus()== ShipNavStatus.IN_TRANSIT)
        {
            orbitAction.setEnabled(false);
            dockAction.setEnabled(false);
            refuelAction.setEnabled(false);
            extractAction.setEnabled(false);
            extractUntilFullAction.setEnabled(false);
            sellAction.setEnabled(false);
            navigateAction.setEnabled(true);
            travelToNearestMarketPlaceAction.setEnabled(getNearestMarketPlace(system.getData(),sr.getData())!=null);
            travelToNearestAsteroidFieldAction.setEnabled(getNearestAsteroidField(system.getData(),sr.getData())!=null);
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
                    fleetApi.refuelShip(ship.getSymbol());
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
                    ExtractResourcesRequest extractResourcesRequest=new ExtractResourcesRequest();
                    ExtractResources201Response response=fleetApi.extractResources(ship.getSymbol(),extractResourcesRequest);
                    java.time.Duration duration=java.time.Duration.ofSeconds(response.getData().getCooldown().getTotalSeconds());
                    update();
                    de.elbosso.ui.dialog.CountdownDialog.create(null,"Cooldown...",ship.getCargo().toString()).showDialog(new TimeSpan(duration));
                }
                catch(java.lang.Throwable t)
                {
                    de.elbosso.util.Utilities.handleException(EXCEPTION_LOGGER,ShipPanel.this,t);
                }
            }
        };
        extractAction.setEnabled(false);
        extractUntilFullAction = new javax.swing.AbstractAction("extract*")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    ExtractResourcesRequest extractResourcesRequest=new ExtractResourcesRequest();
                    ExtractResources201Response response=fleetApi.extractResources(ship.getSymbol(),extractResourcesRequest);
                    java.time.Duration duration=java.time.Duration.ofSeconds(response.getData().getCooldown().getTotalSeconds());
                    update();
                    de.elbosso.ui.dialog.CountdownDialog.create(null,"Cooldown...",ship.getCargo().toString()).showDialog(new TimeSpan(duration),ship.getCargo().getUnits()<ship.getCargo().getCapacity()?extractAction:null);
                }
                catch(java.lang.Throwable t)
                {
                    de.elbosso.util.Utilities.handleException(EXCEPTION_LOGGER,ShipPanel.this,t);
                }
            }
        };
        extractUntilFullAction.setEnabled(false);
        navigateAction = new javax.swing.AbstractAction("navigate")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    WaypointTraitPanel waypointTraitPanel=new WaypointTraitPanel(ship.getNav().getSystemSymbol(),defaultClient,null);
                    de.netsysit.ui.dialog.GeneralPurposeOkCancelDialog gpocd=de.netsysit.ui.dialog.GeneralPurposeOkCancelDialog.create(ShipPanel.this,"Destinations");
                    gpocd.showDialog(waypointTraitPanel);
                    Waypoint destination=waypointTraitPanel.getSelectedWaypoint();
                    if(destination!=null)
                    {
                        NavigateShipRequest navigateShipRequest=new NavigateShipRequest();
                        navigateShipRequest.setWaypointSymbol(destination.getSymbol());
                        fleetApi.navigateShip(ship.getSymbol(),navigateShipRequest);
                        update();
                    }
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
                    GetWaypoint200Response sr=systemsApi.getWaypoint(ship.getNav().getSystemSymbol(),ship.getNav().getWaypointSymbol());
                    GetSystem200Response system=systemsApi.getSystem(ship.getNav().getSystemSymbol());
                    Waypoint target=getNearestMarketPlace(system.getData(),sr.getData());
                    if(target!=null)
                    {
                        NavigateShipRequest navigateShipRequest=new NavigateShipRequest();
                        navigateShipRequest.setWaypointSymbol(target.getSymbol());
                        fleetApi.navigateShip(ship.getSymbol(),navigateShipRequest);
                        update();
                    }
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
                    GetWaypoint200Response sr=systemsApi.getWaypoint(ship.getNav().getSystemSymbol(),ship.getNav().getWaypointSymbol());
                    GetSystem200Response system=systemsApi.getSystem(ship.getNav().getSystemSymbol());
                    Waypoint target=getNearestAsteroidField(system.getData(),sr.getData());
                    if(target!=null)
                    {
                        NavigateShipRequest navigateShipRequest=new NavigateShipRequest();
                        navigateShipRequest.setWaypointSymbol(target.getSymbol());
                        fleetApi.navigateShip(ship.getSymbol(),navigateShipRequest);
                        update();
                    }
                }
                catch(java.lang.Throwable t)
                {
                    de.elbosso.util.Utilities.handleException(EXCEPTION_LOGGER,ShipPanel.this,t);
                }
            }
        };
        travelToNearestAsteroidFieldAction.setEnabled(false);
        refreshAction=new javax.swing.AbstractAction("refresh")
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
        sellAction=new javax.swing.AbstractAction("sell")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    GetMarket200Response gmr=systemsApi.getMarket(ship.getNav().getSystemSymbol(),ship.getNav().getWaypointSymbol());
                    Market market=gmr.getData();
                    CLASS_LOGGER.debug("Import: {}",market.getImports());
                    CLASS_LOGGER.debug("Trade: {}",market.getTradeGoods());
                    java.util.Map<java.lang.String,MarketTradeGood> tradeItems=new java.util.HashMap();
                    for(MarketTradeGood marketTradeGood:market.getTradeGoods())
                    {
                        tradeItems.put(marketTradeGood.getSymbol(),marketTradeGood);
                    }
                    int sumup=0;
                    for(ShipCargoItem shipCargoItem:ship.getCargo().getInventory())
                    {
                        if(tradeItems.containsKey(shipCargoItem.getSymbol()))
                        {
                            SellCargoRequest sellCargoRequest = new SellCargoRequest();
                            sellCargoRequest.setSymbol(shipCargoItem.getSymbol());
                            sellCargoRequest.setUnits(shipCargoItem.getUnits());
                            fleetApi.sellCargo(ship.getSymbol(),sellCargoRequest);
                            sumup+=shipCargoItem.getUnits()*tradeItems.get(shipCargoItem.getSymbol()).getSellPrice();
                        }
                    }
                    CLASS_LOGGER.debug("sold goods for a total of {} credits!",sumup);
                    update();
                }
                catch(java.lang.Throwable t)
                {
                    de.elbosso.util.Utilities.handleException(EXCEPTION_LOGGER,ShipPanel.this,t);
                }
            }
        };
    }
}
