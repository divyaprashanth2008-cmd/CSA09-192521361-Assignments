import javax.swing.*;
import java.awt.*;
import java.util.*;

public class HospitalAppointmentApp extends JFrame {
    private final JComboBox<String> department = new JComboBox<>(new String[]{"Cardiology","Dermatology","Orthopaedics"});
    private final JComboBox<String> doctor = new JComboBox<>();
    private final JTextField patient = new JTextField(16);
    private final JTextField date = new JTextField("YYYY-MM-DD", 12);
    private final JLabel status = new JLabel("Select a department.");
    private final Map<String, java.util.List<String>> doctors = Map.of(
        "Cardiology", java.util.List.of("Dr. Rao","Dr. Mehta"),
        "Dermatology", java.util.List.of("Dr. Iyer","Dr. Khan"),
        "Orthopaedics", java.util.List.of("Dr. Das","Dr. Nair"));

    public HospitalAppointmentApp() {
        super("Outpatient Appointment Desk");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel form = new JPanel(new GridLayout(0,2,8,8));
        form.add(new JLabel("Department")); form.add(department);
        form.add(new JLabel("Doctor")); form.add(doctor);
        form.add(new JLabel("Patient name")); form.add(patient);
        form.add(new JLabel("Appointment date")); form.add(date);
        JButton book = new JButton("Book appointment");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bottom.add(book);
        add(form,BorderLayout.CENTER); add(bottom,BorderLayout.SOUTH); add(status,BorderLayout.NORTH);
        department.addActionListener(e -> refreshDoctors());
        book.addActionListener(e -> bookAppointment());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) { status.setText("Closing appointment desk."); }
        });
        refreshDoctors(); setSize(440,250); setLocationRelativeTo(null);
    }
    private void refreshDoctors() {
        doctor.removeAllItems();
        doctors.getOrDefault((String)department.getSelectedItem(), java.util.List.of())
               .forEach(doctor::addItem);
    }
    private void bookAppointment() {
        if (patient.getText().isBlank() || date.getText().isBlank() || doctor.getSelectedItem()==null) {
            status.setText("Enter patient, date and doctor."); return;
        }
        status.setText("Appointment request recorded for " + patient.getText().trim());
    }
    public static void main(String[] a) { SwingUtilities.invokeLater(() -> new HospitalAppointmentApp().setVisible(true)); }
}
