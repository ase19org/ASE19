package ch.uzh.ifi.seal.ase19.miner;

import cc.kave.commons.model.events.completionevents.Context;
import ch.uzh.ifi.seal.ase19.core.InMemoryPersistenceManager;
import ch.uzh.ifi.seal.ase19.core.utils.IoHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;

public class Miner {

    private static Logger logger = LogManager.getLogger(Miner.class);

    /*
        download the context data and set contextDirectory argument
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            logger.error("Not enough arguments provided! Syntax: contextDirectory modelDirectory");
        } else {
            String contextDirectory = args[0];
            String modelDirectory = args[1];

            logger.info("Context directory is: " + contextDirectory);
            logger.info("Model directory is: " + modelDirectory);

            readContextsFromDisk(contextDirectory, modelDirectory);
        }
    }


    private static void readContextsFromDisk(String contextDirectory, String modelDirectory) {
        InMemoryPersistenceManager persistence = new InMemoryPersistenceManager(modelDirectory);
        ContextProcessor processor = new ContextProcessor(persistence);
        Set<String> contextList = IoHelper.findAllZips(contextDirectory);

        int counter = 1;
        int total = contextList.size();
        for (String zip : contextList) {
            logger.info("Process zip (" + counter + "/" + total + "): " + zip);

            List<Context> contexts = IoHelper.read(contextDirectory.concat(zip));

            for (Context context : contexts) {
                processor.runAndPersist(context);
            }

            if (counter % 100 == 0) {
                persistence.saveOnFileSystem();
            }

            counter++;
        }
    }
}