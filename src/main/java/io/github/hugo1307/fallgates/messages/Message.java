package io.github.hugo1307.fallgates.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Message {

    NO_PERMISSION("general.noPermission"),
    INVALID_ARGUMENTS("general.invalidArguments"),
    INVALID_SENDER("general.invalidSender"),

    FALL_CREATION_POSITION_SET("commands.create.positionSet"),
    FALL_CREATION_SUCCESS("commands.create.success"),
    FALL_CREATION_INVALID_SCHEMATIC("commands.create.invalidSchematic"),
    FALL_CREATION_ERROR_LOADING_SCHEMATIC("commands.create.errorLoadingSchematic"),

    CONFIRM_NO_OPERATION_PENDING("commands.confirm.noOperationPending");

    private final String key;

}
