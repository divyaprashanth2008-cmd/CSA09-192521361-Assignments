import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
public class PassLoader {
    static String url=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/co5_transport");
    static String user=System.getenv().getOrDefault("DB_USER","root");
    static String password=System.getenv().getOrDefault("DB_PASSWORD","");
    static Connection connect()throws SQLException{return DriverManager.getConnection(url,user,password);}
    static void insertBatch(Path file)throws Exception{
        String sql="INSERT INTO bus_pass(pass_id,register_no,holder_name,route_no,boarding_stop,validity_date,amount_paid) VALUES(?,?,?,?,?,?,?)";
        try(Connection c=connect();BufferedReader br=Files.newBufferedReader(file);PreparedStatement ps=c.prepareStatement(sql)){
            c.setAutoCommit(false);String line;int count=0;
            try{
                while((line=br.readLine())!=null){
                    BusPassRecord p=BusPassRecord.parse(line);
                    ps.setString(1,p.id);ps.setString(2,p.reg);ps.setString(3,p.name);ps.setString(4,p.route);
                    ps.setString(5,p.stop);ps.setDate(6,Date.valueOf(p.valid));ps.setBigDecimal(7,p.amount);
                    ps.addBatch();count++;
                    if(count%1000==0)ps.executeBatch(); // TODO 1: execute each 1,000-row batch
                }
                if(count%1000!=0)ps.executeBatch(); // TODO 2: flush final partial batch
                c.commit(); // TODO 3: commit only after every batch succeeds
            }catch(Exception ex){c.rollback();throw ex;}
        }
    }
    record BusPassRecord(String id,String reg,String name,String route,String stop,LocalDate valid,BigDecimal amount){
        static BusPassRecord parse(String s){
            String[] a=s.split("\\|",-1);if(a.length!=7)throw new IllegalArgumentException("Wrong field count");
            BigDecimal amount=new BigDecimal(a[6]);if(amount.signum()<0)throw new IllegalArgumentException("Negative amount");
            return new BusPassRecord(a[0],a[1],a[2],a[3],a[4],LocalDate.parse(a[5]),amount);
        }
    }
    public static void main(String[] args)throws Exception{insertBatch(Path.of(args.length==0?"passes.txt":args[0]));System.out.println("Migration committed");}
}