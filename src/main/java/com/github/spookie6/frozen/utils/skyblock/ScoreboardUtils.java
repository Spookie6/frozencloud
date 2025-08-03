package com.github.spookie6.frozen.utils.skyblock;

import com.github.spookie6.frozen.utils.StringUtils;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.spookie6.frozen.Frozen.mc;

public class ScoreboardUtils {
    static final int SIDEBAR_SLOT = 1;

    public static List<String> getScoreboard() {
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(SIDEBAR_SLOT);
        List<String> scoreList = scoreboard.getSortedScores(objective)
                .stream()
                .limit(15)
                .map(score ->
                        StringUtils.removeUnicode(
                                ScorePlayerTeam.formatPlayerName(
                                        scoreboard.getPlayersTeam(score.getPlayerName()),
                                        score.getPlayerName())))
                .collect(Collectors.toList());
        Collections.reverse(scoreList);
        return scoreList;
    }
}
