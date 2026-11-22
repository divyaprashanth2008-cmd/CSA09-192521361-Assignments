import java.io.*;
import java.nio.file.*;
import java.util.*;
public class PassStore {
    private final Path path;
    public PassStore(Path path) { this.path = path; }
    public void add(BusPass pass) throws IOException {
        Path parent = path.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        try (BufferedWriter w = Files.newBufferedWriter(path, java.nio.charset.StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(pass.toCsv()); w.newLine();
        }
    }
    // TODO 1 completed: scan one line at a time; malformed records are skipped.
    public BusPass find(String id) throws IOException {
        if (!Files.exists(path)) return null;
        try (BufferedReader r = Files.newBufferedReader(path)) {
            String line;
            while ((line = r.readLine()) != null) {
                try {
                    BusPass pass = BusPass.parse(line);
                    if (pass.passId().equals(id)) return pass;
                } catch (RuntimeException damagedLine) {
                    System.err.println("Skipping damaged record: " + line);
                }
            }
        }
        return null;
    }
    public List<BusPass> listByRoute(String route) throws IOException {
        List<BusPass> result = new ArrayList<>();
        if (!Files.exists(path)) return result;
        try (BufferedReader r = Files.newBufferedReader(path)) {
            String line;
            while ((line = r.readLine()) != null) {
                try { BusPass p = BusPass.parse(line); if (p.routeNo().equals(route)) result.add(p); }
                catch (RuntimeException ex) { System.err.println("Skipping damaged record: " + line); }
            }
        }
        return result;
    }
    public static void main(String[] args) throws Exception {
        Path p = Path.of("passes.txt");
        Files.deleteIfExists(p);
        PassStore store = new PassStore(p);
        store.add(new BusPass("P1003","REG103","Anu","R2","Central",java.time.LocalDate.parse("2027-03-31"),new java.math.BigDecimal("1250.00")));
        Files.writeString(p, "P1002|BROKEN|LINE\n", StandardOpenOption.APPEND);
        System.out.println(store.find("P1003"));
        System.out.println(store.find("P1002"));
    }
}