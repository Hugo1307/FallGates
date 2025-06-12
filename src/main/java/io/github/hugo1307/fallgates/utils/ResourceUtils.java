package io.github.hugo1307.fallgates.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.logging.Level;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResourceUtils {

    /**
     * Copies a resource from the plugin's jar to a specified file on the filesystem.
     *
     * @param plugin       the JavaPlugin instance
     * @param resourcePath the path to the resource within the plugin's jar
     * @param outFile      the file to which the resource should be copied
     */
    public static void copyResource(JavaPlugin plugin, String resourcePath, File outFile) {
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) {
                plugin.getLogger().log(Level.WARNING, "Could not find resource {0}", resourcePath);
                return;
            }

            try (OutputStream out = new FileOutputStream(outFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error copying resource", e);
        }
    }

}
