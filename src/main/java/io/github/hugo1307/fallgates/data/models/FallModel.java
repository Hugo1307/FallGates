package io.github.hugo1307.fallgates.data.models;

import io.github.hugo1307.fallgates.data.domain.Fall;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;

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

    @Column(name = "target_fall_id")
    private Long targetFallId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "position_id", nullable = false)
    private PositionModel position;

    @Enumerated(EnumType.STRING)
    @Column(name = "material", nullable = false)
    private Material material;

    @Column(name = "x_size", nullable = false)
    private Integer xSize;

    @Column(name = "z_size", nullable = false)
    private Integer zSize;

    @Override
    public Fall toDomainEntity() {
        return new Fall(id, targetFallId, name, position.toDomainEntity(), material, xSize, zSize);
    }

}
