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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.ReclaimTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Sapper extends ArmorAbility {

	{
		baseChargeUse = 25f;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {

		if (target == null){
			return;
		}

		Level l = Dungeon.level;
		Heap heap = l.heaps.get(target);
		float workTime = 3f;

		if (armor.charge < chargeUse(hero)){
			GLog.w(Messages.get(ClassArmor.class, "low_charge"));
			return;
		}

		if (!Dungeon.level.adjacent(hero.pos, target) && target != hero.pos) {
			GLog.w(Messages.get(Sapper.class, "too_far"));
			return;
		}

		//stair
		if (target == hero.pos && hero.hasTalent(Talent.EMERGENCY_STAIR)){
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(
					new WndOptions( new Image(Assets.Interfaces.TALENT_ICONS,
							272, 112, 16, 16),
							Messages.get(Sapper.class, "target_self"),
							Messages.get(Sapper.class, "warn"),
							Messages.get(Sapper.class, "yes"),
							Messages.get(Sapper.class, "no") ) {
						@Override
						protected void onSelect( int index ) {
							if (index == 0) {
								if (Dungeon.bossLevel()) {
									GLog.w( Messages.get(Sapper.class, "boss") );
									return;
								}
								if (Dungeon.depth == 1) {
									GLog.w( Messages.get(Sapper.class, "exit") );
									return;
								}
								MakeStair(target, workTime);
								return;
							} if (index == 1) {
								WallRemoval(target, workTime);
								return;
							}
						}
					}); }});
			return;
		}

		//lock
		if (target != hero.pos && hero.hasTalent(Talent.LOCKSMITH)
				&& (((l.map[target] == Terrain.LOCKED_DOOR)
					|| (l.map[target] == Terrain.DOOR && hero.pointsInTalent(Talent.LOCKSMITH) >= 2)
					|| (heap != null && heap.type == Heap.Type.LOCKED_CHEST && hero.pointsInTalent(Talent.LOCKSMITH) >= 3)))) {

			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(
					new WndOptions( new Image(Assets.Interfaces.TALENT_ICONS,
							288, 112, 16, 16),
							Messages.get(Sapper.class, "target_lock"),
							Messages.get(Sapper.class, "warn"),
							Messages.get(Sapper.class, "yes"),
							Messages.get(Sapper.class, "no") ) {
						@Override
						protected void onSelect( int index ) {
							if (index == 0) {
								if (Dungeon.bossLevel()) {
									GLog.w( Messages.get(Sapper.class, "boss") );
									return;
								}

								if (l.map[target] == Terrain.LOCKED_DOOR) {
									PickDoor(target, hero.pointsInTalent(Talent.LOCKSMITH) == 4 ? 2f : workTime);
									return;
								}

								if ((l.map[target] == Terrain.DOOR || l.map[target] == Terrain.OPEN_DOOR)
										&& hero.pointsInTalent(Talent.LOCKSMITH) >= 2) {
									LockDoor(target, hero.pointsInTalent(Talent.LOCKSMITH) == 4 ? 2f : workTime);
									return;
								}

								if (heap.type == Heap.Type.LOCKED_CHEST
										&& hero.pointsInTalent(Talent.LOCKSMITH) >= 3) {
									PickChest(target, hero.pointsInTalent(Talent.LOCKSMITH) == 4 ? 2f : workTime);
									return;
								}
								return;
							} if (index == 1) {
								WallRemoval(target, workTime);
								return;
							}
						}
					}); }});
			return;
		}

		//trap
		if (l.traps.get(target) != null && l.traps.get(target).visible
				&& hero.hasTalent(Talent.TRAP_DISPOSAL)) {
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(
					new WndOptions( new Image(Assets.Interfaces.TALENT_ICONS,
							304, 112, 16, 16),
							Messages.get(Sapper.class, "target_trap"),
							Messages.get(Sapper.class, "warn"),
							Messages.get(Sapper.class, "yes"),
							Messages.get(Sapper.class, "no") ) {
						@Override
						protected void onSelect( int index ) {
							if (index == 0) {
								TrapDisposal(target, hero.pointsInTalent(Talent.TRAP_DISPOSAL) >= 2 ? 1f : workTime);
								return;
							} if (index == 1) {
								WallRemoval(target, workTime);
								return;
							}
						}
					}); }});
			return;
		}

		WallRemoval(target, workTime);
		return;
	}

	public void WallRemoval(int target, float workTime) {

		Hero hero = Dungeon.hero;
		Level l = Dungeon.level;
		int pos = Dungeon.hero.pos;
		workTime = 10f;

		if (hero.isStarving()){
			GLog.w(Messages.get(Sapper.class, "starving"));
			return;
		}

		ClassArmor armor = (ClassArmor)hero.belongings.armor;

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			mob.beckon( hero.pos );
		}

		hero.sprite.zap(target);
		hero.spend(workTime);

		if (hero.isAlive() && hero.pos == pos){
			Sample.INSTANCE.play(Assets.Sounds.ROCKS);
			for (int walls : PathFinder.NEIGHBOURS9){
				int c = target + walls;
				if (c >= 0 && c < l.length()) {
					CellEmitter.get(c).start(Speck.factory(Speck.ROCK), 0.07f, 10);

					int terr = l.map[c];
					if (terr == Terrain.WALL || terr == Terrain.WALL_DECO
							|| terr == Terrain.DOOR || terr == Terrain.OPEN_DOOR
							|| terr == Terrain.LOCKED_DOOR || terr == Terrain.SECRET_DOOR
							|| terr == Terrain.STATUE
							|| terr == Terrain.BARRICADE || terr == Terrain.BOOKSHELF){
						l.set(c, Terrain.EMPTY);
					}

					if (terr == Terrain.STATUE_SP) {
						l.set(c, Terrain.EMPTY_SP);
					}

					if (terr == Terrain.FURROWED_GRASS || terr == Terrain.HIGH_GRASS) {
						l.set(c, Terrain.GRASS);
					}

					GameScene.updateMap(c);
					GameScene.updateFog(c, 3);
					hero.checkVisibleMobs();
					l.updateFieldOfView(hero, l.heroFOV);
					Dungeon.level.cleanWalls();
					Dungeon.observe();
				}
			}
			hero.next();

		} else {
			if (hero.pos != pos) {
				GLog.w(Messages.get(Sapper.class, "fail"));
				hero.next();
			}
		}
	}

	public void MakeStair(int target, float workTime) {
		Hero hero = Dungeon.hero;
		int pos = Dungeon.hero.pos;
		ClassArmor armor = (ClassArmor) hero.belongings.armor;
		float needCharge = 3f;
		switch (hero.pointsInTalent(Talent.EMERGENCY_STAIR)){
			case 1: default: break;
			case 2: needCharge = 2.5f; break;
			case 3: needCharge = 2f; break;
			case 4: needCharge = 1.5f; break;
		}
		if (armor.charge < needCharge*chargeUse(hero)) {
			GLog.w(Messages.get(ClassArmor.class, "low_charge"));
			return;
		}

		armor.charge -= needCharge*chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();
		hero.spend(workTime);

		if (hero.isAlive() && hero.pos == pos) {
			Sample.INSTANCE.play(Assets.Sounds.ROCKS);

			Buff buff = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
			if (buff != null) buff.detach();
			buff = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
			if (buff != null) buff.detach();

			InterlevelScene.mode = InterlevelScene.Mode.RETURN;
			InterlevelScene.returnDepth = Math.max(1, (Dungeon.depth - 1));
			InterlevelScene.returnPos = -2;
			Game.switchScene( InterlevelScene.class );

			hero.next();

		} else {
			if (hero.pos != pos) {
				GLog.w(Messages.get(Sapper.class, "fail"));
				hero.next();
			}
		}
	}

	public void LockDoor(int target, float workTime) {
		Hero hero = Dungeon.hero;
		Level l = Dungeon.level;
		int pos = Dungeon.hero.pos;
		ClassArmor armor = (ClassArmor) hero.belongings.armor;

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();
		hero.sprite.operate(target);
		hero.spend(workTime);

		if (hero.isAlive() && hero.pos == pos){
			Sample.INSTANCE.play(Assets.Sounds.UNLOCK, 2f);
			l.set(target, Terrain.LOCKED_DOOR);
			hero.next();

		} else {
			if (hero.pos != pos) {
				GLog.w(Messages.get(Sapper.class, "fail"));
				hero.next();
			}
		}
	}

	public void PickDoor(int target, float workTime) {
		Hero hero = Dungeon.hero;
		Level l = Dungeon.level;
		int pos = Dungeon.hero.pos;
		ClassArmor armor = (ClassArmor) hero.belongings.armor;

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();
		hero.sprite.operate(target);
		hero.spend(workTime);

		if (hero.isAlive() && hero.pos == pos) {
			Sample.INSTANCE.play(Assets.Sounds.UNLOCK, 2f);
			l.set(target, Terrain.DOOR);
			hero.next();

		} else {
			if (hero.pos != pos) {
				GLog.w(Messages.get(Sapper.class, "fail"));
				hero.next();
			}
		}
	}

	public void PickChest(int target, float workTime) {
		Hero hero = Dungeon.hero;
		Heap heap = Dungeon.level.heaps.get(target);
		int pos = Dungeon.hero.pos;
		ClassArmor armor = (ClassArmor) hero.belongings.armor;

		armor.charge -= chargeUse(hero);
		Invisibility.dispel();
		armor.updateQuickslot();
		hero.sprite.operate(target);
		hero.spend(workTime);

		if (hero.isAlive() && hero.pos == pos) {
			Sample.INSTANCE.play(Assets.Sounds.UNLOCK, 2f);
			heap.open(hero);
			hero.next();

		} else {
			if (hero.pos != pos) {
				GLog.w(Messages.get(Sapper.class, "fail"));
				hero.next();
			}
		}
	}

	public void TrapDisposal(int target, float workTime) {
		Hero hero = Dungeon.hero;
		Trap trap = Dungeon.level.traps.get(target);
		int pos = Dungeon.hero.pos;
		ClassArmor armor = (ClassArmor) hero.belongings.armor;

		armor.charge -= chargeUse(hero);
		Invisibility.dispel();
		armor.updateQuickslot();
		hero.spend(workTime);

		if (hero.isAlive() && hero.pos == pos) {
			Sample.INSTANCE.play(Assets.Sounds.UNLOCK, 2f);
			CellEmitter.get( target ).burst( Speck.factory( Speck.LIGHT ), 4 );
			hero.sprite.operate(target);

			trap.disarm();
			Buff.count(hero, SapperTrapCounter.class, 1f);
			SapperTrapCounter maxTrap = hero.buff(SapperTrapCounter.class);
			if ((hero.pointsInTalent(Talent.TRAP_DISPOSAL) == 3 && maxTrap.count() < 4)
				|| hero.pointsInTalent(Talent.TRAP_DISPOSAL) == 4 && maxTrap.count() < 6) {
				ReclaimTrap spell = new ReclaimTrap();
				spell.setStoredTrap(target);

				if (spell.doPickUp( hero )) {
					GLog.i( Messages.get(Sapper.class, "trap_get", trap.name() ));
				} else {
					Dungeon.level.drop( spell, target ).sprite.drop();
				}
			}

			hero.next();

		} else {
			if (hero.pos != pos) {
				GLog.w(Messages.get(Sapper.class, "fail"));
				hero.next();
			}
		}
	}

	public static class SapperTrapCounter extends CounterBuff {};

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.EMERGENCY_STAIR, Talent.LOCKSMITH, Talent.TRAP_DISPOSAL, Talent.HEROIC_ENERGY};
	}
}
