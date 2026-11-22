import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.concurrent.ExecutionException;

public class CampusEventRegistrationApp extends JFrame {
    private final JTextField studentId=new JTextField(12), studentName=new JTextField(14), email=new JTextField(18);
    private final JComboBox<String> event=new JComboBox<>(new String[]{"Tech Fest","Cultural Day","Sports Meet"});
    private final DefaultTableModel model=new DefaultTableModel(new String[]{"ID","Name","Email","Event"},0);
    private final JTable table=new JTable(model);
    public CampusEventRegistrationApp() {
        super("Campus Event Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JMenuBar bar=new JMenuBar(); JMenu file=new JMenu("File"); JMenuItem exit=new JMenuItem("Exit");
        exit.addActionListener(e->dispose()); file.add(exit); bar.add(file); setJMenuBar(bar);
        JPanel form=new JPanel(new GridLayout(0,2,6,6));
        form.add(new JLabel("Student ID"));form.add(studentId);form.add(new JLabel("Name"));form.add(studentName);
        form.add(new JLabel("Email"));form.add(email);form.add(new JLabel("Event"));form.add(event);
        JButton register=new JButton("Register"), refresh=new JButton("Filter / Refresh");
        JPanel actions=new JPanel();actions.add(register);actions.add(refresh);
        add(form,BorderLayout.NORTH);add(new JScrollPane(table),BorderLayout.CENTER);add(actions,BorderLayout.SOUTH);
        register.addActionListener(e->registerAsync()); refresh.addActionListener(e->loadAsync());
        setSize(720,440);setLocationRelativeTo(null);
    }
    private void registerAsync() {
        String id=studentId.getText().trim(), name=studentName.getText().trim(), mail=email.getText().trim(), ev=(String)event.getSelectedItem();
        if(id.isBlank()||name.isBlank()||!mail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            JOptionPane.showMessageDialog(this,"Enter ID, name and a valid email."); return;
        }
        new SwingWorker<Void,Void>() {
            protected Void doInBackground() throws Exception {
                try(Connection c=DB.connect(); PreparedStatement p=c.prepareStatement(
                    "INSERT INTO registrations(student_id,student_name,email,event_name) VALUES(?,?,?,?)")) {
                    p.setString(1,id);p.setString(2,name);p.setString(3,mail);p.setString(4,ev);p.executeUpdate();
                } return null;
            }
            protected void done(){try{get();JOptionPane.showMessageDialog(CampusEventRegistrationApp.this,"Registration successful.");loadAsync();}
                catch(ExecutionException ex){String m=ex.getCause() instanceof SQLIntegrityConstraintViolationException ?
                    "This student is already registered for this event." : "Registration failed: "+ex.getCause().getMessage();
                    JOptionPane.showMessageDialog(CampusEventRegistrationApp.this,m);}
                catch(Exception ex){JOptionPane.showMessageDialog(CampusEventRegistrationApp.this,ex.getMessage());}}
        }.execute();
    }
    private void loadAsync() {
        new SwingWorker<java.util.List<Object[]>,Void>() {
            protected java.util.List<Object[]> doInBackground() throws Exception {
                java.util.List<Object[]> rows=new java.util.ArrayList<>();
                String sql="SELECT student_id,student_name,email,event_name FROM registrations WHERE event_name=? ORDER BY student_id LIMIT 100 OFFSET 0";
                try(Connection c=DB.connect();PreparedStatement p=c.prepareStatement(sql)){
                    p.setString(1,(String)event.getSelectedItem());try(ResultSet r=p.executeQuery()){
                        while(r.next()) rows.add(new Object[]{r.getString(1),r.getString(2),r.getString(3),r.getString(4)});
                    }}
                return rows;
            }
            protected void done(){try{model.setRowCount(0);for(Object[] row:get())model.addRow(row);}
                catch(Exception ex){JOptionPane.showMessageDialog(CampusEventRegistrationApp.this,"Load failed: "+ex.getMessage());}}
        }.execute();
    }
    public static void main(String[] a){SwingUtilities.invokeLater(()->new CampusEventRegistrationApp().setVisible(true));}
}
class DB {
    static Connection connect() throws SQLException {
        // Configure environment variables or replace placeholders for your local MySQL setup.
        String url=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/campus_events");
        String user=System.getenv().getOrDefault("DB_USER","root");
        String pass=System.getenv().getOrDefault("DB_PASSWORD","");
        return DriverManager.getConnection(url,user,pass);
    }
}
