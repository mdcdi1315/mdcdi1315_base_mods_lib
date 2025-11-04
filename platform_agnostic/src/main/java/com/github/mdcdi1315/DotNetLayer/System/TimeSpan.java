package com.github.mdcdi1315.DotNetLayer.System;

import com.github.mdcdi1315.DotNetLayer.ClassIsDotNetStruct;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImpl;
import com.github.mdcdi1315.DotNetLayer.System.Runtime.CompilerServices.MethodImplOptions;

/**
 * Represents a time interval.
 */
@ClassIsDotNetStruct
public final class TimeSpan
    extends ValueType
{
    /**
     * Represents the number of nanoseconds per tick. This field is constant.
     * @apiNote The value of this constant is 100.
     */
    public static final long NanosecondsPerTick = 100;                                                 //             100

    /**
     * Represents the number of ticks in 1 microsecond. This field is constant.
     * @apiNote The value of this constant is 10.
     */
    public static final long TicksPerMicrosecond = 10;                                                 //              10

    /**
     * Represents the number of ticks in 1 millisecond. This field is constant.
     * @apiNote The value of this constant is 10 thousand; that is, 10,000.
     */
    public static final long TicksPerMillisecond = TicksPerMicrosecond * 1000;                         //          10,000

    /**
     * Represents the number of ticks in 1 second. This field is constant.
     * @apiNote The value of this constant is 10 million; that is, 10,000,000.
     */
    public static final long TicksPerSecond = TicksPerMillisecond * 1000;                              //      10,000,000

    /**
     * Represents the number of ticks in 1 minute. This field is constant.
     * @apiNote The value of this constant is 600 million; that is, 600,000,000.
     */
    public static final long TicksPerMinute = TicksPerSecond * 60;                                     //     600,000,000

    /**
     * Represents the number of ticks in 1 hour. This field is constant.
     * @apiNote The value of this constant is 36 billion; that is, 36,000,000,000.
     */
    public static final long TicksPerHour = TicksPerMinute * 60;                                       //  36,000,000,000

    /**
     * Represents the number of ticks in 1 day. This field is constant.
     * @apiNote The value of this constant is 864 billion; that is, 864,000,000,000.
     */
    public static final long TicksPerDay = TicksPerHour * 24;                                          // 864,000,000,000

    /**
     * Represents the number of microseconds in 1 millisecond. This field is constant.
     * @apiNote The value of this constant is 1 thousand; that is, 1,000.
     */
    public static final long MicrosecondsPerMillisecond = TicksPerMillisecond / TicksPerMicrosecond;   //           1,000

    /**
     * Represents the number of microseconds in 1 second. This field is constant.
     * @apiNote The value of this constant is 1 million; that is, 1,000,000.
     */
    public static final long MicrosecondsPerSecond = TicksPerSecond / TicksPerMicrosecond;             //       1,000,000

    /**
     * Represents the number of microseconds in 1 minute. This field is constant.
     * @apiNote The value of this constant is 60 million; that is, 60,000,000.
     */
    public static final long MicrosecondsPerMinute = TicksPerMinute / TicksPerMicrosecond;             //      60,000,000

    /**
     * Represents the number of microseconds in 1 hour. This field is constant.
     * @apiNote The value of this constant is 3.6 billion; that is, 3,600,000,000.
     */
    public static final long MicrosecondsPerHour = TicksPerHour / TicksPerMicrosecond;                 //   3,600,000,000

    /**
     * Represents the number of microseconds in 1 day. This field is constant.
     * @apiNote The value of this constant is 86.4 billion; that is, 86,400,000,000.
     */
    public static final long MicrosecondsPerDay = TicksPerDay / TicksPerMicrosecond;                   //  86,400,000,000

    /**
     * Represents the number of milliseconds in 1 second. This field is constant.
     * @apiNote The value of this constant is 1 thousand; that is, 1,000.
     */
    public static final long MillisecondsPerSecond = TicksPerSecond / TicksPerMillisecond;             //           1,000

    /**
     * Represents the number of milliseconds in 1 minute. This field is constant.
     * @apiNote The value of this constant is 60 thousand; that is, 60,000.
     */
    public static final long MillisecondsPerMinute = TicksPerMinute / TicksPerMillisecond;             //          60,000

    /**
     * Represents the number of milliseconds in 1 hour. This field is constant.
     * @apiNote The value of this constant is 3.6 million; that is, 3,600,000.
     */
    public static final long MillisecondsPerHour = TicksPerHour / TicksPerMillisecond;                 //       3,600,000

    /**
     * Represents the number of milliseconds in 1 day. This field is constant.
     * @apiNote The value of this constant is 86.4 million; that is, 86,400,000.
     */
    public static final long MillisecondsPerDay = TicksPerDay / TicksPerMillisecond;                   //      86,400,000

    /**
     * Represents the number of seconds in 1 minute. This field is constant.
     * @apiNote The value of this constant is 60.
     */
    public static final long SecondsPerMinute = TicksPerMinute / TicksPerSecond;                       //              60

    /**
     * Represents the number of seconds in 1 hour. This field is constant.
     * @apiNote The value of this constant is 3.6 thousand; that is, 3,600.
     */
    public static final long SecondsPerHour = TicksPerHour / TicksPerSecond;                           //           3,600

    /**
     * Represents the number of seconds in 1 day. This field is constant.
     * @apiNote The value of this constant is 86.4 thousand; that is, 86,400.
     */
    public static final long SecondsPerDay = TicksPerDay / TicksPerSecond;                             //          86,400

    /**
     * Represents the number of minutes in 1 hour. This field is constant.
     * @apiNote The value of this constant is 60.
     */
    public static final long MinutesPerHour = TicksPerHour / TicksPerMinute;                           //              60

    /**
     * Represents the number of minutes in 1 day. This field is constant.
     * @apiNote The value of this constant is 1.44 thousand; that is, 1,440.
     */
    public static final long MinutesPerDay = TicksPerDay / TicksPerMinute;                             //           1,440

    /**
     * Represents the number of hours in 1 day. This field is constant.
     * @apiNote The value of this constant is 24.
     */
    public static final int HoursPerDay = (int)(TicksPerDay / TicksPerHour);                           //              24

    static final long MinTicks = Long.MIN_VALUE;                                              // -9,223,372,036,854,775,808
    static final long MaxTicks = Long.MAX_VALUE;                                              // +9,223,372,036,854,775,807

    static final long MinMicroseconds = MinTicks / TicksPerMicrosecond;                       // -  922,337,203,685,477,580
    static final long MaxMicroseconds = MaxTicks / TicksPerMicrosecond;                       // +  922,337,203,685,477,580

    static final long MinMilliseconds = MinTicks / TicksPerMillisecond;                       // -      922,337,203,685,477
    static final long MaxMilliseconds = MaxTicks / TicksPerMillisecond;                       // +      922,337,203,685,477

    static final long MinSeconds = MinTicks / TicksPerSecond;                                 // -          922,337,203,685
    static final long MaxSeconds = MaxTicks / TicksPerSecond;                                 // +          922,337,203,685

    static final long MinMinutes = MinTicks / TicksPerMinute;                                 // -           15,372,286,728
    static final long MaxMinutes = MaxTicks / TicksPerMinute;                                 // +           15,372,286,728

    static final long MinHours = MinTicks / TicksPerHour;                                     // -              256,204,778
    static final long MaxHours = MaxTicks / TicksPerHour;                                     // +              256,204,778

    static final long MinDays = MinTicks / TicksPerDay;                                       // -               10,675,199
    static final long MaxDays = MaxTicks / TicksPerDay;                                       // +               10,675,199

    static final long TicksPerTenthSecond = TicksPerMillisecond * 100;

    /**
     * Represents the zero {@link TimeSpan} value. This field is read-only.
     */
    public static final TimeSpan Zero = new TimeSpan(0);
    /**
     * Represents the maximum {@link TimeSpan} value. This field is read-only.
     */
    public static final TimeSpan MaxValue = new TimeSpan(MaxTicks);
    /**
     * Represents the minimum {@link TimeSpan} value. This field is read-only.
     */
    public static final TimeSpan MinValue = new TimeSpan(MinTicks);

    // internal so that DateTime doesn't have to call an extra get
    // method for some arithmetic operations.
    final long _ticks; // Do not rename (binary serialization)

    public TimeSpan() { _ticks = 0; } // Empty ctor as defined in .NET

    /**
     * Initializes a new instance of the {@link TimeSpan} structure to the specified number of ticks.
     * @param ticks A time period expressed in 100-nanosecond units.
     */
    public TimeSpan(long ticks)
    {
        _ticks = ticks;
    }

    /**
     * Initializes a new instance of the {@link TimeSpan} structure to a specified number of hours, minutes, and seconds.
     * @param hours Number of hours.
     * @param minutes Number of minutes.
     * @param seconds Number of seconds.
     * @throws ArgumentOutOfRangeException The parameters specify a {@link TimeSpan} value less than {@link TimeSpan#MinValue} or greater than {@link TimeSpan#MaxValue}.
     * @see Long
     */
    public TimeSpan(int hours, int minutes, int seconds)
        throws ArgumentOutOfRangeException
    {
        _ticks = TimeToTicks(hours, minutes, seconds);
    }

    /**
     * Initializes a new instance of the {@link TimeSpan} structure to a specified number of days, hours, minutes, and seconds.
     * @param days Number of days.
     * @param hours Number of hours.
     * @param minutes Number of minutes.
     * @param seconds Number of seconds.
     * @implNote The specified {@code days}, {@code hours}, {@code minutes}, and {@code seconds} are converted to ticks, and that value initializes this instance.
     * @throws ArgumentOutOfRangeException The parameters specify a {@link TimeSpan} value less than {@link TimeSpan#MinValue} or greater than {@link TimeSpan#MaxValue}.
     * @see Long
     */
    public TimeSpan(int days, int hours, int minutes, int seconds)
            throws ArgumentOutOfRangeException
    {
        this(days, hours, minutes, seconds, 0);
    }

    /**
     * Initializes a new instance of the {@link TimeSpan} structure to a specified number of days, hours, minutes, and seconds.
     * @param days Number of days.
     * @param hours Number of hours.
     * @param minutes Number of minutes.
     * @param seconds Number of seconds.
     * @param milliseconds Number of milliseconds.
     * @implNote The specified {@code days}, {@code hours}, {@code minutes}, {@code seconds}, and {@code milliseconds} are converted to ticks, and that value initializes this instance.
     * @throws ArgumentOutOfRangeException The parameters specify a {@link TimeSpan} value less than {@link TimeSpan#MinValue} or greater than {@link TimeSpan#MaxValue}.
     * @see Long
     */
    public TimeSpan(int days, int hours, int minutes, int seconds, int milliseconds)
    {
        this(days, hours, minutes, seconds, milliseconds, 0);
    }

    /**
     * Initializes a new instance of the {@link TimeSpan} structure to a specified number of days, hours, minutes, and seconds.
     * @param days Number of days.
     * @param hours Number of hours.
     * @param minutes Number of minutes.
     * @param seconds Number of seconds.
     * @param milliseconds Number of milliseconds.
     * @param microseconds Number of microseconds.
     * @implNote The specified {@code days}, {@code hours}, {@code minutes}, {@code seconds}, {@code milliseconds} and {@code microseconds} are converted to ticks, and that value initializes this instance.
     * @throws ArgumentOutOfRangeException The parameters specify a {@link TimeSpan} value less than {@link TimeSpan#MinValue} or greater than {@link TimeSpan#MaxValue}.
     * @see Long
     */
    public TimeSpan(int days, int hours, int minutes, int seconds, int milliseconds, int microseconds)
    {
        long totalMicroseconds = (days * MicrosecondsPerDay)
                + (hours * MicrosecondsPerHour)
                + (minutes * MicrosecondsPerMinute)
                + (seconds * MicrosecondsPerSecond)
                + (milliseconds * MicrosecondsPerMillisecond)
                + microseconds;

        if ((totalMicroseconds > MaxMicroseconds) || (totalMicroseconds < MinMicroseconds))
        {
            // ThrowHelper.ThrowArgumentOutOfRange_TimeSpanTooLong();
            throw new ArgumentOutOfRangeException("Given value was too long to be for a TimeSpan value.");
        }
        _ticks = totalMicroseconds * TicksPerMicrosecond;
    }

    /**
     * Gets the number of ticks that represent the value of the current {@link TimeSpan} structure.
     * @return The number of ticks contained in this instance.
     */
    public long GetTicks() { return _ticks; }

    /**
     * Gets the days component of the time interval represented by the current {@link TimeSpan} structure.
     * @return The day component of this instance. The return value can be positive or negative.
     */
    public int GetDays() { return (int)(_ticks / TicksPerDay); }

    /**
     * Gets the hours component of the time interval represented by the current {@link TimeSpan} structure.
     * @return The hour component of the current {@link TimeSpan} structure. The return value ranges from -23 through 23.
     */
    public int GetHours() { return (int)(_ticks / TicksPerHour % HoursPerDay); }

    /**
     * Gets the milliseconds component of the time interval represented by the current {@link TimeSpan} structure.
     * @return The millisecond component of the current TimeSpan structure. The return value ranges from -999 through 999.
     */
    public int GetMilliseconds() { return (int)(_ticks / TicksPerMillisecond % MillisecondsPerSecond); }

    /**
     * Gets the microseconds component of the time interval represented by the current {@link TimeSpan} structure.
     * @apiNote The {@link #GetMicroseconds()} method represents whole microseconds, whereas the {@link #GetTotalMicroseconds()} method represents whole and fractional microseconds.
     */
    public int GetMicroseconds() { return (int)(_ticks / TicksPerMicrosecond % MicrosecondsPerMillisecond); }

    /**
     * Gets the nanoseconds component of the time interval represented by the current {@link TimeSpan} structure.
     * @apiNote The {@link #GetNanoseconds()} method represents whole nanoseconds, whereas the {@link #GetTotalNanoseconds()} method represents whole and fractional nanoseconds.
     */
    public int GetNanoseconds() { return (int)(_ticks % TicksPerMicrosecond * NanosecondsPerTick); }

    /**
     * Gets the minutes component of the time interval represented by the current {@link TimeSpan} structure.
     * @return The minute component of the current {@link TimeSpan} structure. The return value ranges from -59 through 59.
     */
    public int GetMinutes() { return (int)(_ticks / TicksPerMinute % MinutesPerHour); }

    /**
     * Gets the seconds component of the time interval represented by the current {@link TimeSpan} structure.
     * @return The second component of the current {@link TimeSpan} structure. The return value ranges from -59 through 59.
     */
    public int GetSeconds() { return (int)(_ticks / TicksPerSecond % SecondsPerMinute); }

    /**
     * Gets the value of the current {@link TimeSpan} structure expressed in whole and fractional days.
     * @return The total number of days represented by this instance.
     */
    public double GetTotalDays() { return (double)_ticks / TicksPerDay; }

    /**
     * Gets the value of the current {@link TimeSpan} structure expressed in whole and fractional hours.
     * @return The total number of hours represented by this instance.
     */
    public double GetTotalHours() { return (double)_ticks / TicksPerHour; }

    /**
     * Gets the value of the current {@link TimeSpan} structure expressed in whole and fractional milliseconds.
     * @return The total number of milliseconds represented by this instance.
     */
    public double GetTotalMilliseconds()
    {
        double temp = (double)_ticks / TicksPerMillisecond;

        return (temp > MaxMilliseconds) ? MaxMilliseconds : ((temp < MinMilliseconds) ? MinMilliseconds : temp);
    }

    /**
     * Gets the value of the current {@link TimeSpan} structure expressed in whole and fractional microseconds.
     * @apiNote This method converts the value of this instance from ticks to microseconds.
     * This number might include whole and fractional microseconds. <br /> <br />
     *
     * The {@link #GetTotalMicroseconds()} method represents whole and fractional microseconds,
     * whereas the {@link #GetMicroseconds()} method represents whole microseconds.
     */
    public double GetTotalMicroseconds() { return (double)_ticks / TicksPerMicrosecond; }

    /**
     * Gets the value of the current {@link TimeSpan} structure expressed in whole and fractional nanoseconds.
     * @apiNote This method converts the value of this instance from ticks to nanoseconds.
     * This number might include whole and fractional nanoseconds. <br /> <br />
     *
     * The {@link #GetTotalNanoseconds()} method represents whole and fractional nanoseconds,
     * whereas the {@link #GetNanoseconds()} method represents whole nanoseconds.
     */
    public double GetTotalNanoseconds() { return (double)_ticks * NanosecondsPerTick; }

    /**
     * Gets the value of the current {@link TimeSpan} structure expressed in whole and fractional minutes.
     * @return The total number of minutes represented by this instance.
     */
    public double GetTotalMinutes() { return (double)_ticks / TicksPerMinute; }

    /**
     * Gets the value of the current {@link TimeSpan} structure expressed in whole and fractional seconds.
     * @return The total number of seconds represented by this instance.
     */
    public double GetTotalSeconds() { return (double)_ticks / TicksPerSecond; }

    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    static long TimeToTicks(int hour, int minute, int second)
    {
        // totalSeconds is bounded by 2^31 * 2^12 + 2^31 * 2^8 + 2^31,
        // which is less than 2^44, meaning we won't overflow totalSeconds.
        long totalSeconds = (hour * SecondsPerHour)
                + (minute * SecondsPerMinute)
                + second;

        if ((totalSeconds > MaxSeconds) || (totalSeconds < MinSeconds))
        {
            // ThrowHelper.ThrowArgumentOutOfRange_TimeSpanTooLong();
            throw new ArgumentOutOfRangeException("Given value was too long to be for a TimeSpan value.");
        }
        return totalSeconds * TicksPerSecond;
    }

    public TimeSpan Add(TimeSpan ts)
            throws ArgumentOutOfRangeException
    {
        long result = _ticks + ts._ticks;
        long t1Sign = _ticks >> 63;

        if ((t1Sign == (ts._ticks >> 63)) && (t1Sign != (result >> 63)))
        {
            // Overflow if signs of operands was identical and result's sign was opposite.
            // >> 63 gives the sign bit (either 64 1's or 64 0's).
            // ThrowHelper.ThrowOverflowException_TimeSpanTooLong();
            throw new ArgumentOutOfRangeException("Given value was too long to be for a TimeSpan value.");
        }
        return new TimeSpan(result);
    }

    public boolean Equals(TimeSpan obj) {
        return Equals(this, obj);
    }

    public static boolean Equals(TimeSpan t1, TimeSpan t2) {
        return t1._ticks == t2._ticks;
    }

    public int GetHashCode() {
        return Long.hashCode(_ticks);
    }

    @MethodImpl(GetValue = MethodImplOptions.AggressiveInlining)
    private static TimeSpan FromUnits(long units, long ticksPerUnit, long minUnits, long maxUnits)
        throws ArgumentOutOfRangeException
    {
        // System.Diagnostics.Debug.Assert(minUnits < 0);
        assert minUnits < 0;
        // System.Diagnostics.Debug.Assert(maxUnits > 0);
        assert maxUnits > 0;

        if (units > maxUnits || units < minUnits)
        {
            // ThrowHelper.ThrowArgumentOutOfRange_TimeSpanTooLong();
            throw new ArgumentOutOfRangeException("Given value was too long to be for a TimeSpan value.");
        }
        return TimeSpan.FromTicks(units * ticksPerUnit);
    }

    /**
     * Initializes a new instance of the {@link TimeSpan} structure to a specified number of days.
     * @param days Number of days.
     * @return A {@link TimeSpan} that represents a specified number of days.
     * @throws ArgumentOutOfRangeException The parameters specify a {@link TimeSpan} value less than {@link #MinValue} or greater than {@link #MaxValue}.
     */
    public static TimeSpan FromDays(int days)
        throws ArgumentOutOfRangeException
    {
        return FromUnits(days, TicksPerDay, MinDays, MaxDays);
    }

    /**
     * Initializes a new instance of the {@link TimeSpan} structure to a specified number of hours.
     * @param hours Number of hours.
     * @return A {@link TimeSpan} that represents a specified number of hours.
     * @throws ArgumentOutOfRangeException The parameters specify a {@link TimeSpan} value less than {@link #MinValue} or greater than {@link #MaxValue}
     */
    public static TimeSpan FromHours(int hours)
        throws ArgumentOutOfRangeException
    {
        return FromUnits(hours, TicksPerHour, MinHours, MaxHours);
    }

    /**
     * Initializes a new instance of the {@link TimeSpan} structure to a specified number of minutes.
     * @param minutes Number of minutes.
     * @return A {@link TimeSpan} that represents a specified number of minutes.
     * @throws ArgumentOutOfRangeException The parameters specify a {@link TimeSpan} value less than {@link #MinValue} or greater than {@link #MaxValue}
     */
    public static TimeSpan FromMinutes(long minutes)
        throws ArgumentOutOfRangeException
    {
        return FromUnits(minutes, TicksPerMinute, MinMinutes, MaxMinutes);
    }

    /**
     * Initializes a new instance of the {@link TimeSpan} structure to a specified number of seconds.
     * @param seconds Number of seconds.
     * @return A {@link TimeSpan} that represents a specified number of seconds.
     * @throws ArgumentOutOfRangeException The parameters specify a {@link TimeSpan} value less than {@link #MinValue} or greater than {@link #MaxValue}
     */
    public static TimeSpan FromSeconds(long seconds)
        throws ArgumentOutOfRangeException
    {
        return FromUnits(seconds, TicksPerSecond, MinSeconds, MaxSeconds);
    }

    /**
     * Initializes a new instance of the {@link TimeSpan} structure to a specified number of milliseconds.
     * @param milliseconds Number of milliseconds.
     * @return A {@link TimeSpan} that represents a specified number of milliseconds.
     * @throws ArgumentOutOfRangeException The parameters specify a {@link TimeSpan} value less than {@link #MinValue} or greater than {@link #MaxValue}
     */
    public static TimeSpan FromMilliseconds(long milliseconds)
        throws ArgumentOutOfRangeException
    {
        return FromUnits(milliseconds, TicksPerMillisecond, MinMilliseconds, MaxMilliseconds);
    }

    /**
     * Initializes a new instance of the {@link TimeSpan} structure to a specified number of microseconds.
     * @param microseconds Number of microseconds.
     * @return A {@link TimeSpan} that represents a specified number of microseconds.
     * @throws ArgumentOutOfRangeException The parameters specify a {@link TimeSpan} value less than {@link #MinValue} or greater than {@link #MaxValue}
     */
    public static TimeSpan FromMicroseconds(long microseconds)
        throws ArgumentOutOfRangeException
    {
        return FromUnits(microseconds, TicksPerMicrosecond, MinMicroseconds, MaxMicroseconds);
    }

    /**
     * Returns a {@link TimeSpan} that represents a specified number of hours, where the specification is accurate to the nearest millisecond.
     * @param value A number of hours accurate to the nearest millisecond.
     * @return An object that represents {@code value}.
     * @throws ArgumentException {@code value} is equal to NaN.
     * @throws OverflowException {@code value} is less than {@link #MinValue} or greater than {@link #MaxValue}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code value} is {@link Double#POSITIVE_INFINITY}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code value} is {@link Double#NEGATIVE_INFINITY}.
     */
    public static TimeSpan FromHours(double value)
            throws ArgumentException, OverflowException
    {
        return Interval(value, TicksPerHour);
    }

    private static TimeSpan Interval(double value, double scale)
    {
        if (Double.isNaN(value)) {
            // ThrowHelper.ThrowArgumentException_Arg_CannotBeNaN();
            throw new ArgumentException("Value cannot be the NaN value.");
        } else {
            return IntervalFromDoubleTicks(value * scale);
        }
    }

    private static TimeSpan IntervalFromDoubleTicks(double ticks)
    {
        if ((ticks > MaxTicks) || (ticks < MinTicks) || Double.isNaN(ticks))
        {
            // ThrowHelper.ThrowOverflowException_TimeSpanTooLong();
            throw new ArgumentOutOfRangeException("Given value was too long to be for a TimeSpan value.");
        } else {
            return ticks == MaxTicks ? MaxValue : new TimeSpan((long)ticks);
        }
    }

    /**
     * Returns a {@link TimeSpan} that represents a specified number of milliseconds.
     * @param value A number of milliseconds.
     * @return An object that represents {@code value}.
     * @throws ArgumentException {@code value} is equal to NaN.
     * @throws OverflowException {@code value} is less than {@link #MinValue} or greater than {@link #MaxValue}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code value} is {@link Double#POSITIVE_INFINITY}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code value} is {@link Double#NEGATIVE_INFINITY}.
     */
    public static TimeSpan FromMilliseconds(double value)
            throws ArgumentException, OverflowException
    {
        return Interval(value, TicksPerMicrosecond);
    }

    /**
     * Returns a {@link TimeSpan} that represents a specified number of microseconds.
     * @param value A number of microseconds.
     * @return An object that represents {@code value}.
     * @throws ArgumentException {@code value} is equal to {@link Double#NaN}.
     * @throws OverflowException {@code value} is less than {@link #MinValue} or greater than {@link #MaxValue}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code value} is {@link Double#POSITIVE_INFINITY}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code value} is {@link Double#NEGATIVE_INFINITY}.
     */
    public static TimeSpan FromMicroseconds(double value)
            throws ArgumentException, OverflowException
    {
        return Interval(value, TicksPerMicrosecond);
    }

    /**
     * Returns a {@link TimeSpan} that represents a specified number of minutes, where the specification is accurate to the nearest millisecond.
     * @param value Number of minutes.
     * @return An object that represents {@code value}.
     * @throws ArgumentException {@code value} is equal to {@link Double#NaN}.
     * @throws OverflowException {@code value} is less than {@link #MinValue} or greater than {@link #MaxValue}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code value} is {@link Double#POSITIVE_INFINITY}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code value} is {@link Double#NEGATIVE_INFINITY}.
     */
    public static TimeSpan FromMinutes(double value)
            throws ArgumentException, OverflowException
    {
        return Interval(value, TicksPerMinute);
    }

    /**
     * Returns a new {@link TimeSpan} object whose value is the negated value of this instance.
     * @return A new object with the same numeric value as this instance, but with the opposite sign.
     */
    public TimeSpan Negate() {
        if (_ticks == MinTicks) {
            // ThrowHelper.ThrowOverflowException_NegateTwosCompNum();
            throw new OverflowException("Negated result is overflown.");
        } else {
            return new TimeSpan(-_ticks);
        }
    }

    /**
     * Returns a {@link TimeSpan} that represents a specified number of seconds, where the specification is accurate to the nearest millisecond.
     * @param value A number of seconds, accurate to the nearest millisecond.
     * @return An object that represents {@code value}.
     * @throws ArgumentException {@code value} is equal to {@link Double#NaN}.
     * @throws OverflowException {@code value} is less than {@link #MinValue} or greater than {@link #MaxValue}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code value} is {@link Double#POSITIVE_INFINITY}. <br /> <br />
     *
     * -or- <br /> <br />
     *
     * {@code value} is {@link Double#NEGATIVE_INFINITY}.
     */
    public static TimeSpan FromSeconds(double value)
        throws ArgumentException, OverflowException
    {
        return Interval(value, TicksPerSecond);
    }

    /**
     * Returns a new {@link TimeSpan} object whose value is the difference between the specified {@link TimeSpan} object and this instance.
     * @param ts The time interval to be subtracted.
     * @return A new time interval whose value is the result of the value of this instance minus the value of {@code ts}.
     */
    public TimeSpan Subtract(TimeSpan ts) {
        return new TimeSpan(_ticks - ts._ticks);
    }

    /**
     * Returns a new {@link TimeSpan} object which value is the result of multiplication of this instance and the specified factor.
     * @param factor The value to be multiplied by.
     * @return A new object that represents the value of this instance multiplied by the value of {@code factor}.
     */
    public TimeSpan Multiply(double factor) {
        if (Double.isNaN(factor)) {
            // ThrowHelper.ThrowArgumentException_Arg_CannotBeNaN(ExceptionArgument.factor);
            throw new ArgumentException("Argument cannot be NaN.", "factor");
        }

        // Rounding to the nearest tick is as close to the result we would have with unlimited
        // precision as possible, and so likely to have the least potential to surprise.
        return IntervalFromDoubleTicks(Math.round(_ticks * factor));
    }

    /**
     * Returns a new {@link TimeSpan} object whose value is the result of dividing this instance by the specified divisor.
     * @param divisor The divisor or value to be divided by.
     * @return A new object that represents the value of this instance divided by the value of {@code divisor}.
     */
    public TimeSpan Divide(double divisor) {
        if (Double.isNaN(divisor)) {
            // ThrowHelper.ThrowArgumentException_Arg_CannotBeNaN(ExceptionArgument.factor);
            throw new ArgumentException("Argument cannot be NaN.", "divisor");
        }

        // Rounding to the nearest tick is as close to the result we would have with unlimited
        // precision as possible, and so likely to have the least potential to surprise.
        return IntervalFromDoubleTicks(Math.round(_ticks / divisor));
    }

    /**
     * Returns a new {@link Double} value that's the result of dividing this instance by {@code ts}.
     * @param ts The value to be divided by.
     * @return A new value that represents result of dividing this instance by the value of {@code ts}.
     */
    public double Divide(TimeSpan ts) {
        return _ticks / (double)ts._ticks;
    }

    /**
     * Returns a {@link TimeSpan} that represents a specified time, where the specification is in units of ticks.
     * @param value A number of ticks that represent a time.
     * @return An object that represents {@code value}.
     */
    public static TimeSpan FromTicks(long value)
    {
        return (value == 0) ? Zero : (
                (value == MaxTicks) ? MaxValue : (
                        (value == MinTicks) ? MinValue : new TimeSpan(value)
                )
        );
    }

    /**
     * Converts the value of the current {@link TimeSpan} object to its equivalent string representation.
     * @return The string representation of the current {@link TimeSpan} value.
     */
    @Override
    public String ToString() {
        // See more info at https://learn.microsoft.com/en-us/dotnet/api/system.timespan.tostring?view=net-9.0 why this is implemented this way.
        // This is the 'c' format specifier.
        StringBuilder sb = new StringBuilder(20);
        if (_ticks < 0) {
            sb.append('-');
        }
        int days = GetDays();
        if (days > 0) {
            sb.append(String.format("%02d." , days));
        }
        sb.append(String.format("%02d:%02d:%02d", GetHours(), GetMinutes(), GetSeconds()));
        double ts = GetTotalSeconds();
        if ((ts = ts - (long)ts) > 0) {
            sb.append(String.format(".%07f" , ts));
        }
        return sb.toString();
    }


    /**
     * Compares this instance to a specified {@link TimeSpan} object and returns an integer that indicates whether this instance is shorter than, equal to, or longer than the {@link TimeSpan} object.
     * @param other An object to compare to this instance.
     * @return The value {@code 0} if {@code x == y};
     *         a value less than {@code 0} if {@code x < y}; and
     *         a value greater than {@code 0} if {@code x > y}.
     */
    public int CompareTo(TimeSpan other) {
        return Long.compare(_ticks , other._ticks);
    }

    /**
     * Compares this instance to a specified object or {@link TimeSpan} object and returns an integer that indicates whether this instance is shorter than, equal to, or longer than the specified object or TimeSpan object.
     * @param value An object to compare, or {@code null}.
     * @return A value ranging from -1 to 1, inclusive. 0 means that both instances are equal. 1 is returned if {@code value} is {@code null}.
     */
    public int CompareTo(Object value)
    {
        if (value == null) {
            return 1;
        } else if (value instanceof TimeSpan other) {
            return (_ticks == other._ticks) ? 0 : ((_ticks > other._ticks) ? 1 : -1);
        } else {
            throw new ArgumentException("Argument must be another TimeSpan structure.");
        }
    }

    /**
     * Compares two {@link TimeSpan} values and returns an integer that indicates whether the first value is shorter than, equal to, or longer than the second value.
     * @param t1 The first time interval to compare.
     * @param t2 The second time interval to compare.
     * @return The value {@code 0} if {@code x == y};
     *         the value {@code -1} if {@code x < y}; and
     *         the value {@code 1} if {@code x > y}.
     */
    public static int Compare(TimeSpan t1, TimeSpan t2) {
        return (t1._ticks == t2._ticks) ? 0 : ((t1._ticks > t2._ticks) ? 1 : -1);
    }
}
