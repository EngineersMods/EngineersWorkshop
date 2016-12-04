package engineers.workshop.table;

import engineers.workshop.util.Logger;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileTable extends TileEntity implements ITickable{

	public boolean isServer(){
		return !worldObj.isRemote;
	}
	
	@Override
	public void update() {
		if(isServer()){
			if(worldObj.getWorldTime() % (50) == 0)
				Logger.infof("Update at [%s,%s,%s].", pos.getX(), pos.getY(), pos.getZ());
		}
	}

}
