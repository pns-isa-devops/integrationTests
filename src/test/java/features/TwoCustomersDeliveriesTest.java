package features;

import arquillian.AbstractDroneDeliveryTest;
import cucumber.api.CucumberOptions;
import cucumber.api.java.fr.Alors;
import cucumber.api.java.fr.Et;
import cucumber.api.java.fr.Quand;
import cucumber.runtime.arquillian.CukeSpace;
import fr.unice.polytech.isa.dd.*;
import fr.unice.polytech.isa.dd.entities.Bill;
import fr.unice.polytech.isa.dd.entities.Customer;
import fr.unice.polytech.isa.dd.entities.Delivery;
import fr.unice.polytech.isa.dd.entities.Package;
import fr.unice.polytech.isa.dd.entities.Provider;
import fr.unice.polytech.isa.dd.exceptions.*;
import io.cucumber.java8.Fr;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.runner.RunWith;
import utils.MyDate;

import javax.ejb.EJB;

import java.text.ParseException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(CukeSpace.class)
@CucumberOptions(features = "src/test/resources/features/TwoCustomerDeliveriesFR.feature")
@Transactional(TransactionMode.COMMIT)
public class TwoCustomersDeliveriesTest extends AbstractDroneDeliveryTest implements Fr {

    @EJB(name = "delivery-stateless") private DeliverySchedule deliverySchedule;
    @EJB(name = "delivery-stateless") private NextDeliveryInterface nextDeliveryInterface;
    @EJB(name = "package-stateless") private PackageRegistration packageRegistration;
    @EJB(name = "provider-stateless") private ProviderRegistration providerRegistration;
    @EJB(name = "package-stateless") private PackageFinder packageFinder;
    @EJB(name = "provider-stateless") private ProviderFinder providerFinder;
    @EJB(name = "planning-stateless") private AvailableSlotTime availableSlotTime;
    @EJB(name = "planning-stateless") private DeliveryRegistration deliveryRegistration;
    @EJB(name = "customer-stateless") private CustomerRegistration customerRegistration;
    @EJB(name = "customer-stateless") private CustomerFinder customerFinder;
    @EJB(name = "bill-stateless") private BillingGeneratedInterface billingGeneratedInterface;
    @EJB(name = "drone-stateless") private DroneRegister droneRegister;
    @EJB(name = "drone-stateless") private DroneStatusInterface droneStatusInterface;
    @EJB(name = "drone-stateless") private AvailableDrone availableDrone;

    private Customer customer1 = new Customer();
    private Customer customer2 = new Customer();
    private String [] nameCustomer ;
    private String datedel ; private String hourdel; private String newDate;
    private boolean valid = false; private int colisNumber;
    private Delivery delivery; private double sum = 0;

    @Quand("^un employé enregistre 3 colis d'id (.*) (.*) (.*)$")
    public void enregistrementdrone(String arg0, String arg1, String arg2) throws ParseException {
        droneRegister.register(arg0,"13/04/2020","10h00");
        droneRegister.register(arg1,"13/04/2020","10h00");
        droneRegister.register(arg2,"13/04/2020","10h00");
    }
    @Alors("^il y a (\\d+) drones libres$")
    public void nombrededronelibres(int arg){
        assertEquals(arg,droneRegister.allDrones().size());
        assertEquals(arg,availableDrone.allDroneAvailable().size());
    }

    @Quand("^Un employé enregistre un colis de numéro (\\d+) de (\\d+)kg d'un fournisseur du nom de (.*)$")
    public void engistrepremiercolis(int arg0, double arg1, String arg3) throws AlreadyExistingProviderException, UnknownProviderException, AlreadyExistingPackageException {

        providerRegistration.registerProvider(arg3);
        Provider provider = providerFinder.findProviderByName(arg3);
        packageRegistration.register(String.valueOf(arg0),arg1,"12/04/2020",provider.getName());
    }

    @Et("^un autre colis de numéro (\\d+) de (\\d+)kg d'un fournisseur de nom (.*)$")
    public void enregistresecondcolis(int arg0, int arg1,String arg2) throws AlreadyExistingProviderException, UnknownProviderException, AlreadyExistingPackageException {
        providerRegistration.registerProvider(arg2);
        Provider provider = providerFinder.findProviderByName(arg2);
        packageRegistration.register(String.valueOf(arg0), (double) arg1,"12/04/2020",provider.getName());
    }

    @Et("^un autre cois de numéro (\\d+) de (\\d+)kg du fournisseur (.*)$")
    public void enregitertroisiemecolis(int arg0, int arg1,String arg2) throws UnknownProviderException, AlreadyExistingPackageException {
        Provider provider = providerFinder.findProviderByName(arg2);
        packageRegistration.register(String.valueOf(arg0), (double) arg1,"12/04/2020",provider.getName());
    }

