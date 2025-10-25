package com.github.mdcdi1315.DotNetLayer.System;

/**
 * Represents the version number of an assembly, operating system, or the Java runtime. <br />
 * This class cannot be inherited.
 */
public final class Version
     implements ICloneable, IEquatable<Version>, IComparable<Version>
{
    private int major, minor, build, revision;

    /**
     * Initializes a new instance of the {@link Version} class.
     */
    public Version()
    {
        major = 0;
        minor = 0;
        build = -1;
        revision = -1;
    }

    /**
     * Initializes a new instance of the {@link Version} class using the specified major and minor values.
     * @param major The major version number.
     * @param minor The minor version number.
     * @throws ArgumentOutOfRangeException {@code major} or {@code minor} is less than zero.
     */
    public Version(int major, int minor)
            throws ArgumentOutOfRangeException
    {
        if (major < 0) {
            throw new ArgumentOutOfRangeException("major", "Major version number is negative.");
        }
        if (minor < 0) {
            throw new ArgumentOutOfRangeException("minor", "Minor version number is negative.");
        }

        this.major = major;
        this.minor = minor;
        this.build = -1;
        this.revision = -1;
    }

    /**
     * Initializes a new instance of the {@link Version} class using the specified major, minor, and build values.
     * @param major The major version number.
     * @param minor The minor version number.
     * @param build The build number.
     * @throws ArgumentOutOfRangeException {@code major}, {@code minor} or {@code build} is less than zero.
     */
    public Version(int major, int minor, int build)
            throws ArgumentOutOfRangeException
    {
        if (major < 0) {
            throw new ArgumentOutOfRangeException("major", "Major version number is negative.");
        }
        if (minor < 0) {
            throw new ArgumentOutOfRangeException("minor", "Minor version number is negative.");
        }
        if (build < 0) {
            throw new ArgumentOutOfRangeException("build", "Build number is negative.");
        }

        this.major = major;
        this.minor = minor;
        this.build = build;
        this.revision = -1;
    }

    /**
     * Initializes a new instance of the {@link Version} class with the specified major, minor, build, and revision numbers.
     * @param major The major version number.
     * @param minor The minor version number.
     * @param build The build number.
     * @param revision The revision number.
     * @throws ArgumentOutOfRangeException {@code major}, {@code minor}, {@code build} or {@code revision} is less than zero.
     */
    public Version(int major, int minor, int build, int revision)
        throws ArgumentOutOfRangeException
    {
        if (major < 0) {
            throw new ArgumentOutOfRangeException("major", "Major version number is negative.");
        }
        if (minor < 0) {
            throw new ArgumentOutOfRangeException("minor", "Minor version number is negative.");
        }
        if (build < 0) {
            throw new ArgumentOutOfRangeException("build", "Build number is negative.");
        }
        if (revision < 0) {
            throw new ArgumentOutOfRangeException("revision", "Revision number is negative.");
        }

        this.major = major;
        this.minor = minor;
        this.build = build;
        this.revision = revision;
    }

    /**
     * Gets the value of the build component of the version number for the current {@link Version} object.
     * @return The build number, or -1 if the build number is undefined.
     */
    public int Build() {
        return build;
    }

    /**
     * Gets the value of the revision component of the version number for the current {@link Version} object.
     * @return The revision number, or -1 if the revision number is undefined.
     */
    public int Revision() {
        return revision;
    }

    /**
     * Gets the value of the major component of the version number for the current {@link Version} object.
     * @return The major version number.
     */
    public int Major() {
        return major;
    }

    /**
     * Gets the value of the minor component of the version number for the current {@link Version} object.
     * @return The minor version number.
     */
    public int Minor() {
        return minor;
    }

    /**
     * Gets the high 16 bits of the revision number.
     * @return A 16-bit signed integer.
     */
    public short MajorRevision() {
        return (short) (revision >> 16);
    }

    /**
     * Gets the low 16 bits of the revision number.
     * @return A 16-bit signed integer.
     */
    public short MinorRevision() {
        return (short) (revision & 0xFFFF);
    }

    @Override
    public Object Clone() {
        boolean revisionisundefined = revision == -1;
        if (build == -1 && revisionisundefined) {
            return new Version(major, minor);
        } else if (revisionisundefined) {
            return new Version(major, minor, build);
        } else {
            return new Version(major, minor, build, revision);
        }
    }

    @Override
    public int CompareTo(Version value) {
        return
                value == this ? 0 :
                        value == null ? 1 :
                major != value.major ? (major > value.major ? 1 : -1) :
                        minor != value.minor ? (minor > value.minor ? 1 : -1) :
                                build != value.build ? (build > value.build ? 1 : -1) :
                                        revision != value.revision ? (revision > value.revision ? 1 : -1) :
                                                0;
    }

    @Override
    public boolean Equals(Version obj) {
        return obj == this || (obj != null &&
                major == obj.major &&
                minor == obj.minor &&
                build == obj.build &&
                revision == obj.revision);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Version v) {
            return Equals(v);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        // Let's assume that most version numbers will be pretty small and just
        // OR some lower order bits together.

        int accumulator = 0;

        accumulator |= (major & 0x0000000F) << 28;
        accumulator |= (minor & 0x000000FF) << 20;
        accumulator |= (build & 0x000000FF) << 12;
        accumulator |= (revision & 0x00000FFF);

        return accumulator;
    }

    /**
     * Converts the value of the current {@link Version} object to its equivalent {@link String} representation.
     * A specified count indicates the number of components to return.
     * @param fieldCount The number of components to return. The {@code fieldCount} ranges from 0 to 4.
     * @return The {@link String} representation of the values of the major, minor, build, and revision components of the current {@link Version} object, each separated by a period character ('.').
     * The {@code fieldCount} parameter determines how many components are returned.
     * @throws ArgumentException {@code fieldCount} is less than 0, or more than 4. <br />-or-<br />
     * {@code fieldCount} is more than the number of components defined in the current Version object.
     */
    public String ToString(int fieldCount)
            throws ArgumentException
    {
        switch (fieldCount) {
            case 0 -> {
                return "";
            }
            case 1 -> {
                return Integer.toString(major);
            }
            case 2 -> {
                return String.format("%d.%d", major, minor);
            }
            case 3 -> {
                if (revision > -1) {
                    throw new ArgumentException("fieldCount is more than the number of components defined in the current Version object.", "fieldCount");
                } else {
                    return String.format("%d.%d.%d", major, minor, build);
                }
            }
            case 4 -> {
                return String.format("%d.%d.%d.%d", major, minor, build, revision);
            }
            default -> throw new ArgumentException("The number of fields requested was not in the range [0..4].");
        }
    }

    /**
     * Converts the value of the current {@link Version} object to its equivalent {@link String} representation.
     * @return The {@link String} representation of the values of the major, minor, build, and revision components of the current {@link Version} object, as depicted in the following format. <br />
     * Each component is separated by a period character ('.').
     * Square brackets ('[' and ']') indicate a component that will not appear in the return value if the component is not defined: <br /> <br />
     * major.minor[.build[.revision]]
     */
    public String toString()
    {
        boolean revisionisundefined = revision == -1;
        if (build == -1 && revisionisundefined) {
            return ToString(2);
        } else if (revisionisundefined) {
            return ToString(3);
        } else {
            return ToString(4);
        }
    }

    /**
     * Converts the string representation of a version number to an equivalent {@link Version} object.
     * @param input A string that contains a version number to convert.
     * @return An object that is equivalent to the version number specified in the {@code input} parameter.
     * @throws ArgumentNullException {@code input} is {@code null}.
     * @throws ArgumentException {@code input} has fewer than two or more than four version components.
     * @throws FormatException At least one component in {@code input} is not an integer.
     * @throws ArgumentOutOfRangeException At least one component in {@code input} is less than zero.
     */
    public static Version Parse(String input)
            throws ArgumentNullException, ArgumentException, FormatException, ArgumentOutOfRangeException
    {
        ArgumentNullException.ThrowIfNullOrEmpty(input);
        int index_of_minor_dot_sep = input.indexOf('.');
        if (index_of_minor_dot_sep == -1) {
            throw new ArgumentException("The input parameter has fewer than two version components.", "input");
        }
        Version v = new Version();
        int next_index;
        try {
            v.major = Integer.parseInt(input.substring(0, index_of_minor_dot_sep));
        } catch (NumberFormatException e) {
            throw new FormatException("Major component was not an integer.");
        }
        if (v.major < 0) {
            throw new ArgumentOutOfRangeException("The major component was a negative integer.");
        }
        next_index = input.indexOf('.' , index_of_minor_dot_sep+1);
        try {
            v.minor = Integer.parseInt(input.substring(index_of_minor_dot_sep+1, next_index == -1 ? input.length() : next_index));
        } catch (NumberFormatException e) {
            throw new FormatException("Minor component was not an integer.");
        }
        if (v.minor < 0) {
            throw new ArgumentOutOfRangeException("The minor component was a negative integer.");
        }
        if (next_index > 0) {
            int i = next_index+1;
            next_index = input.indexOf('.' , i);
            try {
                if ((v.build = Integer.parseInt(input.substring(i, next_index == -1 ? input.length() : next_index))) < 0) {
                    throw new ArgumentOutOfRangeException("The build component was a negative integer.");
                }
            } catch (NumberFormatException e) {
                throw new FormatException("Build component was not an integer.");
            }
            if (next_index > 0) {
                if (input.indexOf('.' , next_index+1) > 0) {
                    throw new ArgumentException("Too many version components of what a Version string should be.");
                }
                try {
                    if ((v.revision = Integer.parseInt(input.substring(next_index+1))) < 0) {
                        throw new ArgumentOutOfRangeException("The revision component was a negative integer.");
                    }
                } catch (NumberFormatException e) {
                    throw new FormatException("Revision component was not an integer.");
                }
            }
        }
        return v;
    }
}
