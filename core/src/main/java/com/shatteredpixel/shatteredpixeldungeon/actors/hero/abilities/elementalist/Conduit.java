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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.elementalist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SpellWeave;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.ElementalSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Conduit extends ArmorAbility {

	{
		baseChargeUse = 25f;
	}

	private static ConduitNPC getConduit(){
		for (Char ch : Actor.chars()){
			if (ch instanceof ConduitNPC){
				return (ConduitNPC) ch;
			}
		}
		return null;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {

		if (target == null){
			return;
		}

		if (!Dungeon.level.heroFOV[target]){
			GLog.w( Messages.get(Conduit.class, "out_fov") );
			return;
		}

		PathFinder.buildDistanceMap(target, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
		if (Actor.findChar(target) != null || Dungeon.level.pit[target] ||
				(Dungeon.level.solid[target] && !Dungeon.level.passable[target]) ||
				PathFinder.distance[hero.pos] == Integer.MAX_VALUE){
			GLog.w( Messages.get(Conduit.class, "invalid_conduit") );
			return;
		}

		int otherConduit = 0;
		for (Char ch : Actor.chars()){
			if (ch instanceof ConduitNPC){
				otherConduit++;
			}
		}
		if (hero.hasTalent(Talent.CONDUIT_RELAY)){
			if (hero.pointsInTalent(Talent.CONDUIT_RELAY) >= 3 && otherConduit >= 4){
				GLog.w( Messages.get(Conduit.class, "already") );
				return;
			}

			else if (hero.pointsInTalent(Talent.CONDUIT_RELAY) == 2 && otherConduit >= 3){
				GLog.w( Messages.get(Conduit.class, "already") );
				return;
			}

			else if (hero.pointsInTalent(Talent.CONDUIT_RELAY) == 1 && otherConduit >= 2){
				GLog.w( Messages.get(Conduit.class, "already") );
				return;
			}

			else {
				armor.charge -= chargeUse(hero);
				int stabilizer = (!hero.hasTalent(Talent.STABILIZED_CONDUIT)
						|| hero.pointsInTalent(Talent.STABILIZED_CONDUIT) == 1) ? 0
						: (2*hero.pointsInTalent(Talent.STABILIZED_CONDUIT))-2;

				ConduitNPC ally = getConduit();
				ally = new ConduitNPC();
				ally.pos = target;
				ally.count = 4 + stabilizer;
				GameScene.add(ally);
				ScrollOfTeleportation.appear(ally, ally.pos);

				hero.sprite.operate(target);
				Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
				Invisibility.dispel();
				hero.spendAndNext(Actor.TICK);
				return;
			}

		} else {
			if (otherConduit >= 1){
				GLog.w( Messages.get(Conduit.class, "already") );
				return;

			} else {
				armor.charge -= chargeUse(hero);

				ConduitNPC ally = getConduit();
				ally = new ConduitNPC();
				ally.pos = target;
				ally.count = 6 + (hero.pointsInTalent(Talent.STABILIZED_CONDUIT) >= 4 ? 10
						: hero.pointsInTalent(Talent.STABILIZED_CONDUIT) == 3 ? 2 : 0);
				GameScene.add(ally);
				ScrollOfTeleportation.appear(ally, ally.pos);

				hero.sprite.operate(target);
				Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
				Invisibility.dispel();
				hero.spendAndNext(Actor.TICK);
				return;
			}
		}
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.CONDUIT_RELAY, Talent.STABILIZED_CONDUIT, Talent.ELEMENTAL_AMPLIFIER, Talent.HEROIC_ENERGY};
	}

	public static int minFire() {
		Hero hero = Dungeon.hero;
		Item item = hero.belongings.getItem(ElementalSpell.ElementalSpellFire.class);
		int bonus = 1+(hero.pointsInTalent(Talent.ELEMENTAL_MASTER));
		int lvl = item.buffedLvl();

		if (!hero.hasTalent(Talent.ELEMENTAL_MASTER)) bonus = 0;
		if (!hero.hasTalent(Talent.ELEMENTAL_AMPLIFIER)){
			switch (hero.pointsInTalent(Talent.ELEMENTAL_AMPLIFIER)){
				case 0: default: lvl = 0; break;
				case 1: lvl = (int)(item.buffedLvl()*0.25f); break;
				case 2: lvl = (int)(item.buffedLvl()*0.5f); break;
				case 3: lvl = (int)(item.buffedLvl()*0.75f); break;
				case 4: lvl = (int)(item.buffedLvl());
			}
		}
		return 2+(int)(1.5f*lvl)+(2*bonus);
	}
	public static int maxFire() {
		Hero hero = Dungeon.hero;
		Item item = hero.belongings.getItem(ElementalSpell.ElementalSpellFire.class);
		int bonus = 1 + (hero.pointsInTalent(Talent.ELEMENTAL_MASTER));

		int lvl = item.buffedLvl();
		if (!hero.hasTalent(Talent.ELEMENTAL_MASTER)) bonus = 0;
		if (!hero.hasTalent(Talent.ELEMENTAL_AMPLIFIER)){
			switch (hero.pointsInTalent(Talent.ELEMENTAL_AMPLIFIER)){
				case 0: default: lvl = 0; break;
				case 1: lvl = (int)(item.buffedLvl()*0.25f); break;
				case 2: lvl = (int)(item.buffedLvl()*0.5f); break;
				case 3: lvl = (int)(item.buffedLvl()*0.75f); break;
				case 4: lvl = (int)(item.buffedLvl());
			}
		}
		return 4+(int)(2.5f*lvl)+(2+(2*bonus));
	}
	public static int minIce() {
		Hero hero = Dungeon.hero;
		Item item = hero.belongings.getItem(ElementalSpell.ElementalSpellIce.class);
		int bonus = 1+(hero.pointsInTalent(Talent.ELEMENTAL_MASTER));
		if (!hero.hasTalent(Talent.ELEMENTAL_MASTER)) bonus = 0;

		int lvl = item.buffedLvl();
		if (!hero.hasTalent(Talent.ELEMENTAL_MASTER)) bonus = 0;
		if (!hero.hasTalent(Talent.ELEMENTAL_AMPLIFIER)){
			switch (hero.pointsInTalent(Talent.ELEMENTAL_AMPLIFIER)){
				case 0: default: lvl = 0; break;
				case 1: lvl = (int)(item.buffedLvl()*0.25f); break;
				case 2: lvl = (int)(item.buffedLvl()*0.5f); break;
				case 3: lvl = (int)(item.buffedLvl()*0.75f); break;
				case 4: lvl = (int)(item.buffedLvl());
			}
		}
		return 1+(int)(0.5f*lvl)+(2*bonus);
	}
	public static int maxIce() {
		Hero hero = Dungeon.hero;
		Item item = hero.belongings.getItem(ElementalSpell.ElementalSpellIce.class);
		int bonus = 1 + (hero.pointsInTalent(Talent.ELEMENTAL_MASTER));
		if (!hero.hasTalent(Talent.ELEMENTAL_MASTER)) bonus = 0;

		int lvl = item.buffedLvl();
		if (!hero.hasTalent(Talent.ELEMENTAL_MASTER)) bonus = 0;
		if (!hero.hasTalent(Talent.ELEMENTAL_AMPLIFIER)){
			switch (hero.pointsInTalent(Talent.ELEMENTAL_AMPLIFIER)){
				case 0: default: lvl = 0; break;
				case 1: lvl = (int)(item.buffedLvl()*0.25f); break;
				case 2: lvl = (int)(item.buffedLvl()*0.5f); break;
				case 3: lvl = (int)(item.buffedLvl()*0.75f); break;
				case 4: lvl = (int)(item.buffedLvl());
			}
		}
		return 2+(int)(1.5f*lvl)+(2+(2*bonus));
	}
	public static int minElec() {
		Hero hero = Dungeon.hero;
		Item item = hero.belongings.getItem(ElementalSpell.ElementalSpellElec.class);
		int bonus = 1+(hero.pointsInTalent(Talent.ELEMENTAL_MASTER));

		int lvl = item.buffedLvl();
		if (!hero.hasTalent(Talent.ELEMENTAL_MASTER)) bonus = 0;
		if (!hero.hasTalent(Talent.ELEMENTAL_AMPLIFIER)){
			switch (hero.pointsInTalent(Talent.ELEMENTAL_AMPLIFIER)){
				case 0: default: lvl = 0; break;
				case 1: lvl = (int)(item.buffedLvl()*0.25f); break;
				case 2: lvl = (int)(item.buffedLvl()*0.5f); break;
				case 3: lvl = (int)(item.buffedLvl()*0.75f); break;
				case 4: lvl = (int)(item.buffedLvl());
			}
		}
		if (!hero.hasTalent(Talent.ELEMENTAL_MASTER)) bonus = 0;
		return 3+(int)(1.5f*lvl)+(2*bonus);
	}
	public static int maxElec() {
		Hero hero = Dungeon.hero;
		Item item = hero.belongings.getItem(ElementalSpell.ElementalSpellElec.class);
		int bonus = 1+(hero.pointsInTalent(Talent.ELEMENTAL_MASTER));

		int lvl = item.buffedLvl();
		if (!hero.hasTalent(Talent.ELEMENTAL_MASTER)) bonus = 0;
		if (!hero.hasTalent(Talent.ELEMENTAL_AMPLIFIER)){
			switch (hero.pointsInTalent(Talent.ELEMENTAL_AMPLIFIER)){
				case 0: default: lvl = 0; break;
				case 1: lvl = (int)(item.buffedLvl()*0.25f); break;
				case 2: lvl = (int)(item.buffedLvl()*0.5f); break;
				case 3: lvl = (int)(item.buffedLvl()*0.75f); break;
				case 4: lvl = (int)(item.buffedLvl());
			}
		}
		if (!hero.hasTalent(Talent.ELEMENTAL_MASTER)) bonus = 0;
		return 8+(3*lvl)+(2+(2*bonus));
	}

	public static int damageRollFire() { return Random.NormalIntRange( minFire(), maxFire() ); }
	public static int damageRollIce() { return Random.NormalIntRange( minIce(), maxIce() ); }
	public static int damageRollElec() { return Random.NormalIntRange( minElec(), maxElec() ); }

	public static class ConduitNPC extends NPC {

		{
			spriteClass = ConduitNPCSprite.class;
			defenseSkill = 0;

			properties.add(Property.INORGANIC);
			properties.add(Property.IMMOVABLE);

			alignment = Alignment.ALLY;

			HP = HT = 50;
		}

		{
			immunities.add( Burning.class );
			immunities.add( Chill.class );
			immunities.add( Frost.class );

			immunities.add( Paralysis.class );

			immunities.add( ScrollOfPsionicBlast.class );
			immunities.add( ScrollOfRetribution.class );
		}

		@Override
		public boolean isImmune(Class effect) {
			if (effect == ElementalSpell.class) return true;
			if (effect == ConduitFire.class) return true;
			if (effect == ConduitIce.class)  return true;
			if (effect == ConduitElec.class) return true;
			return super.isImmune(effect);
		}

		private static final String COUNT = "count";
		protected int count;

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(COUNT, count);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			count = bundle.getInt(COUNT);
		}

		@Override protected boolean canAttack( Char enemy ) { return false; }
		@Override protected boolean doAttack( Char enemy ) { return false; }
		@Override public void aggro( Char ch ) { } //null

		@Override protected boolean act() {

			if (!(this instanceof ConduitFire
				|| this instanceof ConduitIce
				|| this instanceof ConduitElec)){
				return super.act();
			}

			Hero hero = Dungeon.hero;
			int distance = (hero.pointsInTalent(Talent.CONDUIT_RELAY) == 4 ? 1 : 0) + 1;

			if (Dungeon.level.heroFOV[pos]){
				this.sprite.attack(pos);
				WandOfBlastWave.BlastWave.blast(pos);
				Sample.INSTANCE.play( Assets.Sounds.BLAST );
			}

			PathFinder.buildDistanceMap( pos, BArray.not( Dungeon.level.solid, null ), distance );
			for (int cell = 0; cell < PathFinder.distance.length; cell++) {

				if (PathFinder.distance[cell] < Integer.MAX_VALUE) {
					Char ch = Actor.findChar(cell);

					boolean except = hero.hasTalent(Talent.STABILIZED_CONDUIT);

					if (ch == null) {
						if (this instanceof ConduitFire){
							if (Dungeon.level.heroFOV[cell]) {
								CellEmitter.center(cell).burst(FlameParticle.FACTORY, 3);
							}
							if (Blob.volumeAt(cell, Fire.class) == 0)
								GameScene.add( Blob.seed( cell, 2, Fire.class ) );

						} if (this instanceof ConduitIce){
							if (Dungeon.level.heroFOV[cell]) {
								CellEmitter.center(cell).burst(MagicMissile.WhiteParticle.FACTORY, 3);
							}
							Heap heap = Dungeon.level.heaps.get(cell);
							if (heap != null) {
								heap.freeze();
							}
							Fire fireBlob = (Fire)Dungeon.level.blobs.get( Fire.class );
							if (fireBlob != null) {
								fireBlob.clear( cell );
							}
							if (Dungeon.level.water[cell] && Blob.volumeAt(cell, Freezing.class) == 0) {
								GameScene.add( Blob.seed( cell, 2, Freezing.class ) );
							}

						} if (this instanceof ConduitElec) {
							if (Dungeon.level.heroFOV[cell]) {
								CellEmitter.center(cell).burst(SparkParticle.FACTORY, 3);
							}
						}
					}

					if (ch != null && ch != this && !(ch == hero && except)) {
						// fire
						if (this instanceof ConduitFire){
							Buff.affect(ch, Burning.class).reignite(ch);
							ch.damage(damageRollFire(), ConduitFire.class);

							if (hero.subClass == HeroSubClass.SPELLWEAVER){
								Buff.affect(hero, SpellWeave.class).gainCount();
							}

							if (!ch.isAlive() && hero.hasTalent(Talent.WILDFIRE)
									&& Random.Float() < 0.34f + 0.33f* hero.pointsInTalent(Talent.WILDFIRE)) {
								float extend = 3f + hero.pointsInTalent(Talent.WILDFIRE);
								Buff.affect(hero, ElementalSpell.FireFocus.class).set(hero, extend);
							}
						}

						//ice
						if (this instanceof ConduitIce && !(ch == hero && except)){

							if (ch.buff(Frost.class) != null) {
								Buff.affect(ch, Frost.class, 2f);
							} else {
								Chill chill = ch.buff(Chill.class);
								ch.damage(damageRollIce(), ConduitIce.class);

								if (hero.subClass == HeroSubClass.SPELLWEAVER){
									Buff.affect(hero, SpellWeave.class).gainCount();
								}

								Buff.affect(ch, Chill.class, 3f);

								if (chill != null && chill.cooldown() >= 5f) {
									Buff.affect(ch, Frost.class, Frost.DURATION);
								}
							}
						}

						//elec
						if (this instanceof ConduitElec && !(ch == hero && except)){
							if (Dungeon.level.heroFOV[ch.pos]) {
								ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
								ch.sprite.flash();
							}
							ch.damage((int) (damageRollElec()), ConduitElec.class);

							if (hero.subClass == HeroSubClass.SPELLWEAVER){
								Buff.affect(hero, SpellWeave.class).gainCount();
							}

							PathFinder.buildDistanceMap( ch.pos, BArray.not( Dungeon.level.solid, null ), 2 );
							for (int arc = 0; arc < PathFinder.distance.length; arc++) {
								Char affected = Actor.findChar(arc);
								if (affected != null) {
									int dist = Dungeon.level.distance(ch.pos, affected.pos);
									float spread = 0;
									boolean onWater = (Dungeon.level.water[affected.pos] && !affected.flying);

									if (affected != ch && (dist == 1 || (dist == 2 && onWater))) {
										spread++;

										float multipler = 0.4f + (0.6f / spread);
										if (onWater) multipler = 1f;
										if (Dungeon.hero.hasTalent(Talent.CHAIN_LIGHTNING)) {
											multipler = 1f;
											if (Dungeon.hero.pointsInTalent(Talent.CHAIN_LIGHTNING) == 2)
												multipler += (multipler * 0.1f) * spread;
										}

										if (Dungeon.level.heroFOV[affected.pos]) {
											affected.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
											affected.sprite.flash();
										}

										affected.damage((int) (damageRollElec() * multipler), ConduitElec.class);

										if (affected instanceof Mob){
											((Mob) affected).aggro(this);
										}

										if (hero.subClass == HeroSubClass.SPELLWEAVER) {
											Buff.affect(hero, SpellWeave.class).gainCount();
										}
									}
								}
							}
						}

						if (ch instanceof Mob){
							((Mob) ch).aggro(this);
						}
					}
				}
			}

			count--;
			if (count <= 0)
				die(this);

			return super.act();
		}
		@Override
		public void damage( int dmg, Object src ) {
			Hero hero = Dungeon.hero;

			if (src == ConduitFire.class) return;
			if (src == ConduitIce.class)  return;
			if (src == ConduitElec.class) return;

			if (src instanceof ElementalSpell){

				if (src instanceof ElementalSpell.ElementalSpellFire){
					Char change = new ConduitFire();
					change.pos = this.pos;
					change.HP = this.HP;
					if (count > 0){
						((ConduitNPC)change).count = this.count;
					} else {
						((ConduitNPC)change).count = 4 + 2*hero.pointsInTalent(Talent.STABILIZED_CONDUIT);
					}
					this.sprite.remove();
					this.die(src);
					GameScene.add((Mob)change);
				}
				if (src instanceof ElementalSpell.ElementalSpellIce){
					Char change = new ConduitIce();
					change.pos = this.pos;
					change.HP = this.HP;
					if (count > 0){
						((ConduitNPC)change).count = this.count;
					} else {
						((ConduitNPC)change).count = 4 + 2*hero.pointsInTalent(Talent.STABILIZED_CONDUIT);
					}
					this.sprite.remove();
					this.die(src);
					GameScene.add((Mob)change);
				}
				if (src instanceof ElementalSpell.ElementalSpellElec){
					this.die(src);
					Char change = new ConduitElec();
					change.pos = this.pos;
					change.HP = this.HP;
					if (count > 0){
						((ConduitNPC)change).count = this.count;
					} else {
						((ConduitNPC)change).count = 4 + 2*hero.pointsInTalent(Talent.STABILIZED_CONDUIT);
					}
					this.sprite.remove();
					this.die(src);
					GameScene.add((Mob)change);
				}
			}

			super.damage(dmg, src);
		}

		@Override public String info(){
			Hero hero = Dungeon.hero;
			int distance = (hero.pointsInTalent(Talent.CONDUIT_RELAY) == 4 ? 1 : 0) + 1;

			String desc = super.info();
			desc = Messages.get(this, "desc", count);

			if (this instanceof ConduitFire) {
				desc += "\n\n" + Messages.get(this, "fire", distance, distance, minFire(), maxFire());
			} else if (this instanceof ConduitIce) {
				desc += "\n\n" + Messages.get(this, "ice", distance, distance, minIce(), maxIce());
			} else if (this instanceof ConduitElec) {
				desc += "\n\n" + Messages.get(this, "elec", distance, distance, minElec(), maxElec());
			} else
				desc += "\n\n" + Messages.get(this, "none");

			return desc;
		}

		public static class ConduitFire extends Conduit.ConduitNPC {

			{
				spriteClass = ConduitNPCSpriteFire.class;
			}
		}
		public static class ConduitIce extends Conduit.ConduitNPC {

			{
				spriteClass = ConduitNPCSpriteIce.class;
			}
		}
		public static class ConduitElec extends Conduit.ConduitNPC {

			{
				spriteClass = ConduitNPCSpriteElec.class;
			}
		}
	}

	public static class ConduitNPCSprite extends MobSprite {

		public ConduitNPCSprite(){
			super();

			texture( Assets.Sprites.CONDUIT );

			TextureFilm frames = new TextureFilm( texture, 16, 16 );

			idle = new Animation( 0, true );
			idle.frames( frames, 0 );

			run = idle.clone();

			attack = new Animation( 15, false );
			attack.frames( frames, 0, 1, 2, 3, 4, 5 );

			zap = attack.clone();

			die = new Animation( 12, false );
			die.frames( frames, 0, 6, 7 );

			play( idle );

		}

		@Override
		public void showAlert() {
			//do nothing
		}

		@Override
		public int blood() {
			return 0xFFFFFF;
		}
	}

	public static class ConduitNPCSpriteFire extends MobSprite {

		public ConduitNPCSpriteFire(){
			super();

			texture( Assets.Sprites.CONDUIT );

			TextureFilm frames = new TextureFilm( texture, 16, 16 );

			idle = new Animation( 0, true );
			idle.frames( frames, 24 );

			run = idle.clone();

			attack = new Animation( 15, false );
			attack.frames( frames, 24, 25, 26, 27, 28, 29 );

			zap = attack.clone();

			die = new Animation( 12, false );
			die.frames( frames, 24, 30, 31 );

			play( idle );

		}

		@Override
		public void showAlert() {
			//do nothing
		}

		@Override
		public int blood() {
			return 0xFF7B00;
		}

		@Override
		public void die() {
			emitter().burst( FlameParticle.FACTORY, 5 );
			super.die();
		}
	}

	public static class ConduitNPCSpriteIce extends MobSprite {

		public ConduitNPCSpriteIce(){
			super();

			texture( Assets.Sprites.CONDUIT );

			TextureFilm frames = new TextureFilm( texture, 16, 16 );

			idle = new Animation( 0, true );
			idle.frames( frames, 16 );

			run = idle.clone();

			attack = new Animation( 15, false );
			attack.frames( frames, 16, 17, 18, 19, 20, 21 );

			zap = attack.clone();

			die = new Animation( 12, false );
			die.frames( frames, 16, 21, 22 );

			play( idle );

		}

		@Override
		public void showAlert() {
			//do nothing
		}

		@Override
		public int blood() {
			return 0x00D1FF;
		}

		@Override
		public void die() {
			emitter().burst( MagicMissile.MagicParticle.UP, 5 );
			super.die();
		}
	}

	public static class ConduitNPCSpriteElec extends MobSprite {

		public ConduitNPCSpriteElec(){
			super();

			texture( Assets.Sprites.CONDUIT );

			TextureFilm frames = new TextureFilm( texture, 16, 16 );

			idle = new Animation( 0, true );
			idle.frames( frames, 8 );

			run = idle.clone();

			attack = new Animation( 15, false );
			attack.frames( frames, 8, 9, 10, 11, 12, 13 );

			zap = attack.clone();

			die = new Animation( 12, false );
			die.frames( frames, 8, 14, 15 );

			play( idle );

		}

		@Override
		public void showAlert() {
			//do nothing
		}

		@Override
		public int blood() {
			return 0xFFD800;
		}

		@Override
		public void die() {
			emitter().burst( SparkParticle.FACTORY, 5 );
			super.die();
		}
	}

}
