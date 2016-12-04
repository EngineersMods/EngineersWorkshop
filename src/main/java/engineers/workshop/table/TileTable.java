package engineers.workshop.table;

import engineers.workshop.util.Logger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileTable extends TileEntity implements ITickable{

	public boolean isServer(){
		return !worldObj.isRemote;
	}
	
	private int updates = 0;
	
	@Override
	public void update() {
		if(isServer()){
			if(worldObj.getWorldTime() % (50) == 0){
				updates++;
				Logger.infof("Update[%s] at [%s,%s,%s].", updates, pos.getX(), pos.getY(), pos.getZ());
			}
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("updates", updates);
		return super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		updates = compound.getInteger("updates");
		super.readFromNBT(compound);
	}
	
	public int getUpdates(){
		return updates;
	}

}
