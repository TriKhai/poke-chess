package pac;

/** Portrait identity must include species, form, Shiny, size and alive/dead tint. */
public final class AvatarRouteTest {
 private static void check(boolean value,String msg){if(!value)throw new RuntimeException(msg);}
 public static void run(){int megas=0,forms=0;
  for(int sp=0;sp<Data.N;sp++){
   String normal=PetAvatar.path(sp,0,false,false),shiny=PetAvatar.path(sp,0,false,true);
   check(!normal.equals(shiny),"Shiny portrait aliases normal");
   check(normal.equals(sp<Data.CORE_N?"/av/"+sp+".png":"/dex/"+Data.nationalDex(sp)+".png"),"portrait species/dex route mismatch");
   check(!PetAvatar.cacheKey(sp,0,false,true,20,false).equals(PetAvatar.cacheKey(sp,0,false,true,20,true)),"dead tint aliases alive");
   check(!PetAvatar.cacheKey(sp,0,false,true,20,false).equals(PetAvatar.cacheKey(sp,0,false,true,32,false)),"portrait size aliases");
   if(MegaData.available(sp)){megas++;check(PetAvatar.path(sp,0,true,true).equals("/megaav/"+Data.nationalDex(sp)+"-shiny.png"),"Mega Shiny portrait mismatch");}
  }
  for(int form=1;form<=SpecialFormData.MAX_FORM;form++){forms++;check(PetAvatar.path(0,form,false,true).equals("/formav/"+SpecialFormData.key(form)+"-shiny.png"),"special Shiny route mismatch");check(PetAvatar.path(0,form,true,false).indexOf("/formav/")==0,"portrait/sprite form precedence mismatch");}
  int pixel=PetAvatar.grayPixel(0x8050A0F0);check((pixel>>>24)==128&&(pixel>>16&255)==(pixel>>8&255)&&(pixel>>8&255)==(pixel&255),"gray tint loses transparency or hue");
  System.out.println("AvatarRouteTest OK: "+Data.N+" species, "+megas+" Mega, "+forms+" forms, Shiny, dead tint and independent sizes");
 }
}
