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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.alchemist.Exoskeleton;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.alchemist.MountNLoad;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.alchemist.SiegeMachine;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.elementalist.AetherBlink;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.elementalist.Conduit;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.elementalist.Resonance;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.heretic.Conflict;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.heretic.DeathGazeAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.heretic.Metamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpectralBlades;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WarpBeacon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.ElementalBlast;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.DeathMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.SmokeBomb;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.trollhero.CapeAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.trollhero.GreatThrowingWeapon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.trollhero.Sapper;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.HeroicLeap;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Shockwave;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmorVariant;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;

import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfHoneyedHealing;

import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHaste;

import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfDivination;

import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Dreamfoil;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;

import com.shatteredpixel.shatteredpixeldungeon.items.spells.ElementalSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;

import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTransfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Pistol;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.BoneBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.ForgeHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.BlindPowder;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.FishingSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.DeviceCompat;

public enum HeroClass {

	WARRIOR( HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR ),
	MAGE( HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK ),
	ROGUE( HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER ),
	HUNTRESS( HeroSubClass.SNIPER, HeroSubClass.WARDEN ),
	//Scorched
	HERETIC(HeroSubClass.SUMMONER, HeroSubClass.BLOODKNIGHT),
	ALCHEMIST(HeroSubClass.TRAILBLAZER, HeroSubClass.INNOVATOR),
	ELEMENTALIST(HeroSubClass.BINDER, HeroSubClass.SPELLWEAVER),
	TROLL(HeroSubClass.WARSMITH, HeroSubClass.GEOMANCER);

	private HeroSubClass[] subClasses;

	HeroClass( HeroSubClass...subClasses ) {
		this.subClasses = subClasses;
	}

	public void initHero( Hero hero ) {

		hero.heroClass = this;
		Talent.initClassTalents(hero);

		Item i = new ClothArmor().identify();
		if (!Challenges.isItemBlocked(i)) hero.belongings.armor = (ClothArmor)i;

		i = new Food();
		if (!Challenges.isItemBlocked(i)) i.collect();

		new VelvetPouch().collect();
		Dungeon.LimitedDrops.VELVET_POUCH.drop();

		Waterskin waterskin = new Waterskin();
		waterskin.collect();

		new ScrollOfIdentify().identify();

		switch (this) {
			case WARRIOR:
				initWarrior( hero );
				break;

			case MAGE:
				initMage( hero );
				break;

			case ROGUE:
				initRogue( hero );
				break;

			case HUNTRESS:
				initHuntress( hero );
				break;

			case HERETIC:
				initHeretic( hero );
				break;

			case ALCHEMIST:
				initAlchemist( hero );
				break;

			case ELEMENTALIST:
				initElementalist( hero );
				break;

			case TROLL:
				initTroll( hero );
				break;
		}

		for (int s = 0; s < QuickSlot.SIZE; s++){
			if (Dungeon.quickslot.getItem(s) == null){
				Dungeon.quickslot.setSlot(s, waterskin);
				break;
			}
		}

		//TESTPLAY ONLY
		if (DeviceCompat.isDebug()) {
			if (this != ELEMENTALIST){
				new ScrollHolder().collect();
				new PotionBandolier().collect();
			}
			new MagicalHolster().collect();

			new Gloves().identify().upgrade(100).collect();
			new AlchemistsToolkit().collect();
			new RingOfHaste().upgrade(100).collect();
			new RingOfFuror().upgrade(100).collect();
			new TengusMask().collect();
			new KingsCrown().collect();
			new Honeypot().quantity(20).collect();
			new Food().quantity(30).collect();
			
			new Rotberry.Seed().quantity(100).collect();
			new Starflower.Seed().quantity(100).collect();
			new Blindweed.Seed().quantity(100).collect();
			new Sungrass.Seed().quantity(100).collect();
			new Swiftthistle.Seed().quantity(100).collect();
			new Firebloom.Seed().quantity(100).collect();
			new Icecap.Seed().quantity(100).collect();
			new Sorrowmoss.Seed().quantity(100).collect();
			new Stormvine.Seed().quantity(100).collect();
			new Fadeleaf.Seed().quantity(100).collect();
			new Dreamfoil.Seed().quantity(100).collect();
			new Earthroot.Seed().quantity(100).collect();
			
			new ScrollOfIdentify().quantity(100).collect();
			new ScrollOfUpgrade().quantity(100).collect();
			new ScrollOfTransmutation().quantity(100).collect();
			new ScrollOfTerror().quantity(100).collect();
			new ScrollOfLullaby().quantity(100).collect();
			new ScrollOfTeleportation().quantity(100).collect();
			new ScrollOfMagicMapping().quantity(100).collect();
			new ScrollOfMirrorImage().quantity(100).collect();
			new ScrollOfRage().quantity(100).collect();
			new ScrollOfRemoveCurse().quantity(100).collect();
			new ScrollOfRecharging().quantity(100).collect();
			new ScrollOfRetribution().quantity(100).collect();
			
			new ScrollOfDivination().quantity(8).collect();
			
			new PotionOfStrength().quantity(100).collect();
			new PotionOfExperience().quantity(100).collect();
			new PotionOfInvisibility().quantity(100).collect();
			new PotionOfHealing().quantity(100).collect();
			new PotionOfHaste().quantity(100).collect();
			new PotionOfLiquidFlame().quantity(100).collect();
			new PotionOfFrost().quantity(100).collect();
			new PotionOfToxicGas().quantity(100).collect();
			new PotionOfLevitation().quantity(100).collect();
			new PotionOfMindVision().quantity(100).collect();
			new PotionOfPurity().quantity(100).collect();
			new PotionOfParalyticGas().quantity(100).collect();
			
			new Dart().quantity(10).collect();
			new WandOfMagicMissile().identify().collect();
		}
		//TESTPLAY ONLY
	}

