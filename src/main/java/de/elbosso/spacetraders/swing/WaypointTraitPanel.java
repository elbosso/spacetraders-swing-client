package de.elbosso.spacetraders.swing;

import de.elbosso.spacetraders.client.api.SystemsApi;
import de.elbosso.spacetraders.client.invoker.ApiClient;
import de.elbosso.spacetraders.client.invoker.ApiException;
import de.elbosso.spacetraders.client.model.*;
import de.netsysit.model.table.BeanListModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.beans.IntrospectionException;

public class WaypointTraitPanel extends javax.swing.JPanel implements javax.swing.event.ListSelectionListener
{
    private static final org.slf4j.Logger CLASS_LOGGER=org.slf4j.LoggerFactory.getLogger(SystemWindow.class);
    private static final org.slf4j.Logger EXCEPTION_LOGGER=org.slf4j.LoggerFactory.getLogger("ExceptionCatcher");
    private final ApiClient defaultClient;
    private final String systemId;
    private final de.elbosso.spacetraders.client.api.SystemsApi systemsApi;
    private javax.swing.JTable table;
    private javax.swing.Action showDetailAction;
    private java.util.List<Waypoint> waypoints;
    private de.netsysit.util.beans.InterfaceFactory interfaceFactory = new de.netsysit.util.beans.InterfaceFactory();
    private final WaypointType type;

    public WaypointTraitPanel(String systemId, ApiClient defaultClient, WaypointType type) throws HeadlessException, ApiException, IntrospectionException
    {
        super(new java.awt.BorderLayout());
        this.defaultClient = defaultClient;
        this.systemId = systemId;
        this.type=type;
        systemsApi = new SystemsApi(defaultClient);
        table = new javax.swing.JTable();
        add(new javax.swing.JScrollPane(table));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this);
        table.setDefaultRenderer(java.util.List.class,new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                java.awt.Component comp=super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if((comp!=null)&&(javax.swing.JLabel.class.isAssignableFrom(comp.getClass())))
                {
                    if(value!=null)
                    {
                        if(column==5)
                        {
                            java.lang.StringBuffer buf=new java.lang.StringBuffer();
                            for(WaypointTrait waypointTrait:waypoints.get(row).getTraits())
                            {
                                if(buf.length()>0)
                                    buf.append(", ");
                                buf.append(waypointTrait.getSymbol());
                            }
                            ((javax.swing.JLabel)comp).setText(buf.toString());
                        }
                    }
                }
                return comp;
            }
        });

        createActions();
        JToolBar tb = new JToolBar();
        add(tb, BorderLayout.NORTH);
        tb.setFloatable(false);
        tb.add(showDetailAction);
        update();
    }

    void update() throws ApiException, IntrospectionException
    {
        GetSystemWaypoints200Response wr = systemsApi.getSystemWaypoints(systemId, 1, 20);
        CLASS_LOGGER.debug("GetSystemWaypoints200Response {}", wr);
        waypoints = new java.util.LinkedList();
        for(Waypoint waypoint:wr.getData())
        {
            if((type==null)||(waypoint.getType()== type))
                waypoints.add(waypoint);
        }
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
                    de.netsysit.ui.dialog.GeneralPurposeInfoDialog.showComponentInDialog(WaypointTraitPanel.this, interfaceFactory.fetchInterfaceForBean(waypoint, waypoint.getSymbol() + "(" + waypoint.getType() + ")"));
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

    Waypoint getSelectedWaypoint()
    {
        return(table.getSelectedRowCount()>0?waypoints.get(table.getSelectedRow()):null);
    }

}
