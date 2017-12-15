package engineers.workshop.common.util;

import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

import static engineers.workshop.common.util.Reference.Info.MODID;

public class Logger {

	private static void log(Level logLevel, Object info, Object... data) {
		if (data == null)
			data = new Object[0];
		FMLLog.log(MODID, logLevel, String.valueOf(info), data);
	}

	public static void debug(Object info) {
		log(Level.DEBUG, info);
	}

	public static void debug(String info, Object... data) {
		log(Level.DEBUG, info, data);
	}

	public static void warn(Object info) {
		log(Level.WARN, info);
	}

	public static void warn(String info, Object... data) {
		log(Level.WARN, info, data);
	}

	public static void info(Object info) {
		log(Level.INFO, info);
	}

	public static void info(String info, Object... data) {
		log(Level.INFO, info, data);
	}

	public static void error(Object info) {
		log(Level.ERROR, info);
	}

	public static void error(String info, Object... data) {
		log(Level.ERROR, info, data);
	}

	public static void stacktrace(String message) {
		try {
			throw new Exception(message != null ? message : "Stacktracing!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
