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

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class TrollAnvil extends Buff {

	{
		type = buffType.POSITIVE;
	}

	@Override
	public boolean act() {
		if (target.isAlive()) {
			spend( TICK );
			left--;
			if (left <= 0){
				left = 3;
				drBoost--;
				if (drBoost <= 0)
					detach();
			}
		} else {
			detach();
		}
		return true;
	}

	@Override
	public int icon() {
		return BuffIndicator.SC_ANVIL;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (3-left) / 3f);
	}

	@Override
	public void tintIcon(Image icon) {
		if (drBoost >= 10){
			icon.hardlight(1f, 0f, 0f);
		} else if (drBoost >= 5) {
			icon.hardlight(1f, 1f - drBoost*.05f, 0f);
		} else {
			icon.hardlight(1f, 1f, 1f - drBoost*.1f);
		}
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", drBoost, 0.4f*drBoost, left);
	}

	public int drBoost;
	public int left;

	public void set(int shots){
		left = Math.max(left, shots);
		if (drBoost <= 0)
			drBoost++;
	}

	public void hit(){
			drBoost++;
		if (((Hero)target).hasTalent(Talent.ARCANESMITH)
			&& ((Hero)target).belongings.armor != null
			&& ((Hero)target).belongings.armor.hasGoodGlyph()){
			drBoost++;
		}
		left = 3;
	}

	public int getDrBoost(){
		return drBoost;
	}

	private static final String BOOST = "drBoost";
	private static final String LEFT = "left";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( BOOST, drBoost );
		bundle.put( LEFT, left );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		drBoost = bundle.getInt( BOOST );
		left = bundle.getInt( LEFT );
	}

}