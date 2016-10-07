package user;

/**
 * Created by Alexey on 19.09.2016.
 */
public class User {
    public String name;
    public String password;
    private boolean admin;

    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User(String name, String password, boolean admin) {
        this.name = name;
        this.password = password;
        this.admin = admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {

        return admin;
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
