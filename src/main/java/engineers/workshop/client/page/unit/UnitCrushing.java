package engineers.workshop.client.page.unit;

import org.apache.commons.lang3.ArrayUtils;

import com.enderio.core.common.config.ConfigHandler;

import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.sagmill.SagMillRecipeManager;
import engineers.workshop.client.container.slot.crushing.SlotUnitCrusherInput;
import engineers.workshop.client.container.slot.crushing.SlotUnitCrusherQueue;
import engineers.workshop.client.container.slot.crushing.SlotUnitCrusherResult;
import engineers.workshop.client.page.Page;
import engineers.workshop.common.items.Upgrade;
import engineers.workshop.common.loaders.ConfigLoader;
import engineers.workshop.common.table.TileTable;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class UnitCrushing extends Unit {

    public UnitCrushing(TileTable table, Page page, int id, int x, int y) {
        super(table, page, id, x, y);
    }

    private int inputId;
    private int outputId;
    private int queueId;

    private static final int QUEUE_MAX_COUNT = 3;
    private static final int QUEUE_X = 5;
    private static final int QUEUE_Y = 5;
    private static final int START_X = 25;
    private static final int START_Y = 23;
    private static final int RESULT_X = 56;
    private static final int SLOT_SIZE = 18;

    @Override
    public int createSlots(int id) {
        inputId = id;
        addSlot(new SlotUnitCrusherInput(table, page, id++, this.x + START_X, this.y + START_Y, this));
        outputId = id;
        addSlot(new SlotUnitCrusherResult(table, page, id++, this.x + START_X + RESULT_X, this.y + START_Y, this));
        queueId = id;

        for (int i = 0; i < QUEUE_MAX_COUNT; i++) {
            addSlot(new SlotUnitCrusherQueue(table, page, id++, this.x + QUEUE_X, this.y + QUEUE_Y + i * SLOT_SIZE, this, QUEUE_ORDER[i]));
        }

        return id;
    }

    private static final int[] QUEUE_ORDER = {2, 0, 1};
    private static final int[] QUEUE_ORDER_START = {1, 1, 0};

    @Override
    public void onUpdate() {
        super.onUpdate();

        int queueLength = table.getUpgradePage().getUpgradeCount(id, Upgrade.QUEUE);
        if (queueLength > 0) {
            int start = QUEUE_ORDER_START[queueLength - 1];
            for (int i = start + queueLength - 1; i >= start; i--) {
                int targetId;
                if (i == start + queueLength - 1) {
                    targetId = inputId;
                }else{
                    targetId = queueId + i + 1;
                }
                int sourceId = queueId + i;

                ItemStack target = table.getStackInSlot(targetId);
                ItemStack source = table.getStackInSlot(sourceId);
                if (source != null) {
                    ItemStack move = source.copy();
                    move.stackSize = 1;
                    if (canMove(move, target)) {
                        if (target == null) {
                            table.setInventorySlotContents(targetId, move);
                        } else {
                            target.stackSize++;
                        }
                        source.stackSize--;
                        if (source.stackSize == 0) {
                            table.setInventorySlotContents(sourceId, null);
                        }
                    }
                }
            }
        }
    }


    @Override
    protected ItemStack getProductionResult() {
        ItemStack input = table.getStackInSlot(inputId);
        IRecipe ir = SagMillRecipeManager.getInstance().getRecipeForInput(input);
        return input == null ? null : ir == null ? null : ir.getOutputs()[0].getOutput();
    }

    @Override
    protected void onProduction(ItemStack result) {
        table.decrStackSize(inputId, 1);
    }

    @Override
    public int getOutputId() {
        return outputId;
    }

    @Override
    public boolean isEnabled() {
        ItemStack item = table.getUpgradePage().getUpgradeMainItem(id);
        return item != null && ArrayUtils.contains(ConfigLoader.MACHINES.CRUSHER_BLOCKS, item.getItem().getRegistryName().toString());
    }


    private static final int ARROW_X = 25;
    private static final int ARROW_Y = 1;

    @Override
    public int getArrowX() {
        return START_X + ARROW_X;
    }

    @Override
    public int getArrowY() {
        return START_Y + ARROW_Y;
    }
}
