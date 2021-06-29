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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Berserk;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TrollHammer;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Metabolism;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Annoying;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Displacing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Exhausting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Fragile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Friendly;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Polarized;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Sacrificial;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Wayward;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blooming;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Chilling;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Corrupting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Kinetic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Lucky;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Unstable;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Vampiric;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;

abstract public class Weapon extends KindOfWeapon {

	public float    ACC = 1f;	// Accuracy modifier
	public float	DLY	= 1f;	// Speed modifier
	public int      RCH = 1;    // Reach modifier (only applies to melee hits)

	public enum Augment {
		SPEED   (0.7f, 0.6667f),
		DAMAGE  (1.5f, 1.6667f),
		NONE	(1.0f, 1.0000f);

		private float damageFactor;
		private float delayFactor;

		Augment(float dmg, float dly){
			damageFactor = dmg;
			delayFactor = dly;
		}

		public int damageFactor(int dmg){
			return Math.round(dmg * damageFactor);
		}

		public float delayFactor(float dly){
			return dly * delayFactor;
		}
	}
	
	public Augment augment = Augment.NONE;
	
	private static final int USES_TO_ID = 20;
	private float usesLeftToID = USES_TO_ID;
	private float availableUsesToID = USES_TO_ID/2f;
	
	public Enchantment enchantment;
	public boolean curseInfusionBonus = false;

	@Override
	public int proc( Char attacker, Char defender, int damage ) {

		if (enchantment != null && attacker.buff(MagicImmune.class) == null) {
			damage = enchantment.proc( this, attacker, defender, damage );
		}

		if (!levelKnown && attacker == Dungeon.hero) {
			float uses = Math.min( availableUsesToID, Talent.itemIDSpeedFactor(Dungeon.hero, this) );
			availableUsesToID -= uses;
			usesLeftToID -= uses;
			if (usesLeftToID <= 0) {
				identify();
				GLog.p( Messages.get(Weapon.class, "identify") );
				Badges.validateItemLevelAquired( this );
			}
		}

		if (attacker == Dungeon.hero && attacker.isAlive()
				&& Dungeon.hero.belongings.armor != null
				&& Dungeon.hero.belongings.armor.glyph != null
				&& Dungeon.hero.belongings.armor.glyph instanceof Metabolism
				&& Dungeon.hero.heroClass == HeroClass.HERETIC) {

			Metabolism.HereticMetabolismProc m = Dungeon.hero.buff(Metabolism.HereticMetabolismProc.class);
			if (m != null){
				m.activate(Dungeon.hero);
			}
		}

		if (this instanceof MeleeWeapon && ((MeleeWeapon)this).innovationBonus != 0){
			innovationLeft--;
			if (innovationLeft == 5) GLog.w(Messages.get(Weapon.class, "innovation_msg"));
			if (innovationLeft <= 0) innovationBonus = 0;
			updateQuickslot();
		}

		return damage;
	}

