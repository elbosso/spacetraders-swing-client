package de.elbosso.spacetraders.swing;

import de.elbosso.spacetraders.client.api.ContractsApi;
import de.elbosso.spacetraders.client.api.FleetApi;
import de.elbosso.spacetraders.client.api.SystemsApi;
import de.elbosso.spacetraders.client.invoker.ApiClient;
import de.elbosso.spacetraders.client.invoker.ApiException;
import de.elbosso.spacetraders.client.invoker.Configuration;
import de.elbosso.spacetraders.client.invoker.auth.*;
import de.elbosso.spacetraders.client.model.*;
import de.elbosso.spacetraders.client.api.AgentsApi;
import de.netsysit.model.table.BeanListModel;
import de.netsysit.util.beans.InterfaceFactory;
import de.netsysit.util.beans.InterfaceFactoryException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.IntrospectionException;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SpaceTraders extends javax.swing.JFrame
{
    private static final org.slf4j.Logger CLASS_LOGGER=org.slf4j.LoggerFactory.getLogger(SpaceTraders.class);
    private static final org.slf4j.Logger EXCEPTION_LOGGER=org.slf4j.LoggerFactory.getLogger("ExceptionCatcher");
    private ApiClient defaultClient = Configuration.getDefaultApiClient();
    private AgentsApi agentsApi;
    private Agent agent;
    private javax.swing.JToolBar tb=new javax.swing.JToolBar();
    private javax.swing.JPanel toplevel=new javax.swing.JPanel(new java.awt.BorderLayout());
    private javax.swing.Action homeAction;
    private javax.swing.Action fleetAction;
    private javax.swing.Action contractsAction;
    private javax.swing.JFrame homeWindow;
    private javax.swing.JFrame fleetWindow;
    private javax.swing.JFrame contractsWindow;
    private de.netsysit.util.beans.InterfaceFactory interfaceFactory=new de.netsysit.util.beans.InterfaceFactory();

    SpaceTraders() throws ApiException, InterfaceFactoryException, IOException
    {
        super("SwingSpaceTraders");
        defaultClient.setBasePath("https://api.spacetraders.io/v2");
        HttpBearerAuth AgentToken = (HttpBearerAuth) defaultClient.getAuthentication("AgentToken");
        java.io.File userHome=new java.io.File(java.lang.System.getProperty("user.home"));
        java.io.File authTokenFile=new java.io.File(userHome,"SpaceTraderToken.txt");
        java.io.FileInputStream fis=new java.io.FileInputStream(authTokenFile);
        java.lang.String tokenValue=de.elbosso.util.io.Utilities.readIntoString(fis);
        fis.close();
        AgentToken.setBearerToken(tokenValue);
        agentsApi = new AgentsApi(defaultClient);
        GetMyAgent200Response result = agentsApi.getMyAgent();
        CLASS_LOGGER.debug("GetMyAgent200Response {}",result);
        agent=result.getData();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(toplevel);
        createActions();
        tb.setFloatable(false);
        tb.add(homeAction);
        tb.add(fleetAction);
        tb.add(contractsAction);
        toplevel.add(tb, BorderLayout.NORTH);
        toplevel.add(interfaceFactory.fetchInterfaceForBean(agent,agent.getSymbol()));
        pack();
        setVisible(true);
    }
    private void createActions()
    {
        homeAction=new javax.swing.AbstractAction("home")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                java.lang.String sys=agent.getHeadquarters();
                sys=sys.substring(0,sys.lastIndexOf("-"));
                CLASS_LOGGER.debug(sys);
                if(homeWindow==null)
                {
                    try
                    {
                        homeWindow=SystemRegistry.getSharedInstance(defaultClient).getWindow(sys);
                        homeWindow.setTitle("Home ("+agent.getHeadquarters()+")");
                    }
                    catch(java.lang.Throwable t)
                    {
                        de.elbosso.util.Utilities.handleException(null,t);
                    }
                }
                if(homeWindow!=null)
                    homeWindow.setVisible(true);
            }
        };
        fleetAction=new javax.swing.AbstractAction("fleet")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(fleetWindow==null)
                {
                    try
                    {
                        fleetWindow = new FleetWindow("Fleet", defaultClient);
                    }
                    catch(java.lang.Throwable t)
                    {
                        de.elbosso.util.Utilities.handleException(null,t);
                    }
                }
                if(fleetWindow!=null)
                    fleetWindow.setVisible(true);
            }
        };
        contractsAction=new javax.swing.AbstractAction("contracts")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(contractsWindow==null)
                {
                    try
                    {
                        contractsWindow = new ContractsWindow("Contracts", defaultClient);
                    }
                    catch(java.lang.Throwable t)
                    {
                        de.elbosso.util.Utilities.handleException(null,t);
                    }
                }
                if(contractsWindow!=null)
                    contractsWindow.setVisible(true);
            }
        };

    }

    static class SystemRegistry
    {
        private final static java.util.Map<java.lang.String, SystemWindow> systems=new java.util.HashMap();
        private static ApiClient defaultClient;
        private static SystemRegistry sharedInstance;

        public static SystemRegistry getSharedInstance(ApiClient defaultClient)
        {
            if(sharedInstance==null)
                sharedInstance=new SystemRegistry(defaultClient);
            return sharedInstance;
        }

        private SystemRegistry(ApiClient defaultClient)
        {
            super();
            this.defaultClient = defaultClient;
        }
        public SystemWindow getWindow(String id) throws IntrospectionException, ApiException
        {
            if(systems.containsKey(id)==false)
            {
                SystemWindow sw=new SystemWindow(null,id,defaultClient);
                systems.put(id,sw);
            }
            return systems.get(id);
        }
    }
    static class FleetWindow extends javax.swing.JFrame implements javax.swing.event.ListSelectionListener
    {
        private final ApiClient defaultClient;
        private final de.elbosso.spacetraders.client.api.FleetApi fleetApi;
        private javax.swing.JPanel toplevel=new javax.swing.JPanel(new java.awt.BorderLayout());
        private javax.swing.JTable table;
        private javax.swing.Action showDetailAction;
        private java.util.List<Ship> ships;
        private de.netsysit.util.beans.InterfaceFactory interfaceFactory=new de.netsysit.util.beans.InterfaceFactory();

        public FleetWindow(String title, ApiClient defaultClient) throws HeadlessException, ApiException, IntrospectionException
        {
            super(title);
            this.defaultClient = defaultClient;
            fleetApi=new FleetApi(defaultClient);
            setDefaultCloseOperation(HIDE_ON_CLOSE);
            setContentPane(toplevel);
            table=new javax.swing.JTable();
            toplevel.add(new javax.swing.JScrollPane(table));
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getSelectionModel().addListSelectionListener(this);
            createActions();
            javax.swing.JToolBar tb=new javax.swing.JToolBar();
            tb.setFloatable(false);
            tb.add(showDetailAction);
            toplevel.add(tb, BorderLayout.NORTH);
            update();
            pack();
        }

        private void update() throws ApiException, IntrospectionException
        {
            GetMyShips200Response shipsr=fleetApi.getMyShips(1,20);
            ships=shipsr.getData();
            de.netsysit.model.table.BeanListModel<Ship> model=new BeanListModel(Ship.class,ships);
            table.setModel(model);
        }

        public void createActions()
        {
            showDetailAction =new javax.swing.AbstractAction("details")
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    Ship ship=ships.get(table.getSelectedRow());
                    try
                    {
                        de.netsysit.ui.dialog.GeneralPurposeInfoDialog.showComponentInDialog(FleetWindow.this, interfaceFactory.fetchInterfaceForBean(ship, ship.getRegistration().getName()));
                        update();
                    }
                    catch(java.lang.Throwable t)
                    {
                        de.elbosso.util.Utilities.handleException(null,t);
                    }
                }
            };
            showDetailAction.setEnabled(false);
        }

        @Override
        public void valueChanged(ListSelectionEvent e)
        {
            showDetailAction.setEnabled(table.getSelectedRowCount()>0);
        }
    }
    static class ContractsWindow extends javax.swing.JFrame implements javax.swing.event.ListSelectionListener
    {
        private final ApiClient defaultClient;
        private final de.elbosso.spacetraders.client.api.ContractsApi contractsApi;
        private javax.swing.JPanel toplevel=new javax.swing.JPanel(new java.awt.BorderLayout());
        private javax.swing.JTable table;
        private javax.swing.Action showDetailAction;
        private java.util.List<Contract> contracts;
        private de.netsysit.util.beans.InterfaceFactory interfaceFactory=new de.netsysit.util.beans.InterfaceFactory();

        public ContractsWindow(String title, ApiClient defaultClient) throws HeadlessException, ApiException, IntrospectionException
        {
            super(title);
            this.defaultClient = defaultClient;
            contractsApi=new ContractsApi(defaultClient);
            setDefaultCloseOperation(HIDE_ON_CLOSE);
            setContentPane(toplevel);
            table=new javax.swing.JTable();
            toplevel.add(new javax.swing.JScrollPane(table));
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getSelectionModel().addListSelectionListener(this);
            createActions();
            javax.swing.JToolBar tb=new javax.swing.JToolBar();
            tb.setFloatable(false);
            tb.add(showDetailAction);
            toplevel.add(tb, BorderLayout.NORTH);
            update();
            pack();
        }
        private void update() throws ApiException, IntrospectionException
        {
            GetContracts200Response cr=contractsApi.getContracts(1,20);
            CLASS_LOGGER.debug("GetContracts200Response {}",cr);
            contracts=cr.getData();
            de.netsysit.model.table.BeanListModel<Contract> model=new BeanListModel(Contract.class,contracts);
            table.setModel(model);
        }

        public void createActions()
        {
            showDetailAction =new javax.swing.AbstractAction("details")
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    Contract contract=contracts.get(table.getSelectedRow());
                    try
                    {
                        de.netsysit.ui.dialog.GeneralPurposeInfoDialog.showComponentInDialog(ContractsWindow.this, interfaceFactory.fetchInterfaceForBean(contract, contract.getId()));
                        update();
                    }
                    catch(java.lang.Throwable t)
                    {
                        de.elbosso.util.Utilities.handleException(null,t);
                    }
                }
            };
            showDetailAction.setEnabled(false);
        }

        @Override
        public void valueChanged(ListSelectionEvent e)
        {
            showDetailAction.setEnabled(table.getSelectedRowCount()>0);
        }
    }
    static class SystemWindow extends javax.swing.JFrame implements javax.swing.event.ListSelectionListener
    {
        private final ApiClient defaultClient;
        private final java.lang.String systemId;
        private final de.elbosso.spacetraders.client.api.SystemsApi systemsApi;
        private javax.swing.JPanel toplevel=new javax.swing.JPanel(new java.awt.BorderLayout());
        private javax.swing.JTable table;
        private javax.swing.Action showDetailAction;
        private java.util.List<Waypoint> waypoints;
        private de.netsysit.util.beans.InterfaceFactory interfaceFactory=new de.netsysit.util.beans.InterfaceFactory();

        public SystemWindow(String title, java.lang.String systemId,ApiClient defaultClient) throws HeadlessException, ApiException, IntrospectionException
        {
            super(title);
            this.defaultClient = defaultClient;
            this.systemId=systemId;
            systemsApi=new SystemsApi(defaultClient);
            setDefaultCloseOperation(HIDE_ON_CLOSE);
            setContentPane(toplevel);
            table=new javax.swing.JTable();
            toplevel.add(new javax.swing.JScrollPane(table));
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getSelectionModel().addListSelectionListener(this);
            createActions();
            javax.swing.JToolBar tb=new javax.swing.JToolBar();
            tb.setFloatable(false);
            tb.add(showDetailAction);
             toplevel.add(tb, BorderLayout.NORTH);
            update();
            pack();
        }
        private void update() throws ApiException, IntrospectionException
        {
            GetSystemWaypoints200Response wr=systemsApi.getSystemWaypoints(systemId,1,20);
            CLASS_LOGGER.debug("GetSystemWaypoints200Response {}",wr);
            waypoints=wr.getData();
            de.netsysit.model.table.BeanListModel<Waypoint> model=new BeanListModel(Waypoint.class,waypoints);
            table.setModel(model);
        }

        public void createActions()
        {
            showDetailAction =new javax.swing.AbstractAction("details")
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    Waypoint waypoint=waypoints.get(table.getSelectedRow());
                    try
                    {
                        de.netsysit.ui.dialog.GeneralPurposeInfoDialog.showComponentInDialog(SystemWindow.this, interfaceFactory.fetchInterfaceForBean(waypoint, waypoint.getSymbol()+"("+waypoint.getType()+")"));
                        update();
                    }
                    catch(java.lang.Throwable t)
                    {
                        de.elbosso.util.Utilities.handleException(null,t);
                    }
                }
            };
            showDetailAction.setEnabled(false);
        }

        @Override
        public void valueChanged(ListSelectionEvent e)
        {
            showDetailAction.setEnabled(table.getSelectedRowCount()>0);
        }
    }
    public static void main(String[] args) throws InterfaceFactoryException, ApiException, IOException
    {
        new SpaceTraders();
    }
    public static void main1(String[] args) throws InterfaceFactoryException
    {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://api.spacetraders.io/v2");

// Configure HTTP bearer authorization: AgentToken
        HttpBearerAuth AgentToken = (HttpBearerAuth) defaultClient.getAuthentication("AgentToken");
        AgentToken.setBearerToken("");

        AgentsApi apiInstance = new AgentsApi(defaultClient);
        de.elbosso.spacetraders.client.api.SystemsApi systemsApi=new SystemsApi(defaultClient);
        de.elbosso.spacetraders.client.api.ContractsApi contractsApi=new ContractsApi(defaultClient);
        de.elbosso.spacetraders.client.api.FleetApi fleetApi=new FleetApi(defaultClient);
        try
        {
            GetMyAgent200Response result = apiInstance.getMyAgent();
            CLASS_LOGGER.debug("GetMyAgent200Response {}",result);
            Agent agent=result.getData();

            java.lang.String sys=agent.getHeadquarters();
            sys=sys.substring(0,sys.lastIndexOf("-"));
            CLASS_LOGGER.debug(sys);
            GetSystem200Response system=systemsApi.getSystem(sys);
            CLASS_LOGGER.debug("GetSystem200Response {}",system);

            InterfaceFactory ifac=new InterfaceFactory();


            GetSystemWaypoints200Response wr=systemsApi.getSystemWaypoints(sys,1,20);
            CLASS_LOGGER.debug("GetSystemWaypoints200Response {}",wr);
            java.util.List<Waypoint> waypoints=wr.getData();
            for(Waypoint waypoint:waypoints)
            {
                if((waypoint.getOrbitals()!=null)&&(waypoint.getOrbitals().isEmpty()==false))
                {
                    de.netsysit.ui.dialog.GeneralPurposeInfoDialog.showComponentInDialog(null,ifac.fetchInterfaceForBean(waypoint,waypoint.getSymbol()));
                }
            }

            GetContracts200Response cr=contractsApi.getContracts(1,20);
            CLASS_LOGGER.debug("GetContracts200Response {}",cr);
            java.util.List<Contract> contracts=cr.getData();
            for(Contract contract:contracts)
            {
                de.netsysit.ui.dialog.GeneralPurposeInfoDialog.showComponentInDialog(null,ifac.fetchInterfaceForBean(contract,contract.getId()));
                if(contract.getAccepted().booleanValue()==false)
                {
                    contractsApi.acceptContract(contract.getId());
                }
            }

            GetMyShips200Response shipsr=fleetApi.getMyShips(1,20);
            java.util.List<Ship> ships=shipsr.getData();
            CLASS_LOGGER.debug("ships {}",ships);
            Ship miningDrone=null;
            for(Ship ship:ships)
            {
                if(ship.getRegistration().getRole()==ShipRole.EXCAVATOR)
                    miningDrone=ship;
            }
            if(miningDrone==null)
            {
                for(Waypoint waypoint:waypoints)
                {
                    java.util.List<WaypointTrait> traits=waypoint.getTraits();
                    java.lang.String shipyardWaypointSymbol=null;
                    for(WaypointTrait trait:traits)
                    {
                        if(trait.getSymbol()== WaypointTrait.SymbolEnum.SHIPYARD)
                        {
                            shipyardWaypointSymbol=waypoint.getSymbol();
                        }
                    }
                    if(shipyardWaypointSymbol!=null)
                    {
                        CLASS_LOGGER.debug(shipyardWaypointSymbol);
                        PurchaseShipRequest psr=new PurchaseShipRequest();
                        psr.shipType(ShipType.MINING_DRONE);
                        psr.waypointSymbol(shipyardWaypointSymbol);
                        PurchaseShip201Response psresp=fleetApi.purchaseShip(psr);
                        CLASS_LOGGER.debug("PurchaseShip201Response {}",psresp);
                        miningDrone=psresp.getData().getShip();
                        break;
                    }
                }
            }
            if(miningDrone.getNav().getStatus()==ShipNavStatus.DOCKED)
            {
//https://docs.spacetraders.io/quickstart/mine-asteroids
            }


/*            org.json.JSONObject system=new JSONObject(wr.toJson());
            de.elbosso.model.tree.json.JsonNode node=new JsonNode("json",system,null);
            javax.swing.tree.TreeModel model=new javax.swing.tree.DefaultTreeModel(node);
            de.netsysit.ui.dialog.GeneralPurposeInfoDialog.showComponentInDialog(null,new javax.swing.JScrollPane(new javax.swing.JTree(model)));
            GetContracts200Response con=contractsApi.getContracts(1,20);
            CLASS_LOGGER.debug(con);
            org.json.JSONObject contracts=new JSONObject(con.toJson());
            node=new JsonNode("json",contracts,null);
            model=new javax.swing.tree.DefaultTreeModel(node);
            de.netsysit.ui.dialog.GeneralPurposeInfoDialog.showComponentInDialog(null,new javax.swing.JScrollPane(new javax.swing.JTree(model)));
*/
        } catch (ApiException e)
        {
            EXCEPTION_LOGGER.error("Exception when calling AgentsApi#getMyAgent");
            EXCEPTION_LOGGER.error("Status code: " + e.getCode());
            EXCEPTION_LOGGER.error("Reason: " + e.getResponseBody());
            EXCEPTION_LOGGER.error("Response headers: " + e.getResponseHeaders());
            EXCEPTION_LOGGER.error(e.getMessage(),e);
        }
    }
}