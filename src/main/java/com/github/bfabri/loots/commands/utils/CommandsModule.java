package com.github.bfabri.loots.commands.utils;

import com.github.bfabri.loots.commands.LootExecutor;
import com.github.bfabri.loots.commands.utils.framework.BaseCommandModule;

public class CommandsModule extends BaseCommandModule {
	public CommandsModule() {
		this.commands.add(new LootExecutor());
	}
}