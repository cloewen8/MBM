package ca.cjloewen.base;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * A class that collects and manages configurations.
 *
 * @param <A> The client configuration.
 * @param <B> The server configuration.
 */
public class ConfigHolder<A extends IConfig, B extends IConfig> {
	public final ForgeConfigSpec CONFIG_SPEC_CLIENT;
	public final ForgeConfigSpec CONFIG_SPEC_SERVER;
	public final A CONFIG_CLIENT;
	public final B CONFIG_SERVER;
	
	public ConfigHolder(A clientConfig, B serverConfig) {
		ForgeConfigSpec.Builder clientSpecBuilder = new ForgeConfigSpec.Builder();
		ForgeConfigSpec.Builder serverSpecBuilder = new ForgeConfigSpec.Builder();
		
		clientConfig.build(clientSpecBuilder);
		CONFIG_CLIENT = clientConfig;
		CONFIG_SPEC_CLIENT = clientSpecBuilder.build();
		
		serverConfig.build(serverSpecBuilder);
		CONFIG_SERVER = serverConfig;
		CONFIG_SPEC_SERVER = serverSpecBuilder.build();
	}
	
	/**
	 * Registers the configurations.
	 * @param mod The mod to register for.
	 * @return The ConfigHolder, used for chaining.
	 */
	public ConfigHolder<A, B> register(BaseMod mod) {
		ModLoadingContext modLoadingContext = ModLoadingContext.get();
		modLoadingContext.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, CONFIG_SPEC_CLIENT);
		modLoadingContext.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, CONFIG_SPEC_SERVER);
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener((ModConfig.ModConfigEvent event) -> {
			ModConfig config = event.getConfig();
			if (config.getSpec() == CONFIG_SPEC_CLIENT)
				CONFIG_CLIENT.bake(config);
			else if (config.getSpec() == CONFIG_SPEC_SERVER)
				CONFIG_SERVER.bake(config);
		});
		return this;
	}
}
