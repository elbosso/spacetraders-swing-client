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

class SystemWindow extends javax.swing.JFrame
{
    private static final org.slf4j.Logger CLASS_LOGGER=org.slf4j.LoggerFactory.getLogger(SystemWindow.class);
    private static final org.slf4j.Logger EXCEPTION_LOGGER=org.slf4j.LoggerFactory.getLogger("ExceptionCatcher");
    private final ApiClient defaultClient;
    private final String systemId;
    private final de.elbosso.spacetraders.client.api.SystemsApi systemsApi;
    private javax.swing.JPanel toplevel = new javax.swing.JPanel(new java.awt.BorderLayout());
    private final WaypointTraitPanel waypointTraitPanel;

    public SystemWindow(String title, String systemId, ApiClient defaultClient) throws HeadlessException, ApiException, IntrospectionException
    {
        super(title);
        this.defaultClient = defaultClient;
        this.systemId = systemId;
        systemsApi = new SystemsApi(defaultClient);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setContentPane(toplevel);
        waypointTraitPanel=new WaypointTraitPanel(systemId,defaultClient,null);
        toplevel.add(waypointTraitPanel);
        update();
        pack();
    }

    private void update() throws ApiException, IntrospectionException
    {
        waypointTraitPanel.update();
    }

}
