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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class Sublimation extends Buff {
	{
		type = buffType.POSITIVE;
	}

	@Override
	public boolean act() {
		if (target.HP > target.HT/2){
			detach();
		}

		boolean pain = target.HP <= target.HT/5;

		float WandCharge = pain && Dungeon.hero.pointsInTalent(Talent.SUBLIMATION) == 2 ? 0.666f : 0.25f;
		float ArtifactCharge = pain && Dungeon.hero.pointsInTalent(Talent.SUBLIMATION) == 2 ? 0.333f : 0.1f;

		for (Buff b : target.buffs()) {
			((Hero) target).belongings.charge(WandCharge);
			if (b instanceof Artifact.ArtifactBuff) {
				((Artifact.ArtifactBuff) b).charge((Hero) target, ArtifactCharge);
			}
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
}
