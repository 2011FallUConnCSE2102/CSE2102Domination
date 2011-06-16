package sound;

/**
 *
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public class ClipTest {

   public static void main(String[] args) throws Exception {

      SoundSystem s = new SoundSystem();
      s.playBackgroundMusic(0.75);
      s.playSound(SoundSystem.WILHELM_SCREAM, 0);
      Thread.sleep(10000);
//      s.playBackgroundMusic(0.5);
//      Thread.sleep(5000);
//      int distance = -1000;
//      for (int i = 0; i < 40; i++) {
//         distance += 50;
//         s.playSound(SoundSystem.ASSAULT_RIFLE, distance);
//         Thread.sleep(300);
//      }
//      Thread.sleep(5000);
      //new SoundSystem().playMusic(SoundSystem.BATTLE_BACKGROUND, 1);
      //Thread.sleep(5000);

//      for (int i = 0; i < 10; i++) {
//         SoundSystem newSys = new SoundSystem();
//         SoundSystem newSys2 = new SoundSystem();
//         newSys.playSound(SoundSystem.ASSAULT_RIFLE, 0);
//         Thread.sleep(250);
//         newSys2.playSound(SoundSystem.WILHELM_SCREAM, 0);
//      }
//
//      Thread.sleep(25000);
//
//      for (int i = 0; i < 10; i++) {
//         SoundSystem newSys = new SoundSystem();
//         SoundSystem newSys2 = new SoundSystem();
//         newSys.playSound(SoundSystem.ASSAULT_RIFLE, 0);
//         Thread.sleep(250);
//         newSys2.playSound(SoundSystem.WILHELM_SCREAM, 0);
//      }

//      Thread.sleep(10000);
      /*  newSys.anotherSound();
      newSys.dryFire();
      newSys.playContinuousSound();
      Thread.sleep(800);

      newSys.anotherSound();
      newSys.dropClip();
      newSys.playSound();
      Thread.sleep(500);

      newSys2.anotherSound();
      newSys2.bulletDrop();
      newSys2.playSound();
      Thread.sleep(800);

      newSys.loadClip();
      newSys.playSound();
      Thread.sleep(500);

      newSys.anotherSound();
      newSys.assaultRifle();
      newSys.playContinuousSound();
      Thread.sleep(2000);*/
   }
}
