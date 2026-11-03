package yesman.epicfight.nightfall.client;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.nightfall.NightfallParticles;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.nightfall.NightfallRegistries;

/// Pendaftaran sisi klien addon Nightfall (renderer mesh 3D senjata).
public final class NightfallClient {
	private NightfallClient() {}
	
	public static void init() {
		ParticleFactoryRegistry.getInstance().register(NightfallParticles.CRIMSON_SLASH.get(), CrimsonSlashParticle.Provider::new);
		NightfallCameraShake.register();
		EpicFightClientEventHooks.Registry.PATCHED_ITEM.registerEvent(event ->
			event.addItemRenderer(ResourceLocation.fromNamespaceAndPath(NightfallRegistries.MODID, "mesh_item"), RenderMeshItem::new));
	}
}
