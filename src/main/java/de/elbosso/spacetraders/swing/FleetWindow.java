package de.elbosso.spacetraders.swing;

import de.elbosso.spacetraders.client.api.FleetApi;
import de.elbosso.spacetraders.client.invoker.ApiClient;
import de.elbosso.spacetraders.client.invoker.ApiException;
import de.elbosso.spacetraders.client.model.GetMyShips200Response;
import de.elbosso.spacetraders.client.model.Ship;
import de.elbosso.spacetraders.client.model.ShipRegistration;
import de.netsysit.model.table.BeanListModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.IntrospectionException;

class FleetWindow extends javax.swing.JFrame implements javax.swing.event.ListSelectionListener
{
    private static final org.slf4j.Logger CLASS_LOGGER=org.slf4j.LoggerFactory.getLogger(FleetWindow.class);
    private static final org.slf4j.Logger EXCEPTION_LOGGER=org.slf4j.LoggerFactory.getLogger("ExceptionCatcher");
    private final ApiClient defaultClient;
    private final de.elbosso.spacetraders.client.api.FleetApi fleetApi;
    private javax.swing.JPanel toplevel = new javax.swing.JPanel(new java.awt.BorderLayout());
    private javax.swing.JTable table;
    private javax.swing.Action showDetailAction;
    private java.util.List<Ship> ships;
    private de.netsysit.util.beans.InterfaceFactory interfaceFactory = new de.netsysit.util.beans.InterfaceFactory();

    public FleetWindow(String title, ApiClient defaultClient) throws HeadlessException, ApiException, IntrospectionException
    {
        super(title);
        this.defaultClient = defaultClient;
        fleetApi = new FleetApi(defaultClient);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setContentPane(toplevel);
        table = new javax.swing.JTable();
        toplevel.add(new javax.swing.JScrollPane(table));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this);
        table.setDefaultRenderer(ShipRegistration.class,new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                java.awt.Component comp=super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if((comp!=null)&&(javax.swing.JLabel.class.isAssignableFrom(comp.getClass())))
                {
                    if(value!=null)
                    {
                        ShipRegistration shipRegistration=(ShipRegistration)value;
                        ((javax.swing.JLabel) comp).setText(shipRegistration.getName()+" "+shipRegistration.getRole());
                    }
                }
                return comp;
            }
        });
        createActions();
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.add(showDetailAction);
        toplevel.add(tb, BorderLayout.NORTH);
        update();
        pack();
    }

    void update() throws ApiException, IntrospectionException
    {
        GetMyShips200Response shipsr = fleetApi.getMyShips(1, 20);
        ships = shipsr.getData();
        de.netsysit.model.table.BeanListModel<Ship> model = new BeanListModel(Ship.class, ships);
        table.setModel(model);
    }

    public void createActions()
    {
        showDetailAction = new AbstractAction("details")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Ship ship = ships.get(table.getSelectedRow());
                try
                {
                    ShipRegistry.getSharedInstance(defaultClient).getWindow(ship.getSymbol()).setVisible(true);
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
