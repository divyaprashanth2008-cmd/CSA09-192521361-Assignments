import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

class Wallet {
    final int id; private long balance; final ReentrantLock lock=new ReentrantLock();
    Wallet(int id,long balance){this.id=id;this.balance=balance;}
    long getBalance(){return balance;}
    void debit(long n){balance-=n;} void credit(long n){balance+=n;}
}
public class WalletTransfer {
    static boolean transfer(Wallet from,Wallet to,long amount)throws InterruptedException{
        Wallet first=from.id<to.id?from:to, second=from.id<to.id?to:from;
        first.lock.lock();
        try{
            second.lock.lock();
            try{if(from.getBalance()<amount)return false;from.debit(amount);to.credit(amount);return true;}
            finally{second.lock.unlock();}
        }finally{first.lock.unlock();}
    }
    static boolean tryTransfer(Wallet from,Wallet to,long amount)throws InterruptedException{
        if(!from.lock.tryLock(100,TimeUnit.MILLISECONDS))return false;
        try{
            if(!to.lock.tryLock(100,TimeUnit.MILLISECONDS))return false;
            try{if(from.getBalance()<amount)return false;from.debit(amount);to.credit(amount);return true;}
            finally{to.lock.unlock();}
        }finally{from.lock.unlock();}
    }
    public static void main(String[] args)throws InterruptedException{
        Wallet a=new Wallet(1,1000),b=new Wallet(2,1000);
        Thread t1=new Thread(()->{try{for(int i=0;i<100;i++)transfer(a,b,1);}catch(InterruptedException e){Thread.currentThread().interrupt();}});
        Thread t2=new Thread(()->{try{for(int i=0;i<100;i++)transfer(b,a,1);}catch(InterruptedException e){Thread.currentThread().interrupt();}});
        t1.start();t2.start();t1.join();t2.join();
        System.out.println("Total wallet balance: "+(a.getBalance()+b.getBalance()));
    }
}
