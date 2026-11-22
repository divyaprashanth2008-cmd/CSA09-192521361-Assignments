import java.sql.*;
import java.util.*;
public class StudentDao implements Dao<Student,Integer> {
    private final Connection c;
    public StudentDao(Connection c){this.c=c;}
    public Integer insert(Student s) throws SQLException {
        String q="INSERT INTO student(register_no,name,email) VALUES(?,?,?)";
        try(PreparedStatement ps=c.prepareStatement(q,Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,s.registerNo()); ps.setString(2,s.name()); ps.setString(3,s.email());
            ps.executeUpdate(); try(ResultSet k=ps.getGeneratedKeys()){if(k.next()) return k.getInt(1);}
        } return null;
    }
    private Student map(ResultSet r)throws SQLException{return new Student(r.getInt("student_id"),r.getString("register_no"),r.getString("name"),r.getString("email"));}
    public Optional<Student> findById(Integer id)throws SQLException{
        try(PreparedStatement ps=c.prepareStatement("SELECT * FROM student WHERE student_id=?")){
            ps.setInt(1,id); try(ResultSet r=ps.executeQuery()){return r.next()?Optional.of(map(r)):Optional.empty();}
        }
    }
    public List<Student> findAll()throws SQLException{
        List<Student> out=new ArrayList<>(); try(PreparedStatement ps=c.prepareStatement("SELECT * FROM student");ResultSet r=ps.executeQuery()){while(r.next())out.add(map(r));} return out;
    }
    public boolean update(Student s)throws SQLException{
        try(PreparedStatement ps=c.prepareStatement("UPDATE student SET register_no=?,name=?,email=? WHERE student_id=?")){
            ps.setString(1,s.registerNo());ps.setString(2,s.name());ps.setString(3,s.email());ps.setInt(4,s.studentId());return ps.executeUpdate()==1;
        }
    }
    public boolean delete(Integer id)throws SQLException{
        try(PreparedStatement ps=c.prepareStatement("DELETE FROM student WHERE student_id=?")){ps.setInt(1,id);return ps.executeUpdate()==1;}
    }
}