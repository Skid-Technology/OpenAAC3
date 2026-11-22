package me.konsolas.aac.gui;

public class EmptyContextCallback
implements Callback<MenuClickContext> {
    final ContextClickMenuItem menuItem;

    public EmptyContextCallback(ContextClickMenuItem contextClickMenuItem) {
        this.menuItem = contextClickMenuItem;
    }

    public void noop(MenuClickContext menuClickContext) {
    }

    @Override
    public void call(MenuClickContext menuClickContext) {
        this.noop(menuClickContext);
    }
}
