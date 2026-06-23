import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class ScoreManager {
        private final String FILE_NAME = "scores.txt";

        public void saveScore(Score score) {
            try {
                FileWriter writer = new FileWriter(FILE_NAME, true);
                writer.write(score.toFileString() + "\n");
                writer.close();
            } catch (IOException e) {
                System.out.println("Error saving score.");
            }
        }

        public ArrayList<Score> getScores() {
            ArrayList<Score> scores = new ArrayList<>();

            try {
                File file = new File(FILE_NAME);

                if (!file.exists()) {
                    return scores;
                }

                Scanner scanner = new Scanner(file);

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");

                    if (parts.length == 4) {
                        String name = parts[0];
                        int moves = Integer.parseInt(parts[1]);
                        int time = Integer.parseInt(parts[2]);

                        scores.add(new Score(name, moves, time));
                    }
                }

                scanner.close();

            } catch (Exception e) {
                System.out.println("Error reading scores.");
            }

            scores.sort(Comparator.comparingInt(Score::getTotalScore));

            return scores;
        }

        public String getLeaderboardText() {
            ArrayList<Score> scores = getScores();

            if (scores.isEmpty()) {
                return "No scores yet.";
            }

            StringBuilder text = new StringBuilder("Leaderboard:\n\n");

            int rank = 1;

            for (Score score : scores) {
                text.append(rank)
                        .append(". ")
                        .append(score.getPlayerName())
                        .append(" | Moves: ")
                        .append(score.getMoves())
                        .append(" | Time: ")
                        .append(score.getTime())
                        .append("s")
                        .append(" | Score: ")
                        .append(score.getTotalScore())
                        .append("\n");

                rank++;

                if (rank > 10) break;
            }

            return text.toString();
        }
    }

