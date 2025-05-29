import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class Product {
    private String name;
    private double price;
    private int expiration;
    private Date productionDate;

    public Product(String name, double price, int expiration, Date productionDate) {
        this.name = name;
        this.price = price;
        this.expiration = expiration;
        this.productionDate = productionDate;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", expiration=" + expiration +
                ", productionDate=" + productionDate +
                '}';
    }
    public boolean isExpired(Date date) {
        // 计算产品过期日期
        Date expirationDate = new Date(productionDate.getTime() + expiration * 24 * 60 * 60 * 1000);
        // 比较当前日期与过期日期
        return date.after(expirationDate);
    }
}
