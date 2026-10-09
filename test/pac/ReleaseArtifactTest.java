package pac;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/** Desktop-only release gate: validates the actual JAR/JAD files delivered to phones. */
public final class ReleaseArtifactTest {
    private static void check(boolean ok,String message){
        if(!ok)throw new RuntimeException(message);
    }
    private static boolean entry(JarFile jar,String name){return jar.getEntry(name)!=null;}
    private static void verifyAnim(JarFile jar,String name)throws Exception{
        String png=name.endsWith("-shiny.dat")?name.substring(0,name.length()-10)+".png":name.substring(0,name.length()-4)+".png";
        check(entry(jar,png),"Atlas image missing "+png);java.io.DataInputStream image=new java.io.DataInputStream(jar.getInputStream(jar.getEntry(png)));int width,height;
        try{check(image.readInt()==0x89504e47&&image.readInt()==0x0d0a1a0a,"Bad PNG signature "+png);check(image.readInt()==13&&image.readInt()==0x49484452,"PNG IHDR missing "+png);width=image.readInt();height=image.readInt();check(width>0&&height>0,"Empty PNG "+png);}finally{image.close();}
        check(entry(jar,name),"Animation missing "+name);java.io.DataInputStream in=new java.io.DataInputStream(jar.getInputStream(jar.getEntry(name)));
        try{check(in.readInt()==0x50414352&&in.readUnsignedByte()==6,"Bad PACR "+name);for(int a=0;a<6;a++)for(int d=0;d<8;d++){int count=in.readUnsignedShort();check(count>0&&count<1024,"Empty animation direction "+name);for(int f=0;f<count;f++){int x=in.readUnsignedShort(),y=in.readUnsignedShort(),w=in.readUnsignedShort(),h=in.readUnsignedShort();check(w>0&&h>0&&x+w<=width&&y+h<=height,"Frame outside PNG "+name);for(int k=0;k<4;k++)in.readUnsignedShort();in.readByte();}}check(in.read()==-1,"Unexpected animation bytes "+name);}finally{in.close();}
    }
    private static boolean prefix(JarFile jar,String value){
        Enumeration e=jar.entries();
        while(e.hasMoreElements())if(((ZipEntry)e.nextElement()).getName().startsWith(value))return true;
        return false;
    }
    private static String jad(File file,String key)throws Exception{
        BufferedReader in=new BufferedReader(new FileReader(file));String line;
        try{while((line=in.readLine())!=null)if(line.startsWith(key+": "))return line.substring(key.length()+2).trim();}
        finally{in.close();}
        return null;
    }
    private static void verify(File jarFile,File jadFile,boolean lite,String version,String label)throws Exception{
        JarFile jar=new JarFile(jarFile);
        try{
            Manifest manifest=jar.getManifest();check(manifest!=null,"missing manifest "+jarFile);
            Attributes a=manifest.getMainAttributes();
            check("Kdic".equals(a.getValue("MIDlet-Vendor")),"vendor mismatch");
            check(version.equals(a.getValue("MIDlet-Version")),"numeric MIDlet version mismatch");
            check(label.equals(a.getValue("PAC-Build")),"build label mismatch");
            check(entry(jar,"pac/PacMidlet.class")&&entry(jar,"icon.png"),"runtime entry missing");
            check(entry(jar,"pac/ExtraChessRules.class")&&entry(jar,"pac/ExtraChessModeScreen.class"),"New chess modes missing");
            check(entry(jar,"pac/ExploreMotion.class")&&entry(jar,"pac/ExploreCaught.class")&&entry(jar,"pac/ExploreTiles.class"),"Explore redesign code missing");
            for(int sp=0;sp<Data.N;sp++){check(entry(jar,PetAvatar.path(sp,0,false,false).substring(1)),"normal portrait missing "+sp);if(ShinyData.available(sp))check(entry(jar,PetAvatar.path(sp,0,false,true).substring(1)),"Shiny portrait missing "+sp);if(MegaData.available(sp)){check(entry(jar,PetAvatar.path(sp,0,true,false).substring(1)),"Mega portrait missing "+sp);if(MegaData.shinyVisual(sp))check(entry(jar,PetAvatar.path(sp,0,true,true).substring(1)),"Mega Shiny portrait missing "+sp);}}
            for(int form=1;form<=SpecialFormData.MAX_FORM;form++)check(entry(jar,PetAvatar.path(0,form,false,false).substring(1)),"special-form portrait missing "+form);
            check(entry(jar,"map/explore-20.png")&&entry(jar,"map/explore-16.png")&&entry(jar,"map/explore-12.png"),"Explore generated tiles missing");
            for(int gen=4;gen<=9;gen++)for(int size=12;size<=20;size+=4)check(entry(jar,"map/explore-gen"+gen+"-"+size+".png"),"regional Explore tiles missing: Gen "+gen+" size "+size);
            check(!entry(jar,"ui/menu-background.jpg"),"Removed menu background still packaged");
            check(entry(jar,"pac/SurvivalRun.class")&&entry(jar,"pac/SurvivalScreen.class"),"Explore defence mode missing");
            check(entry(jar,"pac/SurvivalPickScreen.class")&&entry(jar,"pac/SurvivalProgress.class"),"Defence picker/progression missing");
            check(entry(jar,"pac/SurvivalTap.class")&&entry(jar,"fx/spawn/0.png"),"Defence double-tap/death effects missing");
            check(entry(jar,"pac/SurvivalBolt.class")&&entry(jar,"fx/survival-bolt.png"),"Animated defence projectile missing");
            check(entry(jar,"pac/SurvivalMapData.class")&&entry(jar,"fx/survival-terrain.png"),"generated defence map missing");
            check(entry(jar,"sp/0.png")&&entry(jar,"av/0.png")&&entry(jar,"dex/1000.png"),"fallback art missing");
            check(entry(jar,"fx/spawn/0.png")&&entry(jar,"fx/spawn/4.png"),"original SPAWN frames missing");
            check(entry(jar,"fx/evo2/0.png")&&entry(jar,"fx/evo2/7.png")
                  &&entry(jar,"fx/evo3/0.png")&&entry(jar,"fx/evo3/7.png"),"evolution colour frames missing");
            check(entry(jar,"megaraw/384.png")&&entry(jar,"megaraw/384.dat")
                  &&entry(jar,"megaraw/384-shiny.dat")&&entry(jar,"megaav/384.png")
                  &&entry(jar,"megaav/384-shiny.png"),"Mega/Shiny Mega assets missing");
            check(entry(jar,"formraw/0718-0002.dat")&&entry(jar,"formraw/0718-0002-shiny.dat"),
                  "Zygarde 100 normal/Shiny metadata missing");
            int[] megaGen1={6,65,94,115,142,150};for(int i=0;i<megaGen1.length;i++){String d=""+megaGen1[i];check(entry(jar,"megaraw/"+d+".png")&&entry(jar,"megaraw/"+d+".dat")&&entry(jar,"megaraw/"+d+"-shiny.dat")&&entry(jar,"megaav/"+d+".png")&&entry(jar,"megaav/"+d+"-shiny.png"),"Gen-1 Mega resource missing: "+d);}
            for(int form=13;form<=79;form++){String key=SpecialFormData.key(form);check(entry(jar,"formraw/"+key+".png")&&entry(jar,"formraw/"+key+".dat")&&(form==46||form==79||entry(jar,"formraw/"+key+"-shiny.dat"))&&entry(jar,"formav/"+key+".png")&&(form==46||form==79||entry(jar,"formav/"+key+"-shiny.png")),"regional resource missing: "+key);}
            check(entry(jar,"credits/SpriteCollab-forms.txt")&&entry(jar,"credits/SpriteCollab-LICENSE.md"),"SpriteCollab source credits missing");
            check(entry(jar,"credits/Gen3-forms.txt"),"Gen3 credits missing");
            int[] megaGen3={254,282,302,303,308,310,319,323,334,354,358,359,362,380,381,384};
            for(int i=0;i<megaGen3.length;i++){String d=""+megaGen3[i];check(entry(jar,"megaraw/"+d+".png")&&entry(jar,"megaav/"+d+".png"),"Gen3 Mega image missing "+d);verifyAnim(jar,"megaraw/"+d+".dat");if(megaGen3[i]!=308){check(entry(jar,"megaav/"+d+"-shiny.png"),"Gen3 shiny Mega avatar missing");verifyAnim(jar,"megaraw/"+d+"-shiny.dat");}}
            check(entry(jar,"credits/Gen4-9-forms.txt"),"Gen4-9 credits missing");
            String[] musicFiles={"music/autochess.mp3","music/explore.mp3"};
            for(int track=0;track<musicFiles.length;track++){String name=musicFiles[track];check(entry(jar,name),"Packaged MIDI missing "+name);check(jar.getEntry(name).getSize()>1000,"Empty music "+name);}
            check(entry(jar,"music/menu.mp3"),"Menu MP3 missing");
            check(entry(jar,"music/credits.txt"),"Music credits missing");
            for(int f=80;f<=SpecialFormData.MAX_FORM;f++)if(LaterFormData.contains(f)){String k=SpecialFormData.key(f);check(entry(jar,"formraw/"+k+".png")&&entry(jar,"formav/"+k+".png"),"Later form image missing "+k);verifyAnim(jar,"formraw/"+k+".dat");if(LaterFormData.shiny(f)){check(entry(jar,"formav/"+k+"-shiny.png"),"Later shiny avatar missing "+k);verifyAnim(jar,"formraw/"+k+"-shiny.dat");}}
            for(int i=0;i<LaterMegaData.DEX.length;i++){int dex=LaterMegaData.DEX[i],s=Data.speciesForDex(dex);String k=""+dex;check(entry(jar,"megaraw/"+k+".png")&&entry(jar,"megaav/"+k+".png"),"Later Mega image missing "+k);verifyAnim(jar,"megaraw/"+k+".dat");if(LaterMegaData.shiny(s)){check(entry(jar,"megaav/"+k+"-shiny.png"),"Later Mega shiny avatar missing "+k);verifyAnim(jar,"megaraw/"+k+"-shiny.dat");}}
            for(int f=74;f<=79;f++){String key=SpecialFormData.key(f);verifyAnim(jar,"formraw/"+key+".dat");if(f!=79)verifyAnim(jar,"formraw/"+key+"-shiny.dat");}
            if(lite){
                check(!prefix(jar,"raw/")&&!prefix(jar,"dexraw/")&&!prefix(jar,"sfx/")&&!prefix(jar,"maps/"),
                      "Lite contains excluded heavy assets");
            }else{
                check(entry(jar,"raw/0.png")&&entry(jar,"raw/0.dat")&&entry(jar,"dexraw/1000.png")
                      &&entry(jar,"sfx/0.png")&&entry(jar,"pac/SkillFxData.class"),"Full is missing heavy assets");
                check(!prefix(jar,"maps/")&&!entry(jar,"pac/MapData.class")&&!entry(jar,"pac/MapPreview.class"),"removed collection maps still packaged");
                boolean[] seen=new boolean[Data.MAX];int unique=0;
                for(int sp=0;sp<SkillFxData.FX.length;sp++){int fx=SkillFxData.effect(sp);check(fx>=0&&fx<seen.length,"invalid shared SFX id");check(entry(jar,"sfx/"+fx+".png"),"missing shared SFX strip "+fx);if(!seen[fx]){seen[fx]=true;unique++;}}
                check(unique<SkillFxData.FX.length,"SFX strips were not deduplicated");
            }
        }finally{jar.close();}
        check(label.equals(jad(jadFile,"PAC-Build")),"JAD build label mismatch");
        check(version.equals(jad(jadFile,"MIDlet-Version")),"JAD version mismatch");
        check(String.valueOf(jarFile.length()).equals(jad(jadFile,"MIDlet-Jar-Size")),"JAD size mismatch");
    }
    public static void main(String[] args)throws Exception{
        check(args.length==4,"usage: dist-dir app-name version build-label");
        File dir=new File(args[0]);String app=args[1],version=args[2],label=args[3];
        verify(new File(dir,app+"-Full.jar"),new File(dir,app+"-Full.jad"),false,version,label);
        check(!new File(dir,app+"-Lite.jar").exists()&&!new File(dir,app+"-Lite.jad").exists(),"Lite artifacts must not be published");
        System.out.println("ReleaseArtifactTest OK: Full contents, manifest and JAD; no Lite artifact");
    }
}
