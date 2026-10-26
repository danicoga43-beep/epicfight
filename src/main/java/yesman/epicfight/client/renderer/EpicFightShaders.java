package yesman.epicfight.client.renderer;

import java.io.IOException;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.api.EnvType;
import yesman.epicfight.platform.fabric.shim.bus.SubscribeEvent;
import yesman.epicfight.platform.fabric.shim.bus.EventBusSubscriber;
import yesman.epicfight.platform.fabric.shim.clientevent.RegisterShadersEvent;
import yesman.epicfight.main.EpicFightMod;

@EventBusSubscriber(modid = EpicFightMod.MODID, value = EnvType.CLIENT)
public class EpicFightShaders {
	public static ShaderInstance positionColorNormalShader;
	
	@Nullable
	public static ShaderInstance getPositionColorNormalShader() {
		return positionColorNormalShader;
	}
	
	@SubscribeEvent
	public static void registerShadersEvent(RegisterShadersEvent event) throws IOException {
		event.registerShader(new ShaderInstance(event.getResourceProvider(), EpicFightMod.identifier("solid_model"), DefaultVertexFormat.POSITION_COLOR_NORMAL), reloadedShader -> {
			positionColorNormalShader = reloadedShader;
		});
	}
}
