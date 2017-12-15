package engineers.workshop;

import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.util.Logger;
import engineers.workshop.proxy.CommonProxy;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static engineers.workshop.common.util.Reference.Info.MODID;
import static engineers.workshop.common.util.Reference.Info.NAME;
import static engineers.workshop.common.util.Reference.Paths.CLIENT_PROXY;
import static engineers.workshop.common.util.Reference.Paths.COMMON_PROXY;
import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.*;

@Mod(modid = MODID, name = NAME, dependencies = "required-after:bibliotheca@[1.1.1-1.12.2,)")
public class EngineersWorkshop {

	@SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
	public static CommonProxy proxy;

	@Instance(MODID)
	public static EngineersWorkshop instance;

	public static void loadRecipes() {
		//RebornCraftingHelper.addShapedOreRecipe(new ItemStack(EngineersWorkshop.blockTable), "PPP", "CUC", "CCC", 'P', PLANKS, 'C', COBBLESTONE, 'U', Upgrade.BLANK.getItemStack());
		addRecipe(Upgrade.BLANK, "SP", "PS", 'S', STONE, 'P', PLANKS);
		addRecipe(Upgrade.STORAGE, "C", "U", 'C', Blocks.CHEST, 'U', Upgrade.BLANK.getItemStack());
		addRecipe(Upgrade.AUTO_CRAFTER, "PPP", "CTC", "CUC", 'P', PLANKS, 'C', COBBLESTONE, 'T', PISTON, 'U', Upgrade.BLANK.getItemStack());
		addRecipe(Upgrade.SPEED, "IRI", "LUL", "IRI", 'I', IRON_INGOT, 'R', REDSTONE, 'L', new ItemStack(DYE, 1, 4), 'U', Upgrade.BLANK.getItemStack());
		addRecipe(Upgrade.QUEUE, "PPP", "IUI", "PPP", 'I', IRON_INGOT, 'P', PLANKS, 'U', Upgrade.BLANK.getItemStack());

		addRecipe(Upgrade.AUTO_TRANSFER, "GGG", "HUH", "GGG", 'G', GOLD_INGOT, 'H', HOPPER, 'U', Upgrade.BLANK.getItemStack());
		addRecipe(Upgrade.FILTER, "III", "GBG", "IUI", 'G', Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 'I', IRON_INGOT, 'B', Blocks.IRON_BARS, 'U', Upgrade.BLANK.getItemStack());
		addRecipe(Upgrade.CHARGED, "IRI", "IUI", "IRI", 'I', IRON_INGOT, 'R', REDSTONE, 'U', Upgrade.BLANK.getItemStack());
		addRecipe(Upgrade.TRANSFER, "III", "GRG", "GUG", 'G', GOLD_INGOT, 'I', IRON_INGOT, 'R', REDSTONE_BLOCK, 'U', Upgrade.BLANK.getItemStack());
		addRecipe(Upgrade.AXE, "FAF", "RUR", "III", 'F', FLINT, 'A', IRON_AXE, 'R', REDSTONE, 'U', Upgrade.BLANK.getItemStack(), 'I', IRON_BARS);
	}

	private static void addRecipe(Upgrade upgrade, Object... recipe) {
		if (upgrade.isEnabled()) {
			//RebornCraftingHelper.addShapedOreRecipe(upgrade.getItemStack(), recipe);
			Logger.info(upgrade + " recipe loaded.");
		}
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void init(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
