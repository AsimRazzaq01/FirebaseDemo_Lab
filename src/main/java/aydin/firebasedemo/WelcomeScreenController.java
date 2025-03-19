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

    private boolean key;
    @FXML
    private TextField PasswordText;
    @FXML
    private TextField UserNameText;


    void initialize() {
        AccessDataView accessDataViewModel = new AccessDataView();
        //nameTextField.textProperty().bindBidirectional(accessDataViewModel.personNameProperty());
        //writeButton.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());

        UserNameText.textProperty().bindBidirectional(accessDataViewModel.getUserName());
        PasswordText.textProperty().bindBidirectional(accessDataViewModel.getPassword());
    }



    @FXML
    void LoginButtonAction(ActionEvent event) {
        String userName = UserNameText.getText();
        String password = PasswordText.getText();

//        if (userName.isEmpty() || password.isEmpty()) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Please enter your username and password");
//        }
//        else {
//            if (FirebaseAuth.getInstance(userName) == null) {}
//
//        }

        //asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future =  DemoApp.fstore.collection("Users").get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;
        try
        {
            documents = future.get().getDocuments();
            if(documents.size()>0)
            {
                System.out.println("Checking for User in firabase database....");

                for (QueryDocumentSnapshot document : documents)
                {
                    userName = document.getData().get("UserName").toString();
                    password = document.getData().get("Password").toString();
                }
                // exits the method and returns true when matching username & password was found
                if (UserNameText.getText().equals(userName) && PasswordText.getText().equals(password)){
                    switchToPrimary();
                }
            }
            else
            {
                System.out.println("No User found in firebase database....");
            }
            key=true;

        }
        catch (InterruptedException | ExecutionException | IOException ex)
        {
            ex.printStackTrace();
        }
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



    public boolean registerUser() {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(UserNameText.getText())
                    .setEmailVerified(false)
                    .setPassword(PasswordText.getText())
                    .setDisabled(false);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            System.out.println("Created user for: " + userRecord.getEmail());
            return true;
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
            return false;
        }



        /*
        UserRecord userRecord;
        try {
            userRecord = DemoApp.fauth.createUser(request);
            System.out.println("Successfully created new user with Firebase Uid: " + userRecord.getUid()
                    + " check Firebase > Authentication > Users tab");
            return true;

        } catch (FirebaseAuthException ex) {
            // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error creating a new user in the firebase");
            return false;
        }

         */

    }



    public void addUser() {

        DocumentReference docRef = DemoApp.fstore.collection("Users").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("UserName", UserNameText.getText());
        data.put("Password", PasswordText.getText());

        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
    }















/*
    public boolean readFirebase()
    {
        key = false;

        //asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future =  DemoApp.fstore.collection("Persons").get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;
        try
        {
            documents = future.get().getDocuments();
            if(documents.size()>0)
            {
                System.out.println("Getting (reading) data from firabase database....");
                listOfUsers.clear();
                for (QueryDocumentSnapshot document : documents)
                {
                    outputTextArea.setText(outputTextArea.getText()+ document.getData().get("Name")+ " , Age: "+
                            document.getData().get("Age")+ " \n ");
                    System.out.println(document.getId() + " => " + document.getData().get("Name"));
                    person  = new Person(String.valueOf(document.getData().get("Name")),
                            Integer.parseInt(document.getData().get("Age").toString()));
                    listOfUsers.add(person);
                }
            }
            else
            {
                System.out.println("No data");
            }
            key=true;

        }
        catch (InterruptedException | ExecutionException ex)
        {
            ex.printStackTrace();
        }
        return key;
    }

 */

}

