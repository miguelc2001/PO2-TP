package pt.ipbeja.estig.po2.snowman.gui;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import pt.ipbeja.estig.po2.snowman.model.*;

import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.sound.sampled.*;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;

public class View extends VBox implements ViewObserver {

    private BoardModel model;
    private GridPane boardPane = new GridPane();
    private TextArea moveHistory = new TextArea();
    private Clip music;

    private String playerName;

    private String currentLevel = "/levels/level1.txt";

    private final List<String> levels = List.of("/levels/level1.txt", "/levels/level2.txt", "/levels/level3.txt");
    private int currentLevelIndex = 0;

    VBox scorePanel = new VBox();

    private final int CELL_SIZE = 64;

    public View() {

        this.playerName = askPlayerName();

        this.model = BoardModel.createBoard(currentLevel);
        this.model.setViewObserver(this);
        this.setFocusTraversable(true);
        this.setSpacing(10);

        moveHistory.setEditable(false);
        moveHistory.setPrefHeight(200);

        this.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) model.moveMonster(Direction.UP);
            if (event.getCode() == KeyCode.DOWN) model.moveMonster(Direction.DOWN);
            if (event.getCode() == KeyCode.LEFT) model.moveMonster(Direction.LEFT);
            if (event.getCode() == KeyCode.RIGHT) model.moveMonster(Direction.RIGHT);
            if (event.getCode() == KeyCode.Z) model.undo();
            if (event.getCode() == KeyCode.X) model.redo();

