package engineers.workshop.common.loaders;

import engineers.workshop.common.items.Upgrade;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;

import static engineers.workshop.common.util.Reference.Info.MODID;

@GameRegistry.ObjectHolder(MODID)
public final class ConfigLoader {

	private abstract static class ConfigHandler {
		protected String category;
		protected Configuration config;

		public ConfigHandler(Configuration config, String category) {
			this.category = category;
			this.config = config;
		}

		public abstract void load();
	}

	public static class UPGRADES extends ConfigHandler {

		public static int SOLAR_GENERATION, FUEL_EFFICIENCY_CHANGE, MAX_POWER_CHANGE, FUEL_DELAY_CHANGE;

		private final String MAX_COUNT_SUFFIX = " maximum amount";

		public UPGRADES(Configuration config, String category) {
			super(config, category);
		}

		@Override
		public void load() {
			for (Upgrade upgrade : Upgrade.values()) {
				Upgrade.MaxCount max = upgrade.getMaxCountObject();
				if (max.getConfigurableMax() > 0) {
					upgrade.getMaxCountObject().setMax(config.getInt(upgrade.getName() + MAX_COUNT_SUFFIX, category, max.getMax(), 0,
							max.getConfigurableMax(), "Max amount of the " + upgrade.getName() + " upgrade"));
				}
			}

			SOLAR_GENERATION = config.getInt("Solar Generation", category, 4, 0, Integer.MAX_VALUE,
					"Sets the amount of energy generated per tick per solar panel upgrade in the workshop table");
			FUEL_EFFICIENCY_CHANGE = config.getInt("Fuel Efficiency Change", category, 4, 0, Integer.MAX_VALUE,
					"Sets how efficient each upgrade is, formula: 'power = fuel * (upgradeAmount / this)'");
			MAX_POWER_CHANGE = config.getInt("Max Power Change", category, 1000, 0, Integer.MAX_VALUE,
					"Sets the amount that each max power upgrade increases the max power by.");
			FUEL_DELAY_CHANGE = config.getInt("Fuel Delay Change", category, 1, 0, 14,
					"Sets the amount that each fuel delay upgrade decreases the fuel delay by.");
		}

	}

	public static class TWEAKS extends ConfigHandler {
		public static int FUEL_DELAY, MIN_POWER, POWER_CONVERSION;

		public TWEAKS(Configuration config, String category) {
			super(config, category);
		}

		@Override
		public void load() {
			FUEL_DELAY = config.getInt("Fuel Delay", category, 15, 0, Integer.MAX_VALUE,
					"Sets the amount of ticks between each time the worktable consumes a fuel resource");
			
			MIN_POWER = config.getInt("Max Power", category, 8000, 1, Integer.MAX_VALUE,
					"Sets the default max power in the workshop table (minimum, since upgrades can only add power)");
			
			POWER_CONVERSION = config.getInt("Power Conversion Rate", category, 8, 1, Integer.MAX_VALUE,
					"Power conversion rate [x:1] [x = external power input]");
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
					"Should RF upgrades be allowed?");
		}
	}
	
	public static class MACHINES extends ConfigHandler {

		public static boolean CRUSHER_ENABLED, ALLOY_ENABLED;
		public static String[] CRAFTER_BLOCKS, FURNACE_BLOCKS, CRUSHER_BLOCKS, ALLOY_BLOCKS, STORAGE_BLOCKS;

		public MACHINES(Configuration config, String category) {
			super(config, category);
		}

		@Override
		public void load() {
			CRUSHER_ENABLED = config.getBoolean("Enable Crusher", "Machines", true,
					"Is a crusher allowed as a machine? (Requires EnderIO)") && Loader.isModLoaded("EnderIO");
			ALLOY_ENABLED = config.getBoolean("Enable Alloy Smelter", "Machines", true,
					"Is an alloy smelter allowed as a machine? (Requires EnderIO)") && Loader.isModLoaded("EnderIO");
			CRAFTER_BLOCKS = config.getStringList("Crafter Blocks", "Machines", new String[]{"minecraft:crafting_table"}, "What blocks should the table accept for crafters.");
			FURNACE_BLOCKS = config.getStringList("Furnace Blocks", "Machines", new String[]{"minecraft:furnace"}, "What blocks should the table accept for furances.");
			CRUSHER_BLOCKS = config.getStringList("Crusher Blocks", "Machines", new String[]{"enderio:blockSagMill"}, "What blocks should the table accept for crushers.");
			ALLOY_BLOCKS = config.getStringList  ("Alloy Blocks", "Machines", new String[]{"enderio:blockAlloySmelter"}, "What blocks should the table accept for alloy smelters.");
			STORAGE_BLOCKS = config.getStringList  ("Storage Blocks", "Machines", new String[]{"minecraft:chest"}, "What blocks should the table accept for storage.");
		}
	}

	public static void loadConfig(File file) {
		Configuration config = new Configuration(file);
		config.load();
            new UPGRADES(config, "Upgrades").load();
            new TWEAKS(config, "Tweaks").load();
            new POWER(config, "Power").load();
            new MACHINES(config, "Machines").load();
		config.save();
	}
}
