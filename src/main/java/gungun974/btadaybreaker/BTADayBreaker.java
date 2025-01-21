package gungun974.btadaybreaker;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BTADayBreaker implements ModInitializer {
    public static final String MOD_ID = "btadaybreaker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        LOGGER.info("BTA-DayBreaker initialized.");
    }
}
