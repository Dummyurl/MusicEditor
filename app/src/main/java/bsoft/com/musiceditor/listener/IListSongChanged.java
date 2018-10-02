package bsoft.com.musiceditor.listener;


import java.util.List;

import bsoft.com.musiceditor.model.AudioEntity;


public interface IListSongChanged {
    void onNoteListChanged(List<AudioEntity> audioEntities);
}
