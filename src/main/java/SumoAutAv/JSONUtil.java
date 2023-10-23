package SumoAutAv;

import com.google.gson.Gson;


public class JSONUtil {
    private static final Gson gson = new Gson();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static TransferData parseTransferJson(String json) {
        TransferData data = fromJson(json, TransferData.class);
        return data;
    }

    public static String createTransferJson(String from, String to, double amount) {
        TransferData data = new TransferData(from, to, amount);
        return toJson(data);
    }

    public static String createSendRouteToDriverJson(Route route, String driver) {
        SendRouteToDriverData data = new SendRouteToDriverData(route, driver);
        return toJson(data);
    }

    public static SendRouteToDriverData parseSendRouteToDriverJson(String json) {
        return fromJson(json, SendRouteToDriverData.class);
    }

    public static class SendRouteToDriverData {
        private Route route;
        private String driver;

        public SendRouteToDriverData(Route route, String driver) {
            this.route = route;
            this.driver = driver;
        }

        public Route getRoute() {
            return route;
        }

        public String getDriver() {
            return driver;
        }
    }

    public static class TransferData {
        private String from;
        private String to;
        private double amount;

        public TransferData(String from, String to, double amount) {
            this.from = from;
            this.to = to;
            this.amount = amount;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public double getAmount() {
            return amount;
        }
    }

}
