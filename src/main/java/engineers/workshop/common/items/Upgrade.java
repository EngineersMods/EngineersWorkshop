package engineers.workshop.common.items;

import static engineers.workshop.common.util.Reference.Info.MODID;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.enderio.core.common.config.annot.Config;

import engineers.workshop.common.loaders.ConfigLoader;
import engineers.workshop.common.loaders.ItemLoader;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
@SuppressWarnings("unchecked")
public enum Upgrade {
    BLANK			(new MaxCount(0), 			    ParentType.NULL), 		// Max count = 0  (Not usable as an upgrade)
    AUTO_CRAFTER	(new MaxCount(1), 			    ParentType.CRAFTING), 	// Max count = 1
    STORAGE			(new MaxCount(1),			    ParentType.CRAFTING), 	// Max count = 1
    CHARGED			(new ConfigurableMax(8),	  	ParentType.MachineSet),		// Max count = 8  (Configable)
	SPEED			(new ConfigurableMax(8),	    ParentType.MachineSet),		// Max count = 8  (Configable)
    QUEUE			(new MaxCount(3), 			    EnumSet.of(ParentType.CRUSHING, ParentType.SMELTING)),	// Max count = 3
    EFFICIENCY		(new ConfigurableMax(4), 	    ParentType.GLOBAL),		// Max count = 4  (Configable)
    RF  			(new MaxCount(1), 			    ParentType.GLOBAL),		// Max count = 1
    SOLAR			(new ConfigurableMax(4),	    ParentType.GLOBAL),		// Max count = 4  (Configable)
    AUTO_TRANSFER	(new MaxCount(1),			    ParentType.GLOBAL),		// Max count = 1
    FILTER			(new MaxCount(1),			    ParentType.GLOBAL),		// Max count = 1
    TRANSFER		(new ConfigurableMax(6, 20),    ParentType.GLOBAL),		// Max count = 6  (Configable upto 20)
    MAX_POWER		(new ConfigurableMax(16), 	    ParentType.GLOBAL),		// Max count = 16 (Configable)
    FUEL_DELAY		(new ConfigurableMax(5), 	    ParentType.GLOBAL);		// Max count = 5  (Configable)
	

    /**
     * PATTERN("Pattern Crafting", "Remembers old recipes", 4, ParentType.CRAFTING),
     * RESTOCK("Restock Control", "Only produce more items* when there isn't enough of them", 1),
     */

    private String name;
    private String description;
    private MaxCount maxCount;
    private EnumSet<ParentType> validParents;

    Upgrade(MaxCount maxCount, EnumSet<ParentType> validParents) {
        this.validParents = validParents;
        this.name = toString().toLowerCase();
        this.description = String.format(MODID + ":"  + "upgrade" + "." + "%s" + "." + "description", name);
        this.maxCount = maxCount;
        maxCount.init(this);
    }

    Upgrade(MaxCount maxCount, ParentType type) {
        this(maxCount, type == null ? EnumSet.of(ParentType.NULL) : EnumSet.of((type)));
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return maxCount.getConfigurableMax() == 0 || maxCount.getMax() > 0;
    }

    public ItemStack getItemStack() {
        return new ItemStack(ItemLoader.itemUpgrade, 1, ordinal());
    }

    public static ItemStack getInvalidItemStack() {
        return new ItemStack(ItemLoader.itemUpgrade, 1, values().length);
    }

    public void addInfo(List<String> info) {
        info.add(I18n.format(description));

        if(!GuiScreen.isShiftKeyDown() && !GuiScreen.isCtrlKeyDown()){
            info.add(I18n.format("<hold shift for stack info>"));
            info.add(I18n.format("<hold control for parent info>"));
        }

        if (GuiScreen.isShiftKeyDown()) {
            if (getMaxCount() == 1)     info.add(I18n.format("engineersworkshop:upgrade.unstackable"));
            else if (getMaxCount() > 1) info.add(I18n.format("engineersworkshop:upgrade.stackable", getMaxCount()));
            info.addAll(validParents.stream().map(validParent -> I18n.format(validParent.description)).collect(Collectors.toList()));
        }

        if (GuiScreen.isCtrlKeyDown()) {
            if (validParents.size() > 0) {
                info.add("Parents: ");
                info.addAll(validParents.stream().map(ParentType::name).collect(Collectors.toList()));
            }
        }
    }

    public boolean isValid(ItemStack parent) {
        for (ParentType validParent : validParents) {
            if (validParent.isValidParent(parent)) {
                return true;
            }
        }
        return false;
    }

    public int getMaxCount() {
        return maxCount.getMax();
    }

    public MaxCount getMaxCountObject() {
        return maxCount;
    }

    public enum ParentType {
		CRAFTING("Works with Crafting Tables") {
			@Override
			protected boolean isValidParent(ItemStack item) {
				if (item != null)
					for (String parent : ConfigLoader.MACHINES.CRAFTER_BLOCKS)
						if (item.getItem().getRegistryName().toString().equals(parent))
							return true;
				return false;
			}
		},
		SMELTING("Works with Furnaces") {
			@Override
			protected boolean isValidParent(ItemStack item) {
				if (item != null)
					for (String parent : ConfigLoader.MACHINES.FURNACE_BLOCKS)
						if (item.getItem().getRegistryName().toString().equals(parent))
							return true;

				return false;
			}
		},
		CRUSHING("Works with Crushers") {
			@Override
			protected boolean isValidParent(ItemStack item) {
				if (item != null)
					for (String parent : ConfigLoader.MACHINES.CRUSHER_BLOCKS)
						if (item.getItem().getRegistryName().toString().equals(parent))
							return true;

				return false;
			}

		},
		
		ALLOY("Works with Alloy Smelters") {
			@Override
			protected boolean isValidParent(ItemStack item) {
				if (item != null)	
					for (String parent : ConfigLoader.MACHINES.ALLOY_BLOCKS)
						if (item.getItem().getRegistryName().toString().equals(parent))
							return true;

				return false;
			}

		},
        GLOBAL("Upgrades the entire Table") {
            @Override
            protected boolean isValidParent(ItemStack item) {
                return item == null;
            }
        },
        NULL("you shouldn't be seeing this.") {
            @Override
            protected boolean isValidParent(ItemStack item) {
                return false;
            }
        };

        private String description;

        ParentType(String description) {
            this.description = description;
        }

        protected abstract boolean isValidParent(ItemStack item);
        
    	private static final EnumSet MachineSet = EnumSet.of(ParentType.CRAFTING, ParentType.SMELTING, ParentType.CRUSHING, ParentType.ALLOY);
    }

    public static class MaxCount {
        private int max;
        private int defaultMax;

        public MaxCount(int max) {
            this.max = max;
            this.defaultMax = max;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int value) {
            this.max = value;
        }

        public int getConfigurableMax() {
            return defaultMax;
        }

        public void init(Upgrade upgrade) {}
    }

    private static class ConfigurableMax extends MaxCount {
        private boolean isGlobal;
        private int configurableMax;

        private ConfigurableMax(int max, int configurableMax) {
            super(max);
            this.configurableMax = configurableMax;
        }

        private ConfigurableMax(int max) {
            this(max, -1);
        }

        private static final int GLOBAL_MAX_COUNT = 8 * 64;
        private static final int MAX_COUNT = 7 * 64;

        @Override
        public int getConfigurableMax() {
            return configurableMax != -1 ? configurableMax : isGlobal ? GLOBAL_MAX_COUNT : MAX_COUNT;
        }

        @Override
        public void init(Upgrade upgrade) {
            isGlobal = upgrade.validParents.contains(ParentType.GLOBAL);
        }
    }
}
