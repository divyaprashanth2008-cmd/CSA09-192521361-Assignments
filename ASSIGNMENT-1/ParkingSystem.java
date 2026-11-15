package parking.app;

import java.time.*;
import java.util.*;

/** Demonstration implementation for the Campus Smart-Parking assignment (Java 17+). */
public class ParkingSystem {
    enum UserType { STUDENT, STAFF, VISITOR }
    enum SlotType { TWO_WHEELER, CAR, CHARGING }

    interface Chargeable { double chargingFee(int kWh); }
    interface ParkingPass { boolean isValidAt(LocalDateTime time); }
    static final class MonthlyPass implements ParkingPass {
        private final LocalDate validUntil;
        MonthlyPass(LocalDate validUntil) { this.validUntil = Objects.requireNonNull(validUntil); }
        public boolean isValidAt(LocalDateTime time) { return !time.toLocalDate().isAfter(validUntil); }
    }
    static final class OneDayPass implements ParkingPass {
        private final LocalDate validDate;
        OneDayPass(LocalDate validDate) { this.validDate = Objects.requireNonNull(validDate); }
        public boolean isValidAt(LocalDateTime time) { return validDate.equals(time.toLocalDate()); }
    }

    abstract static class Vehicle {
        private final String registrationNumber;
        Vehicle(String registrationNumber) {
            if (registrationNumber == null || registrationNumber.isBlank()) throw new IllegalArgumentException("Registration number required");
            this.registrationNumber = registrationNumber.trim().toUpperCase(Locale.ROOT);
        }
        public final String getRegistrationNumber() { return registrationNumber; }
        public abstract SlotType requiredSlot();
        public abstract double hourlyRate();
        public double flatFee() { return -1; }
        @Override public final boolean equals(Object obj) { return obj instanceof Vehicle v && registrationNumber.equals(v.registrationNumber); }
        @Override public final int hashCode() { return registrationNumber.hashCode(); }
        @Override public String toString() { return getClass().getSimpleName() + "[" + registrationNumber + "]"; }
    }
    static final class TwoWheeler extends Vehicle {
        TwoWheeler(String reg) { super(reg); }
        public SlotType requiredSlot() { return SlotType.TWO_WHEELER; }
        public double hourlyRate() { return 10.0; }
    }
    static class Car extends Vehicle {
        Car(String reg) { super(reg); }
        public SlotType requiredSlot() { return SlotType.CAR; }
        public double hourlyRate() { return 30.0; }
    }
    static final class ElectricCar extends Car implements Chargeable {
        ElectricCar(String reg) { super(reg); }
        @Override public SlotType requiredSlot() { return SlotType.CHARGING; }
        @Override public double hourlyRate() { return 60.0; }
        public double chargingFee(int kWh) { if (kWh < 0) throw new IllegalArgumentException("Negative kWh"); return kWh * 3.0; }
    }
    static final class EBike extends Vehicle {
        EBike(String reg) { super(reg); }
        public SlotType requiredSlot() { return SlotType.CHARGING; }
        public double hourlyRate() { return 0; }
        @Override public double flatFee() { return 20.0; }
    }
    static final class DeliveryVan extends Vehicle {
        DeliveryVan(String reg) { super(reg); }
        public SlotType requiredSlot() { return SlotType.CAR; }
        public double hourlyRate() { return 30.0; }
    }

