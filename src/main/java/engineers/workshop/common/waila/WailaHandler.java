package engineers.workshop.common.waila;

import engineers.workshop.common.table.BlockTable;
import engineers.workshop.common.util.Logger;
import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaHandler {
	public static void onWailaCall(IWailaRegistrar registrar) {
		registrar.registerStackProvider(new WailaWorkshop(), BlockTable.class);
		registrar.registerBodyProvider(new WailaWorkshop(), BlockTable.class);
		registrar.registerNBTProvider(new WailaWorkshop(), BlockTable.class);
	}
}
