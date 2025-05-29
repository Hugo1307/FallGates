package io.github.hugo1307.fallgates.data.models;

import io.github.hugo1307.fallgates.data.domain.Fall;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "falls")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FallModel implements DataModel<Long, Fall> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "position_id", nullable = false)
    private PositionModel position;

    @Column(name = "x_size", nullable = false)
    private int xSize;

    @Column(name = "z_size", nullable = false)
    private int zSize;

    @Override
    public Fall toDomainEntity() {
        return new Fall(id, name, position.toDomainEntity(), xSize, zSize);
    }

}
