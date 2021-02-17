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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.watabou.utils.Bundle;

public class TrollSkin extends ShieldBuff {
	
	{
		type = buffType.POSITIVE;
	}

	float partialLostShield;
	float gainedShield;

	@Override
	public void incShield(int amt) {
		super.incShield(amt);
		partialLostShield = 0;
		gainedShield += amt;
	}

	@Override
	public void setShield(int shield) {
		super.setShield(shield);
		gainedShield = 0;
		if (shielding() == shield) partialLostShield = 0;
		gainedShield = shield;
	}

	@Override
	public int absorbDamage( int dmg ){
		gainedShield = Math.max(0, gainedShield - dmg);
		return super.absorbDamage(dmg);
	}

	@Override
	public boolean act() {
		partialLostShield += Math.min(1f, shielding()/20f);

		if (partialLostShield >= 1f) {
			absorbDamage(1);
			partialLostShield = 0;

			if (target.buff(Talent.TrollRegeneration.class) == null
					&& target.HP < target.HT && target.shielding() >= 1
					&& Dungeon.hero.hasTalent(Talent.REGENERATION)
					&& gainedShield >= 1) {

				target.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
				// healing twice
				target.HP = Math.min(target.HP++, target.HT);
				target.HP = Math.min(target.HP++, target.HT);
			}
		}
		
		if (shielding() <= 0){
			gainedShield = 0;
			detach();
		}
		
		spend( TICK );
		
		return true;
	}

	private static final String PARTIAL_LOST_SHIELD = "partial_lost_shield";
	private static final String GAINED_SHIELD = "gained_shield";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PARTIAL_LOST_SHIELD, partialLostShield);
		bundle.put(GAINED_SHIELD, gainedShield);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		partialLostShield = bundle.getFloat(PARTIAL_LOST_SHIELD);
		gainedShield = bundle.getFloat(GAINED_SHIELD);
	}
}
