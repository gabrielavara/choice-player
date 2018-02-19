package com.gabrielavara.choiceplayer.beatport;

public class LevenshteinDistance {
    private LevenshteinDistance() {
    }

    public static int calculate(String source, String target) {
        source = source.toLowerCase();
        target = target.toLowerCase();

        if (source.isEmpty()) {
            return target.isEmpty() ? 0 : target.length();
        }
        if (target.isEmpty()) {
            return source.length();
        }

        if (source.length() > target.length()) {
            String temp = target;
            target = source;
            source = temp;
        }

        int m = target.length();
        int n = source.length();
        int[][] distance = new int[2][m + 1];
        for (int j = 1; j <= m; j++) {
            distance[0][j] = j;
        }

        int currentRow = 0;
        for (int i = 1; i <= n; ++i) {
            currentRow = i & 1;
            distance[currentRow][0] = i;
            int previousRow = currentRow ^ 1;
            for (int j = 1; j <= m; j++) {
                int cost = (target.charAt(j - 1) == source.charAt(i - 1) ? 0 : 1);
                distance[currentRow][j] = Math.min(Math.min(
                        distance[previousRow][j] + 1,
                        distance[currentRow][j - 1] + 1),
                        distance[previousRow][j - 1] + cost);
            }
        }
        return distance[currentRow][m];
    }
}
