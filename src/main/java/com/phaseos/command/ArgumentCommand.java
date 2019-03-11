package com.phaseos.command;

import org.bukkit.command.CommandSender;

/**
 * Created by Dennis Heckmann on 14.05.17
 * Copyright (c) 2017 Dennis Heckmann
 */
public class ArgumentCommand extends Command {

    public ArgumentCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    protected void execute(CommandSender sender, ArgumentParser ap) {

    }

    @ArgumentMethod
    public void doSomething(int x) {

    }

    public @interface ArgumentMethod {}

}
