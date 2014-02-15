package com.mxia.personal;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.StringTokenizer;

public class ZombieSmasher {

    private static final int TIME_ZOMBIE_LIFE = 1000;
    private static final int TIME_MOVE_ADJ = 100;
    private static final int TIME_RECHARGE = 750;

    public static void main(String[] args) {
	BufferedReader in = null;
	PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
	try {
	    in = new BufferedReader(new InputStreamReader(new FileInputStream(
		    "resources/zombie_input.txt")));
	    String line = in.readLine();
	    if (line != null) {
		int cases = Integer.parseInt(line);
		for (int i = 1; i <= cases; ++i) {
		    solve(i, in, out);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    if (in != null) {
		try {
		    in.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	    out.close();
	}
    }

    private static void solve(int caseNumber, BufferedReader in, PrintWriter out)
	    throws IOException, NumberFormatException {
	int zCount = Integer.parseInt(in.readLine());
	int[] x = new int[zCount];
	int[] y = new int[zCount];
	int[] time = new int[zCount];
	StringTokenizer st;
	for (int i = 0; i < zCount; ++i) {
	    st = new StringTokenizer(in.readLine());
	    x[i] = Integer.parseInt(st.nextToken());
	    y[i] = Integer.parseInt(st.nextToken());
	    time[i] = Integer.parseInt(st.nextToken());
	}

	int[][] minTime = new int[zCount][zCount];

	for (int i = 0; i < zCount; ++i) {
	    for (int j = 0; j < zCount; ++j) {
		minTime[i][j] = Integer.MAX_VALUE;
	    }
	}

	for (int i = 0; i < zCount; ++i) {
	    // compute the time needed to move from (0, 0) to (x[i], y[i])
	    int moveTime = Math.max(Math.abs(x[i]), Math.abs(y[i]))
		    * TIME_MOVE_ADJ;
	    if (moveTime <= time[i] + TIME_ZOMBIE_LIFE) {
		// could get there in time
		minTime[i][0] = Math.max(moveTime, time[i]);
	    }
	}

	for (int index1 = 0; index1 < zCount; ++index1) {
	    for (int index2 = 0; index2 < zCount; ++index2) {
		if (index2 == index1) {
		    continue;
		}
		// time to get from (x[i], y[i]) to (x[j], y[j]) after a zombie
		// was just smashed
		int moveTime = Math.max(
			TIME_RECHARGE,
			Math.max(Math.abs(x[index1] - x[index2]),
				Math.abs(y[index1] - y[index2]))
				* TIME_MOVE_ADJ);
		for (int j = 0; j < zCount; ++j) {
		    if (minTime[index1][j] == Integer.MAX_VALUE) {
			// already not reachable
			continue;
		    }
		    int timeToNextZ = minTime[index1][j] + moveTime;
		    if (timeToNextZ < minTime[index2][j + 1]
			    && timeToNextZ <= time[index2] + TIME_ZOMBIE_LIFE) {
			// found a faster time to kill the next zombie
			minTime[index2][j + 1] = Math.max(timeToNextZ,
				time[index2]);
		    }
		}
	    }
	}

	// matrix is built, with minTime[i][j] representing the minimum time to
	// kill z[i] after already smashing j zombies. Now find the right-most
	// column containing at least one non-default value
	int count = 0;
	for (int j = 0; j < zCount; ++j) {
	    for (int i = 0; i < zCount; ++i) {
		if (minTime[i][j] != Integer.MAX_VALUE) {
		    count++;
		    break;
		}
	    }
	}

	out.println(MessageFormat.format("Case #{0}: {1}", caseNumber, count));
    }
}
