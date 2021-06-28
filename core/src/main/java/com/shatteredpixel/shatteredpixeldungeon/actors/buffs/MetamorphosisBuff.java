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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.heretic.Metamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.HereticArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class MetamorphosisBuff extends Buff {

    public static final float DURATION = 20f;

    {
        type = buffType.POSITIVE;
    }

    protected float left;
    public float left(){
        return left;
    }

    private int armorTier;

    private static final String LEFT		= "left";
    private static final String ARMOR_TIER	= "armortier";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( LEFT, left );
        bundle.put( ARMOR_TIER, armorTier );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        left = bundle.getFloat( LEFT );
        armorTier = bundle.getInt( ARMOR_TIER );
    }

    public void set( float left, int tier) {
        this.left = Math.max(this.left, left);
        ((Hero)target).belongings.armor.tier = 7;
        ((HeroSprite)target.sprite).updateArmor();
        ((Hero)target).belongings.armor.tier = tier;
        armorTier = tier;
    }

    public void extend( float left ) {
        this.left += left;
    }

    @Override
    public boolean act() {
        if (target.isAlive()){
            if (left > 0){
                left--;
                if (!(((Hero)target).belongings.armor instanceof HereticArmor)) {
                    detach();
                    return true;
                }
                if (((Hero)target).belongings.armor.tier != armorTier){
                    ((Hero)target).belongings.armor.tier = armorTier;
                }
                spend(TICK);
            } else detach();
        } else detach();
        return true;
    }

    @Override
    public void detach() {
        ((Hero)target).belongings.armor.tier = armorTier;
        Sample.INSTANCE.play( Assets.Sounds.BADGE );
        GameScene.flash(0x404040);
        super.detach();
        ((HeroSprite)target.sprite).updateArmor();
    }

    @Override
    public int icon() {
        return BuffIndicator.SC_METAMORPHOSIS;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (DURATION - left) / DURATION);
    }

    @Override
    public String toString() {
        return Messages.get(Metamorphosis.class, "buff_name");
    }

    @Override
    public String desc() {
        return Messages.get(Metamorphosis.class, "buff_desc", left);
    }

    {
        resistances.add( Burning.class );
        resistances.add( Blindness.class );
        resistances.add( Bleeding.class );
        resistances.add( Charm.class );
        resistances.add( Cripple.class );
        resistances.add( Roots.class );
        resistances.add( Ooze.class );
        resistances.add( Poison.class );
        resistances.add( Chill.class );
        resistances.add( Frost.class );
        resistances.add( Paralysis.class );
        resistances.add( Weakness.class );
        resistances.add( Vulnerable.class );
        resistances.add( Hex.class );
        resistances.add( Vertigo.class );
        resistances.add( Slow.class );
        resistances.add( Eye.DeathGaze.class );
        resistances.add( DisintegrationTrap.class );
        resistances.add( Grim.class );
        resistances.add( GrimTrap.class );
    }
}
