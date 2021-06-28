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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class TrollGeomancy extends Buff {
	
	{
		type = buffType.POSITIVE;
	}
	
	private int stacks = 0;
	private int step = 0;
	private int partialStacks = 0;

	@Override
	public boolean act() {
		if (stacks <= 0) {
			detach();
		} else {
			if (step == 0){
				step = target.pos;
			}
			ArrayList<Integer> walls = new ArrayList<>();
			for (int n : PathFinder.NEIGHBOURS8) {
				int w = step + n;
				if (Dungeon.level.solid[w]
						&& (Dungeon.level.map[w] == Terrain.WALL_DECO
						|| Dungeon.level.map[w] == Terrain.WALL)) {
					walls.add( w );
				}
			}

			if (((Hero)target).hasTalent(Talent.HARMONY)){
				if (walls.size() > 0){
					partialStacks++;
					if (partialStacks >= (4-((Hero)target).pointsInTalent(Talent.HARMONY))){
						partialStacks = 0;
						gainStack();
					}
				}
			}

			if (walls.size() <= 0) {
				fall();
				detach();
			}
		}
		spend(TICK);
		return true;
	}

	public void setStep(int cell) {
		step = cell;
	}

	public int min() {
		float avalanche = stacks * ((Hero)target).pointsInTalent(Talent.AVALANCHE) >= 2 ? 1.333f : 1.25f;
		return Math.max((int)(Dungeon.depth * 0.75f), 2) * Math.max((int)avalanche, 1);
	}
	public int max() {
		float avalanche = stacks * ((Hero)target).pointsInTalent(Talent.AVALANCHE) >= 2 ? 1.333f : 1.25f;
		return Math.max((int)(Dungeon.depth * 1.25f), 4) * Math.max((int)avalanche, 1);
	}

	public void fall() {
		int distance = ((Hero)target).pointsInTalent(Talent.AVALANCHE) == 3 ? 2 : 1;
		for (Mob ch : Dungeon.level.mobs.toArray(new Mob[0])){
			if (Dungeon.level.distance(step, ch.pos) <= distance){
				if (ch != null && ch != target && ch.alignment != Char.Alignment.ALLY
						&& ch.alignment != Char.Alignment.NEUTRAL) {

					int damage = Random.NormalIntRange(min(), max());

					if (stacks >= 9) {
						CellEmitter.floor( ch.pos ).start( Speck.factory( Speck.ROCK ), 0.07f, 10 );

						damage = Math.max(0, damage);
						ch.damage(damage, this);
						Buff.affect(ch, Paralysis.class, Math.max(ch.cooldown()*2, 3f));

						Camera.main.shake( 3, 0.7f );
						Sample.INSTANCE.play( Assets.Sounds.ROCKS );

						if (((Hero)target).pointsInTalent(Talent.LANDSLIDER) >= 1
								&& target.buff(Stamina.class) == null){
							Buff.affect(target, Stamina.class, 10f);
						}

					} else if (stacks >= 6) {
						CellEmitter.floor( ch.pos ).start( Speck.factory( Speck.ROCK ), 0.07f, 6 );

						damage = Math.max(0, damage - (ch.drRoll()/2));
						ch.damage(damage, this);
						Buff.affect(ch, Paralysis.class, ch.cooldown()*2);

						Camera.main.shake( 1, 0.5f );
						Sample.INSTANCE.play( Assets.Sounds.ROCKS, 0.666f );

						if (((Hero)target).pointsInTalent(Talent.LANDSLIDER) >= 2
								&& target.buff(Stamina.class) == null){
							Buff.affect(target, Stamina.class, 10f);
						}

					} else if (stacks >= 3) {
						CellEmitter.floor( ch.pos ).start( Speck.factory( Speck.ROCK ), 0.07f, 3 );

						damage = Math.max(0, damage - (ch.drRoll()));
						ch.damage(damage, this);

						Sample.INSTANCE.play( Assets.Sounds.ROCKS, 0.333f );

						if (((Hero)target).pointsInTalent(Talent.LANDSLIDER) == 3
								&& target.buff(Stamina.class) == null){
							Buff.affect(target, Stamina.class, 10f);
						}
					}
				}
			}
		}
	}

	@Override
	public void detach() {
		super.detach();
	}
	
	public void gainStack(){
		stacks = Math.min(stacks+1, 10);
	}
	
	public int stacks(){
		return stacks;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.SC_EARTH;
	}

	@Override
	public void tintIcon(Image icon) {
		if (stacks < 3) icon.brightness(0.25f);
		else icon.resetColor();
	}

	@Override
	public float iconFadePercent() {
		return (10-stacks)/10f;
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}
	
	@Override
	public String desc() {
		String info = Messages.get(this, "desc");

		if (stacks >= 9) {
			info += Messages.get(this, "high", stacks, min(), max());
		} else if (stacks >= 6) {
			info += Messages.get(this, "mid", stacks, min(), max());
		} else if (stacks >= 3) {
			info += Messages.get(this, "low", stacks, min(), max());
		}

		return info;
	}
	
	private static final String STACKS = "stacks";
	private static final String STEP   = "step";
	private static final String PARTIAL_STACKS   = "partialStacks";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(STACKS, stacks);
		bundle.put(STEP, step);
		bundle.put(PARTIAL_STACKS, partialStacks);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		stacks = bundle.getInt(STACKS);
		step = bundle.getInt(STEP);
		partialStacks = bundle.getInt(PARTIAL_STACKS);
	}
}
