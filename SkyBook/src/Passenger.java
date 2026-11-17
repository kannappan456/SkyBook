public class Passenger {

    private final String name;
    private final int age;
    private final String email;

    public Passenger(String name, int age, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        if (age < 1 || age > 120) {
            throw new IllegalArgumentException("Invalid age.");
        }

        if (email == null ||
                !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email.");
        }

        this.name = name.trim();
        this.age = age;
        this.email = email.trim();
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }
}