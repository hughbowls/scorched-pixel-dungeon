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

package com.shatteredpixel.shatteredpixeldungeon.items.armor;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class HereticArmor extends ClassArmor {
	
	{
		image = ItemSpriteSheet.ARMOR_HERETIC;
	}
	
	@Override
	public void doSpecial() {

		charge -= 35;
		updateQuickslot();

		int amt = (int) (0.5f * curUser.HP);
		curUser.HP -= amt;
		Buff.affect(curUser, Barrier.class).setShield(amt*2);
		
		curUser.spend( Actor.TICK );
		curUser.sprite.operate( curUser.pos );
		Invisibility.dispel();
		curUser.busy();
		
		curUser.sprite.emitter().start( ShadowParticle.CURSE, 0.025f, 20 );
		Sample.INSTANCE.play( Assets.Sounds.MELD );
	}

}