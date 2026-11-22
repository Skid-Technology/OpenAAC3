package me.konsolas.aac.compat;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public enum ParticleEffect {
   HUGE_EXPLOSION("hugeexplosion"),
   LARGE_EXPLODE("largeexplode"),
   FIREWORKS_SPARK("fireworksSpark"),
   BUBBLE("bubble"),
   SUSPEND("suspend"),
   DEPTH_SUSPEND("depthSuspend"),
   TOWN_AURA("townaura"),
   CRIT("crit"),
   MAGIC_CRIT("magicCrit"),
   MOB_SPELL("mobSpell"),
   MOB_SPELL_AMBIENT("mobSpellAmbient"),
   SPELL("spell"),
   INSTANT_SPELL("instantSpell"),
   WITCH_MAGIC("witchMagic"),
   NOTE("note"),
   PORTAL("portal"),
   ENCHANTMENT_TABLE("enchantmenttable"),
   EXPLODE("explode"),
   FLAME("flame"),
   LAVA("lava"),
   FOOTSTEP("footstep"),
   SPLASH("splash"),
   LARGE_SMOKE("largesmoke"),
   CLOUD("cloud"),
   RED_DUST("reddust"),
   SNOWBALL_POOF("snowballpoof"),
   DRIP_WATER("dripWater"),
   DRIP_LAVA("dripLava"),
   SNOW_SHOVEL("snowshovel"),
   SLIME("slime"),
   HEART("heart"),
   ANGRY_VILLAGER("angryVillager"),
   HAPPY_VILLAGER("happerVillager"),
   ICONCRACK("iconcrack"),
   BLOCKCRACK("blockcrack"),
   BLOCKDUST("blockdust");

   private String particleName;
   private Integer blockId = 1;
   private Integer materialData = 0;
   private Integer itemId = 260;
   private Vector offsetVector = new Vector(0, 0, 0);

   ParticleEffect(String str) {
      this.particleName = str;
   }

   PacketContainer buildPacket(Location location, float f, int i) {
      PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(Server.WORLD_PARTICLES);
      Particle particle = Particle.getByName(this.particleName);
      if (this == BLOCKCRACK || this == BLOCKDUST) {
         this.particleName = this.particleName.replace("{blockid}", String.valueOf(this.blockId));
         this.particleName = this.particleName.replace("{materialid}", String.valueOf(this.materialData));
      }

      if (this == ICONCRACK) {
         this.particleName = this.particleName.replace("{itemid}", String.valueOf(this.itemId));
      }

      packet.getParticles().write(0, particle);
      packet.getFloat()
         .write(0, (float)location.getX())
         .write(1, (float)location.getY())
         .write(2, (float)location.getZ())
         .write(3, (float)this.offsetVector.getX())
         .write(4, (float)this.offsetVector.getY())
         .write(5, (float)this.offsetVector.getZ())
         .write(6, f);
      packet.getIntegers().write(0, i);
      packet.getBooleans().write(0, particle.isLongDistance());
      if (this == BLOCKCRACK || this == BLOCKDUST || this == ICONCRACK) {
         int value = 0;
         if (this != BLOCKCRACK && this != BLOCKDUST) {
            value = this.itemId;
         } else {
            value = this.blockId + (this.materialData << 12);
         }

         packet.getIntegerArrays().write(0, new int[]{value});
      }

      return packet;
   }

   public ParticleEffect withColor(Color color) {
      if (this != RED_DUST) {
         throw new IllegalStateException("Colors can only be set on RED_DUST");
      }

      this.offsetVector.setX(color.getRed() / 255.0F);
      this.offsetVector.setY(color.getGreen() / 255.0F);
      this.offsetVector.setZ(color.getBlue() / 255.0F);
      if (this.offsetVector.getX() < Float.MIN_NORMAL) {
         this.offsetVector.setX(Float.MIN_NORMAL);
      }

      return this;
   }

   public ParticleEffect withOffset(Vector vector) {
      this.offsetVector = vector;
      return this;
   }

   public void sendToPlayer(Player player, Location location, float f, int i) {
      try {
         ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
         int value = i;
         float f2 = f;
         Location location2 = location;
         protocolManager.sendServerPacket(player, this.buildPacket(location2, f2, value));
      } catch (InvocationTargetException ex) {
         ex.printStackTrace();
      }
   }

   public void broadcast(Location location, float f, int i) {
      ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
      int value = i;
      float f2 = f;
      Location location2 = location;
      protocolManager.broadcastServerPacket(this.buildPacket(location2, f2, value));
   }

   public ParticleEffect withBlockId(Integer intVal) {
      if (this != BLOCKDUST && this != BLOCKCRACK) {
         throw new IllegalStateException("ParticleEffects other than BLOCKDUST and BLOCKCRACK can't get blockId's");
      }

      this.blockId = intVal;
      return this;
   }

   public ParticleEffect withMaterialData(Integer intVal) {
      if (this != BLOCKDUST && this != BLOCKCRACK) {
         throw new IllegalStateException("ParticleEffects other than BLOCKDUST and BLOCKCRACK can't get materialId's");
      }

      this.materialData = intVal;
      return this;
   }

   public ParticleEffect withItemId(Integer intVal) {
      if (this != ICONCRACK) {
         throw new IllegalStateException("ParticleEffects other than ICONCRACK can't get itemId's");
      }

      this.itemId = intVal;
      return this;
   }
}
