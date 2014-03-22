package ru.andrey.bullsncows;

import java.util.Arrays;

public class BullsAndCows implements Runnable {

  int n;
  int size;
  int[][] all;
  int last = 0;
  int remaining;
  int first = 0;
  int[] next;
  int[] groups;

  public BullsAndCows(int n) {
    this.n = n;
    size = 1;
    for (int i = 10; i > 10 - n; i--) {
      size *= i;
    }
    all = new int[size][];
    next = new int[size];
    groups = new int[10 * this.n + 1];
    getPlacements(1, new int[n + 1], new boolean[10]);
  }

  void getPlacements(int pos, int[] digits, boolean[] used) {
    if (pos == n + 1) {
      all[last++] = Arrays.copyOf(digits, digits.length);
    } else {
      for (int i = 0; i < 10; i++) {
        if (used[i]) continue;
        used[i] = true;
        digits[0] += 1 << i;
        digits[pos] = i;
        getPlacements(pos + 1, digits, used);
        digits[0] -= 1 << i;
        used[i] = false;
      }
    }
  }

  int match(int[] secret, int[] number) {
    int result = 0;
    for (int i = 1; i < secret.length; i++) {
      if (secret[i] == number[i]) {
        result += 10;
      } else if ((secret[0] & (1 << number[i])) != 0) {
        result++;
      }
    }
    return result;
  }

  int play(int[] secret) {
    first = 0;
    for (int i = 0; i < size - 1; i++) next[i] = i + 1;
    next[size - 1] = -1;
    remaining = size;

    int[] number = all[0];
    int turnCount = 0;
    while (true) {
      turnCount++;
      int result = match(secret, number);
      //System.out.println("select: " + number + ", " + result);
      if (result == 10 * n) return turnCount;
      filter(number, result);
      //System.out.println("remaining: " + remaining);
      number = select();
    }
  }

  private int[] select() {
    if (remaining == 1) return all[first];
    int min = remaining;
    int[] best = null;
    for (int[] variant : all) {
      int max = 0;
      Arrays.fill(groups, 0);
      for (int n = first; n != -1; n = next[n]) {
        int i = match(all[n], variant);
        groups[i]++;
        if (groups[i] > max) {
          max = groups[i];
          if (max >= min) break;
        }
      }
      if (max < min) {
        min = max;
        best = variant;
      }
    }
    if (best == null) throw new IllegalStateException();
    return best;
  }

  private void filter(int[] number, int result) {
    remaining = 0;
    int last = -1;
    for (int n = first; n != -1; n = next[n]) {
      if (match(all[n], number) == result) {
        if (last == -1) {
          first = n;
        } else {
          next[last] = n;
        }
        last = n;
        remaining++;
      }
    }
    if (last != -1) {
      next[last] = -1;
    } else {
      first = -1;
    }
  }

  @Override
  public void run() {
    int[] turnsCount = new int[10];
    for (int[] secret : all) {
      int count = play(secret);
      turnsCount[count]++;
      //System.out.println("secret: " + Arrays.toString(secret) + ", turns: " + turns);
    }
    System.out.println("Total:");
    for (int i = 1; i < 10; i++) {
      System.out.println(i + ": " + turnsCount[i]);
    }
  }

  public static void main(String[] args) {
    new BullsAndCows(3).run();
  }
}