            drawBoard();
        });

        drawBoard();
        playBackgroundMusic();

        HBox layout = new HBox();
        layout.setSpacing(30);
        layout.getChildren().addAll(boardPane, scorePanel);
        this.getChildren().addAll(layout, moveHistory);
    }

    private void drawBoard() {
        boardPane.getChildren().clear();

        int rows = model.getBoard().size();
        int cols = model.getBoard().get(0).size();

        // Letras no topo das colunas
        for (int col = 0; col < cols; col++) {
            Label colLabel = new Label(String.valueOf((char) ('A' + col)));
            colLabel.setMinSize(CELL_SIZE, CELL_SIZE);
            colLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
            boardPane.add(colLabel, col + 1, 0); // colunas começam na coluna 1
        }

        // Números no início das linhas
        for (int row = 0; row < rows; row++) {
            Label rowLabel = new Label(String.valueOf(row));
            rowLabel.setMinSize(CELL_SIZE, CELL_SIZE);
            rowLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
            boardPane.add(rowLabel, 0, row + 1); // linhas começam na linha 1
        }

        // Células do tabuleiro
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position pos = new Position(row, col);
                ImageView baseImage = new ImageView(getTileImage(pos));
                baseImage.setFitWidth(CELL_SIZE);
                baseImage.setFitHeight(CELL_SIZE);

                StackPane cell = new StackPane();
                cell.getChildren().add(baseImage);

                Snowball snowball = model.getSnowball(pos);
                Position monsterPos = model.getMonster().getPosition();

                if (monsterPos.equals(pos)) {
                    ImageView monster = new ImageView(load("monster.png"));
                    monster.setFitWidth(CELL_SIZE);
                    monster.setFitHeight(CELL_SIZE);
                    cell.getChildren().add(monster);
                }

                else if (snowball != null) {
                    ImageView ball = new ImageView(getBallImage(snowball.getSize()));
                    ball.setFitWidth(CELL_SIZE);
                    ball.setFitHeight(CELL_SIZE);
                    cell.getChildren().add(ball);
                }

                boardPane.add(cell, col + 1, row + 1); // tabuleiro começa em (1,1)
            }
        }
    }

    @Override
    public void gameOver() {

        int moveCount = (int) moveHistory.getText().lines().count();
        Score currentScore = new Score(playerName, BoardModel.getLevelName(currentLevel), moveCount);
        updateScorePanel(currentScore);

        music.stop();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Fim do Jogo");
            alert.setHeaderText("Parabéns! Construíste o boneco de neve completo!");
            alert.setContentText("O que pretendes fazer a seguir?");

            ButtonType retryButton = new ButtonType("Recomeçar");
            ButtonType nextLevelButton = new ButtonType("Próximo nível");
            ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(retryButton, nextLevelButton, cancelButton);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent()) {
                if (result.get() == retryButton) {
                    restartCurrentLevel();
                } else if (result.get() == nextLevelButton) {
                    loadNextLevel();
                }
            }
        });
    }

    private void restartCurrentLevel() {
        moveHistory.clear();
        scorePanel.getChildren().clear();
        this.model = BoardModel.createBoard(currentLevel);
        this.model.setViewObserver(this);
        drawBoard();
        playBackgroundMusic();
    }

    private void loadNextLevel() {
        moveHistory.clear();
        scorePanel.getChildren().clear();
        if (currentLevelIndex < levels.size() - 1) {
            currentLevelIndex++;
            currentLevel = levels.get(currentLevelIndex);
            model = BoardModel.createBoard(currentLevel);
            model.setViewObserver(this);
            drawBoard();
            playBackgroundMusic();
        } else {
            showError("Não há mais níveis disponíveis.");
        }
    }

    @Override
    public void monsterMoved(Position from, Direction direction) {
        Position to = from.newPosition(direction);
        String moveText = String.format("(%d, %s) -> (%d, %s)",
                from.getRow(), (char) ('A' + from.getCol()),
                to.getRow(), (char) ('A' + to.getCol()));

        moveHistory.appendText(moveText + "\n");
    }

    private Image load(String name) {
        return new Image(getClass().getResourceAsStream("/images/" + name));
    }

    private Image getTileImage(Position pos) {
        return switch (model.getPositionContent(pos)) {
            case SNOW -> load("SNOW.png");
            case NO_SNOW -> load("NO_SNOW.png");
            case BLOCK -> load("BLOCK.png");
            case SNOWMAN -> load("SNOWMAN.png");
        };
    }

    private Image getBallImage(SnowballSize size) {
        return switch (size) {
            case SMALL -> load("SMALL.png");
            case AVERAGE -> load("AVERAGE.png");
            case BIG -> load("BIG.png");
            case BIG_AVERAGE -> load("BIG_AVERAGE.png");
            case BIG_SMALL -> load("BIG_SMALL.png");
            case AVERAGE_SMALL -> load("AVERAGE_SMALL.png");
            case BIG_AVERAGE_SMALL -> load("SNOWMAN.png");
        };
    }

    public void saveToFile(Position snowmanPosition) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String fileName = "snowman" + timestamp + ".txt";

        try (PrintWriter writer = new PrintWriter(fileName)) {
            // 1. Mapa utilizado (original)
            writer.println("Mapa:");
            try (var is = getClass().getResourceAsStream(currentLevel);
                 var reader = new java.io.BufferedReader(new java.io.InputStreamReader(is))) {
                reader.readLine(); // skip level name
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.println(line);
                }
            }

            // 2. Conteúdo do painel de jogadas
            writer.println("\nJogadas:");
            writer.println(moveHistory.getText());

            // 3. Quantidade de movimentos
            long totalMoves = moveHistory.getText().lines().count();
            writer.println("Total de movimentos: " + totalMoves);

            // 4. Posição do boneco de neve
            writer.println("Boneco criado em: (" + snowmanPosition.getRow() + ", " + (char)('A' + snowmanPosition.getCol()) + ")");

            System.out.println("Ficheiro gravado: " + fileName);
        } catch (IOException e) {
            System.err.println("Erro a gravar o ficheiro: " + e.getMessage());
        }
    }

    public void playBackgroundMusic() {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    getClass().getResource("/audio/song.wav"));

            music = AudioSystem.getClip();
            music.open(audioStream);
            music.loop(Clip.LOOP_CONTINUOUSLY);
            music.start();
        } catch (Exception e) {
            System.err.println("Erro ao tocar música: " + e.getMessage());
        }
    }

    private String askPlayerName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nome do Jogador");
        dialog.setHeaderText("Introduz o teu nome (máx. 3 caracteres):");
        dialog.setContentText("Nome:");

        // Limita o input a 3 caracteres
        TextField editor = dialog.getEditor();
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            return newText.length() <= 3 ? change : null;
        };
        editor.setTextFormatter(new TextFormatter<>(filter));

        while (true) {
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String name = result.get().trim();
                if (name.isEmpty()) {
                    showError("O nome não pode estar vazio.");
                } else {
                    return name.toUpperCase();
                }
            } else {
                // Cancelado → fechar o jogo
                Platform.exit();
                return null; // opcional (linha nunca executada, mas necessário para compilar)
            }
        }
    }


    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }



    private void updateScorePanel(Score currentScore) {
        List<Score> topScores = loadScores();
        if (currentScore != null) {
            topScores.add(currentScore);
            topScores.sort(Comparator.naturalOrder());
            if (topScores.size() > 3) {
                topScores = topScores.subList(0, 3);
            }
            saveScores(topScores);
        }

        scorePanel.getChildren().clear();

        // 1. Show current game score
        if (currentScore != null) {
            scorePanel.getChildren().add(new Label("Current score: " + currentScore.getPlayerName() +
                    " - " + currentScore.getMoves() + " moves"));
        }

        // 2. Show level name
        String levelName = BoardModel.getLevelName(currentLevel);
        scorePanel.getChildren().add(new Label("Level: " + levelName));
        scorePanel.getChildren().add(new Label("Top 3 players:"));

        // 3. Show top 3 players
        for (Score s : topScores) {
            String label = s.getPlayerName() + " - " + s.getMoves() + " moves";
            if (currentScore != null &&
                    s.getPlayerName().equals(currentScore.getPlayerName()) &&
                    s.getMoves() == currentScore.getMoves()) {
                label += " TOP";
            }
            scorePanel.getChildren().add(new Label(label));
        }
    }

    private String getScoreFileName() {
        String levelName = BoardModel.getLevelName(currentLevel);
        String safeLevelName = levelName.replaceAll("[^a-zA-Z0-9]", "_");
        return "scores_" + safeLevelName + ".txt";
    }

    private List<Score> loadScores() {
        List<Score> scores = new ArrayList<>();
        Path path = Path.of(getScoreFileName());

        if (!Files.exists(path)) return scores;

        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    scores.add(new Score(parts[0], parts[1], Integer.parseInt(parts[2])));
                }
            }
        } catch (IOException e) {
            System.err.println("Erro a ler scores: " + e.getMessage());
        }

        return scores;
    }

    private void saveScores(List<Score> scores) {
        try (PrintWriter out = new PrintWriter(getScoreFileName())) {
            for (Score s : scores) {
                out.println(s.getPlayerName() + ";" + s.getLevelName() + ";" + s.getMoves());
            }
        } catch (IOException e) {
            System.err.println("Erro a guardar scores: " + e.getMessage());
        }
    }




    @Override
    public void modelUpdated() {
        drawBoard();
    }


}