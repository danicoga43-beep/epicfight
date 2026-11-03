package yesman.epicfight.nightfall.client;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.client.renderer.texture.OverlayTexture;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec4f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.model.armature.HumanoidArmature;

/// Mesh Crimson Moon yang di-bake SEKALI ke koordinat lokal sendi Tool_R (semua titik hanya terikat ke satu sendi),
/// lalu digambar tanpa skinning per frame -> jauh lebih ringan dari SkinnedMesh/compute shader.
public final class BakedScythe {
	private BakedScythe() {}
	
	private static float[] pos;
	private static float[] nrm;
	private static float[] uv;
	private static int count;
	private static boolean failed;
	
	public static boolean ensure() {
		if (pos != null) {
			return true;
		}
		
		if (failed) {
			return false;
		}
		
		try {
			SkinnedMesh mesh = NightfallMeshes.CRIMSON_MOON.get();
			HumanoidArmature armature = Armatures.BIPED.get();
			
			if (mesh == null || armature == null) {
				return false;
			}
			
			OpenMatrix4f[] identity = new OpenMatrix4f[armature.getJointNumber()];
			
			for (int i = 0; i < identity.length; i++) {
				identity[i] = new OpenMatrix4f();
			}
			
			OpenMatrix4f toOrigin = armature.toolR.getToOrigin();
			FloatArrayList p = new FloatArrayList();
			FloatArrayList n = new FloatArrayList();
			FloatArrayList t = new FloatArrayList();
			
			mesh.drawPosed(new PoseStack(), null, (consumer, px, py, pz, nx, ny, nz, light, r, g, b, a, u, v, overlay) -> {
				Vec4f position = new Vec4f(px, py, pz, 1.0F);
				Vec4f normal = new Vec4f(nx, ny, nz, 0.0F);
				OpenMatrix4f.transform(toOrigin, position, position);
				OpenMatrix4f.transform(toOrigin, normal, normal);
				p.add(position.x); p.add(position.y); p.add(position.z);
				n.add(normal.x); n.add(normal.y); n.add(normal.z);
				t.add(u); t.add(v);
			}, 0, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, null, identity);
			
			pos = p.toFloatArray();
			nrm = n.toFloatArray();
			uv = t.toFloatArray();
			count = pos.length / 3;
			
			return count > 0;
		} catch (Exception e) {
			failed = true;
			EpicFightMod.LOGGER.error("Gagal bake mesh Crimson Moon", e);
			return false;
		}
	}
	
	/// Gambar mesh (triangle mode, pakai EpicFightRenderTypes.getTriangulated).
	public static void draw(PoseStack poseStack, VertexConsumer consumer, int light, float r, float g, float b, float a) {
		if (!ensure()) {
			return;
		}
		
		PoseStack.Pose last = poseStack.last();
		Matrix4f matrix = last.pose();
		
		for (int i = 0; i < count; i++) {
			int k = i * 3;
			consumer.addVertex(matrix, pos[k], pos[k + 1], pos[k + 2])
				.setColor(r, g, b, a)
				.setUv(uv[i * 2], uv[i * 2 + 1])
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(light)
				.setNormal(last, nrm[k], nrm[k + 1], nrm[k + 2]);
		}
	}
	
	/// Sprite 2D datar (ikon inventori / di tanah / item frame). Pakai RenderType mode QUADS.
	public static void drawSprite(PoseStack poseStack, VertexConsumer consumer, int light) {
		PoseStack.Pose last = poseStack.last();
		Matrix4f matrix = last.pose();
		float[][] v = { {0, 0, 0, 1}, {1, 0, 1, 1}, {1, 1, 1, 0}, {0, 1, 0, 0} };
		
		for (float[] c : v) {
			consumer.addVertex(matrix, c[0], c[1], 0.5F).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(c[2], c[3])
				.setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(last, 0.0F, 0.0F, 1.0F);
		}
	}
}
