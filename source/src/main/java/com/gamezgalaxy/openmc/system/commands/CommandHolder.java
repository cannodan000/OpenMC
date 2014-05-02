package com.gamezgalaxy.openmc.system.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandHolder {
    private Method method;
    private Command command;

    public void invoke(String line) {
        try {
            method.invoke(null, line);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean matches(char cmd) {
        return cmd == command.command();
    }

    public static List<CommandHolder> lazyLoadCommands(Class<?> commandClass) {
        ArrayList<CommandHolder> temp = new ArrayList<CommandHolder>();

        Method[] methods = commandClass.getMethods();
        for (Method m : methods) {
            if (Modifier.isStatic(m.getModifiers())) {
                Command c = m.getAnnotation(Command.class);
                if (c == null) continue;

                CommandHolder ch = new CommandHolder();
                ch.method = m;
                ch.command = c;
                temp.add(ch);
            }
        }

        return Collections.unmodifiableList(temp);
    }

    public Command getCommand() {
        return command;
    }
}
