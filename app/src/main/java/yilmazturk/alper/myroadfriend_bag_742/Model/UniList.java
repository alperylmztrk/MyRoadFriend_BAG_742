package yilmazturk.alper.myroadfriend_bag_742.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import yilmazturk.alper.myroadfriend_bag_742.Model.UniDetail;

public class UniList {

    @SerializedName("universities")
    @Expose
    private List<UniDetail> uniDetailList=null;

    public List<UniDetail> getUniDetailList() {
        return uniDetailList;
    }

    public void setUniDetailList(List<UniDetail> uniDetailList) {
        this.uniDetailList = uniDetailList;
    }
}
