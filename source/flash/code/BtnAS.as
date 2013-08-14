package code{
	/*****************************************
	 * Interactivity1 :
	 * Demonstrates movement controlled by buttons.
	 * -------------------
	 * See 1_buttons.fla
	 ****************************************/

	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.display.MovieClip;
	import flash.display.SimpleButton;
	import flash.media.Sound;
	import flash.net.URLRequest;

	public class BtnAS extends MovieClip {
		//*************************
		// Properties:

		public var mp3Path:String="blank";

		//*************************
		// Constructor:

		public function BtnAS() {
			// Respond to mouse events
			btn1.addEventListener(MouseEvent.MOUSE_DOWN,btnHandler,false,0,true);
			mp3Path=this.loaderInfo.parameters.path;
		}

		//*************************
		// Event Handling:

		protected function btnHandler(event:Event):void {
			var snd:Sound=new Sound(new URLRequest(mp3Path));
			snd.play();
		}
	}
}