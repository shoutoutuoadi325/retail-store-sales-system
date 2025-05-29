import cn.hutool.core.io.resource.ResourceUtil;
import lombok.Data;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Data
public class FamilyMart {
    private List<Product> inventory;
    private Map<String, Double> discounts;

    /**
     * 加载商品
     * @return
     */
    public static List<Product> loadData(){
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = ResourceUtil.getUtf8Reader("purchase.txt");) {
            String line;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                String name = parts[0];
                // 验证价格字段是否为有效的数字格式
                if (parts.length >= 2 && parts[1].matches("^\\d+(\\.\\d+)?$")) {
                    double price = Double.parseDouble(parts[1]);
                    int expiration = Integer.parseInt(parts[2]);
                    Date productionDate = dateFormat.parse(parts[3]);
                    products.add(new Product(name, price, expiration, productionDate));
                } else {
                    System.err.println("Invalid price format in line: " + line);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return products;
    }

    public static void saveData(List<Product> products) {
        String filePath="C:\\Users\\zxyzw\\Desktop\\lab\\src\\main\\resources\\purchase.txt";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Product product : products) {
                String line = product.getName() + "\t" + product.getPrice() + "\t" + product.getExpiration() + "\t" + dateFormat.format(product.getProductionDate());
                bw.write(line);
                bw.newLine();
            }
            System.out.println("Product data saved successfully to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error occurred while saving product data to file: " + e.getMessage());
        }
    }

    /**
     * 删除过期产品
     * @param products
     */
    public static void removeExpiredProducts(List<Product> products,Date date) {
        Iterator<Product> iterator = products.iterator();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            if (!product.isExpired(date)) {
                System.out.println("Removing expired product: " + product.getName());
                iterator.remove();
            }
        }
    }

    /**
     * 加载售卖信息
     */
    private static List<Sale> loadSaleData() {
        List<Sale> sales = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\zxyzw\\Desktop\\lab\\src\\main\\resources\\sell.txt"))) {
            reader.readLine();
            String line;
            String commodityName;
            while ((line = reader.readLine()) != null) {
                // 跳过空白行
                double discount;
                boolean flag=false;
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] data = line.split("\t");
                // 检查是否有足够数量的元素
                if (data.length < 2) {
                    discount=0;
                    flag=true;
                }
                commodityName = data[0];
                if (!flag){
                    discount = data[1].isEmpty() ? 0.0 : Double.parseDouble(data[1]);
                }else {
                    discount=0;
                }
                sales.add(new Sale(commodityName, discount));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sales;
    }

    /**
     * 根据售卖列表售卖
     */
    public static void saleBySaleList(List<Product> products, List<Sale> sales,Date date) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\zhiqi\\Desktop\\lab\\src\\main\\resources\\result.txt", true))) {
            double totalPrice = 0.0;
            for (Sale sale : sales) {
                for (Product product : products) {
                    if (sale.getCommodity_name().equals(product.getName())) {
                        // 写入售卖记录到文件
                        totalPrice+=product.getPrice()-sale.getDiscount();
                        break;
                    }
                }
            }
            // 使用String.format()保留两位小数
            String outputLine = String.format("0 day: turnover: %.2f", totalPrice);
            writer.write( outputLine);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void oneDay(List<Product> products,List<Sale> sales,Date date){

        saleBySaleList(products, sales,date);

        removeExpiredProducts(products,date);

        saveData(products);
    }

    public static void main(String[] args) {

        List<Sale> sales = loadSaleData();
        List<Product> products = loadData();
//        int day=2;
////        while (true){
////            oneDay(products, sales, new Date(2022, 4, day));
////            day++;
////            if (day>6){
////                break;
////            }
////        }
//        oneDay(products, sales, new Date(2022, 4, day));
        Date date = new Date(2022, 4, 2);
        saleBySaleList(products, sales,date);

        removeExpiredProducts(products,date);

        saveData(products);
    }
}
