package kr.kaist.resl.kitchenhublauncher.models;

/**
 * Created by NicolaiSonne on 11-06-2015.
 */
public class ListProduct {

    private Integer productId = null;
    private String companyName = null;
    private String itemName = null;
    private Long expirationTime = null;
    private boolean recalled = false;

    public ListProduct(Integer productId, String companyName, String itemName, Long expirationTime, boolean recalled) {
        this.productId = productId;
        this.companyName = companyName;
        this.itemName = itemName;
        this.expirationTime = expirationTime;
        this.recalled = recalled;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getItemName() {
        return itemName;
    }

    public Long getExpirationTime() {
        return expirationTime;
    }

    public boolean isRecalled() {
        return recalled;
    }
}
