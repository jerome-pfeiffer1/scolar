import java.time.LocalTime;

public class ${name} implements timedexpr._generator.ITimedExpr {

  LocalTime c;
  Boolean isAfter;
  Integer hour;
  Integer minute;

/**
* Constructor for ${name}
*/
  public ${name}() {
    hour = ${node.time.hours.value};
    minute = ${node.time.minutes.value};
    isAfter = ${node.later?c};
  }

  public boolean doesHold() {
    if (c.getHour() != hour) {
      if (hour < c.getHour() && isAfter) {
        return true;
      } else if (hour > c.getHour() && !isAfter) {
        return true;
      }
    } else {
      if (minute < c.getMinute() && isAfter) {
        return true;
      } else if (minute > c.getMinute() && !isAfter) {
        return true;
      }
    }
  return false;
  }
}