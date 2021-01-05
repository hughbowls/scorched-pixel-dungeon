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

package com.shatteredpixel.shatteredpixeldungeon.items.armor;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GarmentChange;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GarmentCooldown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.ArmorKit;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.ElementalSpell;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class ElementalArmor extends Armor {

	{
		levelKnown = true;
		cursedKnown = true;

		image = ItemSpriteSheet.ARMOR_ELEMENTALIST_BASIC;
		defaultAction = AC_ABSORB;

		unique = true;
		bones = false;
	}

	@Override
	public String name() {
		if (kitUsed) return Messages.get(this, "true") + " " + super.name();
		else return super.name();
	}

	@Override
	public String info() {
		if (kitUsed) return super.info() + " " + Messages.get(this, "desc_true");
		else return super.info();
	}

	@Override
	public int image() {
		return kitUsed == false ? ItemSpriteSheet.ARMOR_ELEMENTALIST_BASIC
				: ItemSpriteSheet.ARMOR_ELEMENTALIST_ADV;
	}

	public static final String AC_ABSORB 	= "ABSORB";
	private int armorTier;
	private static final String ARMOR_TIER	= "armortier";
	public boolean kitUsed = false;
	private static final String KIT_USED	= "kitUsed";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ARMOR_TIER, armorTier );
		bundle.put( KIT_USED, kitUsed );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		armorTier = bundle.getInt( ARMOR_TIER );
		kitUsed = bundle.getBoolean( KIT_USED );
	}

	@Override
	public ArrayList<String> actions(Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.remove( AC_DROP );
		actions.remove( AC_THROW );
		actions.remove( AC_UNEQUIP );
		actions.add( AC_ABSORB );
		return actions;
	}

	public ElementalArmor() {
		super( 1 );
	}

	@Override
	public void execute( final Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_ABSORB )) {

			if (curUser.buff(GarmentCooldown.class) == null) {
				if (hero.belongings.armor instanceof ElementalArmorIce
						|| hero.belongings.armor instanceof ElementalArmorFire
						|| hero.belongings.armor instanceof ElementalArmorElec) {
					doChange(null, hero.belongings.armor);
					if (hero.buff(GarmentChange.class) != null) Buff.detach(hero, GarmentChange.class);
				}

				float absorbed = 0;
				Fire fire = (Fire)Dungeon.level.blobs.get(Fire.class);
				Freezing freezing = (Freezing)Dungeon.level.blobs.get(Freezing.class);
				Electricity electricity = (Electricity)Dungeon.level.blobs.get(Electricity.class);

				PathFinder.buildDistanceMap( hero.pos, hero.fieldOfView, hero.viewDistance );
				for (int i=0; i < Dungeon.level.length(); i++) {
					if (Blob.volumeAt(i, Fire.class) > 0) 			{ fire.clear(i); 		absorbed++; }
					if (Blob.volumeAt(i, Freezing.class) > 0) 		{ freezing.clear(i); 	absorbed++; }
					if (Blob.volumeAt(i, Electricity.class) > 0) 	{ electricity.clear(i); absorbed++; }
				}

				if (hero.buff(Burning.class) != null) {
					Burning burn = hero.buff(Burning.class);
					absorbed += burn.getLeft();
					Buff.detach(hero, Burning.class);
				}
				if (hero.buff(Chill.class) != null) {
					Chill chill = hero.buff(Chill.class);
					absorbed += chill.cooldown();
					Buff.detach(hero, Chill.class);
				}

				if (absorbed > 0) {
					hero.spend(Actor.TICK);
					hero.busy();
					Sample.INSTANCE.play(Assets.Sounds.MELD);
					hero.sprite.operate(hero.pos);
					Invisibility.dispel();

					Buff.affect(curUser, GarmentCooldown.class, absorbed);
					GLog.p(Messages.get(ElementalArmor.class, "cooldown_msg"));
					absorbed = 0;
				} else {
					GLog.w(Messages.get(ElementalArmor.class, "cooldown_msg_none"));
				}
			} else GLog.w(Messages.get(ElementalArmor.class, "cooldown_msg_cant"));
		}
	}

	public static void doChange ( ElementalSpell element, Armor armor ) {

		if (Dungeon.hero.subClass == HeroSubClass.TEMPEST) {
			ElementalArmor changeForm = ElementalArmor.change(element, armor);
			if (curUser.belongings.armor == armor) {
				curUser.belongings.armor = changeForm;
				((HeroSprite) curUser.sprite).updateArmor();
				changeForm.activate(curUser);
			} else {
				armor.detach(curUser.belongings.backpack);
				changeForm.collect(curUser.belongings.backpack);
			}
			Sample.INSTANCE.play(Assets.Sounds.MELD);
		} else if (Dungeon.hero.subClass == null || Dungeon.hero.subClass == HeroSubClass.NONE
				|| Dungeon.hero.subClass == HeroSubClass.SPELLWEAVER) {
			// do noting
		}
	}

	public static void doArmorKit( Armor armor ) {
		int slot = Dungeon.quickslot.getSlot( armor );
		ElementalArmor changeForm = null;

		changeForm = new ElementalArmor();
		changeForm.armorTier = 6;
		changeForm.tier = 6;
		changeForm.kitUsed = true;

		changeForm.level(armor.level() - (armor.curseInfusionBonus ? 1 : 0));
		changeForm.augment = armor.augment;
		changeForm.inscribe( armor.glyph );
		changeForm.cursed = armor.cursed;
		changeForm.curseInfusionBonus = armor.curseInfusionBonus;
		changeForm.identify();
		curUser.belongings.armor = changeForm;
		((HeroSprite) curUser.sprite).updateArmor();
		changeForm.activate(curUser);

		if (curUser.buff(GarmentChange.class) != null) {
			Buff.detach(curUser, GarmentChange.class);
		}
		if (curUser.buff(GarmentCooldown.class) != null) {
			Buff.detach(curUser, GarmentCooldown.class);
		}

		if (slot != -1) {
			Dungeon.quickslot.setSlot( slot, changeForm );
			updateQuickslot();
		}

		curUser.sprite.centerEmitter().start( Speck.factory( Speck.KIT ), 0.05f, 10 );
		curUser.spend( 2 );
		curUser.busy();

		GLog.w( Messages.get(ArmorKit.class, "upgraded", changeForm.name()) );

		curUser.sprite.operate( curUser.pos );
		Sample.INSTANCE.play( Assets.Sounds.EVOKE );
	}

	public static ElementalArmor change(ElementalSpell element, Armor armor ) {

		int slot = Dungeon.quickslot.getSlot( armor );
		ElementalArmor changeForm = null;

		if (element == null) {
			changeForm = new ElementalArmor();
			changeForm.tier = 1;
			changeForm.kitUsed = ((ElementalArmor)armor).kitUsed;
			if (changeForm.kitUsed) {
				changeForm.armorTier = 6;
				changeForm.tier = 6;
			}

		} else if (element instanceof ElementalSpell.ElementalSpellIce) {
            changeForm = new ElementalArmorIce();
            changeForm.tier = 2;
			changeForm.kitUsed = ((ElementalArmor)armor).kitUsed;
		} else if (element instanceof ElementalSpell.ElementalSpellFire) {
            changeForm = new ElementalArmorFire();
            changeForm.tier = 3;
			changeForm.kitUsed = ((ElementalArmor)armor).kitUsed;
		} else if (element instanceof ElementalSpell.ElementalSpellElec) {
            changeForm = new ElementalArmorElec();
            changeForm.tier = 4;
			changeForm.kitUsed = ((ElementalArmor)armor).kitUsed;
		} else {
            changeForm = new ElementalArmor();
            changeForm.tier = 1;
			changeForm.kitUsed = ((ElementalArmor)armor).kitUsed;
			if (changeForm.kitUsed) {
				changeForm.armorTier = 6;
				changeForm.tier = 6;
			}
        }
		changeForm.armorTier = changeForm.tier;

		changeForm.level(armor.level() - (armor.curseInfusionBonus ? 1 : 0));
		changeForm.augment = armor.augment;
		changeForm.inscribe( armor.glyph );
		changeForm.cursed = armor.cursed;
		changeForm.curseInfusionBonus = armor.curseInfusionBonus;
		changeForm.identify();

		if (slot != -1) {
			Dungeon.quickslot.setSlot( slot, changeForm );
			updateQuickslot();
		}

		return changeForm;
	}

	@Override
	public int STRReq(int lvl) {
		lvl = Math.max(0, lvl);
		//strength req decreases at +1,+3,+6,+10,etc.
		return 10 - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
	}

	@Override
	public int DRMax(int lvl){
		int t = kitUsed ? 4 : 1;
		if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
			return 1 + t + lvl + augment.defenseFactor(lvl);
		}

		int max = t * (2 + lvl) + augment.defenseFactor(lvl);
		if (lvl > max){
			return ((lvl - max)+1)/2;
		} else {
			return max;
		}
	}

	@Override
	public int DRMin(int lvl){
		if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
			return 0;
		}

		int max = DRMax(lvl);
		if (lvl >= max){
			return (lvl - max);
		} else {
			return lvl;
		}
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 0;
	}

	public static class ElementalArmorIce extends ElementalArmor {
		@Override
		public String name() {
			if (kitUsed) return Messages.get(this, "true") + " " + super.name();
			else return super.name();
		}

		@Override
		public String info() {
			if (kitUsed) return super.info() + " " + Messages.get(this, "desc_true");
			else return super.info();
		}

		@Override
		public int image() {
			return kitUsed == false ? ItemSpriteSheet.ARMOR_ELEMENTALIST_BASIC
					: ItemSpriteSheet.ARMOR_ELEMENTALIST_ADV;
		}
		@Override
		public int STRReq(int lvl) {
			lvl = Math.max(0, lvl);
			//strength req decreases at +1,+3,+6,+10,etc.
			return 10 - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
		}

		@Override
		public int DRMax(int lvl){
			int t = kitUsed ? 4 : 1;
			if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
				return 1 + t + lvl + augment.defenseFactor(lvl);
			}

			int max = t * (2 + lvl) + augment.defenseFactor(lvl);
			if (lvl > max){
				return ((lvl - max)+1)/2;
			} else {
				return max;
			}
		}

		@Override
		public int DRMin(int lvl){
			if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
				return 0;
			}

			int max = DRMax(lvl);
			if (lvl >= max){
				return (lvl - max);
			} else {
				return lvl;
			}
		}
	}
	public static class ElementalArmorFire extends ElementalArmor {
		@Override
		public String name() {
			if (kitUsed) return Messages.get(this, "true") + " " + super.name();
			else return super.name();
		}

		@Override
		public String info() {
			if (kitUsed) return super.info() + " " + Messages.get(this, "desc_true");
			else return super.info();
		}

		@Override
		public int image() {
			return kitUsed == false ? ItemSpriteSheet.ARMOR_ELEMENTALIST_BASIC
					: ItemSpriteSheet.ARMOR_ELEMENTALIST_ADV;
		}
		@Override
		public int STRReq(int lvl) {
			lvl = Math.max(0, lvl);
			//strength req decreases at +1,+3,+6,+10,etc.
			return 10 - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
		}

		@Override
		public int DRMax(int lvl){
			int t = kitUsed ? 4 : 1;
			if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
				return 1 + t + lvl + augment.defenseFactor(lvl);
			}

			int max = t * (2 + lvl) + augment.defenseFactor(lvl);
			if (lvl > max){
				return ((lvl - max)+1)/2;
			} else {
				return max;
			}
		}

		@Override
		public int DRMin(int lvl){
			if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
				return 0;
			}

			int max = DRMax(lvl);
			if (lvl >= max){
				return (lvl - max);
			} else {
				return lvl;
			}
		}
	}
	public static class ElementalArmorElec extends ElementalArmor {
		@Override
		public String name() {
			if (kitUsed) return Messages.get(this, "true") + " " + super.name();
			else return super.name();
		}

		@Override
		public String info() {
			if (kitUsed) return super.info() + " " + Messages.get(this, "desc_true");
			else return super.info();
		}

		@Override
		public int image() {
			return kitUsed == false ? ItemSpriteSheet.ARMOR_ELEMENTALIST_BASIC
					: ItemSpriteSheet.ARMOR_ELEMENTALIST_ADV;
		}
		@Override
		public int STRReq(int lvl) {
			lvl = Math.max(0, lvl);
			//strength req decreases at +1,+3,+6,+10,etc.
			return 10 - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
		}

		@Override
		public int DRMax(int lvl){
			int t = kitUsed ? 4 : 1;
			if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
				return 1 + t + lvl + augment.defenseFactor(lvl);
			}

			int max = t * (2 + lvl) + augment.defenseFactor(lvl);
			if (lvl > max){
				return ((lvl - max)+1)/2;
			} else {
				return max;
			}
		}

		@Override
		public int DRMin(int lvl){
			if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
				return 0;
			}

			int max = DRMax(lvl);
			if (lvl >= max){
				return (lvl - max);
			} else {
				return lvl;
			}
		}
	}
}
