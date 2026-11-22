import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class DrawingPad extends JFrame {
    private final List<Point> points = new ArrayList<>();
    private final JLabel counter = new JLabel("Clicks: 0");
    private final JPanel canvas = new JPanel() {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLUE);
            for (Point p : points) g.fillOval(p.x-4,p.y-4,8,8);
        }
    };
    public DrawingPad() {
        super("Mouse Drawing Pad");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                points.add(e.getPoint());
                counter.setText("Clicks: " + points.size());
                canvas.repaint();
            }
        });
        add(canvas,BorderLayout.CENTER); add(counter,BorderLayout.SOUTH);
        setSize(600,400); setLocationRelativeTo(null);
    }
    public static void main(String[] a) { SwingUtilities.invokeLater(() -> new DrawingPad().setVisible(true)); }
}
