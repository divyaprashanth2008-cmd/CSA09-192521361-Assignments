import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class FeePaymentApp extends JFrame {
    private final JTextField studentId = new JTextField(14);
    private final JTextField amount = new JTextField(10);
    private final JLabel status = new JLabel("Enter details and choose Pay.");
    private final FeeService service = new FeeService();

    public FeePaymentApp() {
        super("Student Fee Payment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Student ID:")); form.add(studentId);
        form.add(new JLabel("Amount:")); form.add(amount);
        JButton pay = new JButton("Pay");
        JButton receipt = new JButton("Receipt");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(pay); actions.add(receipt);
        add(form, BorderLayout.CENTER); add(actions, BorderLayout.SOUTH);
        add(status, BorderLayout.NORTH);
        pay.addActionListener(e -> processPayment());
        receipt.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Receipt is available after a successful payment."));
        setSize(420, 190); setLocationRelativeTo(null);
    }

    private void processPayment() {
        try {
            FeeService.PaymentResult result =
                service.pay(studentId.getText().trim(), new BigDecimal(amount.getText().trim()));
            status.setText(result.message());
            JOptionPane.showMessageDialog(this, result.message(), "Payment",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            status.setText(ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid input",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FeePaymentApp().setVisible(true));
    }
}
class FeeService {
    record PaymentResult(boolean success, String receiptId, String message) {}
    PaymentResult pay(String studentId, BigDecimal amount) {
        if (studentId == null || studentId.isBlank())
            throw new IllegalArgumentException("Student ID is required.");
        if (amount == null || amount.signum() <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero.");
        // Replace this demonstration result with a parameterised JDBC transaction.
        String receipt = "FEE-" + System.currentTimeMillis();
        return new PaymentResult(true, receipt, "Payment accepted. Receipt: " + receipt);
    }
}