    @Quand("^un employé reçoit l'appel de madame (.*) (.*) résidant à l'adresse (.*)")
    public void appelpremierclient(String arg0, String arg1, String arg2) {
        customer1.setAddress(arg2);
        customer1.setName(arg0+" "+arg1);
    }

    @Et("^il vérifie si elle existe déjà dans le système et l'enregistre si ce n'est pas le cas$")
    public void enregistreoupas() throws AlreadyExistingCustomerException {
        Customer c = null;
        try {
            c =  customerFinder.findCustomerByName(customer1.getName());
        }catch (UnknownCustomerException ignored){
        }
       assertNull(c);
       String [] names = customer1.getName().split(" ");
       customerRegistration.registerCustomer(names[0],names[1],customer1.getAddress());
    }

    @Et("^à la demande du client enregistre une livraison le (.*) à (.*) pour le colis (\\d+)$")
    public void enregiterunelivraison(String arg0, String arg1,int arg2) throws Exception {
        valid = availableSlotTime.valid_slot_time(arg0,arg1);
        assertTrue(valid);
        deliveryRegistration.register_delivery(customer1.getName(),String.valueOf(arg2),arg0,arg1);
    }

    @Alors("^il y a (\\d+) colis à livrer$")
    public void nombredelivraisonafaire(Integer arg0) {
        assertEquals(arg0.intValue(),deliverySchedule.get_deliveries().size());
    }

    @Quand("^l'employé reçoit l'appel de madame (.*)$")
    public void rec(String arg0) {
        nameCustomer = arg0.split(" ");
    }

    @Alors("^il remarque donc que cette dernière est dans le système$")
    public void existe() throws UnknownCustomerException {
        customer1 = customerFinder.findCustomerByName(nameCustomer[0] +" " +nameCustomer[1]);
        assertNotNull(customer1);
    }

    @Et("^à sa demande, il enregistre une livraison le (.*) à (.*) pour le colis (\\d+)$")
    public void engliv(String arg0, String arg1,int arg2) throws Exception {
        colisNumber = arg2;
        valid = availableSlotTime.valid_slot_time(arg0,arg1);
    }

    @Alors("^l'employé explique cela n'est pas possible$")
    public void impo1() {
        assertFalse(valid);
        assertEquals(1,customer1.getCustomer_deliveries().size());
    }

    @Et("^donc la cliente reprogramme sa livraison pour le (.*) à (.*) pour le colis (\\d+)$")
    public void engliv1(String arg0, String arg1,int arg2) throws Exception {
        valid = availableSlotTime.valid_slot_time(arg0,arg1);
        assertTrue(valid);
        deliveryRegistration.register_delivery(customer1.getName(),String.valueOf(arg2),arg0,arg1);
    }

    @Alors("^il y a maintenant (\\d+) colis à livrer$")
    public void nbliv(Integer arg0) {
        assertEquals(arg0.intValue(),deliverySchedule.get_deliveries().size());
    }

    @Quand("^l'employé est contacté par madame (.*) (.*) résidant à l'adresse (.*)$")
    public void rec(String arg0,String arg1, String arg2) {
        customer2.setAddress(arg2);
        customer2.setName(arg0+" "+arg1);
    }

    @Alors("^il constate qu'elle n'est pas dans le système et donc l'enregistre$")
    public void impo() throws AlreadyExistingCustomerException {
        Customer c = null;
        try {
            c =  customerFinder.findCustomerByName(customer2.getName());
        }catch (UnknownCustomerException ignored){
        }
        assertNull(c);
        String [] names = customer2.getName().split(" ");
        customerRegistration.registerCustomer(names[0],names[1],customer2.getAddress());
    }

    @Et("^cette dernière demande à être livrée le (.*) à (.*) pour le colis (\\d+)$")
    public void eng(String arg0, String arg1,int arg2) throws Exception {
        datedel = arg0;
        hourdel = arg1;
        colisNumber = arg2;
        valid = availableSlotTime.valid_slot_time(arg0,arg1);
    }

    @Alors("^l'employé lui dit que le colis (\\d+) n'existe pas$")
    public void notin(int arg0){
        Package aPackage = null;
        try {
            aPackage =  packageFinder.findPackageBySecretNumber(String.valueOf(arg0));
        }catch (UnknownPackageException ignored){
        }
        assertNull(aPackage);
    }

