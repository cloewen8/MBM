package ca.cjloewen.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;

public class BaseMod {
	/**
	 * The main logger.
	 */
	protected static final Logger LOGGER = LogManager.getLogger();
	
	public BaseMod() {
		// todo: Register methods for registering things!
		
		// Register for server events.
		MinecraftForge.EVENT_BUS.register(this);
	}
}
