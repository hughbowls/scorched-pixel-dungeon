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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ElementalArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.PathFinder;

public class GarmentChange extends FlavourBuff {
	{
		type = buffType.POSITIVE;
	}

	@Override
	public void detach() {
		super.detach();
		if (target instanceof Hero && target.isAlive()) {
			((ElementalArmor)(((Hero)target).belongings.armor)).doChange(null, (((Hero)target).belongings.armor));

			Fire fire = (Fire)Dungeon.level.blobs.get(Fire.class);
			Freezing freezing = (Freezing)Dungeon.level.blobs.get(Freezing.class);
			Electricity electricity = (Electricity)Dungeon.level.blobs.get(Electricity.class);
			for (int i : PathFinder.NEIGHBOURS9) {
				if (((ElementalArmor)(((Hero)target).belongings.armor))
						instanceof ElementalArmor.ElementalArmorFire){
					if (Blob.volumeAt(i, Fire.class) > 0) fire.clear(i);
				}
				if (((ElementalArmor)(((Hero)target).belongings.armor))
						instanceof ElementalArmor.ElementalArmorIce){
					if (Blob.volumeAt(i, Freezing.class) > 0) freezing.clear(i);
				}
				if (((ElementalArmor)(((Hero)target).belongings.armor))
						instanceof ElementalArmor.ElementalArmorElec){
					if (Blob.volumeAt(i, Electricity.class) > 0) electricity.clear(i);
				}
			}

			Buff.affect(target, GarmentCooldown.class, 80f);
			GLog.w(Messages.get(ElementalArmor.class, "change_msg_end"));
		}
	}

	@Override
	public int icon() {
		return BuffIndicator.SC_FORM;
	}

	@Override
	public void tintIcon(Image icon) {
		if (Dungeon.hero.belongings.armor instanceof ElementalArmor.ElementalArmorFire)
			icon.hardlight(1f, 0.8f, 0f);
		else if (Dungeon.hero.belongings.armor instanceof ElementalArmor.ElementalArmorIce)
			icon.hardlight(0f, 0.8f, 0.8f);
		else if (Dungeon.hero.belongings.armor instanceof ElementalArmor.ElementalArmorElec)
			icon.hardlight(1f, 1f, 0f);
		else icon.resetColor();
	}

	@Override
	public String toString() {
		return Messages.get(ElementalArmor.class, "change_name");
	}

	@Override
	public String desc() {
		String desc = Messages.get(ElementalArmor.class, "change_desc");

		if (Dungeon.hero.belongings.armor instanceof ElementalArmor.ElementalArmorFire)
			desc += "\n\n" + Messages.get(ElementalArmor.class, "change_fire", dispTurns());
		if (Dungeon.hero.belongings.armor instanceof ElementalArmor.ElementalArmorIce)
			desc += "\n\n" + Messages.get(ElementalArmor.class, "change_ice", dispTurns());
		if (Dungeon.hero.belongings.armor instanceof ElementalArmor.ElementalArmorElec)
			desc += "\n\n" + Messages.get(ElementalArmor.class, "change_elec", dispTurns());

		return desc;
	}

}

