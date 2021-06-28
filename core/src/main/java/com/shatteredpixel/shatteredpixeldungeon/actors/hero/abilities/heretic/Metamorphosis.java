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
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MetamorphosisBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import static com.shatteredpixel.shatteredpixeldungeon.actors.Char.hit;

public class Metamorphosis extends ArmorAbility {

	{
		baseChargeUse = 35f;
	}

	@Override
	public String targetingPrompt() {
		if (Dungeon.hero.hasTalent(Talent.RIPPING_CLAW)
				&& Dungeon.hero.buff(MetamorphosisBuff.class) != null)
			return Messages.get(this, "prompt");
		else
			return null;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {

		if (hero.buff(MetamorphosisBuff.class) != null) {
			if (hero.hasTalent(Talent.RIPPING_CLAW)) {
				if (target != null) {
					final Char enemy = Actor.findChar(target);
					if (enemy == null || Dungeon.hero.isCharmedBy(enemy)
							|| enemy instanceof NPC || !Dungeon.level.heroFOV[target]) {
						GLog.w(Messages.get(this, "no_target"));
						return;

					} if (Dungeon.level.distance(hero.pos, target)
							> 2 * hero.pointsInTalent(Talent.RIPPING_CLAW)) {
						GLog.w(Messages.get(this, "too_far"));
						return;

					}

					Ballistica route = new Ballistica(hero.pos, target,
							Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
					int cell = route.collisionPos;

					//can't occupy the same cell as another char, so move back one.
					int backTrace = route.dist - 1;
					while (Actor.findChar(cell) != null && cell != hero.pos) {
						cell = route.path.get(backTrace);
						backTrace--;
					}

					final int dest = cell;
					if (Dungeon.level.pit[cell] && !hero.flying) {
						GLog.w(Messages.get(this, "will_fall"));
						return;
					}

					hero.busy();
					hero.sprite.jump(hero.pos, cell, new Callback() {
						@Override
						public void call() {
							hero.move(dest);
							Dungeon.level.occupyCell(hero);
							Dungeon.observe();
							GameScene.updateFog();

							Invisibility.dispel();
							hero.spendAndNext(Actor.TICK);

							if (hero.pointsInTalent(Talent.RIPPING_CLAW) >= 2) {
								//Same amount as Ripper Demon
								Buff.affect(enemy, Bleeding.class).set(0.75f*Random.NormalIntRange( 15, 25 ));
							}

							if (hero.pointsInTalent(Talent.RIPPING_CLAW) >= 3) {
								Buff.prolong(hero, RippingClawTracker.class, 1f);
							}
						}});
				}
			} else {
				GLog.w(Messages.get(this, "already"));
				return;
			}
		} else {
			armor.charge -= chargeUse(hero);
			armor.updateQuickslot();
			Buff.affect(hero, MetamorphosisBuff.class).set(20f, hero.belongings.armor.tier);
			Sample.INSTANCE.play(Assets.Sounds.BADGE);
			GameScene.flash(0xFF0000);

			hero.sprite.operate(hero.pos);
			Invisibility.dispel();
			hero.next();
		}
	}

	public static class RippingClawTracker extends FlavourBuff {};

	public static class CripplingStingShot extends Item {
		{
			image = ItemSpriteSheet.FISHING_SPEAR;
		}
	}

	public static void CripplingStingFire(int pos) {
		Char target = Actor.findChar(pos);
		Actor.add(new Actor() {

			{
				actPriority = VFX_PRIO;
			}

			@Override
			protected boolean act() {
				final Actor toRemove = this;
				((MissileSprite) ShatteredPixelDungeon.scene().recycle(MissileSprite.class)).
						reset(Dungeon.hero.pos, target.sprite, new CripplingStingShot(), new Callback() {
							@Override
							public void call() {
								if (hit( Dungeon.hero, target, false )) {
									int factor = Dungeon.hero.pointsInTalent(Talent.CRIPPLING_STING);
									int dmg = Random.NormalIntRange(6+(3*factor), 10+(5*factor))-target.drRoll();
									target.damage(dmg, Dungeon.hero);

									if (factor >= 2) {
										if (Random.Int( 5-factor ) == 0) {
											Buff.prolong( target, Cripple.class, Cripple.DURATION );
										}
									}

									Sample.INSTANCE.play(Assets.Sounds.HIT, 1, 1, Random.Float(0.8f, 1.25f));
									target.sprite.bloodBurstA(target.sprite.center(), dmg);
									target.sprite.flash();
								} else {
									Sample.INSTANCE.play(Assets.Sounds.MISS);
								}
								Actor.remove(toRemove);
								next();
							}
						});
				return false;
			}
		});
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.CARNAGE, Talent.RIPPING_CLAW, Talent.CRIPPLING_STING, Talent.HEROIC_ENERGY};
	}
}
