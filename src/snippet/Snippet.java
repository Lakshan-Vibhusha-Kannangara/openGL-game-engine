package snippet;

import engine.animation.KeyFrame;

public class Snippet {
	private KeyFrame[] getPreviousAndNextFrames() {
			KeyFrame[] allFrames = currentAnimation.getKeyFrames();
			KeyFrame previousFrame = allFrames[0];
			KeyFrame nextFrame = allFrames[0];
			for (int i = 1; i < allFrames.length; i++) {
				nextFrame = allFrames[i];
				if (nextFrame.getTimeStamp() > animationTime) {
					break;
				}
				previousFrame = allFrames[i];
			}
			return new KeyFrame[] { previousFrame, nextFrame };
		}

	private KeyFrame[] getPreviousAndNextFrames() {
			KeyFrame[] allFrames = currentAnimation.getKeyFrames();
			KeyFrame previousFrame = allFrames[0];
			KeyFrame nextFrame = allFrames[0];
			for (int i = 1; i < allFrames.length; i++) {
				nextFrame = allFrames[i];
				if (nextFrame.getTimeStamp() > animationTime) {
					break;
				}
				previousFrame = allFrames[i];
			}
			return new KeyFrame[] { previousFrame, nextFrame };
		}
	
		/**
	
		/**
}

