package com.github.bfabri.loots.loot;

import java.util.HashMap;

public interface LootInterface {

	HashMap<String, Loot> getLoots();

	Loot getLoot(String name);

	void addLoot(String name);

	void deleteLoot(String name);

	void loadLoots();

	void saveLoot(Loot loot);

	void saveLoots();
}
