package pac;
/** Existing upgrade rates, checked before mutating the persistent wallet. */
public final class BallExchange {
 private BallExchange(){}
 public static int cost(int route){return route==0?10:route==1?100:0;}
 public static int target(int route){return route==0?BallArt.SILVER:BallArt.GOLD;}
 public static boolean exchange(int route){int price=cost(route);if(price==0||Save.balls<price)return false;if(route==0&&Save.silverBalls==2147483647||route==1&&Save.goldBalls==2147483647)return false;Save.balls-=price;if(route==0)Save.silverBalls++;else Save.goldBalls++;return true;}
}
