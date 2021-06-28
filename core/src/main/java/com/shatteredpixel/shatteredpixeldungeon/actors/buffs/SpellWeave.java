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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.ElementalSpell;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSpellWeave;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SpellWeave extends Buff implements ActionIndicator.Action {

	public int count = 0;
	public float timeLeft = 0f;
	private float initialCountTime = 10f;

	public int clairvoyance_left;

	@Override
	public int icon() {
		return BuffIndicator.SC_ARCANE;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (initialCountTime - timeLeft)/ initialCountTime);
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	public String spellReady(){
		if (count == 2) return Messages.get(SpellWeave.class, "spell1");
		if (count == 4) return Messages.get(SpellWeave.class, "spell2");
		if (count == 6) return Messages.get(SpellWeave.class, "spell3");
		return null;
	}

	public void addCount( int time ){
		count = Math.min(count+time, 10);

		if (spellReady() != null){
			target.sprite.showStatus(0xEE99FF, (spellReady())
					+ " " + Messages.get(SpellWeave.class, "ready"));
		}
		ActionIndicator.updateIcon();
	}

	public void gainCount() {

		timeLeft = 10f;
		initialCountTime = timeLeft;

		ActionIndicator.setAction( this );
		BuffIndicator.refreshHero();
	}

	@Override
	public void detach() {
		super.detach();
		ActionIndicator.clearAction(this);
	}

	@Override
	public boolean act() {

		spend(TICK);

		if (timeLeft > 0 && count > 0)
			timeLeft -=TICK;

		if (timeLeft <= 0) {
			timeLeft = initialCountTime;
			count = Math.min(count--, 0);
			ActionIndicator.updateIcon();
		}
		BuffIndicator.refreshHero();

		return true;
	}

	@Override
	public String desc() {
		String desc = Messages.get(this, "desc", count, dispTurns(timeLeft));
		if (clairvoyance_left > 0)
			desc += "\n\n" + Messages.get(SpellWeave.class, "clairvoyance_left", clairvoyance_left);
		return desc;
	}

	private static final String COUNT = "count";
	private static final String TIME  = "timeleft";
	private static final String INITIAL_TIME  		= "initialComboTime";
	private static final String CLAIRVOYANCE_LEFT  	= "clairvoyance";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(COUNT, count);
		bundle.put(TIME, timeLeft);
		bundle.put(INITIAL_TIME, initialCountTime);
		bundle.put(CLAIRVOYANCE_LEFT, clairvoyance_left);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		count = bundle.getInt( COUNT );
		timeLeft = bundle.getFloat( TIME );
		initialCountTime = bundle.getFloat( INITIAL_TIME );
		clairvoyance_left = bundle.getInt( CLAIRVOYANCE_LEFT );

		if (getHighestSpell() != null) ActionIndicator.setAction(this);
	}

	@Override
	public Image getIcon() {
		if (getHighestSpell() == null){
			return new Image(Assets.Interfaces.BUFFS_LARGE, 48, 96, 16, 16);
		}
		switch ((getHighestSpell())){
			case TORRENT:
				return new Image(Assets.Interfaces.TALENT_ICONS, 224, 96, 16, 16);
			case BEND_TIME:
				return new Image(Assets.Interfaces.TALENT_ICONS, 240, 96, 16, 16);
			case CLAIRVOYANCE:
				return new Image(Assets.Interfaces.TALENT_ICONS, 256, 96, 16, 16);
			default:
				return new Image(Assets.Interfaces.BUFFS_LARGE, 48, 96, 16, 16);
		}
	}

	@Override
	public void doAction() {
		GameScene.show(new WndSpellWeave(this));
	}

	public enum ArcaneSpell {
		TORRENT	     (2),
		BEND_TIME    (4),
		CLAIRVOYANCE (6);

		public int stackReq;

		ArcaneSpell(int stackReq){
			this.stackReq = stackReq;
		}

		public String desc(int stackReq){
			return Messages.get(this, name()+"_desc");
		}
	}

	public ArcaneSpell getHighestSpell(){
		ArcaneSpell best = null;
		for (ArcaneSpell spell : ArcaneSpell.values()){
			if (count >= spell.stackReq){
				best = spell;
			}
		}
		return best;
	}

	public int getArcaneCount(){
		return count;
	}

	public boolean canCastSpell(ArcaneSpell spell){
		return spell.stackReq <= count;
	}

	public void castSpell(ArcaneSpell spell){

		Hero hero = Dungeon.hero;
		ElementalSpell.FireFocus fireFocus = hero.buff(ElementalSpell.FireFocus.class);
		ElementalSpell.IceFocus iceFocus = hero.buff(ElementalSpell.IceFocus.class);
		ElementalSpell.ElecFocus elecFocus = hero.buff(ElementalSpell.ElecFocus.class);

		if (spell == ArcaneSpell.TORRENT &&
				fireFocus == null && iceFocus == null && elecFocus == null) {
			GLog.w(Messages.get(SpellWeave.class, "no_focus"));
			return;
		}

		switch (spell){
			case TORRENT:
				GameScene.selectCell(torrent);
				if (count <= 0) timeLeft = initialCountTime;
				BuffIndicator.refreshHero();
				ActionIndicator.updateIcon();
				break;

			case BEND_TIME:
				Sample.INSTANCE.play(Assets.Sounds.HEALTH_WARN);
				GameScene.flash(0xFFFFFF);
				float dur = 4 + Math.min(2*((Hero)target).pointsInTalent(Talent.TRAVELER), 4);
				Buff.affect(target, Swiftthistle.TimeBubble.class).bendTime(dur);
				count -= 4;
				if (count <= 0) timeLeft = initialCountTime;
				BuffIndicator.refreshHero();
				ActionIndicator.updateIcon();
				break;

			case CLAIRVOYANCE:
				Sample.INSTANCE.play(Assets.Sounds.SCAN);
				target.sprite.operate(target.pos);
				target.sprite.parent.add( new Identification( target.sprite.center().offset( 0, -16 ) ) );
				GLog.p(Messages.get(SpellWeave.class, "clairvoyance_cast"));
				int pow = 2 + ((hero.pointsInTalent(Talent.CLAIRVOYANT) >= 1) ? 2 : 0)
							+ ((hero.pointsInTalent(Talent.CLAIRVOYANT) >= 2) ? 2 : 0);
				clairvoyance_left = Math.min(pow, 10);
				count -= 6;
				if (count <= 0) timeLeft = initialCountTime;
				BuffIndicator.refreshHero();
				ActionIndicator.updateIcon();
				break;
		}
	}

	public boolean checkBendTime() {
		Swiftthistle.TimeBubble bubble = target.buff(Swiftthistle.TimeBubble.class);
		if (bubble == null)
			return false;

		if (((Hero)target).pointsInTalent(Talent.TRAVELER) == 3 && bubble.bend_time){
			return true;
		} else
			return false;
	}

	public boolean checkClairvoyance() {
		if (clairvoyance_left > 0) {
			clairvoyance_left--;
			if (clairvoyance_left == 0){
				target.sprite.showStatus(0xEE99FF, Messages.get(SpellWeave.class, "clairvoyance_out"));
			}
			return true;
		} else {
			clairvoyance_left = 0;
			return false;
		}
	}

	private void castTorrent(int cell) {

		Hero hero = (Hero) target;

		ElementalSpell.ElementalSpellFire fire = hero.belongings.getItem(ElementalSpell.ElementalSpellFire.class);
		ElementalSpell.ElementalSpellIce ice = hero.belongings.getItem(ElementalSpell.ElementalSpellIce.class);
		ElementalSpell.ElementalSpellElec elec = hero.belongings.getItem(ElementalSpell.ElementalSpellElec.class);

		ElementalSpell.FireFocus fireFocus = hero.buff(ElementalSpell.FireFocus.class);
		ElementalSpell.IceFocus iceFocus = hero.buff(ElementalSpell.IceFocus.class);
		ElementalSpell.ElecFocus elecFocus = hero.buff(ElementalSpell.ElecFocus.class);

		PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ),
				hero.pointsInTalent(Talent.DEVASTATOR) == 3 ? 2 : 1 );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				Char ch = Actor.findChar(i);

				float factor = 1f;
				if (hero.hasTalent(Talent.DEVASTATOR)) {
					switch (hero.pointsInTalent(Talent.DEVASTATOR)) {
						case 1:
							factor = 1.5f; break;
						case 2: case 3:
							factor = 2f;   break;
					}
				}

				if (fireFocus != null) {
					Buff.detach(hero, ElementalSpell.FireFocus.class);
					Splash.at(i, 0xFFFFBB33, 10);

					if (ch != null) {
						Buff.affect(ch, Burning.class).reignite(ch);
						ch.damage((int)(fire.damageRoll()*factor), fire);
						if (!ch.isAlive() && Dungeon.hero.hasTalent(Talent.WILDFIRE)
								&& Random.Float() < 0.34f + 0.33f* Dungeon.hero.pointsInTalent(Talent.WILDFIRE)) {
							float extend = 3f + Dungeon.hero.pointsInTalent(Talent.WILDFIRE);
							Buff.affect(Dungeon.hero, ElementalSpell.FireFocus.class).set(Dungeon.hero, extend);
						}

					} else {
						GameScene.add(Blob.seed(i, 2, Fire.class));
					}

				}
				if (elecFocus != null) {
					Buff.detach(hero, ElementalSpell.ElecFocus.class);
					CellEmitter.center(i).burst(SparkParticle.FACTORY, 3);

					if (ch != null) {
						ch.sprite.flash();
						ch.damage((int) (elec.damageRoll()), elec);

						PathFinder.buildDistanceMap(ch.pos, BArray.not(Dungeon.level.solid, null), 2);
						for (int arc = 0; arc < PathFinder.distance.length; arc++) {
							Char affected = Actor.findChar(arc);
							if (affected != null) {
								int dist = Dungeon.level.distance(ch.pos, affected.pos);
								float spread = 0;
								boolean onWater = (Dungeon.level.water[affected.pos] && !affected.flying);


								if (affected != ch && (dist == 1 || (dist == 2 && onWater))) {
									spread++;

									float multipler = 0.4f + (0.6f / spread);
									if (onWater) multipler = 1f;
									if (Dungeon.hero.hasTalent(Talent.CHAIN_LIGHTNING)) {
										multipler = 1f;
										if (Dungeon.hero.pointsInTalent(Talent.CHAIN_LIGHTNING) == 2)
											multipler += (multipler * 0.1f) * spread;
									}

									if (Dungeon.level.heroFOV[affected.pos]) {
										affected.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
										affected.sprite.flash();
									}

									affected.damage((int) (ElementalSpell.ElementalSpellElec.damageRoll() * multipler),
											ElementalSpell.ElementalSpellElec.class);
								}
							}
						}
					}
				}
				if (iceFocus != null) {
					Buff.detach(hero, ElementalSpell.IceFocus.class);
					Splash.at(i, 0xFF8EE3FF, 10);

					Heap heap = Dungeon.level.heaps.get(i);
					if (heap != null) {
						heap.freeze();
					}
					Fire fireBlob = (Fire) Dungeon.level.blobs.get(Fire.class);
					if (fireBlob != null) {
						fireBlob.clear(i);
					}
					if (Dungeon.level.water[i] && Blob.volumeAt(i, Freezing.class) == 0) {
						GameScene.add(Blob.seed(i, 2, Freezing.class));
					}

					if (ch != null) {
						if (ch.buff(Frost.class) != null) {
							Buff.affect(ch, Frost.class, 2f);
						} else {
							Chill chill = ch.buff(Chill.class);
							if (ch == hero && hero.hasTalent(Talent.ICEMAIL)) {
								// do not damage herself
							} else ch.damage((int) (ice.damageRoll() * factor), ice);
							Buff.affect(ch, Chill.class, 3f);

							if (chill != null && chill.cooldown() >= 5f) {
								Buff.affect(ch, Frost.class, Frost.DURATION);
							}
						}
					}
				}
			}
		}
		Invisibility.dispel();
		ActionIndicator.clearAction(SpellWeave.this);
		hero.spendAndNext(Actor.TICK);
	}

	private CellSelector.Listener torrent = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer cell) {
			Hero hero = Dungeon.hero;
			ElementalSpell.FireFocus fireFocus = hero.buff(ElementalSpell.FireFocus.class);
			ElementalSpell.IceFocus iceFocus = hero.buff(ElementalSpell.IceFocus.class);
			ElementalSpell.ElecFocus elecFocus = hero.buff(ElementalSpell.ElecFocus.class);

			if (cell == null) return;
			if (!Dungeon.level.heroFOV[cell]) {
				GLog.w(Messages.get(SpellWeave.class, "no_sight"));
				return;
			}

			hero.busy();
			target.sprite.zap(cell, new Callback() {
				@Override
				public void call() {
					if (fireFocus != null){
						Sample.INSTANCE.play(Assets.Sounds.BURNING);
					} if (elecFocus != null){
						Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
					} if (iceFocus != null){
						Sample.INSTANCE.play(Assets.Sounds.SHATTER);
					}
					Camera.main.shake(2, 0.3f);
					castTorrent(cell);
				}
			});
			count -= 2;
		}

		@Override
		public String prompt() {
			return Messages.get(SpellWeave.class, "prompt");
		}
	};

	private ArrayList<Char> affected = new ArrayList<>();
	private ArrayList<Lightning.Arc> arcs = new ArrayList<>();

	private void arc( Char ch ) {
		int dist = (Dungeon.level.water[ch.pos] && !ch.flying) ? 2 : 1;
		ArrayList<Char> hitThisArc = new ArrayList<>();
		PathFinder.buildDistanceMap( ch.pos, BArray.not( Dungeon.level.solid, null ), dist );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE){
				Char n = Actor.findChar( i );
				if (n == Dungeon.hero && PathFinder.distance[i] > 1)
					//the hero is only zapped if they are adjacent
					continue;
				else if (n != null && !affected.contains( n )) {
					hitThisArc.add(n);
				}
			}
		}

		affected.addAll(hitThisArc);
		for (Char hit : hitThisArc){
			arcs.add(new Lightning.Arc(ch.sprite.center(), hit.sprite.center()));
			arc(hit);
		}
	}
}
