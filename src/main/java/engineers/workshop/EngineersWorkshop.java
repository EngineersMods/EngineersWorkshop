package engineers.workshop;

import engineers.workshop.gui.GuiHandler;
import engineers.workshop.items.ItemUpgrade;
import engineers.workshop.network.PacketHandler;
import engineers.workshop.proxies.CommonProxy;
import engineers.workshop.table.BlockTable;
import engineers.workshop.util.ConfigHandler;
import engineers.workshop.util.Logger;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = "engineersworkshop")
public class EngineersWorkshop {

	@SidedProxy(serverSide = "engineers.workshop.proxies.CommonProxy", clientSide = "engineers.workshop.proxies.ClientProxy")
	public static CommonProxy proxy;
	
	@Instance("engineersworkshop")
	public static EngineersWorkshop instance;
	
	public static FMLEventChannel packetHandler;

	public static Item itemUpgrade;
	public static Block blockTable;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		packetHandler = NetworkRegistry.INSTANCE.newEventDrivenChannel("engineersworkshop");
		itemUpgrade = new ItemUpgrade();
		blockTable = new BlockTable();
		ConfigHandler.init(event.getSuggestedConfigurationFile());
		
		proxy.preInit();
		

		FMLInterModComms.sendMessage("Waila", "register", "engineers.workshop.waila.WailaHandler.onWailaCall");
		Logger.debug("FMLPreInitialization done.");
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		packetHandler.register(new PacketHandler());
		proxy.init();
		Logger.debug("FMLInitialization done.");
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
		Logger.debug("FMLPostInitialization done.");
	}

	// Creative Tab
	public static CreativeTabs tabWorkshop = new CreativeTabs("engineersworkshop") {
		@Override
		public Item getTabIconItem() {
			return itemUpgrade;
		}
	};

}
