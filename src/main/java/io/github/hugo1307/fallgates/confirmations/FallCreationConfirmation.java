package io.github.hugo1307.fallgates.confirmations;

import io.github.hugo1307.fallgates.FallGates;
import io.github.hugo1307.fallgates.data.domain.Fall;
import io.github.hugo1307.fallgates.messages.Message;
import io.github.hugo1307.fallgates.messages.MessageService;
import io.github.hugo1307.fallgates.services.FallService;
import io.github.hugo1307.fallgates.services.SchematicsService;
import io.github.hugo1307.fallgates.services.ServiceAccessor;
import org.bukkit.entity.Player;

public class FallCreationConfirmation extends PluginConfirmation {

    private final FallService fallService;
    private final SchematicsService schematicsService;
    private final MessageService messageService;

    private final Fall fallToCreate;

    public FallCreationConfirmation(FallGates plugin, ServiceAccessor serviceAccessor, Fall fallToCreate) {
        super(ConfirmationType.CREATE_FALL, plugin, serviceAccessor);

        this.fallService = serviceAccessor.accessService(FallService.class);
        this.schematicsService = serviceAccessor.accessService(SchematicsService.class);
        this.messageService = serviceAccessor.accessService(MessageService.class);

        this.fallToCreate = fallToCreate;
    }

    @Override
    public void onConfirm(Player player) {
        schematicsService.pasteSchematic(fallToCreate.getSchematic(), fallToCreate.getPosition().toBukkitLocation());
        fallService.saveFall(fallToCreate);
        messageService.sendPlayerMessage(player, Message.FALL_CREATION_SUCCESS);
    }

}
