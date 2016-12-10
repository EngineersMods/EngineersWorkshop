package engineers.workshop.common.loaders;

import engineers.workshop.common.items.Upgrade;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;

import static engineers.workshop.common.util.Reference.Info.MODID;

@GameRegistry.ObjectHolder(MODID)
public final class ConfigLoader {

	private static final String MAX_COUNT_SUFFIX = " maximum amount";
	public static boolean doRenderSpinningEntity, debugMode;
	public static int MIN_POWER, SOLAR_GENERATION, FUEL_DELAY, MAX_POWER_CHANGE, FUEL_DELAY_CHANGE;

	public static boolean EUSupport, TeslaSupport;

	public static void loadConfig(File file) {
	    Configuration config = new Configuration(file);
        config.load();
            FUEL_DELAY = config.getInt("Fuel Delay", "Tweaks", 15, 0, Integer.MAX_VALUE,
                    "Sets the amount of ticks between each time the worktable consumes a fuel resource");
            /*EUSupport = config.getBoolean("EU Support", "Power", true,
                    "Should EU upgrades be allowed? (Requires IC2)");*/
            TeslaSupport = config.getBoolean("Tesla Support", "Power", true,
                    "Should Tesla upgrades be allowed? (Requires Tesla)");
            MIN_POWER = config.getInt("Max Power", "Power", 8000, 1, Integer.MAX_VALUE, "Sets the max number of energy storage in the workshop table");
            SOLAR_GENERATION = config.getInt("Solar Generation", "Upgrades", 4, 0, Integer.MAX_VALUE,
                    "Sets the amount of energy generated per tick per solar panel upgrade in the workshop table");
            MAX_POWER_CHANGE = config.getInt("Max Power Change", "Upgrades", 1000, 0, Integer.MAX_VALUE,
                    "Sets the amount that each max power upgrade increases the max power by.");
            FUEL_DELAY_CHANGE = config.getInt("Fuel Delay Change", "Upgrades", 1, 0, 14,
                    "Sets the amount that each fuel delay upgrade decreases the fuel delay by.");

            for (Upgrade upgrade : Upgrade.values()) {
                Upgrade.MaxCount max = upgrade.getMaxCountObject();
                if (max.getConfigurableMax() > 0) {
                    upgrade.getMaxCountObject().setMax(config.getInt(upgrade.getName() + MAX_COUNT_SUFFIX, "Upgrades", max.getMax(), 0,
                                    max.getConfigurableMax(), "Max amount of the " + upgrade.getName() + " upgrade"));
                }
            }
        config.save();
	}
}
