package io.github.hugo1307.fallgates.data.domain;

import io.github.hugo1307.fallgates.data.models.PositionModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Position implements DomainEntity<PositionModel> {

    private Long id;
    private String world;
    private double x;
    private double y;
    private double z;

    @Override
    public PositionModel toDataModel() {
        return new PositionModel(id, world, x, y, z);
    }

}
