package yesman.epicfight.nightfall;

import java.util.Set;

import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.platform.fabric.shim.registries.DeferredHolder;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.weaponinnate.SimpleWeaponInnateSkill;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

public final class NightfallSkills {
	private NightfallSkills() {}
	
	/// Skill bawaan sabit: Crimson Harvest (3 tebasan memutar). Mekanik Blood Power / Curse of Blood menyusul di tahap 2.
	public static final DeferredHolder<Skill, SimpleWeaponInnateSkill> SCYTHE_SKILL = NightfallRegistries.SKILLS.register("scythe_skill", key ->
		SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder()
			.setAnimations(NightfallScytheAnimations.SCYTHE_HARVEST)
			.newProperty()
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(3))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.85F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.SHORT)
				.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE))
			.build(key)
	);
}
