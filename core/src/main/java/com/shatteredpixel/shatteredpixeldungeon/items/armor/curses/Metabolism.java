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

package com.shatteredpixel.shatteredpixeldungeon.items.armor.curses;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor.Glyph;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite.Glowing;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

public class Metabolism extends Glyph {

	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	
	@Override
	public int proc( Armor armor, Char attacker, Char defender, int damage) {

		if (Random.Int( 6 ) == 0 && defender instanceof Hero) {

			//assumes using up 10% of starving, and healing of 1 hp per 10 turns;
			int healing = Math.min((int)Hunger.STARVING/100, defender.HT - defender.HP);

			if (healing > 0) {
				
				Hunger hunger = Buff.affect(defender, Hunger.class);
				
				if (!hunger.isStarving()) {
					
					hunger.affectHunger( healing * -10 );
					
					defender.HP = Math.min(defender.HT, defender.HP+=healing);
					defender.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
					defender.sprite.showStatus( CharSprite.POSITIVE, Integer.toString( healing ) );
				}
			}

		}
		
		return damage;
	}

	public static class HereticMetabolismProc extends Buff {

		{
			type = buffType.POSITIVE;
		}

		@Override
		public boolean act() {
			if (target.isAlive()) {
				spend( TICK );
				if (dmgBoost <= 0) {
					detach();
				}
				if (((Hero)target).belongings.armor == null
						|| ((Hero)target).belongings.armor.glyph == null
						|| !(((Hero)target).belongings.armor.glyph instanceof Metabolism)) {
					detach();
				}

			} else {
				detach();
			}

			return true;
		}

		@Override
		public int icon() {
			return BuffIndicator.HERB_HEALING;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(1f, 0, 0);
		}

		@Override
		public String toString() {
			return Messages.get(Metabolism.class, "heretic_buff_name");
		}

		@Override
		public String desc() {
			return Messages.get(Metabolism.class, "heretic_buff_desc", dmgBoost, pow);
		}

		public int dmgBoost = 0;
		public int pow = 0;

		public void set(int dmg, int armor){
			pow = 10 + (armor*5);
			dmgBoost = Math.min(dmgBoost+dmg, pow);
		}

		public void activate(Char ch){
			ch.HP = Math.min(target.HP+dmgBoost, target.HT);
			ch.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
			ch.sprite.showStatus( CharSprite.POSITIVE, Integer.toString( dmgBoost ) );

			dmgBoost = 0;
			pow = 0;
			detach();
		}

		private static final String BOOST = "boost";
		private static final String POW = "pow";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( BOOST, dmgBoost );
			bundle.put( POW, pow );
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			dmgBoost = bundle.getInt( BOOST );
			pow = bundle.getInt( POW );
		}
	}

	@Override
	public Glowing glowing() {
		return BLACK;
	}

	@Override
	public boolean curse() {
		return true;
	}
}
