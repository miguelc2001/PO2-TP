package pt.ipbeja.app.ui;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import pt.ipbeja.app.model.*;

import javafx.scene.image.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sound.sampled.*;



public class View extends VBox implements ViewObserver {

    private BoardModel model;
    private Label monsterLabel;
    private GridPane boardPane = new GridPane();
    private TextArea moveHistory = new TextArea();
    private Clip music;


    private final int CELL_SIZE = 64;


    public View() {
        this.model = createSampleModel();
        this.model.setViewObserver(this);
        this.setFocusTraversable(true);
        this.setSpacing(10);

        moveHistory.setEditable(false);
        moveHistory.setPrefHeight(100);

        this.setOnKeyPressed(event -> {
//            System.out.println("Tecla: " + event.getCode());

            if (event.getCode() == KeyCode.UP) model.moveMonster(Direction.UP);
            if (event.getCode() == KeyCode.DOWN) model.moveMonster(Direction.DOWN);
            if (event.getCode() == KeyCode.LEFT) model.moveMonster(Direction.LEFT);
            if (event.getCode() == KeyCode.RIGHT) model.moveMonster(Direction.RIGHT);
            drawBoard();
        });

        drawBoard();
        playBackgroundMusic();

        this.getChildren().addAll(boardPane, moveHistory);
    }

    private BoardModel createSampleModel() {
        List<List<PositionContent>> board = new ArrayList<>();

        // Definir o layout do tabuleiro com base em símbolos
        String[] layout = {
                "+++++++++",
                "+_______+",
                "+_____o_+",
                "+_o_____+",
                "+_____o_+",
                "+_______+",
                "+++++++++"
        };

        List<Snowball> snowballs = new ArrayList<>();
        Monster monster = null;

        for (int row = 0; row < layout.length; row++) {
            List<PositionContent> rowContent = new ArrayList<>();
            for (int col = 0; col < layout[row].length(); col++) {
                char tile = layout[row].charAt(col);
                Position pos = new Position(row, col);

                switch (tile) {
                    case '_' -> rowContent.add(PositionContent.SNOW);
                    case '+' -> rowContent.add(PositionContent.BLOCK);
                    case 'o' -> {
                        rowContent.add(PositionContent.NO_SNOW); // o chão da bola
                        snowballs.add(new Snowball(pos, SnowballSize.SMALL));
                    }
                    case 'M' -> {
                        rowContent.add(PositionContent.NO_SNOW);
                        monster = new Monster(pos);
                    }
                    default -> rowContent.add(PositionContent.NO_SNOW);
                }
            }
            board.add(rowContent);
        }


        if (monster == null) {
            monster = new Monster(new Position(board.size() / 2, board.get(0).size() / 2));
        }

        return new BoardModel(board, monster, snowballs);
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
        this.model = createSampleModel();
        this.model.setViewObserver(this);
        drawBoard();
    }

    private void loadNextLevel() {
        // TODO: PROXIMO NÍVEL
        this.model = createSampleModel();
        this.model.setViewObserver(this);
        drawBoard();
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
            // 1. Mapa utilizado
            writer.println("Mapa:");
            for (List<PositionContent> row : model.getBoard()) {
                for (PositionContent pc : row) {
                    writer.print(switch (pc) {
                        case SNOW -> "_";
                        case NO_SNOW -> ".";
                        case BLOCK -> "+";
                        case SNOWMAN -> "☃";
                    });
                }
                writer.println();
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

}