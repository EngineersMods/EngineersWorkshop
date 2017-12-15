package engineers.workshop.common.util.helpers;

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
			case 0:
				return DARK_RED;
			case 1:
				return RED;
			case 2:
				return GOLD;
			case 3:
				return YELLOW;
			case 4:
				return DARK_GREEN;
			case 5:
				return GREEN;
			case 6:
				return DARK_AQUA;
			case 7:
				return AQUA;

			default:
				return AQUA;
		}
	}

	public static int getRGB(float power, float maxPower) {
		float percentage = ((power) / maxPower) * 100;
		int color = (int) (percentage / (100f / 8));

		switch (color) {
			case 0:
				return 0xff8c0000;
			case 1:
				return 0xffff0000;
			case 2:
				return 0xffffc800;
			case 3:
				return 0xffffff00;
			case 4:
				return 0xff009600;
			case 5:
				return 0xff00ff00;
			case 6:
				return 0xff0055ff;
			case 7:
				return 0xff00aaff;

			default:
				return 0xff00aaff;
		}
	}

	public static float getRed(float power, float maxPower) {
		float percentage = ((power) / maxPower) * 100;
		int color = (int) (percentage / (100f / 8));

		switch (color) {
			case 0:
				return 140;
			case 1:
				return 255;
			case 2:
				return 255;
			case 3:
				return 255;
			case 4:
				return 0;
			case 5:
				return 0;
			case 6:
				return 0;
			case 7:
				return 0;

			default:
				return 0;
		}
	}

	public static float getGreen(float power, float maxPower) {
		float percentage = ((power) / maxPower) * 100;
		int color = (int) (percentage / (100f / 8));

		switch (color) {
			case 0:
				return 0;
			case 1:
				return 0;
			case 2:
				return 200;
			case 3:
				return 255;
			case 4:
				return 150;
			case 5:
				return 255;
			case 6:
				return 85;
			case 7:
				return 170;

			default:
				return 170;
		}
	}

	public static float getBlue(float power, float maxPower) {
		float percentage = ((power) / maxPower) * 100;
		int color = (int) (percentage / (100f / 8));

		switch (color) {
			case 0:
				return 0;
			case 1:
				return 0;
			case 2:
				return 0;
			case 3:
				return 0;
			case 4:
				return 0;
			case 5:
				return 0;
			case 6:
				return 255;
			case 7:
				return 255;

			default:
				return 255;
		}
	}
}
