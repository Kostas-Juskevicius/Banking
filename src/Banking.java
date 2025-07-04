import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

public class Banking extends JFrame {
    private static final String DATE_PATTERN = "yyyy-MM-dd_HH-mm-ss";
    private final List<BankAccount> accounts = new ArrayList<>();

    private final JTextArea displayArea = new JTextArea();
    private final JComboBox<String> accountSelector = new JComboBox<>();
    private final JTextField amountField = new JTextField(10);

    public Banking() {
        setTitle("Banking Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
        refreshAccountSelectors();
    }

    public void run() {
        setVisible(true);
    }

    private void initComponents() {
        displayArea.setEditable(false);
        displayArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        displayArea.setBackground(Color.BLACK);
        displayArea.setForeground(Color.GREEN);
        displayArea.setText("=== BANKING APPLICATION ===\n");

        amountField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addButton(buttonPanel, "Register Account", e -> registerAccount());
        addButton(buttonPanel, "Check Balance", e -> checkBalance());
        addButton(buttonPanel, "Deposit", e -> deposit());
        addButton(buttonPanel, "Withdraw", e -> withdraw());
        addButton(buttonPanel, "Transfer", e -> openTransferDialog());
        addButton(buttonPanel, "Download Data", e -> downloadData());
        addButton(buttonPanel, "Save Log", e -> saveLog());
        addButton(buttonPanel, "Exit", e -> System.exit(0));

        JPanel ioPanel = new JPanel(new BorderLayout(10, 10));
        ioPanel.setBorder(BorderFactory.createTitledBorder("Operations"));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Select Account:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(accountSelector, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        inputPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        inputPanel.add(amountField, gbc);

        JScrollPane outputScroll = new JScrollPane(displayArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Output"));
        outputScroll.setPreferredSize(new Dimension(0, 200));

        ioPanel.add(inputPanel, BorderLayout.NORTH);
        ioPanel.add(outputScroll, BorderLayout.CENTER);

        add(buttonPanel, BorderLayout.NORTH);
        add(ioPanel, BorderLayout.CENTER);
    }

    private void addButton(JPanel panel, String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        panel.add(btn);
    }

    private void registerAccount() {
        accounts.add(new BankAccount());
        displayArea.append("Account #" + accounts.size() + " registered successfully.\n");
        refreshAccountSelectors();
    }

    private void checkBalance() {
        runIfSelected(idx -> {
            BankAccount acct = accounts.get(idx);
            displayArea.append(String.format("Account #%d balance: $%s\n", idx + 1, acct));
        });
    }

    private void deposit() {
        performAmountOp("deposit", BankAccount::deposit);
    }

    private void withdraw() {
        performAmountOp("withdraw", BankAccount::withdraw);
    }

    private void performAmountOp(String opName, BiConsumer<BankAccount, BigDecimal> operation) {
        runIfSelected(idx -> {
            try {
                String text = amountField.getText().trim();
                if (text.isEmpty()) throw new IllegalArgumentException("Please enter an amount.");
                BigDecimal amt = new BigDecimal(text);
                BankAccount acct = accounts.get(idx);
                displayArea.append(String.format("Before %s: $%s\n", opName, acct));
                operation.accept(acct, amt);
                displayArea.append(String.format("After %s: $%s\n", opName, acct));
                amountField.setText("");
            } catch (NumberFormatException ex) {
                displayArea.append("ERROR: Invalid amount format.\n");
            } catch (IllegalArgumentException ex) {
                displayArea.append("ERROR: " + ex.getMessage() + "\n");
            }
        });
    }

    private void runIfSelected(java.util.function.Consumer<Integer> action) {
        int idx = accountSelector.getSelectedIndex() - 1;
        if (idx < 0) {
            displayArea.append("ERROR: Please select an account first.\n");
        } else {
            action.accept(idx);
        }
    }

    private void openTransferDialog() {
        if (accounts.size() < 2) {
            displayArea.append("ERROR: Need at least 2 accounts to transfer.\n");
            return;
        }

        JDialog dlg = new JDialog(this, "Transfer Money", true);
        dlg.setSize(400, 200);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JComboBox<String> fromSel = new JComboBox<>();
        JComboBox<String> toSel = new JComboBox<>();
        refreshSelector(fromSel, "-- Select From --");
        refreshSelector(toSel, "-- Select To --");

        JTextField amtField = new JTextField(10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        dlg.add(new JLabel("From:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dlg.add(fromSel, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        dlg.add(new JLabel("To:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dlg.add(toSel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dlg.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        dlg.add(amtField, gbc);

        JPanel btns = new JPanel(new FlowLayout());
        JButton tr = new JButton("Transfer");
        JButton cn = new JButton("Cancel");

        tr.addActionListener(e -> {
            int fromIdx = fromSel.getSelectedIndex() - 1;
            int toIdx = toSel.getSelectedIndex() - 1;
            if (fromIdx < 0 || toIdx < 0 || fromIdx == toIdx) {
                JOptionPane.showMessageDialog(dlg, "Invalid account selection.");
                return;
            }
            try {
                BigDecimal amt = new BigDecimal(amtField.getText().trim());
                BankAccount src = accounts.get(fromIdx);
                BankAccount dst = accounts.get(toIdx);
                displayArea.append(String.format("Before transfer:\n  From #%d: $%s\n  To   #%d: $%s\n",
                        fromIdx+1, src, toIdx+1, dst));
                src.transfer(dst, amt);
                displayArea.append(String.format("After  transfer of $%s:\n  From #%d: $%s\n  To   #%d: $%s\n",
                        amt, fromIdx+1, src, toIdx+1, dst));
                dlg.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "ERROR: " + ex.getMessage());
            }
        });

        cn.addActionListener(e -> dlg.dispose());
        btns.add(tr); btns.add(cn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        dlg.add(btns, gbc);

        dlg.setVisible(true);
    }

    private void refreshAccountSelectors() {
        refreshSelector(accountSelector, "-- Select Account --");
    }

    private void refreshSelector(JComboBox<String> combo, String prompt) {
        combo.removeAllItems();
        combo.addItem(prompt);
        for (int i = 0; i < accounts.size(); i++) {
            combo.addItem("Account #" + (i + 1));
        }
    }

    private void downloadData() {
        if (accounts.isEmpty()) {
            displayArea.append("No accounts to download.\n");
            return;
        }
        try (FileWriter fw = openTimestampedFile("banking_data_")) {
            fw.write("=== BANKING DATA EXPORT ===\n");
            fw.write("Export Date: " + new Date() + "\n\n");
            BigDecimal total = BigDecimal.ZERO;
            for (int i = 0; i < accounts.size(); i++) {
                BankAccount a = accounts.get(i);
                fw.write(String.format("Account #%d Balance: $%s\n", i+1, a));
                total = total.add(a.getBalance());
            }
            fw.write("\nTotal Accounts: " + accounts.size() + "\n");
            fw.write("Total Balance: $" + total + "\n");
            displayArea.append("Banking data exported.\n");
        } catch (IOException ex) {
            displayArea.append("ERROR: Failed to export data - " + ex.getMessage() + "\n");
        }
    }

    private void saveLog() {
        try (FileWriter fw = openTimestampedFile("banking_log_")) {
            fw.write("=== BANKING APPLICATION LOG ===\n");
            fw.write("Log Date: " + new Date() + "\n\n");
            fw.write(displayArea.getText());
            displayArea.append("Application log saved.\n");
        } catch (IOException ex) {
            displayArea.append("ERROR: Failed to save log - " + ex.getMessage() + "\n");
        }
    }

    private FileWriter openTimestampedFile(String prefix) throws IOException {
        String stamp = new SimpleDateFormat(DATE_PATTERN).format(new Date());
        return new FileWriter(prefix + stamp + ".txt");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Banking().run());
    }
}