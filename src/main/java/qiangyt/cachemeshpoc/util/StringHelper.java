package qiangyt.cachemeshpoc.util;

import java.util.Collection;
import java.util.Objects;

import com.google.common.collect.Lists;

public class StringHelper {

  public static <T> String toString(T[] array) {
    if (array == null) {
      return null;
    }
    return Lists.newArrayList(array).toString();
  }

  public static <T> String join(String separator, Collection<T> texts) {
    return join(separator, texts.toArray(new String[texts.size()]));
  }

  public static <T> String join(String separator, T[] array) {
    var r = new StringBuilder(array.length * 64);
    var isFirst = true;
    for (var obj : array) {
      if (isFirst) {
        isFirst = false;
      } else {
        r.append(separator);
      }
      r.append(Objects.toString(obj));
    }
    return r.toString();
  }

  public static boolean isBlank(String str) {
    return (str == null || str.length() == 0 || str.trim().length() == 0);
  }

  public static boolean notBlank(String str) {
    return !isBlank(str);
  }

}
