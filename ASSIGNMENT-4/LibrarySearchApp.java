import javax.swing.*;
import java.awt.*;

public class LibrarySearchApp extends JFrame {
    private final JTextField search = new JTextField(22);
    private final JComboBox<String> category = new JComboBox<>(new String[]{"All","Books","Journals","Theses"});
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> results = new JList<>(model);
    private final JLabel status = new JLabel("Ready");

    public LibrarySearchApp() {
        super("Campus Library Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel title = new JLabel("Campus Library", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        JPanel north = new JPanel(new BorderLayout(5,5));
        north.add(title,BorderLayout.NORTH);
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchRow.add(new JLabel("Search")); searchRow.add(search);
        searchRow.add(category);
        JButton find = new JButton("Search"); searchRow.add(find);
        north.add(searchRow,BorderLayout.SOUTH);
        results.setVisibleRowCount(10);
        JPanel center = new JPanel(new BorderLayout()); center.add(new JScrollPane(results),BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prev = new JButton("Prev"), next = new JButton("Next"), reserve = new JButton("Reserve");
        bottom.add(prev); bottom.add(next); bottom.add(reserve);
        add(north,BorderLayout.NORTH); add(center,BorderLayout.CENTER);
        add(bottom,BorderLayout.SOUTH); add(status,BorderLayout.PAGE_END);
        find.addActionListener(e -> {
            model.clear();
            if (search.getText().isBlank()) { status.setText("Enter a search term."); return; }
            model.addElement("Example result for: " + search.getText().trim());
            model.addElement("Additional matching catalogue item");
            status.setText(model.size() + " sample results");
        });
        reserve.addActionListener(e -> status.setText(results.getSelectedValue()==null ?
                "Select a result first." : "Reservation request: " + results.getSelectedValue()));
        setSize(650,430); setLocationRelativeTo(null);
    }
    public static void main(String[] a) { SwingUtilities.invokeLater(() -> new LibrarySearchApp().setVisible(true)); }
}
