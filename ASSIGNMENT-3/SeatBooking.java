import java.util.concurrent.atomic.AtomicBoolean;

public class SeatBooking {
    private final boolean[] booked;
    private int bookingsMade=0;
    SeatBooking(int seats){booked=new boolean[seats];}
    public synchronized boolean book(int seat){
        if(booked[seat]) return false;
        booked[seat]=true;
        bookingsMade++;
        return true;
    }
    public int getBookingsMade(){return bookingsMade;}
    public static void main(String[] args)throws InterruptedException{
        SeatBooking screen=new SeatBooking(10);
        Thread[] users=new Thread[100];
        for(int i=0;i<users.length;i++){
            final int seat=i%10;
            users[i]=new Thread(()->screen.book(seat));
            users[i].start();
        }
        for(Thread t:users)t.join();
        System.out.println("Seats sold: "+screen.getBookingsMade());
    }
}
class AtomicSeatBooking {
    private final AtomicBoolean[] seats;
    AtomicSeatBooking(int count){seats=new AtomicBoolean[count];for(int i=0;i<count;i++)seats[i]=new AtomicBoolean(false);}
    boolean book(int seat){return seats[seat].compareAndSet(false,true);}
}
