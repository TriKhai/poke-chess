package pac;
/** Source-backed Gen 1-3 passive hooks independent from move behavior. */
public final class PokemonPassive {
 private PokemonPassive(){}private static boolean named(Unit u,String n){return Data.name[u.sp].equals(n);}
 public static void onStart(Unit u){if(named(u,"Wobbuffet")||named(u,"Sudowoodo"))u.passiveTree=true;}
 public static boolean blocksMove(Unit u){return u.passiveTree;}
 public static boolean immune(Unit u,int status){return named(u,"Vigoroth")&&status==CombatStatus.SLEEP;}
 public static void onTick(Battle b,Unit u){int wanted=0;if(named(u,"Heracross")&&(u.status.burn>0||u.status.poison>0))wanted=5;if(named(u,"Zangoose")&&u.status.poison>0)wanted=10;if(wanted!=u.passiveAttackBonus){u.atk+=wanted-u.passiveAttackBonus;u.passiveAttackBonus=wanted;}if(named(u,"Sudowoodo")&&u.passiveTree&&b.tick%10==0)u.atk+=2;}
 public static void onDamaged(Battle b,Unit u){if((named(u,"Psyduck")||named(u,"Golduck"))&&b.itemChance(10))u.status.apply(CombatStatus.CONFUSION,30);}
 public static String description(int sp){String n=Data.name[sp];if(n.equals("Heracross"))return Lang.t("Nội tại Guts: +5 ATK khi Bỏng hoặc Nhiễm độc.","Guts: +5 ATK while Burned or Poisoned.");if(n.equals("Zangoose"))return Lang.t("Nội tại Toxic Boost: +10 ATK khi Nhiễm độc.","Toxic Boost: +10 ATK while Poisoned.");if(n.equals("Vigoroth"))return Lang.t("Nội tại Vigoroth: miễn nhiễm Ngủ.","Vigoroth: immune to Sleep.");if(n.equals("Psyduck")||n.equals("Golduck"))return Lang.t("Nội tại Psyduck: 10% tự Hoang mang khi trúng đòn.","Psyduck: 10% chance to self-Confuse when hit.");if(n.equals("Sudowoodo"))return Lang.t("Dạng Cây: đứng yên và nhận +2 ATK mỗi giây.","Tree form: immobile and gains +2 ATK each second.");if(n.equals("Wobbuffet"))return Lang.t("Dạng Cây: không tự di chuyển khỏi vị trí.","Tree form: does not move from its position.");return "";}
}
