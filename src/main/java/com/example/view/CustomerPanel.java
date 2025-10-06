package com.example.view;

import com.example.db.CustomerDao;
import com.example.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomerPanel extends JFrame {
    private final CustomerDao dao = new CustomerDao();
    private final DefaultTableModel customerModel = new DefaultTableModel(
            new Object[]{"ID", "Short Name","Full Name","City","Postal"}, 0
    );
    private final JTable customerTable = new JTable(customerModel);

    public CustomerPanel() {
        super("Customer Management");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new JScrollPane(customerTable), BorderLayout.CENTER);

        JPanel btns = new JPanel();
        JButton loadBtn = new JButton("Load");
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton addrBtn = new JButton("Manage Addresses");
        btns.add(loadBtn); btns.add(addBtn); btns.add(editBtn); btns.add(delBtn); btns.add(addrBtn);
        add(btns, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> loadCustomers());
        addBtn.addActionListener(e -> openCustomerDialog(null));
        editBtn.addActionListener(e -> editSelectedCustomer());
        delBtn.addActionListener(e -> deleteSelected());
        addrBtn.addActionListener(e -> manageAddresses());

        loadCustomers();
    }

    private void loadCustomers() {
        try {
            customerModel.setRowCount(0);
            List<Customer> list = dao.findAll();
            for (Customer c : list) {
                customerModel.addRow(new Object[]{c.getId(), c.getShortName(), c.getFullName(), c.getCity(), c.getPostalCode()});
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private void editSelectedCustomer() {
        int r = customerTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select a customer first"); return; }
        Long id = (Long) customerModel.getValueAt(r, 0);
        try { openCustomerDialog(dao.findById(id)); } catch (SQLException ex) { showError(ex); }
    }

    private void manageAddresses() {
        int r = customerTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select a customer first"); return; }
        Long id = (Long) customerModel.getValueAt(r, 0);
        try {
            Customer c = dao.findById(id);
            if (c != null) new AddressPanel(this, c, dao).setVisible(true);
            loadCustomers();
        } catch (SQLException ex) { showError(ex); }
    }

    private void openCustomerDialog(Customer c) {
        CustomerFormDialog dialog = new CustomerFormDialog(this, dao, c);
        dialog.setVisible(true);
        loadCustomers();
    }

    private void deleteSelected() {
        int r = customerTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select a customer first"); return; }
        Long id = (Long) customerModel.getValueAt(r, 0);
        int yes = JOptionPane.showConfirmDialog(this, "Delete selected customer?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (yes == JOptionPane.YES_OPTION) {
            try { dao.delete(id); loadCustomers(); } catch (SQLException ex) { showError(ex); }
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

