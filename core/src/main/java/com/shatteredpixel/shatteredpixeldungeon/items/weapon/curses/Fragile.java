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

			// BaseMax at 150 hits = 10%, +1% per lvl, activates only after 50 hits+
			// Limited at 25%
			int chanceGrim = Math.max(0, (hits + (int) (weapon.buffedLvl()*1.5f) - 50));
			float res = defender.resist(Grim.class);
			if (chanceGrim > (int) (250*1.5)) chanceGrim = 250;
			if (Random.Int( 1500 ) <= chanceGrim
				&& !(defender.isImmune(Grim.class)
					|| defender.isInvulnerable(attacker.getClass()))){
				defender.damage( defender.HP * (int) (res), Grim.class );
				defender.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
				hits = Math.max(0, hits-50);

			} else {
				// BaseMax at 150 hits = 20%, +2% per lvl
				// Limited at 66%
				int chanceDoom = Math.max(0, (hits + (int) (weapon.buffedLvl()*2f)));
				if (chanceDoom > 660) chanceDoom = 660;
				if (Random.Int( 1000 ) <= chanceDoom){
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
