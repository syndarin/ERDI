package com.syndarin.erdi.dialogs;

import java.io.File;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Toast;
import android.widget.VideoView;

import com.syndarin.erdi.ERDIActivity;
import com.syndarin.erdi.R;
import com.syndarin.erdi.synchronizer.VideoDownloader;

public class VideoDialog extends Dialog implements OnTouchListener {

	private VideoView player;
	private File videoFile;
	
	public VideoDialog(Context context, String filename) {
		super(context);
		// TODO Auto-generated constructor stub

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.play_video_dialog);
		
		this.player=(VideoView)findViewById(R.id.detailedMediaController);	
		this.player.setOnTouchListener(this);
		this.player.setOnCompletionListener(new PlaybackListener());
		if(filename!=null&&!filename.equals("")){
			this.videoFile=new File(ERDIActivity.STORAGE_ROOT+VideoDownloader.LOCAL_PATH+filename);
			if(videoFile.exists()&& videoFile.length()>0){
				this.player.setVideoPath(this.videoFile.getAbsolutePath());
				this.player.start();
				Log.i("PLAYER", "Video found!");
			}else{
				Toast.makeText(context, "  сожалению, видео дл€ данной модели недоступно!", Toast.LENGTH_SHORT).show();
			}
		}
	
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		this.dismiss();
		return true;
	}
	
	private class PlaybackListener implements OnCompletionListener{

		@Override
		public void onCompletion(MediaPlayer arg0) {
			// TODO Auto-generated method stub
			dismiss();			
		}
		
	}
}
