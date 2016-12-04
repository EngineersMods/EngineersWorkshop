package engineers.workshop.proxies;

import engineers.workshop.EngineersWorkshop;
import engineers.workshop.items.Upgrade;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends CommonProxy{
	@Override
	public void preInit() {
		super.preInit();
		for (int i = 0; i < Upgrade.values().length; ++i) {
			Upgrade[] upgrades = Upgrade.values().clone();
			ModelLoader.setCustomModelResourceLocation(EngineersWorkshop.itemUpgrade, i,
					new ModelResourceLocation("engineersworkshop:" + upgrades[i].getName()));
		}

	}
	
	@Override
	public Side getSide() {
		return Side.CLIENT;
	}

}
