import java.util.*;
public class Task06 {
 static class Patient {final String name;final int severity;final long arrival;Patient(String n,int s,long a){name=n;severity=s;arrival=a;}public String toString(){return name+"(S"+severity+")";}}
 private long counter=0;private final PriorityQueue<Patient> queue=new PriorityQueue<>(Comparator.comparingInt((Patient p)->p.severity).thenComparingLong(p->p.arrival));
 void admit(String n,int s){queue.add(new Patient(n,s,counter++));} Patient next(){return queue.poll();}
 public static void main(String[] a){Task06 t=new Task06();t.admit("Asha",3);t.admit("Bala",1);t.admit("Charan",2);t.admit("Divya",1);t.admit("Eshan",5);t.admit("Farah",3);t.admit("Gokul",2);t.admit("Hari",4);System.out.println("Treatment order:");Patient p;while((p=t.next())!=null)System.out.println(p);for(int n:new int[]{1000,10000,100000}){PriorityQueue<Integer> q=new PriorityQueue<>();long x=System.nanoTime();for(int i=0;i<n;i++)q.add(n-i);long add=System.nanoTime()-x;x=System.nanoTime();while(!q.isEmpty())q.poll();long poll=System.nanoTime()-x;System.out.println("n="+n+" add-all="+add+"ns poll-all="+poll+"ns");}}
}