package engineers.workshop;

import static engineers.workshop.common.Reference.Info.MODID;
import static engineers.workshop.common.Reference.Info.NAME;
import static engineers.workshop.common.Reference.Paths.CLIENT_PROXY;
import static engineers.workshop.common.Reference.Paths.COMMON_PROXY;

import engineers.workshop.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MODID, name = NAME, dependencies = "required-after:bibliotheca@[1.1.2-1.12.2,)")
public class EngineersWorkshop {

	@SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
	public static CommonProxy proxy;

	@Instance(MODID)
	public static EngineersWorkshop instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
		MinecraftForge.EVENT_BUS.register(this);
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
