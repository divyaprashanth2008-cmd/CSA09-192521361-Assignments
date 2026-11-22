import java.math.BigDecimal;
import java.sql.*;
public class WalletTransfer {
    public static void transfer(Connection c,long from,long to,BigDecimal amount)throws SQLException{
        if(amount.signum()<=0)throw new IllegalArgumentException("Amount must be positive");
        boolean old=c.getAutoCommit(); c.setAutoCommit(false);
        try{
            try(PreparedStatement debit=c.prepareStatement("UPDATE wallet SET balance=balance-? WHERE wallet_id=? AND balance>=?");
                PreparedStatement credit=c.prepareStatement("UPDATE wallet SET balance=balance+? WHERE wallet_id=?")){
                debit.setBigDecimal(1,amount);debit.setLong(2,from);debit.setBigDecimal(3,amount);
                if(debit.executeUpdate()!=1)throw new SQLException("Insufficient funds or source wallet missing");
                credit.setBigDecimal(1,amount);credit.setLong(2,to);
                if(credit.executeUpdate()!=1)throw new SQLException("Destination wallet missing");
            }
            c.commit();
        }catch(SQLException|RuntimeException ex){c.rollback();throw ex;}
        finally{c.setAutoCommit(old);}
    }
    public static boolean unsafeLogin(Connection c,String user,String pass)throws SQLException{
        String sql="SELECT COUNT(*) FROM app_user WHERE username='"+user+"' AND password='"+pass+"'";
        try(Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){r.next();return r.getInt(1)>0;}
    }
    public static boolean safeLogin(Connection c,String user,String pass)throws SQLException{
        String sql="SELECT COUNT(*) FROM app_user WHERE username=? AND password_hash=?";
        try(PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,user);ps.setString(2,pass);
            try(ResultSet r=ps.executeQuery()){r.next();return r.getInt(1)>0;}
        }
    }
}