package com.jingcaisky.dictatewords.controller;

import com.jingcaisky.dictatewords.HelloApplication;
import com.jingcaisky.dictatewords.bean.CommonService;
import com.jingcaisky.dictatewords.bean.EasilyWrongWordsService;
import com.jingcaisky.dictatewords.bean.SpeechService;
import com.jingcaisky.dictatewords.domain.Words;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class EasilyWrongWordsController implements Initializable {

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
    private ToggleGroup tgType;



    /**
     * 显示自定义的Stage窗口
     * 该方法用于加载一个外部的FXML文件，将其作为场景加载到一个模态对话框中，并显示该对话框
     * 使用了JavaFX的FXMLLoader来加载界面文件，并设置了窗口的模态属性，确保用户必须关闭此窗口才能继续操作主窗口
     * 窗口的标题被设置为"错词听写"，表达了该应用的主要功能
     *
     * @throws IOException 如果无法加载FXML文件或资源，则抛出此异常
     */
    public static void showStage() throws IOException {
        // 创建一个新的Stage对象
        Stage stage=new Stage();
        // 初始化Stage的模态属性，使其成为应用程序模态对话框
        stage.initModality(Modality.WINDOW_MODAL);
        // 创建FXMLLoader对象，用于加载FXML文件
        FXMLLoader fxmlLoader;
        // 实例化FXMLLoader，指定加载的FXML文件路径
        fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("easily-wrong-words-view.fxml"));
        // 使用FXMLLoader加载场景，并设置场景的宽高
        Scene scene = new Scene(fxmlLoader.load(), 960, 680);
        // 设置Stage的标题
        stage.setTitle("错词听写");
        // 设置Stage的场景
        stage.setScene(scene);
        // 设置Stage的大小不可调节
        stage.setResizable(false);
        // 显示Stage并等待，直到它被关闭
        stage.showAndWait();
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

        if (countStr == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText("请填写单词数量");
            alert.showAndWait();
            return;
        }
        String typeStr = tgType.getSelectedToggle().getUserData().toString();
        System.out.println(typeStr);
        // 创建EasilyWrongWordsService实例，用于数据库操作
        EasilyWrongWordsService easilyWrongWordsService = new EasilyWrongWordsService();
        if(typeStr.equals("radomWrongWords")){//随机错词
            // 从数据库中查询指定数量的单词
            List<Words> list = easilyWrongWordsService.selectAllEasilyWrongWords();
            Collections.shuffle(list);//乱序
            if (list.size() > Integer.parseInt(countStr)){
                list = list.subList(0, Integer.parseInt(countStr));
            }
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
            TableColumn errorCount = new TableColumn("错次");
            // 设置各列的数据绑定，以显示对应属性的值
            id.setCellValueFactory(new PropertyValueFactory<>("id"));
            en.setCellValueFactory(new PropertyValueFactory<>("en"));
            zh.setCellValueFactory(new PropertyValueFactory<>("zh"));
            type.setCellValueFactory(new PropertyValueFactory<>("type"));
            errorCount.setCellValueFactory(new PropertyValueFactory<>("errorCount"));
            // 将所有列添加到表格中
            tbWords.getColumns().addAll(id, en, zh, type, errorCount);
        }else if(typeStr.equals("highWrongWords")){ //高频错词

        }

    }

    /**
     * 汉语听写按钮被点击时执行的操作。
     */
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

    /**
     * 英语听写按钮被点击时执行的操作。
     */
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
        tbWords.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // 双击操作
                Words word = tbWords.getSelectionModel().getSelectedItem();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("易错词");
                alert.setContentText("是否确定将该单词存入‘易错词库’？");
                Optional<ButtonType> buttonType = alert.showAndWait();
                // 判断返回的按钮类型是确定还是取消，再据此分别进一步处理
                if (buttonType.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) { // 单击了确定按钮OK_DONE
                    EasilyWrongWordsService easilyWrongWordsService = new EasilyWrongWordsService();
                    if (easilyWrongWordsService.setErrorWord(word) > 0) {
                        System.out.println("设置错词成功");
                    } else {
                        System.out.println("插入失败");
                    }
                } else { // 单击了取消按钮CANCEL_CLOSE
                    System.out.println("您选择了“取消”按钮。");
                }
            }
        });
    }
}