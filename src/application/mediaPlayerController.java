package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebView;
import javafx.util.Duration;

public class mediaPlayerController implements Initializable{
	
	@FXML
	private Pane pane;
	@FXML
	private Label songLabel;
	@FXML
	private Button playButton, pauseButton, resetButton, previousButton, nextButton;
	@FXML
	private ComboBox<String> speedBox;
	@FXML
	private Slider volumeSlider;
	@FXML
	private ProgressBar songProgressBar;
	

	@FXML
    private ListView<String> muzikliste;


	
	private Media media;
	private MediaPlayer mediaPlayer;
	
	private File directory;
	private File[] files;
	
	private ArrayList<File> songs;
	
	private int songNumber;
	private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};
	
	private Timer timer;
	private TimerTask task;
	
	private boolean running;
	
	 
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		songs = new ArrayList<File>();
		
		directory = new File("music");
		
		files = directory.listFiles();
		
		

		if(files != null) {
			for(File file : files) {
				
				songs.add(file);
				muzikliste.getItems().add(file.getName());
				
			}
		}
		media = new Media(songs.get(songNumber).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		
		songLabel.setText(songs.get(songNumber).getName());
		
		for(int i = 0; i < speeds.length; i++) {
			
			speedBox.getItems().add(Integer.toString(speeds[i])+"%");
		}
		
		speedBox.setOnAction(this::changeSpeed);
		
		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				
				mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);			
			}
		});
		
		songProgressBar.setStyle("-fx-accent: #00FF00;");
		
		//tab2 initalizer
		youtubelinkCb.getItems().add("Aşırı Pahalı Aşırı Lezzetli Mi ? Yurtdışı Atıştırmalık Testi");
		youtubelinkCb.getItems().add("Edis - Martılar");
		youtubelinkCb.getItems().add("Eternals");
		youtubelinkCb.getItems().add("Gezegenin Kara Kutusu: Antarktika | TRT Belgesel");
		youtubelinkCb.getItems().add("Hem Cennet Hem Cehennem \" İZLANDA \" Bu Dünyadan Olmayan Ülke!");
		youtubelinkCb.getItems().add("Tutankhamun'un Kehaneti: Mit ve Bilim");
		youtubelinkCb.getItems().add("Gizemli Tarih: Göbeklitepe | TRT Belgesel");
		youtubelinkCb.getItems().add("Mavi Tutku - Van Gölü'nün Gizemleri");
		youtubelinkCb.getItems().add("2025'teki 5 teknolojik gelişme");
		youtubelinkCb.getItems().add("");
		youtubelinkCb.getItems().add("");
		youtubelinkCb.getItems().add("");
		youtubelinkCb.getItems().add("Gezegenin Kara Kutusu: Antarktika | TRT Belgesel");

		  
		   

	}

	public void playMedia() {

		beginTimer();
		changeSpeed(null);
		mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
		mediaPlayer.play();
	}
	
	public void pauseMedia() {
		
		cancelTimer();
		mediaPlayer.pause();
	}
	
	public void resetMedia() {
		
		songProgressBar.setProgress(0);
		mediaPlayer.seek(Duration.seconds(0));
	}
	
	public void previousMedia() {
		
		if(songNumber > 0) {
			
			songNumber--;
			
			mediaPlayer.stop();
			
			if(running) {
				
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());


			
			playMedia();
		}
		else {
			
			songNumber = songs.size() - 1;
			
			mediaPlayer.stop();
			
			if(running) {
				
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			
			playMedia();
		}
	}
	
	public void nextMedia() {
		
		if(songNumber < songs.size() - 1) {
			
			songNumber++;
			
			mediaPlayer.stop();
			
			if(running) {
				
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			
			playMedia();
		}
		else {
			
			songNumber = 0;
			
			mediaPlayer.stop();
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			
			playMedia();
		}
	}
	
	public void changeSpeed(ActionEvent event) {
		
		if(speedBox.getValue() == null) {
			
			mediaPlayer.setRate(1);
		}
		else {
			
			//mediaPlayer.setRate(Integer.parseInt(speedBox.getValue()) * 0.01);
			mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
		}
	}
	
	public void beginTimer() {
		
		timer = new Timer();
		
		task = new TimerTask() {
			
			public void run() {
				
				running = true;
				double current = mediaPlayer.getCurrentTime().toSeconds();
				double end = media.getDuration().toSeconds();
				songProgressBar.setProgress(current/end);
				
				if(current/end == 1) {
					
					cancelTimer();
				}
			}
		};
		
		timer.scheduleAtFixedRate(task, 0, 1000);
	}
	
	public void cancelTimer() {
		
		running = false;
		timer.cancel();
	}
	
	
	
	//tab1end
	@FXML
    private WebView youtube;
	
    @FXML
    private ComboBox<String> youtubelinkCb;
	 
 
    @FXML
    private Button baslat;

    
    
    
   @SuppressWarnings("null")
@FXML
   void baslatClick(ActionEvent event) {
	   

	   ArrayList<String> links = new ArrayList<String>();
	   links.add("https://www.youtube.com/embed/bxQAWoJzlQw");
	   links.add("https://www.youtube.com/embed/NPUTdqYUa9A");
	   links.add("https://www.youtube.com/embed/UwcFvKjuHxM");
	   links.add("https://www.youtube.com/embed/pUeLhNWkZsE");
	   links.add("https://www.youtube.com/embed/EV0dDJmmdzo");
	   links.add("https://www.youtube.com/embed/_kkIKcwms5I");
	   links.add("https://www.youtube.com/embed/s35TW70K4CU");
	   links.add("https://www.youtube.com/embed/s35TW70K4CU");
	   links.add("https://www.youtube.com/embed/Z1JMZDowu-g");
	   links.add("https://www.youtube.com/embed/gt4QhJluSaA");
	   
	   
	   if(youtubelinkCb.getValue()==null)
	    	youtube.getEngine().load("https://www.youtube.com/");

	   else
	    	youtube.getEngine().load(links.get(youtubelinkCb.getSelectionModel().getSelectedIndex()));

		   
	    }
	
	    
	 
	 
}

