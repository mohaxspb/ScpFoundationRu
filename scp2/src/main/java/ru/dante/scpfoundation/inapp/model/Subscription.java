package ru.dante.scpfoundation.inapp.model;

/**
 * Created by mohax on 14.01.2017.
 * <p>
 * for scp_ru
 */
public class Subscription {
    public String productId;
    public String price;
    public String title;

    public Subscription(String productId, String price, String title) {
        this.productId = productId;
        this.price = price;
        //TODO move to adapter
        this.title = title.replace("(SCP Foundation RU On/Off-line)", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscription item = (Subscription) o;

        return productId.equals(item.productId);
    }

    @Override
    public int hashCode() {
        return productId.hashCode();
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "productId='" + productId + '\'' +
                ", price='" + price + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}