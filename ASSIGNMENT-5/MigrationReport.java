import java.sql.*;
public class MigrationReport {
    public static void main(String[] args)throws Exception{
        String url=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/co5_transport");
        try(Connection c=DriverManager.getConnection(url,System.getenv().getOrDefault("DB_USER","root"),System.getenv().getOrDefault("DB_PASSWORD",""));
            ResultSet tables=c.getMetaData().getTables(c.getCatalog(),null,"%",new String[]{"TABLE"})){
            DatabaseMetaData m=c.getMetaData();
            System.out.println("Database: "+m.getDatabaseProductName());
            System.out.println("Driver: "+m.getDriverName()+" "+m.getDriverVersion());
            System.out.println("Tables:");
            while(tables.next())System.out.println(tables.getString("TABLE_NAME"));
        }
    }
}