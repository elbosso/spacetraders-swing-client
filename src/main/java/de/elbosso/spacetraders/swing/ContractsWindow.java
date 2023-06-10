package de.elbosso.spacetraders.swing;

import de.elbosso.spacetraders.client.api.ContractsApi;
import de.elbosso.spacetraders.client.invoker.ApiClient;
import de.elbosso.spacetraders.client.invoker.ApiException;
import de.elbosso.spacetraders.client.model.Contract;
import de.elbosso.spacetraders.client.model.GetContracts200Response;
import de.netsysit.model.table.BeanListModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.IntrospectionException;

class ContractsWindow extends javax.swing.JFrame implements javax.swing.event.ListSelectionListener
{
    private static final org.slf4j.Logger CLASS_LOGGER=org.slf4j.LoggerFactory.getLogger(ContractsWindow.class);
    private static final org.slf4j.Logger EXCEPTION_LOGGER=org.slf4j.LoggerFactory.getLogger("ExceptionCatcher");
    private final ApiClient defaultClient;
    private final de.elbosso.spacetraders.client.api.ContractsApi contractsApi;
    private javax.swing.JPanel toplevel = new javax.swing.JPanel(new java.awt.BorderLayout());
    private javax.swing.JTable table;
    private javax.swing.Action showDetailAction;
    private java.util.List<Contract> contracts;
    private de.netsysit.util.beans.InterfaceFactory interfaceFactory = new de.netsysit.util.beans.InterfaceFactory();

    public ContractsWindow(String title, ApiClient defaultClient) throws HeadlessException, ApiException, IntrospectionException
    {
        super(title);
        this.defaultClient = defaultClient;
        contractsApi = new ContractsApi(defaultClient);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setContentPane(toplevel);
        table = new javax.swing.JTable();
        toplevel.add(new javax.swing.JScrollPane(table));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this);
        createActions();
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.add(showDetailAction);
        toplevel.add(tb, BorderLayout.NORTH);
        update();
        pack();
    }

    private void update() throws ApiException, IntrospectionException
    {
        GetContracts200Response cr = contractsApi.getContracts(1, 20);
        CLASS_LOGGER.debug("GetContracts200Response {}", cr);
        contracts = cr.getData();
        de.netsysit.model.table.BeanListModel<Contract> model = new BeanListModel(Contract.class, contracts);
        table.setModel(model);
    }

    public void createActions()
    {
        showDetailAction = new AbstractAction("details")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Contract contract = contracts.get(table.getSelectedRow());
                try
                {
                    de.netsysit.ui.dialog.GeneralPurposeInfoDialog.showComponentInDialog(ContractsWindow.this, new ContractPanel(defaultClient,contract));
                    update();
                } catch (Throwable t)
                {
                    de.elbosso.util.Utilities.handleException(null, t);
                }
            }
        };
        showDetailAction.setEnabled(false);
    }

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        showDetailAction.setEnabled(table.getSelectedRowCount() > 0);
    }
}
