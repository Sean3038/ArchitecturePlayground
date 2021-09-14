package common;

import java.util.ArrayList;
import java.util.List;

public class VendorModel {

    /**
     * 商品資訊
     */
    private final List<Product> productList = new ArrayList<>();

    /**
     * 啟動程式時取得商品資訊
     */
    public void initialize() {
        initializeProducts();
    }

    private void initializeProducts() {
        Product p1 = new Product();
        p1.setName("珍奶");
        p1.setPrice(45);
        p1.setRemainCount(50);
        Product p2 = new Product();
        p2.setName("紅茶");
        p2.setPrice(25);
        p2.setRemainCount(10);
        productList.add(p1);
        productList.add(p2);
    }

    /**
     * 取得編號商品
     */
    public Call<Product> getProduct(int index) {
        return new Call<Product>() {

            @Override
            public Product get() {
                return productList.get(index);
            }

            @Override
            public void cancel() {
                //TODO cancel when background service working
            }
        };
    }

    /**
     * 取得商品數量
     */
    public Call<Integer> getProductCount() {
        return new Call<Integer>() {

            @Override
            public Integer get() {
                return productList.size();
            }

            @Override
            public void cancel() {
                //TODO cancel when background service working
            }
        };
    }

    /**
     * 檢查目前有無商品剩餘
     */
    public Call<Boolean> isNoProductRemain() {
        return new Call<Boolean>() {

            @Override
            public Boolean get() {
                boolean isAllProductSoldOut = true;
                for (Product product : productList) {
                    if (!product.isSoldOut()) {
                        isAllProductSoldOut = false;
                        break;
                    }
                }
                return isAllProductSoldOut;
            }

            @Override
            public void cancel() {
                //TODO cancel when background service working
            }
        };
    }

    /**
     * 取得尚可購買商品
     */
    public Call<List<Product>> getServingProducts() {
        return new Call<List<Product>>() {

            @Override
            public List<Product> get() {
                List<Product> products = new ArrayList<>();
                for (Product product : productList) {
                    if (!product.isSoldOut()) {
                        products.add(product);
                    }
                }
                return products;
            }

            @Override
            public void cancel() {
                //TODO cancel when background service working
            }
        };
    }
}
