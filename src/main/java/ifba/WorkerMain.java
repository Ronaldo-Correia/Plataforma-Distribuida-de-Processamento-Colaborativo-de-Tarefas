package ifba;

import ifba.worker.Worker;

public class WorkerMain {
    public static void main(String[] args) {
        new Worker("worker1", 6001).start();
        new Worker("worker2", 6002).start();
        new Worker("worker3", 6003).start();
    }
}