	public int hereticProc(Weapon weapon, Char attacker, Char defender, int damage) {
		Hero hero = Dungeon.hero;
		Level l = Dungeon.level;
		if (hero != null || hero.heroClass != HeroClass.HERETIC) return damage;

		if (weapon == null) weapon = (Weapon)hero.belongings.weapon;
		Enchantment type = weapon.enchantment;
		if (defender == null || !defender.isAlive()) return damage;
		if (type == null || !weapon.hasCurseEnchant()) return damage;

		if (type instanceof Annoying){
			for (Mob mob : l.mobs.toArray(new Mob[0])) {
				float pow = 5f + Random.NormalFloat(weapon.buffedLvl() * 0.5f, weapon.buffedLvl() * 1.5f);
				if (mob.alignment != hero.alignment && l.heroFOV[mob.pos]) {
					Buff.affect(mob, Amok.class, pow);
				}
			}
			return damage;
		}

		if (type instanceof Displacing){
			float pow = 5f + Random.NormalFloat(weapon.buffedLvl()*0.5f, weapon.buffedLvl()*1.5f);
			Buff.affect(defender, Blindness.class, pow);
			Buff.append(hero, TalismanOfForesight.CharAwareness.class, pow).charID = defender.id();

			return damage;
		}

		if (type instanceof Exhausting){
			int dur = Random.NormalIntRange(5, 20);
			float pow = dur + Random.NormalFloat(weapon.buffedLvl()*0.5f, weapon.buffedLvl()*1.5f);
			Buff.affect(defender, Weakness.class, pow);
			defender.sprite.emitter().burst( ShadowParticle.CURSE, 6 );

			return damage;
		}

		if (type instanceof Fragile){
			int hits = ((Fragile) type).getHits();

			int enemyHealth = defender.HP - damage;
			if (enemyHealth <= 0) return damage; //no point in proccing if they're already dead.

			// BaseMax at 150 hits = 10%, +1% per lvl, activates only after 50 hits+
			// Limited at 25%
			int chanceGrim = Math.max(0, (hits + (int) (weapon.buffedLvl()*1.5f) - 50));
			float res = defender.resist(Grim.class);
			if (chanceGrim > (int) (250*1.5)) chanceGrim = 250;
			if (Random.Int( 1500 ) <= chanceGrim
					&& !(defender.isImmune(Grim.class)
					|| defender.isInvulnerable(attacker.getClass()))){
				defender.damage( defender.HP * (int) (res), Grim.class );
				defender.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
				((Fragile) type).setHits(Math.max(0, hits-50));

			} else {
				// BaseMax at 150 hits = 20%, +2% per lvl
				// Limited at 66%
				int chanceDoom = Math.max(0, (hits + (int) (weapon.buffedLvl()*2f)));
				if (chanceDoom > 660) chanceDoom = 660;
				if (Random.Int( 1000 ) <= chanceDoom){
					Buff.affect(defender, Doom.class);
					defender.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
				}
			}

			return damage;
		}

		if (type instanceof Friendly){
			float pow = 5f + Random.NormalFloat(weapon.buffedLvl()*0.5f, weapon.buffedLvl()*1.5f);
			Buff.prolong(defender, StoneOfAggression.Aggression.class, pow);
			defender.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );

			return damage;
		}

		if (type instanceof Polarized){
			for (Mob mob : l.mobs.toArray( new Mob[0] )) {
				float pow = 5f + Random.NormalFloat(weapon.buffedLvl()*0.5f, weapon.buffedLvl()*1.5f);
				if (mob.alignment != hero.alignment && l.heroFOV[mob.pos]) {
					Buff.affect(mob, Vulnerable.class, pow);
					mob.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
				}
			}

			return damage;
		}

		if (type instanceof Sacrificial){
			int pow = Math.max(1, attacker.HP/6)
					+ (int) (Random.NormalFloat(weapon.buffedLvl()*0.2f, weapon.buffedLvl()*0.5f));
			Buff.affect(defender, Bleeding.class).set(Math.max(1, pow));

			return damage;
		}

		if (type instanceof Wayward){
			for (Mob mob : l.mobs.toArray( new Mob[0] )) {
				float pow = 5f + Random.NormalFloat(weapon.buffedLvl()*0.5f, weapon.buffedLvl()*1.5f);
				if (mob.alignment != hero.alignment && l.heroFOV[mob.pos]) {
					Buff.affect(mob, Hex.class, pow);
					mob.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
				}
			}

			return damage;
		}

