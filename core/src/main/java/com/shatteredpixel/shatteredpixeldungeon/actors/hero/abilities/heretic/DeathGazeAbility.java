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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.heretic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DeathGazeAbility extends ArmorAbility {

	{
		baseChargeUse = 25f;
	}

	@Override
	public float chargeUse( Hero hero ) {
		float chargeUse = super.chargeUse(hero);
		if (hero.buff(SerialGazerTracker.class) != null){
			//reduced charge use by 33%/55%/70%/80%
			chargeUse *= Math.pow(0.67, hero.pointsInTalent(Talent.SERIAL_GAZER));
		}
		return chargeUse;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	public static void fx(Ballistica beam, Callback callback) {
		int cell = beam.path.get(Math.min(beam.dist, 8));
		Dungeon.hero.sprite.parent.add(
				new Beam.DeathRay(
				Dungeon.hero.sprite.center(),
				DungeonTilemap.raisedTileCenterToWorld( cell )));
		callback.call();
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		if (target == null){
			return;
		}
		if (target == hero.pos){
			GLog.w(Messages.get(this, "self_target"));
			return;
		}

		Buff.prolong(hero, GazeChargeTracker.class, 3f).setup(hero, target);
		hero.sprite.emitter().start(MagicMissile.MagicParticle.ATTRACTING, 0.05f, 20 );
		Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );

		hero.sprite.operate(hero.pos);

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();
		hero.spendAndNext(3f);

		hero.sprite.zap(target);
		if (hero.hasTalent(Talent.SERIAL_GAZER)) {
			Buff.affect(hero, SerialGazerTracker.class, 5);
		}
	}

	public static class GazeChargeTracker extends FlavourBuff {

		public boolean charging;
		public int gazeTarget;

		public void setup(Hero hero, int target){
			charging = true;
			gazeTarget = target;
		}

		public int adjustDamageTaken(int damage){
			if (charging && Dungeon.hero.hasTalent(Talent.HELLISH_RESISTANCE)) {
				return damage/(Dungeon.hero.pointsInTalent(Talent.HELLISH_RESISTANCE));
			}
			return damage;
		}

		public void endCharging(){
			if (!charging){
				return;
			}

			final Ballistica shot = new Ballistica( Dungeon.hero.pos, gazeTarget, Ballistica.IGNORE_SOFT_SOLID);
			DeathGazeAbility.fx(shot, new Callback() {
				public void call() {
					boolean terrainAffected = false;

					int maxDistance = Math.min(8, shot.dist);

					ArrayList<Char> chars = new ArrayList<>();

					for (int c : shot.subPath(1, maxDistance)) {

						Char ch;
						if ((ch = Actor.findChar( c )) != null) {
							chars.add( ch );
						}

						if (Dungeon.level.flamable[c]) {
							Dungeon.level.destroy( c );
							GameScene.updateMap( c );
							terrainAffected = true;
						}

						CellEmitter.center( c ).burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
					}

					if (terrainAffected) {
						Dungeon.observe();
					}

					int dmg = Random.NormalIntRange( 30, 50 );
					for (Char ch : chars) {
						ch.damage( dmg, this );

						if (ch.isAlive() && Dungeon.hero.hasTalent(Talent.AGONIZING_GAZE)){
							if (ch.buff(Slow.class) != null && Dungeon.hero.pointsInTalent(Talent.AGONIZING_GAZE) >= 3){
								Buff.detach(ch, Slow.class);
								Buff.affect(ch, Paralysis.class, 3f);
							} else Buff.affect(ch, Slow.class, 3*Dungeon.hero.pointsInTalent(Talent.AGONIZING_GAZE));
						}

						ch.sprite.centerEmitter().burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
						ch.sprite.flash();
						Sample.INSTANCE.play( Assets.Sounds.RAY );
					}
				}
			});

			charging = false;
			detach();
		}

		public static String CHARGING = "charging";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(CHARGING, charging);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			charging = bundle.getBoolean(CHARGING);
		}
	};

	public static class SerialGazerTracker extends FlavourBuff {};

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.HELLISH_RESISTANCE, Talent.AGONIZING_GAZE, Talent.SERIAL_GAZER, Talent.HEROIC_ENERGY};
	}
}
