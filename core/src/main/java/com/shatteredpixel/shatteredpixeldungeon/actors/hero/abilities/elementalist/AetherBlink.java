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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class AetherBlink extends ArmorAbility {

	{
		baseChargeUse = 30f;
	}

	@Override
	public float chargeUse( Hero hero ) {
		float chargeUse = super.chargeUse(hero);
		if (hero.buff(TetherTracker.class) != null){
			//see below how this work
			return 0f;
		}
		return chargeUse;
	}

	@Override
	public String targetingPrompt() {
		Hero hero = Dungeon.hero;
		TetherTracker tether = hero.buff(TetherTracker.class);
		if (tether != null){
			return Messages.get(this, "prompt_tether");
		}

		return Messages.get(this, "prompt");
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		if (target != null) {

			TetherTracker tether = hero.buff(TetherTracker.class);
			PathFinder.buildDistanceMap(hero.pos, BArray.not(Dungeon.level.solid,null), 8);
			// check tether available
			if (tether != null) {
				// if she has tether, it can be far more then 3, so check it "tether.pos ?= target"
				if (Dungeon.level.distance(hero.pos, target) > 3
							&& tether.pos != target) {
					GLog.w( Messages.get(this, "too_far") );
					return;
				}
			} else {
				if (Dungeon.level.distance(hero.pos, target) > 3) {
					GLog.w( Messages.get(this, "too_far") );
					return;
				}
			}

			if (Actor.findChar( target ) != null) {
				GLog.w( Messages.get(this, "no_empty") );
				return;
			}

			if (!Dungeon.level.heroFOV[target]
					&& !Dungeon.level.mapped[target]
					&& !Dungeon.level.visited[target]) {
				GLog.w( Messages.get(this, "no_sight") );
				return;
			}

			// with tether
			if (tether != null && tether.pos == target){
				Buff.detach(hero, TetherTracker.class);

				if (hero.hasTalent(Talent.AFTERSHOCK)) {
					for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
						if (Dungeon.level.adjacent(mob.pos, hero.pos) && mob.alignment != Char.Alignment.ALLY) {
							mob.damage(Random.NormalIntRange(5 + (5 * hero.pointsInTalent(Talent.AFTERSHOCK)),
									10 + (5 * hero.pointsInTalent(Talent.AFTERSHOCK))), this);
						}
					}
					WandOfBlastWave.BlastWave.blast(hero.pos);
				}

			} else {
				TetherTracker oldTracker = hero.buff(TetherTracker.class);
				int oldPos = 0;
				int oldDuration = 0;
				if (oldTracker != null){
					oldPos = oldTracker.pos;
					oldDuration = oldTracker.duration;
				}

				Buff.detach(hero, TetherTracker.class);
				if (armor.charge < chargeUse(hero)){
					GLog.w( Messages.get(this, "no_charge") );
					TetherTracker altTracker = new TetherTracker();
					altTracker.pos = oldPos;
					altTracker.duration = oldDuration;
					altTracker.attachTo(hero);
					return;
				}

				//common usage
				armor.charge -= chargeUse(hero);
				Item.updateQuickslot();

				if (hero.hasTalent(Talent.AFTERSHOCK)) {
					for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
						if (Dungeon.level.adjacent(mob.pos, hero.pos) && mob.alignment != Char.Alignment.ALLY) {
							mob.damage(Random.NormalIntRange(5 + (5 * hero.pointsInTalent(Talent.AFTERSHOCK)),
									10 + (5 * hero.pointsInTalent(Talent.AFTERSHOCK))), this);
						}
					}
					WandOfBlastWave.BlastWave.blast(hero.pos);
				}

				int wasPos = hero.pos;

				if (hero.hasTalent(Talent.AETHER_TETHER)) {
					Buff.detach(hero, TetherTracker.class);
					CellEmitter.get(wasPos).start( ShaftParticle.FACTORY, 0.3f, 4 );

					TetherTracker newTracker = new TetherTracker();
					newTracker.pos = wasPos;
					newTracker.duration = 2*hero.pointsInTalent(Talent.AETHER_TETHER)+1;
					newTracker.attachTo(hero);
				}
			}

			//now blink to target position!
			ScrollOfTeleportation.appear( hero, target );
			hero.sprite.emitter().start(MagicMissile.MagicParticle.ATTRACTING, 0.05f, 20 );
			Sample.INSTANCE.play( Assets.Sounds.MELD );

			//reveal secrets & mobs
			if (hero.hasTalent(Talent.AETHER_VISION)) {
				PathFinder.buildDistanceMap(target, BArray.not(Dungeon.level.solid, null),
						hero.pointsInTalent(Talent.AETHER_VISION) == 4 ? 3
								: (hero.pointsInTalent(Talent.AETHER_VISION) == 3 ? 2 : 1));
				for (int sight = 0; sight < PathFinder.distance.length; sight++) {

					int terr = Dungeon.level.map[sight];
					if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {
						Dungeon.level.discover( sight );

						if (Dungeon.level.heroFOV[sight]) {
							GameScene.discoverTile( sight, terr );
						}
					}

					if (hero.pointsInTalent(Talent.AETHER_VISION) >= 2) {
						Char ch = Actor.findChar(sight);
						if (ch != null && ch.alignment != Char.Alignment.NEUTRAL && ch.alignment != hero.alignment) {
							Buff.append(hero, TalismanOfForesight.CharAwareness.class, 10).charID = ch.id();
						}
					}
				}
			}

			Dungeon.level.occupyCell( hero );
			Dungeon.observe();
			GameScene.updateFog();
			Invisibility.dispel();
			hero.spendAndNext(Actor.TICK);
		}
	}

	public static class TetherTracker extends Buff {

		int pos;
		int duration;

		Emitter e;

		@Override
		public void fx(boolean on) {
			if (on) {
				e = CellEmitter.center(pos);
				e.pour(MagicMissile.MagicParticle.UP, 0.05f);
			}
			else if (e != null) e.on = false;
		}

		@Override
		public boolean act() {
			if (target.isAlive()) {
				spend( TICK );
				if (--duration <= 0) {
					detach();
				}
			} else {
				detach();
			}

			return true;
		}

		public static final String POS = "pos";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(POS, pos);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			pos = bundle.getInt(POS);
		}
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.AFTERSHOCK, Talent.AETHER_VISION, Talent.AETHER_TETHER, Talent.HEROIC_ENERGY};
	}
}
