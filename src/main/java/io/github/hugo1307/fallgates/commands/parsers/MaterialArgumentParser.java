package io.github.hugo1307.fallgates.commands.parsers;

import dev.hugog.minecraft.dev_command.arguments.parsers.CommandArgumentParser;
import org.bukkit.Material;

import java.util.Optional;

public class MaterialArgumentParser extends CommandArgumentParser<Material> {
    public MaterialArgumentParser(String argument) {
        super(argument);
    }

    @Override
    public boolean isValid() {
        return Material.matchMaterial(getArgument()) != null;
    }

    @Override
    public Optional<Material> parse() {
        return Optional.ofNullable(Material.matchMaterial(getArgument()))
                .or(() -> Optional.ofNullable(Material.getMaterial(getArgument().toUpperCase())));
    }
}
