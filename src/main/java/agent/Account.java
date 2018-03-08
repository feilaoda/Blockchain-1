package agent;

import java.io.Serializable;

public class Account implements Serializable {

    private ByteArray address;

    private String alias;

    private Key key;

    public Account() {

    }

    public ByteArray getAddress() {
        return address;
    }

    public void setAddress(ByteArray address) {
        this.address = address;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Account) {
            return address.equals(((Account) obj).address);
        }else {
            return super.equals(obj);
        }
    }
}
