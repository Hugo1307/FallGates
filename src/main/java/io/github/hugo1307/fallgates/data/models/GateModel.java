package io.github.hugo1307.fallgates.data.models;

import io.github.hugo1307.fallgates.data.domain.Gate;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gates")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GateModel implements DataModel<Long, Gate> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "min_position_id", nullable = false)
    private PositionModel minPosition;

    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "max_position_id", nullable = false)
    private PositionModel maxPosition;

    @Override
    public Gate toDomainEntity() {
        return new Gate(id, name, minPosition.toDomainEntity(), maxPosition.toDomainEntity());
    }

}
