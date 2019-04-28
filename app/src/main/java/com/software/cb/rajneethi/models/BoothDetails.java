package com.software.cb.rajneethi.models;

/**
 * Created by w7 on 10/8/2017.
 */

public class BoothDetails {

    private String booth_name;
    private boolean is_selected;

    public String getBooth_name() {
        return booth_name;
    }

    public void setBooth_name(String booth_name) {
        this.booth_name = booth_name;
    }

    public boolean is_selected() {
        return is_selected;
    }

    public void setIs_selected(boolean is_selected) {
        this.is_selected = is_selected;
    }

    public BoothDetails(String booth_name, boolean is_selected) {

        this.booth_name = booth_name;
        this.is_selected = is_selected;
    }
}
