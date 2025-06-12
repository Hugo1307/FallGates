package io.github.hugo1307.fallgates.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Message {

    HEADER("general.header"),
    FOOTER("general.footer"),
    NO_PERMISSION("general.noPermission"),
    INVALID_ARGUMENTS("general.invalidArguments"),
    INVALID_SENDER("general.invalidSender"),
    FALL_NOT_FOUND("general.fallNotFound"),

    FALL_CREATION_POSITION_SET("commands.create.positionSet"),
    FALL_CREATION_SUCCESS("commands.create.success"),
    FALL_CREATION_INVALID_SCHEMATIC("commands.create.invalidSchematic"),
    FALL_CREATION_ERROR_LOADING_SCHEMATIC("commands.create.errorLoadingSchematic"),

    FALL_CONNECT_NO_FALL("commands.connect.noFall"),
    FALL_CONNECT_ALREADY_CONNECTED("commands.connect.alreadyConnected"),
    FALL_CONNECT_SUCCESS("commands.connect.success"),

    FALL_DELETE_SUCCESS("commands.delete.success"),

    FALL_LIST_NO_FALLS("commands.list.noFalls"),
    FALL_LIST_ID("commands.list.fallId"),
    FALL_LIST_LOCATION("commands.list.fallLocation"),
    FALL_LIST_CONNECTED("commands.list.fallConnected"),

    CONFIRM_NO_OPERATION_PENDING("commands.confirm.noOperationPending");

    private final String key;

}
