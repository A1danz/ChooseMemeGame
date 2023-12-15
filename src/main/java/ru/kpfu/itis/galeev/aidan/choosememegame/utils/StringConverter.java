package ru.kpfu.itis.galeev.aidan.choosememegame.utils;

import ru.kpfu.itis.galeev.aidan.choosememegame.server.ServerMessages;

import java.util.*;

public class StringConverter {
    public static String escapeMessage(String message) {
        return message.replace("\\", "\\\\");
    }

    public static String unescapeMessage(String message) {
        return message.replace("\\,", ",").replace("\\/\\/", "//");
    }

    public static String createCommand(String command, String[][] arguments) {
        StringBuilder sb = new StringBuilder();
        sb.append(command).append(ServerMessages.COMMANDS_SEPARATOR);
        for (String[] argument : arguments) {
            for (String item : argument) {
                sb.append(item);
                sb.append(ServerMessages.ITEMS_SEPARATOR);
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(ServerMessages.ARGUMENTS_SEPARATOR);
        }
        if (arguments.length != 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("\n");

        System.out.print("SENDED COMMAND: " + sb);
        return sb.toString();
    }

    public static Map.Entry<String, String[][]> getCommand(String givenString) {
        System.out.println("HANDLED COMMAND: " + givenString);
        String[] command = givenString.split(ServerMessages.COMMANDS_SEPARATOR);
        if (command.length == 2 || command.length == 1) {
            String[] arguments = command.length == 2 ? command[1].split(ServerMessages.ARGUMENTS_SEPARATOR) : new String[]{};
            String[][] result = new String[arguments.length][];
            for (int i = 0; i < arguments.length; i++) {
                result[i] = arguments[i].split(ServerMessages.ITEMS_SEPARATOR);
            }
            return new AbstractMap.SimpleEntry<>(command[0], result);
        } else {
            throw new UnsupportedOperationException("Given string wrong: " + givenString);
        }
    }
}
