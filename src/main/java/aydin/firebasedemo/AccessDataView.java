package aydin.firebasedemo;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AccessDataView{

    private final StringProperty personName = new SimpleStringProperty();
    private final int age=0;
    private final ReadOnlyBooleanWrapper writePossible = new ReadOnlyBooleanWrapper();
    private final StringProperty userName = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();

    public AccessDataView() {
        writePossible.bind(personName.isNotEmpty());
    }
    public AccessDataView(ReadOnlyBooleanWrapper writePossible, StringProperty userName, StringProperty password) {
        this.writePossible.set(writePossible.get());
    }

    public StringProperty getUserName() {
        return userName;
    }
    public StringProperty getPassword() {
        return password;
    }
    public StringProperty personNameProperty() {
        return personName;
    }

    public ReadOnlyBooleanProperty isWritePossibleProperty() {
        return writePossible.getReadOnlyProperty();
    }
}
