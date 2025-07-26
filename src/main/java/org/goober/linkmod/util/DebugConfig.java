package org.goober.linkmod.util;

public class DebugConfig {
    // debug toggle - set to true to enable debug logging
    private static boolean DEBUG_ENABLED = false;
    
    public static boolean isDebugEnabled() {
        return DEBUG_ENABLED;
    }
    
    public static void setDebugEnabled(boolean enabled) {
        DEBUG_ENABLED = enabled;
    }
    
    public static void debug(String message) {
        if (DEBUG_ENABLED) {
            System.out.println("[LinkMod Debug] " + message);
        }
    }
}