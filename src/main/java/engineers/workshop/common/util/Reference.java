package engineers.workshop.common.util;

public class Reference {

    public static final class Info {
        public static final String MODID = "engineersworkshop";
        public static final String NAME = "Engineers Workshop";
        static final String VersionMajor = "1";
        static final String VersionMinor = "1";
        static final String VersionPatch = "4";
        public static final String MinecraftVersion = "1.10.2";
        public static final String BuildVersion = VersionMajor + "." + VersionMinor + "." + VersionPatch + "-" + MinecraftVersion;
    }

    public static final class Paths {
        public static final String CLIENT_PROXY = "engineers.workshop.proxy.ClientProxy";
        public static final String COMMON_PROXY = "engineers.workshop.proxy.CommonProxy";
    }
}
