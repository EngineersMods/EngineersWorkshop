package engineers.workshop.common.loaders;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;

import static engineers.workshop.common.Reference.Info.MODID;

@GameRegistry.ObjectHolder(MODID)
public final class ConfigLoader {

	//TODO - Disable configs that is not currently in use.

	public static void loadConfig(File file) {
		Configuration config = new Configuration(file);
		config.load();
			new TWEAKS(config, "Tweaks").load();
			new POWER(config, "Power").load();
			new MACHINES(config, "Machines").load();
		config.save();
	}

	private abstract static class ConfigHandler {
		String category;
		Configuration config;

		ConfigHandler(Configuration config, String category) {
			this.category = category;
			this.config = config;
		}

		public abstract void load();
	}

	public static class TWEAKS extends ConfigHandler {
		public static int FUEL_DELAY;
		public TWEAKS(Configuration config, String category) {
			super(config, category);
		}

		@Override
		public void load() {
			FUEL_DELAY = config.getInt("Fuel Delay", category, 15, 0, Integer.MAX_VALUE,
				"Sets the amount of ticks between each time the worktable consumes a fuel resource");
		}
	}

	public static class POWER extends ConfigHandler {
		public static boolean RF_SUPPORT;
		public POWER(Configuration config, String category) {
			super(config, category);
		}

		@Override
		public void load() {
			RF_SUPPORT = config.getBoolean("RF Support", "Power", true,
				"Should RF upgrades be allowed?"
			);
		}
	}

	public static class MACHINES extends ConfigHandler {
		public static String[] CRAFTER_BLOCKS, FURNACE_BLOCKS, CRUSHER_BLOCKS, ALLOY_BLOCKS, STORAGE_BLOCKS;

		public MACHINES(Configuration config, String category) {
			super(config, category);
		}

		@Override
		public void load() {
			CRAFTER_BLOCKS = config.getStringList("Crafter Blocks", "Machines", new String[] { "minecraft:crafting_table" }, "What blocks should the table accept for crafters.");
			FURNACE_BLOCKS = config.getStringList("Furnace Blocks", "Machines", new String[] { "minecraft:furnace" }, "What blocks should the table accept for furances.");
			CRUSHER_BLOCKS = config.getStringList("Crusher Blocks", "Machines", new String[] { "enderio:blockSagMill" }, "What blocks should the table accept for crushers.");
			ALLOY_BLOCKS = config.getStringList("Alloy Blocks", "Machines", new String[] { "enderio:blockAlloySmelter" }, "What blocks should the table accept for alloy smelters.");
			STORAGE_BLOCKS = config.getStringList("Storage Blocks", "Machines", new String[] { "minecraft:chest" }, "What blocks should the table accept for storage.");
		}
	}
}
