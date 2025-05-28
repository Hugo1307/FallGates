package io.github.hugo1307.fallgates.config.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DatabaseConfigEntry implements ConfigEntry {
    DATABASE_NAME("name"),
    DATABASE_USER("user"),
    DATABASE_PASSWORD("password");

    private final String key;

    @Override
    public String getConfigPrefix() {
        return "database";
    }
}
