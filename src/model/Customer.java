package model;

public class Customer {

    private final int id;
    private final String ssn;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private final String password;
    private String created;

    public Customer(int id, String ssn, String firstname, String lastname, String phoneNumber, String password, String created) {
        this.id = id;
        this.ssn = ssn;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.created = created;
    }
    public Customer(int id, String ssn, String password) {
        this.id = id;
        this.ssn = ssn;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getSsn() {
        return ssn;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getCreated() {
        return created;
    }
}