package de.elbosso.spacetraders.swing;

import de.elbosso.spacetraders.client.api.ContractsApi;
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

public class ContractPanel extends javax.swing.JPanel
{
    private static final org.slf4j.Logger CLASS_LOGGER = org.slf4j.LoggerFactory.getLogger(ContractPanel.class);
    private static final org.slf4j.Logger EXCEPTION_LOGGER = org.slf4j.LoggerFactory.getLogger("ExceptionCatcher");
    private final ApiClient defaultClient;
    private final ContractsApi contractsApi;
    private final FleetApi fleetApi;
    private final javax.swing.JToolBar tb;
    private Contract contract;
    private InterfaceFactory interfaceFactory = new InterfaceFactory();
    private javax.swing.Action deliverAction;
    private javax.swing.Action fulfillAction;

    public ContractPanel(ApiClient defaultClient, Contract contract) throws InterfaceFactoryException, ApiException
    {
        super(new BorderLayout());
        this.defaultClient = defaultClient;
        this.contract=contract;
        this.contractsApi = new ContractsApi(defaultClient);
        this.fleetApi=new FleetApi(defaultClient);
        tb = new javax.swing.JToolBar();
        tb.setFloatable(false);
        add(tb, BorderLayout.NORTH);
        add(interfaceFactory.fetchInterfaceForBean(contract, contract.getId()+" "+contract.getTerms().getDeadline()));
        createActions();
        manageActionState();
        tb.add(deliverAction);
        tb.add(fulfillAction);
    }
    private void update() throws InterfaceFactoryException, ApiException
    {
        removeAll();
        contract=contractsApi.getContract(contract.getId()).getData();
        add(tb, BorderLayout.NORTH);
        add(interfaceFactory.fetchInterfaceForBean(contract, contract.getId()+" "+contract.getTerms().getDeadline()));
        invalidate();
        validate();
        doLayout();
        repaint();
        manageActionState();
    }
    private void manageActionState() throws ApiException
    {
        boolean allThere=true;
        for(ContractDeliverGood contractDeliverGood:contract.getTerms().getDeliver())
        {
            if(contractDeliverGood.getUnitsFulfilled()<contractDeliverGood.getUnitsRequired())
            {
                allThere = false;
                break;
            }
        }
        deliverAction.setEnabled(contract.getAccepted()==true&& contract.getFulfilled()==false);
        fulfillAction.setEnabled(contract.getAccepted()==true&& contract.getFulfilled()==false&&allThere);
    }

    private void createActions()
    {
        deliverAction=new javax.swing.AbstractAction("deliver")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    GetMyShips200Response resp=fleetApi.getMyShips(1,20);
                    for(ContractDeliverGood contractDeliverGood:contract.getTerms().getDeliver())
                    {
                        for (Ship ship : resp.getData())
                        {
                            for (ShipCargoItem shipCargoItem : ship.getCargo().getInventory())
                            {
                                CLASS_LOGGER.debug("cargo {} contract {}",shipCargoItem.getSymbol(),contractDeliverGood.getTradeSymbol());
                                CLASS_LOGGER.debug("location {} destination {}",ship.getNav().getWaypointSymbol(),contractDeliverGood.getDestinationSymbol());
                                if((shipCargoItem.getSymbol().equals(contractDeliverGood.getTradeSymbol()))&(ship.getNav().getWaypointSymbol().equals(contractDeliverGood.getDestinationSymbol())))
                                {
                                    DeliverContractRequest deliverContractRequest = new DeliverContractRequest(); // DeliverContractRequest |
                                    deliverContractRequest.setShipSymbol(ship.getSymbol());
                                    deliverContractRequest.setTradeSymbol(contractDeliverGood.getTradeSymbol());
                                    deliverContractRequest.setUnits(shipCargoItem.getUnits());
                                    DeliverContract200Response result = contractsApi.deliverContract(contract.getId(), deliverContractRequest);
                                }
                            }
                        }

                    }
                    update();
                }
                catch(java.lang.Throwable t)
                {
                    de.elbosso.util.Utilities.handleException(EXCEPTION_LOGGER,ContractPanel.this,t);
                }
            }
        };
        fulfillAction=new javax.swing.AbstractAction("fulfill")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    FulfillContract200Response result = contractsApi.fulfillContract(contract.getId());
                    update();
                }
                catch(java.lang.Throwable t)
                {
                    de.elbosso.util.Utilities.handleException(EXCEPTION_LOGGER,ContractPanel.this,t);
                }
            }
        };
    }
}
