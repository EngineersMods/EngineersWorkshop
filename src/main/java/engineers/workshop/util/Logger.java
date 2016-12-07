package engineers.workshop.util;

import org.apache.logging.log4j.Level;

import net.minecraftforge.fml.common.FMLLog;

public class Logger {

	private Logger() {
		
	}

	private static void log(Level logLevel, Object info, Object... data) {
		if (data == null)
			data = new Object[0];
		FMLLog.log("EWorkshop", logLevel, String.valueOf(info), data);
	}

	public static void debug(Object info) {
		log(Level.DEBUG, info);
	}

	public static void debugf(String info, Object... data) {
		log(Level.DEBUG, info, data);
	}

	public static void warn(Object info) {
		log(Level.WARN, info);
	}

	public static void warnf(String info, Object... data) {
		log(Level.WARN, info, data);
	}

	public static void info(Object info) {
		log(Level.INFO, info);
	}

	public static void infof(String info, Object... data) {
		log(Level.INFO, info, data);
	}

	public static void error(Object info) {
		log(Level.ERROR, info);
	}

	public static void errorf(String info, Object... data) {
		log(Level.ERROR, info, data);
	}
	
	public static void stacktrace(){
		try{
			throw new Exception();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
