import lotus.domino.*;

import javax.naming.NamingException;
import java.util.Vector;

class DominoTest {
            public static void main(String[] args) {

                String version = "DominoTest - v1.1 - 13/03/2014";

                try {
                    System.out.println("##################################################################");
                    System.out.println("Version       : " + version);
                    System.out.println("################################################################## Session info");
                    Session session = NotesFactory.createSession("*******i", "******", "******");


                    System.out.println("CommonName        : " +  session.getCommonUserName());
                    System.out.println("UserName          : " +  session.getUserName());
                    //System.out.println("EffectiveUsername : " + session.getEffectiveUserName());


                    System.out.println("NotesVersion  : " + session.getNotesVersion());
                    System.out.println("ServerName    : " + session.getServerName());

                    // System.out.println("ServerName   : " + session.getCredentials().toString() );         ***not implemented***
                    // System.out.println("ServerName   : " + session.getCurrentDatabase());                ***Older version on server does not support this method***
                    System.out.println("Org Dir Path  : " + session.getOrgDirectoryPath().toString());

                    // 3 standard DBs
                    Database dbstd1 = session.getDatabase(null, "names.nsf");
                    Database dbstd2 = session.getDatabase(null, "admin4.nsf");
                    Database dbstd3 = session.getDatabase(null, "certlog.nsf");

                    // 2 connectors template Dbs
                    Database dbtRegArch = session.getDatabase(null, "REGARCHV.NTF");
                    Database dbtRegCert = session.getDatabase(null, "REGCERTS.NTF");

                    // 2 connectors template Dbs
                    // Database dbRegArch = session.getDatabase(null, "REGARCH.NSF");               *** 13-03-2014 (ronro03) - wrong db name ****
                    Database dbRegArch = session.getDatabase(null, "REGARCH.NSF");
                    Database dbRegCert = session.getDatabase(null, "REGCERT.NSF");

                    /*
                    ACLLEVEL_: NOACCESS (0) , DEPOSITOR (1), READER (2), AUTHOR (3), EDITOR (4), DESIGNER (5), MANAGER (6)
                    */

                    System.out.println("################################################################## Standard DBs : Current Access Level");
                    System.out.println("names.nsf     : " + dbstd1.getCurrentAccessLevel());
                    System.out.println("admin4.nsf    : " + dbstd2.getCurrentAccessLevel());
                    System.out.println("certlog.nsf   : " + dbstd3.getCurrentAccessLevel());

                    System.out.println("################################################################## Connector's template DBs : Current Access Level");
                    System.out.println("REGARCHV.NTF  : " + dbtRegArch.getCurrentAccessLevel());
                    System.out.println("REGCERTS.NTF  : " + dbtRegCert.getCurrentAccessLevel());
                    System.out.println("################################################################## Connector's endpoint DBs : Current Access Level");
                    System.out.println("REGARCH.NSF   : " + dbRegArch.getCurrentAccessLevel());
                    System.out.println("REGCERT.NSF   : " + dbRegCert.getCurrentAccessLevel());


                    UserInfo(session);
                    writeTestCertifierDocument(session);

                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }


            private static void writeTestCertifierDocument(Session session) throws NamingException
            {
                Document certdoc = null;
                try
                {
                    //CryptoService cryptoService = new LegacyCryptoService();
                    System.out.println("***** Writing test certifier document");
                    Database certdb = session.getDatabase(session.getServerName(), "regcert.nsf");

                    System.out.println(certdb.getAllDocuments().toString());

                    certdoc = certdb.createDocument();

                    certdoc.appendItemValue("Form", "OUCertifier");
                    certdoc.appendItemValue("OUName", "O=TestEncryptionKeys");
                    certdoc.appendItemValue("Enabled", "1");

                    //Item item = certdoc.appendItemValue("SecretOUPwd", cryptoService.encrypt(null, "secret"));
                    //item.setEncrypted(true);

                    Name dominoName = session.createName(session.getUserName());

                    System.out.println("Canonical    : " + dominoName.getCanonical());
                    System.out.println("Common       : " + dominoName.getCommon());
                    //System.out.println("PRMD       : " + dominoName.getPRMD());
                    System.out.println("Abbreviated  : " + dominoName.getAbbreviated());
                    //System.out.println("Abbreviated  : " + dominoName.getGeneration());



//                    UserObjectBaseHelper

                    DateTime dt = session.createDateTime("Today");
                    dt.setNow();
                    certdoc.appendItemValue("Comment", dt.getLocalTime() + " Added by (ronro03) " + dominoName.getAbbreviated());
                    certdoc.appendItemValue("IDType", "0");

                    Vector v = new Vector();
                    v.addElement("RegXCertifier");
                    certdoc.setEncryptionKeys(v);


                    //certdoc.encrypt();
                    certdoc.save();

                    System.out.println("Test certifier document could be written with success.");
                }
                catch (NotesException ne)
                {
                    String err = "Failed to create test certifier document due to: " + ne;
                    System.out.println(err);

                }
                return certdoc;
            }

            private static void UserInfo(Session session) throws NamingException {

                try
                {
                Registration reg = session.createRegistration();
                reg.setRegistrationServer("CN=TESTVR001/O=DOMINO");
                String username = "CN=IdentityMinder2 Utente/O=DOMINO";

                StringBuffer mailserver = new StringBuffer();
                StringBuffer mailfile = new StringBuffer();
                StringBuffer maildomain = new StringBuffer();
                StringBuffer mailsystem = new StringBuffer();
                Vector profile = new Vector();


                    Vector ulist = session.getUserNameList();
                    Name primary = (Name)ulist.firstElement();
                    System.out.println("\t" + primary.getCommon());
                    if (ulist.size() > 1) {
                        Name secondary = (Name)ulist.lastElement();
                        System.out.println("\t" + secondary.getCommon() + " (alternate)");
                    } else {
                        System.out.println("\tNo alternate name");
                    }




                    reg.getUserInfo(username,
                        mailserver,
                        mailfile,
                        maildomain,
                        mailsystem,
                        profile);
                System.out.println("Info for --> " + username + " <--" + "\n" +
                        "\tMail server:\t" + mailserver + "\n" +
                        "\tMail file:\t\t" + mailfile + "\n" +
                        "\tMail domain:\t" + maildomain + "\n" +
                        "\tMail system:\t" + mailsystem + "\n" +
                        "\tProfile:");
                for (int n=0; n<profile.size(); n++) {
                    System.out.println(profile.elementAt(n));
                }
                }

                 catch (NotesException ne)
                    {
                        String err = "Failed due to: " + ne;
                        System.out.println(err);

                    }

            }
    }

