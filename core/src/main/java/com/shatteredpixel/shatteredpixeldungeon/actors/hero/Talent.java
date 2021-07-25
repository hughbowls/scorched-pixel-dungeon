/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AdrenalineSurge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnhancedRings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stamina;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TrollEncourage;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WandEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BloodParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.ElementalSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Pistol;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public enum Talent {

	//Warrior T1
	HEARTY_MEAL(0), ARMSMASTERS_INTUITION(1), TEST_SUBJECT(2), IRON_WILL(3),
	//Warrior T2
	IRON_STOMACH(4), RESTORED_WILLPOWER(5), RUNIC_TRANSFERENCE(6), LETHAL_MOMENTUM(7), IMPROVISED_PROJECTILES(8),
	//Warrior T3
	HOLD_FAST(9, 3), STRONGMAN(10, 3),
	//Berserker T3
	ENDLESS_RAGE(11, 3), BERSERKING_STAMINA(12, 3), ENRAGED_CATALYST(13, 3),
	//Gladiator T3
	CLEAVE(14, 3), LETHAL_DEFENSE(15, 3), ENHANCED_COMBO(16, 3),
	//Heroic Leap T4
	BODY_SLAM(17, 4), IMPACT_WAVE(18, 4), DOUBLE_JUMP(19, 4),
	//Shockwave T4
	EXPANDING_WAVE(20, 4), STRIKING_WAVE(21, 4), SHOCK_FORCE(22, 4),
	//Endure T4
	SUSTAINED_RETRIBUTION(23, 4), SHRUG_IT_OFF(24, 4), EVEN_THE_ODDS(25, 4),

	//Mage T1
	EMPOWERING_MEAL(32), SCHOLARS_INTUITION(33), TESTED_HYPOTHESIS(34), BACKUP_BARRIER(35),
	//Mage T2
	ENERGIZING_MEAL(36), ENERGIZING_UPGRADE(37), WAND_PRESERVATION(38), ARCANE_VISION(39), SHIELD_BATTERY(40),
	//Mage T3
	EMPOWERING_SCROLLS(41, 3), ALLY_WARP(42, 3),
	//Battlemage T3
	EMPOWERED_STRIKE(43, 3), MYSTICAL_CHARGE(44, 3), EXCESS_CHARGE(45, 3),
	//Warlock T3
	SOUL_EATER(46, 3), SOUL_SIPHON(47, 3), NECROMANCERS_MINIONS(48, 3),
	//Elemental Blast T4
	BLAST_RADIUS(49, 4), ELEMENTAL_POWER(50, 4), REACTIVE_BARRIER(51, 4),
	//Wild Magic T4
	WILD_POWER(52, 4), FIRE_EVERYTHING(53, 4), CONSERVED_MAGIC(54, 4),
	//Warp Beacon T4
	TELEFRAG(55, 4), REMOTE_BEACON(56, 4), LONGRANGE_WARP(57, 4),

	//Rogue T1
	CACHED_RATIONS(64), THIEFS_INTUITION(65), SUCKER_PUNCH(66), PROTECTIVE_SHADOWS(67),
	//Rogue T2
	MYSTICAL_MEAL(68), MYSTICAL_UPGRADE(69), WIDE_SEARCH(70), SILENT_STEPS(71), ROGUES_FORESIGHT(72),
	//Rogue T3
	ENHANCED_RINGS(73, 3), LIGHT_CLOAK(74, 3),
	//Assassin T3
	ENHANCED_LETHALITY(75, 3), ASSASSINS_REACH(76, 3), BOUNTY_HUNTER(77, 3),
	//Freerunner T3
	EVASIVE_ARMOR(78, 3), PROJECTILE_MOMENTUM(79, 3), SPEEDY_STEALTH(80, 3),
	//Smoke Bomb T4
	HASTY_RETREAT(81, 4), BODY_REPLACEMENT(82, 4), SHADOW_STEP(83, 4),
	//Death Mark T4
	FEAR_THE_REAPER(84, 4), DEATHLY_DURABILITY(85, 4), DOUBLE_MARK(86, 4),
	//Shadow Clone T4
	SHADOW_BLADE(87, 4), CLONED_ARMOR(88, 4), PERFECT_COPY(89, 4),

	//Huntress T1
	NATURES_BOUNTY(96), SURVIVALISTS_INTUITION(97), FOLLOWUP_STRIKE(98), NATURES_AID(99),
	//Huntress T2
	INVIGORATING_MEAL(100), RESTORED_NATURE(101), REJUVENATING_STEPS(102), HEIGHTENED_SENSES(103), DURABLE_PROJECTILES(104),
	//Huntress T3
	POINT_BLANK(105, 3), SEER_SHOT(106, 3),
	//Sniper T3
	FARSIGHT(107, 3), SHARED_ENCHANTMENT(108, 3), SHARED_UPGRADES(109, 3),
	//Warden T3
	DURABLE_TIPS(110, 3), BARKSKIN(111, 3), SHIELDING_DEW(112, 3),
	//Spectral Blades T4
	FAN_OF_BLADES(113, 4), PROJECTING_BLADES(114, 4), SPIRIT_BLADES(115, 4),
	//Natures Power T4
	GROWING_POWER(116, 4), NATURES_WRATH(117, 4), WILD_MOMENTUM(118, 4),
	//Spirit Hawk T4
	EAGLE_EYE(119, 4), GO_FOR_THE_EYES(120, 4), SWIFT_SPIRIT(121, 4),

	//Heretic T1
	BUTCHERY(128), ACCURSEDS_INTUITION(129), KNOWLEDGE_IS_POWER(130), MALEVOLENT_ARMOR(131),
	//Heretic T2
	ASCETIC(132), SUBLIMATION(133), CHAOS_ADEPT(134), OVERWHELM(135), RUNE_OF_CURSE(136),
	//Heretic T3
	ENHANCED_CURSE(137, 3), TRANSFER_HARM(138, 3),
	//Blood Knight T3
	PREDATOR(139, 3), RUSH_OF_BLOOD(140, 3), SERUM(141, 3),
	//Summoner T3
	FIEND_WARP(142, 3), PACT_OF_KNOT(143, 3), CURSED_CLAW(144, 3),
	//Metamorphosis T4
	CARNAGE(145, 4), RIPPING_CLAW(146, 4), CRIPPLING_STING(147, 4),
	//Death Gaze T4
	HELLISH_RESISTANCE(148, 4), AGONIZING_GAZE(149, 4), SERIAL_GAZER(150, 4),
	//Curse of Conflict T4
	EATER_OF_CONFLICT(151, 4), END_OF_CONFLICT(152, 4), CENTER_OF_CONFLICT(153, 4),

	//Alchemist T1
	FOOD_ALCHEMY(160), INVENTORS_INTUITION(161), PREEMPTIVE_FIRE(162), EXPERIMENTAL_BARRIER(163),
	//Alchemist T2
	FRESH_MEAL(164), RELOADING_UPGRADE(165), ELIXIR_FORMULA(166), REBREATHER(167), GASEOUS_WARFARE(168),
	//Alchemist T3
	ADVANCED_PISTOL(169, 3), PORTABLE_KIT(170, 3),
	//Trailblazer T3
	STEADY_AIM(171, 3), INFUSED_GUNSMITH(172, 3), DOUBLE_TAP(173, 3),
	//Innovator T3
	DEWCHEMY(174, 3), ARCANE_ENGINEER(175, 3), CATALYST_MK2(176, 3),
	//Exoskeleton T4
	HYDRAULIC_PISTON(177, 4), DEPLOY_BARRIER(178, 4), KINETIC_CHARGER(179, 4),
	//Siege Machine T4
	SHARED_ROUND(180, 4), JUGGERNAUT(181, 4), TRANSPORTER(182, 4),
	//Mount N' Load T4
	HAIL_OF_SHOTS(183, 4), FMJ(184, 4), TRENCH_WARFARE(185, 4),

	//Elementalist T1
	SHIELDING_MEAL(192), PYTHONESS_INTUITION(193), EXTENDED_FOCUS(194), ELEMENTAL_SHIELD(195),
	//Elementalist T2
	PENETRATING_MEAL(196), HYDROMANCER(197), ICEMAIL(198), CHAIN_LIGHTNING(199), WILDFIRE(200),
	//Elementalist T3
	ELEMENTAL_MASTER(201, 3), ELEMENT_OF_CHAOS(202, 3),
	//Binder T3
	ARDENT_BLADE(203, 3), WALKING_GLACIER(204, 3), MIGHTY_THUNDER(205, 3),
	//Spellweaver T3
	DEVASTATOR(206, 3), TRAVELER(207, 3), CLAIRVOYANT(208, 3),
	//Resonance T4
	ECHOING_RESONANCE(209, 4), REACTIVE_RESONANCE(210, 4), DOUBLE_RESONANCE(211, 4),
	//Aether Step T4
	AFTERSHOCK(212, 4), AETHER_VISION(213, 4), AETHER_TETHER(214, 4),
	//Elemental Conduit T4
	CONDUIT_RELAY(215, 4), STABILIZED_CONDUIT(216, 4), ELEMENTAL_AMPLIFIER(217, 4),

	//Blacksmith T1
	ENCOURAGING_MEAL(224), ARTISANS_INTUITION(225), TESTED_STAMINA(226), ARMORED_TROLL(227),
	//Blacksmith T2
	CLEANSING_MEAL(228), REGENERATION(229), INDUSTRIOUS_HANDS(230), BOULDER_IS_COMING(231), MARKSTROLL(232),
	//Blacksmith T3
	UPGRADE_MASTERY(233, 3), MINT_MASTER(234, 3),
	//Warsmith T3
	ARCANESMITH(235, 3), CRUCIBLE(236, 3), WANDTROLL(237, 3),
	//Geomancer T3
	HARMONY(238, 3), AVALANCHE(239, 3), LANDSLIDER(240, 3),
	//Sapper T4
	EMERGENCY_STAIR(241, 4), LOCKSMITH(242, 4), TRAP_DISPOSAL(243, 4),
	//Cape of Thorns T4
	SHARP_THORNS(244, 4), CAPE_OF_VICTORIOUS(245, 4), CAPE_OF_ARCANE(246, 4),
	//Grand Grapple T4
	SHOT_PUT(247, 4), DAZING_COLLIDE(248, 4), GREAT_COMMUNICATOR(249, 4),

	//universal T4
	HEROIC_ENERGY(26, 4), //See icon() and title() for special logic for this one
	//Ratmogrify T4
	RATSISTANCE(124, 4), RATLOMACY(125, 4), RATFORCEMENTS(126, 4);

	public static class ImprovisedProjectileCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.15f, 0.2f, 0.5f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 50); }
		public String toString() { return Messages.get(this, "name"); }
		public String desc() { return Messages.get(this, "desc", dispTurns(visualcooldown())); }
	};
	public static class LethalMomentumTracker extends FlavourBuff{};
	public static class WandPreservationCounter extends CounterBuff{};
	public static class EmpoweredStrikeTracker extends FlavourBuff{};
	public static class BountyHunterTracker extends FlavourBuff{};
	public static class RejuvenatingStepsCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0f, 0.35f, 0.15f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / (15 - 5*Dungeon.hero.pointsInTalent(REJUVENATING_STEPS))); }
		public String toString() { return Messages.get(this, "name"); }
		public String desc() { return Messages.get(this, "desc", dispTurns(visualcooldown())); }
	};
	public static class RejuvenatingStepsFurrow extends CounterBuff{};
	public static class SeerShotCooldown extends FlavourBuff{
		public int icon() { return target.buff(RevealedArea.class) != null ? BuffIndicator.NONE : BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.7f, 0.4f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 20); }
		public String toString() { return Messages.get(this, "name"); }
		public String desc() { return Messages.get(this, "desc", dispTurns(visualcooldown())); }
	};
	public static class SpiritBladesTracker extends FlavourBuff{};

	public static class TransferHarmCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.7f, 0.0f, 0.0f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / (Dungeon.hero.pointsInTalent(TRANSFER_HARM) >= 3 ? 10 : 20)); }
		public String toString() { return Messages.get(this, "name"); }
		public String desc() { return Messages.get(this, "desc", dispTurns(visualcooldown())); }
	};
	public static class PactOfKnotCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.7f, 0.0f, 0.2f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 100); }
		public String toString() { return Messages.get(this, "name"); }
		public String desc() { return Messages.get(this, "desc", dispTurns(visualcooldown())); }
	};
	public static class RebreatherCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0x00C7C7); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 20); }
		public String toString() { return Messages.get(this, "name"); }
		public String desc() { return Messages.get(this, "desc", dispTurns(visualcooldown())); }
	};
	public static class TransporterCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0xFFFF00); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / (36-6*(Dungeon.hero.pointsInTalent(TRANSPORTER)))); }
		public String toString() { return Messages.get(this, "name"); }
		public String desc() { return Messages.get(this, "desc", dispTurns(visualcooldown())); }
	};
	public static class BoulderIsComingCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0xB0ADB7); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 10); }
		public String toString() { return Messages.get(this, "name"); }
		public String desc() { return Messages.get(this, "desc", dispTurns(visualcooldown())); }
	};
	public static class MarksTrollCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0xFFCF32); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / (10-(Dungeon.hero.pointsInTalent(MARKSTROLL) == 2 ? 4 : 0))); }
		public String toString() { return Messages.get(this, "name"); }
		public String desc() { return Messages.get(this, "desc", dispTurns(visualcooldown())); }
	};
	public static class UpgradeMasterTracker extends Buff{};
	public static class CrucibleTracker extends FlavourBuff{
		public int icon() { return BuffIndicator.VULNERABLE; }
		public void tintIcon(Image icon) { icon.hardlight(0xFF2A00); }
		public String toString() { return Messages.get(this, "name"); }
		public String desc() { return Messages.get(this, "desc"); }
	};

	int icon;
	int maxPoints;

	// tiers 1/2/3/4 start at levels 2/7/13/21
	public static int[] tierLevelThresholds = new int[]{0, 2, 7, 13, 21, 31};

	Talent( int icon ){
		this(icon, 2);
	}

	Talent( int icon, int maxPoints ){
		this.icon = icon;
		this.maxPoints = maxPoints;
	}

	public int icon(){
		if (this == HEROIC_ENERGY){
			if (Dungeon.hero != null && Dungeon.hero.armorAbility instanceof Ratmogrify){
				return 127;
			}
			HeroClass cls = Dungeon.hero != null ? Dungeon.hero.heroClass : GamesInProgress.selectedClass;
			switch (cls){
				case WARRIOR: default:
					return 26;
				case MAGE:
					return 58;
				case ROGUE:
					return 90;
				case HUNTRESS:
					return 122;
				case HERETIC:
					return 154;
				case ALCHEMIST:
					return 186;
				case ELEMENTALIST:
					return 218;
				case TROLL:
					return 250;
			}
		} else {
			return icon;
		}
	}

	public int maxPoints(){
		return maxPoints;
	}

	public String title(){
		//TODO translate this
		if (this == HEROIC_ENERGY &&
				Messages.lang() == Languages.ENGLISH
				&& Dungeon.hero != null
				&& Dungeon.hero.armorAbility instanceof Ratmogrify){
			return "ratroic energy";
		}
		return Messages.get(this, name() + ".title");
	}

	public String desc(){
		return Messages.get(this, name() + ".desc");
	}

	public static void onTalentUpgraded( Hero hero, Talent talent){
		if (talent == NATURES_BOUNTY){
			if ( hero.pointsInTalent(NATURES_BOUNTY) == 1) Buff.count(hero, NatureBerriesAvailable.class, 4);
			else                                           Buff.count(hero, NatureBerriesAvailable.class, 2);
		}

		if (talent == BUTCHERY && hero.pointsInTalent(BUTCHERY) == 0){
			Buff.count(hero, ButcheryMeatAvailable.class, 4);
		}

		if (talent == ARMSMASTERS_INTUITION && hero.pointsInTalent(ARMSMASTERS_INTUITION) == 2){
			if (hero.belongings.weapon != null) hero.belongings.weapon.identify();
			if (hero.belongings.armor != null)  hero.belongings.armor.identify();
		}
		if (talent == THIEFS_INTUITION && hero.pointsInTalent(THIEFS_INTUITION) == 2){
			if (hero.belongings.ring instanceof Ring) hero.belongings.ring.identify();
			if (hero.belongings.misc instanceof Ring) hero.belongings.misc.identify();
			for (Item item : Dungeon.hero.belongings){
				if (item instanceof Ring){
					((Ring) item).setKnown();
				}
			}
		}
		if (talent == THIEFS_INTUITION && hero.pointsInTalent(THIEFS_INTUITION) == 1){
			if (hero.belongings.ring instanceof Ring) hero.belongings.ring.setKnown();
			if (hero.belongings.misc instanceof Ring) ((Ring) hero.belongings.misc).setKnown();
		}

		if (talent == LIGHT_CLOAK && hero.pointsInTalent(LIGHT_CLOAK) == 1){
			for (Item item : Dungeon.hero.belongings.backpack){
				if (item instanceof CloakOfShadows){
					((CloakOfShadows) item).activate(Dungeon.hero);
				}
			}
		}

		if (talent == HEIGHTENED_SENSES || talent == FARSIGHT){
			Dungeon.observe();
		}

		if (talent == ACCURSEDS_INTUITION){
			for (Item item : Dungeon.hero.belongings){
				item.cursedKnown = true;
				if (hero.pointsInTalent(ACCURSEDS_INTUITION) == 1){
					if (item.isEquipped(Dungeon.hero)){
						if (item instanceof MeleeWeapon && ((MeleeWeapon)item).hasCurseEnchant()){
							item.identify();
						} else if (item instanceof Armor && ((Armor)item).hasCurseGlyph()){
							item.identify();
						} else if (item.cursed){
							item.identify();
						}
					}
				} else if (hero.pointsInTalent(ACCURSEDS_INTUITION) == 2){
					if (item instanceof MeleeWeapon && ((MeleeWeapon)item).hasCurseEnchant()){
						item.identify();
					} else if (item instanceof Armor && ((Armor)item).hasCurseGlyph()){
						item.identify();
					} else if (item.cursed){
						item.identify();
					}
				}
			}
		}

		if (talent == ADVANCED_PISTOL && hero.pointsInTalent(ADVANCED_PISTOL) == 3){
			for (Item item : Dungeon.hero.belongings){
				if (item instanceof Pistol){
					((Pistol) item).setReloadTime(1f);
				}
			}
		}

		if (talent == ELEMENT_OF_CHAOS && hero.pointsInTalent(ELEMENT_OF_CHAOS) == 1){
			ElementalSpell.ElementalSpellChaos chaos = new ElementalSpell.ElementalSpellChaos();
			if (chaos.doPickUp( Dungeon.hero )) {
				GLog.i( Messages.get(Dungeon.hero, "you_now_have", chaos.name() ));
			} else {
				Dungeon.level.drop( chaos, Dungeon.hero.pos ).sprite.drop();
			}
		}

		if (talent == UPGRADE_MASTERY && hero.pointsInTalent(UPGRADE_MASTERY) == 3){
			UpgradeMasterTracker reforged = Dungeon.hero.buff(UpgradeMasterTracker.class);
			if (reforged == null) Buff.affect(Dungeon.hero, UpgradeMasterTracker.class);
		}

		if (talent == MINT_MASTER){
			Gold gold = new Gold();
			switch (hero.pointsInTalent(MINT_MASTER)) {
				case 1: gold.quantity(3000).doPickUp(Dungeon.hero); break;
				case 2: gold.quantity(3500).doPickUp(Dungeon.hero); break;
				case 3: gold.quantity(4000).doPickUp(Dungeon.hero); break;
			}
		}
	}

	public static class CachedRationsDropped extends CounterBuff{};
	public static class NatureBerriesAvailable extends CounterBuff{};
	public static class ButcheryMeatAvailable extends CounterBuff{};

	public static void onFoodEaten( Hero hero, float foodVal, Item foodSource ){
		if (hero.hasTalent(HEARTY_MEAL)){
			//3/5 HP healed, when hero is below 25% health
			if (hero.HP <= hero.HT/4) {
				hero.HP = Math.min(hero.HP + 1 + 2 * hero.pointsInTalent(HEARTY_MEAL), hero.HT);
				hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1+hero.pointsInTalent(HEARTY_MEAL));
			//2/3 HP healed, when hero is below 50% health
			} else if (hero.HP <= hero.HT/2){
				hero.HP = Math.min(hero.HP + 1 + hero.pointsInTalent(HEARTY_MEAL), hero.HT);
				hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), hero.pointsInTalent(HEARTY_MEAL));
			}
		}
		if (hero.hasTalent(IRON_STOMACH)){
			if (hero.cooldown() > 0) {
				Buff.affect(hero, WarriorFoodImmunity.class, hero.cooldown());
			}
		}
		if (hero.hasTalent(EMPOWERING_MEAL)){
			//2/3 bonus wand damage for next 3 zaps
			Buff.affect( hero, WandEmpower.class).set(1 + hero.pointsInTalent(EMPOWERING_MEAL), 3);
			ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(ENERGIZING_MEAL)){
			//5/8 turns of recharging
			Buff.prolong( hero, Recharging.class, 2 + 3*(hero.pointsInTalent(ENERGIZING_MEAL)) );
			ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(MYSTICAL_MEAL)){
			//3/5 turns of recharging
			Buff.affect( hero, ArtifactRecharge.class).set(1 + 2*(hero.pointsInTalent(MYSTICAL_MEAL))).ignoreHornOfPlenty = foodSource instanceof HornOfPlenty;
			ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(INVIGORATING_MEAL)){
			//effectively 1/2 turns of haste
			Buff.prolong( hero, Haste.class, 0.67f+hero.pointsInTalent(INVIGORATING_MEAL));
		}
		if (hero.hasTalent(FRESH_MEAL)){
			//5/8 turns of blob immunity
			Buff.affect( hero, BlobImmunity.class, 2 + 3*(hero.pointsInTalent(FRESH_MEAL)));
		}
		if (hero.hasTalent(SHIELDING_MEAL)){
			//grants 5/8 shielding
			Buff.affect(Dungeon.hero, Barrier.class).setShield(2 + 3*hero.pointsInTalent(Talent.SHIELDING_MEAL));
		}
		if (hero.hasTalent(PENETRATING_MEAL)){
			//3/5 turns of magical sight
			Buff.affect(Dungeon.hero, MagicalSight.class, 1 + 2*hero.pointsInTalent(Talent.PENETRATING_MEAL));
			Dungeon.observe();
		}
		if (hero.hasTalent(ENCOURAGING_MEAL)){
			//2/3 bonus melee & throwing damage for next 3 attacks
			Buff.affect( hero, TrollEncourage.class).set(1 + hero.pointsInTalent(ENCOURAGING_MEAL), 3);
		}
		if (hero.hasTalent(CLEANSING_MEAL)){
			//cleanse self/+nearby area
			for (Buff b : Dungeon.hero.buffs()){
				if (b.type == Buff.buffType.NEGATIVE && !(b instanceof Corruption || b instanceof Hunger))
					b.detach();
			}
			if (hero.pointsInTalent(CLEANSING_MEAL) == 2) PotionOfPurity.purify(hero.pos);
		}
	}

	public static class WarriorFoodImmunity extends FlavourBuff{
		{ actPriority = HERO_PRIO+1; }
	}
	public static class TrollRegeneration extends FlavourBuff{}

	public static float itemIDSpeedFactor( Hero hero, Item item ){
		// 1.75x/2.5x speed with huntress talent
		float factor = 1f + hero.pointsInTalent(SURVIVALISTS_INTUITION)*0.75f;

		// 2x/instant for Warrior (see onItemEquipped)
		if (item instanceof MeleeWeapon || item instanceof Armor){
			factor *= 1f + hero.pointsInTalent(ARMSMASTERS_INTUITION);
		}
		// 3x/instant for mage (see Wand.wandUsed())
		if (item instanceof Wand){
			factor *= 1f + 2*hero.pointsInTalent(SCHOLARS_INTUITION);
		}
		// 2x/instant for rogue (see onItemEqupped), also id's type on equip/on pickup
		if (item instanceof Ring){
			factor *= 1f + hero.pointsInTalent(THIEFS_INTUITION);
		}
		return factor;
	}

	public static void onHealingPotionUsed( Hero hero ){
		if (hero.hasTalent(RESTORED_WILLPOWER)){
			BrokenSeal.WarriorShield shield = hero.buff(BrokenSeal.WarriorShield.class);
			if (shield != null){
				int shieldToGive = Math.round(shield.maxShield() * 0.33f*(1+hero.pointsInTalent(RESTORED_WILLPOWER)));
				shield.supercharge(shieldToGive);
			}
		}
		if (hero.hasTalent(RESTORED_NATURE)){
			ArrayList<Integer> grassCells = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS8){
				grassCells.add(hero.pos+i);
			}
			Random.shuffle(grassCells);
			for (int cell : grassCells){
				Char ch = Actor.findChar(cell);
				if (ch != null && ch.alignment == Char.Alignment.ENEMY){
					Buff.affect(ch, Roots.class, 1f + hero.pointsInTalent(RESTORED_NATURE));
				}
				if (Dungeon.level.map[cell] == Terrain.EMPTY ||
						Dungeon.level.map[cell] == Terrain.EMBERS ||
						Dungeon.level.map[cell] == Terrain.EMPTY_DECO){
					Level.set(cell, Terrain.GRASS);
					GameScene.updateMap(cell);
				}
				CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
			}
			if (hero.pointsInTalent(RESTORED_NATURE) == 1){
				grassCells.remove(0);
				grassCells.remove(0);
				grassCells.remove(0);
			}
			for (int cell : grassCells){
				int t = Dungeon.level.map[cell];
				if ((t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
						|| t == Terrain.GRASS || t == Terrain.FURROWED_GRASS)
						&& Dungeon.level.plants.get(cell) == null){
					Level.set(cell, Terrain.HIGH_GRASS);
					GameScene.updateMap(cell);
				}
			}
			Dungeon.observe();
		}
	}

	public static void onUpgradeScrollUsed( Hero hero ){
		if (hero.hasTalent(ENERGIZING_UPGRADE)){
			MagesStaff staff = hero.belongings.getItem(MagesStaff.class);
			if (staff != null){
				staff.gainCharge(1 + 2*hero.pointsInTalent(ENERGIZING_UPGRADE), true);
				ScrollOfRecharging.charge( Dungeon.hero );
				SpellSprite.show( hero, SpellSprite.CHARGE );
			}
		}
		if (hero.hasTalent(MYSTICAL_UPGRADE)){
			CloakOfShadows cloak = hero.belongings.getItem(CloakOfShadows.class);
			if (cloak != null){
				cloak.overCharge(1 + hero.pointsInTalent(MYSTICAL_UPGRADE));
				ScrollOfRecharging.charge( Dungeon.hero );
				SpellSprite.show( hero, SpellSprite.CHARGE );
			}
		}
		if (hero.hasTalent(RELOADING_UPGRADE)){
			Pistol pistol = hero.belongings.getItem(Pistol.class);
			if (pistol != null){
				pistol.reload_talent();
			}
			Dungeon.hero.sprite.emitter().start(MagicMissile.MagicParticle.ATTRACTING, 0.025f, 10 );
		}
	}

	public static void onArtifactUsed( Hero hero ){
		if (hero.hasTalent(ENHANCED_RINGS)){
			Buff.prolong(hero, EnhancedRings.class, 3f*hero.pointsInTalent(ENHANCED_RINGS));
		}
	}

	public static void onItemEquipped( Hero hero, Item item ){
		if (hero.pointsInTalent(ARMSMASTERS_INTUITION) == 2 && (item instanceof Weapon || item instanceof Armor)){
			item.identify();
		}
		if (hero.hasTalent(THIEFS_INTUITION) && item instanceof Ring){
			if (hero.pointsInTalent(THIEFS_INTUITION) == 2){
				item.identify();
			} else {
				((Ring) item).setKnown();
			}
		}
		if (hero.hasTalent(ACCURSEDS_INTUITION)){
			if (item instanceof MeleeWeapon && ((MeleeWeapon)item).hasCurseEnchant()){
				item.identify();
			} else if (item instanceof Armor && ((Armor)item).hasCurseGlyph()){
				item.identify();
			} else if (item.cursed){
				item.identify();
			}
		}
	}

	public static void onItemCollected( Hero hero, Item item ){
		if (hero.pointsInTalent(THIEFS_INTUITION) == 2){
			if (item instanceof Ring) ((Ring) item).setKnown();
		}
		if (hero.hasTalent(ACCURSEDS_INTUITION)){
			item.cursedKnown = true;
			if (hero.pointsInTalent(ACCURSEDS_INTUITION) == 2){
				if (item instanceof MeleeWeapon && ((MeleeWeapon)item).hasCurseEnchant()){
					item.identify();
				} else if (item instanceof Armor && ((Armor)item).hasCurseGlyph()){
					item.identify();
				} else if (item.cursed){
					item.identify();
				}
			}
		}
		if (hero.pointsInTalent(ADVANCED_PISTOL) == 3){
			if (item instanceof Pistol){
				((Pistol) item).setReloadTime(1f);
			}
		}
	}

	//note that IDing can happen in alchemy scene, so be careful with VFX here
	public static void onItemIdentified( Hero hero, Item item ){
		if (hero.hasTalent(TEST_SUBJECT)){
			//heal for 2/3 HP
			hero.HP = Math.min(hero.HP + 1 + hero.pointsInTalent(TEST_SUBJECT), hero.HT);
			Emitter e = hero.sprite.emitter();
			if (e != null) e.burst(Speck.factory(Speck.HEALING), hero.pointsInTalent(TEST_SUBJECT));
		}
		if (hero.hasTalent(TESTED_HYPOTHESIS)){
			//2/3 turns of wand recharging
			Buff.affect(hero, Recharging.class, 1f + hero.pointsInTalent(TESTED_HYPOTHESIS));
			ScrollOfRecharging.charge(hero);
		}
		if (hero.hasTalent(KNOWLEDGE_IS_POWER)){
			//20/30 turns of adrenaline surge with 1 boost
			Buff.affect(hero, AdrenalineSurge.class).add(1, 10f + 10f*(float)hero.pointsInTalent(KNOWLEDGE_IS_POWER));
		}
		if (hero.hasTalent(TESTED_STAMINA)){
			//6/10 turns of stamina
			Buff.affect(hero, Stamina.class, hero.pointsInTalent(TESTED_STAMINA) == 2 ? 10f : 6f);
		}
	}

	public static int onAttackProc( Hero hero, Char enemy, int dmg ){
		if (hero.hasTalent(Talent.SUCKER_PUNCH)
				&& enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)
				&& enemy.buff(SuckerPunchTracker.class) == null){
			dmg += Random.IntRange(hero.pointsInTalent(Talent.SUCKER_PUNCH) , 2);
			Buff.affect(enemy, SuckerPunchTracker.class);
		}

		if (hero.hasTalent(Talent.FOLLOWUP_STRIKE)) {
			if (hero.belongings.weapon instanceof MissileWeapon) {
				Buff.affect(enemy, FollowupStrikeTracker.class);
			} else if (enemy.buff(FollowupStrikeTracker.class) != null){
				dmg += 1 + hero.pointsInTalent(FOLLOWUP_STRIKE);
				if (!(enemy instanceof Mob) || !((Mob) enemy).surprisedBy(hero)){
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG, 0.75f, 1.2f);
				}
				enemy.buff(FollowupStrikeTracker.class).detach();
			}
		}

		if (hero.hasTalent(PREEMPTIVE_FIRE)) {
			if (hero.belongings.weapon instanceof Pistol.PistolShot) {
				if (enemy.buff(PreemptiveFireTracker.class) == null) {
					dmg += hero.pointsInTalent(PREEMPTIVE_FIRE);
					Buff.affect(enemy, PreemptiveFireTracker.class);
				}
			}
		}

		TrollEncourage encourage = hero.buff(TrollEncourage.class);
		if (encourage != null){
			if (hero.belongings.weapon instanceof MeleeWeapon
					|| hero.belongings.weapon instanceof MissileWeapon
					|| (hero.belongings.weapon == null
						&& RingOfForce.getBuffedBonus(hero, RingOfForce.Force.class) > 0)) {
				dmg += encourage.dmgBoost;
				encourage.left--;
				if (encourage.left <= 0) {
					encourage.detach();
				}
			}
		}

		if (hero.hasTalent(Talent.TRANSFER_HARM)
				&& enemy instanceof Mob
				&& hero.belongings.weapon instanceof MeleeWeapon
				&& (((MeleeWeapon)hero.belongings.weapon).hasCurseEnchant()
					|| hero.belongings.weapon.cursed)
				&& hero.buff(TransferHarmCooldown.class) == null
				&& enemy.buff(TransferHarmTracker.class) == null
				&& (hero.buff(Poison.class) != null || hero.buff(Cripple.class) != null
				|| hero.buff(Weakness.class) != null || hero.buff(Vulnerable.class) != null
				|| hero.buff(Hex.class) != null || hero.buff(Bleeding.class) != null
				|| hero.buff(Slow.class) != null || hero.buff(Vertigo.class) != null
				|| hero.buff(Burning.class) != null || hero.buff(Chill.class) != null
				|| hero.buff(Ooze.class) != null || hero.buff(Roots.class) != null
				|| hero.buff(Blindness.class) != null)){

			enemy.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
			Buff.affect(hero, TransferHarmCooldown.class, 20f - (hero.pointsInTalent(TRANSFER_HARM) >= 2 ? 10f : 0f));
			Buff.affect(enemy, TransferHarmTracker.class);

			if (hero.buff(Poison.class) != null) {
				Buff.affect(enemy, Poison.class).set(Math.round(dmg * 0.6f));
				CellEmitter.center( enemy.pos ).burst( PoisonParticle.SPLASH, 3 );
			} if (hero.buff(Cripple.class) != null) { Buff.affect( enemy, Cripple.class, Math.round(dmg*0.6f)); }
			if (hero.buff(Weakness.class) != null) { Buff.affect( enemy, Weakness.class, Math.round(dmg*0.6f)); }
			if (hero.buff(Vulnerable.class) != null) { Buff.affect( enemy, Vulnerable.class, Math.round(dmg*0.6f)); }
			if (hero.buff(Hex.class) != null) { Buff.affect( enemy, Hex.class, Math.round(dmg*0.6f)); }
			if (hero.buff(Bleeding.class) != null) {
				Buff.affect(enemy, Bleeding.class).set(Math.round(dmg * 0.6f));
				CellEmitter.center( enemy.pos ).burst( BloodParticle.BURST, 3 );
			} if (hero.buff(Slow.class) != null) { Buff.affect( enemy, Slow.class, Math.round(dmg*0.6f)); }
			if (hero.buff(Vertigo.class) != null) { Buff.affect( enemy, Vertigo.class, Math.round(dmg*0.6f)); }
			if (hero.buff(Burning.class) != null) {
				Buff.affect(enemy, Burning.class).reignite( enemy );
				Splash.at( enemy.sprite.center(), 0xFFBB0000, 5);
			} if (hero.buff(Chill.class) != null) {
				Buff.affect( enemy, Chill.class, Math.round(dmg*0.6f));
				Splash.at( enemy.sprite.center(), 0xFFB2D6FF, 5);
			} if (hero.buff(Ooze.class) != null) {
				Buff.affect(enemy, Ooze.class).set(Math.round(dmg * 0.6f));
				enemy.sprite.burst( 0x000000, 5 );
			} if (hero.buff(Roots.class) != null) { Buff.affect( enemy, Roots.class, Math.round(dmg*0.6f)); }
			if (hero.buff(Blindness.class) != null) { Buff.affect( enemy, Blindness.class, Math.round(dmg*0.6f)); }

			if (hero.pointsInTalent(Talent.TRANSFER_HARM) == 3){
				if (hero.buff(Poison.class) != null) hero.buff(Poison.class).detach();
				if (hero.buff(Cripple.class) != null) hero.buff(Cripple.class).detach();
				if (hero.buff(Weakness.class) != null) hero.buff(Weakness.class).detach();
				if (hero.buff(Vulnerable.class) != null) hero.buff(Vulnerable.class).detach();
				if (hero.buff(Hex.class) != null) hero.buff(Hex.class).detach();
				if (hero.buff(Bleeding.class) != null) hero.buff(Bleeding.class).detach();
				if (hero.buff(Slow.class) != null) hero.buff(Slow.class).detach();
				if (hero.buff(Vertigo.class) != null) hero.buff(Vertigo.class).detach();
				if (hero.buff(Burning.class) != null) hero.buff(Burning.class).detach();
				if (hero.buff(Chill.class) != null) hero.buff(Chill.class).detach();
				if (hero.buff(Ooze.class) != null) hero.buff(Ooze.class).detach();
				if (hero.buff(Roots.class) != null) hero.buff(Roots.class).detach();
				if (hero.buff(Blindness.class) != null) hero.buff(Blindness.class).detach();
			}
		}

		if (hero.hasTalent(MARKSTROLL) && hero.buff(MarksTrollCooldown.class) == null
				&& hero.belongings.weapon instanceof MissileWeapon
				&& !Dungeon.level.adjacent(hero.pos, enemy.pos)){
			Sample.INSTANCE.play(Assets.Sounds.EVOKE, 1f, 0.4f);
			Buff.affect(hero, MarksTrollCooldown.class,
					(10-(Dungeon.hero.pointsInTalent(MARKSTROLL) == 2 ? 4 : 0)));
			Buff.affect( enemy, Paralysis.class, 2f);
		}

		return dmg;
	}

	public static class SuckerPunchTracker extends Buff{};
	public static class FollowupStrikeTracker extends Buff{};
	public static class TransferHarmTracker extends Buff{};
	public static class PreemptiveFireTracker extends Buff{};

	public static final int MAX_TALENT_TIERS = 4;

	public static void initClassTalents( Hero hero ){
		initClassTalents( hero.heroClass, hero.talents );
	}

	public static void initClassTalents( HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		ArrayList<Talent> tierTalents = new ArrayList<>();

		//tier 1
		switch (cls){
			case WARRIOR: default:
				Collections.addAll(tierTalents, HEARTY_MEAL, ARMSMASTERS_INTUITION, TEST_SUBJECT, IRON_WILL);
				break;
			case MAGE:
				Collections.addAll(tierTalents, EMPOWERING_MEAL, SCHOLARS_INTUITION, TESTED_HYPOTHESIS, BACKUP_BARRIER);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, CACHED_RATIONS, THIEFS_INTUITION, SUCKER_PUNCH, PROTECTIVE_SHADOWS);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, NATURES_BOUNTY, SURVIVALISTS_INTUITION, FOLLOWUP_STRIKE, NATURES_AID);
				break;
			case HERETIC:
				Collections.addAll(tierTalents, BUTCHERY, ACCURSEDS_INTUITION, KNOWLEDGE_IS_POWER, MALEVOLENT_ARMOR);
				break;
			case ALCHEMIST:
				Collections.addAll(tierTalents, FOOD_ALCHEMY, INVENTORS_INTUITION, PREEMPTIVE_FIRE, EXPERIMENTAL_BARRIER);
				break;
			case ELEMENTALIST:
				Collections.addAll(tierTalents, SHIELDING_MEAL, PYTHONESS_INTUITION, EXTENDED_FOCUS, ELEMENTAL_SHIELD);
				break;
			case TROLL:
				Collections.addAll(tierTalents, ENCOURAGING_MEAL, ARTISANS_INTUITION, TESTED_STAMINA, ARMORED_TROLL);
				break;
		}
		for (Talent talent : tierTalents){
			talents.get(0).put(talent, 0);
		}
		tierTalents.clear();

		//tier 2
		switch (cls){
			case WARRIOR: default:
				Collections.addAll(tierTalents, IRON_STOMACH, RESTORED_WILLPOWER, RUNIC_TRANSFERENCE, LETHAL_MOMENTUM, IMPROVISED_PROJECTILES);
				break;
			case MAGE:
				Collections.addAll(tierTalents, ENERGIZING_MEAL, ENERGIZING_UPGRADE, WAND_PRESERVATION, ARCANE_VISION, SHIELD_BATTERY);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, MYSTICAL_MEAL, MYSTICAL_UPGRADE, WIDE_SEARCH, SILENT_STEPS, ROGUES_FORESIGHT);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, INVIGORATING_MEAL, RESTORED_NATURE, REJUVENATING_STEPS, HEIGHTENED_SENSES, DURABLE_PROJECTILES);
				break;
			case HERETIC:
				Collections.addAll(tierTalents, ASCETIC, SUBLIMATION, CHAOS_ADEPT, OVERWHELM, RUNE_OF_CURSE);
				break;
			case ALCHEMIST:
				Collections.addAll(tierTalents, FRESH_MEAL, RELOADING_UPGRADE, ELIXIR_FORMULA, REBREATHER, GASEOUS_WARFARE);
				break;
			case ELEMENTALIST:
				Collections.addAll(tierTalents, PENETRATING_MEAL, HYDROMANCER, ICEMAIL, CHAIN_LIGHTNING, WILDFIRE);
				break;
			case TROLL:
				Collections.addAll(tierTalents, CLEANSING_MEAL, REGENERATION, INDUSTRIOUS_HANDS, BOULDER_IS_COMING, MARKSTROLL);
				break;
		}
		for (Talent talent : tierTalents){
			talents.get(1).put(talent, 0);
		}
		tierTalents.clear();

		//tier 3
		switch (cls){
			case WARRIOR: default:
				Collections.addAll(tierTalents, HOLD_FAST, STRONGMAN);
				break;
			case MAGE:
				Collections.addAll(tierTalents, EMPOWERING_SCROLLS, ALLY_WARP);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, ENHANCED_RINGS, LIGHT_CLOAK);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, POINT_BLANK, SEER_SHOT);
				break;
			case HERETIC:
				Collections.addAll(tierTalents, ENHANCED_CURSE, TRANSFER_HARM);
				break;
			case ALCHEMIST:
				Collections.addAll(tierTalents, ADVANCED_PISTOL, PORTABLE_KIT);
				break;
			case ELEMENTALIST:
				Collections.addAll(tierTalents, ELEMENTAL_MASTER, ELEMENT_OF_CHAOS);
				break;
			case TROLL:
				Collections.addAll(tierTalents, UPGRADE_MASTERY, MINT_MASTER);
				break;
		}
		for (Talent talent : tierTalents){
			talents.get(2).put(talent, 0);
		}
		tierTalents.clear();

		//tier4
		//TBD
	}

	public static void initSubclassTalents( Hero hero ){
		initSubclassTalents( hero.subClass, hero.talents );
	}

	public static void initSubclassTalents( HeroSubClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
		if (cls == HeroSubClass.NONE) return;

		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		ArrayList<Talent> tierTalents = new ArrayList<>();

		//tier 3
		switch (cls){
			case BERSERKER: default:
				Collections.addAll(tierTalents, ENDLESS_RAGE, BERSERKING_STAMINA, ENRAGED_CATALYST);
				break;
			case GLADIATOR:
				Collections.addAll(tierTalents, CLEAVE, LETHAL_DEFENSE, ENHANCED_COMBO);
				break;
			case BATTLEMAGE:
				Collections.addAll(tierTalents, EMPOWERED_STRIKE, MYSTICAL_CHARGE, EXCESS_CHARGE);
				break;
			case WARLOCK:
				Collections.addAll(tierTalents, SOUL_EATER, SOUL_SIPHON, NECROMANCERS_MINIONS);
				break;
			case ASSASSIN:
				Collections.addAll(tierTalents, ENHANCED_LETHALITY, ASSASSINS_REACH, BOUNTY_HUNTER);
				break;
			case FREERUNNER:
				Collections.addAll(tierTalents, EVASIVE_ARMOR, PROJECTILE_MOMENTUM, SPEEDY_STEALTH);
				break;
			case SNIPER:
				Collections.addAll(tierTalents, FARSIGHT, SHARED_ENCHANTMENT, SHARED_UPGRADES);
				break;
			case WARDEN:
				Collections.addAll(tierTalents, DURABLE_TIPS, BARKSKIN, SHIELDING_DEW);
				break;
			case SUMMONER:
				Collections.addAll(tierTalents, FIEND_WARP, PACT_OF_KNOT, CURSED_CLAW);
				break;
			case BLOODKNIGHT:
				Collections.addAll(tierTalents, PREDATOR, RUSH_OF_BLOOD, SERUM);
				break;
			case TRAILBLAZER:
				Collections.addAll(tierTalents, STEADY_AIM, INFUSED_GUNSMITH, DOUBLE_TAP);
				break;
			case INNOVATOR:
				Collections.addAll(tierTalents, DEWCHEMY, ARCANE_ENGINEER, CATALYST_MK2);
				break;
			case BINDER:
				Collections.addAll(tierTalents, ARDENT_BLADE, WALKING_GLACIER, MIGHTY_THUNDER);
				break;
			case SPELLWEAVER:
				Collections.addAll(tierTalents, DEVASTATOR, TRAVELER, CLAIRVOYANT);
				break;
			case WARSMITH:
				Collections.addAll(tierTalents, ARCANESMITH, CRUCIBLE, WANDTROLL);
				break;
			case GEOMANCER:
				Collections.addAll(tierTalents, HARMONY, AVALANCHE, LANDSLIDER);
				break;
		}
		for (Talent talent : tierTalents){
			talents.get(2).put(talent, 0);
		}
		tierTalents.clear();

	}

	public static void initArmorTalents( Hero hero ){
		initArmorTalents( hero.armorAbility, hero.talents);
	}

	public static void initArmorTalents(ArmorAbility abil, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
		if (abil == null) return;

		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		for (Talent t : abil.talents()){
			talents.get(3).put(t, 0);
		}
	}

	private static final String TALENT_TIER = "talents_tier_";

	public static void storeTalentsInBundle( Bundle bundle, Hero hero ){
		for (int i = 0; i < MAX_TALENT_TIERS; i++){
			LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
			Bundle tierBundle = new Bundle();

			for (Talent talent : tier.keySet()){
				if (tier.get(talent) > 0){
					tierBundle.put(talent.name(), tier.get(talent));
				}
				if (tierBundle.contains(talent.name())){
					tier.put(talent, Math.min(tierBundle.getInt(talent.name()), talent.maxPoints()));
				}
			}
			bundle.put(TALENT_TIER+(i+1), tierBundle);
		}
	}

	public static void restoreTalentsFromBundle( Bundle bundle, Hero hero ){
		if (hero.heroClass != null)     initClassTalents(hero);
		if (hero.subClass != null)      initSubclassTalents(hero);
		if (hero.armorAbility != null)  initArmorTalents(hero);

		for (int i = 0; i < MAX_TALENT_TIERS; i++){
			LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
			Bundle tierBundle = bundle.contains(TALENT_TIER+(i+1)) ? bundle.getBundle(TALENT_TIER+(i+1)) : null;
			//pre-0.9.1 saves
			if (tierBundle == null && i == 0 && bundle.contains("talents")){
				tierBundle = bundle.getBundle("talents");
			}

			if (tierBundle != null){
				for (Talent talent : tier.keySet()){
					if (tierBundle.contains(talent.name())){
						tier.put(talent, Math.min(tierBundle.getInt(talent.name()), talent.maxPoints()));
					}
				}
			}
		}
	}

}
