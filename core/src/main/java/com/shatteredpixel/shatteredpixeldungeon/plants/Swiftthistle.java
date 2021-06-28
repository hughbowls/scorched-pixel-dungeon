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

package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SpellWeave;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class Swiftthistle extends Plant {
	
	{
		image = 2;
		seedClass = Seed.class;
	}
	
	@Override
	public void activate( Char ch ) {
		if (ch == Dungeon.hero) {
			Buff.affect(ch, TimeBubble.class).reset();
			if (Dungeon.hero.subClass == HeroSubClass.WARDEN){
				Buff.affect(ch, Haste.class, 1f);
			}
		}
	}
	
	public static class Seed extends Plant.Seed {
		{
			image = ItemSpriteSheet.SEED_SWIFTTHISTLE;
			
			plantClass = Swiftthistle.class;
		}
	}
	
	//FIXME lots of copypasta from time freeze here
	
	public static class TimeBubble extends Buff {

		{
			type = buffType.POSITIVE;
			announce_check = false; //Scorched
		}
		
		private float left;
		ArrayList<Integer> presses = new ArrayList<>();

		//Scorched
		public boolean announce_check = false;
		public boolean bend_time 	  = false;

		@Override
		public int icon() {
			return BuffIndicator.TIME;
		}

		@Override
		public void tintIcon(Image icon) {
			if (bend_time)
				icon.hardlight(0xA15CE5);
			else
				icon.hardlight(1f, 1f, 0);
		}

		@Override
		public float iconFadePercent() {
			if (bend_time) {
				float dur = 4f + ((Hero)target).pointsInTalent(Talent.TRAVELER) == 1 ? 2f : 0f
						+ ((Hero)target).pointsInTalent(Talent.TRAVELER) >= 2 ? 4f : 0f;
				return Math.max(0, (dur - left) / dur);
			}
			else
				return Math.max(0, (6f - left) / 6f);
		}
		
		public void reset(){
			if (!announce_check) {
				announce_check = true;
				target.sprite.showStatus(CharSprite.POSITIVE, toString());
			}
			left = 7f;
			if (bend_time) {
				bend_time = false;
				BuffIndicator.refreshHero();
			}
		}

		public void add(float pow){
			left += pow;
		}

		public void bendTime(float pow){
			if (!announce_check) {
				target.sprite.showStatus(CharSprite.POSITIVE,
						Messages.get(SpellWeave.class, "bend_time_buff_name"));
				announce_check = true;
			}
			left += pow;
			bend_time = true;
			BuffIndicator.refreshHero();
		}

		@Override
		public String toString() {
			if (bend_time){
				return Messages.get(SpellWeave.class, "bend_time_buff_name");
			}
			return Messages.get(this, "name");
		}
		
		@Override
		public String desc() {
			if (bend_time){
				if (((Hero)target).pointsInTalent(Talent.TRAVELER) == 3)
					return Messages.get(SpellWeave.class, "bend_time_buff_desc1", dispTurns(left));
				else
					return Messages.get(SpellWeave.class, "bend_time_buff_desc2", dispTurns(left));
			}
			return Messages.get(this, "desc", dispTurns(left));
		}
		
		public void processTime(float time){
			left -= time;

			//use 1/1,000 to account for rounding errors
			if (left < -0.001f){
				detach();
			}
			
		}
		
		public void setDelayedPress(int cell){
			if (!presses.contains(cell))
				presses.add(cell);
		}
		
		private void triggerPresses(){
			for (int cell : presses)
				Dungeon.level.pressCell(cell);
			
			presses = new ArrayList<>();
		}
		
		@Override
		public void detach(){
			super.detach();
			triggerPresses();
			target.next();
		}

		@Override
		public void fx(boolean on) {
			Emitter.freezeEmitters = on;
			if (on){
				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
					if (mob.sprite != null) mob.sprite.add(CharSprite.State.PARALYSED);
				}
			} else {
				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
					if (mob.paralysed <= 0) mob.sprite.remove(CharSprite.State.PARALYSED);
				}
			}
		}
		
		private static final String PRESSES = "presses";
		private static final String LEFT = "left";

		private static final String ANNOUNCED = "announce_check";
		private static final String BEND_TIME = "bend_time";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			
			int[] values = new int[presses.size()];
			for (int i = 0; i < values.length; i ++)
				values[i] = presses.get(i);
			bundle.put( PRESSES , values );
			
			bundle.put( LEFT, left );

			bundle.put( ANNOUNCED, announce_check);
			bundle.put( BEND_TIME, bend_time );
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			
			int[] values = bundle.getIntArray( PRESSES );
			for (int value : values)
				presses.add(value);
			
			left = bundle.getFloat(LEFT);

			announce_check = bundle.getBoolean(ANNOUNCED);
			bend_time = bundle.getBoolean(BEND_TIME);
		}
		
	}
}
