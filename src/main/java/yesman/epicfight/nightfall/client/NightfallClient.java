package yesman.epicfight.nightfall.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.nightfall.NightfallItems;
import yesman.epicfight.nightfall.NightfallParticles;
import yesman.epicfight.nightfall.NightfallRegistries;

/// Pendaftaran sisi klien addon Nightfall (renderer mesh 3D senjata, partikel).
public final class NightfallClient {
	private NightfallClient() {}
	
	private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(NightfallRegistries.MODID, "textures/item/crimson_moon.png");
	
	public static void init() {
		ParticleFactoryRegistry.getInstance().register(NightfallParticles.CRIMSON_SLASH.get(), CrimsonSlashParticle.Provider::new);
		ParticleFactoryRegistry.getInstance().register(NightfallParticles.BLOOD_HARVEST.get(), CrimsonSlashParticle.Provider::new);
		ParticleFactoryRegistry.getInstance().register(NightfallParticles.BLOOD_DROP.get(), BloodDropParticle.Provider::new);
		ParticleFactoryRegistry.getInstance().register(NightfallParticles.SHADOW_SLASH.get(), CrimsonSlashParticle.Provider::new);
		ParticleFactoryRegistry.getInstance().register(NightfallParticles.SHADOW_HARVEST.get(), CrimsonSlashParticle.Provider::new);
		ParticleFactoryRegistry.getInstance().register(NightfallParticles.SHADOW_DROP.get(), BloodDropParticle.Provider::new);
		ParticleFactoryRegistry.getInstance().register(NightfallParticles.CRIMSON_HIT.get(), new CrimsonHitParticle.Provider());
		NightfallCameraShake.register();
		NightfallAfterimageLimiter.register();
		EpicFightClientEventHooks.Registry.PATCHED_ITEM.registerEvent(event ->
			event.addItemRenderer(ResourceLocation.fromNamespaceAndPath(NightfallRegistries.MODID, "mesh_item"), RenderMeshItem::new));
		
		// Render vanilla: kamera pertama, tangan pemain/mob non-Epic Fight, ikon inventori, item frame.
		registerBuiltin(NightfallItems.CRIMSON_MOON.get(), "crimson_moon");
		registerBuiltin(NightfallItems.SHADOW.get(), "shadow");
	}
	
	private static void registerBuiltin(net.minecraft.world.item.Item item, String key) {
		BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, context, poseStack, buffers, light, overlay) -> {
			RenderMeshItem.Look look = RenderMeshItem.look(key);
			
			if (context == ItemDisplayContext.GUI || context == ItemDisplayContext.GROUND || context == ItemDisplayContext.FIXED || context == ItemDisplayContext.HEAD) {
				BakedScythe.drawSprite(poseStack, buffers.getBuffer(RenderType.entityCutoutNoCull(look.icon)), 15728880);
				return;
			}
			
			if (!BakedScythe.ensure()) {
				return;
			}
			
			OpenMatrix4f matrix = RenderMeshItem.handInverse();
			RenderMeshItem.applyTuning(matrix, look.fpOffset, look.fpRotation, look.fpScale);
			
			poseStack.pushPose();
			poseStack.translate(0.5F, 0.5F, 0.5F);
			MathUtils.mulStack(poseStack, matrix);
			RenderMeshItem.drawMesh(poseStack, buffers, light, look);
			poseStack.popPose();
		});
	}
}
