package io.github.hugo1307.fallgates.data.domain;

import io.github.hugo1307.fallgates.data.models.FallModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter
@Setter
@AllArgsConstructor
public class Fall implements DomainEntity<FallModel> {

    private Long id;
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
        this.id = id;
        this.name = name;
        this.position = position;
        this.material = material;
        this.xSize = xSize;
        this.zSize = zSize;
    }

    @Override
    public FallModel toDataModel() {
        return new FallModel(id, name, position.toDataModel(), material, xSize, zSize);
    }

}
