package features;

import arquillian.AbstractDroneDeliveryTest;
import cucumber.api.CucumberOptions;
import cucumber.api.java.fr.Alors;
import cucumber.api.java.fr.Et;
import cucumber.api.java.fr.Quand;
import cucumber.runtime.arquillian.CukeSpace;
import fr.unice.polytech.isa.dd.*;
import fr.unice.polytech.isa.dd.entities.Provider;
import io.cucumber.java8.Fr;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.runner.RunWith;
import utils.MyDate;

import javax.ejb.EJB;

import static org.junit.Assert.*;

/*@RunWith(CukeSpace.class)
@CucumberOptions(features = "src/test/resources/features/NothingToDoFR.feature")
@Transactional(TransactionMode.COMMIT)
public class NothingToDoTest extends AbstractDroneDeliveryTest implements Fr {

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

    @Quand("^L'employé demande la prochaine livraison$")
    public void demandeprochainelivraison(){
    }
    @Alors("^il y a (\\d+) livraisons à livrer$")
    public void ilyanombredelivraison(Integer arg0) throws Exception {
       // assertNull(nextDeliveryInterface.getNextDelivery());
        //assertEquals(arg0.intValue(),deliverySchedule.get_deliveries().size());
    }

    @Quand("^Un employé enregistre un colis d'un fournisseur du nom de (.*)$")
    public void enregistrerlecolis(String arg0){
      //  providerRegistration.register(arg0);
       // Provider provider = providerFinder.findByName(arg0);
        //packageRegistration.register("2020",20.0,"12/04/2020",provider);
    }

    @Et("^un client du nom de (.*) (.*) avec l'adresse (.*)$")
    public void enregistrerunclient(String arg0, String arg1,String arg2) throws Exception {
        //assertEquals(1,deliverySchedule.providerList().size());
        //assertEquals("2020",packageFinder.findById("2020").getSecret_number());
        //customerRegistration.register(arg1,arg0,arg2);
    }

    @Et("^appelle pour programmer sa livraison le (.*) à (.*) du colis (.*)$")
    public void enregiterunelivraison(String arg0, String arg1,String arg2) throws Exception {
      //  boolean valid = availableSlotTime.valid_slot_time(arg0,arg1);
       // assertTrue(valid);
        ///deliveryRegistration.register_delivery("Paul Koffi",arg2,arg0,arg1);
    }
    @Alors("^il y a (\\d+) livraison à faire$")
    public void nombredelivraison(Integer arg0) throws Exception {
        //assertEquals(1,deliverySchedule.get_deliveries().size());
    }

    @Quand("^le (.*) un employé demande la prochaine livraison$")
    public void envoyeruncolis(String arg0) throws Exception {
       // MyDate.date_now = arg0;
        //nextDeliveryInterface.getNextDelivery();
    }

    @Et("^il y (\\d+) facture à payer pour le fournisseur (.*)$")
    public void enregiterunefacture(int arg0, String arg1) throws Exception {
        /*billingGeneratedInterface.generateBill();
        assertEquals(arg0,billingGeneratedInterface.get_bills().size());
        Provider provider = providerFinder.findByName(arg1);
        assertEquals(arg0,provider.getProvider_bills().size());
    }
}*/
