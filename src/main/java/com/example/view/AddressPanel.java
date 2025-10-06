package com.example.view;

import com.example.db.CustomerDao;
import com.example.model.Address;
import com.example.model.Customer;
import com.example.util.Validator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AddressPanel extends JDialog {
    private final Customer customer;
    private final CustomerDao dao;
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Line1", "Line2", "Line3"}, 0);
    private final JTable table = new JTable(model);

    public AddressPanel(Frame owner, Customer customer, CustomerDao dao) throws SQLException {
        super(owner, "Addresses for " + customer.getShortName(), true);
        this.customer = customer;
        this.dao = dao;
        setSize(700, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btns = new JPanel();
        JButton load = new JButton("Load");
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton del = new JButton("Delete");
        btns.add(load); btns.add(add); btns.add(edit); btns.add(del);
        add(btns, BorderLayout.SOUTH);

        load.addActionListener(e -> { try { loadAddresses(); } catch (SQLException ex) { showError(ex); } });
        add.addActionListener(e -> openAddressDialog(null));
        edit.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) { JOptionPane.showMessageDialog(this, "Select address first"); return; }
            Long id = (Long) model.getValueAt(r, 0);
            try {
                Address target = dao.findAddresses(customer.getId()).stream().filter(a -> a.getId().equals(id)).findFirst().orElse(null);
                if (target != null) openAddressDialog(target);
            } catch (SQLException ex) { showError(ex); }
        });
        del.addActionListener(e -> deleteAddress());

        loadAddresses();
    }

    private void loadAddresses() throws SQLException {
        model.setRowCount(0);
        List<Address> list = dao.findAddresses(customer.getId());
        for (Address a : list) {
            model.addRow(new Object[]{a.getId(), a.getLine1(), a.getLine2(), a.getLine3()});
        }
    }

    private void openAddressDialog(Address address) {
        try {
            int count = dao.countAddresses(customer.getId());
            if (address == null && count >= 3) {
                JOptionPane.showMessageDialog(this, "Cannot add more than 3 addresses for a customer.");
                return;
            }

            JDialog dialog = new JDialog(this, address == null ? "Add Address" : "Edit Address", true);
            JPanel panel = new JPanel(new GridLayout(0,1));

            JTextField l1 = new JTextField(address == null ? "" : address.getLine1());
            JTextField l2 = new JTextField(address == null ? "" : address.getLine2());
            JTextField l3 = new JTextField(address == null ? "" : address.getLine3());

            panel.add(new JLabel("Line1 (required, max 80 chars):")); panel.add(l1);
            panel.add(new JLabel("Line2 (optional, max 80 chars):")); panel.add(l2);
            panel.add(new JLabel("Line3 (optional, max 80 chars):")); panel.add(l3);

            JButton save = new JButton("Save");
            JButton cancel = new JButton("Cancel");
            JPanel btnPanel = new JPanel();
            btnPanel.add(save);
            btnPanel.add(cancel);
            panel.add(btnPanel);

            save.addActionListener(e -> {
                String line1 = l1.getText().trim();
                String line2 = l2.getText().trim();
                String line3 = l3.getText().trim();

                if (!Validator.isValidAddressLine(line1)) {
                    JOptionPane.showMessageDialog(dialog, "Line1 is required and must be <= 80 characters.");
                    return;
                }
                if (!Validator.isValidOptionalAddressLine(line2) || !Validator.isValidOptionalAddressLine(line3)) {
                    JOptionPane.showMessageDialog(dialog, "Line2 and Line3 must be <= 80 characters.");
                    return;
                }

                try {
                    if (address == null) {
                        dao.saveAddress(new Address(null, customer.getId(), line1, line2, line3));
                    } else {
                        address.setLine1(line1);
                        address.setLine2(line2);
                        address.setLine3(line3);
                        dao.saveAddress(address);
                    }
                    loadAddresses();
                    dialog.dispose();
                } catch (SQLException ex) {
                    showError(ex);
                }
            });

            cancel.addActionListener(e -> dialog.dispose());

            dialog.add(panel);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void deleteAddress() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select address first"); return; }
        Long id = (Long) model.getValueAt(r, 0);
        int yes = JOptionPane.showConfirmDialog(this, "Delete selected address?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (yes == JOptionPane.YES_OPTION) {
            try {
                dao.deleteAddress(id);
                loadAddresses();
            } catch (SQLException ex) { showError(ex); }
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}
