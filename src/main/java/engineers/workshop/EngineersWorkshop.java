package engineers.workshop;

import engineers.workshop.items.ItemUpgrade;
import engineers.workshop.proxies.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = "engineersworkshop")
public class EngineersWorkshop {
	
	@SidedProxy(serverSide = "engineers.workshop.proxies.CommonProxy", clientSide = "engineers.workshop.proxies.ClientProxy")
	public static CommonProxy proxy;

	public static CreativeTabs TabWorkshop = new CreativeTabs("engineersworkshop") {
		@Override
		public Item getTabIconItem() {
			return net.minecraft.init.Items.ACACIA_BOAT;
		}
	};

	public static class Items {
		public static Item upgrade;

		public static void register() {
			GameRegistry.register(upgrade = new ItemUpgrade().setRegistryName("engineersworkshop:upgrade"));
		}
		
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Items.register();
		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
	}

}
