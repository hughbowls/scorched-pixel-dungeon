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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.trollhero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class GreatThrowingWeapon extends ArmorAbility {

	{
		baseChargeUse = 30f;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt1");
	}

	@Override
	public void activate( ClassArmor armor, Hero hero, Integer target ) {

		if (target == null) {
			return;
		}

		if (IsGTW() != null) {
			nowThrow(IsGTW(), target);
			return;
		}

		if (!Dungeon.level.adjacent(hero.pos, target)){
			GLog.w(Messages.get(this, "too_far"));
			return;
		}

		Char ch = Actor.findChar(target);
		if (ch == null) {
			GLog.w(Messages.get(this, "no_target"));
			return;
		}

		if (ch == hero || ch instanceof NPC){
			GLog.w(Messages.get(this, "invalid_gtw"));
			return;
		}

		if (IsGTW() != null){
			Buff.detach(IsGTW(), GTWTracker.class);
		}
		Buff.affect(ch, GTWTracker.class, 1f);
	}

	public void nowThrow(Char ch, Integer dest) {
		Hero hero = Dungeon.hero;
		ClassArmor armor = (ClassArmor)hero.belongings.armor;

		if (dest == null){
			GLog.w(Messages.get(GreatThrowingWeapon.class, "no_target"));
			return;
		}

		if (ch.pos == dest || hero.pos == dest){
			GLog.w(Messages.get(GreatThrowingWeapon.class, "no_self"));
			return;
		}

		if (Dungeon.level.solid[dest] && Dungeon.level.losBlocking[dest]){
			GLog.w(Messages.get(GreatThrowingWeapon.class, "no_wall"));
			return;
		}

		hero.busy();
		hero.sprite.zap(dest);
		ch.sprite.jump(ch.pos, dest, new Callback() {
			@Override
			public void call() {
				Char collide = Actor.findChar(dest);
				boolean ignite = ch.buff(Burning.class) != null;

				ch.move(dest);
				Dungeon.level.occupyCell(ch);

				int damage = Random.NormalIntRange(5+Dungeon.depth, 10+Dungeon.depth*2);
				if (hero.hasTalent(Talent.SHOT_PUT)){
					if (hero.pointsInTalent(Talent.SHOT_PUT) == 1){
						if (Dungeon.level.trueDistance(ch.pos, dest) >= 5){
							damage *= 1.5f;
						}
					}
					if (hero.pointsInTalent(Talent.SHOT_PUT) == 2){
						if (Dungeon.level.trueDistance(ch.pos, dest) >= 4){
							damage *= 1.5f;
						}
					}
					if (hero.pointsInTalent(Talent.SHOT_PUT) == 3){
						if (Dungeon.level.trueDistance(ch.pos, dest) >= 3){
							damage *= 1.5f;
						}
					}
					if (hero.pointsInTalent(Talent.SHOT_PUT) == 4){
						if (Dungeon.level.trueDistance(ch.pos, dest) >= 3){
							damage *= 2f;
						}
					}
				}
				if (!Dungeon.level.pit[dest]) {
					ch.sprite.flash();
					ch.sprite.bloodBurstA(ch.sprite.center(), damage);
					ch.damage(damage - ch.drRoll(), hero);

					if (ch.isAlive() && hero.hasTalent(Talent.DAZING_COLLIDE)) {
						if (hero.pointsInTalent(Talent.DAZING_COLLIDE) == 1){
							Buff.affect(ch, Vertigo.class, 2f);
						}
						if (hero.pointsInTalent(Talent.DAZING_COLLIDE) == 2){
							Buff.affect(ch, Vertigo.class, 3f);
						}
						if (hero.pointsInTalent(Talent.DAZING_COLLIDE) == 3){
							Buff.affect(ch, Paralysis.class, 3f);
						}
						if (hero.pointsInTalent(Talent.DAZING_COLLIDE) == 4){
							Buff.affect(ch, Paralysis.class, 4f);
						}
					}

					Sample.INSTANCE.play(Assets.Sounds.HIT_CRUSH);
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
					Camera.main.shake(2, 0.5f);
				}

				armor.charge -= chargeUse(hero);

				if (collide != null){
					float regain = 0f;
					if (hero.hasTalent(Talent.GREAT_COMMUNICATOR)){
						regain = chargeUse(hero) * (0.2f * hero.pointsInTalent(Talent.GREAT_COMMUNICATOR));
					}
					armor.charge += regain;

					ArrayList<Integer> candidates = new ArrayList<>();
					for (int n : PathFinder.NEIGHBOURS8) {
						int bounce = dest + n;
						if (!Dungeon.level.solid[bounce] && Actor.findChar( bounce ) == null
								&& (!Char.hasProp(collide, Char.Property.LARGE)
								|| Dungeon.level.openSpace[bounce])) {
							candidates.add( bounce );
						}
					}
					Random.shuffle(candidates);
					Char toPush = Char.hasProp(collide, Char.Property.IMMOVABLE) ? ch : collide;

					if (ch != null && ch.isAlive()){
						Buff.detach(ch, GTWTracker.class);
					}

					if (!candidates.isEmpty()){
						Actor.addDelayed( new Pushing( toPush, toPush.pos, candidates.get(0) ), -1 );

						collide.pos = candidates.get(0);
						Dungeon.level.occupyCell(collide);
						if (!Dungeon.level.pit[collide.pos]) {
							collide.sprite.flash();
							collide.sprite.bloodBurstA(collide.sprite.center(), damage);
							collide.damage(damage - collide.drRoll(), hero);

							if (collide.isAlive()){
								if (hero.hasTalent(Talent.DAZING_COLLIDE)) {
									if (hero.pointsInTalent(Talent.DAZING_COLLIDE) == 1){
										Buff.affect(collide, Vertigo.class, 2f);
									}
									if (hero.pointsInTalent(Talent.DAZING_COLLIDE) == 2){
										Buff.affect(collide, Vertigo.class, 3f);
									}
									if (hero.pointsInTalent(Talent.DAZING_COLLIDE) == 3){
										Buff.affect(collide, Paralysis.class, 3f);
									}
									if (hero.pointsInTalent(Talent.DAZING_COLLIDE) == 4){
										Buff.affect(collide, Paralysis.class, 4f);
									}
								}
								if (ignite) collide.buff(Burning.class).reignite(collide);
							}

							Sample.INSTANCE.play(Assets.Sounds.HIT_CRUSH);
							Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
						}
					}
				}

				armor.updateQuickslot();
				Invisibility.dispel();
				hero.spendAndNext(Actor.TICK);
			}
		});
	}

	private static Char IsGTW(){
		for (Char ch : Actor.chars()){
			if (ch.buff(GTWTracker.class) != null)
				return ch;
		}
		return null;
	}
	public static class GTWTracker extends FlavourBuff {

		private static final String POS	= "pos";
		private int pos;

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( POS, pos );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			pos = bundle.getInt( POS );
		}

		@Override public int icon() {
			return BuffIndicator.SC_AIM;
		}
		@Override public void tintIcon(Image icon) {
			icon.hardlight(0, 1f, 0);
		}
		@Override public String toString() {
			return Messages.get(this, "name");
		}
		@Override public String desc() {
			return Messages.get(this, "desc");
		}
		@Override public boolean attachTo( Char target ){
			target.sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "msg1"));
			pos = target.pos;
			return super.attachTo(target);
		}
		@Override public void detach(){
			if (pos == target.pos)
				target.sprite.showStatus(CharSprite.NEGATIVE,Messages.get(this, "msg2"));
			super.detach();
		}
	};

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.SHOT_PUT, Talent.DAZING_COLLIDE, Talent.GREAT_COMMUNICATOR, Talent.HEROIC_ENERGY };
	}
}
