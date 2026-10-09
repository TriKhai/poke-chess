package pac;

/** Offline variant rules, using existing species/resources and fixed-sized run state. */
public final class ExtraChessRules {
 private ExtraChessRules(){}
 public static int menuTop(int fh){return fh*2+6;}
 public static int menuGap(int height,int fh){return Math.max(fh+4,Math.min(fh+10,(height-fh*5-menuTop(fh))/4));}
 public static int menuDescriptionY(int height,int fh){return menuTop(fh)+3*menuGap(height,fh)+fh+8;}
 public static int menuHit(int x,int y,int width,int height,int fh,boolean ask){return TouchLayout.rowAt(x,y,width/8,(ask?height/2:menuTop(fh))-3,width*3/4,ask?fh+10:menuGap(height,fh),fh+6,ask?2:4);}
 public static boolean mode(int m){return m>=Run.MODE_TOWER&&m<=Run.MODE_BOSS_RUSH;}
 public static String name(int m){return m==Run.MODE_TOWER?Lang.t("THÁP THỬ THÁCH","CHALLENGE TOWER"):m==Run.MODE_MONOTYPE?Lang.t("ĐẤU TRƯỜNG ĐƠN HỆ","MONOTYPE ARENA"):m==Run.MODE_RANDOM_TEAM?Lang.t("ĐỘI HÌNH NGẪU NHIÊN","RANDOM TEAM"):Lang.t("BOSS RUSH","BOSS RUSH");}
 public static String objective(int m){return m==Run.MODE_TOWER?Lang.t("30 tầng, luật đổi mỗi tầng. Thắng mỗi 5 tầng chọn buff toàn đội; 100 máu.","30 floors with rotating rules. Every 5 cleared floors choose a team buff; 100 HP."):m==Run.MODE_MONOTYPE?Lang.t("Chọn một hệ đầu run. Shop và quà Pokémon chỉ mang hệ đã chọn; vượt 40 vòng.","Choose one type. Shop and Pokemon rewards only offer that type; clear 40 rounds."):m==Run.MODE_RANDOM_TEAM?Lang.t("Nhận 9 Pokémon cố định và đồ ghép. Không mua, bán hay roll Pokémon; sắp xếp đội và trang bị để vượt 30 vòng.","Receive 9 fixed Pokemon and components. No Pokemon purchases, sales or rerolls; arrange and equip them to clear 30 rounds."):Lang.t("20 trận boss có hộ vệ, tăng sức mạnh dần. Thắng hồi 10 máu và nhận đồ; mua sắm giữa trận.","20 boss fights with guards and rising strength. Wins restore 10 HP and grant items; shop between fights.");}
 public static String towerRule(int round){int rule=(round-1)%3;return rule==0?Lang.t("Tầng sinh lực: địch +25% máu","Vitality floor: enemies +25% HP"):rule==1?Lang.t("Tầng cuồng nộ: hai phe +20% công","Fury floor: both sides +20% ATK"):Lang.t("Tầng chậm: hai phe -20% tốc","Slow floor: both sides -20% speed");}
 public static String buffName(int choice){return choice==0?Lang.t("Sinh lực toàn đội +15%","Team HP +15%"):choice==1?Lang.t("Công toàn đội +10%","Team ATK +10%"):Lang.t("Thủ/Kháng toàn đội +3","Team DEF/RES +3");}
 public static void apply(Run r,Battle b){for(int i=0;i<b.n;i++)applyUnit(r,b.units[i]);}
 public static void applyUnit(Run r,Unit u){if(u.side==0){u.maxHp+=u.maxHp*r.modeHpPct/100;u.hp=u.prevHp=u.maxHp;u.atk+=u.atk*r.modeAtkPct/100;u.def+=r.modeArmor;u.speDef+=r.modeArmor;}if(r.mode==Run.MODE_TOWER){int rule=(r.round-1)%3;if(rule==0&&u.side==1){u.maxHp=u.maxHp*125/100;u.hp=u.prevHp=u.maxHp;}else if(rule==1)u.atk=u.atk*120/100;else if(rule==2){u.speed=CombatRules.cappedSpeed(u.speed*80/100);u.cd=CombatRules.cooldownTicks(1000,u.speed);}}}
}
