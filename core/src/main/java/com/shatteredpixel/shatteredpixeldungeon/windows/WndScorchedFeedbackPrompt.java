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

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ScorchedFeedbackScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.utils.DeviceCompat;

public class WndScorchedFeedbackPrompt extends Window {

	protected static final int WIDTH_P    = 120;
	protected static final int WIDTH_L    = 200;

	public WndScorchedFeedbackPrompt(){

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		IconTitle title = new IconTitle(Icons.get(Icons.HERETIC), Messages.get(ScorchedFeedbackScene.class, "title"));
		title.setRect( 0, 0, width, 0 );
		add(title);

		String message = Messages.get(ScorchedFeedbackScene.class, "intro");
		message += "\n\n" + Messages.get(ScorchedFeedbackScene.class, "feedback_msg");
		if (Messages.lang() != Languages.KOREAN) {
			message += "\n" + Messages.get(ScorchedFeedbackScene.class, "feedback_lang");
		}

		RenderedTextBlock text = PixelScene.renderTextBlock( 6 );
		text.text( message, width );
		text.setPos( title.left(), title.bottom() + 4 );
		add( text );

		RedButton link = new RedButton(Messages.get(ScorchedFeedbackScene.class, "feedback_link")){
			@Override
			protected void onClick() {
				super.onClick();
				String link = "https://github.com/dreamdream2020/scorched-pixel-dungeon/issues";
				DeviceCompat.openURI(link);
				SPDSettings.scorchedfeedbackNagged(true);
				WndScorchedFeedbackPrompt.super.hide();
			}
		};
		link.setRect(0, text.bottom() + 4, width, 18);
		add(link);

		RedButton close = new RedButton(Messages.get(ScorchedFeedbackScene.class, "close")){
			@Override
			protected void onClick() {
				super.onClick();
				SPDSettings.scorchedfeedbackNagged(true);
				WndScorchedFeedbackPrompt.super.hide();
			}
		};
		close.setRect(0, link.bottom() + 2, width, 18);
		add(close);

		resize(width, (int)close.bottom());

	}

	@Override
	public void hide() {
		//do nothing, have to close via the close button
	}
}
