import java.io.*;
import java.util.*;
public class Hostel implements Serializable {
    private static final long serialVersionUID=1L;
    private final String hostelName;
    private final List<Room> rooms;
    public Hostel(String name,List<Room> rooms){this.hostelName=name;this.rooms=new ArrayList<>(rooms);}
    public String toString(){return hostelName+" "+rooms;}
    public static void main(String[] args)throws Exception{
        Resident resident=new Resident("Kavi","REG01","secret", "9876543210");
        Hostel h=new Hostel("North Hostel",List.of(new Room("A-101",List.of(resident))));
        try(ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream("hostel.ser"))){out.writeObject(h);}
        try(ObjectInputStream in=new ObjectInputStream(new FileInputStream("hostel.ser"))){System.out.println(in.readObject());}
    }
}
class Room implements Serializable {
    private static final long serialVersionUID=1L;
    final String roomNo; final List<Resident> residents;
    Room(String n,List<Resident> r){roomNo=n;residents=new ArrayList<>(r);}
    public String toString(){return roomNo+residents;}
}
class Resident implements Serializable {
    private static final long serialVersionUID=1L;
    final String name, registerNo;
    private transient String password; // never persist credentials in a serialized object
    String phone;
    private transient java.sql.Connection connection; // live resource must be re-created
    Resident(String n,String r,String p,String phone){name=n;registerNo=r;password=p;this.phone=phone;}
    public String toString(){return name+"("+registerNo+")";}
}