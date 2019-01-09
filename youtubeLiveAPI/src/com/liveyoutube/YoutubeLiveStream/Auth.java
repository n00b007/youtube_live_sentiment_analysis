package com.liveyoutube.YoutubeLiveStream;

// I am not the author for this class. This is the sample auth code taken from the youtube API
// github page.

// TODO: few changes can be made here.
// Create credentials object using the refresh token rather than using the access token.
// Access token expires after 1 hour, whereas the refresh token is used by the credentials
// builder to obtain another access token. For a truly server-less implementation, please use a
// refresh token.

// Check out the below code for the implementation ->


import static com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * Shared class used by every sample. Contains methods for authorizing a user and caching
 * credentials.
 */

/*
Changes made by Sriram Ananthakrishnan to the existing code implementation.
Added auth function using refresh token (browserless).
Added a function to build the youtube service and return it.
 */
public class Auth {

    /**
     * Define a global instance of the HTTP transport.
     */
    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    public static final String APPLICATION_NAME = "YouTube Sentiment Analyzer";

    private YouTube service;

    /**
     * Define a global instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /**
     * This is the directory that will be used under the user's home directory where OAuth tokens
     * will be stored.
     */
    private static final String CREDENTIALS_DIRECTORY = ".credentials";

    private static final String CLIENT_SECRET_PATH = "enter client secret path";

    /**
     * Authorizes the installed application to access user's protected data.
     *
     * @param scopes              list of scopes needed to run youtube upload.
     * @param credentialDatastore name of the credential datastore to cache OAuth tokens
     */
    public static Credential authorize(List<String> scopes, String credentialDatastore)
            throws IOException {

        // Load client secrets.
        Reader clientSecretReader =
                new InputStreamReader(Auth.class.getResourceAsStream("client_secrets.json"));
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);

        // Checks that the defaults have been replaced (Default = "Enter X here").
        if (clientSecrets.getDetails().getClientId().startsWith("Enter") || clientSecrets
                .getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println("Enter Client ID and Secret from https://console.developers.google"
                    + ".com/project/_/apiui/credential "
                    + "into src/main/resources/client_secrets.json");
            System.exit(1);
        }

        // This creates the credentials datastore at ~/.oauth-credentials/${credentialDatastore}
        FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(new File(
                System.getProperty("user.home") + "/" + CREDENTIALS_DIRECTORY));
        DataStore<StoredCredential> datastore =
                fileDataStoreFactory.getDataStore(credentialDatastore);

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                        clientSecrets, scopes)
                        .setCredentialDataStore(datastore).build();

        // Build the local server and bind it to port 8080
        LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(8080).build();

        // Authorize.
        return new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");
    }


    public Credential new_authorize() throws IOException {
        // Load client secrets.
        InputStream clientSecretIn = new FileInputStream(CLIENT_SECRET_PATH);
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(clientSecretIn));
        GoogleCredential googleCredential =
                new GoogleCredential.Builder().setJsonFactory(JSON_FACTORY)
                        .setTransport(HTTP_TRANSPORT).setClientSecrets(clientSecrets).build();
        // refresh token for sriboi@myman.com sample refresh token
        googleCredential.setRefreshToken("1/Tm9AioJC3sPo5PVDeheqipSo4U54MSKc01CadaM0mb0JOifw");
        System.out.println("The refresh token is: " + googleCredential.getRefreshToken());
        return googleCredential;
    }

    /*
    Function to initialize the service.
    Call this method if you already have a refresh token and wish to initialize this service
    */
    public void startApiService() throws IOException {
        Credential credential = new_authorize();
        service = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }
}
