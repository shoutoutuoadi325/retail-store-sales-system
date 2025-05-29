import lombok.Data;

@Data
public class Sale {
    private String commodity_name;
    private double discount=0;

    public Sale(String commodity_name, double discount) {
        this.commodity_name = commodity_name;
        this.discount = discount;
    }
}
