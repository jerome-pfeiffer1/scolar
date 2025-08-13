package util;

import java.util.Objects;

public class Pair<K, V> {
  private final K firstValue;
  private final V secondValue;

  public Pair(K firstValue, V secondValue) {

    this.firstValue = firstValue;
    this.secondValue = secondValue;
  }

  public K getFirstValue() {
    return firstValue;
  }

  public V getSecondValue() {
    return secondValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Pair)) {
      return false;
    }
    final Pair<?, ?> pair = (Pair<?, ?>) o;
    return firstValue.equals(pair.firstValue) &&
        secondValue.equals(pair.secondValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstValue, secondValue);
  }

  @Override
  public String toString() {
    return "(" +
        firstValue +
        ", " + secondValue +
        ')';
  }
}
