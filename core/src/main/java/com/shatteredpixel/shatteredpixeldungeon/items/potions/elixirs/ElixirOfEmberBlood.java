package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class ElixirOfEmberBlood extends Elixir {

    {
        image = ItemSpriteSheet.ELIXIR_DRAGON;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return new ItemSprite.Glowing(0xFF3C3C, 0.5f);
    }

    @Override
    public void apply(Hero hero) {
        if (!Statistics.emberblood) {
            Statistics.emberblood = true;
            Badges.validateElementalistUnlock();
        }
        if (hero.pointsInTalent(Talent.ELIXIR_FORMULA) == 1) Buff.affect(hero, ElixirOfEmberBlood.EmberBlood.class).set(EmberBlood.DURATION_I, 1.5f);
        else if (hero.pointsInTalent(Talent.ELIXIR_FORMULA) == 2) Buff.affect(hero, ElixirOfEmberBlood.EmberBlood.class).set(EmberBlood.DURATION_II, 1.666f);
        else Buff.affect(hero, ElixirOfEmberBlood.EmberBlood.class).set(EmberBlood.DURATION, 1);
    }

    @Override
    public int value() {
        return 100;
    }

    public static class EmberBlood extends Buff {

        public static final float DURATION		= 1000f;
        public static final float DURATION_I	= 1500f;
        public static final float DURATION_II	= 1666f;

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        private float left;
        private float setting;

        public void set(float amount, float formula) {
            if (amount > left) left = amount;
            setting = formula;
        }

        @Override
        public boolean act() {
            if (target.buff(Burning.class) != null) {
                left--;
            }
            BuffIndicator.refreshHero();
            if (left <= 0) {
                detach();
            } else {
                spend(TICK);
            }
            return true;
        }

        @Override
        public int icon() {
            return BuffIndicator.BERSERK;
        }

        @Override
        public void tintIcon(Image icon) {
            if (target.buff(Burning.class) != null) {
                icon.hardlight(1f, 0.5f, 0f);
            }
            else icon.hardlight(0.5f, 0.25f, 0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, ((DURATION*setting) - left) / (DURATION*setting) );
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", left);
        }

        private static final String LEFT 	= "left";
        private static final String SETTING = "setting";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LEFT, left);
            bundle.put(SETTING, setting);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            left = bundle.getFloat(LEFT);
            setting = bundle.getFloat(SETTING);
        }
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs = new Class[]{ElixirOfDragonsBlood.class, Embers.class};
            inQuantity = new int[]{1, 1};

            cost = 0;

            output = ElixirOfEmberBlood.class;
            outQuantity = 1;
        }

    }
}
