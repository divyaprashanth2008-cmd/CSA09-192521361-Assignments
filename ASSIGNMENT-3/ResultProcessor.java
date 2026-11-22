import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class ResultProcessor {
    record Result(String file,int score){}
    record Failure(String file,String reason){}
    static Result process(Path file)throws IOException{
        if(!Files.exists(file))throw new IOException("File is missing");
        String text=Files.readString(file).trim();
        if(!text.matches("\\d{1,3}"))throw new IOException("Corrupt score data");
        int score=Integer.parseInt(text);
        if(score<0||score>100)throw new IOException("Score outside 0..100");
        return new Result(file.getFileName().toString(),score);
    }
    public static void main(String[] args)throws Exception{
        int pool=args.length>0?Integer.parseInt(args[0]):4;
        Path dir=Path.of(args.length>1?args[1]:"scores");
        ExecutorService executor=Executors.newFixedThreadPool(pool);
        ConcurrentMap<String,Result> successes=new ConcurrentHashMap<>();
        ConcurrentMap<String,Failure> failures=new ConcurrentHashMap<>();
        List<Future<?>> futures=new ArrayList<>();
        long start=System.nanoTime();
        try(DirectoryStream<Path> files=Files.newDirectoryStream(dir,"*.txt")){
            for(Path file:files){
                futures.add(executor.submit(()->{
                    try{Result r=process(file);successes.put(r.file(),r);}
                    catch(Exception e){failures.put(file.getFileName().toString(),new Failure(file.getFileName().toString(),e.getMessage()));}
                }));
            }
        } finally { executor.shutdown(); }
        for(Future<?> f:futures)f.get();
        long elapsed=(System.nanoTime()-start)/1_000_000;
        System.out.println("Successes: "+successes.size());
        System.out.println("Failures: "+failures.size());
        failures.values().forEach(x->System.out.println(x.file()+": "+x.reason()));
        System.out.println("Elapsed ms: "+elapsed);
    }
}
