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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

public class TrollJump extends Buff implements ActionIndicator.Action {

	public float left;
	
	@Override
	public boolean act() {
		if (left > 0){
			if (target == Dungeon.hero){
				ActionIndicator.setAction(this);
			}
			left--;
			spend(TICK);
		} else {
			detach();
		}
		return true;
	}
	
	@Override
	public void detach() {
		super.detach();
		if (target == Dungeon.hero) {
			ActionIndicator.clearAction(this);
		}
	}

	public void setJump(float set) {
		if (target == Dungeon.hero){
			Sample.INSTANCE.play( Assets.Sounds.EVOKE, 1f, 0.8f  );
			ActionIndicator.setAction(this);
		}
		left = set;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.SC_AIM;
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		String desc;
		if (target == Dungeon.hero) desc = Messages.get(this, "desc_hero", left);
		else desc = Messages.get(this, "desc_mob", left);
		return desc;
	}
	
	private static final String LEFT = "left";
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		left = bundle.getFloat(LEFT);
		if (target == Dungeon.hero && left > 0){
			ActionIndicator.setAction(this);
		}
	}
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEFT, left);
	}

	@Override
	public Image getIcon() {
		Image icon;
		icon = new Image(Assets.Interfaces.TALENT_ICONS, 112, 112, 16, 16);
		return icon;
	}
	
	@Override
	public void doAction() {
		GameScene.selectCell(jump);
	}

	private CellSelector.Listener jump = new CellSelector.Listener() {
		
		@Override
		public void onSelect(Integer cell) {
			if (cell == null) return;
			final Char enemy = Actor.findChar(cell);
			if (enemy == null || Dungeon.hero.isCharmedBy(enemy) || enemy instanceof NPC || !Dungeon.level.heroFOV[cell]) {
				GLog.w(Messages.get(TrollJump.class, "no_target"));

			} else if (enemy.buff(TrollJump.class) == null) {
				GLog.w(Messages.get(TrollJump.class, "no_aim"));

			} else {
				//just attack them then!
				if (Dungeon.hero.canAttack(enemy)){
					Dungeon.hero.curAction = new HeroAction.Attack( enemy );
					Dungeon.hero.next();
					return;
				}
				
				boolean[] jumpable = BArray.not(Dungeon.level.solid, null);

				//we consider passable and cell occupancy for adjacent cells to target
				for (int i : PathFinder.NEIGHBOURS9){
					if (Actor.findChar(cell+i) != null)     jumpable[cell+i] = false;
					if (!Dungeon.level.passable[cell+i])    jumpable[cell+i] = false;
				}
				
				PathFinder.Path path = PathFinder.find(Dungeon.hero.pos, cell, jumpable);
				int attackPos = path == null ? -1 : path.get(path.size()-2);
				
				if (attackPos == -1 || Dungeon.hero.rooted){
					GLog.w(Messages.get(TrollJump.class, "cant_jump"));
					return;
				}

				final int dest = attackPos;
				Dungeon.hero.busy();
				Dungeon.hero.sprite.jump(Dungeon.hero.pos, attackPos, new Callback() {
					@Override
					public void call() {
						Dungeon.hero.move(dest);
						Dungeon.level.occupyCell(Dungeon.hero);
						Dungeon.observe();
						Dungeon.hero.checkVisibleMobs();
						GameScene.updateFog();

						Camera.main.shake(2, 0.5f);

						Invisibility.dispel();

						Dungeon.hero.sprite.turnTo( Dungeon.hero.pos, enemy.pos);
						Dungeon.hero.curAction = new HeroAction.Attack( enemy );
						Dungeon.hero.next();
						CellEmitter.center( dest ).burst( Speck.factory( Speck.FORGE ), 4 );
					}
				});
				Buff.detach(Dungeon.hero, TrollJump.class);
				Buff.detach(enemy, TrollJump.class);

				Buff.affect(Dungeon.hero, Talent.BoulderIsComingCooldown.class, 10f);
			}
		}
		
		@Override
		public String prompt() {
			return Messages.get(TrollJump.class, "prompt");
		}
	};
}
