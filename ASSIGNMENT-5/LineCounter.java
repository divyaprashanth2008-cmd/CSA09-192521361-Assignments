import java.io.*;
import java.nio.file.*;
public class LineCounter {
    public static long countUnbuffered(Path p) throws IOException {
        long lines=0; boolean any=false; int b;
        try (InputStream in=Files.newInputStream(p)) {
            while ((b=in.read())!=-1) { any=true; if (b=='\n') lines++; }
        }
        if (any) { try (RandomAccessFile f=new RandomAccessFile(p.toFile(),"r")) {
            long n=f.length(); if(n>0){f.seek(n-1); if(f.read()!='\n') lines++;}
        }}
        return lines;
    }
    public static long countBuffered(Path p) throws IOException {
        long lines=0; boolean any=false; int b;
        try (InputStream in=new BufferedInputStream(Files.newInputStream(p), 64*1024)) {
            while ((b=in.read())!=-1) { any=true; if(b=='\n') lines++; }
        }
        if(any){ try(RandomAccessFile f=new RandomAccessFile(p.toFile(),"r")){
            long n=f.length(); if(n>0){f.seek(n-1); if(f.read()!='\n') lines++;}
        }}
        return lines;
    }
    public static void main(String[] args) throws Exception {
        Path p=Path.of(args.length==0?"attendance.log":args[0]);
        long t=System.nanoTime(), a=countUnbuffered(p), u=System.nanoTime()-t;
        t=System.nanoTime(); long b=countBuffered(p); long v=System.nanoTime()-t;
        System.out.printf("lines=%d unbuffered_ms=%.3f buffered_ms=%.3f%n",a,u/1e6,v/1e6);
        if(a!=b) throw new IllegalStateException("Count mismatch");
    }
}