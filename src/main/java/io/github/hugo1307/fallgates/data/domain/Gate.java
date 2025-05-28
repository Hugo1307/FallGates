package io.github.hugo1307.fallgates.data.domain;

import io.github.hugo1307.fallgates.data.models.GateModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Gate implements DomainEntity<GateModel> {

    private Long id;
    private String name;
    private Position minPosition;
    private Position maxPosition;

    @Override
    public GateModel toDataModel() {
        return new GateModel(id, name, minPosition.toDataModel(), maxPosition.toDataModel());
    }

}
