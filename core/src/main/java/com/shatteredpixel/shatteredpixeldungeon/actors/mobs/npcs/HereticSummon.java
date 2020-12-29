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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HereticSummonSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class HereticSummon extends NPC {

	private boolean canzap = true;

	{
		HP = HT = 60;
		defenseSkill = 20;

		alignment = Alignment.ALLY;
		state = HUNTING;

		//before other mobs
		actPriority = MOB_PRIO + 1;

		intelligentAlly = true;

		flying = true;
		properties.add( Property.DEMONIC );
		WANDERING = new Wandering();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(
				(int) (Dungeon.depth*0.75f),
				Dungeon.depth
		);
	}

	@Override
	public int attackSkill( Char target ){
		return (int) (Dungeon.depth*1.5f);
	}

	@Override
	public int drRoll() {
		if (this instanceof EarthSummon){
			return Random.NormalIntRange(
					(int) (Dungeon.depth*0.33f),
					(int) (Dungeon.depth*0.66f)
			);
		} else return Random.NormalIntRange(
				(int) (Dungeon.depth*0.25f),
				(int) (Dungeon.depth*0.5f)
		);
	}

	@Override
	protected float attackDelay() {
		if (this instanceof BloodSummon){
		    return super.attackDelay()*0.5f;
		} else return super.attackDelay();
	}

	private int rangedCooldown = Random.NormalIntRange( 3, 5 );

	@Override
	protected boolean act() {
		if (state == HUNTING){
			rangedCooldown--;
		}

		return super.act();
	}

	public void summon(int HT) {
		this.HT = HT;
		this.HP = this.HT;
		state = HUNTING;
		target = Dungeon.hero.pos;
		Buff.append(Dungeon.hero, TalismanOfForesight.CharAwareness.class).charID = this.id();
	}

	public void cantzap() {
		canzap = false;
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		if (rangedCooldown <= 0 && canzap) {
			return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT ).collisionPos == enemy.pos;
		} else {
			return super.canAttack( enemy );
		}
	}

	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.adjacent( pos, enemy.pos ) || rangedCooldown > 0 || canzap) {

			return super.doAttack( enemy );

		} else {

			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				zap();
				return true;
			}
		}
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		meleeProc( enemy, damage );

		return damage;
	}

	private void zap() {
		spend( 1f );

		if (hit( this, enemy, true )) {

			rangedProc( enemy );

		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}

		rangedCooldown = Random.NormalIntRange( 3, 5 );
	}

	public void onZapComplete() {
		zap();
		next();
	}

	@Override
	public void add( Buff buff ) {
		if (harmfulBuffs.contains( buff.getClass() )) {
			damage( Random.NormalIntRange( HT/2, HT * 3/5 ), buff );
		} else {
			super.add( buff );
		}
	}

	protected abstract void meleeProc( Char enemy, int damage );
	protected abstract void rangedProc( Char enemy );

	protected ArrayList<Class<? extends Buff>> harmfulBuffs = new ArrayList<>();

	private static final String COOLDOWN = "cooldown";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( COOLDOWN, rangedCooldown );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		if (bundle.contains( COOLDOWN )){
			rangedCooldown = bundle.getInt( COOLDOWN );
		}
	}

	private class Wandering extends Mob.Wandering {

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			if ( enemyInFOV ) {

				enemySeen = true;

				notice();
				alerted = true;
				state = HUNTING;
				target = enemy.pos;

			} else {

				enemySeen = false;

				int oldPos = pos;
				target =  Dungeon.hero.pos;
				//always move towards the hero when wandering
				if (getCloser( target )) {
					//moves 2 tiles at a time when returning to the hero
					if (!Dungeon.level.adjacent(target, pos)){
						getCloser( target );
					}
					spend( 1 / speed() );
					return moveSprite( oldPos, pos );
				} else {
					spend( TICK );
				}

			}
			return true;
		}

	}

	@Override
	protected Char chooseEnemy() {
		Char enemy = super.chooseEnemy();

		//will never attack something far from their target
		if (enemy != null
				&& Dungeon.level.mobs.contains(enemy)
				&& (Dungeon.level.distance(enemy.pos, Dungeon.hero.pos) <= 8)){
			return enemy;
		}

		return null;
	}

	public static class BloodSummon extends HereticSummon {

		{
			spriteClass = HereticSummonSprite.Blood.class;
			baseSpeed = 2f;
		}

		@Override
		protected void meleeProc( Char enemy, int damage ) {
			if (Random.Int( 2 ) == 0) {
				Buff.affect(enemy, Bleeding.class).set(Math.round(damage * 0.33f));
				Splash.at( enemy.sprite.center(), enemy.sprite.blood(), 5);
			}

			if (enemy.HP <= damage) {
				HP = Math.max(HT, HP+enemy.HP);
				sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
			}
		}

		@Override
		protected void rangedProc( Char enemy ) {
			//cant zap
		}
	}

	public static class PoisonSummon extends HereticSummon {

		{
			spriteClass = HereticSummonSprite.Poison.class;
			properties.add( Property.ACIDIC );
			immunities.add( Poison.class );
			immunities.add( ToxicGas.class );
			immunities.add( StenchGas.class );
			immunities.add( CorrosiveGas.class );
		}

		@Override
		protected void meleeProc( Char enemy, int damage ) {
			if (Random.Int( 3 ) == 0) {
				Buff.affect(enemy, Poison.class).set(Random.Int(3, 5) );
			}
		}

		@Override
		protected void rangedProc( Char enemy ) {
			Buff.affect(enemy, Poison.class).set(Random.Int(2, 3) );
		}
	}

	public static class FrostSummon extends HereticSummon {

		{
			spriteClass = HereticSummonSprite.Frost.class;
			properties.add( Property.ICY );
			harmfulBuffs.add( Burning.class );
		}

		@Override
		protected void meleeProc( Char enemy, int damage ) {
			if (Random.Int( 3 ) == 0 || Dungeon.level.water[enemy.pos]) {
				Freezing.freeze( enemy.pos );
				Splash.at( enemy.sprite.center(), sprite.blood(), 5);
			}
		}

		@Override
		protected void rangedProc( Char enemy ) {
			Buff.affect(enemy, Chill.class, 2f);
			Splash.at( enemy.sprite.center(), sprite.blood(), 5);
		}
	}

	public static class ArcaneSummon extends HereticSummon {

		{
			spriteClass = HereticSummonSprite.Arcane.class;
			immunities.add( DisintegrationTrap.class );
			immunities.add( WandOfDisintegration.class );
			resistances.add( Eye.DeathGaze.class );
		}

		@Override
		protected void meleeProc( Char enemy, int damage ) {
			// non
		}

		@Override
		protected void rangedProc( Char enemy ) {
			int zap = damageRoll();
			if (enemy instanceof Eye) zap /= 2;
			enemy.sprite.emitter().burst(PurpleParticle.BURST, 5);
			enemy.damage(zap, this);
		}
	}

	public static class FireSummon extends HereticSummon {

		{
			spriteClass = HereticSummonSprite.Fire.class;
			properties.add( Property.FIERY );
			harmfulBuffs.add( com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost.class );
			harmfulBuffs.add( Chill.class );
			harmfulBuffs.add( Frost.class );
		}

		@Override
		protected void meleeProc( Char enemy, int damage ) {
			if (Random.Int( 2 ) == 0 && !Dungeon.level.water[enemy.pos]) {
				Buff.affect( enemy, Burning.class ).reignite( enemy );
				Splash.at( enemy.sprite.center(), sprite.blood(), 5);
			}
		}

		@Override
		protected void rangedProc( Char enemy ) {
			if (!Dungeon.level.water[enemy.pos]) {
				Buff.affect( enemy, Burning.class ).reignite( enemy, 4f );
			}
			Splash.at( enemy.sprite.center(), sprite.blood(), 5);
		}
	}

	public static class DarkSummon extends HereticSummon {

		{
			spriteClass = HereticSummonSprite.Dark.class;
			immunities.add( Weakness.class );
			immunities.add( Vulnerable.class );
			immunities.add( Hex.class );
			resistances.add( Shaman.RedShaman.EarthenBolt.class );
			resistances.add( Shaman.BlueShaman.EarthenBolt.class );
			resistances.add( Shaman.PurpleShaman.EarthenBolt.class );
		}

		@Override
		protected void meleeProc( Char enemy, int damage ) {
			switch (Random.Int(2)){
				case 0: Buff.affect(enemy, Weakness.class, Random.Int(3, 5) );
				case 1: Buff.affect(enemy, Vulnerable.class, Random.Int(3, 5) );
				case 2: Buff.affect(enemy, Hex.class, Random.Int(3, 5) );
			}
		}

		@Override
		protected void rangedProc( Char enemy ) {
			switch (Random.Int(2)){
				case 0: Buff.affect(enemy, Weakness.class, Random.Int(3, 5) );
				case 1: Buff.affect(enemy, Vulnerable.class, Random.Int(3, 5) );
				case 2: Buff.affect(enemy, Hex.class, Random.Int(3, 5) );
			}
		}
	}

	public static class EarthSummon extends HereticSummon {

		{
			spriteClass = HereticSummonSprite.Earth.class;
		}

		@Override
		protected void meleeProc( Char enemy, int damage ) {
			if (Random.Int( 3 ) == 0) {
				Buff.affect( enemy, Paralysis.class, 2f );
				enemy.sprite.emitter().burst(MagicMissile.EarthParticle.BURST, 5);
			}
		}

		@Override
		protected void rangedProc( Char enemy ) {
			// cant zap
		}
	}
}
