package cliDemo;

import api.DDPublicAPI;
import org.junit.BeforeClass;
import org.junit.Test;
import stubs.billing.BillingWebService;
import stubs.billing.BillingWebServiceService;
import stubs.customer.*;
import stubs.customer.Customer;
import stubs.delivery.DeliveryWebService;
import stubs.delivery.DeliveryWebServiceService;
import stubs.drone.DroneWebService;
import stubs.drone.DroneWebServiceService;
import stubs.packageR.AlreadyExistingPackageException_Exception;
import stubs.packageR.PackageRegisterWebService;
import stubs.packageR.PackageWebServiceService;
import stubs.packageR.UnknownPackageException_Exception;
import stubs.planning.PlanningWebService;
import stubs.planning.PlanningWebServiceService;
import stubs.provider.*;
import stubs.provider.Provider;

import javax.xml.ws.BindingProvider;
import java.lang.Package;
import java.net.URL;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ScenarioCLI1Test {

    //    ### Declarations des WebServices ###
    private static CustomerWebService cws;
    private static ProviderWebService pws;
    private static PackageRegisterWebService packws;
    private static DroneWebService dws;
    private static PlanningWebService plws;
    private static DeliveryWebService dews;
    private static BillingWebService bws;


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

        System.out.println("TEST =====> Pas de client dans la BD");
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

        System.out.println("TEST =====> Enregistrement d'un provider AMAZON qui existe déja");
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

        System.out.println("TEST =====> Enregistrement d'un colis avec le numéro 'X426' et le provider 'GOOGLE' inexistant ");
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

        System.out.println("TEST =====> Enregistrement d'un colis avec le numéro 'X426' qui existe déja ");
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
        try {
            Boolean rep = pws.register("ADIDAS");
            assertTrue(rep);
            Provider p = pws.findProvider("ADIDAS");
            assertEquals("ADIDAS", p.getName());

            rep = packws.registerPackage("X300", 10.0, "08/05/2020 15h00", "AMAZON");
            assertTrue(rep);
            stubs.packageR.Package pa = packws.findPackage("X300");
            assertEquals("AMAZON", pa.getProvider().getName());
            assertEquals("X300", pa.getSecretNumber());

            rep = packws.registerPackage("X310", 5.0, "08/05/2020 15h00", "ADIDAS");
            assertTrue(rep);
            stubs.packageR.Package pa2 = packws.findPackage("X310");
            assertEquals("ADIDAS", pa2.getProvider().getName());
            assertEquals("X310", pa2.getSecretNumber());
        } catch (AlreadyExistingProviderException_Exception | UnknownProviderException_Exception | stubs.packageR.UnknownProviderException | AlreadyExistingPackageException_Exception | UnknownPackageException_Exception  e) {
            exceptionList.add(e.getMessage());
        }
        // Aucune exception ne doit se lever , la taille reste à 6
        assertEquals(6, exceptionList.size());



        for (String s : exceptionList) {
            System.out.println("m " + s);
        }
    }

    private static void initialize() {
//        t
        String host = "localhost";
        String port = "8080";
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
