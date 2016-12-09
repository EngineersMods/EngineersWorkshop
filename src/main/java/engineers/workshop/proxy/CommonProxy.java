package engineers.workshop.proxy;

import engineers.workshop.EngineersWorkshop;
import engineers.workshop.client.gui.GuiHandler;
import engineers.workshop.common.loaders.BlockLoader;
import engineers.workshop.common.loaders.ItemLoader;
import engineers.workshop.common.loaders.RecipeLoader;
import engineers.workshop.common.network.PacketHandler;
import engineers.workshop.common.util.ConfigHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import static engineers.workshop.common.util.Reference.Info.MODID;

public class CommonProxy {

    public static FMLEventChannel packetHandler;
	
	public Side getSide(){
		return Side.SERVER;
	}
	
	public void preInit(FMLPreInitializationEvent event) {
		packetHandler = NetworkRegistry.INSTANCE.newEventDrivenChannel(MODID);
        RecipeLoader.registerRecipies();
        ItemLoader.registerItems();
        BlockLoader.registerBlocks();
		ConfigHandler.init(event.getSuggestedConfigurationFile());
		FMLInterModComms.sendMessage("Waila", "register", "engineers.workshop.common.waila.WailaHandler.onWailaCall");
	}
	
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(EngineersWorkshop.instance, new GuiHandler());
		packetHandler.register(new PacketHandler());
	}
	
	public void postInit(FMLPostInitializationEvent event){}

}
