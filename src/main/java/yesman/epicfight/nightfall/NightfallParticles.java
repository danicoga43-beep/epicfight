package yesman.epicfight.nightfall;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import yesman.epicfight.platform.fabric.shim.registries.DeferredHolder;

public final class NightfallParticles {
	private NightfallParticles() {}
	
	/// Memicu inisialisasi kelas (mendaftarkan holder).
	public static void init() {}
	
	/// Tebasan merah (flipbook 7 frame crimson_slash1..7). Parameter spawn: xSpeed = roll (derajat), ySpeed = ukuran.
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CRIMSON_SLASH = NightfallRegistries.PARTICLES.register("crimson_slash", () -> new SimpleParticleType(true));
}
