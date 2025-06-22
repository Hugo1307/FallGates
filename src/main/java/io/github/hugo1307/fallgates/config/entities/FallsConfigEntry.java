package io.github.hugo1307.fallgates.config.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FallsConfigEntry implements ConfigEntry {
    CLOSE_DELAY("closeDelay"),
    FALL_HEIGHT("fallHeight"),
    VERTICAL_FORCE("verticalForce"),
    HORIZONTAL_FORCE("horizontalForce");

    private final String key;

    @Override
    public String getConfigPrefix() {
        return "falls";
    }
}
