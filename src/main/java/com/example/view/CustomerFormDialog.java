package com.example.view;

import com.example.db.CustomerDao;
import com.example.model.Customer;
import com.example.util.Validator;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class CustomerFormDialog extends JDialog {
    private final JTextField shortName = new JTextField();
    private final JTextField fullName = new JTextField();
    private final JTextField city = new JTextField();
    private final JTextField postal = new JTextField();

    public CustomerFormDialog(JFrame parent, CustomerDao dao, Customer c) {
        super(parent, true);
        setTitle(c == null ? "Add Customer" : "Edit Customer");
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(0,1));

        add(new JLabel("Short Name (max 50):")); add(shortName);
        add(new JLabel("Full Name (max 255):")); add(fullName);
        add(new JLabel("City (max 100):")); add(city);
        add(new JLabel("Postal (5-8 digits):")); add(postal);

        if (c != null) {
            shortName.setText(c.getShortName());
            fullName.setText(c.getFullName());
            city.setText(c.getCity());
            postal.setText(c.getPostalCode());
        }

        JButton okBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        JPanel panel = new JPanel(); panel.add(okBtn); panel.add(cancelBtn);
        add(panel);

        okBtn.addActionListener(e -> saveCustomer(dao, c));
        cancelBtn.addActionListener(e -> dispose());
    }

    private void saveCustomer(CustomerDao dao, Customer c) {
        String sShort = shortName.getText().trim();
        String sFull = fullName.getText().trim();
        String sCity = city.getText().trim();
        String sPostal = postal.getText().trim();

        if (!Validator.isValidShortName(sShort)) { showError(shortName, "Short Name required (max 50)"); return; }
        if (!Validator.isValidFullName(sFull)) { showError(fullName, "Full Name required (max 255)"); return; }
        if (!Validator.isValidCity(sCity)) { showError(city, "City max 100 chars"); return; }
        if (!Validator.isValidPostal(sPostal)) { showError(postal, "Postal invalid (5-8 digits)"); return; }

        try {
            if (c == null) dao.save(new Customer(null, sShort, sFull, sCity, sPostal));
            else { c.setShortName(sShort); c.setFullName(sFull); c.setCity(sCity); c.setPostalCode(sPostal); dao.save(c); }
            dispose();
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private void showError(JTextField field, String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
        field.requestFocus();
    }
}
