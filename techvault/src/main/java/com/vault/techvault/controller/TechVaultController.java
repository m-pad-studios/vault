package com.vault.techvault.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;


import org.springframework.stereotype.Component;


import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/* class to demonstrate use of Drive files list API */
@Component
public class TechVaultController {
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "Tech Vault";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE);
   /* private static final List<String> SCOPES_SHEETS =
            Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);*/

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = TechVaultController.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //returns an authorized Credential object.
        return credential;
    }

    private static Credential getCredentialsSheets(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = TechVaultController.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    public String getFiles() throws IOException, GeneralSecurityException {

        List<List<Object>> values = new ArrayList<>();
        List<String> excelData = new ArrayList<>();
        String header = "";
        List<String> salesInfo = new ArrayList<>();
        int itrt = 0;

        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Print the names and IDs for up to 10 files.
        //search Google Drive for all files.
        FileList result = service.files().list()
                .setPageSize(10)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            //keep this for loop
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
                if(file.getName().equals("Monthly budget")) {

                    System.out.println("Found the monthly budget...");


                    Sheets service2 = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                            .setApplicationName(APPLICATION_NAME)
                            .build();

                    final String spreadsheetId = "1DGb23tHgTN3wRGKKft5KEi30gf14WfmKZEpbWLyY0aY";
                    final String range = "A1:C";

                    ValueRange response;
                    response = service2.spreadsheets().values()
                            .get(spreadsheetId, range)
                            .execute();
                   values = response.getValues();
                    if (values == null || values.isEmpty()) {
                        System.out.println("No data found.");
                    } else {

                        for (List row : values) {
                            // Print columns A and E, which correspond to indices 0 and 4.

                            System.out.printf("%s, %s, %s\n", row.get(0), row.get(1), row.get(2));
                            excelData.add(row.get(0).toString());
                            excelData.add(row.get(1).toString());
                            excelData.add(row.get(2).toString());

                            if(itrt == 0) {
                                header = excelData.get(0) + "|" + excelData.get(1) + "|" + excelData.get(2);
                            }
                            else {
                                salesInfo.add(row.get(0).toString() + " |" + row.get(1).toString() + "|" + row.get(2));
                            }
                            itrt++;
                        }
                    }

                    //how we add/update a Google sheet
                    //need to move to new method
                    ValueRange body = new ValueRange()
                            .setValues(Arrays.asList(
                                    Arrays.asList("Bob", "WY", 868)));

                    UpdateValuesResponse result2 = service2.spreadsheets().values()
                            .update(spreadsheetId, "A6:C", body)
                            .setValueInputOption("RAW")
                            .execute();

                }
            }
        }


        if (files == null || values == null) throw new AssertionError();
        return  "<h1>Here are all the files in your Google Drive</h1> </br></br>" + files.toString() + "</br></br><h1>Here are the values in the Monthly Budget excel...</h1></br></br>" + header +
                "</br>" + salesInfo.get(0) + "</br>" + salesInfo.get(1) + "</br>" + salesInfo.get(2) + "</br>" + salesInfo.get(3);
    }

}

