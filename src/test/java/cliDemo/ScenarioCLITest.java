package cliDemo;

import api.DDPublicAPI;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import stubs.billing.BillingWebService;
import stubs.billing.BillingWebServiceService;
import stubs.customer.*;
import stubs.customer.Customer;
import stubs.delivery.*;
import stubs.delivery.Delivery;
import stubs.delivery.ParseException_Exception;
import stubs.drone.DroneWebService;
import stubs.drone.DroneWebServiceService;
import stubs.packageR.AlreadyExistingPackageException_Exception;
import stubs.packageR.PackageRegisterWebService;
import stubs.packageR.PackageWebServiceService;
import stubs.packageR.UnknownPackageException_Exception;
import stubs.planning.*;
import stubs.planning.UnknownCustomerException;
import stubs.provider.*;
import stubs.provider.Provider;
import utils.MyDate;

import javax.xml.ws.BindingProvider;
import java.io.IOException;
import java.lang.Exception;
import java.lang.Package;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ScenarioCLITest {

    //    ### Declarations des WebServices ###
    private static CustomerWebService cws;
    private static ProviderWebService pws;
    private static PackageRegisterWebService packws;
    private static DroneWebService dws;
    private static PlanningWebService plws;
    private static DeliveryWebService dews;
    private static BillingWebService bws;
    private static final double DELTA = 1e-15;


    @BeforeClass
    public static void init() {
        initialize();
    }

    @Test
    public void testScenarioComplet() {
        ArrayList<String> exceptionList = new ArrayList<>();


        /*****************************************************
         *   PAS ENCORE  DE CLIENT "PAUL KOFFI" DANS LA BD   *
         *****************************************************/
        System.out.println();
        System.out.println("TEST =====> Pas de client dans la BD");
        System.out.println();
        try {
            Customer c = cws.findCustomer("koffi paul");
        } catch (UnknownCustomerException_Exception e) {
            exceptionList.add(e.getMessage());
        }
        // Le client n"existe pas encore donc exception capturée
        assertEquals(1, exceptionList.size());
        assertEquals("This customer koffi paul doesn't exists", exceptionList.get(0));

        /*****************************************************
         *  ENREGISTREMENT DU CLIENT ET RECHERCHE À NOUVEAU  *
         *****************************************************/

        System.out.println("TEST =====> Enregistrement d'un client du nom de Paul Koffi");
        System.out.println();

        try {
            Boolean rep = cws.registerCustomer("koffi", "paul", "3 Rue Soutrane");
            //            Enregistrement reussi
            assertTrue(rep);
            Customer c = cws.findCustomer("koffi paul");
            assertEquals("koffi paul", c.getName());
        } catch (AlreadyExistingCustomerException_Exception | UnknownCustomerException_Exception e) {
            exceptionList.add(e.getMessage());
        }
        // Aucune exception ne sera déclenché, taille de la liste toujours à 1
        assertEquals(1, exceptionList.size());

        /*****************************************************
         *   PAS ENCORE  DE PROVIDER "AMAZON" DANS LA BD     *
         *****************************************************/

        System.out.println("TEST =====> Pas de Provider dans la BD");
        System.out.println();
        try {
            stubs.provider.Provider p = pws.findProvider("Amazon");
        } catch (UnknownProviderException_Exception e) {
            exceptionList.add(e.getMessage());
        }
        // Le provider n"existe pas encore donc exception capturée , nouvelle taille de la liste 2
        assertEquals(2, exceptionList.size());
        assertEquals("This provider Amazon doesn't exists", exceptionList.get(1));

        /*****************************************************
         * ENREGISTREMENT DU PROVIDER ET RECHERCHE À NOUVEAU *
         *****************************************************/

        System.out.println("TEST =====> Enregistrement d'un provider AMAZON");
        System.out.println();

        try {
            Boolean rep = pws.register("AMAZON");
            //            Enregistrement reussi
            assertTrue(rep);
            Provider p = pws.findProvider("AMAZON");
            assertEquals("AMAZON", p.getName());
            assertNotEquals(p.getName(), "GOOGLE");
        } catch (AlreadyExistingProviderException_Exception | UnknownProviderException_Exception e) {
            exceptionList.add(e.getMessage());
        }
        // Le provider est enrégistré et sera trouvé , taille de la liste reste à 2
        assertEquals(2, exceptionList.size());

        /*****************************************************************************
         *  ESSAYER D'ENREGISTRER LE PROVIDER "AMAZON" QUI EXISTE DEJA DANS LA BD    *
         ****************************************************************************/

        System.out.println("TEST =====> L'Enregistrement d'un provider AMAZON qui existe déja dans la BD échoue");
        System.out.println();

        try {
            Boolean rep = pws.register("AMAZON");
            //            Enregistrement reussi
        } catch (AlreadyExistingProviderException_Exception e) {
            exceptionList.add(e.getMessage());
        }
        // Le provider AMAZON est déja enrégistré donc exception généré , taille de la liste maintenant à 3
        assertEquals(3, exceptionList.size());
        assertEquals("This provider : AMAZON already exits", exceptionList.get(2));

        /*****************************************************
         *  PAS ENCORE  DE COLIS "X426" DANS LA BD           *
         *****************************************************/

        System.out.println("TEST =====> Pas de colis avec le num secret 'X426' dans la BD");
        System.out.println();

        try {
            stubs.packageR.Package p = packws.findPackage("X426");
        } catch (UnknownPackageException_Exception e) {
            exceptionList.add(e.getMessage());
        }
        // Le colis n"existe pas encore donc exception capturée , nouvelle taille de la liste 4
        assertEquals(4, exceptionList.size());
        assertEquals("This package : X426 doesn't exist", exceptionList.get(3));

        /********************************************************
         *  ENREGISTREMENT DU COLIS AVEC UN PROVIDER INEXISTANT *
         *******************************************************/

        System.out.println("TEST =====> Enregistrement d'un colis avec le numéro 'X426' et le provider 'GOOGLE' inexistant échoue");
        System.out.println();

        try {
            Boolean rep = packws.registerPackage("X426", 50.0, "08/05/2020 15h00", "GOOGLE");
        } catch (stubs.packageR.UnknownProviderException | AlreadyExistingPackageException_Exception e) {
            exceptionList.add(e.getMessage());
        }
        // Le provider GOOGLE N'EXISTE PAS , exception déclenchée et taille de la liste à 5
        assertEquals(5, exceptionList.size());
        assertEquals("This provider GOOGLE doesn't exists", exceptionList.get(4));

        /********************************************************
         *  ENREGISTREMENT DU COLIS AVEC UN PROVIDER EXISTANT   *
         *******************************************************/

        System.out.println("TEST =====> Enregistrement d'un colis avec le numéro 'X426' et le provider 'AMAZON' existant ");
        System.out.println();

        try {
            Boolean rep = packws.registerPackage("X426", 50.0, "08/05/2020 15h00", "AMAZON");
            //            Enregistrement reussi
            assertTrue(rep);
            stubs.packageR.Package p = packws.findPackage("X426");
            assertEquals("AMAZON", p.getProvider().getName());
            assertEquals("X426", p.getSecretNumber());
        } catch (stubs.packageR.UnknownProviderException | AlreadyExistingPackageException_Exception | UnknownPackageException_Exception e) {
            exceptionList.add(e.getMessage());
        }
        // Le provider AMAZON existe  , le colis sera bien enrégistré et on le retrouve par la suite
        // La taille de la liste d'eceptions est toujours à 5
        assertEquals(5, exceptionList.size());

        /********************************************************
         *         ESSAYER D'ENREGISTRER UN COLIS EXISTANT      *
         *******************************************************/

        System.out.println("TEST =====> Enregistrement d'un colis avec le numéro 'X426' qui existe déja échoue");
        System.out.println();

        try {
            Boolean rep = packws.registerPackage("X426", 50.0, "08/05/2020 15h00", "AMAZON");
        } catch (stubs.packageR.UnknownProviderException | AlreadyExistingPackageException_Exception e) {
            exceptionList.add(e.getMessage());
        }
        // Un colis avec ce numéro secret a déja été enrégistré , exception levée, taille de la liste à 6 maintenant
        assertEquals(6, exceptionList.size());
        assertEquals("This package : X426 from AMAZONalready exists", exceptionList.get(5));

        /********************************************************
         *        ENREGISTREMENT D'AUTRES COLIS ET PROVIDER     *
         *******************************************************/

        System.out.println("TEST =====> Enregistrement d'un autre providers et d'autres colis");
        System.out.println();

        try {
            Boolean rep = pws.register("ADIDAS");
            assertTrue(rep);
            Provider p = pws.findProvider("ADIDAS");
            assertEquals("ADIDAS", p.getName());

            rep = packws.registerPackage("X300", 20.0, "08/05/2020 15h00", "AMAZON");
            assertTrue(rep);
            stubs.packageR.Package pa = packws.findPackage("X300");
            assertEquals("AMAZON", pa.getProvider().getName());
            assertEquals("X300", pa.getSecretNumber());

            rep = packws.registerPackage("X310", 5.0, "08/05/2020 15h00", "ADIDAS");
            assertTrue(rep);
            stubs.packageR.Package pa2 = packws.findPackage("X310");
            assertEquals("ADIDAS", pa2.getProvider().getName());
            assertEquals("X310", pa2.getSecretNumber());
        } catch (AlreadyExistingProviderException_Exception | UnknownProviderException_Exception | stubs.packageR.UnknownProviderException | AlreadyExistingPackageException_Exception | UnknownPackageException_Exception e) {
            exceptionList.add(e.getMessage());
        }
        // Aucune exception ne doit se lever , la taille reste à 6
        assertEquals(6, exceptionList.size());

        /*****************************************************************************************
         *     ESSAYER DE RÉCUPÉRER LA PROCHAINE LIVRAISON  SANS ENREGISTREMENT AU PREALABLE     *
         ****************************************************************************************/

        System.out.println("TEST =====> Essayer de récupérer la prochaine libraison alors que la liste est vide : Retourne NULL");
        System.out.println();

        try {
            stubs.delivery.Delivery d = dews.getNextDelivery();
            // Aucune livraispn erégistrée dans la base
            assertNull(d);
        } catch (ParseException_Exception e) {
            exceptionList.add(e.getMessage());
        }

        /********************************************************
         *     ENREGISTREMENT D'UNE PREMIÈRE LIVRAISON          *
         ********************************************************/

        System.out.println("TEST =====> Enregistrement d'une première livraison avec le colis 'X300' à la date " + MyDate.date_now + " et l'heure 12h00 ");
        System.out.println();

        try {
            String rep = plws.registerDelivery("koffi paul", "X300", MyDate.date_now, "12h00");
            // Enregistrement réussi
            assertEquals(rep, "Livraison Programmé");
        } catch (UnvailableSlotTimeException_Exception | PackageAlreadyTookException_Exception | stubs.planning.ParseException_Exception | UnknownCustomerException | UnknownPackageException e) {
            exceptionList.add(e.getMessage());
        }

        /********************************************************
         *  VERIFICATION DE L'ENREGISTREMENT DE LA LIVRAISON    *
         ********************************************************/

        System.out.println("TEST =====> Vérification de la livraison avec le colis 'X300' à la date " + MyDate.date_now + " et l'heure 12h00 ");
        System.out.println();

        try {
            Delivery p = dews.findDeliveryByDateAndHour(MyDate.date_now, "12h00");
            assertEquals("X300", p.getPackageDelivered().getSecretNumber());
            assertEquals(20.0, p.getPackageDelivered().getWeight(), DELTA);
            assertEquals(MyDate.date_now + " 12h00", p.getPackageDelivered().getDeliveryDate());
        } catch (Exception_Exception e) {
            exceptionList.add(e.getMessage());
        }
        // Aucune exception ne doit se lever , la taille reste à 6
        assertEquals(6, exceptionList.size());

        /*************************************************************************************
         *    ESSAI D'ENREGISTREMENT D'UNE SECONDE LIVRAISON  À UN HORAIRE INDISPONIBLE       *
         *************************************************************************************/

        System.out.println("TEST =====> Essai d'enregistrement d'une autre livraison à la date " + MyDate.date_now + " et à l'heure 12h30 indisponible échoue");
        System.out.println();

        try {
            String rep = plws.registerDelivery("koffi paul", "X426", MyDate.date_now, "12h30");
        } catch (UnvailableSlotTimeException_Exception | PackageAlreadyTookException_Exception | stubs.planning.ParseException_Exception | UnknownCustomerException | UnknownPackageException e) {
            exceptionList.add(e.getMessage());
        }
        // Dans notre système une livraison enrégistré à une heure donnée bloque aussi la tranche horaire pour les 1h30 prochaines minutes
        // Donc une exception doit être levée , nouvelle taille de la liste à 7
        assertEquals(7, exceptionList.size());
        assertEquals("Le slot de : " + MyDate.date_now + " - 12h30 est indisponible", exceptionList.get(6));


        /*****************************************************************************************
         *     ESSAYER DE RÉCUPÉRER LA PROCHAINE LIVRAISON  SANS ENREGISTREMENT DE DRONE         *
         ****************************************************************************************/

        System.out.println("TEST =====> Essayer de récupérer la prochaine libraison alors qu'il n'ya pas de drones disponibles : retourne NULL");
        System.out.println();

        try {
            stubs.delivery.Delivery d = dews.getNextDelivery();
            // Aucun drone disponible dans la base
            assertNull(d);
            exceptionList.add("Aucun Drone disponible");
        } catch (ParseException_Exception e) {
            exceptionList.add(e.getMessage());
        }
        // Ajout du message drone indisponible si aucune livraison retourné , taille de la liste maintenant à 8
        assertEquals(8, exceptionList.size());
        assertEquals("Aucun Drone disponible", exceptionList.get(7));

        /*****************************************************************************************
         *                      ENREGISTRER UN DRONE AVEC LE NUM 'GD001'                         *
         ****************************************************************************************/

        System.out.println("TEST =====> Enrégistrer un drone avec le numéro 'GD001' ");
        System.out.println();

        try {
            Boolean rep = dws.register("GD001", MyDate.date_now, "12h00");
            assertTrue(rep);
        } catch (stubs.drone.ParseException_Exception e) {
            exceptionList.add(e.getMessage());
        }

        /*****************************************************************************************
         *       RÉCUPÉRER LA PROCHAINE LIVRAISON  MAINTENANT QU'IL Y'A UN DRONE                  *
         ****************************************************************************************/

        System.out.println("TEST =====> récupérer la prochaine libraison maintenant qu'il y'a le drone");
        System.out.println();

        try {
            stubs.delivery.Delivery d = dews.getNextDelivery();
            // Drone disponible donc la livraispn va être récupérée
            System.out.println("Obtenu " + d);
            assertEquals("X300", d.getPackageDelivered().getSecretNumber());
            assertEquals(20.0, d.getPackageDelivered().getWeight(), DELTA);
            assertEquals(MyDate.date_now + " 12h00", d.getPackageDelivered().getDeliveryDate());
            // Le drone de la livraison doit être 'GD001'
            assertEquals("GD001", d.getDrone().getDroneId());
        } catch (ParseException_Exception e) {
            exceptionList.add(e.getMessage());
        }
        // La taille de la liste reste à 8 car aucune exception ne doit se lever
        assertEquals(8, exceptionList.size());

        /******************************************************************
         *         ENREGISTREMENT DE DEUX AUTRES LIVRAISONS               *
         *****************************************************************/

        System.out.println("TEST =====> Enregistrement de 2 autres livraisons à la date '08/05/2020' ");
        System.out.println();

        try {
            String rep = plws.registerDelivery("koffi paul", "X426", "08/05/2020", "15h30");
            String rep1 = plws.registerDelivery("koffi paul", "X310", "08/05/2020", "18h30");
            assertEquals(rep, "Livraison Programmé");
            assertEquals(rep1, "Livraison Programmé");
        } catch (UnvailableSlotTimeException_Exception | PackageAlreadyTookException_Exception | stubs.planning.ParseException_Exception | UnknownCustomerException | UnknownPackageException e) {
            exceptionList.add(e.getMessage());
        }

        /*****************************************************************************************
         *                            ENREGISTRER UN NOUVEAU DRONE                              *
         ****************************************************************************************/

        System.out.println("TEST =====> Enrégistrer un nouveau drone avec le numéro 'GD002' ");
        System.out.println();

        try {
            Boolean rep = dws.register("GD002", MyDate.date_now, "12h00");
//            Boolean rep1 = dws.register("GD003", MyDate.date_now, "12h00");
            assertTrue(rep);
//            assertTrue(rep1);
        } catch (stubs.drone.ParseException_Exception e) {
            exceptionList.add(e.getMessage());
        }

        /*****************************************************************************************
         *                 RÉCUPÉRER LA PROCHAINE LIVRAISON  MAINTENANT                          *
         ****************************************************************************************/

        System.out.println("TEST =====> La récupération de la prochaine livraison ne marche pas car les 2 livraisons précédentes ne sont pas enrégistrées avc la date du Jour");
        System.out.println();

        try {
            stubs.delivery.Delivery d = dews.getNextDelivery();
            assertNull(d);
        } catch (ParseException_Exception e) {
            exceptionList.add(e.getMessage());
        }
        // La taille de la liste reste à 8 car aucune exception ne doit se lever
        assertEquals(8, exceptionList.size());

        /******************************************************************
         *         REPROGRAMMER LES  DEUX AUTRES LIVRAISONS               *
         *****************************************************************/

        System.out.println("TEST =====> Reprogrammer les  2 autres livraisons à la date du jour " + MyDate.date_now);
        System.out.println();

        try {
            String rep = plws.repogrammingDelivery("08/05/2020", "15h30", MyDate.date_now, "15h30");
            String rep1 = plws.repogrammingDelivery("08/05/2020", "18h30", MyDate.date_now, "18h30");
            assertEquals(rep, "La livraison a été reprogrammé !");
            assertEquals(rep1, "La livraison a été reprogrammé !");
        } catch (UnvailableSlotTimeException_Exception | stubs.planning.ParseException_Exception e) {
            exceptionList.add(e.getMessage());
        }

        /*****************************************************************************************
         *                 VERIFIER LE NOMBRE DE LIVRAISONS DE LA JOURNÉE                        *
         ****************************************************************************************/

        System.out.println("TEST =====> On doit avoir au final  3 livraisons pour la journée");
        System.out.println();
        List<Delivery> lD = dews.getAllDeliveriesOfTheDate(MyDate.date_now);
        assertEquals(3, lD.size());
        // La taille de la liste reste à 8 car aucune exception ne doit se lever
        assertEquals(8, exceptionList.size());

        /*****************************************************************************************
         *                  RÉUPERER LA PROCHAINE LIVRAISON DE LA JOURNÉE                        *
         ****************************************************************************************/

        System.out.println("TEST =====> La récupération de la prochaine livraison de la journée nous retourne une livraison maintenant");
        System.out.println();

        try {
            stubs.delivery.Delivery d = dews.getNextDelivery();
            System.out.println("Obtenu " + d);
            assertEquals("X426", d.getPackageDelivered().getSecretNumber());
            assertEquals(50.0, d.getPackageDelivered().getWeight(), DELTA);
            assertEquals(MyDate.date_now, d.getDeliveryDate());
            // Le drone de la livraison doit être 'GD002'
            System.out.println("TEST =====> Le drone en charge de cette livraison doit être le second 'GD002' ");
            System.out.println();
            assertEquals("GD002", d.getDrone().getDroneId());
        } catch (ParseException_Exception e) {
            exceptionList.add(e.getMessage());
        }
        // La taille de la liste reste à 8 car aucune exception ne doit se lever
        assertEquals(8, exceptionList.size());

        /*****************************************************************************************
         *                            ENREGISTRER UN NOUVEAU DRONE                              *
         ****************************************************************************************/

        System.out.println("TEST =====> Enrégistrer un nouveau drone avec le numéro 'GD003' ");
        System.out.println();

        try {
            Boolean rep = dws.register("GD003", MyDate.date_now, "12h00");
            assertTrue(rep);
        } catch (stubs.drone.ParseException_Exception e) {
            exceptionList.add(e.getMessage());
        }

        /*****************************************************************************************
         *                  RÉUPERER LA PROCHAINE LIVRAISON DE LA JOURNÉE                        *
         ****************************************************************************************/

        System.out.println("TEST =====> La récupération de la prochaine livraison de la journée nous retourne la dernière qui a comme provider ADIDAS et utilise le seul drone disponible 'GD003' ");
        System.out.println();

        try {
            stubs.delivery.Delivery d = dews.getNextDelivery();
            System.out.println("Obtenu " + d);
            assertEquals("X310", d.getPackageDelivered().getSecretNumber());
            assertEquals(5, d.getPackageDelivered().getWeight(), DELTA);
            assertEquals(MyDate.date_now, d.getDeliveryDate());
            assertEquals("ADIDAS", d.getPackageDelivered().getProvider().getName());
            // Le drone de la livraison doit être 'GD002'
            System.out.println("TEST =====> Le drone en charge de cette livraison doit être le dernier 'GD003' ");
            System.out.println();
            assertEquals("GD003", d.getDrone().getDroneId());
        } catch (ParseException_Exception e) {
            exceptionList.add(e.getMessage());
        }
        // La taille de la liste reste à 8 car aucune exception ne doit se lever
        assertEquals(8, exceptionList.size());




        /******************************************************************
         *                AFFICHAGE DE TOUTES LES EXCEPTIONS              *
         *****************************************************************/

        System.out.println("TEST =====> Affichage de la liste des Exceptions ");
        System.out.println();

        for (String s : exceptionList) {
            System.out.println("m " + s);
        }

        System.out.println();
        System.out.println("TEST =====> Fin du Test");
    }

    private static void initialize() {

        String host = "jenkins-teamd.francecentral.cloudapp.azure.com";
//        ### Vérifier que serveur jenkins UP sinon utiliser localhost
        try {
            URL url = new URL("http://jenkins-teamd.francecentral.cloudapp.azure.com:8000/Web/webservices/BillingWS?wsdl");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int respCode = connection.getResponseCode();
            System.out.println("=====> Jenkins Server J2e UP");
        } catch (IOException e) {
            System.out.println("=====> Jenkins Server J2e DOWN");
            host = "localhost";
        }

        String port = "8000";
        initCWS(host, port);
        initPWS(host, port);
        initPackWS(host, port);
        initDWS(host, port);
        initPLWS(host, port);
        initDDES(host, port);
        initBBS(host, port);
        System.out.println("TEST =====> VIDER LA BD");
//                                            ### Effacer toutes les factures
        bws.deleteAll();
//                                            €## Supprimer toutes les livraisons
        dews.deleteAll();
//                                            ### Effacer tous les drones
        dws.deleteAll();
//                                            ### Effacer tous les colis
        packws.deleteAll();
//                                            ### Effacer tous les clients
        cws.deleteAll();
//                                            ### Effacer tous les transporteurs
        pws.deleteAll();
    }


    //    ### Initialisation des WebServices ###

    private static void initCWS(String host, String port) {
        System.out.println("#### Loading the WSDL contract");
        URL wsdlLocation = DDPublicAPI.class.getResource("/CustomerWebService.wsdl");
        CustomerWebServiceService factory = new CustomerWebServiceService(wsdlLocation);
        cws = factory.getCustomerWebServicePort();
        String address = "http://" + host + ":" + port + "/Web/webservices/CustomerWS";
        ((BindingProvider) cws).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
    }

    private static void initPWS(String host, String port) {
        System.out.println("#### Loading the WSDL contract");
        URL wsdlLocation = DDPublicAPI.class.getResource("/ProviderWebService.wsdl");
        ProviderWebServiceService factory = new ProviderWebServiceService(wsdlLocation);
        pws = factory.getProviderWebServicePort();
        String address = "http://" + host + ":" + port + "/Web/webservices/ProviderWS";
        ((BindingProvider) pws).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
    }

    private static void initPackWS(String host, String port) {
        System.out.println("#### Loading the WSDL contract");
        URL wsdlLocation = DDPublicAPI.class.getResource("/PackageWebService.wsdl");
        System.out.println("#### Instantiating the WS Proxy");
        PackageWebServiceService factory = new PackageWebServiceService(wsdlLocation);
        packws = factory.getPackageWebServicePort();
        String address = "http://" + host + ":" + port + "/Web/webservices/PackageWS";
        ((BindingProvider) packws).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
    }

    private static void initDWS(String host, String port) {
        System.out.println("#### Loading the WSDL contract");
        URL wsdlLocation = DDPublicAPI.class.getResource("/DroneWebService.wsdl");
        System.out.println("#### Instantiating the WS Proxy");
        DroneWebServiceService factory = new DroneWebServiceService(wsdlLocation);
        dws = factory.getDroneWebServicePort();
        String address = "http://" + host + ":" + port + "/Web/webservices/DroneWS";
        ((BindingProvider) dws).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
    }

    private static void initPLWS(String host, String port) {
        System.out.println("#### Loading the WSDL contract");
        URL wsdlLocation = DDPublicAPI.class.getResource("/PlanningWebService.wsdl");
        System.out.println("#### Instantiating the WS Proxy");
        PlanningWebServiceService factory = new PlanningWebServiceService(wsdlLocation);
        plws = factory.getPlanningWebServicePort();
        String address = "http://" + host + ":" + port + "/Web/webservices/PlanningWS";
        ((BindingProvider) plws).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
    }

    private static void initDDES(String host, String port) {
        System.out.println("#### Loading the WSDL contract");
        URL wsdlLocation = DDPublicAPI.class.getResource("/DeliveryWebService.wsdl");
        System.out.println("#### Instantiating the WS Proxy");
        DeliveryWebServiceService factory = new DeliveryWebServiceService(wsdlLocation);
        dews = factory.getDeliveryWebServicePort();
        String address = "http://" + host + ":" + port + "/Web/webservices/DeliveryWS";
        ((BindingProvider) dews).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
    }

    private static void initBBS(String host, String port) {
        System.out.println("#### Loading the WSDL contract");
        URL wsdlLocation = DDPublicAPI.class.getResource("/BillingWebService.wsdl");
        System.out.println("#### Instantiating the WS Proxy");
        BillingWebServiceService factory = new BillingWebServiceService(wsdlLocation);
        bws = factory.getBillingWebServicePort();
        String address = "http://" + host + ":" + port + "/Web/webservices/BillingWS";
        ((BindingProvider) bws).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
    }
}