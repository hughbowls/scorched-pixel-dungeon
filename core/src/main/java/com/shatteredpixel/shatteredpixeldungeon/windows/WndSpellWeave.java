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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SpellWeave;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

public class WndSpellWeave extends Window {

	private static final int WIDTH_P = 120;
	private static final int WIDTH_L = 160;

	private static final int MARGIN  = 2;

	public WndSpellWeave(SpellWeave weave){
		super();

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		float pos = MARGIN;
		RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(this, "title")), 9);
		title.hardlight(TITLE_COLOR);
		title.setPos((width-title.width())/2, pos);
		title.maxWidth(width - MARGIN * 2);
		add(title);

		pos = title.bottom() + 3*MARGIN;

		Image ic;
		ic = new Image(Assets.Interfaces.BUFFS_LARGE, 48, 96, 16, 16);

		for (SpellWeave.ArcaneSpell spell : SpellWeave.ArcaneSpell.values()) {
			if (spell == SpellWeave.ArcaneSpell.TORRENT) {
				ic = new Image(Assets.Interfaces.TALENT_ICONS, 224, 96, 16, 16);
			} else if (spell == SpellWeave.ArcaneSpell.BEND_TIME) {
				ic = new Image(Assets.Interfaces.TALENT_ICONS, 240, 96, 16, 16);
			} else if (spell == SpellWeave.ArcaneSpell.CLAIRVOYANCE) {
				ic = new Image(Assets.Interfaces.TALENT_ICONS, 256, 96, 16, 16);
			}

			RedButton castBtn = new RedButton(spell.desc(weave.getArcaneCount()), 6){
				@Override
				protected void onClick() {
					super.onClick();
					hide();
					weave.castSpell(spell);
				}
			};
			castBtn.icon(ic);
			castBtn.leftJustify = true;
			castBtn.multiline = true;
			castBtn.setSize(width, castBtn.reqHeight());
			castBtn.setRect(0, pos, width, castBtn.reqHeight());
			castBtn.enable(weave.canCastSpell(spell));
			add(castBtn);
			pos = castBtn.bottom() + MARGIN;
		}

		resize(width, (int)pos);

	}


}