    @Alors("^elle change le numéro du colis en (\\d+)$")
    public void change(Integer arg0) throws UnknownPackageException {
        colisNumber = arg0;
        assertNotNull(packageFinder.findPackageBySecretNumber(String.valueOf(arg0)));
    }

    @Alors("^l'employé enregistre la livraison$")
    public void nbliv() throws Exception {
        valid = availableSlotTime.valid_slot_time(datedel,hourdel);
        deliveryRegistration.register_delivery(customer1.getName(),String.valueOf(colisNumber),datedel,hourdel);
    }

    @Et("^il y a (\\d+) livraisons à effectuer$")
    public void engliv1(int arg0) {
        assertEquals(arg0,deliverySchedule.get_deliveries().size());
    }

    @Quand("^le (.*) l'employé demande la prochaine livraison$")
    public void nextliv1(String arg0) throws Exception {
        MyDate.date_now = arg0;
        delivery = nextDeliveryInterface.getNextDelivery();
    }

    @Alors("^il livre le colis au nuémro (\\d+)$")
    public void liv1(int arg0) {
        assertEquals(arg0,Integer.parseInt(delivery.getPackageDelivered().getSecret_number()));
    }

    @Quand("^Madame (.*) rappelle le (.*) pour reprogrammer sa livraison$")
    public void rapp(String arg0,String arg1) throws UnknownCustomerException {
        MyDate.date_now = arg1;
        nameCustomer = arg0.split(" ");
        customer1 = customerFinder.findCustomerByName(nameCustomer[0]+" " +nameCustomer[1]);
    }

    @Alors("^elle donne la date du (.*) à (.*) de son colis (\\d+)$")
    public void newdate(String arg0,String arg1, int arg2) {
        newDate = arg0;
        hourdel = arg1;
        colisNumber = arg2;
    }

    @Et("^l'employé reprogramme sa livraison$")
    public void reprog() throws Exception {
        valid = availableSlotTime.valid_slot_time(newDate,hourdel);
        deliveryRegistration.repogramming_delivery("13/06/2020","10h00",newDate,hourdel);
    }

    @Quand("^l'employé refait une autre livraison")
    public void engliv1() throws Exception {
        MyDate.date_now = "12/06/2020";
        delivery = nextDeliveryInterface.getNextDelivery();
    }
    @Alors("^il livre celui au numéro (\\d+)$")
    public void liv(int arg0) {
        assertEquals(arg0,Integer.parseInt(delivery.getPackageDelivered().getSecret_number()));
    }

    @Quand("^un employé génère les factures de la journées du (.*)$")
    public void finjour(String arg0) throws Exception {
        MyDate.date_now = arg0;
        billingGeneratedInterface.generateBill();
    }

    @Alors("^il y a (\\d+) factures éditées$")
    public void nbfact(int arg0) {
        assertEquals(arg0,billingGeneratedInterface.get_bills().size());
    }

    @Et("^(\\d+) pour le fournisseur (.*) de (\\d+)€$")
    public void fact1(int arg0, String arg1, double arg2) throws UnknownProviderException {
        Provider provider = providerFinder.findProviderByName(arg1);
        assertEquals(arg0,provider.getProvider_bills().size());
        for (Bill b: provider.getProvider_bills()) {
            sum+=b.getBillAmount();
        }
        assertEquals(0, Double.compare(arg2, sum));
        sum = 0.0;
    }
    @Et("^(.*) en a (\\d+) de (\\d+)€$")
    public void fact2(String arg0, int arg1,double arg2) throws UnknownProviderException {
        Provider provider = providerFinder.findProviderByName(arg0);
        assertEquals(arg1,provider.getProvider_bills().size());
        for (Bill b: provider.getProvider_bills()) {
            sum+=b.getBillAmount();
        }
        assertEquals(0, Double.compare(arg2, sum));
    }


    @Quand("^le (.*) on demande la prochaine livraison$")
    public void tpspaass(String arg0) throws Exception {
        MyDate.date_now = arg0;
        delivery = nextDeliveryInterface.getNextDelivery();
    }

    @Alors("^on livre celle au colis numéro (\\d+)$")
    public void liv2(int arg0) {
        assertEquals(arg0,Integer.parseInt(delivery.getPackageDelivered().getSecret_number()));
    }

    @Et("^donc à la fin de la journée le fournisseur (.*) a (\\d+) factures à payer$")
    public void pr(String arg0, int arg1) throws Exception {
        billingGeneratedInterface.generateBill();
        Provider provider = providerFinder.findProviderByName(arg0);
        assertEquals(arg1,provider.getProvider_bills().size());
        assertEquals(1  ,billingGeneratedInterface.get_bills().size());
    }
}
