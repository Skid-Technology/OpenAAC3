package me.konsolas.aac.api;

public enum HackType {
   SPAM("chat.Spam", "spam"),
   HITBOX("combat.Hitbox", "hitbox"),
   FASTBOW("combat.Fastbow", "fastbow"),
   FIGHTSPEED("combat.FightSpeed", "fightspeed"),
   KNOCKBACK("combat.Knockback", "knockback"),
   REGEN("combat.Regen", "regen"),
   FASTUSE("combat.FastUse", "fastuse"),
   KILLAURA("combat.Killaura", "killaura"),
   SPEED("movement.Speed", "speed"),
   PHASE("movement.Phase", "phase"),
   FLY("movement.Fly", "fly"),
   VEHICLES("movement.Vehicles", "vehicles"),
   ELYTRA("movement.Elytra", "elytra"),
   NOFALL("movement.NoFall", "nofall"),
   JESUS("movement.Jesus", "jesus"),
   DERP("movement.Derp", "derp"),
   CRITICALS("combat.Criticals", "criticals"),
   FASTBREAK("world.FastBreak", "fastbreak"),
   FASTPLACE("world.FastPlace", "fastplace"),
   NUKER("world.Nuker", "nuker"),
   NOSWING("world.NoSwing", "noswing"),
   INTERACT("world.Interact", "interact"),
   VELOCITY("world.Velocity", "velocity"),
   MOVE("MovementManager", "move");

   private final String configPath;
   private final String displayName;
   private final int id;

   HackType(String str, String str2) {
      this.configPath = str;
      this.displayName = str2;
      this.id = this.ordinal();
   }

   @Override
   public String toString() {
      return this.configPath;
   }

   public String getName() {
      return this.displayName;
   }

   public int getId() {
      return this.id;
   }
}
