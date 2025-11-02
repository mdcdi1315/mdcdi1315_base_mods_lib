package com.github.mdcdi1315.DotNetLayer.System.Diagnostics;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.DotNetLayer.System.TimeSpan;

public class Stopwatch
{
    // The held value is in nanoseconds!
    private long start_timestamp, elapsed;
    private boolean running;

    /**
     * Indicates whether the timer is based on a high-resolution performance counter. This field is read-only.
     */
    // Uses Java's high quality timer
    public static final boolean IsHighResolution = true;

    /**
     * Initializes a new instance of the {@link Stopwatch} class.
     */
    public Stopwatch() {
        start_timestamp = elapsed = 0;
        running = false;
    }

    /**
     * Starts, or resumes, measuring elapsed time for an interval.
     */
    public void Start()
    {
        // Calling start on a running Stopwatch is a no-op.
        if (!running)
        {
            running = true;
            start_timestamp = System.nanoTime();
        }
    }

    /**
     * Stops measuring elapsed time for an interval.
     */
    public void Stop()
    {
        // Calling stop on a stopped Stopwatch is a no-op.
        if (running)
        {
            elapsed += GetTimestamp() - start_timestamp;
            running = false;
        }
    }

    /**
     * Stops time interval measurement and resets the elapsed time to zero.
     */
    public void Reset()
    {
        elapsed = start_timestamp = 0;
        running = false;
    }

    /**
     * Stops time interval measurement, resets the elapsed time to zero, and starts measuring elapsed time.
     */
    public void Restart()
    {
        // Convenience method for replacing {sw.Reset(); sw.Start();} with a single sw.Restart()
        elapsed = 0;
        start_timestamp = GetTimestamp();
        running = true;
    }

    /**
     * Gets the current number of ticks in the timer mechanism.
     * @return A long integer representing the tick counter value of the underlying timer mechanism.
     * @implNote This method does not return ticks, but nanoseconds. The documentation above is used as-is to preserve understandability.
     */
    public static long GetTimestamp()
    {
        // Forwards to java.lang.System.nanoTime method
        return System.nanoTime();
    }

    /**
     * Initializes a new {@link Stopwatch} instance, sets the elapsed time property to zero, and starts measuring elapsed time.
     * @return A {@link Stopwatch} that has just begun measuring elapsed time.
     */
    public static Stopwatch StartNew()
    {
        Stopwatch s = new Stopwatch();
        s.Start();
        return s;
    }

    /**
     * Gets a value indicating whether the {@link Stopwatch} timer is running.
     * @return {@code true} if the {@link Stopwatch} instance is currently running and measuring elapsed time for an interval; otherwise, {@code false}.
     * @apiNote A {@link Stopwatch} instance begins running with a call to {@link #Start} or {@link #StartNew}. The instance stops running with a call to {@link #Stop} or {@link #Reset}.
     */
    public boolean GetIsRunning() {
        return running;
    }

    /**
     * Gets the total elapsed time measured by the current instance.
     * @return A read-only {@link TimeSpan} representing the total elapsed time measured by the current instance.
     */
    @NotNull
    public TimeSpan GetElapsed() {
        return new TimeSpan(GetElapsedTicks());
    }

    /**
     * Gets the total elapsed time measured by the current instance, in timer ticks.
     * @return A read-only long integer representing the total number of timer ticks measured by the current instance.
     */
    public long GetElapsedTicks()
    {
        long timeElapsed = elapsed; // Value is in nanoseconds

        // If the Stopwatch is running, add elapsed time since the Stopwatch is started last time.
        if (running) {
            timeElapsed += GetTimestamp() - start_timestamp;
        }

        return timeElapsed / TimeSpan.NanosecondsPerTick; // Thus, we need to divide by nanoseconds per tick.
    }

    /**
     * Gets the total elapsed time measured by the current instance, in milliseconds.
     * @return A read-only long integer representing the total number of milliseconds measured by the current instance.
     */
    public long GetElapsedMilliseconds() {
        return GetElapsedTicks() / TimeSpan.TicksPerMillisecond;
    }

    /**
     * Gets the elapsed time since the startingTimestamp value retrieved using {@link #GetTimestamp()}.
     * @param startingTimestamp The timestamp marking the beginning of the time period.
     * @return A {@link TimeSpan} for the elapsed time between the starting timestamp and the time of this call.
     */
    public static TimeSpan GetElapsedTime(long startingTimestamp) {
        return GetElapsedTime(startingTimestamp , GetTimestamp());
    }

    /**
     * Gets the elapsed time between two timestamps retrieved using {@link #GetTimestamp()}.
     * @param startingTimestamp The timestamp marking the beginning of the time period.
     * @param endingTimestamp The timestamp marking the end of the time period.
     * @return A {@link TimeSpan} for the elapsed time between the starting and ending timestamps.
     */
    public static TimeSpan GetElapsedTime(long startingTimestamp, long endingTimestamp) {
        return new TimeSpan((endingTimestamp - startingTimestamp) / TimeSpan.NanosecondsPerTick);
    }

    /**
     * Returns the {@link #GetElapsed()} time as a string.
     * @return The elapsed time string in the same format used by {@link TimeSpan#ToString()}.
     */
    @Override
    public String toString() {
        return GetElapsed().ToString();
    }
}
