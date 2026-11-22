package me.konsolas.aac.compat;

import org.bukkit.Location;

public enum FakeEntityOffset {
   PLUS_PLUS {
      @Override
      public Location offset(double value, Location location) {
         return location.clone().add(value, 0.0, value);
      }
   },
   PLUS_MINUS {
      @Override
      public Location offset(double value, Location location) {
         return location.clone().add(value, 0.0, -value);
      }
   },
   MINUS_PLUS {
      @Override
      public Location offset(double value, Location location) {
         return location.clone().add(-value, 0.0, value);
      }
   },
   MINUS_MINUS {
      @Override
      public Location offset(double value, Location location) {
         return location.clone().add(-value, 0.0, -value);
      }
   },
   ABOVE {
      @Override
      public Location offset(double value, Location location) {
         return location.clone().add(0.0, value, 0.0);
      }
   },
   BELOW {
      @Override
      public Location offset(double value, Location location) {
         return location.clone().add(0.0, -value, 0.0);
      }
   };

   public abstract Location offset(double value, Location location);
}