	private static void initCommon( Hero hero ) {
		if (hero.heroClass != ELEMENTALIST) {
			Item i = new ClothArmor().identify();
			if (!Challenges.isItemBlocked(i)) hero.belongings.armor = (ClothArmor) i;
		}

		Item i = new Food();
		if (!Challenges.isItemBlocked(i)) i.collect();

		new ScrollOfIdentify().identify();

	}

	public Badges.Badge masteryBadge() {
		switch (this) {
			case WARRIOR:
			case HERETIC:
			case ALCHEMIST:
			case TROLL:
				return Badges.Badge.MASTERY_WARRIOR;
			case MAGE:
				return Badges.Badge.MASTERY_MAGE;
			case ROGUE:
				return Badges.Badge.MASTERY_ROGUE;
			case HUNTRESS:
				return Badges.Badge.MASTERY_HUNTRESS;
		}
		return null;
	}

	private static void initWarrior( Hero hero ) {
		(hero.belongings.weapon = new WornShortsword()).identify();
		ThrowingStone stones = new ThrowingStone();
		stones.quantity(3).collect();
		Dungeon.quickslot.setSlot(0, stones);

		if (hero.belongings.armor != null){
			hero.belongings.armor.affixSeal(new BrokenSeal());
		}

		new PotionOfHealing().identify();
		new ScrollOfRage().identify();
	}

	private static void initMage( Hero hero ) {
		MagesStaff staff;

		staff = new MagesStaff(new WandOfMagicMissile());

		(hero.belongings.weapon = staff).identify();
		hero.belongings.weapon.activate(hero);

		Dungeon.quickslot.setSlot(0, staff);

		new ScrollOfUpgrade().identify();
		new PotionOfLiquidFlame().identify();
	}

	private static void initRogue( Hero hero ) {
		(hero.belongings.weapon = new Dagger()).identify();

		CloakOfShadows cloak = new CloakOfShadows();
		(hero.belongings.artifact = cloak).identify();
		hero.belongings.artifact.activate( hero );

		ThrowingKnife knives = new ThrowingKnife();
		knives.quantity(3).collect();

		Dungeon.quickslot.setSlot(0, cloak);
		Dungeon.quickslot.setSlot(1, knives);

		new ScrollOfMagicMapping().identify();
		new PotionOfInvisibility().identify();
	}

	private static void initHuntress( Hero hero ) {

		(hero.belongings.weapon = new Gloves()).identify();
		SpiritBow bow = new SpiritBow();
		bow.identify().collect();

		Dungeon.quickslot.setSlot(0, bow);

		new PotionOfMindVision().identify();
		new ScrollOfLullaby().identify();
	}

	private static void initHeretic( Hero hero ) {

		(hero.belongings.weapon = new BoneBlade()).identify();

		BlindPowder powders = new BlindPowder();
		powders.quantity(5).collect();

		Dungeon.quickslot.setSlot(0, powders);

		new ScrollOfRemoveCurse().identify();
		new PotionOfPurity().identify();
	}

