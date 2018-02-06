package com.nomi.smartkeyprogrammer.model;

import java.util.ArrayList;

/**
 * Created by nomi on 2/3/2018.
 */

public class Remote {

    ArrayList<String> page2;
    ArrayList<String> page3;
    ArrayList<String> page8;

    public Remote() {
        page2 = new ArrayList<>();
        page3 = new ArrayList<>();
        page8 = new ArrayList<>();
    }

    public Remote(ArrayList<String> page2, ArrayList<String> page3, ArrayList<String> page8) {
        this.page2 = page2;
        this.page3 = page3;
        this.page8 = page8;
    }

    public ArrayList<String> getPage2() {
        return page2;
    }

    public void setPage2(ArrayList<String> page2) {
        this.page2 = page2;
    }

    public ArrayList<String> getPage3() {
        return page3;
    }

    public void setPage3(ArrayList<String> page3) {
        this.page3 = page3;
    }

    public ArrayList<String> getPage8() {
        return page8;
    }

    public void setPage8(ArrayList<String> page8) {
        this.page8 = page8;
    }

    public String getPage2InString() {
        StringBuilder page2 = new StringBuilder();
        for(String s : getPage2()) {
            page2.append(s);
        }
        return page2.toString();
    }

    public String getPage3InString() {
        StringBuilder page3 = new StringBuilder();
        for(String s : getPage3()) {
            page3.append(s);
        }
        return page3.toString();
    }

    public String getPage8InString() {
        StringBuilder page8 = new StringBuilder();
        for(String s : getPage8()) {
            page8.append(s);
        }
        return page8.toString();
    }
}
