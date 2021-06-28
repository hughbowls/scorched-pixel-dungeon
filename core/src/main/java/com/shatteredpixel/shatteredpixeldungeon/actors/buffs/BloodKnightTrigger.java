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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

public class BloodKnightTrigger extends Buff implements ActionIndicator.Action {
	
	@Override
	public int icon() {
		return BuffIndicator.SC_DROP;
	}
	
	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(1f, 0f, 0f);
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}
	
	public void set() {
		ActionIndicator.setAction( this );
		BuffIndicator.refreshHero(); //refresh the buff visually on-hit
	}

	@Override
	public void detach() {
		super.detach();
		ActionIndicator.clearAction(this);
	}

	@Override
	public boolean act() {
		spend(TICK);
		for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])){
			if (!Dungeon.level.heroFOV[m.pos])
				detach();
			if (m == null)
				detach();
		}
		return true;
	}

	@Override
	public String desc() {
		String desc = Messages.get(this, "desc");
		return desc;
	}

	@Override
	public Image getIcon() {
		Image icon;
		icon = Icons.get(Icons.TARGET);
		icon.tint(0xFFFF0000);
		return icon;
	}

	@Override
	public void doAction() {
		GameScene.selectCell(trigger);
	}

	private CellSelector.Listener trigger = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer cell) {
			if (cell == null) return;
			final Char enemy = Actor.findChar( cell );
			if (enemy == null
					|| enemy == Dungeon.hero
					|| !Dungeon.level.heroFOV[cell]
					|| enemy.buff(Bleeding.class) == null
					|| enemy.isInvulnerable(target.getClass())){
				GLog.w( Messages.get(BloodKnightTrigger.class, "bad_target") );
			} else {
				target.sprite.attack(cell, new Callback() {
					@Override
					public void call() {
						doExlposion(enemy);
					}
				});
			}
		}

		private void doExlposion(final Char enemy){

			AttackIndicator.target(enemy);

			detach();
			ActionIndicator.clearAction(BloodKnightTrigger.this);

			Bleeding b = enemy.buff(Bleeding.class);
			int dmg = (int)((b.level + (enemy.HT-enemy.HP)*b.level*0.1));
			if (Dungeon.hero.hasTalent(Talent.PREDATOR)) dmg += dmg*0.25;

			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
				Char nearby = Actor.findChar(enemy.pos + PathFinder.NEIGHBOURS8[i]);
				if (nearby != null && nearby != target && nearby.alignment != Char.Alignment.ALLY) {
					nearby.damage( dmg, target );
					nearby.sprite.bloodBurstA( nearby.sprite.center(), dmg );
					nearby.sprite.flash();
				}
			}

			enemy.damage( dmg, target );
			Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
			Splash.at( enemy.sprite.center(), enemy.sprite.blood(), 20 );
			enemy.sprite.flash();
			b.detach();
			if (!enemy.isAlive()){
				ScrollOfTeleportation.teleportToLocation((Hero)target, enemy.pos);
				for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
					if (mob.alignment != Char.Alignment.ALLY && Dungeon.level.heroFOV[mob.pos]) {
						if (Dungeon.hero.pointsInTalent(Talent.PREDATOR) >= 2){
							int dur = Dungeon.hero.pointsInTalent(Talent.PREDATOR) == 2 ? 1 : 3;
							Buff.affect( mob, Paralysis.class, dur + mob.cooldown() );
						}
						if (Dungeon.hero.hasTalent(Talent.SERUM)){
							Dewdrop dewdrop = new Dewdrop();
							Dungeon.level.drop( dewdrop, Dungeon.hero.pos ).sprite.drop();
						}
					}
				}
			}

			Hero hero = (Hero)target;

			detach();
			ActionIndicator.clearAction(BloodKnightTrigger.this);
			hero.spendAndNext(Actor.TICK);
		}

		@Override
		public String prompt() {
			return Messages.get(BloodKnightTrigger.class, "prompt");
		}
	};
}
