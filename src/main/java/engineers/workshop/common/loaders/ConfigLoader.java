package engineers.workshop.common.loaders;

import engineers.workshop.common.items.Upgrade;
import net.minecraftforge.common.config.Configuration;
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
					upgrade.getMaxCountObject()
							.setMax(config.getInt(upgrade.getName() + MAX_COUNT_SUFFIX, category, max.getMax(), 0,
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
		public static int FUEL_DELAY, MIN_POWER, TESLA_CONVERSION;

		public TWEAKS(Configuration config, String category) {
			super(config, category);
		}

		@Override
		public void load() {
			FUEL_DELAY = config.getInt("Fuel Delay", category, 15, 0, Integer.MAX_VALUE,
					"Sets the amount of ticks between each time the worktable consumes a fuel resource");
			
			MIN_POWER = config.getInt("Max Power", category, 8000, 1, Integer.MAX_VALUE,
					"Sets the default max power in the workshop table (minimum, since upgrades can only add power)");
			
			TESLA_CONVERSION = config.getInt("Tesla Conversion", category, 8, 8, Integer.MAX_VALUE,
					"Tesla conversion rate (x Tesla = 1 power)");
		}

	}

	public static class POWER extends ConfigHandler {
		public static boolean TESLA_SUPPORT;

		public POWER(Configuration config, String category) {
			super(config, category);
		}

		@Override
		public void load() {
			TESLA_SUPPORT = config.getBoolean("Tesla Support", "Power", true,
					"Should Tesla upgrades be allowed? (Requires Tesla)");
		}

	}

	public static void loadConfig(File file) {
		Configuration config = new Configuration(file);
		config.load();
		new UPGRADES(config, "Upgrades").load();
		new TWEAKS(config, "Tweaks").load();
		new POWER(config, "Power").load();
		config.save();
	}
}
