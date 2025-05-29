package io.github.hugo1307.fallgates.data.domain;

import io.github.hugo1307.fallgates.data.models.FallModel;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Fall implements DomainEntity<FallModel> {

    private Long id;
    private String name;
    private Position position;
    private int xSize;
    private int zSize;

    @Transient
    private FallGateSchematic schematic;

    public Fall(Long id, String name, Position position, int xSize, int zSize) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.xSize = xSize;
        this.zSize = zSize;
    }

    @Override
    public FallModel toDataModel() {
        return new FallModel(id, name, position.toDataModel(), xSize, zSize);
    }

}
