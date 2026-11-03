package yesman.epicfight.nightfall.client;

import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.nightfall.NightfallRegistries;

/// Pendaftaran sisi klien addon Nightfall (renderer mesh 3D senjata).
public final class NightfallClient {
	private NightfallClient() {}
	
	public static void init() {
		EpicFightClientEventHooks.Registry.PATCHED_ITEM.registerEvent(event ->
			event.addItemRenderer(ResourceLocation.fromNamespaceAndPath(NightfallRegistries.MODID, "mesh_item"), RenderMeshItem::new));
	}
}
