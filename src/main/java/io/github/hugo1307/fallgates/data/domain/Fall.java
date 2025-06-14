package io.github.hugo1307.fallgates.data.domain;

import io.github.hugo1307.fallgates.data.models.FallModel;
import io.github.hugo1307.fallgates.utils.StringSanitizeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

@Getter
@Setter
@AllArgsConstructor
public class Fall implements DomainEntity<FallModel> {

    private String id;
    private String targetFallId;
    private String name;
    private Position position;
    private Material material;
    private int xSize;
    private int zSize;

    /**
     * Temporary data used for the fall domain logic.
     */
    private FallGateSchematic schematic;
    private boolean isOpen;

    public Fall(String id, String targetFallId, String name, Position position, Material material, int xSize, int zSize) {
        this(id, targetFallId, name, position, material, xSize, zSize, null, false);
    }

    /**
     * Create a new Fall instance with a generated ID.
     *
     * @param name     the name of the fall
     * @param position the position of the fall
     * @param material the material of the fall
     * @param xSize    the size of the fall on X axis
     * @param zSize    the size of the fall on Z axis
     * @return a new Fall instance with a generated ID
     */
    public static Fall createNew(String name, Position position, Material material, int xSize, int zSize) {
        return new Fall(StringSanitizeUtils.sanitize(name).toLowerCase(), null, name, position, material, xSize, zSize);
    }

    /**
     * Check if the fall is connected to another fall.
     *
     * @return true if the fall is connected to another fall, false otherwise.
     */
    public boolean isConnected() {
        return targetFallId != null;
    }

    /**
     * Check if a given location is inside the fall area.
     *
     * @param location the location to check
     * @return true if the location is inside the fall area, false otherwise
     */
    public boolean isInside(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }
        Location fallLocation = position.toBukkitLocation();
        return location.getWorld().equals(fallLocation.getWorld())
                && location.getBlockX() >= fallLocation.getBlockX() - xSize / 2
                && location.getBlockX() <= fallLocation.getBlockX() + xSize / 2
                && location.getBlockZ() >= fallLocation.getBlockZ() - zSize / 2
                && location.getBlockZ() <= fallLocation.getBlockZ() + zSize / 2
                && location.getBlockY() < fallLocation.getBlockY();
    }

    @Override
    public FallModel toDataModel() {
        return new FallModel(id, targetFallId, name, position.toDataModel(), material, xSize, zSize);
    }

}
