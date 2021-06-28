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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.alchemist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blizzard;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Inferno;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.AlchemicalCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.BlizzardBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.CausticBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.InfernalBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.ShockingBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCleansing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDragonsBreath;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfEarthenArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfHolyFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfMagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfShielding;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfShroudingFog;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfStamina;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfStormClouds;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Pistol;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ScorpioSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SiegeMachine extends ArmorAbility {

	@Override
	public String targetingPrompt() {
		if (getSiege() == null) {
			return super.targetingPrompt();
		} else {
			return Messages.get(this, "prompt");
		}
	}

	@Override
	public boolean useTargeting(){
		return false;
	}

	{
		baseChargeUse = 35f;
	}

	@Override
	public float chargeUse(Hero hero) {
		if (getSiege() == null) {
			return super.chargeUse(hero);
		} else {
			return 0;
		}
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		SiegeAlly ally = getSiege();

		if (ally != null){
			if (target == null){
				return;
			} else {
				ally.directTocell(target);
			}
		} else {
			ArrayList<Integer> spawnPoints = new ArrayList<>();
			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
				int p = hero.pos + PathFinder.NEIGHBOURS8[i];
				if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
					spawnPoints.add(p);
				}
			}

			if (!spawnPoints.isEmpty()){
				armor.charge -= chargeUse(hero);
				armor.updateQuickslot();

				ally = new SiegeAlly();
				ally.pos = Random.element(spawnPoints);
				GameScene.add(ally);

				ScrollOfTeleportation.appear(ally, ally.pos);
				Dungeon.observe();

				Invisibility.dispel();
				hero.busy();

			} else {
				GLog.w(Messages.get(this, "no_space"));
			}
		}

	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.SHARED_ROUND, Talent.JUGGERNAUT, Talent.TRANSPORTER, Talent.HEROIC_ENERGY};
	}

	private static SiegeAlly getSiege(){
		for (Char ch : Actor.chars()){
			if (ch instanceof SiegeAlly){
				return (SiegeAlly) ch;
			}
		}
		return null;
	}

	public static class SiegeAlly extends DirectableAlly {

		{
			spriteClass = SiegeSprite.class;

			HP = HT = 200 + (50*Dungeon.hero.pointsInTalent(Talent.JUGGERNAUT));
			defenseSkill = 10;

			viewDistance = 8;
			baseSpeed = 2f;

			attacksAutomatically = true;

			properties.add(Property.INORGANIC);
			properties.add(Property.LARGE);
		}

		@Override
		public int attackSkill(Char target) {
			return INFINITE_ACCURACY;
		}

		@Override
		protected boolean canAttack( Char enemy ) {
			Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
			return Dungeon.level.adjacent( pos, enemy.pos ) && attack.collisionPos == enemy.pos;
		}

		@Override
		public int damageRoll() {
			int damage = Random.NormalIntRange(4 + (int)(5*2.5f), 6 + (int)(5*3.5f));
			return damage;
		}

		@Override
		public float attackDelay() {
			Pistol pistol = Dungeon.hero.belongings.getItem(Pistol.class);
			if (pistol != null && pistol.potionAttrib instanceof PotionOfStamina) {
				return super.attackDelay();
			}

			return super.attackDelay()*2f;
		}

		@Override
		public int attackProc( Char enemy, int damage ) {
			damage = super.attackProc( enemy, damage );

			Sample.INSTANCE.play(Assets.Sounds.HIT_CRUSH, 1, Random.Float(0.33f, 0.66f));
			CellEmitter.get(this.pos).burst(SmokeParticle.FACTORY, 2);
			CellEmitter.center(this.pos).burst(BlastParticle.FACTORY, 2);

			CellEmitter.get(enemy.pos).burst(SmokeParticle.FACTORY, 2);
			CellEmitter.center(enemy.pos).burst(BlastParticle.FACTORY, 2);

			Pistol pistol = Dungeon.hero.belongings.getItem(Pistol.class);
			if (Random.Int(4) < Dungeon.hero.pointsInTalent(Talent.SHARED_ROUND) && pistol != null) {

				if (pistol.potionAttrib != null) {
					if (pistol.potionAttrib instanceof PotionOfHealing) {
						// 50% of damage dealt
						int healAmt = Math.round(damage * 0.5f);
						healAmt = Math.min(healAmt, this.HT - this.HP);

						if (healAmt > 0 && this.isAlive()) {
							this.HP += healAmt;
							this.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 1);
							this.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(healAmt));
						}
					} else if (pistol.potionAttrib instanceof PotionOfShielding) {
						// 33% of damage dealt
						int shAmt = Math.round(damage * 0.333f);
						if (shAmt > 0 && this.isAlive()) {

							if (this.shielding() <= 0) {
								Buff.affect(this, Barrier.class).setShield(shAmt);
							} else Buff.affect(this, Barrier.class).incShield(shAmt);

							this.sprite.emitter().burst(MagicMissile.MagicParticle.BURST_ATTRACTING, 10);
							this.sprite.showStatus(0x00A0FF, Integer.toString(shAmt));
						}
					} else if (pistol.potionAttrib instanceof PotionOfLiquidFlame)
						Buff.affect(enemy, Burning.class).reignite(enemy, 4f);
					else if (pistol.potionAttrib instanceof PotionOfFrost)
						Buff.affect(enemy, Chill.class, 4f);
					else if (pistol.potionAttrib instanceof PotionOfParalyticGas)
						Buff.affect(enemy, Paralysis.class, attackDelay()+cooldown());
					else if (pistol.potionAttrib instanceof PotionOfToxicGas)
						Buff.affect(enemy, Poison.class).set(4f);
					else if (pistol.potionAttrib instanceof PotionOfMindVision)
						Buff.append(Dungeon.hero, TalismanOfForesight.CharAwareness.class, 10f).charID = enemy.id();
					else if (pistol.potionAttrib instanceof PotionOfInvisibility)
						Buff.affect(enemy, Blindness.class, 1f+attackDelay());
					else if (pistol.potionAttrib instanceof PotionOfLevitation)
						Buff.affect(enemy, Vertigo.class, 2f);
					else if (pistol.potionAttrib instanceof PotionOfHaste)
						Buff.affect(enemy, Slow.class, 2f);
					else if (pistol.potionAttrib instanceof PotionOfStamina)
						damage -= damage * 0.333f;
					else if (pistol.potionAttrib instanceof PotionOfCleansing)
						PotionOfPurity.purify(this.pos);
					else if (pistol.potionAttrib instanceof PotionOfStormClouds) {
						ArrayList<Char> affected = new ArrayList<>();
						ArrayList<Lightning.Arc> arcs = new ArrayList<>();
						Shocking.arc(this, enemy, 2, affected, arcs);
						if (!Dungeon.level.water[enemy.pos]) {
							affected.remove(enemy);
						}
						for (Char ch : affected) {
							ch.damage(Math.round(damage * 0.4f), this);
						}
						this.sprite.parent.addToFront(new Lightning(arcs, null));
						Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
					} else if (pistol.potionAttrib instanceof PotionOfEarthenArmor) {
						Buff.affect(this, Barkskin.class).add(2, 4);
					} else if (pistol.potionAttrib instanceof PotionOfHolyFuror) {
						if (enemy.properties().contains(Char.Property.DEMONIC)
								|| enemy.properties().contains(Char.Property.UNDEAD)) {
							enemy.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
							damage *= 1.333f;
						}
					} else if (pistol.potionAttrib instanceof AlchemicalCatalyst) {
						damage += Math.max(1, Dungeon.depth / 5);
					}
				}

				// cell effects
				if (pistol.potionAttrib != null) {
					if (pistol.potionAttrib instanceof PotionOfPurity) {
						PotionOfPurity.purify(enemy.pos);
					}
					else if (pistol.potionAttrib instanceof PotionOfLiquidFlame) {
						if (enemy == null && !Dungeon.level.solid[enemy.pos]) {
							Fire.burn(enemy.pos);
						}
					}
					else if (pistol.potionAttrib instanceof PotionOfFrost) {
						if (enemy == null && !Dungeon.level.solid[enemy.pos]) {
							Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
							Freezing.affect(enemy.pos, fire);
							Freezing.freeze(enemy.pos);
						}
					}
					else if (pistol.potionAttrib instanceof PotionOfMagicalSight) {
						for (int n : PathFinder.NEIGHBOURS9) {
							if (Dungeon.level.discoverable[enemy.pos + n])
								Dungeon.level.mapped[enemy.pos + n] = true;

							int terr = Dungeon.level.map[enemy.pos + n];
							if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {
								Dungeon.level.discover(enemy.pos);
								GameScene.discoverTile(enemy.pos, terr);
								ScrollOfMagicMapping.discover(enemy.pos);
							}
						}
						GameScene.updateFog();
					}
					else if (pistol.potionAttrib instanceof PotionOfDragonsBreath) {
						if (!Dungeon.level.solid[enemy.pos]) {
							ArrayList<Char> affected = new ArrayList<>();
							if (Dungeon.level.heroFOV[enemy.pos]) {
								CellEmitter.center(enemy.pos).burst(BlastParticle.FACTORY, 30);
							}
							boolean terrainAffected = false;
							for (int n : PathFinder.NEIGHBOURS9) {
								int c = enemy.pos + n;
								if (c >= 0 && c < Dungeon.level.length()) {
									if (Dungeon.level.heroFOV[c]) {
										CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
									}
									if (Dungeon.level.flamable[c]) {
										Dungeon.level.destroy(c);
										GameScene.updateMap(c);
										terrainAffected = true;
									}
									Heap heap = Dungeon.level.heaps.get(c);
									if (heap != null) heap.explode();
									Char ch = Actor.findChar(c);
									if (ch != null) {
										affected.add(ch);
									}
								}
							}
							for (Char ch : affected) {
								if (!ch.isAlive()) {
									continue;
								}
								int dmg = damageRoll();
								if (ch.pos != enemy.pos && ch != Dungeon.hero) {
									dmg = Math.round(dmg * 0.666f);
								}
								dmg -= ch.drRoll();

								if (dmg > 0) {
									ch.damage(dmg, this);
								}
							}
							if (terrainAffected) {
								Dungeon.observe();
							}
						}
					}
					else if (pistol.potionAttrib instanceof PotionOfShroudingFog) {
						for (int offset : PathFinder.NEIGHBOURS9) {
							if (!Dungeon.level.solid[enemy.pos + offset]) {
								GameScene.add(Blob.seed(enemy.pos + offset, 10, SmokeScreen.class));
								Sample.INSTANCE.play(Assets.Sounds.GAS);
							}
						}
					}
					else if (pistol.potionAttrib instanceof BlizzardBrew) {
						for (int offset : PathFinder.NEIGHBOURS9) {
							if (!Dungeon.level.solid[enemy.pos + offset]) {
								GameScene.add(Blob.seed(enemy.pos + offset, 3, Blizzard.class));
								Sample.INSTANCE.play(Assets.Sounds.GAS);
							}
						}
					}
					else if (pistol.potionAttrib instanceof InfernalBrew) {
						for (int offset : PathFinder.NEIGHBOURS9) {
							if (!Dungeon.level.solid[enemy.pos + offset]) {
								GameScene.add(Blob.seed(enemy.pos + offset, 5, Inferno.class));
								Sample.INSTANCE.play(Assets.Sounds.GAS);
							}
						}
					}
					else if (pistol.potionAttrib instanceof ShockingBrew) {
						for (int offset : PathFinder.NEIGHBOURS9) {
							if (!Dungeon.level.solid[enemy.pos + offset]) {
								GameScene.add(Blob.seed(enemy.pos + offset, 3, Electricity.class));
								Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
							}
						}
					}
					else if (pistol.potionAttrib instanceof CausticBrew) {
						for (int offset : PathFinder.NEIGHBOURS9) {
							if (!Dungeon.level.solid[enemy.pos + offset]) {
								Splash.at(enemy.pos, 0x000000, 5);
								Char caustic = Actor.findChar(enemy.pos);
								if (caustic != null) {
									Buff.affect(caustic, Ooze.class).set(5f);
								}
								Sample.INSTANCE.play(Assets.Sounds.MELD);
							}
						}
					}
				}
			}


			return damage;
		}

		@Override
		protected boolean act() {
			HT = 100 + (25*Dungeon.hero.pointsInTalent(Talent.JUGGERNAUT));
			Buff.append(Dungeon.hero, TalismanOfForesight.CharAwareness.class, TICK).charID = this.id();
			Dungeon.observe();
			GameScene.updateFog();
			boolean result = super.act();
			return result;
		}

		@Override
		public void destroy() {
			super.destroy();
			Dungeon.observe();
			GameScene.updateFog();
		}

		@Override
		public void followHero() {
			GLog.i(Messages.get(this, "direct_follow"));
			super.followHero();
		}

		@Override
		public void targetChar(Char ch) {
			GLog.i(Messages.get(this, "direct_attack"));
			super.targetChar(ch);
		}

		@Override
		public void defendPos(int cell) {
			if (Dungeon.hero.hasTalent(Talent.TRANSPORTER)
					&& Dungeon.level.passable[cell] && !(Dungeon.level.pit[cell])
					&& Dungeon.level.openSpace[cell] && Actor.findChar(cell) == null
					&& Dungeon.hero.buff(Talent.TransporterCooldown.class) == null) {
				ScrollOfTeleportation.appear(this, cell);
				super.defendPos(cell);
				Buff.affect(Dungeon.hero, Talent.TransporterCooldown.class,
						//30, 24, 18, 12
						36-(6*Dungeon.hero.pointsInTalent(Talent.TRANSPORTER)));
			} else {
				super.defendPos(cell);
			}
		}

		@Override
		public String description() {
			String message = Messages.get(this, "desc");
			return message;
		}
	}

	public static class SiegeSprite extends MobSprite {

		private int cellToAttack;

		public SiegeSprite () {
			super();

			texture( Assets.Sprites.DM200 );

			TextureFilm frames = new TextureFilm( texture, 21, 18 );

			int c = 24;

			idle = new Animation( 2, true );
			idle.frames( frames, c+0, c+1 );

			run = new Animation( 10, true );
			run.frames( frames, c+2, c+3 );

			attack = new Animation( 15, false );
			attack.frames( frames, c+5, c+6, c+7, c+8 );

			zap = attack.clone();

			die = new Animation( 8, false );
			die.frames( frames, c+9, c+10, c+11 );

			play( idle );
		}

		@Override
		public void die() {
			emitter().burst( Speck.factory( Speck.WOOL ), 8 );
			super.die();
		}

		@Override
		public int blood() {
			return 0xFFFFFF88;
		}

		@Override
		public void attack( int cell ) {
			if (!Dungeon.level.adjacent( cell, ch.pos )) {

				cellToAttack = cell;
				turnTo( ch.pos , cell );
				play( zap );

			} else {

				super.attack( cell );

			}
		}

		@Override
		public void onComplete( Animation anim ) {
			if (anim == zap) {
				idle();

				((MissileSprite)parent.recycle( MissileSprite.class )).
						reset( this, cellToAttack, new SiegeSprite.SiegeShot(), new Callback() {
							@Override
							public void call() {
								ch.onAttackComplete();
							}
						} );
			} else {
				super.onComplete( anim );
			}
		}

		public class SiegeShot extends Item {
			{
				image = ItemSpriteSheet.PISTOL_SHOT;
			}
		}
	}
}
