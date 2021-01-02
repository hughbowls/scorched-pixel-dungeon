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

package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GarmentChange;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GarmentCooldown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ElementalArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ElementalSpell extends TargetedSpell {

	{
		unique = true;
		bones = false;
		usesTargeting = true;
		defaultAction = AC_CAST;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		//prevents exploits
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		return actions;
	}

	@Override
	protected void fx( Ballistica bolt, Callback callback ) {
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.MAGIC_MISSILE,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}

	@Override
	protected void affectTarget(Ballistica bolt, Hero hero) {
		int cell = bolt.collisionPos;
	}

	@Override
	public int value() { return 0; }

	// focus
	public static class FireFocus extends Buff {

		public static final float DURATION = 3f;

		{
			type = buffType.POSITIVE;
		}

		private float left;
		private static final String LEFT = "left";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( LEFT, left );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle(bundle);
			left = bundle.getFloat( LEFT );
		}

		public void set( Char ch ) {
			set( ch, DURATION );
		}
		public void set( Char ch, float duration ) {
			left = duration + (float)Dungeon.hero.pointsInTalent(Talent.EXTENDED_FOCUS);
		}

		@Override
		public boolean act() {
			if (!target.isAlive()) {
				detach();
			}

			spend( TICK );
			left -= TICK;
			if (left <= 0) {
				detach();
				return true;
			}

			if (((Hero)target).hasTalent(Talent.ELEMENTAL_SHIELD)) {
				Barrier barrier = target.buff(Barrier.class);
				if (barrier != null) {
					if (barrier.shielding() < 1 + 2*((Hero)target).pointsInTalent(Talent.ELEMENTAL_SHIELD)) {
						barrier.incShield(1);
					}
				} else Buff.affect(target, Barrier.class).setShield(1);
			}

			return true;
		}

		@Override
		public boolean attachTo(Char target) {
			if (super.attachTo(target)) {

				if (target.buff(IceFocus.class) != null) {
					Sample.INSTANCE.play(Assets.Sounds.GAS);
					CellEmitter.get(target.pos).burst(Speck.factory(Speck.STEAM), 6);
					Buff.detach(target, IceFocus.class);
					Buff.detach(target, FireFocus.class);
					Splash.at(target.pos, 0x00AAFF, 10);
					for (int i : PathFinder.NEIGHBOURS9) {
						if (Dungeon.hero.hasTalent(Talent.HYDROMANCER)) {
							Dungeon.level.setCellToWater(false, target.pos + i);

							if (Dungeon.hero.pointsInTalent(Talent.HYDROMANCER) == 2) {
								CellEmitter.center(target.pos).burst(Speck.factory(Speck.STEAM_BLAST), 10);
								Char ch = Actor.findChar(i);
								if (ch != null && ch != target) {
									int push = ch.pos + (ch.pos - target.pos);
									Ballistica trajectory = new Ballistica(ch.pos, push, Ballistica.MAGIC_BOLT);
									WandOfBlastWave.throwChar(ch, trajectory, 1, true);
								}
							}

						} else Dungeon.level.setCellToWater(false, target.pos);
					}
				} else {
					if (Dungeon.level.map[target.pos] == Terrain.WATER){
						Level.set( target.pos, Terrain.EMPTY );
						GameScene.updateMap( target.pos );
						CellEmitter.get( target.pos ).burst( Speck.factory( Speck.STEAM ), 10 );
					}
				}
				return true;
			} else
				return false;
		}

		@Override public int icon() { return BuffIndicator.SC_FIRE; }
		@Override public String toString() {
			return Messages.get(this, "name");
		}
		@Override public String desc() {
			return Messages.get(this, "desc", left, (2+Dungeon.hero.lvl/5), (4+(int)(Dungeon.hero.lvl/2.5f)) );
		}

	};

	public static class IceFocus extends Buff {

		public static final float DURATION = 3f;

		{
			type = buffType.POSITIVE;
		}

		private float left;
		private static final String LEFT = "left";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( LEFT, left );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle(bundle);
			left = bundle.getFloat( LEFT );
		}

		public void set( Char ch ) {
			set( ch, DURATION );
		}
		public void set( Char ch, float duration ) {
			left = duration + (float)Dungeon.hero.pointsInTalent(Talent.EXTENDED_FOCUS);
		}

		@Override
		public boolean act() {
			if (!target.isAlive()) {
				detach();
			}
			spend( TICK );
			left -= TICK;
			if (left <= 0) {
				detach();
				return true;
			}

			if (((Hero)target).hasTalent(Talent.ELEMENTAL_SHIELD)) {
				Barrier barrier = target.buff(Barrier.class);
				if (barrier != null) {
					if (barrier.shielding() < 1 + 2*((Hero)target).pointsInTalent(Talent.ELEMENTAL_SHIELD)) {
						barrier.incShield(1);
					}
				} else Buff.affect(target, Barrier.class).setShield(1);
			}

			return true;
		}

		@Override
		public boolean attachTo(Char target) {
			if (super.attachTo(target)){

				if (target.buff(FireFocus.class) != null) {

					Sample.INSTANCE.play(Assets.Sounds.GAS);
					CellEmitter.get(target.pos).burst(Speck.factory(Speck.STEAM), 6);
					Buff.detach(target, IceFocus.class);
					Buff.detach(target, FireFocus.class);

					Splash.at(target.pos, 0x00AAFF, 10);
					for (int i : PathFinder.NEIGHBOURS9) {
						if (Dungeon.hero.hasTalent(Talent.HYDROMANCER)) {
							Dungeon.level.setCellToWater(false, target.pos + i);

							if (Dungeon.hero.pointsInTalent(Talent.HYDROMANCER) == 2) {
								CellEmitter.center(target.pos).burst(Speck.factory(Speck.STEAM_BLAST), 10);
								Char ch = Actor.findChar(i);
								if (ch != null && ch != target) {
									int push = ch.pos + (ch.pos - target.pos);
									Ballistica trajectory = new Ballistica(ch.pos, push, Ballistica.MAGIC_BOLT);
									WandOfBlastWave.throwChar(ch, trajectory, 1, true);
								}
							}
						} else Dungeon.level.setCellToWater(false, target.pos);
					}
				}
				return true;
			} else
				return false;
		}

		@Override public int icon() {
			return BuffIndicator.SC_ICE;
		}
		@Override public String toString() {
			return Messages.get(this, "name");
		}
		@Override public String desc() {
			return Messages.get(this, "desc", left, (1+Dungeon.hero.lvl/6), (3+Dungeon.hero.lvl/3));
		}

	};

	public static class ElecFocus extends Buff {

		public static final float DURATION = 3f;

		{
			type = buffType.POSITIVE;
		}

		private float left;
		private static final String LEFT = "left";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( LEFT, left );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle(bundle);
			left = bundle.getFloat( LEFT );
		}

		public void set( Char ch ) {
			set( ch, DURATION );
		}
		public void set( Char ch, float duration ) {
			left = duration + (float)Dungeon.hero.pointsInTalent(Talent.EXTENDED_FOCUS);
		}

		@Override
		public boolean act() {
			if (!target.isAlive()) {
				detach();
			}
			spend( TICK );
			left -= TICK;
			if (Dungeon.level.water[target.pos] && !target.flying) {
				target.sprite.centerEmitter().burst(EnergyParticle.BURST, 10);
				detach();
				return true;
			}

			if (left <= 0) detach();

			if (((Hero)target).hasTalent(Talent.ELEMENTAL_SHIELD)) {
				Barrier barrier = target.buff(Barrier.class);
				if (barrier != null) {
					if (barrier.shielding() < 1 + 2*((Hero)target).pointsInTalent(Talent.ELEMENTAL_SHIELD)) {
						barrier.incShield(1);
					}
				} else Buff.affect(target, Barrier.class).setShield(1);
			}

			return true;
		}

		@Override public int icon() { return BuffIndicator.SC_ELEC; }
		@Override public String toString() { return Messages.get(this, "name"); }
		@Override public String desc() {
			return Messages.get(this, "desc", left, (3+Dungeon.hero.lvl/4), (8+Dungeon.hero.lvl/2));
		}
	};

	public static class ChaosFocus extends Buff {

		public static final float DURATION = 3f;

		{
			type = buffType.POSITIVE;
		}

		public enum FocusType {
			FIRE, ICE, ELEC, NORMAL
		}
		public FocusType state = FocusType.NORMAL;

		private float left;
		private static final String LEFT = "left";
		private static final String FOCUSTYPE = "state";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( LEFT, left );
			bundle.put( FOCUSTYPE, state );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle(bundle);
			left = bundle.getFloat( LEFT );
			state = bundle.getEnum( FOCUSTYPE, FocusType.class );
		}

		public void set( Char ch ) {
			set( ch, DURATION );
		}
		public void set( Char ch, float duration ) {
			left = duration + (float)Dungeon.hero.pointsInTalent(Talent.EXTENDED_FOCUS);
		}
		public void check( ElementalSpell element ) {
			left += 2f;
			if (element instanceof ElementalSpellFire) state = FocusType.FIRE;
			if (element instanceof ElementalSpellIce) state = FocusType.ICE;
			if (element instanceof ElementalSpellElec) state = FocusType.ELEC;
			BuffIndicator.refreshHero();
		}

		@Override
		public boolean act() {
			if (!target.isAlive()) {
				detach();
			}
			spend( TICK );
			left -= TICK;

			if (left <= 0) detach();

			if (((Hero)target).hasTalent(Talent.ELEMENTAL_SHIELD)) {
				Barrier barrier = target.buff(Barrier.class);
				if (barrier != null) {
					if (barrier.shielding() < 1 + 2*((Hero)target).pointsInTalent(Talent.ELEMENTAL_SHIELD)) {
						barrier.incShield(1);
					}
				} else Buff.affect(target, Barrier.class).setShield(1);
			}

			return true;
		}

		@Override public int icon() {
			if (state == FocusType.FIRE) return BuffIndicator.SC_FIRE;
			if (state == FocusType.ICE)  return BuffIndicator.SC_ICE;
			if (state == FocusType.ELEC) return BuffIndicator.SC_ELEC;
			else return BuffIndicator.SC_CHAOS;
		}
		@Override public String toString() { return Messages.get(this, "name"); }
		@Override public String desc() {
			String desc = Messages.get(this, "desc", left);

			if (state == FocusType.FIRE){
				desc = Messages.get(FireFocus.class, "desc", left,
						(2+Dungeon.hero.lvl/5), (4+(int)(Dungeon.hero.lvl/2.5f)) );
			} else if (state == FocusType.ICE){
				desc = Messages.get(IceFocus.class, "desc", left,
						(1+Dungeon.hero.lvl/6), (3+Dungeon.hero.lvl/3));
			} else if (state == FocusType.ELEC){
				desc = Messages.get(ElecFocus.class, "desc", left,
						(3+Dungeon.hero.lvl/4), (8+Dungeon.hero.lvl/2));
			}

			return desc;
		}
	};

	// spell
	public static class ElementalSpellFire extends ElementalSpell {

		{
			image = ItemSpriteSheet.ELEMENT_FIRE;
		}

		public static int min() {
			return 2 + Dungeon.hero.lvl/5;
		}
		public static int max() {
			return 4 + (int)(Dungeon.hero.lvl/2.5f);
		}
		public static int damageRoll() { return Random.NormalIntRange( min(), max() ); }

		@Override
		protected void onCast(Hero hero) {
			if (hero.buff(FireFocus.class) != null) {
				super.onCast(hero);
			} else {
				curUser.sprite.zap(curUser.pos);
				curUser.sprite.emitter().burst(FlameParticle.FACTORY, 15);

				Buff.affect(hero, FireFocus.class).set(hero, FireFocus.DURATION);
				Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
				curUser.busy();
				Invisibility.dispel();
				updateQuickslot();
				curUser.spendAndNext(1f);
			}
		}

		@Override
		protected void fx( Ballistica bolt, Callback callback ) {
			MagicMissile.boltFromChar( curUser.sprite.parent,
					MagicMissile.FIRE,
					curUser.sprite,
					bolt.collisionPos,
					callback);
			Sample.INSTANCE.play( Assets.Sounds.ZAP );
		}

		@Override
		protected void affectTarget(Ballistica bolt, Hero hero) {
			Buff.detach(hero, FireFocus.class);
			int cell = bolt.collisionPos;
			Splash.at(cell, 0xFFFFBB33, 10);
			Char target = Actor.findChar(cell);
			if (hero.buff(ChaosFocus.class) != null) {
				hero.buff((ChaosFocus.class)).check(this);
			}

			if (target != null) {
					if (target == curUser && curUser.subClass == HeroSubClass.TEMPEST
							&& !(curUser.belongings.armor instanceof ElementalArmor.ElementalArmorFire)
							&& curUser.buff(GarmentCooldown.class) == null
							&& curUser.buff(GarmentChange.class) == null) {
						Buff.affect(curUser, GarmentChange.class, 20f);
						ElementalArmor.doChange(this, curUser.belongings.armor);
						GLog.p(Messages.get(ElementalArmor.class, "change_msg_fire"));

						target.sprite.emitter().burst(FlameParticle.FACTORY, 5);

						for (int i : PathFinder.NEIGHBOURS9) {
							Freezing freezing = (Freezing) Dungeon.level.blobs.get(Freezing.class);
							if (freezing != null) {
								freezing.clear(cell + i);
							}
							Electricity electricity = (Electricity) Dungeon.level.blobs.get(Electricity.class);
							if (electricity != null) {
								electricity.clear(cell + i);
							}
						}

					} else {
						Buff.affect(target, Burning.class).reignite(target);
						target.damage(damageRoll(), this);
					}
				} else GameScene.add(Blob.seed(cell, 2, Fire.class));
		}
	}

	public static class ElementalSpellIce extends ElementalSpell {

		{
			image = ItemSpriteSheet.ELEMENT_ICE;
		}

		public static int min() {
			return 1 + Dungeon.hero.lvl/6;
		}
		public static int max() {
			return 3 + (int)(Dungeon.hero.lvl/3f);
		}
		public static int damageRoll() { return Random.NormalIntRange( min(), max() ); }

		@Override
		protected void onCast(Hero hero) {
			if (hero.buff(IceFocus.class) != null) {
				super.onCast(hero);

			} else {
				curUser.sprite.zap(curUser.pos);
				curUser.sprite.emitter().start(MagicMissile.MagicParticle.ATTRACTING, 0.02f, 20);

				Buff.affect(hero, IceFocus.class).set(hero, IceFocus.DURATION);
				Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
				curUser.busy();
				Invisibility.dispel();
				updateQuickslot();
				curUser.spendAndNext(1f);
			}
		}

		@Override
		protected void fx( Ballistica bolt, Callback callback ) {
			MagicMissile.boltFromChar( curUser.sprite.parent,
					MagicMissile.FROST,
					curUser.sprite,
					bolt.collisionPos,
					callback);
			Sample.INSTANCE.play( Assets.Sounds.ZAP );
		}

		@Override
		protected void affectTarget(Ballistica bolt, Hero hero) {
			Buff.detach(hero, IceFocus.class);
			int cell = bolt.collisionPos;
			Splash.at(cell, 0xFF8EE3FF, 10);
			if (hero.buff(ChaosFocus.class) != null) {
				hero.buff((ChaosFocus.class)).check(this);
			}

			Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
			if (heap != null) {
				heap.freeze();
			}
			Fire fire = (Fire)Dungeon.level.blobs.get( Fire.class );
			if (fire != null) {
				fire.clear( cell );
			}
			if (Dungeon.level.water[cell] && Blob.volumeAt(cell, Freezing.class) == 0) {
				GameScene.add( Blob.seed( cell, 2, Freezing.class ) );
			}

			Char target = Actor.findChar(cell);
			if (target != null) {
				if (target == curUser && curUser.subClass == HeroSubClass.TEMPEST
						&& !(curUser.belongings.armor instanceof ElementalArmor.ElementalArmorIce)
						&& curUser.buff(GarmentCooldown.class) == null
						&& curUser.buff(GarmentChange.class) == null)
				{
					Buff.affect(curUser, GarmentChange.class, 20f);
					ElementalArmor.doChange(this, curUser.belongings.armor);
					GLog.p(Messages.get(ElementalArmor.class, "change_msg_ice"));

					Splash.at(cell, 0xFF8EE3FF, 10);

					for (int i : PathFinder.NEIGHBOURS9) {
						if (fire != null) { fire.clear( cell+i ); }
						Electricity electricity = (Electricity)Dungeon.level.blobs.get( Electricity.class );
						if (electricity != null) { electricity.clear( cell+i ); }
					}

				} else if (target.buff(Frost.class) != null) {
					Buff.affect(target, Frost.class, 2f);
				} else {
					Chill chill = target.buff(Chill.class);
					target.damage(damageRoll(), this);
					Buff.affect(target, Chill.class, 3f);

					if (chill != null && chill.cooldown() >= 5f) {
						Buff.affect(target, Frost.class, Frost.DURATION);
					}
				}
			}
		}
	}

	public static class ElementalSpellElec extends ElementalSpell {

		{
			image = ItemSpriteSheet.ELEMENT_ELEC;
		}

		public static int min() { return 3 + Dungeon.hero.lvl/4; }
		public static int max() {
			return 8 + Dungeon.hero.lvl/2;
		}
		public static int damageRoll() { return Random.NormalIntRange( min(), max() ); }

		@Override
		protected void onCast(Hero hero) {
			if (hero.buff(ElecFocus.class) != null) {
				super.onCast(hero);

			} else {
				curUser.sprite.zap(curUser.pos);
				curUser.sprite.centerEmitter().burst(EnergyParticle.FACTORY, 10);

				Buff.affect(hero, ElecFocus.class).set(hero, ElecFocus.DURATION);
				Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
				curUser.busy();
				Invisibility.dispel();
				updateQuickslot();
				curUser.spendAndNext(1f);
			}
		}

		private ArrayList<Char> affected = new ArrayList<>();
		private ArrayList<Lightning.Arc> arcs = new ArrayList<>();

		@Override
		protected void affectTarget(Ballistica bolt, Hero hero) {
			Buff.detach(curUser, ElecFocus.class);

			if (hero.buff(ChaosFocus.class) != null) {
				hero.buff((ChaosFocus.class)).check(this);
			}

			//lightning deals less damage per-target, the more targets that are hit.
			float multipler = 0.4f + (0.6f/affected.size());
			//if the main target is in water, all affected take full damage
			if (Dungeon.level.water[bolt.collisionPos]) multipler = 1f;
			if (Dungeon.hero.hasTalent(Talent.CHAIN_LIGHTNING)) {
				multipler = 1f;
				if (Dungeon.hero.pointsInTalent(Talent.CHAIN_LIGHTNING) == 2)
					multipler += (multipler*0.1f)*affected.size();
			}

			Char t = Actor.findChar( bolt.collisionPos );
			if (t == curUser && curUser.subClass == HeroSubClass.TEMPEST
					&& !(curUser.belongings.armor instanceof ElementalArmor.ElementalArmorElec)
					&& curUser.buff(GarmentCooldown.class) == null
					&& curUser.buff(GarmentChange.class) == null)
			{
				Buff.affect(curUser, GarmentChange.class, 20f);
				ElementalArmor.doChange(this, curUser.belongings.armor);
				GLog.p(Messages.get(ElementalArmor.class, "change_msg_elec"));

				t.sprite.centerEmitter().burst(EnergyParticle.BURST, 10);

				for (int i : PathFinder.NEIGHBOURS9) {
					Fire fire = (Fire)Dungeon.level.blobs.get( Fire.class );
					if (fire != null) { fire.clear( curUser.pos+i ); }
					Freezing freezing = (Freezing)Dungeon.level.blobs.get( Freezing.class );
					if (freezing != null) { freezing.clear( curUser.pos+i ); }
				}
			}

			for (Char ch : affected){
				if (ch == hero) Camera.main.shake(2, 0.3f);
				ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
				ch.sprite.flash();
				if (ch != curUser &&
						ch.alignment == curUser.alignment
						&& ch.pos != bolt.collisionPos) {
					continue;
				}

				if (ch == hero && curUser.belongings.armor
						instanceof ElementalArmor.ElementalArmorElec) {
					// immune to damage
				} else ch.damage((int) (damageRoll() * multipler), this);
			}
		}

		private void arc( Char ch ) {
			int dist = (Dungeon.level.water[ch.pos] && !ch.flying) ? 2 : 1;
			ArrayList<Char> hitThisArc = new ArrayList<>();
			PathFinder.buildDistanceMap( ch.pos, BArray.not( Dungeon.level.solid, null ), dist );
			for (int i = 0; i < PathFinder.distance.length; i++) {
				if (PathFinder.distance[i] < Integer.MAX_VALUE){
					Char n = Actor.findChar( i );
					if (n == Dungeon.hero && PathFinder.distance[i] > 1)
						//the hero is only zapped if they are adjacent
						continue;
					else if (n != null && !affected.contains( n )) {
						hitThisArc.add(n);
					}
				}
			}

			affected.addAll(hitThisArc);
			for (Char hit : hitThisArc){
				arcs.add(new Lightning.Arc(ch.sprite.center(), hit.sprite.center()));
				arc(hit);
			}
		}

		@Override
		protected void fx( Ballistica bolt, Callback callback ) {
			affected.clear();
			arcs.clear();
			int cell = bolt.collisionPos;
			Char ch = Actor.findChar( cell );
			if (ch != null) {
				affected.add( ch );
				arcs.add( new Lightning.Arc(curUser.sprite.center(), ch.sprite.center()));
				arc(ch);
			} else {
				arcs.add( new Lightning.Arc(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(bolt.collisionPos)));
				CellEmitter.center( cell ).burst( SparkParticle.FACTORY, 3 );
			}
			curUser.sprite.parent.addToFront( new Lightning( arcs, null ) );
			Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
			callback.call();
		}
	}

	public static class ElementalSpellChaos extends ElementalSpell {

		{
			image = ItemSpriteSheet.ELEMENT_CHAOS;
		}

		@Override
		protected void onCast(Hero hero) {
			if (hero.buff(ChaosFocus.class) != null) {
				super.onCast(hero);
			} else {
				curUser.sprite.zap(curUser.pos);
				curUser.sprite.emitter().start( RainbowParticle.ATTRACTING, 0.02f, 20 );

				Buff.affect(hero, ChaosFocus.class).set(hero, ChaosFocus.DURATION);
				Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
				curUser.busy();
				Invisibility.dispel();
				updateQuickslot();
				curUser.spendAndNext(1f);
			}
		}

		private ArrayList<Char> affected = new ArrayList<>();
		private ArrayList<Lightning.Arc> arcs = new ArrayList<>();
		private void arc( Char ch ) {
			int dist = (Dungeon.level.water[ch.pos] && !ch.flying) ? 2 : 1;
			ArrayList<Char> hitThisArc = new ArrayList<>();
			PathFinder.buildDistanceMap( ch.pos, BArray.not( Dungeon.level.solid, null ), dist );
			for (int i = 0; i < PathFinder.distance.length; i++) {
				if (PathFinder.distance[i] < Integer.MAX_VALUE){
					Char n = Actor.findChar( i );
					if (n == Dungeon.hero && PathFinder.distance[i] > 1)
						//the hero is only zapped if they are adjacent
						continue;
					else if (n != null && !affected.contains( n )) {
						hitThisArc.add(n);
					}
				}
			}

			affected.addAll(hitThisArc);
			for (Char hit : hitThisArc){
				arcs.add(new Lightning.Arc(ch.sprite.center(), hit.sprite.center()));
				arc(hit);
			}
		}

		@Override
		protected void fx( Ballistica bolt, Callback callback ) {
			ChaosFocus focus = Dungeon.hero.buff(ChaosFocus.class);
			if (focus != null) {
				if (focus.state == ChaosFocus.FocusType.FIRE) {
					MagicMissile.boltFromChar( curUser.sprite.parent,
							MagicMissile.FIRE,
							curUser.sprite,
							bolt.collisionPos,
							callback);
					Sample.INSTANCE.play( Assets.Sounds.ZAP );
				}
				else if (focus.state == ChaosFocus.FocusType.ICE) {
					MagicMissile.boltFromChar( curUser.sprite.parent,
							MagicMissile.FROST,
							curUser.sprite,
							bolt.collisionPos,
							callback);
					Sample.INSTANCE.play( Assets.Sounds.ZAP );
				}
				else if (focus.state == ChaosFocus.FocusType.ELEC) {
					affected.clear();
					arcs.clear();
					int cell = bolt.collisionPos;
					Char ch = Actor.findChar( cell );
					if (ch != null) {
						affected.add( ch );
						arcs.add( new Lightning.Arc(curUser.sprite.center(), ch.sprite.center()));
						arc(ch);
					} else {
						arcs.add( new Lightning.Arc(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(bolt.collisionPos)));
						CellEmitter.center( cell ).burst( SparkParticle.FACTORY, 3 );
					}
					curUser.sprite.parent.addToFront( new Lightning( arcs, null ) );
					Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
					callback.call();
				}
				else {
					MagicMissile.boltFromChar(curUser.sprite.parent,
							MagicMissile.RAINBOW,
							curUser.sprite,
							bolt.collisionPos,
							callback);
					Sample.INSTANCE.play(Assets.Sounds.ZAP);
				}
			}
		}

		@Override
		protected void affectTarget(Ballistica bolt, Hero hero) {
			ChaosFocus focus = Dungeon.hero.buff(ChaosFocus.class);
			if (focus != null) {
				int cell = bolt.collisionPos;
				Char target = Actor.findChar(cell);
				if (focus.state == ChaosFocus.FocusType.FIRE) {
					Buff.detach(hero, ChaosFocus.class);

					Splash.at(cell, 0xFFFFBB33, 10);

					if (target != null) {
						Buff.affect(target, Burning.class).reignite(target);
						target.damage(ElementalSpellFire.damageRoll(), this);
					} else GameScene.add(Blob.seed(cell, 2, Fire.class));
				}
				else if (focus.state == ChaosFocus.FocusType.ICE) {
					Buff.detach(hero, ChaosFocus.class);

					Splash.at(cell, 0xFF8EE3FF, 10);
					Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
					if (heap != null) {
						heap.freeze();
					}
					Fire fire = (Fire)Dungeon.level.blobs.get( Fire.class );
					if (fire != null) {
						fire.clear( cell );
					}
					if (Dungeon.level.water[cell] && Blob.volumeAt(cell, Freezing.class) == 0) {
						GameScene.add( Blob.seed( cell, 2, Freezing.class ) );
					}

					if (target != null) {
						if (target.buff(Frost.class) != null) {
							Buff.affect(target, Frost.class, 2f);
						} else {
							Chill chill = target.buff(Chill.class);
							target.damage(ElementalSpellIce.damageRoll(), this);
							Buff.affect(target, Chill.class, 3f);

							if (chill != null && chill.cooldown() >= 5f) {
								Buff.affect(target, Frost.class, Frost.DURATION);
							}
						}
					}
				}
				else if (focus.state == ChaosFocus.FocusType.ELEC) {

					Buff.detach(curUser, ChaosFocus.class);
					//lightning deals less damage per-target, the more targets that are hit.
					float multipler = 0.4f + (0.6f/affected.size());
					//if the main target is in water, all affected take full damage
					if (Dungeon.level.water[bolt.collisionPos]) multipler = 1f;
					if (Dungeon.hero.hasTalent(Talent.CHAIN_LIGHTNING)) {
						multipler = 1f;
						if (Dungeon.hero.pointsInTalent(Talent.CHAIN_LIGHTNING) == 2)
							multipler += (multipler*0.1f)*affected.size();
					}

					for (Char ch : affected){
						if (ch == hero) Camera.main.shake(2, 0.3f);
						ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
						ch.sprite.flash();
						if (ch != curUser &&
								ch.alignment == curUser.alignment
								&& ch.pos != bolt.collisionPos) {
							continue;
						}

						if (ch == hero && curUser.belongings.armor
								instanceof ElementalArmor.ElementalArmorElec) {
							// immune to damage
						} else ch.damage((int) (ElementalSpellElec.damageRoll() * multipler), this);
					}
				}

				else {

					Buff.detach(hero, ChaosFocus.class);
					CellEmitter.center(cell).burst(RainbowParticle.BURST, 20);

					Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
					Freezing freezing = (Freezing) Dungeon.level.blobs.get(Freezing.class);
					Electricity electricity = (Electricity) Dungeon.level.blobs.get(Electricity.class);
					// elemental blobs
					if (fire != null && fire.volume > 0) {
						int vol = fire.volume;
						fire.clear(cell);
						GameScene.add(Blob.seed(cell, vol, Freezing.class));

					} else if (freezing != null && freezing.volume > 0) {
						int vol = freezing.volume;
						freezing.clear(cell);
						GameScene.add(Blob.seed(cell, vol, Fire.class));

					} else if (electricity != null && electricity.volume > 0) {
						int vol = electricity.volume;
						electricity.clear(cell);
						Char elec = Actor.findChar(cell);
						if (elec != null) {
							elec.damage(Random.NormalIntRange((int) (vol * 1.5f), vol * 2), Electricity.class);
							CharSprite s = elec.sprite;
							if (s != null && s.parent != null) {
								ArrayList<Lightning.Arc> arcs = new ArrayList<>();
								arcs.add(new Lightning.Arc(new PointF(s.x, s.y + s.height / 2),
										new PointF(s.x + s.width, s.y + s.height / 2)));
								arcs.add(new Lightning.Arc(new PointF(s.x + s.width / 2, s.y),
										new PointF(s.x + s.width / 2, s.y + s.height)));
								s.parent.add(new Lightning(arcs, null));
								Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
							}
						} else Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
					}

					// damm webs!
					Web web = (Web) Dungeon.level.blobs.get(Web.class);
					if (web != null && web.volume > 0) {
						web.clear(cell);
					}

					// grass and water
					int t = Dungeon.level.map[cell];
					if (t == Terrain.WATER) {
						Level.set(cell, Terrain.EMPTY);
						GameScene.updateMap(cell);
					} else if (t == Terrain.EMPTY && t != Terrain.EMBERS) {
						Level.set(cell, Terrain.GRASS);
						GameScene.updateMap(cell);
					} else if (t == Terrain.EMBERS) {
						Level.set(cell, Terrain.EMPTY);
						GameScene.updateMap(cell);
						if (Blob.volumeAt(cell, Fire.class) == 0)
							GameScene.add(Blob.seed(cell, 5, Fire.class));
					} else if (t == Terrain.GRASS) {
						Level.set(cell, Terrain.FURROWED_GRASS);
						GameScene.updateMap(cell);
					} else if (t == Terrain.HIGH_GRASS || t == Terrain.FURROWED_GRASS) {
						Level.set(cell, Terrain.WATER);
						GameScene.updateMap(cell);
					}

					// doors
					int d = Dungeon.level.map[cell];
					if (d == Terrain.DOOR) {
						Level.set(cell, Terrain.OPEN_DOOR);
						GameScene.updateMap(cell);
					} else if (d == Terrain.OPEN_DOOR) {
						Level.set(cell, Terrain.DOOR);
						GameScene.updateMap(cell);
					}
				}
			}
		}
	}
}