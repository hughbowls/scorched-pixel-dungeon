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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

public class Fragile extends Weapon.Enchantment {

	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	private int hits = 0;

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		//degrades from 100% to 25% damage over 150 hits
		damage *= (1f - hits*0.005f);
		if (hits < 150) hits++;

		if (hero.heroClass == HeroClass.HERETIC){

			int enemyHealth = defender.HP - damage;
			if (enemyHealth <= 0) return damage; //no point in proccing if they're already dead.

			// BaseMax at 150 hits = 25%, +2% per lvl
			int chanceGrim = (int) (0.1f+(hits*0.166f)) + (int) (weapon.buffedLvl()*2f);
			if (Random.Int( 100 ) <= chanceGrim){
				defender.damage( defender.HP, Grim.class );
				defender.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
				hits = Math.max(0, hits-30);

			} else {
				// BaseMax at 150 hits = 40%, +2.5% per lvl
				int chanceDoom = (int) (1f+(hits*0.26f)) + (int) (weapon.buffedLvl()*2.5f);
				if (Random.Int( 100 ) <= chanceDoom){
					Buff.affect(defender, Doom.class);
					defender.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
				}
			}
		}
		return damage;
	}

	@Override
	public boolean curse() {
		return true;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

	private static final String HITS = "hits";

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		hits = bundle.getInt(HITS);
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put(HITS, hits);
	}

}
