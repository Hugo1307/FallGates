package io.github.hugo1307.fallgates.data.domain;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class FallGateSchematic {

    private final String name;

    @Setter
    private Clipboard schematicClipboard;

    public String getFileName() {
        return this.name + ".schem";
    }

}
