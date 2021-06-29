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
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class Sublimation extends Buff {
	{
		type = buffType.POSITIVE;
	}

	private int charge = 0;
	private int partialCharge = 0;

	@Override
	public boolean act() {
		boolean pain = target.HP <= target.HT/5;
		if (target.HP > target.HT/2){
			detach();
		}
		partialCharge++;

		if (((Hero)target).pointsInTalent(Talent.SUBLIMATION) >= 2 && pain){
			if (partialCharge >= 4) charge++;
		} else {
			if (partialCharge >= 10) charge++;
		}

		if (charge >= 1 && target != null) {
			((Hero) target).belongings.charge(1f);
			for (Buff b : target.buffs()) {
				if (b instanceof Artifact.ArtifactBuff)
					((Artifact.ArtifactBuff) b).charge(((Hero) target), 4);
			}
			charge = 0;
			partialCharge = 0;
		}

		spend(TICK);
		return true;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.RECHARGING;
	}
	
	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(1f, 0, 0);
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}
	
	@Override
	public String desc() {
		if (target.HP <= target.HT/5)
			return Messages.get(this, "desc2");
		else
			return Messages.get(this, "desc1");
	}

	private static final String CHARGE = "charge";
	private static final String PARTIALCHARGE = "partialcharge";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( CHARGE , charge );
		bundle.put( PARTIALCHARGE , partialCharge );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		charge = bundle.getInt( CHARGE );
		partialCharge = bundle.getInt( PARTIALCHARGE );
	}
}
