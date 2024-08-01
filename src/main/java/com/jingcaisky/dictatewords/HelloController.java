package com.jingcaisky.dictatewords;

import com.jingcaisky.dictatewords.bean.CommonService;
import com.jingcaisky.dictatewords.bean.SpeechService;
import com.jingcaisky.dictatewords.domain.Words;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class HelloController implements Initializable {

    @FXML
    private TableView<Words> tbWords;
    @FXML
    private TextField spCount;
    @FXML
    private TextField spRepeat;
    @FXML
    private TextField spSleep;
    @FXML
    private TextField spWordSleep;
    @FXML
    private ChoiceBox<String> slType;
    @FXML
    private Button btChinese;
    @FXML
    private Button btEnglish;
    @FXML
    private Button btGenerate;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    /**
     * 当生成按钮被点击时执行的操作。
     * 清空词语列表表格，根据用户输入的数量从数据库中查询新概念英语单词，并显示在表格中。
     */
    public void onBtGenerateClick() {
        // 清空表格中的列
        tbWords.getColumns().clear();
        // 创建一个可观察的列表，用于存储单词数据
        ObservableList<Words> datas = FXCollections.observableArrayList();
        // 获取用户输入的单词数量
        String countStr = spCount.getText();
        String typeStr = slType.getValue();
        if (countStr == null || typeStr == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText("请填写单词数量并选择单词类型");
            alert.showAndWait();
            return;
        }
        // 创建CommonService实例，用于数据库操作
        CommonService util = new CommonService();
        // 从数据库中查询指定数量的单词
        List<Words> list = util.selectWords(Integer.parseInt(countStr), typeStr);
        Collections.shuffle(list);//乱序
        // 将查询到的单词添加到数据列表中
        for (Words word : list) {
            datas.add(word);
            tbWords.setItems(datas);
        }
        // 创建表格列，用于显示单词的ID、英文、中文和类型
        TableColumn id = new TableColumn("ID");
        TableColumn en = new TableColumn("英语");
        TableColumn zh = new TableColumn("汉语");
        TableColumn type = new TableColumn("类别");
        // 设置各列的数据绑定，以显示对应属性的值
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        en.setCellValueFactory(new PropertyValueFactory<>("en"));
        zh.setCellValueFactory(new PropertyValueFactory<>("zh"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        // 将所有列添加到表格中
        tbWords.getColumns().addAll(id, en, zh, type);
    }

    public void onBtChineseClick() {
        System.out.println("onBtChineseClick");
        if (tbWords.getItems().size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText("请生成听写序列");
            alert.showAndWait();
            return;
        }
        String countRepeat = spRepeat.getText();//听写重复次数
        String countSleep = spSleep.getText();//重复间隔时长
        String countWordSleep = spWordSleep.getText();//单词间间隔时长
        //获取所有备选数据
        ObservableList<Words> datas = tbWords.getItems();
        ArrayList<Words> list = new ArrayList<Words>();//旧数据
        for (Words word : datas) {
//            System.out.println(word.getId() + " ++ " + word.getZh() + " ++ " + word.getEn() + " ++ " + word.getType());
            list.add(word);
        }

        SpeechService.textToSpeech("请准备好,即将开始按照汉语听写单词");
        CompletableFuture.runAsync(() -> {
            try {
                for (Words word : list) {
                    for (int i = 0; i < Integer.parseInt(countRepeat); i++) {
                        SpeechService.textToSpeech(word.getZh());
                        TimeUnit.SECONDS.sleep(Integer.parseInt(countSleep)); // 单词重复停顿
                    }
                    TimeUnit.SECONDS.sleep(Integer.parseInt(countWordSleep)); // 单词重复停顿
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SpeechService.textToSpeech("听写完毕");
        });

    }

    public void onBtEnglishClick() {
        System.out.println("onBtEnglishClick");
        if (tbWords.getItems().size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText("请生成听写序列");
            alert.showAndWait();
            return;
        }
        String countRepeat = spRepeat.getText();//听写重复次数
        String countSleep = spSleep.getText();//重复间隔时长
        String countWordSleep = spWordSleep.getText();//单词间间隔时长
        //获取所有备选数据
        ObservableList<Words> datas = tbWords.getItems();
        ArrayList<Words> list = new ArrayList<Words>();//旧数据
        for (Words word : datas) {
//            System.out.println(word.getId() + " ++ " + word.getZh() + " ++ " + word.getEn() + " ++ " + word.getType());
            list.add(word);
        }

        SpeechService.textToSpeech("Please get ready to start dictating words in English.");
        CompletableFuture.runAsync(() -> {
            try {
                for (Words word : list) {
                    for (int i = 0; i < Integer.parseInt(countRepeat); i++) {
                        SpeechService.textToSpeech(word.getEn());
                        TimeUnit.SECONDS.sleep(Integer.parseInt(countSleep)); // 单词重复停顿
                    }
                    TimeUnit.SECONDS.sleep(Integer.parseInt(countWordSleep)); // 单词重复停顿
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SpeechService.textToSpeech("This dictation is over.");
        });

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CommonService util = new CommonService();
        List<Words> wordsList = util.selectAllWords();
        welcomeText.setText(util.statisticalInformation(wordsList));
        ArrayList<String> types = util.selectTypes(wordsList);
        ObservableList<String> options = FXCollections.observableArrayList(types);
        slType.setItems(options);
    }
}