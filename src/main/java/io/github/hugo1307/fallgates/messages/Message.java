package io.github.hugo1307.fallgates.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Message {

    NO_PERMISSION("general.noPermission"),
    INVALID_ARGUMENTS("general.invalidArguments"),
    INVALID_SENDER("general.invalidSender"),

    GATE_BUILD_POSITION_SET("commands.build.positionSet"),
    GATE_BUILD_SUCCESS("commands.build.success"),

    CONFIRM_NO_OPERATION_PENDING("commands.confirm.noOperationPending");

    private final String key;

}
