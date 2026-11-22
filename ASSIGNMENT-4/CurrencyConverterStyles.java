import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CurrencyConverterStyles extends JFrame implements ActionListener {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> from = new JComboBox<>(new String[]{"USD","EUR","INR"});
    private final JComboBox<String> to = new JComboBox<>(new String[]{"INR","USD","EUR"});
    private final JLabel result = new JLabel("Result: —");
    public CurrencyConverterStyles() {
        super("Currency Converter - implements ActionListener");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter(){ public void windowClosing(WindowEvent e){dispose();} });
        JPanel p=new JPanel(new FlowLayout()); p.add(amount); p.add(from); p.add(to);
        JButton convert=new JButton("Convert"); p.add(convert); add(p,BorderLayout.CENTER); add(result,BorderLayout.SOUTH);
        convert.addActionListener(this);
        amount.addActionListener(e -> convertAndDialog());
        setSize(430,140); setLocationRelativeTo(null);
    }
    public void actionPerformed(ActionEvent e) { convertAndDialog(); }
    private void convertAndDialog() {
        try {
            double v=Double.parseDouble(amount.getText().trim());
            if (!Double.isFinite(v) || v<0) throw new NumberFormatException();
            double usd = switch((String)from.getSelectedItem()) { case "EUR" -> v*1.08; case "INR" -> v/83.0; default -> v; };
            double out = switch((String)to.getSelectedItem()) { case "EUR" -> usd/1.08; case "INR" -> usd*83.0; default -> usd; };
            String msg=String.format("%.2f %s = %.2f %s",v,from.getSelectedItem(),out,to.getSelectedItem());
            result.setText(msg); JOptionPane.showMessageDialog(this,msg);
        } catch(NumberFormatException ex) {
            result.setText("Enter a valid non-negative amount.");
            JOptionPane.showMessageDialog(this,result.getText(),"Invalid amount",JOptionPane.ERROR_MESSAGE);
        }
    }
    // Alternative listener styles for the same action:
    private void namedInnerClassExample(JButton b) { b.addActionListener(new ConvertHandler()); }
    private class ConvertHandler implements ActionListener { public void actionPerformed(ActionEvent e){convertAndDialog();} }
    private void anonymousExample(JButton b) { b.addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e){convertAndDialog();} }); }
    private void lambdaExample(JButton b) { b.addActionListener(e -> convertAndDialog()); }
    public static void main(String[] a){SwingUtilities.invokeLater(()->new CurrencyConverterStyles().setVisible(true));}
}
