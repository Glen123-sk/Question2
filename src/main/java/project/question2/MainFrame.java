package project.question2;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MainFrame extends JFrame {
    private final JTextField customerNameField;
    private final JComboBox<String> productNameCombo;
    private final JTextField productPriceField;
    private final JTextField vatAmountField;
    private final JTextField commissionAmountField;
    private final JTextField totalSaleAmountField;

    public MainFrame() {
        super("Product Sales Management System");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 390);
        setLocationRelativeTo(null);
        setResizable(false);

        JLabel customerNameLabel = new JLabel("Customer Name:");
        JLabel productNameLabel = new JLabel("Product Name:");
        JLabel productPriceLabel = new JLabel("Product Price:");
        JLabel vatAmountLabel = new JLabel("VAT Amount:");
        JLabel commissionAmountLabel = new JLabel("Commission Amount:");
        JLabel totalSaleAmountLabel = new JLabel("Total Sale Amount:");

        customerNameField = new JTextField(20);

        String[] mattresses = {
            "Cozy Nights Monarch",
            "Restonic Barbados",
            "Sealy Phuket"
        };
        productNameCombo = new JComboBox<>(mattresses);
        productPriceField = new JTextField(20);
        vatAmountField = new JTextField(20);
        commissionAmountField = new JTextField(20);
        totalSaleAmountField = new JTextField(20);

        vatAmountField.setEditable(false);
        commissionAmountField.setEditable(false);
        totalSaleAmountField.setEditable(false);

        JButton processButton = new JButton("Process");
        JButton clearButton = new JButton("Clear");
        processButton.addActionListener(event -> processSale());
        clearButton.addActionListener(event -> clearForm());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 8, 6, 8);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        addRow(panel, constraints, 0, customerNameLabel, customerNameField);
        addRow(panel, constraints, 1, productNameLabel, productNameCombo);
        addRow(panel, constraints, 2, productPriceLabel, productPriceField);
        addRow(panel, constraints, 3, vatAmountLabel, vatAmountField);
        addRow(panel, constraints, 4, commissionAmountLabel, commissionAmountField);
        addRow(panel, constraints, 5, totalSaleAmountLabel, totalSaleAmountField);

        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridwidth = 1;
        panel.add(processButton, constraints);

        constraints.gridx = 1;
        panel.add(clearButton, constraints);

        setJMenuBar(createMenuBar());
        add(panel);
    }

    private void addRow(JPanel panel, GridBagConstraints constraints, int row,
                        JLabel label, java.awt.Component component) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 1;
        panel.add(label, constraints);

        constraints.gridx = 1;
        panel.add(component, constraints);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(event -> dispose());
        fileMenu.add(exitItem);

        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem processItem = new JMenuItem("Process");
        JMenuItem clearItem = new JMenuItem("Clear");
        processItem.addActionListener(event -> processSale());
        clearItem.addActionListener(event -> clearForm());
        toolsMenu.add(processItem);
        toolsMenu.add(clearItem);

        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        return menuBar;
    }

    private void processSale() {
        String customerName = customerNameField.getText().trim();
        String priceText = productPriceField.getText().trim();

        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Customer name cannot be empty.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Product price cannot be empty.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        double productPrice;
        try {
            productPrice = Double.parseDouble(priceText);
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this,
                    "Product price must be a valid number.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (productPrice < 0) {
            JOptionPane.showMessageDialog(this,
                    "Product price cannot be negative.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Double.isFinite(productPrice)) {
            JOptionPane.showMessageDialog(this,
                    "Product price must be a valid number.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        ProductCalculations calculations = new ProductCalculations(productPrice);
        double vatAmount = calculations.GetVat();
        double commissionAmount = calculations.GetCommission();
        double totalSaleAmount = calculations.GetTotal();

        vatAmountField.setText(formatMoney(vatAmount));
        commissionAmountField.setText(formatMoney(commissionAmount));
        totalSaleAmountField.setText(formatMoney(totalSaleAmount));

        String productName = (String) productNameCombo.getSelectedItem();
        if (saveSale(customerName, productName, productPrice,
                vatAmount, commissionAmount, totalSaleAmount)) {
            JOptionPane.showMessageDialog(this,
                    "Sale processed and saved successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean saveSale(String customerName, String productName,
                             double productPrice, double vatAmount,
                             double commissionAmount, double totalSaleAmount) {
        try (PrintWriter writer = new PrintWriter(
                new FileWriter("data.txt", true))) {
            writer.println("Customer Name: " + customerName);
            writer.println("Product Name: " + productName);
            writer.println("Product Price: " + formatMoney(productPrice));
            writer.println("VAT Amount: " + formatMoney(vatAmount));
            writer.println("Commission Amount: " + formatMoney(commissionAmount));
            writer.println("Total Sale Amount: " + formatMoney(totalSaleAmount));
            writer.println("------------------------------");
            return true;
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this,
                    "The sale could not be saved to data.txt.",
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void clearForm() {
        customerNameField.setText("");
        productNameCombo.setSelectedIndex(0);
        productPriceField.setText("");
        vatAmountField.setText("");
        commissionAmountField.setText("");
        totalSaleAmountField.setText("");
    }

    private String formatMoney(double amount) {
        return String.format(Locale.US, "%.2f", amount);
    }
}
