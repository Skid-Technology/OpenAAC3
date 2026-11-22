package me.konsolas.aac.checks.movement;

import java.util.Set;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import me.konsolas.aac.compat.RelativePositionFlag;
import me.konsolas.aac.util.MathUtilities;
public class PendingTeleport {
   final boolean relativeX;
   final boolean relativeY;
   final boolean relativeZ;
   final Vector target;
   final MovementManager movementManager;

   PendingTeleport(MovementManager movementManager, Set set, Vector vector) {
      this.movementManager = movementManager;
      this.relativeX = set.contains(RelativePositionFlag.X);
      this.relativeY = set.contains(RelativePositionFlag.Y);
      this.relativeZ = set.contains(RelativePositionFlag.Z);
      this.target = vector;
   }

   boolean matches(Location location, Location location2) {
      boolean flag;
      label62: {
         label65: {
            if (this.relativeX) {
               double x = location2.getX() - location.getX();
               double x2 = this.target.getX();
               double d = 0.1;
               double d2 = x2;
               double d3 = x;
               if (MathUtilities.approximatelyEquals(d3, d2, d)) {
                  break label65;
               }
            }

            if (!this.relativeX) {
               double x3 = location2.getX();
               double x4 = this.target.getX();
               double d4 = MovementManager.getTpAcceptThreshold(this.movementManager);
               double d5 = x4;
               double d6 = x3;
               if (MathUtilities.approximatelyEquals(d6, d5, d4)) {
                  break label65;
               }
            }

            flag = false;
            break label62;
         }

         flag = true;
      }

      boolean flag2;
      label52: {
         label66: {
            flag2 = flag;
            if (this.relativeY) {
               double y = location2.getY() - location.getY();
               double y2 = this.target.getY();
               double d7 = 0.1;
               double d8 = y2;
               double d9 = y;
               if (MathUtilities.approximatelyEquals(d9, d8, d7)) {
                  break label66;
               }
            }

            if (!this.relativeY) {
               double y3 = location2.getY();
               double y4 = this.target.getY();
               double d10 = MovementManager.getTpAcceptThreshold(this.movementManager);
               double d11 = y4;
               double d12 = y3;
               if (MathUtilities.approximatelyEquals(d12, d11, d10)) {
                  break label66;
               }
            }

            flag = false;
            break label52;
         }

         flag = true;
      }

      boolean flag3;
      label42: {
         label67: {
            flag3 = flag;
            if (this.relativeZ) {
               double z = location2.getZ() - location.getZ();
               double z2 = this.target.getZ();
               double d13 = 0.1;
               double d14 = z2;
               double d15 = z;
               if (MathUtilities.approximatelyEquals(d15, d14, d13)) {
                  break label67;
               }
            }

            if (!this.relativeZ) {
               double z3 = location2.getZ();
               double z4 = this.target.getZ();
               double d16 = MovementManager.getTpAcceptThreshold(this.movementManager);
               double d17 = z4;
               double d18 = z3;
               if (MathUtilities.approximatelyEquals(d18, d17, d16)) {
                  break label67;
               }
            }

            flag = false;
            break label42;
         }

         flag = true;
      }

      boolean flag4 = flag;
      return flag2 && flag3 && flag4;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder()
         .append(this.relativeX ? "dX" : "")
         .append(this.relativeY ? "dY" : "")
         .append(this.relativeZ ? "dZ" : "")
         .append("[");
      Vector vector = this.target;
      Vector vector2 = vector;
      return sb.append(MathUtilities.formatVector(vector2)).append("]").toString();
   }
}
