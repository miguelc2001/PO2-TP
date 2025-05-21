package pt.ipbeja.app.ui;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import pt.ipbeja.app.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class View extends VBox implements ViewObserver {

    private BoardModel model;
    private Label monsterLabel;
    private GridPane boardPane = new GridPane();
    private TextArea moveHistory = new TextArea();


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

        this.getChildren().addAll(boardPane, moveHistory);
    }

    private BoardModel createSampleModel() {
        List<List<PositionContent>> board = new ArrayList<>();

        // Definir o layout do tabuleiro com base em símbolos
        String[] layout = {
                "+++++++",
                "++_._++",
                "+_o.o_+",
                "+_.M._+",
                "+_.oo_+",
                "+_._._+",
                "+++++++"
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

        // Se não definiste o monstro, põe no centro por defeito
        if (monster == null) {
            monster = new Monster(new Position(2, 2));
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
            colLabel.setMinSize(30, 30);
            colLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
            boardPane.add(colLabel, col + 1, 0); // colunas começam na coluna 1
        }

        // Números no início das linhas
        for (int row = 0; row < rows; row++) {
            Label rowLabel = new Label(String.valueOf(row));
            rowLabel.setMinSize(30, 30);
            rowLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
            boardPane.add(rowLabel, 0, row + 1); // linhas começam na linha 1
        }

        // Células do tabuleiro
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position pos = new Position(row, col);
                Label label = new Label();
                label.setMinSize(30, 30);
                label.setStyle("-fx-border-color: gray; -fx-alignment: center;");
                label.setFont(Font.font(20));

                Snowball snowball = model.getSnowball(pos);
                Position monsterPos = model.getMonster().getPosition();

                if (monsterPos.equals(pos)) {
                    label.setText("M");
                } else if (snowball != null) {
                    label.setText(switch (snowball.getSize()) {
                        case SMALL -> "s";
                        case AVERAGE -> "m";
                        case BIG -> "b";
                        case BIG_AVERAGE -> "B+M";
                        case BIG_AVERAGE_SMALL -> "☃";
                        case BIG_SMALL -> "B+S";
                        case AVERAGE_SMALL -> "M+S";
                    });
                } else {
                    PositionContent content = model.getPositionContent(pos);
                    label.setText(switch (content) {
                        case SNOW -> "_";
                        case NO_SNOW -> ".";
                        case BLOCK -> "+";
                        case SNOWMAN -> "☃";
                    });
                }

                boardPane.add(label, col + 1, row + 1); // tabuleiro começa em (1,1)
            }
        }
    }

    @Override
    public void gameOver() {
        Platform.runLater(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
}