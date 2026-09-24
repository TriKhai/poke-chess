package pac;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/** Desktop-only RC gate: validates the actual JAR/JAD files delivered to phones. */
public final class ReleaseArtifactTest {
    private static void check(boolean ok,String message){
        if(!ok)throw new RuntimeException(message);
    }
    private static boolean entry(JarFile jar,String name){return jar.getEntry(name)!=null;}
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
    private static void verify(File jarFile,File jadFile,boolean lite,String label)throws Exception{
        JarFile jar=new JarFile(jarFile);
        try{
            Manifest manifest=jar.getManifest();check(manifest!=null,"missing manifest "+jarFile);
            Attributes a=manifest.getMainAttributes();
            check("Kdic".equals(a.getValue("MIDlet-Vendor")),"vendor mismatch");
            check("1.3.9".equals(a.getValue("MIDlet-Version")),"numeric MIDlet version mismatch");
            check(label.equals(a.getValue("PAC-Build")),"build label mismatch");
            check(entry(jar,"pac/PacMidlet.class")&&entry(jar,"icon.png"),"runtime entry missing");
            check(entry(jar,"sp/0.png")&&entry(jar,"av/0.png")&&entry(jar,"dex/1000.png"),"fallback art missing");
            if(lite){
                check(!prefix(jar,"raw/")&&!prefix(jar,"dexraw/")&&!prefix(jar,"sfx/")&&!prefix(jar,"maps/"),
                      "Lite contains excluded heavy assets");
            }else{
                check(entry(jar,"raw/0.png")&&entry(jar,"raw/0.dat")&&entry(jar,"dexraw/1000.png")
                      &&entry(jar,"sfx/0.png")&&prefix(jar,"maps/"),"Full is missing heavy assets");
            }
        }finally{jar.close();}
        check(label.equals(jad(jadFile,"PAC-Build")),"JAD build label mismatch");
        check(String.valueOf(jarFile.length()).equals(jad(jadFile,"MIDlet-Jar-Size")),"JAD size mismatch");
    }
    public static void main(String[] args)throws Exception{
        check(args.length==3,"usage: dist-dir app-name build-label");
        File dir=new File(args[0]);String app=args[1],label=args[2];
        verify(new File(dir,app+"-Full.jar"),new File(dir,app+"-Full.jad"),false,label);
        verify(new File(dir,app+"-Lite.jar"),new File(dir,app+"-Lite.jad"),true,label);
        System.out.println("ReleaseArtifactTest OK: Full/Lite contents, manifest and JAD");
    }
}
