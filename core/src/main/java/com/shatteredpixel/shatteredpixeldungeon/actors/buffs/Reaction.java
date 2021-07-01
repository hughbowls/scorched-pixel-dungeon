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

public class Reaction extends Buff {

	{
		type = buffType.POSITIVE;
	}

	public boolean tap = false;

	public void setTap() {
		if (((Hero)target).hasTalent(Talent.DOUBLE_TAP))
			tap = true;
		else
			tap = false;
	}
	public void removeTap() { tap = false; }
	public boolean getTap() { return tap;  }

	@Override
	public int icon() { return BuffIndicator.MOMENTUM; }

	@Override
	public void tintIcon(Image icon) {
		if (tap) icon.hardlight(1f, 0f, 0f);
		else icon.hardlight(0x00C7C7);
	}

	@Override
	public String toString() {
		if (tap) return Messages.get(this, "name_tap");
		else return Messages.get(this, "name");
	}

	@Override
	public String desc() { return Messages.get(this, "desc"); }

	private static final String TAP = "tap";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( TAP, tap );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		tap = bundle.getBoolean( TAP );
	}
}
