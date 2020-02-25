package ca.cjloewen.base;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * A simplified deferred registry that instantiates classes and reports exceptions as crashes
 * automatically.
 *
 * @param <T> The base registry type.
 */
public abstract class BaseRegistry<T extends IForgeRegistryEntry<T>> extends DeferredRegister<T> {
	private IForgeRegistry<T> type;
	private String modid;
	
	public BaseRegistry(IForgeRegistry<T> reg, String modid) {
		super(reg, modid);
		type = reg;
		this.modid = modid;
	}
	
	/**
	 * Registers this registry.
	 * @return The registry, used for chaining.
	 */
	public BaseRegistry<T> register() {
		this.register(FMLJavaModLoadingContext.get().getModEventBus());
		return this;
	}
	
	/**
	 * Registers the entry.
	 * @param name The name of the entry.
	 * @param cls The class to instantiate and register.
	 */
	public <I extends T> RegistryObject<I> register(String name, Class<? extends I> cls) {
		return super.register(name, () -> {
			try {
				return cls.newInstance();
			} catch (InstantiationException e) {
				reportRegistryFailure("Unable to register object (instantiation exception)!", name, e);
			} catch (IllegalAccessException e) {
				reportRegistryFailure("Unable to register object (illegal access)!", name, e);
			}
			return null;
		});
	}
	
	/**
	 * Gracefully crashes when an entry fails to be registered.
	 * @param message The reason it failed to be registered.
	 * @param name The name of the entry.
	 * @param cause The cause of the failure.
	 * @return
	 */
	private Object reportRegistryFailure(String message, String name, Throwable cause) {
		CrashReport crashReport = CrashReport.makeCrashReport(cause,
			String.format("Unable to register %s!", type.getRegistryName().toString()));
		CrashReportCategory crashCategory = crashReport.makeCategory("Object being registered");
		crashCategory.addDetail("Resource location", () -> new ResourceLocation(modid, name).toString());
		throw new ReportedException(crashReport);
	}
}
