import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SalesReportResourceDemo {
    static String generate(Path folder, Path output) throws IOException {
        List<String> lines=new ArrayList<>(); double total=0;
        try (DirectoryStream<Path> files=Files.newDirectoryStream(folder,"*.csv")) {
            for(Path file:files) {
                System.out.println("Reading "+file.getFileName());
                try(BufferedReader reader=Files.newBufferedReader(file)) {
                    String line;
                    while((line=reader.readLine())!=null) {
                        if(line.isBlank()) continue;
                        String[] parts=line.split(",");
                        if(parts.length!=2) throw new IOException("Malformed row in "+file);
                        total+=Double.parseDouble(parts[1].trim());
                    }
                } catch(NumberFormatException ex) {
                    throw new IOException("Invalid sales amount in "+file,ex);
                }
            }
        }
        String report="Daily sales total: "+total+System.lineSeparator();
        Files.writeString(output,report);
        return report;
    }
    public static void main(String[] args) throws Exception {
        Path input=Path.of(args.length>0?args[0]:"sales-input");
        Path output=Path.of("sales-summary.txt");
        try { System.out.print(generate(input,output)); }
        catch(IOException e) { System.err.println("Report incomplete: "+e.getMessage()); e.printStackTrace(); }
    }
}