    static final class ParkingSlot {
        private final int floor, number;
        private final SlotType type;
        private Vehicle occupant;
        ParkingSlot(int floor, int number, SlotType type) { this.floor=floor; this.number=number; this.type=type; }
        boolean isFree() { return occupant == null; }
        boolean fits(Vehicle v) { return isFree() && (type == v.requiredSlot() || (type == SlotType.CAR && v instanceof DeliveryVan)); }
        void park(Vehicle v) { if (!fits(v)) throw new IllegalStateException("Slot unavailable or incompatible"); occupant=v; }
        void release() { if (isFree()) throw new IllegalStateException("Slot already free"); occupant=null; }
        int floor() { return floor; } int number() { return number; }
    }
    static final class ParkingTicket {
        private static long nextId = 1000;
        private final long ticketId;
        private final Vehicle vehicle;
        private final ParkingSlot slot;
        private final LocalDateTime entryTime;
        ParkingTicket(Vehicle vehicle, ParkingSlot slot, LocalDateTime entryTime) {
            this.ticketId=nextId++; this.vehicle=Objects.requireNonNull(vehicle); this.slot=Objects.requireNonNull(slot); this.entryTime=Objects.requireNonNull(entryTime);
        }
        long getTicketId() { return ticketId; } Vehicle getVehicle() { return vehicle; }
        ParkingSlot getSlot() { return slot; } LocalDateTime getEntryTime() { return entryTime; }
    }
    static final class ParkingFacility {
        private final ParkingSlot[][] floors;
        ParkingFacility(int[] slotCounts) {
            if (slotCounts == null || slotCounts.length == 0) throw new IllegalArgumentException("Floors required");
            floors = new ParkingSlot[slotCounts.length][];
            for (int f=0; f<slotCounts.length; f++) {
                if (slotCounts[f] < 0) throw new IllegalArgumentException("Negative slot count");
                floors[f] = new ParkingSlot[slotCounts[f]];
                for (int s=0; s<slotCounts[f]; s++) {
                    SlotType type = (s % 5 == 0) ? SlotType.CHARGING : (s % 2 == 0 ? SlotType.CAR : SlotType.TWO_WHEELER);
                    floors[f][s] = new ParkingSlot(f, s, type);
                }
            }
        }
        ParkingSlot allocate(Vehicle vehicle) {
            for (ParkingSlot[] floor : floors) for (ParkingSlot slot : floor) if (slot.fits(vehicle)) { slot.park(vehicle); return slot; }
            return null;
        }
        void release(ParkingSlot slot) { slot.release(); }
        int floorCount() { return floors.length; }
    }
    static final class FeeCalculator {
        static double calculate(Vehicle v, LocalDateTime in, LocalDateTime out, UserType user, ParkingPass pass, int chargingKWh) {
            if (out.isBefore(in)) throw new IllegalArgumentException("Exit precedes entry");
            if (user != UserType.VISITOR && pass != null && pass.isValidAt(in) && pass.isValidAt(out)) return 0.0;
            long minutes = Duration.between(in, out).toMinutes();
            if (v.flatFee() >= 0) return v.flatFee();
            long hours = (minutes + 59) / 60;
            double fee = 0;
            if (v instanceof DeliveryVan) {
                LocalDateTime cursor = in;
                for (long i=0; i<minutes; i++) {
                    LocalDateTime t = cursor.plusMinutes(i);
                    double perMinute = v.hourlyRate() / 60.0 * (t.toLocalTime().isBefore(LocalTime.of(9,0)) || !t.toLocalTime().isBefore(LocalTime.of(17,0)) ? 1.0 : 2.0);
                    fee += perMinute;
                }
                fee = Math.ceil(fee / v.hourlyRate()) * v.hourlyRate();
            } else fee = hours * v.hourlyRate();
            if (v instanceof Chargeable c) fee += c.chargingFee(chargingKWh);
            return fee;
        }
    }
    static final class ParkingService {
        private final ParkingFacility facility;
        private final Map<String, ParkingTicket> active = new HashMap<>();
        ParkingService(ParkingFacility facility) { this.facility=facility; }
        ParkingTicket enter(Vehicle v, LocalDateTime time) {
            if (active.containsKey(v.getRegistrationNumber())) throw new IllegalArgumentException("Duplicate active registration");
            ParkingSlot slot=facility.allocate(v); if (slot==null) throw new IllegalStateException("No suitable slot available");
            ParkingTicket ticket=new ParkingTicket(v,slot,time); active.put(v.getRegistrationNumber(),ticket); return ticket;
        }
        double exit(String reg, LocalDateTime time, UserType user, ParkingPass pass, int kWh) {
            ParkingTicket ticket=active.remove(reg.toUpperCase(Locale.ROOT));
            if (ticket==null) throw new IllegalArgumentException("No active ticket");
            double fee=FeeCalculator.calculate(ticket.getVehicle(),ticket.getEntryTime(),time,user,pass,kWh);
            facility.release(ticket.getSlot()); return fee;
        }
        int activeCount() { return active.size(); }
    }

    public static void main(String[] args) {
        ParkingFacility facility = new ParkingFacility(new int[]{4, 3, 5, 2, 4});
        ParkingService service = new ParkingService(facility);
        LocalDateTime entry = LocalDateTime.of(2026, 11, 15, 8, 30);
        service.enter(new TwoWheeler("TN01AA1111"), entry);
        service.enter(new ElectricCar("TN02BB2222"), entry);
        service.enter(new EBike("TN03CC3333"), entry);
        service.enter(new DeliveryVan("TN04DD4444"), LocalDateTime.of(2026,11,15,10,0));
        System.out.printf(Locale.ROOT, "TwoWheeler fee: Rs %.2f%n", service.exit("TN01AA1111", entry.plusMinutes(61), UserType.VISITOR, null, 0));
        System.out.printf(Locale.ROOT, "EV fee: Rs %.2f%n", service.exit("TN02BB2222", entry.plusMinutes(60), UserType.VISITOR, null, 10));
        System.out.printf(Locale.ROOT, "E-bike fee: Rs %.2f%n", service.exit("TN03CC3333", entry.plusMinutes(30), UserType.VISITOR, null, 0));
        System.out.printf(Locale.ROOT, "Delivery van fee: Rs %.2f%n", service.exit("TN04DD4444", LocalDateTime.of(2026,11,15,11,0), UserType.VISITOR, null, 0));
        System.out.println("Active tickets: " + service.activeCount());
    }
}
