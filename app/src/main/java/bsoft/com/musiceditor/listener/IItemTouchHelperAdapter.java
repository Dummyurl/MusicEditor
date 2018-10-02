package bsoft.com.musiceditor.listener;


public interface IItemTouchHelperAdapter {
    void onItemDismiss(int position);
    boolean onItemMove(int fromPosition, int toPosition);
}
