import javax.swing.*;
import java.awt.*;

public class ArrayDisplay extends JFrame {
    private char[][] grid; // Your 2D array
    private int cellSize = 150;
    public ArrayDisplay(char[][] grid) {
        this.grid = grid;
        setTitle("RaceTrack");
        int width = grid[0].length * cellSize;
        int height = grid.length * cellSize;
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int cellSize = Math.min(getWidth() / grid[0].length, getHeight() / grid.length);

                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[0].length; j++) {
                        if (grid[i][j] == '#') {
                            g.setColor(Color.BLUE);
                        } else if(grid[i][j] == '.'){
                            g.setColor(Color.DARK_GRAY);
                        }
                        else if(grid[i][j] == 'S'){
                            g.setColor(Color.GREEN);
                        }
                        else if (grid[i][j] == 'F'){
                            g.setColor(Color.RED);
                        }

                        int x = j * cellSize;
                        int y = i * cellSize;
                        g.fillRect(x, y, cellSize, cellSize);
                    }
                }
            }
        };

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        Track tr = new Track();
        tr.loadTrack("O");
        char[][] track = tr.getTrack();

        SwingUtilities.invokeLater(() -> {
            new ArrayDisplay(track);
        });
    }
}