package com.comandante;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.internal.Maps;
import com.comandante.http.BillHttpClient;
import com.comandante.http.server.BillHttpServerApplication;
import com.comandante.http.server.BillHttpServerConfiguration;
import com.comandante.http.server.resource.BillHttpGraph;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import io.dropwizard.cli.ServerCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import javax.swing.*;
import java.io.File;
import java.io.IOException;


public class Bill {

    public static final String BILL_DB_VERSION = "1.0";
    public static final String BILL_DB_FILENAME = "bill-settings.mapdb";
    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 600;
    public static final int DEFAULT_HTTP_PORT = 32224;
    public static final int DEFAULT_HTTP_PORT_ADMIN = 32225;
    public static final String CREATE_URL = "http://localhost:" + DEFAULT_HTTP_PORT + "/bill/create";
    public static final String HEALTHCHECK_URL = "http://localhost:" + DEFAULT_HTTP_PORT_ADMIN + "/healthcheck";

    private static final Logger log = LogManager.getLogger(Bill.class);
    private static boolean isDebug = false;

    private static BillHttpClient billHttpClient = new BillHttpClient();

    public static void main(String[] args) throws Exception {
        BillCommand billCommand = new BillCommand();
        new JCommander(billCommand, args);
        if (billCommand.isDebug()) {
            isDebug = true;
        }
        if (billCommand.getGraphUrl() != null && isServerRunning(billHttpClient)) {
            sendGraphToBill(billCommand, billHttpClient);
            System.exit(0);
        }
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        DB db = DBMaker.newFileDB(getOrCreateUserDataFile())
                .closeOnJvmShutdown()
                .transactionDisable()
                .make();
        BillGraphManager billGraphManager = new BillGraphManager(db);
        billGraphManager.generateAllGraphsFromDisk();
        if (billCommand.getGraphUrl() != null) {
            billGraphManager.addNewGraph(createBillHttpGraph(billCommand));
        }
        BillHttpServerApplication billHttpServerApplication = new BillHttpServerApplication(billGraphManager);
        Bootstrap bootstrap = new Bootstrap(billHttpServerApplication);
        ServerCommand<BillHttpServerConfiguration> serverConfigurationServerCommand = new ServerCommand<BillHttpServerConfiguration>(billHttpServerApplication);
        serverConfigurationServerCommand.run(bootstrap, new Namespace(Maps.<String, Object>newHashMap()));
        log.info("Bill server started");
    }

    private static boolean isServerRunning(BillHttpClient billHttpClient) {
        try {
            return billHttpClient.billServerHealthCheck(HEALTHCHECK_URL);
        } catch (IOException e) {
            return false;
        }
    }

    private static void sendGraphToBill(BillCommand billCommand, BillHttpClient billHttpClient) throws IOException {
        BillHttpGraph billHttpGraph = createBillHttpGraph(billCommand);
        billHttpClient.createGraph(CREATE_URL, new ObjectMapper().writeValueAsString(billHttpGraph));
    }

    private static BillHttpGraph createBillHttpGraph(BillCommand billCommand) {
        BillHttpGraph billHttpGraph = new BillHttpGraph();
        billHttpGraph.setGraphUrl(billCommand.getGraphUrl());
        billHttpGraph.setRefreshRate(billCommand.getReloadInterval());
        billHttpGraph.setTimezone(billCommand.getTimezone());
        billHttpGraph.setHeight(billCommand.getHeight());
        billHttpGraph.setWidth(billCommand.getWidth());
        billHttpGraph.setTitle(billCommand.getTitle());
        billHttpGraph.setGraphDuration(billCommand.getDuration());
        if (billCommand.getTimezone() != null) {
            billHttpGraph.setTimezone(billCommand.getTimezone());
        }
        return billHttpGraph;
    }

    public static File getOrCreateUserDataFile() throws IOException {
        File userDataFile = new File(getUserDataFile());
        if (!userDataFile.exists()) {
            Files.createParentDirs(userDataFile);
        }
        return userDataFile;
    }

    public static String getUserDataDirectory() {
        return System.getProperty("user.home") + File.separator + ".bill" + File.separator + BILL_DB_VERSION + File.separator;
    }

    public static String getUserDataFile() {
        return getUserDataDirectory() + BILL_DB_FILENAME;
    }

    public static boolean isDebug() {
        return isDebug;
    }
}
