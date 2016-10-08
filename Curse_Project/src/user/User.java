package user;

/**
 * Created by Alexey on 19.09.2016.
 */
public class User {
    private String name;
    private String password;
    private byte access;

    public User(){}
    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User(String name, String password, boolean admin) {
        this.name = name;
        this.password = password;
        this.access = (byte)(admin ? -1 : 0);
    }

    public byte getAccess() {
        return access;
    }

    public void setAccess(byte access) {
        this.access = access;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (name != null ? !name.equalsIgnoreCase(user.name) : user.name != null) return false;
        return password != null ? password.equals(user.password) : user.password == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    public boolean Empty() {
        return name == null || password == null || name.isEmpty() || password.isEmpty();
    }
}
