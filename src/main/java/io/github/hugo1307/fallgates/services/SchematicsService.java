package io.github.hugo1307.fallgates.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import io.github.hugo1307.fallgates.FallGates;
import io.github.hugo1307.fallgates.data.domain.FallGateSchematic;
import io.github.hugo1307.fallgates.exceptions.SchematicPasteException;
import io.github.hugo1307.fallgates.exceptions.SchematicReadException;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

@Singleton
public final class SchematicsService {

    private final FallGates plugin;

    @Inject
    public SchematicsService(FallGates plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads a schematic file into a {@link FallGateSchematic} object.
     *
     * @param schematicName the name of the schematic to load
     * @return the loaded {@link FallGateSchematic}
     */
    public FallGateSchematic loadSchematic(String schematicName) {
        FallGateSchematic schematic = new FallGateSchematic(schematicName);

        File schematicFile = Path.of(plugin.getDataFolder().getPath(), "schematics", schematic.getFileName()).toFile();
        ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
        if (format == null) {
            throw new SchematicReadException(String.format("Unsupported schematic format for file %s.", schematicFile.getName()));
        }
        try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
            schematic.setSchematicClipboard(reader.read());
            return schematic;
        } catch (IOException e) {
            throw new SchematicReadException(String.format("Failed to read the schematic file %s.", schematicFile.getName()), e);
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
            Operation operation = new ClipboardHolder(schematic.getSchematicClipboard())
                    .createPaste(editSession)
                    .to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
                    .ignoreAirBlocks(true)
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            throw new SchematicPasteException(String.format("Failed to paste the Schematic %s at location %s.", schematic.getName(), location), e);
        }

    }

}
