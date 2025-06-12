package io.github.hugo1307.fallgates.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import io.github.hugo1307.fallgates.FallGates;
import io.github.hugo1307.fallgates.data.domain.FallGateSchematic;
import io.github.hugo1307.fallgates.exceptions.SchematicException;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public final class SchematicsService implements Service {

    public static final Path SCHEMATICS_PATH = Path.of("schematics", "falls");
    public static final Path TERRAIN_BACKUP_PATH = Path.of("schematics", "terrain");

    private final FallGates plugin;

    @Inject
    public SchematicsService(FallGates plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks if a schematic with the given name is available in the "schematics" folder.
     *
     * @param schematicName the name of the schematic to check
     * @return true if the schematic is available, false otherwise
     */
    public boolean isSchematicAvailable(String schematicName) {
        if (schematicName == null || schematicName.isEmpty()) {
            return false; // Invalid schematic name
        }

        File schematicsFolder = new File(plugin.getDataFolder(), SCHEMATICS_PATH.toString());
        if (!schematicsFolder.exists() || !schematicsFolder.isDirectory()) {
            return false; // Schematics folder does not exist
        }

        File schematicFile = new File(schematicsFolder, schematicName + ".schem");
        return schematicFile.exists() && schematicFile.isFile(); // Check if the file exists and is a file
    }

    /**
     * Checks if a backup schematic with the given name is available in the "schematics" folder.
     *
     * @param schematicName the name of the schematic to check
     * @return true if the schematic is available, false otherwise
     */
    public boolean isBackupAvailable(String schematicName) {
        if (schematicName == null || schematicName.isEmpty()) {
            return false; // Invalid schematic name
        }

        File schematicsFolder = new File(plugin.getDataFolder(), TERRAIN_BACKUP_PATH.toString());
        if (!schematicsFolder.exists() || !schematicsFolder.isDirectory()) {
            return false; // Schematics folder does not exist
        }

        File schematicFile = new File(schematicsFolder, schematicName + ".schem");
        return schematicFile.exists() && schematicFile.isFile(); // Check if the file exists and is a file
    }

    /**
     * Loads a schematic file into a {@link FallGateSchematic} object.
     *
     * @param schematicPath the path to the schematic file, relative to the plugin's data folder
     * @return the loaded {@link FallGateSchematic}
     * @throws SchematicException if the schematic file cannot be read or is in an unsupported format
     */
    public FallGateSchematic loadSchematic(Path schematicPath) throws SchematicException {
        FallGateSchematic schematic = new FallGateSchematic(schematicPath.getFileName().toString());
        File schematicFile = schematicPath.toFile();
        ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
        if (format == null) {
            throw new SchematicException(String.format("Unsupported schematic format for file %s.", schematicFile.getName()));
        }

        try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
            schematic.setSchematicClipboard(reader.read());
            return schematic;
        } catch (IOException e) {
            throw new SchematicException(String.format("Failed to read the schematic file %s.", schematicFile.getName()), e);
        }
    }

    /**
     * Pastes a {@link FallGateSchematic} at the specified location in the world.
     *
     * @param schematic the schematic to paste
     * @param location  the location where the schematic should be pasted
     */
    public void pasteSchematic(FallGateSchematic schematic, Location location) {
        if (schematic.getSchematicClipboard() == null) {
            throw new IllegalStateException("Schematic clipboard is not loaded for schematic: " + schematic.getName());
        }

        if (location == null || location.getWorld() == null) {
            throw new IllegalArgumentException("Location must not be null and must have a valid world.");
        }

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(location.getWorld()))) {
            ClipboardHolder holder = new ClipboardHolder(schematic.getSchematicClipboard());
            Operation operation = holder
                    .createPaste(editSession)
                    .to(BukkitAdapter.asBlockVector(location))
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            throw new SchematicException(String.format("Failed to paste the Schematic %s at location %s.", schematic.getName(), location), e);
        }
    }

    /**
     * Gets the dimensions of the fall schematic.
     *
     * @param schematic the schematic whose dimensions are to be retrieved
     * @return the dimensions of the schematic as a {@link BlockVector3}
     * @throws IllegalStateException if the schematic clipboard is not loaded
     */
    public BlockVector3 getSchematicDimensions(FallGateSchematic schematic) {
        if (schematic.getSchematicClipboard() == null) {
            throw new IllegalStateException("Schematic clipboard is not loaded for schematic: " + schematic.getName());
        }
        return schematic.getSchematicClipboard().getDimensions();
    }

    /**
     * Saves a region defined by its origin and dimensions into a schematic file.
     *
     * @param schematicPath the path where the schematic will be saved, relative to the plugin's data folder
     * @param origin        the origin location in the world where the schematic will be saved
     * @param dimensions    the dimensions of the region to save
     */
    public void saveToSchematic(Path schematicPath, Location origin, BlockVector3 dimensions) {
        File schematicFile = schematicPath.toFile();
        BlockVector3 maxPos = BukkitAdapter.asBlockVector(origin).add(dimensions.getX() / 2, dimensions.getY(), dimensions.getZ() / 2);
        BlockVector3 minPos = BukkitAdapter.asBlockVector(origin).subtract(dimensions.getX() / 2, dimensions.getY(), dimensions.getZ() / 2);
        CuboidRegion region = new CuboidRegion(maxPos, minPos);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        clipboard.setOrigin(BukkitAdapter.asBlockVector(origin));
        World world = BukkitAdapter.adapt(origin.getWorld());

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
            Operations.complete(copy);

            try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(schematicFile))) {
                writer.write(clipboard);
            }
        } catch (IOException | WorldEditException e) {
            throw new SchematicException("Error saving schematic", e);
        }
    }

    /**
     * Deletes a schematic file.
     *
     * <p>This method will delete the specified schematic file if it exists.
     *
     * @param schematicPath the path to the schematic file to delete
     * @throws IllegalArgumentException if the schematic file does not exist
     */
    public void deleteSchematic(Path schematicPath) {
        File schematicFile = schematicPath.toFile();
        if (schematicFile.exists() && schematicFile.isFile() && !schematicFile.delete()) {
            throw new SchematicException("Failed to delete the schematic file: " + schematicFile.getAbsolutePath());
        }
    }

    /**
     * Get a list of the names of all available schematics.
     *
     * <p>This method will return the names of all files in the schematics folder without the file extension.
     *
     * @return a list of names of available schematics
     */
    public List<String> getAvailableSchematicsNames() {
        File schematicsFolder = new File(plugin.getDataFolder(), SCHEMATICS_PATH.toString());
        if (!schematicsFolder.exists() || !schematicsFolder.isDirectory()) {
            return List.of(); // Return an empty list if the folder does not exist
        }

        return Stream.of(Objects.requireNonNull(schematicsFolder.list((dir, name) -> name.endsWith(".schem"))))
                .map(name -> name.substring(0, name.lastIndexOf('.'))) // Remove the file extension
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Gets the path to the terrain backup file for a specific fall.
     *
     * @param fallName the name of the fall
     * @return the path to the terrain backup file
     */
    public Path getTerrainBackupPath(String fallName) {
        return Path.of(plugin.getDataFolder().getPath(), TERRAIN_BACKUP_PATH.toString(), fallName + ".schem");
    }

    /**
     * Gets the path to a schematic file by its name.
     *
     * @param schematicName the name of the schematic
     * @return the path to the schematic file
     */
    public Path getSchematicPath(String schematicName) {
        return Path.of(plugin.getDataFolder().getPath(), SCHEMATICS_PATH.toString(), schematicName + ".schem");
    }

}
