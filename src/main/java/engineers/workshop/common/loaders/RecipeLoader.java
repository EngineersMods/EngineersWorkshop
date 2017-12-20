package engineers.workshop.common.loaders;

import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.*;

import engineers.workshop.common.Reference;
import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.register.Register;
import engineers.workshop.common.util.EWLogger;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RecipeLoader {

    public static void loadRecipes() {
    	GameRegistry.addShapedRecipe(new ResourceLocation(Reference.Info.MODID, "recipeBlockTable"), null, new ItemStack(Register.Blocks.table), "PTP", "UcU", "CFC", 'P', PLANKS, 'C', COBBLESTONE, 'U', Upgrade.BLANK.getItemStack(), 'F', FURNACE, 'T', CRAFTING_TABLE, 'c', CHEST);
        addRecipe(Upgrade.BLANK, "SP", "PS", 'S', STONE, 'P', PLANKS);
        addRecipe(Upgrade.STORAGE, "C", "U", 'C', Blocks.CHEST, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.AUTO_CRAFTER, "PPP", "CTC", "CUC", 'P', PLANKS, 'C', COBBLESTONE, 'T', PISTON, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.SPEED, "IRI", "LUL", "IRI", 'I', IRON_INGOT, 'R', REDSTONE, 'L', new ItemStack(DYE, 1, 4), 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.QUEUE, "PPP", "IUI", "PPP", 'I', IRON_INGOT, 'P', PLANKS, 'U', Upgrade.BLANK.getItemStack());
//        addRecipe(Upgrade.SOLAR, "CCC", "GGG", "UDU", 'I', IRON_INGOT, 'G', DAYLIGHT_DETECTOR, 'C', GLASS, 'U', DIAMOND,'D', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.AUTO_TRANSFER, "GGG", "HUH", "GGG", 'G', GOLD_INGOT, 'H', HOPPER, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.FILTER, "III", "GBG", "IUI", 'G', Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 'I', IRON_INGOT, 'B', Blocks.IRON_BARS, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.CHARGED, "IRI", "IUI", "IRI", 'I', IRON_INGOT, 'R', REDSTONE, 'U', Upgrade.BLANK.getItemStack());
        addRecipe(Upgrade.TRANSFER, "III", "GRG", "GUG", 'G', GOLD_INGOT, 'I', IRON_INGOT, 'R', REDSTONE_BLOCK, 'U', Upgrade.BLANK.getItemStack());
//        addRecipe(Upgrade.RF, "ORO", "RDR", "ORO", 'O', IRON_INGOT, 'R', REDSTONE_BLOCK, 'D', Upgrade.CHARGED.getItemStack());
//        addRecipe(Upgrade.FUEL_DELAY, "IRI", "LUL", "IRI", 'I', IRON_INGOT, 'R', REDSTONE, 'L', new ItemStack(DYE, 1, 4), 'U', Upgrade.SPEED.getItemStack());
//        addRecipe(Upgrade.MAX_POWER, "GRG", "RTR", "GUG", 'G', GOLD_INGOT, 'T', TRAPPED_CHEST, 'R', REDSTONE_BLOCK, 'U', Upgrade.STORAGE.getItemStack());
        addRecipe(Upgrade.AXE, "FAF", "RUR", "III", 'F', FLINT, 'A', IRON_AXE, 'R', REDSTONE, 'U', Upgrade.BLANK.getItemStack(), 'I', IRON_BARS);
        
//    	UnitCrush.addLogSawRecipies();
    }

    private static void addRecipe(Upgrade upgrade, Object ... recipe) {
        if (upgrade.isEnabled()) {
            GameRegistry.addShapedRecipe(new ResourceLocation(Reference.Info.MODID, "recipe"+ upgrade.toString()), null, upgrade.getItemStack(), recipe);
            EWLogger.info(upgrade + " recipe loaded.");
        }
    }
}
