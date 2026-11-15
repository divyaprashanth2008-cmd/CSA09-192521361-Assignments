import java.util.*;
public class Task01 {
 static String[] fields(int n){String[] a=new String[n]; for(int i=0;i<n;i++) a[i]="FIELD"+i+"=value"+i; return a;}
 static String plus(String[] a){String s=""; for(String x:a)s+=x+'|'; return s;}
 static String builder(String[] a){StringBuilder b=new StringBuilder(); for(String x:a)b.append(x).append('|'); return b.toString();}
 static String buffer(String[] a){StringBuffer b=new StringBuffer(); for(String x:a)b.append(x).append('|'); return b.toString();}
 static long bench(java.util.function.Supplier<String> f,int reps){long t=System.nanoTime(); for(int i=0;i<reps;i++)f.get(); return (System.nanoTime()-t)/reps;}
 public static void main(String[] z){int[] sizes={6,50,200}; int reps= sizes[0]==6?20000:2000; System.out.println("fields,plus_ns,builder_ns,buffer_ns"); for(int n:sizes){String[] a=fields(n); int r=n==6?20000:n==50?4000:500; System.out.println(n+","+bench(()->plus(a),r)+","+bench(()->builder(a),r)+","+bench(()->buffer(a),r));} System.out.println("Shared log: synchronize access to a shared StringBuffer, or preferably use a dedicated logger; use local StringBuilder per ticket.");}
}