package ru.kpfu.itis.galeev.aidan.choosememegame.utils;

import ru.kpfu.itis.galeev.aidan.choosememegame.server.ServerMessages;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StringConverter {
    public static String escapeMessage(String message) {
        return message.replace("\\", "\\\\");
    }

    public static String unescapeMessage(String message) {
        return message.replace("\\,", ",").replace("\\/\\/", "//");
    }

    public static String writeCommand(String command, String[] arguments) {
        StringBuilder sb = new StringBuilder();
        sb.append(command).append(ServerMessages.COMMANDS_SEPARATOR);
        for (String argument : arguments) {
            sb.append(argument);
            sb.append(ServerMessages.ARGUMENTS_SEPARATOR);
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("\n");

        return sb.toString();
    }

    public static Map.Entry<String, String[]> getResult(String givenString) {
        String[] command = givenString.split(ServerMessages.COMMANDS_SEPARATOR);
        if (command.length == 2) {
            return new AbstractMap.SimpleEntry<>(command[0], command[1].split(ServerMessages.ARGUMENTS_SEPARATOR));
        } else {
            throw new UnsupportedOperationException("Given string wrong: " + givenString);
        }
    }
}
