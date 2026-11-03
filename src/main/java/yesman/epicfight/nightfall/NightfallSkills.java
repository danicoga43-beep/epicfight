package yesman.epicfight.nightfall;

import yesman.epicfight.platform.fabric.shim.registries.DeferredHolder;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;

public final class NightfallSkills {
	private NightfallSkills() {}
	
	/// Memicu inisialisasi kelas (mendaftarkan holder).
	public static void init() {}
	
	/// Skill bawaan sabit Crimson Moon (Blood Power, Harvest, Scarlet End, mode sarung).
	public static final DeferredHolder<Skill, ScytheSkill> SCYTHE_SKILL = NightfallRegistries.SKILLS.register("scythe_skill", key ->
		WeaponInnateSkill.createWeaponInnateBuilder(ScytheSkill::new)
			.setResource(Skill.Resource.NONE)
			.build(key)
	);
}
