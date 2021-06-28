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

package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.items.food.StewedMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.AlchemicalCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class InnovationSpell extends InventorySpell {
	
	{
		image = ItemSpriteSheet.INNOVATION;
		mode = WndBag.Mode.INNOVATIONABLE;

		bones = false;
	}

	private static final ItemSprite.Glowing GLOW = new ItemSprite.Glowing( 0xFFFFFF );

	@Override
	public ItemSprite.Glowing glowing() { return GLOW; }

	public static boolean isInnovationable( Item item ){
		if (item.isIdentified()) {
			if (item instanceof MeleeWeapon
					|| item instanceof Armor
					|| item instanceof Ring) {
				return true;
			} else return false;
		}
		return false;
	}

	public static class InnovationCounter extends CounterBuff {};

	@Override
	protected void onItemSelected(Item item) {

		curUser.sprite.emitter().start( Speck.factory( Speck.UP ), 0.2f, 3 );
		Sample.INSTANCE.play(Assets.Sounds.READ);

		if (item instanceof MeleeWeapon) {
			Weapon w = (Weapon) item;
			w.setInnovation(3, 50);
			//Of course he can't use staff
			if (w instanceof MagesStaff){
				((MagesStaff) w).updateWand(true);
			}
		} else if (item instanceof Armor){
			Armor a = (Armor) item;
			a.setInnovation(3, 50);
		} else if (item instanceof Ring){
			Ring r = (Ring) item;
			r.setInnovation(3, 2);
			if (item instanceof RingOfMight){
				curUser.updateHT(false);
			}
		}

		updateQuickslot();
	}
	
	@Override
	public int value() {
		return 0;
	}

	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe {

		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			if (Dungeon.hero.subClass != HeroSubClass.INNOVATOR) return false;
			if (ingredients.size() != 1) return false;
			if (ingredients.get(0) instanceof AlchemicalCatalyst) return true;
			if (ingredients.get(0) instanceof ArcaneCatalyst) return true;
			return false;
		}

		@Override
		public int cost(ArrayList<Item> ingredients) {
			InnovationSpell.InnovationCounter i = Dungeon.hero.buff(InnovationSpell.InnovationCounter.class);
			if (i != null) {
				return Math.min(5 + (int)(i.count()*5), 75);
			} else return 5;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;

			for (Item ingredient : ingredients){
				ingredient.quantity(ingredient.quantity() - 1);
			}

			InnovationSpell.InnovationCounter i = Dungeon.hero.buff(InnovationSpell.InnovationCounter.class);
			if (i != null) {
				i.countUp(1);
			} else Buff.count(Dungeon.hero, InnovationSpell.InnovationCounter.class, 1);

			return sampleOutput(null);
		}

		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			return new InnovationSpell();
		}
	}
}
