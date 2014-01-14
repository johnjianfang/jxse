package org.apache.peer.server;


import com.google.common.base.Splitter;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class SamplePeerGroup {
    private static final Logger log = LoggerFactory.getLogger(SamplePeerGroup.class);

    private static final String HELP = "help";
    private static final String SEED = "seed";
    private static final String PORT = "port";
    private static final String TOKEN = "token";

    public static void main(String[] args) {
        try {
            Options opt = new Options();
            opt.addOption(HELP, false, "Print help");
            opt.addOption(SEED, true, "Seed");
            opt.addOption(PORT, true, "Port");
            opt.addOption(TOKEN, true, "Token");

            if(args == null || args.length < 2){
                HelpFormatter f = new HelpFormatter();
                f.printHelp("OptionsTip", opt);
            } else {
                BasicParser parser = new BasicParser();
                CommandLine cl = parser.parse(opt, args);

                if(cl.hasOption(HELP)){
                    HelpFormatter f = new HelpFormatter();
                    f.printHelp("OptionsTip", opt);
                } else {
                    Set<String> seeds = new HashSet<String>();
                    if (cl.hasOption(SEED)) {
                        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
                        Iterable<String> elements = splitter.split(cl.getOptionValue(SEED));
                        for (String element: elements) {
                            seeds.add(element);
                        }
                    }

                    int port = 9000;
                    if (cl.hasOption(PORT)) {
                        port = Integer.parseInt(cl.getOptionValue(PORT).trim());
                    }
                    String token = "test";
                    if (cl.hasOption(TOKEN)) {
                        token = cl.getOptionValue(TOKEN).trim();
                    }

                    log.info("Starting sample PeerGroup on port " + port);
                    DefaultPeerGroup peerGroup = new DefaultPeerGroup(seeds, port, token);
                    peerGroup.setName("Sample PeerGroup");
                    peerGroup.start();
                    try {
                        peerGroup.join();
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
