package org.thoth.ged.rname.main;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class MainForRname {
    public static void main(String[] args) throws Exception {
        
        // EXTRACT
        Path extractPath = Paths.get("D:\\Desktop\\jgk81.ged");
        Stream<String> extractData = Files.lines(extractPath);
         
        // TRANSLATE
        final Pattern fullNamePattern
            = Pattern.compile("1 NAME ([\\w\\s]*)/(\\w*)/");
        
        List<String> loadData = new LinkedList<>();
        extractData.forEach(line -> {
            Matcher m = fullNamePattern.matcher(line);
            try {
                if (!m.matches() || m.groupCount() != 2) {
                    loadData.add(line);
                    return;
                }
                // 1 NAME Ralph George /Kuhn/
                // BECOMES:
                // 1 NAME Ralph /Kuhn/
                // 2 GIVN Ralph
                // 2 SURN Kuhn
                // 2 _RNAME George
                //
                //
                // 1 NAME Mary /Ruhling/
                // BECOMES:
                // 1 NAME Mary /Ruhling/
                // 2 GIVN Mary
                // 2 SURN Ruhling
                String GIVN = null;
                String _RNAME = null; {
                    // "Katherine Magdelena "
                    String[] tokens = m.group(1).trim().split(" ");
                    if (tokens.length == 1) {
                        GIVN = tokens[0].trim();
                        _RNAME = null;
                    }
                    else
                    if (tokens.length == 2) {
                        GIVN = tokens[0].trim();
                        _RNAME = tokens[1].trim();
                    }
                    else
                    if (tokens.length >= 3) {
                        GIVN = tokens[0].trim();
                        _RNAME = "";
                        for (int i=1; i<tokens.length; i++) {
                            if (i > 1) {
                                _RNAME += " ";
                            }
                            _RNAME += tokens[i];
                        }
                    }
                }
                
                String SURN = null; {
                    // "Gasser"
                    SURN = m.group(2).trim();
                }
                
                String NAME = null; {
                    NAME = GIVN + " " + "/" + SURN + "/"; 
                }
                
                NAME = "1 NAME " + NAME;
                loadData.add(NAME);
                
                GIVN = "2 GIVN " + GIVN;
                loadData.add(GIVN);
                
                SURN = "2 SURN " + SURN;
                loadData.add(SURN);
                
                if (_RNAME != null) {
                    _RNAME = "2 _RNAME " + _RNAME;
                    loadData.add(_RNAME);
                }                
                
//                System.out.printf("%s%n", line);
//                System.out.printf("    NAME: %s%n", NAME);
//                System.out.printf("    GIVN: %s%n", GIVN);
//                System.out.printf("    SURN: %s%n", SURN);
//                System.out.printf("  _RNAME: %s%n%n", _RNAME);
            } catch (Exception e) {
                System.out.printf("Process failure for: \"%s\"%n", line);
            }
        });
        
        // LOAD
        
        PrintWriter pw = new PrintWriter(new File(extractPath.getParent().toFile(), extractPath.getFileName().toString() + ".new.ged"));
        loadData.forEach(line -> {
            pw.printf("%s%n", line);
        });
        pw.flush();
        pw.close();
        
        // EXIT
        System.out.printf("** DONE **%n");
    }
}
