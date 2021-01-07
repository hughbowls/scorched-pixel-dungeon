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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WandEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
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
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Pistol;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public enum Talent {

	HEARTY_MEAL(0),
	ARMSMASTERS_INTUITION(1),
	TEST_SUBJECT(2),
	IRON_WILL(3),
	IRON_STOMACH(4),
	RESTORED_WILLPOWER(5),
	RUNIC_TRANSFERENCE(6),
	LETHAL_MOMENTUM(7),
	IMPROVISED_PROJECTILES(8),

	EMPOWERING_MEAL(16),
	SCHOLARS_INTUITION(17),
	TESTED_HYPOTHESIS(18),
	BACKUP_BARRIER(19),
	ENERGIZING_MEAL(20),
	ENERGIZING_UPGRADE(21),
	WAND_PRESERVATION(22),
	ARCANE_VISION(23),
	SHIELD_BATTERY(24),

	CACHED_RATIONS(32),
	THIEFS_INTUITION(33),
	SUCKER_PUNCH(34),
	PROTECTIVE_SHADOWS(35),
	MYSTICAL_MEAL(36),
	MYSTICAL_UPGRADE(37),
	WIDE_SEARCH(38),
	SILENT_STEPS(39),
	ROGUES_FORESIGHT(40),

	NATURES_BOUNTY(48),
	SURVIVALISTS_INTUITION(49),
	FOLLOWUP_STRIKE(50),
	NATURES_AID(51),
	INVIGORATING_MEAL(52),
	RESTORED_NATURE(53),
	REJUVENATING_STEPS(54),
	HEIGHTENED_SENSES(55),
	DURABLE_PROJECTILES(56),

	BUTCHERY(64),
	ACCURSEDS_INTUITION(65),
	KNOWLEDGE_IS_POWER(66),
	MALEVOLENT_ARMOR(67),
	ASCETIC(68),
	TRANSFER_HARM(69),
	ENHANCED_CURSE(70),
	CHAOS_ADEPT(71),
	WRAITH_DECEPTION(72),

	FOOD_ALCHEMY(80),
	INVENTORS_INTUITION(81),
	PREEMPTIVE_FIRE(82),
	EXPERIMENTAL_BARRIER(83),
	FRESH_MEAL(84),
	RELOADING_UPGRADE(85),
	ELIXIR_FORMULA(86),
	GRENADIER(87),
	GASEOUS_WARFARE(88),

	SHIELDING_MEAL(96),
	PYTHONESS_INTUITION(97),
	EXTENDED_FOCUS(98),
	ELEMENTAL_SHIELD(99),
	PENETRATING_MEAL(100),
	HYDROMANCER(101),
	ICEMAIL(102),
	CHAIN_LIGHTNING(103),
	WILDFIRE(104);

	public static class ImprovisedProjectileCooldown extends FlavourBuff{};
	public static class LethalMomentumTracker extends FlavourBuff{};
	public static class WandPreservationCounter extends CounterBuff{};
	public static class RejuvenatingStepsCooldown extends FlavourBuff{};
	public static class TransferHarmCooldown extends FlavourBuff{};

	int icon;

	// tiers 1/2/3/4 start at levels 2/7/13/21
	public static int[] tierLevelThresholds = new int[]{0, 2, 7, 13, 21, 31};

	Talent(int icon ){
		this.icon = icon;
	}

	public int icon(){
		return icon;
	}

	public int maxPoints(){
		return 2;
	}

	public String title(){
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
		if (hero.hasTalent(ACCURSEDS_INTUITION)){
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
			Buff.affect( hero, Recharging.class, 2 + 3*(hero.pointsInTalent(ENERGIZING_MEAL)) );
			ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(MYSTICAL_MEAL)){
			//3/5 turns of recharging
			Buff.affect( hero, ArtifactRecharge.class).set(1 + 2*(hero.pointsInTalent(MYSTICAL_MEAL))).ignoreHornOfPlenty = foodSource instanceof HornOfPlenty;
			ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(INVIGORATING_MEAL)){
			//effectively 1/2 turns of haste
			Buff.affect( hero, Haste.class, 0.67f+hero.pointsInTalent(INVIGORATING_MEAL));
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
	}

	public static class WarriorFoodImmunity extends FlavourBuff{
		{ actPriority = HERO_PRIO+1; }
	}

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
				if (ch != null){
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
				staff.gainCharge( hero.pointsInTalent(ENERGIZING_UPGRADE), true);
				ScrollOfRecharging.charge( Dungeon.hero );
				SpellSprite.show( hero, SpellSprite.CHARGE );
			}
		}
		if (hero.hasTalent(MYSTICAL_UPGRADE)){
			CloakOfShadows cloak = hero.belongings.getItem(CloakOfShadows.class);
			if (cloak != null){
				cloak.overCharge(hero.pointsInTalent(MYSTICAL_UPGRADE));
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
			//15/20 turns of adrenaline surge with 1 boost
			Buff.affect(hero, AdrenalineSurge.class).add(1, 10f + 5f*(float)hero.pointsInTalent(KNOWLEDGE_IS_POWER));
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

		if (hero.hasTalent(Talent.TRANSFER_HARM)
				&& enemy instanceof Mob
				&& hero.belongings.weapon instanceof MeleeWeapon
				&& ((MeleeWeapon)hero.belongings.weapon).hasCurseEnchant()
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
			Buff.affect(hero, TransferHarmCooldown.class, 15f);
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

			if (hero.pointsInTalent(Talent.TRANSFER_HARM) == 2){
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

		return dmg;
	}

	public static class SuckerPunchTracker extends Buff{};
	public static class FollowupStrikeTracker extends Buff{};
	public static class TransferHarmTracker extends Buff{};
	public static class PreemptiveFireTracker extends Buff{};

	public static final int MAX_TALENT_TIERS = 2;

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
		}
		for (Talent talent : tierTalents){
			talents.get(0).put(talent, 0);
		}
		tierTalents.clear();

		//tier 2+
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
				Collections.addAll(tierTalents, ASCETIC, TRANSFER_HARM, ENHANCED_CURSE, CHAOS_ADEPT, WRAITH_DECEPTION);
				break;
			case ALCHEMIST:
				Collections.addAll(tierTalents, FRESH_MEAL, RELOADING_UPGRADE, ELIXIR_FORMULA, GRENADIER, GASEOUS_WARFARE);
				break;
			case ELEMENTALIST:
				Collections.addAll(tierTalents, PENETRATING_MEAL, HYDROMANCER, ICEMAIL, CHAIN_LIGHTNING, WILDFIRE);
				break;
		}
		for (Talent talent : tierTalents){
			talents.get(1).put(talent, 0);
		}
		tierTalents.clear();


		//tier 3+
		//TBD
	}

	public static void initSubclassTalents( Hero hero ){
		//Nothing here yet. Hm.....
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
		if (hero.heroClass != null) initClassTalents(hero);
		if (hero.subClass != null)  initSubclassTalents(hero);

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
