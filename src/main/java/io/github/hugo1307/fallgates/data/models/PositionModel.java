package io.github.hugo1307.fallgates.data.models;

import io.github.hugo1307.fallgates.data.domain.Position;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "positions")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PositionModel implements DataModel<Long, Position> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "world", nullable = false, length = 64)
    private String world;

    @Column(name = "x")
    private Double x;

    @Column(name = "y")
    private Double y;

    @Column(name = "z")
    private Double z;

    @Override
    public Position toDomainEntity() {
        return new Position(id, world, x, y, z);
    }

}