		return damage;
	}
	
	public void onHeroGainExp( float levelPercent, Hero hero ){
		levelPercent *= Talent.itemIDSpeedFactor(hero, this);
		if (!levelKnown && isEquipped(hero) && availableUsesToID <= USES_TO_ID/2f) {
			//gains enough uses to ID over 0.5 levels
			availableUsesToID = Math.min(USES_TO_ID/2f, availableUsesToID + levelPercent * USES_TO_ID);
		}
	}
	
	private static final String USES_LEFT_TO_ID = "uses_left_to_id";
	private static final String AVAILABLE_USES  = "available_uses";
	private static final String ENCHANTMENT	    = "enchantment";
	private static final String CURSE_INFUSION_BONUS = "curse_infusion_bonus";
	private static final String AUGMENT	        = "augment";

	public int innovationBonus = 0;
	public int innovationLeft = 0;
	private static final String INNOVATION_BONUS	 = "innovation_bonus";
	private static final String INNOVATION_LEFT 	 = "innovation_left";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( USES_LEFT_TO_ID, usesLeftToID );
		bundle.put( AVAILABLE_USES, availableUsesToID );
		bundle.put( ENCHANTMENT, enchantment );
		bundle.put( CURSE_INFUSION_BONUS, curseInfusionBonus );
		bundle.put( AUGMENT, augment );

		bundle.put( INNOVATION_BONUS, innovationBonus );
		bundle.put( INNOVATION_LEFT, innovationLeft );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		usesLeftToID = bundle.getFloat( USES_LEFT_TO_ID );
		availableUsesToID = bundle.getFloat( AVAILABLE_USES );
		enchantment = (Enchantment)bundle.get( ENCHANTMENT );
		curseInfusionBonus = bundle.getBoolean( CURSE_INFUSION_BONUS );

		augment = bundle.getEnum(AUGMENT, Augment.class);

		innovationBonus = bundle.getInt( INNOVATION_BONUS );
		innovationLeft = bundle.getInt( INNOVATION_LEFT );
	}
	
	@Override
	public void reset() {
		super.reset();
		usesLeftToID = USES_TO_ID;
		availableUsesToID = USES_TO_ID/2f;
		// for Innovator
		innovationBonus = 0;
		innovationLeft = 0;
	}
	
	@Override
	public float accuracyFactor( Char owner ) {
		
		int encumbrance = 0;
		
		if( owner instanceof Hero ){
			encumbrance = STRReq() - ((Hero)owner).STR();
		}

		if (hasEnchant(Wayward.class, owner))
			encumbrance = Math.max(2, encumbrance+2);

		float ACC = this.ACC;

		return encumbrance > 0 ? (float)(ACC / Math.pow( 1.5, encumbrance )) : ACC;
	}
	
	@Override
	public float delayFactor( Char owner ) {
		return baseDelay(owner) * (1f/speedMultiplier(owner));
	}

	protected float baseDelay( Char owner ){
		float delay = augment.delayFactor(this.DLY);
		if (owner instanceof Hero) {
			int encumbrance = STRReq() - ((Hero)owner).STR();
			if (encumbrance > 0){
				delay *= Math.pow( 1.2, encumbrance );
			}
		}

		return delay;
	}

	protected float speedMultiplier(Char owner ){
		return RingOfFuror.attackSpeedMultiplier(owner);
	}

	@Override
	public int reachFactor(Char owner) {
		return hasEnchant(Projecting.class, owner) ? RCH+1 : RCH;
	}

	public int STRReq(){
		if (innovationBonus != 0){
			return STRReq(level()+innovationBonus);
		}

		return STRReq(level());
	}

	public abstract int STRReq(int lvl);

	protected static int STRReq(int tier, int lvl){
		lvl = Math.max(0, lvl);

		//strength req decreases at +1,+3,+6,+10,etc.
		return (8 + tier * 2) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
	}

	@Override
	public int level() {
		int trueLevel = super.level();

		if ((cursed || hasCurseEnchant()) && Dungeon.hero.hasTalent(Talent.ENHANCED_CURSE)){
			trueLevel += Dungeon.hero.pointsInTalent(Talent.ENHANCED_CURSE) >= 2 ? 2 : 1;
		}

		return trueLevel + (curseInfusionBonus ? 1 : 0);
	}
	
	//overrides as other things can equip these
	@Override
	public int buffedLvl() {
		if (isEquipped( Dungeon.hero ) || Dungeon.hero.belongings.contains( this )){
			if (innovationBonus != 0) {
				return super.buffedLvl() + innovationBonus;
			}

			return super.buffedLvl();
		} else {
			return level();
		}
	}

	public void setInnovation(int bonus, int left) {
		this.innovationBonus = bonus;
		this.innovationLeft = left;
		if (this.level() >= 4) this.innovationBonus--;
		if (this.level() >= 8) this.innovationBonus--;
		if (this.innovationBonus < 0) this.innovationBonus = 0;
	}

	public int getInnovationBonus(){ return this.innovationBonus; }
	public int getInnovationLeft(){ return this.innovationLeft; }

	@Override
	public Item upgrade() {
		return upgrade(false);
	}
	
	public Item upgrade(boolean enchant ) {

		if (enchant){
			if (enchantment == null || hasCurseEnchant()){
				enchant(Enchantment.random());
			}
		} else {
			//Scorched: Troll's T2
			Hero hero = Dungeon.hero;
			if (hero != null && hero.hasTalent(Talent.UPGRADE_MASTERY)){
				cursed = false; //same as Vanilla
				//if talent is LV2, enchant it randomly if it isn't good-enchanted
				if (!hasGoodEnchant() && hero.pointsInTalent(Talent.UPGRADE_MASTERY) == 2)
					enchant(Enchantment.random());
				return super.upgrade(); //Always preserve good-enchantments
			}

			//Scorched: Heretic; preserve curse-enchantments (NOT boolean 'cursed')
			if (hero != null && (hasCurseEnchant() && hero.heroClass == HeroClass.HERETIC)){
				return super.upgrade();
			}

			//Vanilla
			else if (hasCurseEnchant()){
				if (Random.Int(3) == 0) enchant(null);
			} else if (level() >= 4 && Random.Float(10) < Math.pow(2, level()-4)){
				enchant(null);
			}
		}

		cursed = false;
		
		return super.upgrade();
	}
	
	@Override
	public String name() {
		return enchantment != null && (cursedKnown || !enchantment.curse()) ? enchantment.name( super.name() ) : super.name();
	}
	
	@Override
	public Item random() {
		//+0: 75% (3/4)
		//+1: 20% (4/20)
		//+2: 5%  (1/20)
		int n = 0;
		if (Random.Int(4) == 0) {
			n++;
			if (Random.Int(5) == 0) {
				n++;
			}
		}
		level(n);
		
		//30% chance to be cursed
		//10% chance to be enchanted
		float effectRoll = Random.Float();
		if (effectRoll < 0.3f) {
			enchant(Enchantment.randomCurse());
			cursed = true;
		} else if (effectRoll >= 0.9f){
			enchant();
		}

		return this;
	}
	
	public Weapon enchant( Enchantment ench ) {
		if (ench == null || !ench.curse()) curseInfusionBonus = false;
		enchantment = ench;
		updateQuickslot();
		return this;
	}

	public Weapon enchant() {

		Class<? extends Enchantment> oldEnchantment = enchantment != null ? enchantment.getClass() : null;
		Enchantment ench = Enchantment.random( oldEnchantment );

		return enchant( ench );
	}

	public boolean hasEnchant(Class<?extends Enchantment> type, Char owner) {
		return enchantment != null && enchantment.getClass() == type && owner.buff(MagicImmune.class) == null;
	}
	
	//these are not used to process specific enchant effects, so magic immune doesn't affect them
	public boolean hasGoodEnchant(){
		return enchantment != null && !enchantment.curse();
	}

	public boolean hasCurseEnchant(){
		return enchantment != null && enchantment.curse();
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return enchantment != null && (cursedKnown || !enchantment.curse()) ? enchantment.glowing() : null;
	}

	public static abstract class Enchantment implements Bundlable {
		
		private static final Class<?>[] common = new Class<?>[]{
				Blazing.class, Chilling.class, Kinetic.class, Shocking.class};
		
		private static final Class<?>[] uncommon = new Class<?>[]{
				Blocking.class, Blooming.class, Elastic.class,
				Lucky.class, Projecting.class, Unstable.class};
		
		private static final Class<?>[] rare = new Class<?>[]{
				Corrupting.class, Grim.class, Vampiric.class};
		
		private static final float[] typeChances = new float[]{
				50, //12.5% each
				40, //6.67% each
				10  //3.33% each
		};
		
		private static final Class<?>[] curses = new Class<?>[]{
				Annoying.class, Displacing.class, Exhausting.class, Fragile.class,
				Sacrificial.class, Wayward.class, Polarized.class, Friendly.class
		};
		
			
		public abstract int proc( Weapon weapon, Char attacker, Char defender, int damage );

		protected float procChanceMultiplier( Char attacker ){
			float multi = 1f;
			if (attacker instanceof Hero) {
				if (((Hero) attacker).hasTalent(Talent.ENRAGED_CATALYST)) {
					Berserk rage = attacker.buff(Berserk.class);
					if (rage != null) {
						multi += (rage.rageAmount() / 6f) * ((Hero) attacker).pointsInTalent(Talent.ENRAGED_CATALYST);
					}
				}

				Weapon wep = ((Weapon)((Hero) attacker).belongings.weapon);
				//Scorched: little trick to reach getInnovationLeft();
				if (wep != null && wep.getInnovationLeft() > 0 && ((Hero) attacker).hasTalent(Talent.CATALYST_MK2)){
					multi *= 1.3f;
				}

				if (((Hero) attacker).hasTalent(Talent.ARCANESMITH)
						&& ((Hero) attacker).pointsInTalent(Talent.ARCANESMITH) >= 2) {
					TrollHammer hammer = attacker.buff(TrollHammer.class);
					if (hammer != null) {
						multi += 0.05f * hammer.boost;
						if (hammer.getBoost() >= 10 && ((Hero) attacker).pointsInTalent(Talent.ARCANESMITH) == 3) {
							multi += 10f; //a.k.a. "always activates enchantment"
						}
					}
				}
			}

			return multi;
		}

		public String name() {
			if (!curse())
				return name( Messages.get(this, "enchant"));
			else
				return name( Messages.get(Item.class, "curse"));
		}

		public String name( String weaponName ) {
			return Messages.get(this, "name", weaponName);
		}

		public String desc() {
			return Messages.get(this, "desc");
		}

		public boolean curse() {
			return false;
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
		}

		@Override
		public void storeInBundle( Bundle bundle ) {
		}
		
		public abstract ItemSprite.Glowing glowing();
		
		@SuppressWarnings("unchecked")
		public static Enchantment random( Class<? extends Enchantment> ... toIgnore ) {
			switch(Random.chances(typeChances)){
				case 0: default:
					return randomCommon( toIgnore );
				case 1:
					return randomUncommon( toIgnore );
				case 2:
					return randomRare( toIgnore );
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Enchantment randomCommon( Class<? extends Enchantment> ... toIgnore ) {
			ArrayList<Class<?>> enchants = new ArrayList<>(Arrays.asList(common));
			enchants.removeAll(Arrays.asList(toIgnore));
			if (enchants.isEmpty()) {
				return random();
			} else {
				return (Enchantment) Reflection.newInstance(Random.element(enchants));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Enchantment randomUncommon( Class<? extends Enchantment> ... toIgnore ) {
			ArrayList<Class<?>> enchants = new ArrayList<>(Arrays.asList(uncommon));
			enchants.removeAll(Arrays.asList(toIgnore));
			if (enchants.isEmpty()) {
				return random();
			} else {
				return (Enchantment) Reflection.newInstance(Random.element(enchants));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Enchantment randomRare( Class<? extends Enchantment> ... toIgnore ) {
			ArrayList<Class<?>> enchants = new ArrayList<>(Arrays.asList(rare));
			enchants.removeAll(Arrays.asList(toIgnore));
			if (enchants.isEmpty()) {
				return random();
			} else {
				return (Enchantment) Reflection.newInstance(Random.element(enchants));
			}
		}

		@SuppressWarnings("unchecked")
		public static Enchantment randomCurse( Class<? extends Enchantment> ... toIgnore ){
			ArrayList<Class<?>> enchants = new ArrayList<>(Arrays.asList(curses));
			enchants.removeAll(Arrays.asList(toIgnore));
			if (enchants.isEmpty()) {
				return random();
			} else {
				return (Enchantment) Reflection.newInstance(Random.element(enchants));
			}
		}
		
	}
}
