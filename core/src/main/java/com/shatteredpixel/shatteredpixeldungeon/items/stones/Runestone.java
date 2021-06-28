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

package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public abstract class Runestone extends Item {
	
	{
		stackable = true;
		defaultAction = AC_THROW;
	}
	
	@Override
	protected void onThrow(int cell) {
		if (Dungeon.level.pit[cell] || !defaultAction.equals(AC_THROW)){
			super.onThrow( cell );
		} else {
			Dungeon.level.pressCell( cell );
			activate(cell);
			Invisibility.dispel();
		}
	}
	
	protected abstract void activate(int cell);
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public int value() {
		return 10 * quantity;
	}
	
	public static class PlaceHolder extends Runestone {
		
		{
			image = ItemSpriteSheet.STONE_HOLDER;
		}
		
		@Override
		protected void activate(int cell) {
			//does nothing
		}
		
		@Override
		public boolean isSimilar(Item item) {
			return item instanceof Runestone;
		}
		
		@Override
		public String info() {
			return "";
		}
	}

	public static final String AC_CURSE	= "CURSE";

	@Override
	public ArrayList<String> actions(Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (hero.heroClass == HeroClass.HERETIC
			&& hero.hasTalent(Talent.RUNE_OF_CURSE)) {
			actions.add( AC_CURSE );
		}
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals(AC_CURSE)) {
			doCurse();
			curItem.detach( Dungeon.hero.belongings.backpack );
		}
	}

	public void doCurse() {
		GameScene.selectItem( itemSelector, WndBag.Mode.ENCHANTABLE, Messages.get(Runestone.class, "inv_title"));
	}

	protected WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect(final Item item) {

			if (!item.isEquipped(curUser)){
				GLog.w(Messages.get(Runestone.class, "need_equip"));
				return;
			}

			if (curUser.pointsInTalent(Talent.RUNE_OF_CURSE) == 2)
			{
				if (item instanceof Weapon){

					final Weapon.Enchantment enchants[] = new Weapon.Enchantment[5];
					Class<? extends Weapon.Enchantment> existing
							= ((Weapon) item).enchantment != null ? ((Weapon) item).enchantment.getClass() : null;
					enchants[0] = Weapon.Enchantment.randomCurse( existing );
					enchants[1] = Weapon.Enchantment.randomCurse( existing, enchants[0].getClass() );
					enchants[2] = Weapon.Enchantment.randomCurse( existing, enchants[0].getClass(), enchants[1].getClass() );
					enchants[3] = Weapon.Enchantment.randomCurse( existing, enchants[0].getClass(), enchants[1].getClass(),
																			enchants[2].getClass() );
					enchants[4] = Weapon.Enchantment.randomCurse( existing, enchants[0].getClass(), enchants[1].getClass(),
																			enchants[2].getClass(), enchants[3].getClass() );

					GameScene.show(new WndOptions(new Image(Assets.Interfaces.TALENT_ICONS, 128, 64, 16, 16),
							Messages.titleCase(Messages.get(Runestone.class, "name")),
							Messages.get(Runestone.class, "weapon") +
									"\n\n" +
									Messages.get(Runestone.class, "cancel_warn"),
							enchants[0].name(),
							enchants[1].name(),
							enchants[2].name(),
							enchants[3].name(),
							enchants[4].name(),
							Messages.get(Runestone.class, "cancel")){

						@Override
						protected void onSelect(int index) {
							if (index < 5) {
								((Weapon) item).enchant(enchants[index]);
								GLog.p(Messages.get(Runestone.class, "result"));
								Sample.INSTANCE.play( Assets.Sounds.MELD );
							}
						}

						@Override
						public void onBackPressed() { }
					});

				}

				else if (item instanceof Armor) {

					final Armor.Glyph glyphs[] = new Armor.Glyph[5];

					Class<? extends Armor.Glyph> existing = ((Armor) item).glyph != null ? ((Armor) item).glyph.getClass() : null;
					glyphs[0] = Armor.Glyph.randomCurse( existing );
					glyphs[1] = Armor.Glyph.randomCurse( existing, glyphs[0].getClass() );
					glyphs[2] = Armor.Glyph.randomCurse( existing, glyphs[0].getClass(), glyphs[1].getClass());
					glyphs[3] = Armor.Glyph.randomCurse( existing, glyphs[0].getClass(), glyphs[1].getClass(),
																   glyphs[2].getClass());
					glyphs[4] = Armor.Glyph.randomCurse( existing, glyphs[0].getClass(), glyphs[1].getClass(),
																   glyphs[2].getClass(), glyphs[3].getClass());

					GameScene.show(new WndOptions(new Image(Assets.Interfaces.TALENT_ICONS, 128, 64, 16, 16),
							Messages.titleCase(Messages.get(Runestone.class, "name")),
							Messages.get(Runestone.class, "armor") +
									"\n\n" +
									Messages.get(Runestone.class, "cancel_warn"),
							glyphs[0].name(),
							glyphs[1].name(),
							glyphs[2].name(),
							glyphs[3].name(),
							glyphs[4].name(),
							Messages.get(Runestone.class, "cancel")){

						@Override
						protected void onSelect(int index) {
							if (index < 5) {
								((Armor) item).inscribe(glyphs[index]);
								GLog.p(Messages.get(Runestone.class, "result"));
								Sample.INSTANCE.play( Assets.Sounds.MELD );
							}
						}

						@Override
						public void onBackPressed() { }
					});
				}

				else curItem.detach( Dungeon.hero.belongings.backpack );

			}
			else {
				if (item instanceof Weapon){

					final Weapon.Enchantment enchants[] = new Weapon.Enchantment[3];
					Class<? extends Weapon.Enchantment> existing
							= ((Weapon) item).enchantment != null ? ((Weapon) item).enchantment.getClass() : null;
					enchants[0] = Weapon.Enchantment.randomCurse( existing );
					enchants[1] = Weapon.Enchantment.randomCurse( existing, enchants[0].getClass() );
					enchants[2] = Weapon.Enchantment.randomCurse( existing, enchants[0].getClass(), enchants[1].getClass());

					GameScene.show(new WndOptions(new Image(Assets.Interfaces.TALENT_ICONS, 128, 64, 16, 16),
							Messages.titleCase(Messages.get(Runestone.class, "name")),
							Messages.get(Runestone.class, "weapon") +
									"\n\n" +
									Messages.get(Runestone.class, "cancel_warn"),
							enchants[0].name(),
							enchants[1].name(),
							enchants[2].name(),
							Messages.get(Runestone.class, "cancel")){

						@Override
						protected void onSelect(int index) {
							if (index < 3) {
								((Weapon) item).enchant(enchants[index]);
								GLog.p(Messages.get(Runestone.class, "result"));
								Sample.INSTANCE.play( Assets.Sounds.MELD );
							}
						}

						@Override
						public void onBackPressed() { }
					});

				}

				else if (item instanceof Armor) {

					final Armor.Glyph glyphs[] = new Armor.Glyph[3];

					Class<? extends Armor.Glyph> existing = ((Armor) item).glyph != null ? ((Armor) item).glyph.getClass() : null;
					glyphs[0] = Armor.Glyph.randomCurse( existing );
					glyphs[1] = Armor.Glyph.randomCurse( existing, glyphs[0].getClass() );
					glyphs[2] = Armor.Glyph.randomCurse( existing, glyphs[0].getClass(), glyphs[1].getClass());

					GameScene.show(new WndOptions(new Image(Assets.Interfaces.TALENT_ICONS, 128, 64, 16, 16),
							Messages.titleCase(Messages.get(Runestone.class, "name")),
							Messages.get(Runestone.class, "armor") +
									"\n\n" +
									Messages.get(Runestone.class, "cancel_warn"),
							glyphs[0].name(),
							glyphs[1].name(),
							glyphs[2].name(),
							Messages.get(Runestone.class, "cancel")){

						@Override
						protected void onSelect(int index) {
							if (index < 3) {
								((Armor) item).inscribe(glyphs[index]);
								GLog.p(Messages.get(Runestone.class, "result"));
								Sample.INSTANCE.play( Assets.Sounds.MELD );
							}
						}

						@Override
						public void onBackPressed() { }
					});
				}

				else curItem.detach( Dungeon.hero.belongings.backpack );
			}
		}
	};
}
