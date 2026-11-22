public class ScannerStations {
    static class Station extends Thread {
        private final int scripts;
        Station(String name,int scripts){super(name);this.scripts=scripts;}
        public void run(){process();}
        void process(){
            int done=0;
            for(int i=0;i<scripts;i++){
                try{Thread.sleep(20);}catch(InterruptedException e){Thread.currentThread().interrupt();return;}
                done++;
            }
            System.out.println(getName()+" scanned "+done+" scripts.");
        }
    }
    static class RunnableStation implements Runnable {
        private final String name; private final int scripts;
        RunnableStation(String name,int scripts){this.name=name;this.scripts=scripts;}
        public void run(){
            for(int i=0;i<scripts;i++){
                try{Thread.sleep(20);}catch(InterruptedException e){Thread.currentThread().interrupt();return;}
            }
            System.out.println(name+" scanned "+scripts+" scripts.");
        }
    }
    public static void main(String[] args)throws InterruptedException{
        Thread[] stations={
            new Station("Station-1",5),
            new Thread(new RunnableStation("Station-2",5)),
            new Station("Station-3",5),
            new Thread(new RunnableStation("Station-4",5))
        };
        for(Thread t:stations)t.start();
        for(Thread t:stations)System.out.println(t.getName()+" alive? "+t.isAlive());
        for(Thread t:stations)t.join();
        System.out.println("All stations completed.");
    }
}
