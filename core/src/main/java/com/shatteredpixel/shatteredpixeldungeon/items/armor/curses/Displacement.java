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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

public class Displacement extends Armor.Glyph {

	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage ) {

		if (defender == Dungeon.hero && Random.Int(20) == 0){
			if (hero.heroClass == HeroClass.HERETIC) {
				Buff.affect(hero, HereticDisplacementProc.class).set(armor);
				Sample.INSTANCE.play(Assets.Sounds.MELD);
			} else
				ScrollOfTeleportation.teleportHero(Dungeon.hero);


			return 0;
		}

		return damage;
	}

	public static class HereticDisplacementProc extends Buff {

		private static final float STEP = 1f;
		private int pos;
		private float pow;
		private int count;

		{
			type = buffType.POSITIVE;
			announced = true;
		}

		@Override
		public boolean attachTo( Char target ) {
			pos = target.pos;
			count = 1;
			return super.attachTo( target );
		}

		@Override
		public boolean act() {
			if (count > 0){
				count = 0;
				spend( TICK );
				return true;
			}

			if (target.pos == pos) {
				Buff.affect(hero, Invisibility.class, pow);
				ScrollOfTeleportation.teleportHero(Dungeon.hero);
			}
			detach();
			spend( STEP );
			return true;
		}

		public void set(Armor armor) {
			pos = target.pos;
			pow = 5f + Random.NormalFloat(armor.buffedLvl()*0.5f, armor.buffedLvl()*1.5f);
		}

		@Override
		public int icon() {
			return BuffIndicator.INVISIBLE;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(1f, 0, 0);
		}

		@Override
		public String toString() {
			return Messages.get(Displacement.class, "heretic_buff_name");
		}

		@Override
		public String desc() { return Messages.get(Displacement.class, "heretic_buff_desc", pow); }

		private static final String POS		= "pos";
		private static final String POW		= "pow";
		private static final String COUNT	= "count";
		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( POS, pos );
			bundle.put( POW, pow );
			bundle.put( COUNT, count );
		}
		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			pos = bundle.getInt( POS );
			pow = bundle.getFloat( POW );
			count = bundle.getInt( COUNT );
		}
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

	@Override
	public boolean curse() {
		return true;
	}
}
