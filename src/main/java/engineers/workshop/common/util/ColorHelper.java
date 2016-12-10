package engineers.workshop.common.util;

import net.minecraft.util.text.TextFormatting;

import static net.minecraft.util.text.TextFormatting.*;

/**
 * Created by EwyBoy
 **/
public class ColorHelper {

    public static TextFormatting getPowerColor(float power, float maxPower) {
        float percentage = ((power) / maxPower) * 100;
        int color = (int) (percentage / (100f / 8));

        switch (color) {
            case 0: return DARK_RED;
            case 1: return RED;
            case 2: return GOLD;
            case 3: return YELLOW;
            case 4: return DARK_GREEN;
            case 5: return GREEN;
            case 6: return DARK_AQUA;
            case 7: return AQUA;

            default: return AQUA;
        }
    }
}
