package engineers.workshop.common.dependencies.waila;

import engineers.workshop.common.table.BlockTable;
import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaHandler {
	public static void onWailaCall(IWailaRegistrar registrar) {
		registrar.registerStackProvider(new WailaWorkshop(), BlockTable.class);
		registrar.registerBodyProvider(new WailaWorkshop(), BlockTable.class);
		registrar.registerNBTProvider(new WailaWorkshop(), BlockTable.class);
	}
}
