package aydin.firebasedemo;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class WelcomeScreenController {


    @FXML
    private TextField PasswordText;
    @FXML
    private TextField UserNameText;


    void initialize() {
        AccessDataView accessDataViewModel = new AccessDataView();
        UserNameText.textProperty().bindBidirectional(accessDataViewModel.getUserName());
        PasswordText.textProperty().bindBidirectional(accessDataViewModel.getPassword());
    }


    @FXML
    void LoginButtonAction(ActionEvent event) {
        String userName = UserNameText.getText();
        String password = PasswordText.getText();

        // if signIn returns true then matching username & password was found
        // & goes to the primary screen
        if (signIn(userName, password))
        {
            System.out.println(userName + " is inside the database");
            try {
                switchToPrimary();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else
            System.out.println(userName + " is not inside database or password is incorrect");
    }

    @FXML
    void RegisterButtonAction(ActionEvent event) throws IOException {
        if (UserNameText.getText().isEmpty() || PasswordText.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error: please enter a valid username and password");
        }
        else {
            registerUser();
            addUser();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Registration has been Confirmed \nPlease Log-In");
        }
    }



    @FXML
    private void switchToPrimary() throws IOException {
        DemoApp.setRoot("primary");
    }



    public void registerUser() {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(UserNameText.getText())
                    .setEmailVerified(false)
                    .setPassword(PasswordText.getText())
                    .setDisabled(false);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            System.out.println("Created user for: " + userRecord.getEmail());
        }
        catch (IllegalArgumentException  | FirebaseAuthException firebaseAuthException) {
            String message = firebaseAuthException.getMessage();
            if (message != null && message.contains("exists"))
            {
                System.out.println("Email already in use");
            }
            else
            {
                System.out.println("Email not valid format or password incorrect");
            }
        }
    }



    public void addUser() {

        DocumentReference docRef = DemoApp.fstore.collection("Users").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("UserName", UserNameText.getText());
        data.put("Password", PasswordText.getText());

        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
    }


    public boolean signIn(String username, String password) {
        ApiFuture<QuerySnapshot> future = DemoApp.fstore.collection("Users").get();
        List<QueryDocumentSnapshot> documents;
        try {
            documents = future.get().getDocuments();
            if(documents.size()>0) {
                System.out.println("Checking if user is in database...");

                // user and pass are used to store the usernames and passwords in the firestore database
                String User = "", Pass;

                // going through the document list
                for (QueryDocumentSnapshot document : documents)
                {
                    User = document.getData().get("UserName").toString();
                    Pass = document.getData().get("Password").toString();

                    // exits the method and returns true when matching username & password was found
                    if (username.equals(User) && password.equals(Pass))
                        return true;
                }
            }
            else
            {
                System.out.println("No User found in database");
            }
        } catch (InterruptedException | ExecutionException ex)
        {
            ex.printStackTrace();
        }
        return false;
    }




}   // End

