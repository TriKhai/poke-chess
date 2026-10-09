package pac;

import java.io.InputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.rms.RecordStore;

/** One MMAPI player; decoding/loading never runs in the render/update loop. */
public final class Music implements Runnable {
 public static final int NONE=0,CHESS=1,EXPLORE=2,MENU=3;
 private static Music instance;
 private static synchronized Music get(){if(instance==null)instance=new Music();return instance;}
 private boolean enabled=true,paused;private int wanted,revision;
 private Thread worker;private Player player;private InputStream input;
 private int active;
 public static volatile String lastError="";public static volatile int starts;
 private Music(){RecordStore rs=null;try{rs=RecordStore.openRecordStore("pac_music",false);if(rs.getNumRecords()>0)enabled=rs.getRecord(1)[0]!=0;}catch(Exception e){}finally{if(rs!=null)try{rs.closeRecordStore();}catch(Exception e){}}}
 public static boolean enabled(){Music m=get();synchronized(m){return m.enabled;}}
 public static String path(int track){return track==CHESS?"/music/autochess.mp3":track==EXPLORE?"/music/explore.mp3":track==MENU?"/music/menu.mp3":null;}
 public static String mime(int track){return "audio/mpeg";}
 public static int trackFor(String name){if(name.startsWith("pac.Explore")||name.equals("pac.GachaScreen")||name.equals("pac.WorldScreen")||name.equals("pac.CatchScreen")||name.equals("pac.SurvivalScreen")||name.equals("pac.SurvivalPickScreen"))return EXPLORE;if(name.equals("pac.ExtraChessModeScreen")||name.equals("pac.ChessScreen")||name.equals("pac.ChessModeScreen")||name.equals("pac.GenerationModeScreen")||name.equals("pac.GenerationTestScreen")||name.equals("pac.HistoryScreen"))return CHESS;if(name.equals("pac.StartupNoticeScreen")||name.equals("pac.PlayPathScreen")||name.equals("pac.MenuScreen")||name.equals("pac.SettingsScreen")||name.equals("pac.AboutScreen")||name.equals("pac.HelpScreen")||name.equals("pac.ProfileScreen")||name.equals("pac.CollectionScreen"))return MENU;return NONE;}
 public static void screen(Screen screen){get().request(trackFor(screen.getClass().getName()));}
 public static void pause(boolean value){Music m=get();synchronized(m){if(m.paused==value)return;m.paused=value;m.wake();}}
 public static void toggle(){Music m=get();boolean enabled;synchronized(m){m.enabled=!m.enabled;enabled=m.enabled;m.wake();}RecordStore rs=null;try{rs=RecordStore.openRecordStore("pac_music",true);byte[] b={(byte)(enabled?1:0)};if(rs.getNumRecords()==0)rs.addRecord(b,0,1);else rs.setRecord(1,b,0,1);}catch(Exception e){}finally{if(rs!=null)try{rs.closeRecordStore();}catch(Exception e){}}}
 private synchronized void request(int track){if(wanted==track)return;wanted=track;wake();}
 private void wake(){revision++;if(worker==null){worker=new Thread(this);worker.start();}notifyAll();}
 private void close(){if(player!=null){try{player.stop();}catch(Exception e){}try{player.close();}catch(Exception e){}player=null;}if(input!=null){try{input.close();}catch(Exception e){}input=null;}active=NONE;}
 public void run(){int seen=-1;for(;;){int track; synchronized(this){while(seen==revision)try{wait();}catch(InterruptedException e){}seen=revision;track=enabled&&!paused?wanted:NONE;}if(track==active)continue;close();if(track==NONE)continue;try{input=getClass().getResourceAsStream(path(track));if(input==null)throw new java.io.IOException("Missing music");player=Manager.createPlayer(input,mime(track));player.realize();player.prefetch();VolumeControl volume=(VolumeControl)player.getControl("VolumeControl");if(volume!=null)volume.setLevel(35);player.setLoopCount(-1);synchronized(this){if(seen!=revision){close();continue;}}player.start();active=track;starts++;lastError="";}catch(Exception e){lastError=e.toString();close();}}}
}
