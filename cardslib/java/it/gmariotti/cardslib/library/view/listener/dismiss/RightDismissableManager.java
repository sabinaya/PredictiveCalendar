package it.gmariotti.cardslib.library.view.listener.dismiss;

import it.gmariotti.cardslib.library.view.listener.dismiss.DefaultDismissableManager;

public class RightDismissableManager extends DefaultDismissableManager {

	@Override
	public SwipeDirection getSwipeDirectionAllowed() {
		return SwipeDirection.RIGHT;
	}
}