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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ScrollOfIdentify extends InventoryScroll {

	{
		icon = ItemSpriteSheet.Icons.SCROLL_IDENTIFY;
		mode = WndBag.Mode.UNIDENTIFED;

		bones = true;
	}
	
	@Override
	protected void onItemSelected( Item item ) {
		
		curUser.sprite.parent.add( new Identification( curUser.sprite.center().offset( 0, -16 ) ) );
		
		item.identify();
		GLog.i( Messages.get(this, "it_is", item) );
		
		Badges.validateItemLevelAquired( item );

		if (curUser.hasTalent(Talent.PYTHONESS_INTUITION)){
			ArrayList<Item> unId = new ArrayList<>();
			for (Item i : curUser.belongings)
				if (!i.isIdentified()) unId.add(i);

			if (!unId.isEmpty()){
				if (curUser.pointsInTalent(Talent.PYTHONESS_INTUITION) == 1){
					Item toId = Random.element(unId);
					toId.identify();
					GLog.p(Messages.get(ScrollOfIdentify.class,
							"pythoness_it_is", toId));
					Badges.validateItemLevelAquired( item );
				} if (curUser.pointsInTalent(Talent.PYTHONESS_INTUITION) == 2) {
					GameScene.selectItem(pythoness_Selector, WndBag.Mode.UNIDENTIFED,
							Messages.get(ScrollOfIdentify.class, "inv_title"));
				}
			}
		}
	}

	private static WndBag.Listener pythoness_Selector = new WndBag.Listener() {
		@Override
		public void onSelect( Item item ) {
			if (item != null) {
				item.identify();
				GLog.p( Messages.get(ScrollOfIdentify.class, "pythoness_it_is", item) );
				Badges.validateItemLevelAquired( item );
			}
		}
	};
	
	@Override
	public int value() {
		return isKnown() ? 30 * quantity : super.value();
	}
}
