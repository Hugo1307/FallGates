package io.github.hugo1307.fallgates.data.domain;

import io.github.hugo1307.fallgates.data.models.PositionModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Data
@AllArgsConstructor
public class Position implements DomainEntity<PositionModel> {

    private Long id;
    private String world;
    private double x;
    private double y;
    private double z;

    public static Position fromBukkitLocation(Location location) {
        return new Position(null, location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    public Location toBukkitLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    @Override
    public PositionModel toDataModel() {
        return new PositionModel(id, world, x, y, z);
    }

}
