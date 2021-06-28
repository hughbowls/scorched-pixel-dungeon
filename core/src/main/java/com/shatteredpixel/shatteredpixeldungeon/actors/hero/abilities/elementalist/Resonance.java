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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SpellWeave;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.HeroicLeap;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.ElementalSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Resonance extends ArmorAbility {

	{
		baseChargeUse = 50f;
	}

	@Override
	public float chargeUse( Hero hero ) {
		float chargeUse = super.chargeUse(hero);
		if (hero.buff(Resonance.DoubleResonaceTracker.class) != null){
			//reduced charge use by 24%/42%/56%/67%
			chargeUse *= Math.pow(0.76, hero.pointsInTalent(Talent.DOUBLE_RESONANCE));
		}
		return chargeUse;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {

		hero.sprite.operate( hero.pos );
		hero.sprite.emitter().start(MagicMissile.MagicParticle.ATTRACTING, 0.05f, 20 );
		Buff.affect(hero, ResonaceTracker.class);

		ResonaceTracker resonaceTracker = hero.buff(ResonaceTracker.class);
		resonaceTracker.countReset();
		resonaceTracker.countUp(3);

		if (hero.buff(DoubleResonaceTracker.class) != null){
			hero.buff(DoubleResonaceTracker.class).detach();
		} else {
			if (hero.hasTalent(Talent.DOUBLE_RESONANCE)) {
				Buff.affect(hero, DoubleResonaceTracker.class, 10);
			}
		}

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();

		Sample.INSTANCE.play(Assets.Sounds.RAY);
		Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
		hero.sprite.operate(hero.pos);

		Invisibility.dispel();
		hero.next();
	}

	public static void castResonace(ElementalSpell spell, Hero hero) {
		ClassArmor armor = (ClassArmor)hero.belongings.armor;
		boolean echoed = false;
		int charsHit = 0;
		WandOfBlastWave.BlastWave.blast(hero.pos);

		for (Mob target : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (target.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[target.pos]) {
				// fire
				if (spell instanceof ElementalSpell.ElementalSpellFire){
					Splash.at(target.pos, 0xFFFFBB33, 10);
					Buff.affect(target, Burning.class).reignite(target);
					target.damage(ElementalSpell.ElementalSpellFire.damageRoll(), spell);

					if (hero.subClass == HeroSubClass.SPELLWEAVER){
						Buff.affect(hero, SpellWeave.class).gainCount();
						SpellWeave weave = hero.buff(SpellWeave.class);
						weave.addCount(1);
					}

					if (!target.isAlive() && Dungeon.hero.hasTalent(Talent.WILDFIRE)
							&& Random.Float() < 0.34f + 0.33f* Dungeon.hero.pointsInTalent(Talent.WILDFIRE)) {
						float extend = 3f + Dungeon.hero.pointsInTalent(Talent.WILDFIRE);
						Buff.affect(Dungeon.hero, ElementalSpell.FireFocus.class).set(Dungeon.hero, extend);
					}
				}
				// ice
				if (spell instanceof ElementalSpell.ElementalSpellIce){
					Splash.at(target.pos, 0xFF8EE3FF, 10);

					Heap heap = Dungeon.level.heaps.get(target.pos);
					if (heap != null) {
						heap.freeze();
					}
					Fire fire = (Fire)Dungeon.level.blobs.get( Fire.class );
					if (fire != null) {
						fire.clear( target.pos );
					}
					if (Dungeon.level.water[target.pos] && Blob.volumeAt(target.pos, Freezing.class) == 0) {
						GameScene.add( Blob.seed( target.pos, 2, Freezing.class ) );
					}

					if (target.buff(Frost.class) != null) {
						Buff.affect(target, Frost.class, 2f);
					} else {
						Chill chill = target.buff(Chill.class);

						target.damage(ElementalSpell.ElementalSpellIce.damageRoll(),
								ElementalSpell.ElementalSpellIce.class);

						if (hero.subClass == HeroSubClass.SPELLWEAVER){
							Buff.affect(hero, SpellWeave.class).gainCount();
							SpellWeave weave = hero.buff(SpellWeave.class);
							weave.addCount(1);
						}

						Buff.affect(target, Chill.class, 3f);

						if (chill != null && chill.cooldown() >= 5f) {
							Buff.affect(target, Frost.class, Frost.DURATION);
						}
					}
				}
				// elec
				if (spell instanceof ElementalSpell.ElementalSpellElec){
					if (Dungeon.level.heroFOV[target.pos]) {
						target.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
						target.sprite.flash();
					}
					target.damage((int) (ElementalSpell.ElementalSpellElec.damageRoll()),
							ElementalSpell.ElementalSpellElec.class);

					if (hero.subClass == HeroSubClass.SPELLWEAVER){
						Buff.affect(hero, SpellWeave.class).gainCount();
						SpellWeave weave = hero.buff(SpellWeave.class);
						weave.addCount(1);
					}

					PathFinder.buildDistanceMap( target.pos, BArray.not( Dungeon.level.solid, null ), 2 );
					for (int arc = 0; arc < PathFinder.distance.length; arc++) {
						Char affected = Actor.findChar(arc);
						if (affected != null) {
							int dist = Dungeon.level.distance(target.pos, affected.pos);
							float spread = 0;
							boolean onWater = (Dungeon.level.water[affected.pos] && !affected.flying);

							if (affected != target && (dist == 1 || (dist == 2 && onWater))) {
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
								affected.damage((int) (ElementalSpell.ElementalSpellElec.damageRoll() * multipler),
										ElementalSpell.ElementalSpellElec.class);

								if (hero.subClass == HeroSubClass.SPELLWEAVER) {
									Buff.affect(hero, SpellWeave.class).gainCount();
									SpellWeave weave = hero.buff(SpellWeave.class);
									weave.addCount(1);
								}
							}
						}
					}
				}
				// chaos
				if (spell instanceof ElementalSpell.ElementalSpellChaos){
					ElementalSpell.ChaosFocus chaos = hero.buff(ElementalSpell.ChaosFocus.class);
					switch (chaos.state) {
						case FIRE: {
							Splash.at(target.pos, 0xFFFFBB33, 10);
							Buff.affect(target, Burning.class).reignite(target);
							target.damage(ElementalSpell.ElementalSpellFire.damageRoll(), spell);

							if (hero.subClass == HeroSubClass.SPELLWEAVER){
								Buff.affect(hero, SpellWeave.class).gainCount();
								SpellWeave weave = hero.buff(SpellWeave.class);
								weave.addCount(1);
							}

							if (!target.isAlive() && Dungeon.hero.hasTalent(Talent.WILDFIRE)
									&& Random.Float() < 0.34f + 0.33f * Dungeon.hero.pointsInTalent(Talent.WILDFIRE)) {
								float extend = 3f + Dungeon.hero.pointsInTalent(Talent.WILDFIRE);
								Buff.affect(Dungeon.hero, ElementalSpell.FireFocus.class).set(Dungeon.hero, extend);
							}
						} break;
						case ICE: {
							Splash.at(target.pos, 0xFF8EE3FF, 10);

							Heap heap = Dungeon.level.heaps.get(target.pos);
							if (heap != null) {
								heap.freeze();
							}
							Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
							if (fire != null) {
								fire.clear(target.pos);
							}
							if (Dungeon.level.water[target.pos] && Blob.volumeAt(target.pos, Freezing.class) == 0) {
								GameScene.add(Blob.seed(target.pos, 2, Freezing.class));
							}
							if (target.buff(Frost.class) != null) {
								Buff.affect(target, Frost.class, 2f);
							} else {
								Chill chill = target.buff(Chill.class);

								target.damage(ElementalSpell.ElementalSpellIce.damageRoll(),
										ElementalSpell.ElementalSpellIce.class);

								if (hero.subClass == HeroSubClass.SPELLWEAVER){
									Buff.affect(hero, SpellWeave.class).gainCount();
									SpellWeave weave = hero.buff(SpellWeave.class);
									weave.addCount(1);
								}

								Buff.affect(target, Chill.class, 3f);

								if (chill != null && chill.cooldown() >= 5f) {
									Buff.affect(target, Frost.class, Frost.DURATION);
								}
							}
						}  break;
						case ELEC: {
							if (Dungeon.level.heroFOV[target.pos]) {
								target.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
								target.sprite.flash();
							}
							target.damage((int) (ElementalSpell.ElementalSpellElec.damageRoll()),
									ElementalSpell.ElementalSpellElec.class);

							if (hero.subClass == HeroSubClass.SPELLWEAVER){
								Buff.affect(hero, SpellWeave.class).gainCount();
								SpellWeave weave = hero.buff(SpellWeave.class);
								weave.addCount(1);
							}

							PathFinder.buildDistanceMap( target.pos, BArray.not( Dungeon.level.solid, null ), 2 );
							for (int arc = 0; arc < PathFinder.distance.length; arc++) {
								Char affected = Actor.findChar(arc);
								if (affected != null) {
									int dist = Dungeon.level.distance(target.pos, affected.pos);
									float spread = 0;
									boolean onWater = (Dungeon.level.water[affected.pos] && !affected.flying);

									if (affected != target && (dist == 1 || (dist == 2 && onWater))) {
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
										affected.damage((int) (ElementalSpell.ElementalSpellElec.damageRoll() * multipler),
												ElementalSpell.ElementalSpellElec.class);

										if (hero.subClass == HeroSubClass.SPELLWEAVER) {
											Buff.affect(hero, SpellWeave.class).gainCount();
											SpellWeave weave = hero.buff(SpellWeave.class);
											weave.addCount(1);
										}
									}
								}
							}
						} break;
						case NORMAL: default: {
							int cell = target.pos;
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
								int vol = electricity.cur[cell];
								electricity.clear(cell);
								Char elec = Actor.findChar(cell);
								if (elec != null) {
									elec.damage(Random.NormalIntRange((int) (vol * 1.5f), vol * 2), Electricity.class);
									if (hero.subClass == HeroSubClass.SPELLWEAVER){
										Buff.affect(hero, SpellWeave.class).gainCount();
										SpellWeave weave = hero.buff(SpellWeave.class);
										weave.addCount(1);
									}

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
						} break;
					}
				}

				charsHit++;
				if (charsHit > 0 && hero.hasTalent(Talent.REACTIVE_RESONANCE)){
					charsHit = Math.min(5, charsHit);
					Buff.affect(hero, Barrier.class).setShield(charsHit
							*2*hero.pointsInTalent(Talent.REACTIVE_RESONANCE));
				}

				if (!target.isAlive() && !echoed && hero.hasTalent(Talent.ECHOING_RESONANCE)){
					int chance = 0;
					switch (hero.pointsInTalent(Talent.ECHOING_RESONANCE)){
						case 1: default: chance = 20; break;
						case 2: chance = 35; break;
						case 3: chance = 50; break;
						case 4: chance = 100; break;
					}
					if (Random.Int(0, 100) <= chance) {
						echoed = true;
						castResonace(spell, hero);
					}
				}
			}
		}
	}

	public static class ResonaceTracker extends Buff {

		private float count = 0;
		private static final String COUNT = "count";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(COUNT, count);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			count = bundle.getFloat(COUNT);
		}

		public void countUp( float inc ){
			count += inc;
			if (count >= 3) count = 3;
		}
		public void countDown( float inc ){
			count -= inc;
			if (count <= 0) detach();
		}
		public void countReset(){ count = 0; }

		public float count(){
			return count;
		}

		@Override
		public int icon() {
			return BuffIndicator.SC_RESONANCE;
		}

		@Override
		public String toString() {
			return Messages.get(Resonance.class, "tracker_name");
		}

		@Override
		public String desc() {
			return Messages.get(Resonance.class, "tracker_desc", count);
		}
	}
	public static class DoubleResonaceTracker extends FlavourBuff {};

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.ECHOING_RESONANCE, Talent.REACTIVE_RESONANCE, Talent.DOUBLE_RESONANCE, Talent.HEROIC_ENERGY};
	}
}
