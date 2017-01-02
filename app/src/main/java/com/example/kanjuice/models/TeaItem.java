package com.example.kanjuice.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TeaItem implements Parcelable {
    public String teaName;
    public boolean isMultiSelected;
    public int selectedQuantity;
    public boolean isSugarless;
    public boolean animate;
    public int imageResId;
    public int kanResId;
    public boolean isFruit;


    public TeaItem(String teaName, int imageId, int kanResId, boolean isSugarless, boolean isFruit) {
        this.teaName = teaName;
        this.imageResId = imageId;
        this.kanResId = kanResId;
        this.isSugarless = isSugarless;
        this.isMultiSelected = false;
        this.selectedQuantity = 1;
        this.isFruit = isFruit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(teaName);
        dest.writeByte((byte) (isSugarless ? 1:0));
        dest.writeByte((byte) (isFruit ? 1 : 0));
        dest.writeInt(selectedQuantity);
    }

    public static final Creator<TeaItem> CREATOR = new Creator<TeaItem>() {
        public TeaItem createFromParcel(Parcel in) {
            return new TeaItem(in);
        }

        public TeaItem[] newArray(int size) {
            return new TeaItem[size];
        }
    };

    private TeaItem(Parcel in) {
        teaName = in.readString();
        isSugarless = in.readByte() != 0;
        isFruit = in.readByte() != 0;
        selectedQuantity = in.readInt();
    }
}
