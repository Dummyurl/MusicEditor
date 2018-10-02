package bsoft.com.musiceditor.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class AudioEntity implements Parcelable, Comparable<AudioEntity> {
    private String id;
    private String nameAudio;
    private String nameArtist;
    private String nameAlbum;
    private String duration;
    private String path;
    private int albumId;
    private String pathImage;
    private String dateModifier;
    private long size;
    private boolean isCheck;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDateModifier() {
        return dateModifier;
    }

    public void setDateModifier(String dateModifier) {
        this.dateModifier = dateModifier;
    }


    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public AudioEntity(String id, String nameAudio, String nameArtist, String nameAlbum, String duration, String path, int albumId, String pathImage, String date) {
        this.id = id;
        this.nameAudio = nameAudio;
        this.nameArtist = nameArtist;
        this.nameAlbum = nameAlbum;
        this.duration = duration;
        this.albumId = albumId;
        this.path = path;
        this.pathImage = pathImage;
        this.dateModifier = date;
    }

    protected AudioEntity(Parcel source) {
        id = source.readString();
        nameAudio = source.readString();
        nameArtist = source.readString();
        nameAlbum = source.readString();
        duration = source.readString();
        albumId = source.readInt();
        path = source.readString();
        pathImage = source.readString();
        dateModifier = source.readString();
        size = source.readLong();
        isCheck = source.readByte() != 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameAudio() {
        return nameAudio;
    }

    public void setNameAudio(String nameAudio) {
        this.nameAudio = nameAudio;
    }

    public String getNameArtist() {
        return nameArtist;
    }

    public void setNameArtist(String nameArtist) {
        this.nameArtist = nameArtist;
    }

    public String getNameAlbum() {
        return nameAlbum;
    }

    public void setNameAlbum(String nameAbum) {
        this.nameAlbum = nameAbum;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AudioEntity> CREATOR = new Creator<AudioEntity>() {
        @Override
        public AudioEntity createFromParcel(Parcel source) {
            return new AudioEntity(source);
        }

        @Override
        public AudioEntity[] newArray(int size) {
            return new AudioEntity[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nameAudio);
        dest.writeString(nameArtist);
        dest.writeString(nameAlbum);
        dest.writeString(duration);
        dest.writeString(path);
        dest.writeInt(albumId);
        dest.writeString(pathImage);
        dest.writeString(dateModifier);
        dest.writeLong(size);
        dest.writeByte((byte) (isCheck ? 1 : 0));
    }

    @Override
    public int compareTo(@NonNull AudioEntity audioEntity) {
        return 0;
    }
}