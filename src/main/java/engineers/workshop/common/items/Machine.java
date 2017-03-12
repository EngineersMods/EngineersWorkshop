package engineers.workshop.common.items;

import engineers.workshop.common.loaders.ItemLoader;
import net.minecraft.item.ItemStack;

public enum Machine {
	CRUSHER("Crusher", "Crush stuff into dust!"), CRAFTER("Crafter", "Make this from that."), SMELTER("Smelter", "Cook it... or smelt, whatever.");
	
	private String name, desc;
	private Machine(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}

	public String getName() {
		return name;
	}
	
	public String getDescription(){
		return desc;
	}
	
    public ItemStack getItemStack() {
        return new ItemStack(ItemLoader.itemMachine, 1, ordinal());
    }
}