	private static void initAlchemist( Hero hero ) {

		Pistol pistol = new Pistol();
		pistol.identify().collect();
		pistol.reload_start();

		AlchemistsToolkit kit = new AlchemistsToolkit();
		(hero.belongings.artifact = kit).identify();
		hero.belongings.artifact.activate( hero );
		kit.bones = false;

		Dungeon.quickslot.setSlot(0, pistol);
		Dungeon.quickslot.setSlot(1, kit);

		new ScrollOfTransmutation().identify();
		new PotionOfStrength().identify();
	}

	private static void initElementalist( Hero hero ) {

		hero.STR = 8;

		ClothArmorVariant variant;
		variant = new ClothArmorVariant();
		(hero.belongings.armor = variant).identify();

		new ScrollHolder().collect();
		new PotionBandolier().collect();
		Dungeon.LimitedDrops.SCROLL_HOLDER.drop();
		Dungeon.LimitedDrops.POTION_BANDOLIER.drop();

		ElementalSpell.ElementalSpellFire fire = new ElementalSpell.ElementalSpellFire();
		fire.collect(); Dungeon.quickslot.setSlot(0, fire);
		ElementalSpell.ElementalSpellIce ice = new ElementalSpell.ElementalSpellIce();
		ice.collect(); Dungeon.quickslot.setSlot(1, ice);
		ElementalSpell.ElementalSpellElec elec = new ElementalSpell.ElementalSpellElec();
		elec.collect(); Dungeon.quickslot.setSlot(2, elec);

		new ScrollOfTeleportation().identify();
		new PotionOfLevitation().identify();
	}

	private static void initTroll( Hero hero ) {

		ForgeHammer hammer;
		hammer = new ForgeHammer();
		(hero.belongings.weapon = hammer).identify();

		new ScrollOfUpgrade().identify();
		new PotionOfHaste().identify();
	}

	public String title() {
		return Messages.get(HeroClass.class, name());
	}

	public String desc(){
		return Messages.get(HeroClass.class, name()+"_desc");
	}

	public HeroSubClass[] subClasses() {
		return subClasses;
	}

	public ArmorAbility[] armorAbilities(){
		switch (this) {
			case WARRIOR: default:
				return new ArmorAbility[]{new HeroicLeap(), new Shockwave(), new Endure()};
			case MAGE:
				return new ArmorAbility[]{new ElementalBlast(), new WildMagic(), new WarpBeacon()};
			case ROGUE:
				return new ArmorAbility[]{new SmokeBomb(), new DeathMark(), new ShadowClone()};
			case HUNTRESS:
				return new ArmorAbility[]{new SpectralBlades(), new NaturesPower(), new SpiritHawk()};
			case HERETIC:
				return new ArmorAbility[]{new Metamorphosis(), new DeathGazeAbility(), new Conflict()};
			case ALCHEMIST:
				return new ArmorAbility[]{new Exoskeleton(), new SiegeMachine(), new MountNLoad()};
			case ELEMENTALIST:
				return new ArmorAbility[]{new Resonance(), new AetherBlink(), new Conduit()};
			case TROLL:
				return new ArmorAbility[]{new Sapper(), new CapeAbility(), new GreatThrowingWeapon()};
		}
	}

	public String spritesheet() {
		switch (this) {
			case WARRIOR: default:
				return Assets.Sprites.WARRIOR;
			case MAGE:
				return Assets.Sprites.MAGE;
			case ROGUE:
				return Assets.Sprites.ROGUE;
			case HUNTRESS:
				return Assets.Sprites.HUNTRESS;
			case HERETIC:
				return Assets.Sprites.HERETIC;
			case ALCHEMIST:
				return Assets.Sprites.ALCHEMIST;
			case ELEMENTALIST:
				return Assets.Sprites.ELEMENTALIST;
			case TROLL:
				return Assets.Sprites.TROLL_HERO;
		}
	}

	public String splashArt(){
		switch (this) {
			case WARRIOR: default:
				return Assets.Splashes.WARRIOR;
			case MAGE:
				return Assets.Splashes.MAGE;
			case ROGUE:
				return Assets.Splashes.ROGUE;
			case HUNTRESS:
				return Assets.Splashes.HUNTRESS;
			case HERETIC:
				return Assets.Splashes.HERETIC;
			case ALCHEMIST:
				return Assets.Splashes.ALCHEMIST;
			case ELEMENTALIST:
				return Assets.Splashes.ELEMENTALIST;
			case TROLL:
				return Assets.Splashes.TROLL;
		}
	}
	
