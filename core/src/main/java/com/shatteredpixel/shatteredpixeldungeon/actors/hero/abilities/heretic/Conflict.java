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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.heretic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class Conflict extends ArmorAbility {

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	{
		baseChargeUse = 35f;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		if (target == null){
			return;
		}

		Char ch = Actor.findChar(target);
		for (Mob mob : Dungeon.level.mobs) {
			if (mob.buff(ConflictTracker.class) == null){

				if (ch == null){
					GLog.w(Messages.get(this, "no_target"));
					return;
				}

				if (ch != null){
					Buff.prolong(ch, StoneOfAggression.Aggression.class, ConflictTracker.DURATION);
					Buff.prolong(ch, ConflictTracker.class, ConflictTracker.DURATION);
					ch.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
				}

				Sample.INSTANCE.play( Assets.Sounds.DEATH );

				armor.charge -= chargeUse( hero );
				armor.updateQuickslot();
				hero.sprite.zap(target);

				hero.next();
				return;

			} else {

				if (Dungeon.hero.hasTalent(Talent.CENTER_OF_CONFLICT)) {
					if (ch == null){
						GLog.w(Messages.get(this, "no_target"));
						return;
					}

					if (ch == mob) {
						GLog.w(Messages.get(this, "already"));
						return;

					}

					if (Dungeon.level.distance(ch.pos, mob.pos)
							>= 2 * Dungeon.hero.pointsInTalent(Talent.CENTER_OF_CONFLICT)) {
						int pos1 = ch.pos;
						int pos2 = mob.pos;

						ScrollOfTeleportation.appear(ch, pos2);
						ScrollOfTeleportation.appear(mob, pos1);

						if (Dungeon.hero.pointsInTalent(Talent.CENTER_OF_CONFLICT) == 4) {
							Buff.affect(ch, Paralysis.class, 2f);
							Buff.affect(mob, Paralysis.class, 2f);
						}

						armor.charge -= chargeUse(hero);
						armor.updateQuickslot();
						hero.sprite.zap(target);

						hero.next();
						return;

					} else {
						GLog.w(Messages.get(this, "too_far"));
						return;
					}

				} else {
					GLog.w(Messages.get(this, "already"));
					return;
				}
			}
		}
	}

	public static class ConflictTracker extends FlavourBuff{

		public static final float DURATION = 10f;

		@Override
		public boolean act() {
			if (target.buff(StoneOfAggression.Aggression.class) == null)
				detach();

			return super.act();
		}

		@Override
		public int icon() { return BuffIndicator.AMOK; }

		@Override
		public String toString() { return Messages.get(this, "name"); }

		@Override
		public String desc() {
			return Messages.get(this, "desc", dispTurns());
		}
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.EATER_OF_CONFLICT, Talent.END_OF_CONFLICT, Talent.CENTER_OF_CONFLICT, Talent.HEROIC_ENERGY};
	}

}
