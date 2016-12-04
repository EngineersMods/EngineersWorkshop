package engineers.workshop.waila;

import engineers.workshop.table.BlockTable;
import engineers.workshop.util.Logger;
import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaHandler {
	public static void onWailaCall(IWailaRegistrar registrar) {
		Logger.info("Found " + mcp.mobius.waila.Waila.instance);
		Logger.info("Loading WAILA features");
		registrar.registerStackProvider(new WailaWorkshop(), BlockTable.class);
		registrar.registerBodyProvider(new WailaWorkshop(), BlockTable.class);
		registrar.registerNBTProvider(new WailaWorkshop(), BlockTable.class);
		Logger.info("Loaded WAILA features");
	}
}
