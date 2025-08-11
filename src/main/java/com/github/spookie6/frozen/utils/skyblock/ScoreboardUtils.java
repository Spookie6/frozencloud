package com.github.spookie6.frozen.utils.skyblock;

import com.github.spookie6.frozen.utils.StringUtils;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.github.spookie6.frozen.Frozen.mc;

public class ScoreboardUtils {
    static final int SIDEBAR_SLOT = 1;

    public static List<String> getScoreboardLines() {
        List<String> lines = new ArrayList<>();
        if (mc.theWorld == null) return lines;

        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard == null) return lines;

        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) return lines;

        List<Score> scores = scoreboard.getSortedScores(objective).stream()
                .filter(score -> score != null && score.getPlayerName() != null && !score.getPlayerName().startsWith("#"))
                .collect(Collectors.toList());

        if (scores.size() > 15) {
            scores = scores.subList(scores.size() - 15, scores.size());
        }

        scores.forEach(score -> {
            ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
            String line = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());
            lines.add(StringUtils.removeUnicode(StringUtils.removeFormatting(line)));
        });

        return lines;
    }

    public static String getScoreboardMatch(Pattern pattern) {
        List<String> scoreboard = getScoreboardLines();

        return scoreboard.stream()
                .filter(x -> pattern.matcher(x).find())
                .findFirst().orElse(null);
    }
}
