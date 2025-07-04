import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Banking extends JFrame {
    private static final List<BankAccount> accounts = new ArrayList<>();

    private static JTextArea displayArea;
    private static JComboBox<String> accountSelector;
    private static JTextField amountField;

    public void run() {
        this.setVisible(true);
    }

    public Banking() {
        setTitle("Banking Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        createComponents();
        layoutComponents();
        updateAccountSelector();
    }

    private void createComponents() {
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        displayArea.setBackground(Color.BLACK);
        displayArea.setForeground(Color.GREEN);
        displayArea.setText("=== BANKING APPLICATION ===\n");

        accountSelector = new JComboBox<>();
        amountField = new JTextField();
        amountField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    }

    private void layoutComponents() {
        this.setLayout(new BorderLayout(10, 10));

        // Top panel - buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buttonPanel.add(createButton("Register Account", e -> registerAccount()));
        buttonPanel.add(createButton("Check Balance", e -> checkBalance()));
        buttonPanel.add(createButton("Deposit", e -> deposit()));
        buttonPanel.add(createButton("Withdraw", e -> withdraw()));
        buttonPanel.add(createButton("Exit", e -> System.exit(0)));

        // Middle panel - inputs
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Account Operations"));
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

        // Bottom panel - display
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Output"));
        scrollPane.setPreferredSize(new Dimension(0, 200));

        this.add(buttonPanel, BorderLayout.NORTH);
        this.add(inputPanel, BorderLayout.CENTER);
        this.add(scrollPane, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        return button;
    }

    private static void registerAccount() {
        accounts.add(new BankAccount());
        displayArea.append("Account #" + accounts.size() + " registered successfully.\n");
        updateAccountSelector();
    }

    private static void checkBalance() {
        int selectedIndex = accountSelector.getSelectedIndex();
        if (selectedIndex <= 0) {
            displayArea.append("ERROR: Please select an account first.\n");
            return;
        }

        BankAccount account = accounts.get(selectedIndex - 1);
        displayArea.append("Account #" + selectedIndex + " balance: $" + account.toString() + "\n");
    }

    private static void deposit() {
        int selectedIndex = accountSelector.getSelectedIndex();
        if (selectedIndex <= 0) {
            displayArea.append("ERROR: Please select an account first.\n");
            return;
        }

        try {
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                displayArea.append("ERROR: Please enter an amount.\n");
                return;
            }

            BigDecimal amount = new BigDecimal(amountText);
            BankAccount account = accounts.get(selectedIndex - 1);

            displayArea.append("Before deposit: $" + account.toString() + "\n");
            account.deposit(amount);
            displayArea.append("After deposit: $" + account.toString() + "\n");
            amountField.setText("");

        } catch (NumberFormatException ex) {
            displayArea.append("ERROR: Invalid amount format.\n");
        } catch (IllegalArgumentException ex) {
            displayArea.append("ERROR: " + ex.getMessage() + "\n");
        }
    }

    private static void withdraw() {
        int selectedIndex = accountSelector.getSelectedIndex();
        if (selectedIndex <= 0) {
            displayArea.append("ERROR: Please select an account first.\n");
            return;
        }

        try {
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                displayArea.append("ERROR: Please enter an amount.\n");
                return;
            }

            BigDecimal amount = new BigDecimal(amountText);
            BankAccount account = accounts.get(selectedIndex - 1);

            displayArea.append("Before withdrawal: $" + account.toString() + "\n");
            account.withdraw(amount);
            displayArea.append("After withdrawal: $" + account.toString() + "\n");
            amountField.setText("");

        } catch (NumberFormatException ex) {
            displayArea.append("ERROR: Invalid amount format.\n");
        } catch (IllegalArgumentException ex) {
            displayArea.append("ERROR: " + ex.getMessage() + "\n");
        }
    }

    private static void updateAccountSelector() {
        accountSelector.removeAllItems();
        accountSelector.addItem("-- Select Account --");

        for (int i = 0; i < accounts.size(); i++) {
            accountSelector.addItem("Account #" + (i + 1));
        }
    }
}