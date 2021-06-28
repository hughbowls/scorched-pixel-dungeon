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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Anvil;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

public class WndAnvil extends Window {

	private static final int BTN_SIZE	= 36;
	private static final float GAP		= 2;
	private static final float BTN_GAP	= 10;
	private static final int WIDTH		= 116;

	private ItemButton btnPressed;

	private ItemButton btnItem1;
	private ItemButton btnItem2;
	private RedButton btnReforge;

	public WndAnvil( Hero hero ) {
		
		super();
		
		IconTitle titlebar = new IconTitle();
		titlebar.icon( new ItemSprite(ItemSpriteSheet.TROLL_ANVIL) );
		titlebar.label( Messages.get(Anvil.class, "name") );
		titlebar.setRect( 0, 0, WIDTH, 0 );
		add( titlebar );
		
		RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(Anvil.class, "prompt"), 6 );
		message.maxWidth( WIDTH);
		message.setPos(0, titlebar.bottom() + GAP);
		add( message );
		
		btnItem1 = new ItemButton() {
			@Override
			protected void onClick() {
				btnPressed = btnItem1;
				GameScene.selectItem( itemSelector, WndBag.Mode.UPGRADEABLE, Messages.get(Anvil.class, "select") );
			}
		};
		btnItem1.setRect( (WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
		add( btnItem1 );
		
		btnItem2 = new ItemButton() {
			@Override
			protected void onClick() {
				btnPressed = btnItem2;
				GameScene.selectItem( itemSelector, WndBag.Mode.UPGRADEABLE, Messages.get(Anvil.class, "select") );
			}
		};
		btnItem2.setRect( btnItem1.right() + BTN_GAP, btnItem1.top(), BTN_SIZE, BTN_SIZE );
		add( btnItem2 );
		
		btnReforge = new RedButton( Messages.get(Anvil.class, "reforge") ) {
			@Override
			protected void onClick() {
				upgrade( btnItem1.item, btnItem2.item );
				hide();
			}
		};
		btnReforge.enable( false );
		btnReforge.setRect( 0, btnItem1.bottom() + BTN_GAP, WIDTH, 20 );
		add( btnReforge );
		
		
		resize( WIDTH, (int)btnReforge.bottom() );
	}
	
	protected WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect( Item item ) {
			if (item != null && btnPressed.parent != null) {
				btnPressed.item( item );
				
				if (btnItem1.item != null && btnItem2.item != null) {
					String result = verify( btnItem1.item, btnItem2.item );
					if (result != null) {
						GameScene.show( new WndMessage( result ) );
						btnReforge.enable( false );
					} else {
						btnReforge.enable( true );
					}
				}
			}
		}
	};
	
	public static class ItemButton extends Component {
		
		protected NinePatch bg;
		protected ItemSlot slot;
		
		public Item item = null;
		
		@Override
		protected void createChildren() {
			super.createChildren();
			
			bg = Chrome.get( Chrome.Type.RED_BUTTON);
			add( bg );
			
			slot = new ItemSlot() {
				@Override
				protected void onPointerDown() {
					bg.brightness( 1.2f );
					Sample.INSTANCE.play( Assets.Sounds.CLICK );
				}
				@Override
				protected void onPointerUp() {
					bg.resetColor();
				}
				@Override
				protected void onClick() {
					ItemButton.this.onClick();
				}
			};
			slot.enable(true);
			add( slot );
		}
		
		protected void onClick() {}
		
		@Override
		protected void layout() {
			super.layout();
			
			bg.x = x;
			bg.y = y;
			bg.size( width, height );
			
			slot.setRect( x + 2, y + 2, width - 4, height - 4 );
		}
		
		public void item( Item item ) {
			slot.item( this.item = item );
		}
	}

	public static String verify( Item item1, Item item2 ) {

		if (item1 == item2 && (item1.quantity() == 1 && item2.quantity() == 1)) {
			return Messages.get(Anvil.class, "same_item");
		}

		if (item1.getClass() != item2.getClass()) {
			return Messages.get(Anvil.class, "diff_type");
		}

		if (!item1.isIdentified() || !item2.isIdentified()) {
			return Messages.get(Anvil.class, "un_ided");
		}

		if (item1.cursed || item2.cursed) {
			return Messages.get(Anvil.class, "cursed");
		}

		if (item1.level() < 0 || item2.level() < 0) {
			return Messages.get(Anvil.class, "degraded");
		}

		if (!item1.isUpgradable() || !item2.isUpgradable()) {
			return Messages.get(Anvil.class, "cant_reforge");
		}

		return null;
	}

	public static void upgrade( Item item1, Item item2 ) {

		Item first, second;
		if (item2.level() > item1.level()) {
			first = item2;
			second = item1;
		} else {
			first = item1;
			second = item2;
		}

		Sample.INSTANCE.play( Assets.Sounds.EVOKE );
		ScrollOfUpgrade.upgrade( Dungeon.hero );
		Item.evoke( Dungeon.hero );

		if (second.isEquipped( Dungeon.hero )) {
			((EquipableItem)second).doUnequip( Dungeon.hero, false );
		}
		second.detach( Dungeon.hero.belongings.backpack );

		if (second instanceof Armor){
			BrokenSeal seal = ((Armor) second).checkSeal();
			if (seal != null){
				Dungeon.level.drop( seal, Dungeon.hero.pos );
			}
		}

		if (first.isEquipped( Dungeon.hero )) {
			((EquipableItem)first).doUnequip( Dungeon.hero, true );
		}

		//preserves enchant/glyphs if present
		if (first instanceof Weapon && ((Weapon) first).hasGoodEnchant()){
			((Weapon) first).upgrade(true);
		} else if (first instanceof Armor && ((Armor) first).hasGoodGlyph()){
			((Armor) first).upgrade(true);
		} else {
			first.upgrade();
		}

		//Anvil anvil = null;
		//for (Item item : Dungeon.hero.belongings){
		//	if (item instanceof Anvil) {
		//		anvil = (Anvil) item;
		//		anvil.detach(Dungeon.hero.belongings.backpack);
		//	}
		//}

		Talent.UpgradeMasterTracker tracker = Dungeon.hero.buff(Talent.UpgradeMasterTracker.class);
		if (tracker != null) {
			tracker.detach();
		}

		if (!Dungeon.hero.belongings.contains(first)) {
			if (!first.collect()){
				Dungeon.level.drop( first, Dungeon.hero.pos );
			}
		}
		Dungeon.hero.spendAndNext( 2f );
		Badges.validateItemLevelAquired( first );
		Item.updateQuickslot();
	}
}
