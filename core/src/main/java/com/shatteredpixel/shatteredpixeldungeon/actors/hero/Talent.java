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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public enum Talent {

	HEARTY_MEAL(0),
	ARMSMASTERS_INTUITION(1),
	TEST_SUBJECT(2),
	IRON_WILL(3),
	TEST_WARRIOR_T2_1(4),
	TEST_WARRIOR_T2_2(5),
	TEST_WARRIOR_T2_3(6),
	TEST_WARRIOR_T2_4(7),
	TEST_WARRIOR_T2_5(8),

	ENERGIZING_MEAL(16),
	SCHOLARS_INTUITION(17),
	TESTED_HYPOTHESIS(18),
	ENERGIZING_UPGRADE(19),
	TEST_MAGE_T2_1(20),
	TEST_MAGE_T2_2(21),
	TEST_MAGE_T2_3(22),
	TEST_MAGE_T2_4(23),
	TEST_MAGE_T2_5(24),

	RATIONED_MEAL(32),
	THIEFS_INTUITION(33),
	SUCKER_PUNCH(34),
	MENDING_SHADOWS(35),
	TEST_ROGUE_T2_1(36),
	TEST_ROGUE_T2_2(37),
	TEST_ROGUE_T2_3(38),
	TEST_ROGUE_T2_4(39),
	TEST_ROGUE_T2_5(40),

	INVIGORATING_MEAL(48),
	SURVIVALISTS_INTUITION(49),
	FOLLOWUP_STRIKE(50),
	NATURES_AID(51),
	TEST_HUNTRESS_T2_1(52),
	TEST_HUNTRESS_T2_2(53),
	TEST_HUNTRESS_T2_3(54),
	TEST_HUNTRESS_T2_4(55),
	TEST_HUNTRESS_T2_5(56);

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
	}

	public static void onFoodEaten( Hero hero, float foodVal ){
		if (hero.hasTalent(HEARTY_MEAL)){
			//3/5 HP healed, when hero is below 25% health
			if (hero.HP <= hero.HT/4) {
				hero.HP = Math.min(hero.HP + 1 + 2 * hero.pointsInTalent(HEARTY_MEAL), hero.HT);
				hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), hero.pointsInTalent(HEARTY_MEAL));
			//2/3 HP healed, when hero is below 50% health
			} else if (hero.HP <= hero.HT/2){
				hero.HP = Math.min(hero.HP + 1 + hero.pointsInTalent(HEARTY_MEAL), hero.HT);
				hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1+hero.pointsInTalent(HEARTY_MEAL));
			}
		}
		if (hero.hasTalent(ENERGIZING_MEAL)){
			//5/8 turns of recharging
			Buff.affect( hero, Recharging.class, 2 + 3*(hero.pointsInTalent(ENERGIZING_MEAL)) );
			ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(RATIONED_MEAL)){
			//15%/25% bonus food value
			float bonusSatiety = foodVal * (0.05f + (0.1f * hero.pointsInTalent(RATIONED_MEAL)));
			Buff.affect(hero, Hunger.class).affectHunger(bonusSatiety, true);
		}
		if (hero.hasTalent(INVIGORATING_MEAL)){
			//eating food takes 1 turn, instead of 3
			hero.spend(-2);
			//effectively 1/2 turns of haste
			Buff.affect( hero, Haste.class, 0.67f+hero.pointsInTalent(INVIGORATING_MEAL));
		}
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
	}

	public static void onItemCollected( Hero hero, Item item ){
		if (hero.pointsInTalent(THIEFS_INTUITION) == 2){
			if (item instanceof Ring) ((Ring) item).setKnown();
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
		if (item instanceof Scroll && hero.hasTalent(TESTED_HYPOTHESIS)){
			Buff.affect(hero, Barrier.class).setShield(3 + (3 * hero.pointsInTalent(Talent.TESTED_HYPOTHESIS)), 1);
			ScrollOfRecharging.charge(hero);
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

		return dmg;
	}

	public static class SuckerPunchTracker extends Buff{};
	public static class FollowupStrikeTracker extends Buff{};

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
				Collections.addAll(tierTalents, ENERGIZING_MEAL, SCHOLARS_INTUITION, TESTED_HYPOTHESIS, ENERGIZING_UPGRADE);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, RATIONED_MEAL, THIEFS_INTUITION, SUCKER_PUNCH, MENDING_SHADOWS);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, INVIGORATING_MEAL, SURVIVALISTS_INTUITION, FOLLOWUP_STRIKE, NATURES_AID);
				break;
		}
		for (Talent talent : tierTalents){
			talents.get(0).put(talent, 0);
		}
		tierTalents.clear();

		//tier 2+
		switch (cls){
			case WARRIOR: default:
				Collections.addAll(tierTalents, TEST_WARRIOR_T2_1, TEST_WARRIOR_T2_2, TEST_WARRIOR_T2_3, TEST_WARRIOR_T2_4, TEST_WARRIOR_T2_5);
				break;
			case MAGE:
				Collections.addAll(tierTalents, TEST_MAGE_T2_1, TEST_MAGE_T2_2, TEST_MAGE_T2_3, TEST_MAGE_T2_4, TEST_MAGE_T2_5);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, TEST_ROGUE_T2_1, TEST_ROGUE_T2_2, TEST_ROGUE_T2_3, TEST_ROGUE_T2_4, TEST_ROGUE_T2_5);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, TEST_HUNTRESS_T2_1, TEST_HUNTRESS_T2_2, TEST_HUNTRESS_T2_3, TEST_HUNTRESS_T2_4, TEST_HUNTRESS_T2_5);
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

	private static final String TALENTS = "talents";

	public static void storeTalentsInBundle( Bundle bundle, Hero hero ){
		Bundle talentBundle = new Bundle();

		for (Talent talent : values()){
			if (hero.hasTalent(talent)){
				talentBundle.put(talent.name(), hero.pointsInTalent(talent));
			}
		}

		bundle.put(TALENTS, talentBundle);
	}

	public static void restoreTalentsFromBundle( Bundle bundle, Hero hero ){
		if (hero.heroClass != null) initClassTalents(hero);
		if (hero.subClass != null)  initSubclassTalents(hero);

		if (!bundle.contains(TALENTS)) return;
		Bundle talentBundle = bundle.getBundle(TALENTS);

		for (Talent talent : values()){
			if (talentBundle.contains(talent.name())){
				for (LinkedHashMap<Talent, Integer> tier : hero.talents){
					if (tier.containsKey(talent)){
						tier.put(talent, Math.min(talentBundle.getInt(talent.name()), talent.maxPoints()));
					}
				}
			}
		}
	}

}