	public String[] perks() {
		switch (this) {
			case WARRIOR: default:
				return new String[]{
						Messages.get(HeroClass.class, "warrior_perk1"),
						Messages.get(HeroClass.class, "warrior_perk2"),
						Messages.get(HeroClass.class, "warrior_perk3"),
						Messages.get(HeroClass.class, "warrior_perk4"),
						Messages.get(HeroClass.class, "warrior_perk5"),
				};
			case MAGE:
				return new String[]{
						Messages.get(HeroClass.class, "mage_perk1"),
						Messages.get(HeroClass.class, "mage_perk2"),
						Messages.get(HeroClass.class, "mage_perk3"),
						Messages.get(HeroClass.class, "mage_perk4"),
						Messages.get(HeroClass.class, "mage_perk5"),
				};
			case ROGUE:
				return new String[]{
						Messages.get(HeroClass.class, "rogue_perk1"),
						Messages.get(HeroClass.class, "rogue_perk2"),
						Messages.get(HeroClass.class, "rogue_perk3"),
						Messages.get(HeroClass.class, "rogue_perk4"),
						Messages.get(HeroClass.class, "rogue_perk5"),
				};
			case HUNTRESS:
				return new String[]{
						Messages.get(HeroClass.class, "huntress_perk1"),
						Messages.get(HeroClass.class, "huntress_perk2"),
						Messages.get(HeroClass.class, "huntress_perk3"),
						Messages.get(HeroClass.class, "huntress_perk4"),
						Messages.get(HeroClass.class, "huntress_perk5"),
				};
			case HERETIC:
				return new String[]{
						Messages.get(HeroClass.class, "heretic_perk1"),
						Messages.get(HeroClass.class, "heretic_perk2"),
						Messages.get(HeroClass.class, "heretic_perk3"),
						Messages.get(HeroClass.class, "heretic_perk4"),
						Messages.get(HeroClass.class, "heretic_perk5"),
				};
			case ALCHEMIST:
				return new String[]{
						Messages.get(HeroClass.class, "alchemist_perk1"),
						Messages.get(HeroClass.class, "alchemist_perk2"),
						Messages.get(HeroClass.class, "alchemist_perk3"),
						Messages.get(HeroClass.class, "alchemist_perk4"),
						Messages.get(HeroClass.class, "alchemist_perk5"),
				};
			case ELEMENTALIST:
				return new String[]{
						Messages.get(HeroClass.class, "elementalist_perk1"),
						Messages.get(HeroClass.class, "elementalist_perk2"),
						Messages.get(HeroClass.class, "elementalist_perk3"),
						Messages.get(HeroClass.class, "elementalist_perk4"),
						Messages.get(HeroClass.class, "elementalist_perk5"),
				};
			case TROLL:
				return new String[]{
						Messages.get(HeroClass.class, "troll_perk1"),
						Messages.get(HeroClass.class, "troll_perk2"),
						Messages.get(HeroClass.class, "troll_perk3"),
						Messages.get(HeroClass.class, "troll_perk4"),
						Messages.get(HeroClass.class, "troll_perk5"),
				};
		}
	}
	
	public boolean isUnlocked(){
		//always unlock on debug builds
		if (DeviceCompat.isDebug()) return true;
		
		switch (this){
			case WARRIOR: default:
				return true;
			case MAGE:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_MAGE);
			case ROGUE:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_ROGUE);
			case HUNTRESS:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_HUNTRESS);
			case HERETIC:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_HERETIC);
			case ALCHEMIST:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_ALCHEMIST);
			case ELEMENTALIST:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_ELEMENTALIST);
			case TROLL:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_TROLL);
		}
	}
	
	public String unlockMsg() {
		switch (this){
			case WARRIOR: default:
				return "";
			case MAGE:
				return Messages.get(HeroClass.class, "mage_unlock");
			case ROGUE:
				return Messages.get(HeroClass.class, "rogue_unlock");
			case HUNTRESS:
				return Messages.get(HeroClass.class, "huntress_unlock");
			case HERETIC:
				return Messages.get(HeroClass.class, "heretic_unlock");
			case ALCHEMIST:
				return Messages.get(HeroClass.class, "alchemist_unlock");
			case ELEMENTALIST:
				return Messages.get(HeroClass.class, "elementalist_unlock");
			case TROLL:
				return Messages.get(HeroClass.class, "troll_unlock");
		}
	}

}
