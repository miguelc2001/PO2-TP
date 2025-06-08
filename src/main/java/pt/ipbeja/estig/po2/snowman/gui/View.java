/**
 * Autores:
 * Afonso Freitas - 21467
 * Miguel Correia - 21194
 */

package pt.ipbeja.estig.po2.snowman.gui;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import pt.ipbeja.estig.po2.snowman.model.*;

import javafx.scene.image.ImageView;

import java.io.File;
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

        Button restartButton = new Button("Recomeçar");
        restartButton.setOnAction(event -> restartCurrentLevel());

        Button loadMapButton = new Button("Carregar Mapa");
        loadMapButton.setOnAction(event -> loadMap());

        Button exitButton = new Button("Sair");
        exitButton.setOnAction(event -> Platform.exit());

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

        HBox topBox = new HBox();
        topBox.setSpacing(20);
        topBox.getChildren().addAll(boardPane, scorePanel);

        HBox bottomBox = new HBox();
        bottomBox.setSpacing(20);
        bottomBox.getChildren().addAll(moveHistory, showKeybinds());

        HBox buttons = new HBox();
        buttons.setSpacing(20);
        buttons.getChildren().addAll(restartButton, loadMapButton, exitButton);

        VBox layout = new VBox();
        layout.setSpacing(30);
        layout.getChildren().addAll(topBox, bottomBox, buttons);

        this.getChildren().add(layout);
    }

    /**
     * Draws the game board on the UI.
     * Updates the boardPane with the current state of the model.
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
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

    /**
     * Handles the end of the game, shows options to the user, and updates the score panel.
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
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


            ButtonType nextLevelButton = new ButtonType("Próximo nível");
            ButtonType retryButton = new ButtonType("Recomeçar");
            ButtonType exitButton = new ButtonType("Sair", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(nextLevelButton, retryButton, exitButton);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent()) {
                if (result.get() == retryButton) {
                    restartCurrentLevel();
                } else if (result.get() == nextLevelButton) {
                    loadNextLevel();
                } else if (result.get() == exitButton) {
                    Platform.exit();
                }
            }
        });
    }

    /**
     * Restarts the current level, clearing the move history and score panel.
     */
    private void restartCurrentLevel() {
        music.stop();
        moveHistory.clear();
        scorePanel.getChildren().clear();
        this.model = BoardModel.createBoard(currentLevel);
        this.model.setViewObserver(this);
        drawBoard();
        playBackgroundMusic();
        this.requestFocus();
    }

    /**
     * Loads the next level if available, or shows an error if there are no more levels.
     */
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

    /**
     * Loads a map file selected by the user.
     * Only allows files inside the resources/levels folder.
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
    private void loadMap() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Map File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File initialDir = new File("src/main/resources/levels");
        if (initialDir.exists() && initialDir.isDirectory()) {
            fileChooser.setInitialDirectory(initialDir);
        }

        File file = fileChooser.showOpenDialog(this.getScene().getWindow());
        if (file != null) {
            // Only allow files inside src/main/resources/levels
            String absPath = file.getAbsolutePath().replace("\\", "/");
            String projectPath = new File("").getAbsolutePath().replace("\\", "/");
            String resourcePrefix = projectPath + "/src/main/resources";
            if (absPath.startsWith(resourcePrefix)) {
                String resourcePath = absPath.substring(resourcePrefix.length());
                currentLevel = resourcePath;
                restartCurrentLevel();
            } else {
                showError("Only maps inside the resources/levels folder can be loaded.");
            }
        }
    }

    /**
     * Appends a monster move to the move history.
     * @param from The starting position of the monster.
     * @param direction The direction the monster moved.
     */
    public void monsterMoved(Position from, Direction direction) {
        Position to = from.newPosition(direction);
        String moveText = String.format("(%d, %s) -> (%d, %s)",
                from.getRow(), (char) ('A' + from.getCol()),
                to.getRow(), (char) ('A' + to.getCol()));

        moveHistory.appendText(moveText + "\n");
    }

    /**
     * Loads an image from the resources/images folder.
     * @param name The name of the image file.
     * @return The loaded Image.
     */
    private Image load(String name) {
        return new Image(getClass().getResourceAsStream("/images/" + name));
    }

    /**
     * Gets the image for a board tile based on its position.
     * @param pos The position on the board.
     * @return The Image for the tile.
     */
    private Image getTileImage(Position pos) {
        return switch (model.getPositionContent(pos)) {
            case SNOW -> load("SNOW.png");
            case NO_SNOW -> load("NO_SNOW.png");
            case BLOCK -> load("BLOCK.png");
            case SNOWMAN -> load("SNOWMAN.png");
        };
    }

    /**
     * Gets the image for a snowball based on its size.
     * @param size The size of the snowball.
     * @return The Image for the snowball.
     */
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

    /**
     * Saves the current game state to a file.
     * @param snowmanPosition The position where the snowman was created.
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
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

    /**
     * Plays the background music in a loop.
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
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

    /**
     * Asks the player for their name using a dialog.
     * Limits the name to 3 characters.
     * @return The player's name in uppercase.
     */
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

    /**
     * Shows an error message in a dialog.
     * @param msg The error message to display.
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }


    /**
     * Updates the score panel with the current and top scores.
     * @param currentScore The score of the current game.
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
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

    /**
     * Gets the file name for saving scores for the current level.
     * @return The score file name.
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
    private String getScoreFileName() {
        String levelName = BoardModel.getLevelName(currentLevel);
        String safeLevelName = levelName.replaceAll("[^a-zA-Z0-9]", "_");
        return "scores_" + safeLevelName + ".txt";
    }

    /**
     * Loads the list of scores from the score file.
     * @return A list of Score objects.
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
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

    /**
     * Saves the list of scores to the score file.
     * @param scores The list of scores to save.
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
    private void saveScores(List<Score> scores) {
        try (PrintWriter out = new PrintWriter(getScoreFileName())) {
            for (Score s : scores) {
                out.println(s.getPlayerName() + ";" + s.getLevelName() + ";" + s.getMoves());
            }
        } catch (IOException e) {
            System.err.println("Erro a guardar scores: " + e.getMessage());
        }
    }

    /**
     * Shows the keybinds for the game.
     * @return A VBox containing the keybinds.
     */
    private VBox showKeybinds() {
        VBox keybinds = new VBox();
        keybinds.setSpacing(10);
        keybinds.getChildren().add(new Label("Controlos:"));
        keybinds.getChildren().add(new Label("↑, ↓, ←, → : Mover o monstro"));
        keybinds.getChildren().add(new Label("Z: Undo"));
        keybinds.getChildren().add(new Label("X: Redo"));
        return keybinds;
    }

    /**
     * Called when the model is updated. Redraws the board.
     */
    public void modelUpdated() {
        drawBoard();
    }
}