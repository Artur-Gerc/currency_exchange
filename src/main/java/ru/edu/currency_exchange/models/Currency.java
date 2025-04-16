package ru.edu.currency_exchange.models;

public class Currency {
    private int id;
    private String fullName;
    private String code;
    private String sign;

    public Currency() {
    }

    public Currency(int id, String fullName, String code, String sign) {
        this.id = id;
        this.fullName = fullName;
        this.code = code;
        this.sign = sign;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "Currencies{" +
                "id=" + id +
                ", name='" + fullName + '\'' +
                ", code='" + code + '\'' +
                ", symbol='" + sign + '\'' +
                '}';
    }
}
