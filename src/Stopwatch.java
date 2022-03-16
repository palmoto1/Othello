
public class Stopwatch {

    private long startTime;


    private long totalTime = 0;

    private boolean isRunning = false;


    public Stopwatch() {
    }

    public Stopwatch start() {
        if (!isRunning) {
            isRunning = true;
            startTime = System.nanoTime();
        }
        return this;
    }

    public Stopwatch stop() {
        if (isRunning) {
            totalTime += System.nanoTime() - startTime;
            isRunning = false;
        }
        return this;
    }


    public Stopwatch reset() {
        isRunning = false;
        totalTime = 0;
        return this;
    }


    public long milliseconds() {
        return nanoseconds() / 1000000;
    }


    public long nanoseconds() {
        return totalTime +
                (isRunning ? System.nanoTime() - startTime : 0);
    }


    public boolean isRunning() {
        return isRunning;
    }


    @Override
    public String toString() {
        return milliseconds() + " ms" +
                (isRunning() ? " (running)" : " (not running)");
    }
}