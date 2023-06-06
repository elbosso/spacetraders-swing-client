package de.elbosso.spacetraders.swing;

import de.elbosso.spacetraders.client.api.SystemsApi;
import de.elbosso.spacetraders.client.invoker.ApiClient;
import de.elbosso.spacetraders.client.invoker.ApiException;
import de.elbosso.spacetraders.client.model.GetSystemWaypoints200Response;
import de.elbosso.spacetraders.client.model.Waypoint;
import de.netsysit.model.table.BeanListModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.IntrospectionException;

class SystemWindow extends javax.swing.JFrame implements javax.swing.event.ListSelectionListener
{
    private static final org.slf4j.Logger CLASS_LOGGER=org.slf4j.LoggerFactory.getLogger(SystemWindow.class);
    private static final org.slf4j.Logger EXCEPTION_LOGGER=org.slf4j.LoggerFactory.getLogger("ExceptionCatcher");
    private final ApiClient defaultClient;
    private final String systemId;
    private final de.elbosso.spacetraders.client.api.SystemsApi systemsApi;
    private javax.swing.JPanel toplevel = new javax.swing.JPanel(new java.awt.BorderLayout());
    private javax.swing.JTable table;
    private javax.swing.Action showDetailAction;
    private java.util.List<Waypoint> waypoints;
    private de.netsysit.util.beans.InterfaceFactory interfaceFactory = new de.netsysit.util.beans.InterfaceFactory();

    public SystemWindow(String title, String systemId, ApiClient defaultClient) throws HeadlessException, ApiException, IntrospectionException
    {
        super(title);
        this.defaultClient = defaultClient;
        this.systemId = systemId;
        systemsApi = new SystemsApi(defaultClient);
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
        GetSystemWaypoints200Response wr = systemsApi.getSystemWaypoints(systemId, 1, 20);
        CLASS_LOGGER.debug("GetSystemWaypoints200Response {}", wr);
        waypoints = wr.getData();
        de.netsysit.model.table.BeanListModel<Waypoint> model = new BeanListModel(Waypoint.class, waypoints);
        table.setModel(model);
    }

    public void createActions()
    {
        showDetailAction = new AbstractAction("details")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Waypoint waypoint = waypoints.get(table.getSelectedRow());
                try
                {
                    de.netsysit.ui.dialog.GeneralPurposeInfoDialog.showComponentInDialog(SystemWindow.this, interfaceFactory.fetchInterfaceForBean(waypoint, waypoint.getSymbol() + "(" + waypoint.getType() + ")"));
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