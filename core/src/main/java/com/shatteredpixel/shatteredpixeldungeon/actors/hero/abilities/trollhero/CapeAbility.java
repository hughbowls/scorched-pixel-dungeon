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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.trollhero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Affection;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Entanglement;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Potential;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Repulsion;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Thorns;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class CapeAbility extends ArmorAbility {

	{
		baseChargeUse = 35f;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {

		Buff.prolong(hero, CapeAbilityTracker.class, CapeAbilityTracker.DURATION);
		hero.buff(CapeAbilityTracker.class).extensionsLeft = 2;
		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
		hero.sprite.emitter().burst(MagicMissile.ShamanParticle.RED, 10);

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();
		hero.spendAndNext(Actor.TICK);

	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.SHARP_THORNS, Talent.LETHAL_THORNS, Talent.ARCANE_CAPE, Talent.HEROIC_ENERGY };
	}

	public static class CapeAbilityTracker extends FlavourBuff{

		public static final float DURATION = 10f;

		public int extensionsLeft = 2;

		public void extend( int turns ){
			if (extensionsLeft > 0 && turns > 0) {
				spend(turns);
				extensionsLeft--;
			}
		}

		public int proc(int damage, Char attacker, Char defender){
			int deflected = damage/2;
			damage -= deflected;

			Hero hero = Dungeon.hero;
			Armor armor = hero.belongings.armor;
			Armor.Glyph glyph = armor.glyph;

			if (hero.hasTalent(Talent.ARCANE_CAPE)
					&& (4 - Random.Int(hero.pointsInTalent(Talent.ARCANE_CAPE)) == 0)) {
				if (glyph != null && defender.buff(MagicImmune.class) == null) {

					int level = Math.max( 0, armor.buffedLvl() );

					if (glyph instanceof Affection && !attacker.isCharmedBy(defender)){
						Buff.affect( attacker, Charm.class, Charm.DURATION ).object = defender.id();
						attacker.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 5 );
					}

					if (glyph instanceof Entanglement && defender.buff(Earthroot.Armor.class) != null) {
						Buff.affect( defender, Earthroot.Armor.class ).level( 5 + 2 * level );
						CellEmitter.bottom( defender.pos ).start( EarthParticle.FACTORY, 0.05f, 8 );
						Camera.main.shake( 1, 0.4f );
					}

					if (glyph instanceof Potential) {
						int wands = ((Hero) defender).belongings.charge( 1f );
						if (wands > 0) {
							defender.sprite.centerEmitter().burst(EnergyParticle.FACTORY, 10);
						}
					}

					if (glyph instanceof Repulsion) {
						int oppositeHero = attacker.pos + (attacker.pos - defender.pos);
						Ballistica trajectory = new Ballistica(attacker.pos, oppositeHero, Ballistica.MAGIC_BOLT);
						WandOfBlastWave.throwChar(attacker, trajectory, 2, true);
					}

					if (glyph instanceof Thorns) {
						Buff.affect( attacker, Bleeding.class).set( 4 + level );
					}
				}
			}

			if (hero.hasTalent(Talent.SHARP_THORNS)){
				damage += (damage * 0.1f) + ((0.1f)*hero.pointsInTalent(Talent.SHARP_THORNS));
			}

			if (attacker != null && Dungeon.level.adjacent(attacker.pos, defender.pos)) {
				attacker.damage(deflected, this);
			}

			return damage;
		}

		@Override
		public int icon() {
			return BuffIndicator.THORNS;
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

		@Override
		public String toString() {
			return Messages.get(this, "name");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", dispTurns(visualcooldown()));
		}

	}
}
