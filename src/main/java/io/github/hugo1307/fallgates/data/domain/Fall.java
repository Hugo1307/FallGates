package io.github.hugo1307.fallgates.data.domain;

import io.github.hugo1307.fallgates.data.models.FallModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

@Getter
@Setter
@AllArgsConstructor
public class Fall implements DomainEntity<FallModel> {

    private Long id;
    private Long targetFallId;
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

    public Fall(Long id, String name, Position position, Material material, int xSize, int zSize) {
        this(id, null, name, position, material, xSize, zSize, null, false);
    }

    public Fall(Long id, Long targetFallId, String name, Position position, Material material, int xSize, int zSize) {
        this(id, targetFallId, name, position, material, xSize, zSize, null, false);
    }

    /**
     * Check if the fall is connected to another fall.
     *
     * @return true if the fall is connected to another fall, false otherwise.
     */
    public boolean isConnected() {
        return targetFallId != null && targetFallId > 0;
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
