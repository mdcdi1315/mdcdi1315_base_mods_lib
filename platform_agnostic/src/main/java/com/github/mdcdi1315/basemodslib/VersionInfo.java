package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.StringUtils;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.MaybeNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.ConstantExpected;

import com.google.common.collect.ImmutableMap;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.io.BufferedReader;

import java.util.Map;
import java.util.HashMap;

/**
 * Provides versioning information about the BML library itself.
 * @since 1.0.31
 */
public final class VersionInfo
{
    // Do not let anyone instantiate this class.
    private VersionInfo() {}

    private static Map<String, String> info_data;

    static {
        info_data = Map.of();
    }

    /**
     * Provides the version of the BML library.
     */
    public static final String PROPERTY_VERSION = "version";

    /**
     * Provides the exact timestamp when the BML library was built.
     */
    public static final String PROPERTY_BUILD_TIME = "build_time";

    /**
     * Provides the release type of the BML library.
     */
    public static final String PROPERTY_RELEASE_TYPE = "release_series";

    /**
     * Gets a loaded property by the specified key.
     * @param key The key of the property whose value to get.
     * @return The value of the specified property key.
     * If the property is not declared, this method will return {@code null}.
     */
    @MaybeNull
    public static String GetProperty(@ConstantExpected @AllowNull String key) { return info_data.get(key); }

    /**
     * Gets a loaded property by the specified key.
     * @param key The key of the property whose value to get.
     * @return The value of the specified property key.
     * If the property is not declared, this method will return an empty string.
     */
    @NotNull
    public static String GetPropertyOrEmpty(@ConstantExpected @AllowNull String key)
    {
        String value = info_data.get(key);
        return StringUtils.IsNullOrEmpty(value) ? StringUtils.Empty : value;
    }

    static void LoadProjectInfo(@AllowNull Path file_path)
    {
        if (file_path == null)
        {
            BaseModsLib.LOGGER.warn("[VersionDataLoader] The system failed to locate the versioning data file. Skipping versioning info load.");
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(file_path)) {
            String s, key, value;
            int line = 0;
            info_data = new HashMap<>();
            while ((s = reader.readLine()) != null)
            {
                line++;
                if (s.startsWith("#") || s.isBlank()) { continue; }
                int location = s.indexOf('=');
                if (location == -1) {
                    BaseModsLib.LOGGER.warn("[VersionInfoLoader] Line {} at project file info is invalid: Does not contain an equals delimiter character.", line);
                } else {
                    key = s.substring(0, location);
                    int comment_at_end = s.lastIndexOf('#');
                    if (comment_at_end == -1) {
                        value = s.substring(location + 1);
                    } else {
                        BaseModsLib.LOGGER.warn("[VersionInfoLoader] Line {} contains a comment in the end at position {}, the loader may fail to recognize the property correctly.", line, comment_at_end);
                        comment_at_end--;
                        // Now search backwards for the last character without any white space.
                        while (comment_at_end > -1)
                        {
                            char ch = s.charAt(comment_at_end);
                            if (ch == ' ' || ch == '\t') { comment_at_end--; } else { break; }
                        }
                        if (comment_at_end > location) {
                            value = s.substring(location + 1, comment_at_end + 1);
                        } else {
                            BaseModsLib.LOGGER.warn("[VersionInfoLoader] Line {} contains an invalid entry. Ignoring it.", line);
                            continue;
                        }
                    }
                    info_data.put(key, value);
                }
            }
        } catch (IOException io_ex) {
            BaseModsLib.LOGGER.warn("[VersionInfoLoader] Failed to load project info file due to I/O error. Using empty data instead.", io_ex);
        }
        BaseModsLib.LOGGER.info("[VersionInfoLoader] Finalizing project info file data.");
        info_data = ImmutableMap.copyOf(info_data);
    }
}
