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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.alchemist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Pistol;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class MountNLoad extends ArmorAbility {

	{
		baseChargeUse = 50f;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		Pistol pistol = hero.belongings.getItem(Pistol.class);
		if (pistol == null) {
			GLog.w(Messages.get(this, "need_pistol"));
			return;
		}

		hero.sprite.operate( hero.pos );
		CellEmitter.bottom( hero.pos ).start( EarthParticle.FACTORY, 0.05f, 8 );
		Camera.main.shake( 1, 0.4f );

		Buff.affect(hero, MountNLoadTracker.class);
		MountNLoadTracker tracker = hero.buff(MountNLoadTracker.class);

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		pistol.round = pistol.max_round;
		pistol.updateQuickslot();
		Sample.INSTANCE.play(Assets.Sounds.CHAINS);
		

	}

	public static class MountNLoadTracker extends Buff {

		@Override
		public int icon() {
			return BuffIndicator.SC_MOUNTNLOAD;
		}

		@Override
		public String toString() {
			return Messages.get(this, "name");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}

		private static final float STEP = 1f;
		private static final String POS	= "pos";
		private int pos;

		public void setPos() {
			target.pos = pos;
		}

		@Override
		public boolean act() {
			if (target.pos != pos) {
				detach();
			}
			return super.act();
		}

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( POS, pos );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			pos = bundle.getInt( POS );
		}
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.HAIL_OF_SHOTS, Talent.FMJ, Talent.TRENCH_WARFARE, Talent.HEROIC_ENERGY};
	}
}
