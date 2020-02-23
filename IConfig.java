package ca.cjloewen.base;

import javax.annotation.Nonnull;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.fml.config.ModConfig;

public interface IConfig {
	/**
	 * Builds the configuration.
	 * @param builder The unbuilt builder.
	 */
	public void build(@Nonnull Builder builder);
	/**
	 * Should populate the config with the new configurations.
	 * @param config The new mod configuration.
	 */
	public void bake(@Nonnull ModConfig config);
}
