package engineers.workshop.common.loaders;

import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.util.Logger;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.*;

public class RecipeLoader {

    public static void loadRecipes() {
        GameRegistry.addRecipe(new ShapedOreRecipe(BlockLoader.blockTable, "PPP", "CUC", "CCC", 'P', PLANKS, 'C', COBBLESTONE, 'U', Upgrade.BLANK.getItemStack()));

        addRecipe(Upgrade.BLANK, "SP", "PS", 'S', STONE, 'P', PLANKS);
        addRecipe(Upgrade.STORAGE, "C", "U", 'C', Blocks.CHEST, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.AUTO_CRAFTER, "PPP", "CTC", "CUC", 'P', PLANKS, 'C', COBBLESTONE, 'T', PISTON, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.SPEED, "IRI", "LUL", "IRI", 'I', IRON_INGOT, 'R', REDSTONE, 'L', new ItemStack(DYE, 1, 4), 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.QUEUE, "PPP", "IUI", "PPP", 'I', IRON_INGOT, 'P', PLANKS, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.SOLAR, "CCC", "GGG", "UDU", 'I', IRON_INGOT, 'G', DAYLIGHT_DETECTOR, 'C', GLASS, 'U', DIAMOND,'D', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.EFFICIENCY, "III", "FPF", "RUR", 'I', IRON_INGOT, 'R', REDSTONE, 'F', FURNACE, 'P', PISTON, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.AUTO_TRANSFER, "GGG", "HUH", "GGG", 'G', GOLD_INGOT, 'H', HOPPER, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.FILTER, "III", "GBG", "IUI", 'G', Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 'I', IRON_INGOT, 'B', Blocks.IRON_BARS, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.TRANSFER, "III", "GRG", "GUG", 'G', GOLD_INGOT, 'I', IRON_INGOT, 'R', REDSTONE_BLOCK, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.RF, "ORO", "RDR", "ORO", 'O', IRON_INGOT, 'R', REDSTONE_BLOCK, 'D', Upgrade.CHARGED.getItemStack());
        addRecipe(Upgrade.FUEL_DELAY, "IRI", "LUL", "IRI", 'I', IRON_INGOT, 'R', REDSTONE, 'L', new ItemStack(DYE, 1, 4), 'U', Upgrade.SPEED.getItemStack());
        addRecipe(Upgrade.MAX_POWER, "GRG", "RTR", "GUG", 'G', GOLD_INGOT, 'T', TRAPPED_CHEST, 'R', REDSTONE_BLOCK, 'U', Upgrade.STORAGE.getItemStack());
    }

    private static void addRecipe(Upgrade upgrade, Object ... recipe) {
        if (upgrade.isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(upgrade.getItemStack(), recipe));
            Logger.info(upgrade + " recipe loaded");
        }
    }
}
