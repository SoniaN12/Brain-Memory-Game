import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

public class MemoryGameGUI extends JFrame {
    private ArrayList<Card> cards;
    private JButton[] cardButtons;

    private int firstIndex = -1;
    private int secondIndex = -1;
    private int moves = 0;
    private int matches = 0;
    private int seconds = 0;

    private JLabel movesLabel;
    private JLabel matchesLabel;
    private JLabel timerLabel;
    private JLabel messageLabel;

    private Timer gameTimer;
    private boolean boardLocked = false;

    private String playerName;
    private ScoreManager scoreManager;

    public MemoryGameGUI() {
        playerName = JOptionPane.showInputDialog("Enter your name:");

        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Player";
        }

        scoreManager = new ScoreManager();

        setTitle("Brain Memory Game");
        setSize(600, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createGame();
        setVisible(true);
    }

    private void createGame() {
        cards = new ArrayList<>();
        cardButtons = new JButton[18];

        String[] symbols = {"🍎", "🍊", "🍇", "🍓", "🚓", "🏀", "🎵", "⭐","🎹"};

        for (String symbol : symbols) {
            cards.add(new Card(symbol));
            cards.add(new Card(symbol));
        }

        Collections.shuffle(cards);

        moves = 0;
        matches = 0;
        seconds = 0;
        firstIndex = -1;
        secondIndex = -1;
        boardLocked = false;

        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(4, 1));
        topPanel.setBackground(new Color(30, 42, 56));

        JLabel titleLabel = new JLabel("Brain Memory Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);

        movesLabel = new JLabel("Moves: 0", SwingConstants.CENTER);
        movesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        movesLabel.setForeground(Color.WHITE);

        matchesLabel = new JLabel("Matches: 0 / 8", SwingConstants.CENTER);
        matchesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        matchesLabel.setForeground(Color.WHITE);

        timerLabel = new JLabel("Time: 0s", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setForeground(Color.WHITE);

        topPanel.add(titleLabel);
        topPanel.add(movesLabel);
        topPanel.add(matchesLabel);
        topPanel.add(timerLabel);

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 6, 12, 12));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        boardPanel.setBackground(new Color(240, 244, 248));

        for (int i = 0; i < cardButtons.length; i++) {
            JButton button = new JButton("?");
            button.setFont(new Font("Arial", Font.BOLD, 34));
            button.setBackground(new Color(52, 152, 219));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);

            final int index = i;

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    flipCard(index);
                }
            });

            cardButtons[i] = button;
            boardPanel.add(button);
        }

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(3, 1));
        bottomPanel.setBackground(new Color(240, 244, 248));

        messageLabel = new JLabel("Find all matching pairs!", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton restartButton = new JButton("Restart Game");
        restartButton.setFont(new Font("Arial", Font.BOLD, 18));
        restartButton.setBackground(new Color(46, 204, 113));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);

        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        JButton leaderboardButton = new JButton("View Leaderboard");
        leaderboardButton.setFont(new Font("Arial", Font.BOLD, 18));
        leaderboardButton.setBackground(new Color(241, 196, 15));
        leaderboardButton.setForeground(Color.BLACK);
        leaderboardButton.setFocusPainted(false);

        leaderboardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        scoreManager.getLeaderboardText(),
                        "Leaderboard",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        bottomPanel.add(messageLabel);
        bottomPanel.add(restartButton);
        bottomPanel.add(leaderboardButton);

        add(topPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        startTimer();

        revalidate();
        repaint();
    }

    private void flipCard(int index) {
        if (boardLocked) return;
        if (cards.get(index).isMatched()) return;
        if (index == firstIndex) return;

        cardButtons[index].setText(cards.get(index).getSymbol());
        cardButtons[index].setBackground(new Color(155, 89, 182));

        if (firstIndex == -1) {
            firstIndex = index;
        } else {
            secondIndex = index;
            moves++;
            movesLabel.setText("Moves: " + moves);
            checkMatch();
        }
    }

    private void checkMatch() {
        String firstSymbol = cards.get(firstIndex).getSymbol();
        String secondSymbol = cards.get(secondIndex).getSymbol();

        if (firstSymbol.equals(secondSymbol)) {
            cards.get(firstIndex).setMatched(true);
            cards.get(secondIndex).setMatched(true);

            cardButtons[firstIndex].setBackground(new Color(39, 174, 96));
            cardButtons[secondIndex].setBackground(new Color(39, 174, 96));

            matches++;
            matchesLabel.setText("Matches: " + matches + " / 9");
            messageLabel.setText("Good job! Match found.");

            resetSelection();

            if (matches == 8) {
                gameTimer.stop();

                Score score = new Score(playerName, moves, seconds);
                scoreManager.saveScore(score);

                String rank = getRank();

                messageLabel.setText("You won in " + moves + " moves and " + seconds + " seconds!");

                JOptionPane.showMessageDialog(
                        this,

                        "Congratulations " + playerName + "!\n\n" +
                                "Rank: " + rank + "\n\n" +
                                "Moves: " + moves + "\n" +
                                "Time: " + seconds + " seconds\n" +
                                "Score: " + score.getTotalScore() + "\n\n" +

                                generateFeedback() +

                                "\n\nLeaderboard\n\n" +
                                scoreManager.getLeaderboardText(),

                        "Game Completed",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } else {
            boardLocked = true;
            messageLabel.setText("Not a match. Try again.");

            Timer flipBackTimer = new Timer(800, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cardButtons[firstIndex].setText("?");
                    cardButtons[secondIndex].setText("?");

                    cardButtons[firstIndex].setBackground(new Color(52, 152, 219));
                    cardButtons[secondIndex].setBackground(new Color(52, 152, 219));

                    resetSelection();
                    boardLocked = false;
                }
            });

            flipBackTimer.setRepeats(false);
            flipBackTimer.start();
        }
    }

    private void resetSelection() {
        firstIndex = -1;
        secondIndex = -1;
    }

    private void startTimer() {
        gameTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                seconds++;
                timerLabel.setText("Time: " + seconds + "s");
            }
        });

        gameTimer.start();
    }

    private void restartGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        createGame();

    }

    private String getRank() {

        if (moves <= 12 && seconds <= 60) {
            return "MEMORY MASTER";
        }

        if (moves <= 18 && seconds <= 120) {
            return "MEMORY EXPERT";
        }

        if (moves <= 24) {
            return "MEMORY APPRENTICE";
        }

        return "MEMORY BEGINNER";
    }

    private String generateFeedback() {

        if (moves <= 12 && seconds <= 60) {
            return """
Excellent memory performance!

Suggestions:
• Continue using visual grouping techniques.
• Try higher difficulty levels.
• Challenge yourself to improve your completion time.
""";
        }

        else if (moves <= 18 && seconds <= 120) {
            return """
Good job!

Suggestions:
• Focus on remembering card positions in pairs.
• Scan the board systematically.
• Reduce unnecessary card flips.
""";
        }

        else {
            return """
Keep practicing!

Suggestions:
• Create mental links between matching cards.
• Remember locations before selecting new cards.
• Avoid random clicking.
• Focus on one area of the board at a time.
""";
        }
    }
}