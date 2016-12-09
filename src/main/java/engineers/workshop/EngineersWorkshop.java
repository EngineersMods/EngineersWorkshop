package engineers.workshop;

import engineers.workshop.proxy.CommonProxy;
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

@Mod(modid = MODID, name = NAME)
public class EngineersWorkshop {

	@SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
	public static CommonProxy proxy;
	
	@Instance(MODID)
	public static EngineersWorkshop instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
